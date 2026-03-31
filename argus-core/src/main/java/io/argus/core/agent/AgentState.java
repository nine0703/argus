package io.argus.core.agent;

/**
 * Authoritative lifecycle states of an agent execution.
 *
 * <p>
 * The states in this enum are intentionally coarse-grained. They describe the
 * externally observable execution phase of an agent, not internal reasoning
 * subtleties.
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:03
 */
public enum AgentState {

    CREATED(false),

    RUNNING(false),

    WAITING(false),

    COMPLETED(true),

    FAILED(true),

    STOPPED(true);

    private final boolean terminal;

    AgentState(boolean terminal) {
        this.terminal = terminal;
    }

    public boolean isTerminal() {
        return terminal;
    }

} // Class end.