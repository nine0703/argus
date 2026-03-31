package io.argus.agent;

import io.argus.core.agent.AgentState;
import io.argus.core.agent.LoopResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable result of a complete loop-driven agent run.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:08
 */
public final class AgentRunResult {

    private final AgentState terminalState;
    private final int stepCount;
    private final List<LoopResult> loopResults;

    public AgentRunResult(AgentState terminalState, int stepCount, List<LoopResult> loopResults) {
        this.terminalState = Objects.requireNonNull(terminalState, "terminalState");
        this.stepCount = stepCount;
        this.loopResults = Collections.unmodifiableList(new ArrayList<>(loopResults));
    }

    public AgentState terminalState() {
        return terminalState;
    }

    public int stepCount() {
        return stepCount;
    }

    public List<LoopResult> loopResults() {
        return loopResults;
    }

} // Class end.