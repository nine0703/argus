package io.argus.core.memory;

import io.argus.core.model.Metadata;
import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 16:20
 */
public class InMemoryMemoryTest extends TestCase {

    public void testShouldRecallEntriesByScope() {

        InMemoryMemory memory = new InMemoryMemory();
        memory.store(
                new MemoryEntry(
                        "m-1",
                        MemoryScope.WORKING,
                        "value-1",
                        new Metadata(Collections.emptyMap()),
                        1L
                )
        );
        memory.store(
                new MemoryEntry(
                        "m-2",
                        MemoryScope.LONG_TERM,
                        "value-2",
                        new Metadata(Collections.emptyMap()),
                        2L
                )
        );

        List<MemoryEntry> workingEntries = memory.recall(MemoryScope.WORKING);

        assertEquals(1, workingEntries.size());
        assertEquals("m-1", workingEntries.get(0).getId());
        assertEquals(2, memory.snapshot().size());
    }

} // Class end.
