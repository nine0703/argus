package io.argus.runtime;

import io.argus.core.audit.AuditLog;
import io.argus.core.memory.Memory;
import io.argus.ingestion.audit.fetch.FetchAuditPublisher;
import io.argus.ingestion.fetch.FetchExecutorRegistry;

import java.util.Objects;

/**
 * Immutable container of the core runtime infrastructure used by ARGUS.
 *
 * <p>
 * {@code ArgusRuntime} groups the minimum set of runtime services required
 * to execute agent-oriented ingestion workflows:
 * <ul>
 *   <li>{@link Memory} for recall and local fact storage</li>
 *   <li>{@link AuditLog} for authoritative audit artifacts</li>
 *   <li>{@link FetchAuditPublisher} for fetch lifecycle publication</li>
 *   <li>{@link FetchExecutorRegistry} for protocol-based fetch dispatch</li>
 * </ul>
 *
 * <p>
 * This type is intentionally framework-agnostic.
 * Spring Boot integration is provided by dedicated starter modules.
 *
 * <p>
 * {@code ArgusRuntime} 是 ARGUS 运行时基础设施的最小聚合单元，
 * 负责承载内存、审计、抓取审计发布与抓取执行注册表。
 * 它本身不依赖 Spring，由外层自动装配模块负责接入框架。
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:05
 */
public final class ArgusRuntime {

    private final Memory memory;
    private final AuditLog auditLog;
    private final FetchAuditPublisher fetchAuditPublisher;
    private final FetchExecutorRegistry fetchExecutorRegistry;

    public ArgusRuntime(
            Memory memory,
            AuditLog auditLog,
            FetchAuditPublisher fetchAuditPublisher,
            FetchExecutorRegistry fetchExecutorRegistry
    ) {
        this.memory = Objects.requireNonNull(memory, "memory");
        this.auditLog = Objects.requireNonNull(auditLog, "auditLog");
        this.fetchAuditPublisher =
                Objects.requireNonNull(fetchAuditPublisher, "fetchAuditPublisher");
        this.fetchExecutorRegistry =
                Objects.requireNonNull(fetchExecutorRegistry, "fetchExecutorRegistry");
    }

    public Memory memory() {
        return memory;
    }

    public AuditLog auditLog() {
        return auditLog;
    }

    public FetchAuditPublisher fetchAuditPublisher() {
        return fetchAuditPublisher;
    }

    public FetchExecutorRegistry fetchExecutorRegistry() {
        return fetchExecutorRegistry;
    }

} // Class end.
