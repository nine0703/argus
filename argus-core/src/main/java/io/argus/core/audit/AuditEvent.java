package io.argus.core.audit;

import io.argus.core.model.Identifier;
import io.argus.core.model.Metadata;
import io.argus.core.model.Timestamped;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable audit fact recorded by the runtime.
 *
 * <p>
 * An audit event is a first-class value object and therefore carries stable
 * identity, level, message, metadata, and timestamp semantics.
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:04
 */
public final class AuditEvent implements Identifier, Timestamped {

    private final String id;
    private final AuditLevel level;
    private final String type;
    private final String message;
    private final Metadata metadata;
    private final long timestamp;

    public AuditEvent(
            String id,
            AuditLevel level,
            String type,
            String message,
            Metadata metadata,
            long timestamp
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.level = Objects.requireNonNull(level, "level");
        this.type = Objects.requireNonNull(type, "type");
        this.message = Objects.requireNonNull(message, "message");
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

    public AuditLevel getLevel() {
        return level;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
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
        if (!(o instanceof AuditEvent)) {
            return false;
        }
        AuditEvent that = (AuditEvent) o;
        return timestamp == that.timestamp
                && id.equals(that.id)
                && level == that.level
                && type.equals(that.type)
                && message.equals(that.message)
                && metadata.asMap().equals(that.metadata.asMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, level, type, message, metadata.asMap(), timestamp);
    }

    @Override
    public String toString() {
        return "AuditEvent{" +
                "id='" + id + '\'' +
                ", level=" + level +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", metadata=" + metadata.asMap() +
                ", timestamp=" + timestamp +
                '}';
    }

} // Class end.