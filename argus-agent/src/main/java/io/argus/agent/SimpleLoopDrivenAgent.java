package io.argus.agent;

import io.argus.core.agent.AgentLoop;
import io.argus.core.agent.AgentState;

import java.util.Objects;

/**
 * Immutable default implementation of {@link LoopDrivenAgent}.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:06
 */
public final class SimpleLoopDrivenAgent implements LoopDrivenAgent {

    private final AgentState initialState;
    private final AgentLoop loop;

    public SimpleLoopDrivenAgent(AgentState initialState, AgentLoop loop) {
        this.initialState = Objects.requireNonNull(initialState, "initialState");
        this.loop = Objects.requireNonNull(loop, "loop");
    }

    @Override
    public AgentState initialState() {
        return initialState;
    }

    @Override
    public AgentLoop loop() {
        return loop;
    }

} // Class end.