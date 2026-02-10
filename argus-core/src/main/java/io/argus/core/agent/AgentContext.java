package io.argus.core.agent;

import io.argus.core.memory.Memory;

/**
 * Provides the mutable execution context for an Agent during live execution.
 *
 * <p>
 * {@code AgentContext} represents the <strong>ephemeral working environment</strong>
 * of an agent while it is executing.
 * It exists to support reasoning, coordination, and integration
 * with external systems during a single execution run.
 *
 * <h2>Mutability Contract</h2>
 *
 * <p>
 * AgentContext is explicitly <strong>mutable</strong>.
 * Implementations MAY expose setters, mutable collections,
 * caches, and integration handles.
 *
 * <p>
 * Mutations to AgentContext are considered transient and
 * MUST NOT be relied upon for replay, auditing, or state reconstruction.
 *
 * <h2>Boundary Between State and Context</h2>
 *
 * <p>
 * The following strict separation MUST be maintained:
 *
 * <ul>
 *   <li>{@link AgentState} — immutable, replayable, auditable snapshot</li>
 *   <li>{@code AgentContext} — mutable, ephemeral, execution-only workspace</li>
 * </ul>
 *
 * <p>
 * Any information that affects future agent behavior
 * and must survive replay or fork MUST be captured
 * in {@link AgentState} or {@link io.argus.core.agent.LoopResult}.
 *
 * <p>
 * AgentContext MUST NOT be treated as hidden state.
 * Storing decision-critical data exclusively in context
 * invalidates replay guarantees and is forbidden.
 *
 * <h2>Execution and Replay</h2>
 *
 * <p>
 * AgentContext exists ONLY during live execution.
 * During replay, AgentContext MAY be absent, empty,
 * or replaced with a no-op implementation.
 *
 * <p>
 * Replay MUST NOT depend on values previously stored
 * in AgentContext.
 *
 * <h2>Allowed Responsibilities</h2>
 *
 * <p>
 * Typical responsibilities of AgentContext include:
 * <ul>
 *   <li>Short-lived reasoning buffers</li>
 *   <li>External service clients</li>
 *   <li>Rate limiters and execution guards</li>
 *   <li>Tracing, metrics, and logging helpers</li>
 *   <li>{@link Memory} access for non-authoritative recall</li>
 * </ul>
 *
 * <h2>Prohibited Responsibilities</h2>
 *
 * <p>
 * AgentContext MUST NOT:
 * <ul>
 *   <li>Contain authoritative agent state</li>
 *   <li>Be required to reconstruct {@link AgentState}</li>
 *   <li>Store irreversible decisions</li>
 *   <li>Hide side effects from audit or LoopResult</li>
 * </ul>
 *
 * <p>
 * 代理执行上下文。
 *
 * <p>
 * {@code AgentContext} 是代理在执行期间使用的可变工作环境，
 * 用于支持推理、协作与外部系统集成。
 *
 * <p>
 * AgentContext 是短暂的、不可回放的，
 * 严格区别于不可变、可审计的 {@link AgentState}。
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:03
 */
public interface AgentContext {

    AgentState getState();

    Memory getMemory();

} // Class end.