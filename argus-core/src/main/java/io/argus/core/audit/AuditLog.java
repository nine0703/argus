package io.argus.core.audit;

/**
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:04
 */
public interface AuditLog {

    void record(AuditEvent event);

} // Class end.