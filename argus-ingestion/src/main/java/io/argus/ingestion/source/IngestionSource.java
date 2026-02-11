package io.argus.ingestion.source;

import io.argus.core.audit.AuditEvent;
import io.argus.core.observation.Observation;

/**
 * Represents a source of external world input for ingestion.
 *
 * <p>
 * {@code IngestionSource} defines the authoritative boundary between
 * the ARGUS runtime and the external world.
 * It is responsible for producing factual observations
 * about the external environment.
 *
 * <h2>Fact Semantics</h2>
 *
 * <p>
 * Any successful ingestion performed by an IngestionSource
 * represents a <strong>fact</strong>.
 *
 * <p>
 * A fact is defined as:
 * <ul>
 *   <li>An observation that reflects what was externally observed</li>
 *   <li>Bound to a specific request snapshot</li>
 *   <li>Independent of agent reasoning or intent</li>
 * </ul>
 *
 * <p>
 * Once produced, an ingestion result MUST be treated as immutable
 * and authoritative. It represents what the world returned,
 * not what the agent expected or desired.
 *
 * <h2>Replay Semantics</h2>
 *
 * <p>
 * IngestionSource is replay-aware by contract.
 *
 * <p>
 * During live execution, an IngestionSource MAY perform
 * irreversible side effects such as network requests or file reads.
 *
 * <p>
 * During replay, an IngestionSource MUST NOT re-access
 * the external world.
 * Instead, it MUST reproduce {@link Observation} instances
 * exclusively from previously recorded ingestion results.
 *
 * <p>
 * Replay MUST be deterministic and passive.
 * No new external observations may be introduced during replay.
 *
 * <h2>Request Snapshot Requirement</h2>
 *
 * <p>
 * Every ingestion operation MUST be fully described
 * by an {@link IngestionRequest}.
 *
 * <p>
 * The request snapshot MUST contain all information required
 * to audit, reason about, and replay the ingestion step.
 * Hidden defaults or implicit configuration are forbidden.
 *
 * <h2>Auditing</h2>
 *
 * <p>
 * IngestionSource MUST emit {@link AuditEvent}s
 * describing ingestion attempts, successes, and failures.
 *
 * <p>
 * Auditing MUST occur regardless of execution mode
 * (live or replay), allowing a complete reconstruction
 * of ingestion history.
 *
 * <h2>Execution Modes</h2>
 *
 * <p>
 * Implementations MUST respect {@link IngestionMode}:
 * <ul>
 *   <li>{@code LIVE}: access the external world</li>
 *   <li>{@code REPLAY}: reconstruct from recorded facts only</li>
 *   <li>{@code DRY_RUN}: validate without producing facts</li>
 * </ul>
 *
 * <h2>Non-Responsibilities</h2>
 *
 * <p>
 * IngestionSource MUST NOT:
 * <ul>
 *   <li>Contain agent decision logic</li>
 *   <li>Mutate {@link io.argus.core.agent.AgentState}</li>
 *   <li>Perform parsing beyond fact acquisition</li>
 *   <li>Hide side effects from audit or replay</li>
 * </ul>
 *
 * <p>
 * 数据获取源。
 *
 * <p>
 * {@code IngestionSource} 定义了 ARGUS 与外部世界之间的权威边界，
 * 用于生成关于外部环境的事实性观测。
 *
 * <p>
 * 一次成功的数据获取即构成一条事实，
 * 在回放时必须被视为权威结果，而不得重新访问外部世界。
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:57
 */
public class IngestionSource {
} // Class end.