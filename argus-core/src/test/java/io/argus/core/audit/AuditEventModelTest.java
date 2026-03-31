package io.argus.core.audit;

import io.argus.core.model.Metadata;
import junit.framework.TestCase;

import java.time.Instant;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:49
 */
public class AuditEventModelTest extends TestCase {

    public void testShouldExposeIdentifierAndTimestampMixins() {

        AuditEvent event = new AuditEvent(
                "evt-1",
                AuditLevel.INFO,
                "TEST",
                "message",
                new Metadata(Map.of("k", "v")),
                123L
        );

        assertEquals("evt-1", event.identifier());
        assertEquals(Instant.ofEpochMilli(123L), event.timestamp());
    }

} // Class end.
