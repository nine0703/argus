package io.argus.core.memory;

import io.argus.core.model.Identifier;
import io.argus.core.model.Metadata;
import io.argus.core.model.Timestamped;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable memory record stored by the runtime.
 *
 * <p>
 * A memory entry is a durable fact written into a specific memory scope.
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:04
 */
public final class MemoryEntry implements Identifier, Timestamped {

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
        this.id = Objects.requireNonNull(id, "id");
        this.scope = Objects.requireNonNull(scope, "scope");
        this.value = value;
        this.metadata = Objects.requireNonNull(metadata, "metadata");
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    @Override
    public String identifier() {
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

    @Override
    public Instant timestamp() {
        return Instant.ofEpochMilli(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MemoryEntry)) {
            return false;
        }
        MemoryEntry that = (MemoryEntry) o;
        return timestamp == that.timestamp
                && id.equals(that.id)
                && scope == that.scope
                && Objects.equals(value, that.value)
                && metadata.asMap().equals(that.metadata.asMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, scope, value, metadata.asMap(), timestamp);
    }

    @Override
    public String toString() {
        return "MemoryEntry{" +
                "id='" + id + '\'' +
                ", scope=" + scope +
                ", value=" + value +
                ", metadata=" + metadata.asMap() +
                ", timestamp=" + timestamp +
                '}';
    }

} // Class end.