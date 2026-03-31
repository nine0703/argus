package io.argus.ingestion.orchestration;

import io.argus.ingestion.domain.chunk.FixedSizeChunkStrategy;
import io.argus.ingestion.domain.embedding.HashEmbeddingModel;
import io.argus.ingestion.domain.vector.InMemoryVectorStore;
import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.fetch.FetchResult;
import io.argus.ingestion.parse.Parser;
import io.argus.ingestion.parse.SimpleDocumentParser;
import io.argus.ingestion.source.IngestionResult;
import junit.framework.TestCase;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 19:39
 */
public class DefaultIngestionOrchestratorTest extends TestCase {

    public void testShouldRunDefaultClosedLoop() {

        InMemoryVectorStore vectorStore = new InMemoryVectorStore("docs");
        DefaultIngestionOrchestrator orchestrator = new DefaultIngestionOrchestrator(
                new StubFetchExecutor("abcdefghij", Map.of("Content-Type", List.of("text/plain; charset=UTF-8"))),
                parser(),
                new FixedSizeChunkStrategy(4),
                new HashEmbeddingModel(4),
                vectorStore
        );

        IngestionResult result = orchestrator.ingest(
                new IngestionCommand(
                        "cmd-1",
                        new StubFetchRequest(),
                        new IngestionOptions(true, true, true, "docs")
                )
        );

        assertTrue(result.success());
        assertEquals("stub", result.metadata().get("fetchProtocol"));
        assertEquals("TEXT", result.metadata().get("contentType"));
        assertEquals(10, result.metadata().get("fetchedBytes"));
        assertEquals(10, result.metadata().get("documentLength"));
        assertEquals(3, result.metadata().get("chunkCount"));
        assertEquals(3, result.metadata().get("vectorCount"));
        assertEquals(3, vectorStore.snapshot().size());
        assertEquals("docs", result.metadata().get("namespace"));
    }

    public void testShouldSkipEmbeddingAndStorageWhenDisabled() {

        InMemoryVectorStore vectorStore = new InMemoryVectorStore("docs");
        DefaultIngestionOrchestrator orchestrator = new DefaultIngestionOrchestrator(
                new StubFetchExecutor("abcdef", Map.of("Content-Type", List.of("text/plain"))),
                parser(),
                new FixedSizeChunkStrategy(3),
                new HashEmbeddingModel(4),
                vectorStore
        );

        IngestionResult result = orchestrator.ingest(
                new IngestionCommand(
                        "cmd-2",
                        new StubFetchRequest(),
                        new IngestionOptions(false, false, false, "docs")
                )
        );

        assertTrue(result.success());
        assertEquals("TEXT", result.metadata().get("contentType"));
        assertEquals(1, result.metadata().get("chunkCount"));
        assertEquals(0, result.metadata().get("vectorCount"));
        assertEquals(0, vectorStore.snapshot().size());
    }

    public void testShouldRejectVectorStoreWithoutEmbedding() {

        DefaultIngestionOrchestrator orchestrator = new DefaultIngestionOrchestrator(
                new StubFetchExecutor("abcdef", Map.of("Content-Type", List.of("text/plain"))),
                parser(),
                new FixedSizeChunkStrategy(3),
                new HashEmbeddingModel(4),
                new InMemoryVectorStore("docs")
        );

        try {
            orchestrator.ingest(
                    new IngestionCommand(
                            "cmd-3",
                            new StubFetchRequest(),
                            new IngestionOptions(true, false, true, "docs")
                    )
            );
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("vector store requires embedding to be enabled", expected.getMessage());
        }
    }

    private Parser parser() {
        return new SimpleDocumentParser();
    }

    private static final class StubFetchExecutor implements FetchExecutor {

        private final String body;
        private final Map<String, List<String>> metadata;

        private StubFetchExecutor(String body, Map<String, List<String>> metadata) {
            this.body = body;
            this.metadata = metadata;
        }

        @Override
        public FetchProtocol protocol() {
            return () -> "stub";
        }

        @Override
        public FetchResult execute(FetchRequest request) {
            return new FetchResult() {
                @Override
                public FetchProtocol protocol() {
                    return () -> "stub";
                }

                @Override
                public byte[] body() {
                    return body.getBytes(StandardCharsets.UTF_8);
                }

                @Override
                public Map<String, List<String>> metadata() {
                    return metadata;
                }

                @Override
                public boolean success() {
                    return true;
                }
            };
        }
    }

    private static final class StubFetchRequest implements FetchRequest {

        @Override
        public URI resource() {
            return URI.create("http://example.com/resource");
        }

        @Override
        public FetchProtocol protocol() {
            return () -> "http";
        }

        @Override
        public Map<String, List<String>> metadata() {
            return Collections.emptyMap();
        }
    }

} // Class end.