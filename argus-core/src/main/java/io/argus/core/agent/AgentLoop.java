package io.argus.core.agent;

import io.argus.core.action.Action;
import io.argus.core.observation.Observation;

/**
 * Drives the execution of an Agent through explicit decision steps.
 *
 * <p>
 * {@code AgentLoop} defines the core execution semantics of an Agent.
 * It is responsible for advancing the agent's state in discrete,
 * observable steps.
 *
 * <p>
 * An AgentLoop does not prescribe how execution is implemented
 * (e.g. synchronous, asynchronous, event-driven, or distributed).
 * Instead, it defines a deterministic stepping model that can be
 * audited, replayed, and controlled.
 *
 * <p>
 * Each invocation of {@link #step(AgentContext)} represents
 * <strong>one atomic decision cycle</strong> of the agent:
 * <ul>
 *   <li>Evaluating the current context and state</li>
 *   <li>Producing an {@link Action} as an expression of intent</li>
 *   <li>Receiving an {@link Observation} as a factual outcome</li>
 *   <li>Transitioning to a new {@link AgentState}</li>
 * </ul>
 *
 * <p>
 * AgentLoop is intentionally minimal and framework-agnostic.
 * Implementations must not assume a specific runtime, threading model,
 * or execution environment.
 *
 * <p>
 * Agent 执行循环模型。
 *
 * <p>
 * {@code AgentLoop} 定义了代理的核心执行语义，
 * 通过离散、可观测的步骤驱动代理状态的演进。
 *
 * <p>
 * AgentLoop 不限定具体执行方式
 * （例如同步、异步、事件驱动或分布式），
 * 而是提供一种可审计、可回放、可控制的单步执行模型。
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:03
 */
public interface AgentLoop {

    /**
     * Executes a single decision step of the agent.
     *
     * <p>
     * A step represents an atomic unit of agent execution.
     * Implementations MUST ensure that each step is:
     * <ul>
     *   <li>Deterministic with respect to the provided context</li>
     *   <li>Observable via returned action and observation</li>
     *   <li>Auditable as a standalone execution fact</li>
     * </ul>
     *
     * <p>
     * The step operation MUST NOT perform unbounded loops.
     * Long-running execution MUST be expressed as multiple step invocations.
     *
     * <p>
     * If execution cannot proceed (e.g. due to terminal state),
     * implementations SHOULD reflect this via {@link #isRunning()}.
     *
     * <p>
     * 执行代理的一次决策步骤。
     *
     * <p>
     * 一次 step 表示代理执行过程中的一个原子决策单元。
     * 每一次 step 都应当是：
     * <ul>
     *   <li>相对于当前上下文可确定的</li>
     *   <li>可观测的（产生 Action 与 Observation）</li>
     *   <li>可审计的独立事实</li>
     * </ul>
     *
     * <p>
     * step 操作不得包含无限循环，
     * 长时间运行的代理行为必须通过多次 step 表达。
     * @param context the current agent execution context
     * @return the result of this execution step
     */
    LoopResult step(AgentContext context);

    /**
     * Indicates whether the agent loop is still running.
     *
     * <p>
     * Returns {@code false} if the agent has reached a terminal state
     * or has been explicitly stopped.
     *
     * <p>
     * 表示代理循环是否仍处于运行状态。
     * @return {@code true} if further steps may be executed
     */
    boolean isRunning();

    /**
     * Requests termination of the agent loop.
     *
     * <p>
     * This method signals the loop to stop execution.
     * Implementations may perform graceful shutdown and are
     * not required to stop immediately.
     *
     * <p>
     * 请求终止代理执行循环。
     * 实现可以选择优雅停止，而不要求立即中断。
     */
    void stop();

} // Class end.