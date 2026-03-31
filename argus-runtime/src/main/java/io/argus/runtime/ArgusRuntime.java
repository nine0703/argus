package io.argus.runtime;

import io.argus.agent.AgentRunner;
import io.argus.core.audit.AuditLog;
import io.argus.core.memory.Memory;
import io.argus.ingestion.audit.fetch.FetchAuditPublisher;
import io.argus.ingestion.fetch.FetchExecutorRegistry;

import java.util.Objects;

/**
 * Immutable container of the core runtime infrastructure used by ARGUS.
 *
 * <p>
 * {@code ArgusRuntime} groups the minimum set of runtime services required to
 * execute agent-oriented ingestion workflows:
 * <ul>
 *   <li>{@link Memory} for recall and local fact storage</li>
 *   <li>{@link AuditLog} for authoritative audit artifacts</li>
 *   <li>{@link FetchAuditPublisher} for fetch lifecycle publication</li>
 *   <li>{@link FetchExecutorRegistry} for protocol-based fetch dispatch</li>
 *   <li>{@link AgentRunner} for loop-driven agent execution</li>
 * </ul>
 *
 * <p>
 * This type is intentionally framework-agnostic.
 * Spring Boot integration is provided by dedicated starter modules.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:05
 */
public final class ArgusRuntime {

    private final Memory memory;
    private final AuditLog auditLog;
    private final FetchAuditPublisher fetchAuditPublisher;
    private final FetchExecutorRegistry fetchExecutorRegistry;
    private final AgentRunner agentRunner;

    public ArgusRuntime(
            Memory memory,
            AuditLog auditLog,
            FetchAuditPublisher fetchAuditPublisher,
            FetchExecutorRegistry fetchExecutorRegistry,
            AgentRunner agentRunner
    ) {
        this.memory = Objects.requireNonNull(memory, "memory");
        this.auditLog = Objects.requireNonNull(auditLog, "auditLog");
        this.fetchAuditPublisher = Objects.requireNonNull(fetchAuditPublisher, "fetchAuditPublisher");
        this.fetchExecutorRegistry = Objects.requireNonNull(fetchExecutorRegistry, "fetchExecutorRegistry");
        this.agentRunner = Objects.requireNonNull(agentRunner, "agentRunner");
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

    public AgentRunner agentRunner() {
        return agentRunner;
    }

} // Class end.