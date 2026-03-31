package io.argus.runtime;

import io.argus.core.audit.AuditEvent;
import io.argus.core.audit.AuditLevel;
import io.argus.core.audit.AuditLog;
import io.argus.core.model.Metadata;
import io.argus.ingestion.audit.IngestionAuditEvent;
import io.argus.ingestion.audit.IngestionAuditPublisher;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Bridges ingestion lifecycle events into the core {@link AuditLog}.
 *
 * <p>
 * This adapter lives in the runtime layer because it binds the ingestion
 * domain's audit vocabulary to the generic runtime audit infrastructure.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 19:35
 */
public class AuditLogBackedIngestionAuditPublisher implements IngestionAuditPublisher {

    private final AuditLog auditLog;

    public AuditLogBackedIngestionAuditPublisher(AuditLog auditLog) {
        this.auditLog = Objects.requireNonNull(auditLog, "auditLog");
    }

    @Override
    public void publish(IngestionAuditEvent event) {
        auditLog.record(
                new AuditEvent(
                        UUID.randomUUID().toString(),
                        resolveLevel(event),
                        event.type().name(),
                        buildMessage(event),
                        buildMetadata(event),
                        event.timestamp().toEpochMilli()
                )
        );
    }

    private AuditLevel resolveLevel(IngestionAuditEvent event) {
        return event.type().name().endsWith("FAILED") ? AuditLevel.ERROR : AuditLevel.INFO;
    }

    private String buildMessage(IngestionAuditEvent event) {

        if (event.message() == null || event.message().isEmpty()) {
            return event.type().name() + " " + event.commandId();
        }

        return event.type().name() + " " + event.commandId() + " " + event.message();
    }

    private Metadata buildMetadata(IngestionAuditEvent event) {

        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("commandId", event.commandId());
        if (event.message() != null && !event.message().isEmpty()) {
            attributes.put("detail", event.message());
        }

        return new Metadata(attributes);
    }

} // Class end.