package io.argus.core.agent;

import junit.framework.TestCase;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:15
 */
public class AgentStateTest extends TestCase {

    public void testShouldExposeTerminalSemantics() {
        assertFalse(AgentState.CREATED.isTerminal());
        assertFalse(AgentState.RUNNING.isTerminal());
        assertTrue(AgentState.COMPLETED.isTerminal());
        assertTrue(AgentState.FAILED.isTerminal());
        assertTrue(AgentState.STOPPED.isTerminal());
    }

} // Class end.