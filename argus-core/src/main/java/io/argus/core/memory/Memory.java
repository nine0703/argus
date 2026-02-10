package io.argus.core.memory;

import java.util.List;

/**
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:04
 */
public interface Memory {

    void store(MemoryEntry entry);

    List<MemoryEntry> recall(MemoryScope scope);

} // Class end.