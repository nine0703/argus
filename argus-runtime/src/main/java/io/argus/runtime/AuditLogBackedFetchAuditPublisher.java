package io.argus.runtime;

import io.argus.core.audit.AuditEvent;
import io.argus.core.audit.AuditLevel;
import io.argus.core.audit.AuditLog;
import io.argus.core.model.Metadata;
import io.argus.ingestion.audit.fetch.FetchAuditEvent;
import io.argus.ingestion.audit.fetch.FetchAuditPublisher;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Bridges fetch lifecycle audit events into the core {@link AuditLog}.
 *
 * <p>
 * This publisher exists in the runtime layer because it connects
 * ingestion-specific fetch events with the generic audit infrastructure
 * defined by {@code argus-core}.
 *
 * <p>
 * 它负责把抓取链路中的审计事件转换成核心审计事件，
 * 从而让 runtime 可以使用统一的审计存储与分析模型。
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:06
 */
public class AuditLogBackedFetchAuditPublisher implements FetchAuditPublisher {

    private final AuditLog auditLog;

    public AuditLogBackedFetchAuditPublisher(AuditLog auditLog) {
        this.auditLog = Objects.requireNonNull(auditLog, "auditLog");
    }

    @Override
    public void publish(FetchAuditEvent event) {
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

    private AuditLevel resolveLevel(FetchAuditEvent event) {
        return event.type().name().endsWith("FAILED") ? AuditLevel.ERROR : AuditLevel.INFO;
    }

    private String buildMessage(FetchAuditEvent event) {

        if (event.message() == null || event.message().isEmpty()) {
            return event.type().name() + " " + event.resource();
        }

        return event.type().name() + " " + event.resource() + " " + event.message();
    }

    private Metadata buildMetadata(FetchAuditEvent event) {

        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("protocol", event.protocol().name());
        attributes.put("resource", event.resource());
        if (event.message() != null && !event.message().isEmpty()) {
            attributes.put("detail", event.message());
        }

        return new Metadata(attributes);
    }

} // Class end.
