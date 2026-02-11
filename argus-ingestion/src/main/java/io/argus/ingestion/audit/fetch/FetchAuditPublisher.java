package io.argus.ingestion.audit.fetch;

/**
 * Publishes fetch audit events.
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:56
 */
public interface FetchAuditPublisher {

    void publish(FetchAuditEvent event);

}   // Class end.