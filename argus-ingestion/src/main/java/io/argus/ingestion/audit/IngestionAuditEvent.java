package io.argus.ingestion.audit;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable audit event representing an ingestion lifecycle fact.
 *
 * <p>
 * The event stays intentionally compact. It captures the audit type,
 * the ingestion command identifier, the observed timestamp, and
 * an optional descriptive message.
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 15:00
 */
public final class IngestionAuditEvent {

    private final IngestionAuditType type;
    private final String commandId;
    private final Instant timestamp;
    private final String message;

    public IngestionAuditEvent(
            IngestionAuditType type,
            String commandId,
            Instant timestamp,
            String message
    ) {
        this.type = Objects.requireNonNull(type, "type");
        this.commandId = Objects.requireNonNull(commandId, "commandId");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.message = message;
    }

    public IngestionAuditType type() {
        return type;
    }

    public String commandId() {
        return commandId;
    }

    public Instant timestamp() {
        return timestamp;
    }

    public String message() {
        return message;
    }

} // Class end.