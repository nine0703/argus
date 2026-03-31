package io.argus.ingestion.audit;

import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.orchestration.IngestionCommand;
import io.argus.ingestion.orchestration.IngestionOptions;
import io.argus.ingestion.orchestration.IngestionOrchestrator;
import io.argus.ingestion.source.DefaultIngestionResult;
import io.argus.ingestion.source.IngestionResult;
import junit.framework.TestCase;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:26
 */
public class AuditingIngestionOrchestratorTest extends TestCase {

    public void testShouldPublishLifecycleEvents() {

        List<IngestionAuditEvent> events = new ArrayList<>();
        IngestionAuditPublisher publisher = events::add;
        IngestionOrchestrator delegate =
                command -> new DefaultIngestionResult(
                        command.id(),
                        Instant.now(),
                        true,
                        Collections.emptyMap()
                );

        AuditingIngestionOrchestrator orchestrator =
                new AuditingIngestionOrchestrator(delegate, publisher);

        IngestionResult result = orchestrator.ingest(
                new IngestionCommand("cmd-1", new StubFetchRequest(), IngestionOptions.defaultOptions())
        );

        assertNotNull(result);
        assertEquals(2, events.size());
        assertEquals(IngestionAuditType.INGESTION_STARTED, events.get(0).type());
        assertEquals(IngestionAuditType.INGESTION_SUCCEEDED, events.get(1).type());
    }

    public void testShouldPublishFailureEvent() {

        List<IngestionAuditEvent> events = new ArrayList<>();
        IngestionAuditPublisher publisher = events::add;
        IngestionOrchestrator delegate = command -> {
            throw new IllegalStateException("boom");
        };

        AuditingIngestionOrchestrator orchestrator =
                new AuditingIngestionOrchestrator(delegate, publisher);

        try {
            orchestrator.ingest(
                    new IngestionCommand("cmd-2", new StubFetchRequest(), IngestionOptions.defaultOptions())
            );
            fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            assertEquals(2, events.size());
            assertEquals(IngestionAuditType.INGESTION_FAILED, events.get(1).type());
        }
    }

    private static final class StubFetchRequest implements FetchRequest {

        @Override
        public URI resource() {
            return URI.create("http://example.com");
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
