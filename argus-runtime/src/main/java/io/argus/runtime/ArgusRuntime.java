package io.argus.runtime;

import io.argus.agent.AgentRunner;
import io.argus.core.audit.AuditLog;
import io.argus.core.lifecycle.Lifecycle;
import io.argus.core.lifecycle.LifecyclePhase;
import io.argus.core.lifecycle.Stoppable;
import io.argus.core.memory.Memory;
import io.argus.ingestion.audit.IngestionAuditPublisher;
import io.argus.ingestion.audit.fetch.FetchAuditPublisher;
import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchExecutorRegistry;
import io.argus.ingestion.fetch.replay.FetchRecordStore;
import io.argus.ingestion.orchestration.IngestionOrchestrator;
import io.argus.ingestion.policy.FetchPolicy;
import io.argus.ingestion.source.IngestionSource;

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
 *   <li>{@link IngestionAuditPublisher} for ingestion lifecycle publication</li>
 *   <li>{@link FetchExecutorRegistry} for protocol-based fetch dispatch</li>
 *   <li>{@link FetchExecutor} for single-entry fetch execution</li>
 *   <li>{@link IngestionOrchestrator} for default ingestion pipeline execution</li>
 *   <li>{@link AgentRunner} for loop-driven agent execution</li>
 * </ul>
 *
 * <p>
 * The runtime also exposes an explicit lifecycle so starter wiring and plain
 * Java embedding can share the same startup and shutdown semantics.
 *
 * <p>
 * This type is intentionally framework-agnostic.
 * Spring Boot integration is provided by dedicated starter modules.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:05
 */
public final class ArgusRuntime implements Lifecycle, Stoppable {

    private final Memory memory;
    private final AuditLog auditLog;
    private final FetchAuditPublisher fetchAuditPublisher;
    private final IngestionAuditPublisher ingestionAuditPublisher;
    private final FetchExecutorRegistry fetchExecutorRegistry;
    private final FetchRecordStore fetchRecordStore;
    private final FetchPolicy fetchPolicy;
    private final FetchExecutor fetchExecutor;
    private final IngestionOrchestrator ingestionOrchestrator;
    private final IngestionSource ingestionSource;
    private final AgentRunner agentRunner;
    private volatile LifecyclePhase phase;

    public ArgusRuntime(
            Memory memory,
            AuditLog auditLog,
            FetchAuditPublisher fetchAuditPublisher,
            IngestionAuditPublisher ingestionAuditPublisher,
            FetchExecutorRegistry fetchExecutorRegistry,
            FetchRecordStore fetchRecordStore,
            FetchPolicy fetchPolicy,
            FetchExecutor fetchExecutor,
            IngestionOrchestrator ingestionOrchestrator,
            IngestionSource ingestionSource,
            AgentRunner agentRunner
    ) {
        this.memory = Objects.requireNonNull(memory, "memory");
        this.auditLog = Objects.requireNonNull(auditLog, "auditLog");
        this.fetchAuditPublisher = Objects.requireNonNull(fetchAuditPublisher, "fetchAuditPublisher");
        this.ingestionAuditPublisher = Objects.requireNonNull(ingestionAuditPublisher, "ingestionAuditPublisher");
        this.fetchExecutorRegistry = Objects.requireNonNull(fetchExecutorRegistry, "fetchExecutorRegistry");
        this.fetchRecordStore = Objects.requireNonNull(fetchRecordStore, "fetchRecordStore");
        this.fetchPolicy = Objects.requireNonNull(fetchPolicy, "fetchPolicy");
        this.fetchExecutor = Objects.requireNonNull(fetchExecutor, "fetchExecutor");
        this.ingestionOrchestrator = Objects.requireNonNull(ingestionOrchestrator, "ingestionOrchestrator");
        this.ingestionSource = Objects.requireNonNull(ingestionSource, "ingestionSource");
        this.agentRunner = Objects.requireNonNull(agentRunner, "agentRunner");
        this.phase = LifecyclePhase.CREATED;
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

    public IngestionAuditPublisher ingestionAuditPublisher() {
        return ingestionAuditPublisher;
    }

    public FetchExecutorRegistry fetchExecutorRegistry() {
        return fetchExecutorRegistry;
    }

    public FetchRecordStore fetchRecordStore() {
        return fetchRecordStore;
    }

    public FetchPolicy fetchPolicy() {
        return fetchPolicy;
    }

    public FetchExecutor fetchExecutor() {
        return fetchExecutor;
    }

    public IngestionOrchestrator ingestionOrchestrator() {
        return ingestionOrchestrator;
    }

    public IngestionSource ingestionSource() {
        return ingestionSource;
    }

    public AgentRunner agentRunner() {
        return agentRunner;
    }

    @Override
    public synchronized LifecyclePhase phase() {
        return phase;
    }

    @Override
    public synchronized ArgusRuntime start() {

        if (phase == LifecyclePhase.RUNNING || phase == LifecyclePhase.STARTING) {
            return this;
        }
        if (phase == LifecyclePhase.STOPPING) {
            throw new IllegalStateException("runtime is stopping and cannot be started");
        }
        if (phase == LifecyclePhase.FAILED) {
            throw new IllegalStateException("failed runtime must be recreated before start");
        }

        phase = LifecyclePhase.STARTING;
        phase = LifecyclePhase.RUNNING;
        return this;
    }

    @Override
    public synchronized void stop() {

        if (phase == LifecyclePhase.STOPPED) {
            return;
        }
        if (phase == LifecyclePhase.CREATED) {
            phase = LifecyclePhase.STOPPED;
            return;
        }

        phase = LifecyclePhase.STOPPING;
        phase = LifecyclePhase.STOPPED;
    }

} // Class end.
