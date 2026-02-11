package io.argus.ingestion.audit.fetch;

import io.argus.ingestion.fetch.FetchProtocol;

import java.time.Instant;

/**
 * Immutable audit event for fetch lifecycle.
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:56
 */
public final class FetchAuditEvent {

    private final FetchAuditType type;
    private final FetchProtocol protocol;
    private final String resource;
    private final Instant timestamp;
    private final String message;

    public FetchAuditEvent(
            FetchAuditType type,
            FetchProtocol protocol,
            String resource,
            Instant timestamp,
            String message
    ) {
        this.type = type;
        this.protocol = protocol;
        this.resource = resource;
        this.timestamp = timestamp;
        this.message = message;
    }

    public FetchAuditType type() {
        return type;
    }

    public FetchProtocol protocol() {
        return protocol;
    }

    public String resource() {
        return resource;
    }

    public Instant timestamp() {
        return timestamp;
    }

    public String message() {
        return message;
    }

}   // Class end.