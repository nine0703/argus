package io.argus.runtime;

import io.argus.core.audit.AuditEvent;
import io.argus.core.audit.AuditLevel;
import io.argus.core.audit.InMemoryAuditLog;
import io.argus.ingestion.audit.IngestionAuditEvent;
import io.argus.ingestion.audit.IngestionAuditType;
import junit.framework.TestCase;

import java.time.Instant;
import java.util.List;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 19:36
 */
public class AuditLogBackedIngestionAuditPublisherTest extends TestCase {

    public void testShouldBridgeIngestionEventIntoAuditLog() {

        InMemoryAuditLog auditLog = new InMemoryAuditLog();
        AuditLogBackedIngestionAuditPublisher publisher =
                new AuditLogBackedIngestionAuditPublisher(auditLog);

        publisher.publish(
                new IngestionAuditEvent(
                        IngestionAuditType.INGESTION_FAILED,
                        "cmd-1",
                        Instant.ofEpochMilli(123L),
                        "boom"
                )
        );

        List<AuditEvent> events = auditLog.snapshot();
        assertEquals(1, events.size());
        assertEquals(AuditLevel.ERROR, events.get(0).getLevel());
        assertEquals("INGESTION_FAILED", events.get(0).getType());
        assertEquals("cmd-1", events.get(0).getMetadata().get("commandId").orElse(null));
    }

} // Class end.