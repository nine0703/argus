package io.argus.core.agent;

/**
 * Represents the complete, authoritative state of an Agent at a specific point in time.
 *
 * <p>
 * {@code AgentState} is a <strong>logical snapshot</strong> of an agent.
 * It captures all information required to continue execution
 * or to deterministically replay past execution steps.
 *
 * <h2>Immutability Contract</h2>
 *
 * <p>
 * AgentState MUST be treated as immutable.
 * Implementations MUST NOT expose mutator methods or allow
 * in-place modification of state after creation.
 *
 * <p>
 * Any state transition MUST produce a new AgentState instance.
 * In-place mutation invalidates replay guarantees and is strictly forbidden.
 *
 * <h2>Snapshot Semantics</h2>
 *
 * <p>
 * Each AgentState represents a full snapshot of the agent at the end
 * of a single {@link AgentLoop} step.
 * It is not a delta and must not depend on prior AgentState instances
 * to be interpreted correctly.
 *
 * <p>
 * This design enables:
 * <ul>
 *   <li>Deterministic replay</li>
 *   <li>Time-travel debugging</li>
 *   <li>State forking and branching</li>
 *   <li>Auditable execution history</li>
 * </ul>
 *
 * <h2>Execution and Replay</h2>
 *
 * <p>
 * During live execution, AgentState represents the current working state
 * of the agent.
 *
 * <p>
 * During replay, AgentState MUST be reconstructed exclusively
 * from previously recorded {@link LoopResult} instances,
 * without consulting external systems or global state.
 *
 * <h2>Identity and Equality</h2>
 *
 * <p>
 * Implementations SHOULD define structural equality for AgentState.
 * Two AgentState instances that represent the same logical state
 * SHOULD be considered equal, regardless of object identity.
 *
 * <p>
 * However, object identity MUST NOT be relied upon
 * for correctness or comparison.
 *
 * <p>
 * 代理状态。
 *
 * <p>
 * {@code AgentState} 表示代理在某一时刻的完整逻辑状态快照，
 * 是执行与回放的权威数据来源。
 *
 * <p>
 * AgentState 必须被视为不可变对象，
 * 每一次状态变化都必须生成新的状态实例。
 * 不允许原地修改。
 *
 * <p>
 * AgentState 是完整快照而非增量，
 * 不得依赖历史状态才能被正确解释。
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:03
 */
public enum AgentState {

} // Class end.