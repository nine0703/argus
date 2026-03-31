package io.argus.core.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Append-only in-memory Memory implementation.
 * <p>
 * Intended as the default local memory store for the runtime container.
 * @author TK.ENDO
 * @since 2026-03-31 周二 16:18
 */
public class InMemoryMemory implements Memory {

    private final CopyOnWriteArrayList<MemoryEntry> entries = new CopyOnWriteArrayList<>();

    @Override
    public void store(MemoryEntry entry) {
        entries.add(Objects.requireNonNull(entry, "entry"));
    }

    @Override
    public List<MemoryEntry> recall(MemoryScope scope) {

        List<MemoryEntry> matched = new ArrayList<>();

        for (MemoryEntry entry : entries) {
            if (entry.getScope() == scope) {
                matched.add(entry);
            }
        }

        return Collections.unmodifiableList(matched);
    }

    public List<MemoryEntry> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(entries));
    }

} // Class end.
