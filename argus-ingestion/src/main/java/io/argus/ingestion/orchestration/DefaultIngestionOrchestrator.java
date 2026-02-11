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
import java.util.List;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 15:31
 */
public class DefaultIngestionOrchestrator implements IngestionOrchestrator {

    private final FetchExecutor fetchExecutor;
    private final Parser parser;
    private final ChunkStrategy strategy;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    public DefaultIngestionOrchestrator(FetchExecutor fetchExecutor, Parser parser, ChunkStrategy strategy, EmbeddingModel embeddingModel, VectorStore vectorStore) {
        this.fetchExecutor = fetchExecutor;
        this.parser = parser;
        this.strategy = strategy;
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
    }

    @Override
    public IngestionResult ingest(IngestionCommand command) {

        FetchResult fetchResult = fetchExecutor.execute(command.fetchRequest());

        ContentType contentType = resolveContentType(fetchResult);

        ParseInput input =
                new ParseInput(
                        fetchResult.body(),
                        contentType,
                        fetchResult.metadata()
                );

        ParseResult parseResult = parser.parse(input);

        Document document = parseResult.document();

        List<TextChunk> chunks = strategy.chunk(document);

        List<EmbeddingVector> vectors = embeddingModel.embed(chunks);

        List<VectorRecord> records = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            records.add(
                    new VectorRecord(
                            chunks.get(i).documentId(),
                            vectors.get(i),
                            null
                    )
            );
        }

        vectorStore.upsert(records);
        return new DefaultIngestionResult(
                command.id(),
                Instant.now(),
                true,
                null
        );
    }

    private ContentType resolveContentType(FetchResult fetchResult) {

        Map<String, List<String>> metadata = fetchResult.metadata();

        if (metadata == null) {
            return ContentType.UNKNOWN;
        }

        List<String> values = metadata.get("Content-Type");

        if (values == null || values.isEmpty()) {
            return ContentType.UNKNOWN;
        }

        String raw = values.get(0);

        if (raw == null) {
            return ContentType.UNKNOWN;
        }

        raw = raw.toLowerCase();

        if (raw.contains("application/json")) {
            return ContentType.JSON;
        }

        if (raw.contains("text/html")) {
            return ContentType.HTML;
        }

        if (raw.contains("text/plain")) {
            return ContentType.TEXT;
        }

        return ContentType.UNKNOWN;
    }

}   // Class end.