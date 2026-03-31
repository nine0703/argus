package io.argus.core.agent;

import io.argus.core.action.Action;
import io.argus.core.action.ActionResult;
import io.argus.core.observation.Observation;

/**
 * Represents the immutable outcome of a single AgentLoop step.
 *
 * <p>
 * {@code LoopResult} is the minimal, authoritative record of one
 * agent decision cycle.
 * It captures <strong>what the agent intended to do</strong>,
 * <strong>what was observed as a result</strong>,
 * and <strong>how the agent state transitioned</strong>.
 *
 * <p>
 * A LoopResult MUST be:
 * <ul>
 *   <li>Immutable</li>
 *   <li>Self-contained</li>
 *   <li>Sufficient for deterministic replay</li>
 * </ul>
 *
 * <h2>Replay Semantics</h2>
 *
 * <p>
 * LoopResult defines the replay contract of the Agent system.
 * Given the same initial {@link AgentState} and the same ordered
 * sequence of {@code LoopResult} instances, an implementation MUST
 * be able to reproduce the same agent state transitions without
 * re-executing the original decision logic.
 *
 * <p>
 * During replay:
 * <ul>
 *   <li>{@link Action} represents the agent's committed intent</li>
 *   <li>{@link ActionResult} represents the authoritative execution outcome</li>
 *   <li>{@link Observation} represents the committed factual outcome</li>
 *   <li>No external side effects may be re-triggered</li>
 * </ul>
 *
 * <p>
 * Replay MUST be passive and referentially transparent.
 * That is, replaying a LoopResult sequence must not depend on
 * external systems, clocks, randomness, or hidden state.
 *
 * <h2>Execution vs Replay</h2>
 *
 * <p>
 * A LoopResult bridges live execution and replay:
 * <ul>
 *   <li>During execution, it is produced by {@link AgentLoop#step}</li>
 *   <li>During replay, it is consumed as a source of truth</li>
 * </ul>
 *
 * <p>
 * Implementations MUST NOT embed executable logic inside LoopResult.
 * It is a pure data carrier representing an execution fact,
 * not a behavior.
 *
 * <p>
 * 单步执行结果。
 *
 * <p>
 * {@code LoopResult} 表示代理一次执行步骤的不可变结果，
 * 是代理决策过程的最小、权威事实记录。
 * 它描述了代理的意图、观测结果以及状态演进。
 *
 * <p>
 * LoopResult 必须是不可变、自包含的，
 * 并且足以支持确定性的回放。
 *
 * <p>
 * 在回放过程中，LoopResult 作为唯一真实来源，
 * 不允许重新触发外部副作用或重新执行决策逻辑。
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:23
 */
public final class LoopResult {
    /**
     * Returns the action produced by the agent during this step.
     */
    private final Action action;
    /**
     * Returns the execution result produced for the action.
     */
    private final ActionResult actionResult;
    /**
     * Returns the observation obtained as a result of the action.
     */
    private final Observation observation;
    /**
     * Returns the agent state after this step has completed.
     */
    private final AgentState nextState;

    public LoopResult(
            Action action,
            ActionResult actionResult,
            Observation observation,
            AgentState nextState
    ) {
        this.action = action;
        this.actionResult = actionResult;
        this.observation = observation;
        this.nextState = nextState;
    }

    public LoopResult(
            Action action,
            Observation observation,
            AgentState nextState
    ) {
        this(action, null, observation, nextState);
    }

    public Action getAction() {
        return action;
    }

    public ActionResult getActionResult() {
        return actionResult;
    }

    public Observation getObservation() {
        return observation;
    }

    public AgentState getNextState() {
        return nextState;
    }

}
