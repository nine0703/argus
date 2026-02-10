package io.argus.core.memory;

import io.argus.core.model.Metadata;

/**
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:04
 */
public final class MemoryEntry {

    private final String id;
    private final MemoryScope scope;
    private final Object value;
    private final Metadata metadata;
    private final long timestamp;

    public MemoryEntry(
            String id,
            MemoryScope scope,
            Object value,
            Metadata metadata,
            long timestamp
    ) {
        this.id = id;
        this.scope = scope;
        this.value = value;
        this.metadata = metadata;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public MemoryScope getScope() {
        return scope;
    }

    public Object getValue() {
        return value;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // equals / hashCode / toString 建议手写或 Lombok @EqualsAndHashCode

} // Class end.