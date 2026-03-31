package io.argus.ingestion.audit;

/**
 * Types of audit events emitted around ingestion orchestration.
 *
 * <p>
 * These event types describe authoritative lifecycle facts for a single
 * ingestion command at orchestration level.
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 15:00
 */
public enum IngestionAuditType {

    INGESTION_STARTED,

    INGESTION_SUCCEEDED,

    INGESTION_FAILED

} // Class end.