package io.argus.core.memory;

import io.argus.core.model.Metadata;
import junit.framework.TestCase;

import java.time.Instant;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:49
 */
public class MemoryEntryModelTest extends TestCase {

    public void testShouldExposeIdentifierAndTimestampMixins() {

        MemoryEntry entry = new MemoryEntry(
                "mem-1",
                MemoryScope.WORKING,
                "value",
                new Metadata(Map.of("k", "v")),
                456L
        );

        assertEquals("mem-1", entry.identifier());
        assertEquals(Instant.ofEpochMilli(456L), entry.timestamp());
    }

} // Class end.
