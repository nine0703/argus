package io.argus.ingestion.audit.fetch;

/**
 * Types of audit events during fetch execution.
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:56
 */
public enum FetchAuditType {

    FETCH_STARTED,

    FETCH_REPLAYED,

    FETCH_SUCCEEDED,

    FETCH_FAILED,

    FETCH_RECORDED

}
