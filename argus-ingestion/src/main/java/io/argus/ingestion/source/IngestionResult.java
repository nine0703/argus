package io.argus.ingestion.source;

import io.argus.core.observation.Observation;

import java.io.Serializable;

/**
 * Represents the authoritative factual outcome of a single ingestion request.
 *
 * <p>
 * {@code IngestionResult} captures what was externally observed
 * when executing an {@link IngestionRequest}.
 *
 * <p>
 * It represents a <strong>fact about the world at a specific moment</strong>.
 * Once created, it MUST be treated as immutable and authoritative.
 *
 * <h2>Fact Semantics</h2>
 *
 * <p>
 * An IngestionResult does not describe what was intended,
 * but what was actually observed.
 *
 * <p>
 * It MUST contain sufficient information to:
 * <ul>
 *   <li>Audit the ingestion outcome</li>
 *   <li>Reconstruct the corresponding {@link Observation}</li>
 *   <li>Support deterministic replay</li>
 * </ul>
 *
 * <h2>Immutability Contract</h2>
 *
 * <p>
 * IngestionResult MUST be immutable.
 * No field may change after construction.
 *
 * <p>
 * Implementations MUST NOT expose mutable internal structures
 * without defensive copying.
 *
 * <h2>Replay Semantics</h2>
 *
 * <p>
 * During replay, IngestionResult serves as the sole
 * source of truth for reconstructing ingestion observations.
 *
 * <p>
 * Replay MUST NOT trigger new external access.
 * No network calls, file reads, or environmental queries
 * are permitted during replay.
 *
 * <p>
 * If replay is requested but no corresponding IngestionResult
 * exists, the system MUST fail deterministically.
 *
 * <h2>Determinism Requirement</h2>
 *
 * <p>
 * Two IngestionResult instances that are structurally equal
 * MUST represent semantically identical world observations.
 *
 * <p>
 * Equality MUST be defined in terms of logical content,
 * not object identity.
 *
 * <h2>Relationship to Observation</h2>
 *
 * <p>
 * IngestionResult is a lower-level fact container.
 * {@link Observation} is the structured interpretation
 * of that fact for agent consumption.
 *
 * <p>
 * The transformation from IngestionResult to Observation
 * SHOULD be deterministic and side-effect free.
 *
 * <h2>Prohibited Responsibilities</h2>
 *
 * <p>
 * IngestionResult MUST NOT:
 * <ul>
 *   <li>Contain executable logic</li>
 *   <li>Trigger external side effects</li>
 *   <li>Depend on global mutable state</li>
 *   <li>Require external services for interpretation</li>
 * </ul>
 *
 * <p>
 * 数据获取结果。
 *
 * <p>
 * {@code IngestionResult} 表示一次数据获取操作的权威事实结果，
 * 描述了在某一时刻外部世界实际返回的内容。
 *
 * <p>
 * 它必须是不可变的，并在回放时作为唯一真实来源，
 * 不得重新访问外部系统。
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:57
 */
public interface IngestionResult extends Serializable {
} // Class end.