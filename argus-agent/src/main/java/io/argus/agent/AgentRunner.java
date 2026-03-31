package io.argus.agent;

/**
 * Contract for executing a loop-driven agent to completion.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:27
 */
public interface AgentRunner {

    AgentRunResult run(LoopDrivenAgent agent, int maxSteps);

} // Class end.