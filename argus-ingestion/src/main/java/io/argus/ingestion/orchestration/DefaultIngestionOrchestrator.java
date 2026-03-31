package io.argus.ingestion.orchestration;

import io.argus.ingestion.domain.chunk.ChunkStrategy;
import io.argus.ingestion.domain.chunk.TextChunk;
import io.argus.ingestion.domain.document.Document;
import io.argus.ingestion.domain.embedding.EmbeddingModel;
import io.argus.ingestion.domain.embedding.EmbeddingVector;
import io.argus.ingestion.domain.vector.VectorRecord;
import io.argus.ingestion.domain.vector.VectorStore;
import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchResult;
import io.argus.ingestion.parse.ContentType;
import io.argus.ingestion.parse.ParseInput;
import io.argus.ingestion.parse.ParseResult;
import io.argus.ingestion.parse.Parser;
import io.argus.ingestion.source.DefaultIngestionResult;
import io.argus.ingestion.source.IngestionResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Default orchestrator for the local ingestion pipeline.
 *
 * <p>
 * The orchestrator executes the canonical sequence:
 * fetch -> parse -> chunk -> embed -> store.
 * Individual pipeline stages remain pluggable through constructor-injected
 * strategy interfaces.
 *
 * @author TK.ENDO
 * @since 2026-02-11 周三 15:31
 */
public class DefaultIngestionOrchestrator implements IngestionOrchestrator {

    private final FetchExecutor fetchExecutor;
    private final Parser parser;
    private final ChunkStrategy strategy;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    public DefaultIngestionOrchestrator(
            FetchExecutor fetchExecutor,
            Parser parser,
            ChunkStrategy strategy,
            EmbeddingModel embeddingModel,
            VectorStore vectorStore
    ) {
        this.fetchExecutor = Objects.requireNonNull(fetchExecutor, "fetchExecutor");
        this.parser = Objects.requireNonNull(parser, "parser");
        this.strategy = Objects.requireNonNull(strategy, "strategy");
        this.embeddingModel = Objects.requireNonNull(embeddingModel, "embeddingModel");
        this.vectorStore = Objects.requireNonNull(vectorStore, "vectorStore");
    }

    @Override
    public IngestionResult ingest(IngestionCommand command) {

        Objects.requireNonNull(command, "command");
        IngestionOptions options = command.options() == null
                ? IngestionOptions.defaultOptions()
                : command.options();

        if (options.enableVectorStore() && !options.enableEmbedding()) {
            throw new IllegalArgumentException("vector store requires embedding to be enabled");
        }

        FetchResult fetchResult = fetchExecutor.execute(command.fetchRequest());
        ContentType contentType = resolveContentType(fetchResult);

        ParseInput input = new ParseInput(
                fetchResult.body(),
                contentType,
                fetchResult.metadata()
        );
        ParseResult parseResult = parser.parse(input);
        Document document = parseResult.document();

        List<TextChunk> chunks = resolveChunks(document, options);
        List<EmbeddingVector> vectors = options.enableEmbedding()
                ? embeddingModel.embed(chunks)
                : List.of();

        if (!vectors.isEmpty() && vectors.size() != chunks.size()) {
            throw new IllegalStateException("embedding result size must match chunk size");
        }

        List<VectorRecord> records = buildVectorRecords(chunks, vectors, options);
        if (options.enableVectorStore() && !records.isEmpty()) {
            vectorStore.upsert(records);
        }

        return new DefaultIngestionResult(
                command.id(),
                Instant.now(),
                true,
                buildResultMetadata(fetchResult, contentType, document, chunks, vectors, records, options)
        );
    }

    private List<TextChunk> resolveChunks(Document document, IngestionOptions options) {

        if (options.enableChunking()) {
            return strategy.chunk(document);
        }

        return List.of(
                new TextChunk(
                        document.id(),
                        0,
                        document.content() == null ? "" : document.content(),
                        document.metadata()
                )
        );
    }

    private List<VectorRecord> buildVectorRecords(
            List<TextChunk> chunks,
            List<EmbeddingVector> vectors,
            IngestionOptions options
    ) {

        List<VectorRecord> records = new ArrayList<>();
        for (int i = 0; i < vectors.size(); i++) {
            Map<String, Object> metadata = new LinkedHashMap<>(chunks.get(i).metadata());
            metadata.put("namespace", options.namespace());
            metadata.put("chunkId", vectors.get(i).chunkId());
            metadata.put("embeddingModel", embeddingModel.modelName());
            records.add(
                    new VectorRecord(
                            chunks.get(i).documentId() + "#" + chunks.get(i).index(),
                            vectors.get(i),
                            metadata
                    )
            );
        }
        return records;
    }

    private Map<String, Object> buildResultMetadata(
            FetchResult fetchResult,
            ContentType contentType,
            Document document,
            List<TextChunk> chunks,
            List<EmbeddingVector> vectors,
            List<VectorRecord> records,
            IngestionOptions options
    ) {

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("documentId", document.id());
        metadata.put("parser", parser.name());
        metadata.put("fetchProtocol", fetchResult.protocol().name());
        metadata.put("fetchSuccess", fetchResult.success());
        metadata.put("contentType", contentType.name());
        metadata.put("fetchedBytes", fetchResult.body() == null ? 0 : fetchResult.body().length);
        metadata.put("documentLength", document.content() == null ? 0 : document.content().length());
        metadata.put("chunkCount", chunks.size());
        metadata.put("vectorCount", vectors.size());
        metadata.put("storedVectorCount", options.enableVectorStore() ? records.size() : 0);
        metadata.put("chunkingEnabled", options.enableChunking());
        metadata.put("embeddingEnabled", options.enableEmbedding());
        metadata.put("vectorStoreEnabled", options.enableVectorStore());
        metadata.put("namespace", options.namespace());
        if (options.enableChunking()) {
            metadata.put("chunkStrategy", strategy.name());
        }
        if (options.enableEmbedding()) {
            metadata.put("embeddingModel", embeddingModel.modelName());
        }
        if (options.enableVectorStore()) {
            metadata.put("vectorStore", vectorStore.getClass().getSimpleName());
        }
        return metadata;
    }

    private ContentType resolveContentType(FetchResult fetchResult) {

        Map<String, List<String>> metadata = fetchResult.metadata();
        if (metadata == null || metadata.isEmpty()) {
            return ContentType.UNKNOWN;
        }

        String raw = firstHeaderValue(metadata, "content-type");
        if (raw == null) {
            return ContentType.UNKNOWN;
        }

        String normalized = raw.toLowerCase(Locale.ROOT);
        if (normalized.contains("application/json")) {
            return ContentType.JSON;
        }
        if (normalized.contains("text/html")) {
            return ContentType.HTML;
        }
        if (normalized.contains("text/plain")) {
            return ContentType.TEXT;
        }
        return ContentType.UNKNOWN;
    }

    private String firstHeaderValue(Map<String, List<String>> metadata, String headerName) {

        for (Map.Entry<String, List<String>> entry : metadata.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(headerName)) {
                continue;
            }

            List<String> values = entry.getValue();
            if (values == null || values.isEmpty()) {
                return null;
            }

            String value = values.get(0);
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return value;
        }

        return null;
    }

} // Class end.