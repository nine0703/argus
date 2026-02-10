package io.argus.core.audit;

import io.argus.core.model.Metadata;

/**
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:04
 */
public final class AuditEvent {

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
        this.id = id;
        this.level = level;
        this.type = type;
        this.message = message;
        this.metadata = metadata;
        this.timestamp = timestamp;
    }

    // getters only
    public String getId() {
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
    // equals / hashCode / toString 建议手写或 Lombok @EqualsAndHashCode

} // Class end.