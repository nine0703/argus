package io.argus.ingestion.audit;

/**
 * Publishes ingestion lifecycle audit events.
 *
 * <p>
 * This abstraction exists at ingestion-orchestration level and allows
 * different runtime adapters to route ingestion audit facts into logs,
 * stores, metrics, or higher-level audit bridges.
 *
 * <p>
 * 该接口定义 ingestion 生命周期审计事件的发布边界，
 * 便于后续接入 runtime 审计桥接、持久化或指标系统。
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:25
 */
public interface IngestionAuditPublisher {

    void publish(IngestionAuditEvent event);

} // Class end.
