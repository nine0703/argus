package io.argus.ingestion.source;

import java.io.Serializable;

/**
 * Represents a complete, immutable snapshot of an ingestion attempt.
 *
 * <p>
 * {@code IngestionRequest} defines the full declarative description
 * of a single ingestion operation.
 *
 * <p>
 * It MUST contain all information required to:
 * <ul>
 *   <li>Execute the ingestion during LIVE mode</li>
 *   <li>Audit what was requested</li>
 *   <li>Deterministically replay the ingestion in REPLAY mode</li>
 * </ul>
 *
 * <h2>Snapshot Semantics</h2>
 *
 * <p>
 * IngestionRequest is a <strong>logical snapshot</strong>.
 * It must be self-contained and independently interpretable.
 *
 * <p>
 * No hidden defaults, implicit configuration,
 * global static state, or environmental assumptions
 * may be required to understand or execute this request.
 *
 * <h2>Immutability Contract</h2>
 *
 * <p>
 * IngestionRequest MUST be immutable.
 * All fields must be fixed at construction time.
 *
 * <p>
 * Implementations MUST NOT expose setters or mutable internal state.
 *
 * <h2>Determinism Requirement</h2>
 *
 * <p>
 * Two IngestionRequest instances that are structurally equal
 * MUST represent semantically identical ingestion intent.
 *
 * <p>
 * Equality MUST NOT depend on object identity.
 *
 * <h2>Replay Safety</h2>
 *
 * <p>
 * During replay, an IngestionRequest MUST serve as
 * the authoritative reference describing what was originally requested.
 *
 * <p>
 * Replay MUST NOT depend on external configuration
 * that is not represented in the request snapshot.
 *
 * <h2>Allowed Contents</h2>
 *
 * <p>
 * Typical fields MAY include:
 * <ul>
 *   <li>Target resource identifier (e.g., URL, file path)</li>
 *   <li>Protocol or method (e.g., GET, POST)</li>
 *   <li>Headers and request parameters</li>
 *   <li>Authentication descriptor (snapshot form only)</li>
 *   <li>Timeout configuration</li>
 *   <li>Declared content expectations</li>
 * </ul>
 *
 * <h2>Prohibited Contents</h2>
 *
 * <p>
 * IngestionRequest MUST NOT:
 * <ul>
 *   <li>Contain live network clients or connection objects</li>
 *   <li>Contain executable logic or callbacks</li>
 *   <li>Reference global mutable state</li>
 *   <li>Depend on the current system clock implicitly</li>
 * </ul>
 *
 * <h2>Serialization</h2>
 *
 * <p>
 * Implementations SHOULD be serializable to a stable format
 * (e.g., JSON) for storage, auditing, and replay.
 *
 * <p>
 * 数据获取请求快照。
 *
 * <p>
 * {@code IngestionRequest} 表示一次数据获取操作的完整声明式快照，
 * 必须包含执行、审计与回放所需的全部信息。
 *
 * <p>
 * 它是不可变、自包含的，不得依赖隐式配置或全局状态。
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:57
 */
public interface IngestionRequest extends Serializable {
} // Class end.
