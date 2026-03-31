package io.argus.core.audit;

import io.argus.core.model.Metadata;
import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 16:20
 */
public class InMemoryAuditLogTest extends TestCase {

    public void testShouldRecordAndExposeImmutableSnapshot() {

        InMemoryAuditLog auditLog = new InMemoryAuditLog();
        auditLog.record(
                new AuditEvent(
                        "evt-1",
                        AuditLevel.INFO,
                        "FETCH_STARTED",
                        "started",
                        new Metadata(Collections.singletonMap("k", "v")),
                        1L
                )
        );

        List<AuditEvent> snapshot = auditLog.snapshot();

        assertEquals(1, snapshot.size());
        assertEquals("evt-1", snapshot.get(0).getId());

        try {
            snapshot.add(null);
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            assertNotNull(expected);
        }
    }

} // Class end.