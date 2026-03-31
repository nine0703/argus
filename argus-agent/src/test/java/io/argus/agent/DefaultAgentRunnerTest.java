package io.argus.agent;

import io.argus.core.action.ActionType;
import io.argus.core.agent.AgentContext;
import io.argus.core.agent.AgentLoop;
import io.argus.core.agent.AgentState;
import io.argus.core.agent.LoopResult;
import io.argus.core.audit.InMemoryAuditLog;
import io.argus.core.memory.InMemoryMemory;
import io.argus.core.memory.MemoryEntry;
import io.argus.core.memory.MemoryScope;
import io.argus.core.model.Metadata;
import io.argus.core.observation.ObservationType;
import junit.framework.TestCase;

import java.util.List;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:16
 */
public class DefaultAgentRunnerTest extends TestCase {

    public void testShouldRunLoopUntilCompleted() {

        InMemoryMemory memory = new InMemoryMemory();
        InMemoryAuditLog auditLog = new InMemoryAuditLog();
        DefaultAgentRunner runner = new DefaultAgentRunner(memory, auditLog);
        LoopDrivenAgent agent = new SimpleLoopDrivenAgent(AgentState.CREATED, new TwoStepLoop());

        AgentRunResult result = runner.run(agent, 4);

        assertEquals(AgentState.COMPLETED, result.terminalState());
        assertEquals(2, result.stepCount());
        assertEquals(2, result.loopResults().size());
        List<MemoryEntry> entries = memory.recall(MemoryScope.WORKING);
        assertEquals(2, entries.size());
        assertEquals("RESPONSE", entries.get(1).getMetadata().get("observationType").orElse(null));
        assertEquals(4, auditLog.snapshot().size());
    }

    public void testShouldFailWhenStepLimitExceeded() {

        InMemoryMemory memory = new InMemoryMemory();
        InMemoryAuditLog auditLog = new InMemoryAuditLog();
        DefaultAgentRunner runner = new DefaultAgentRunner(memory, auditLog);
        LoopDrivenAgent agent = new SimpleLoopDrivenAgent(AgentState.CREATED, new EndlessLoop());

        try {
            runner.run(agent, 1);
            fail("Expected AgentExecutionException");
        } catch (RuntimeException expected) {
            assertEquals("agent run exceeded maxSteps=1", expected.getMessage());
            assertEquals(3, auditLog.snapshot().size());
        }
    }

    private static final class TwoStepLoop implements AgentLoop {

        private int counter;
        private boolean running = true;

        @Override
        public LoopResult step(AgentContext context) {
            counter++;
            if (counter == 1) {
                return new LoopResult(
                        new BasicAction(ActionType.DECIDE, new Metadata(Map.of("phase", "plan"))),
                        new BasicObservation(ObservationType.STATE, new Metadata(Map.of("phase", "running"))),
                        AgentState.RUNNING
                );
            }

            running = false;
            return new LoopResult(
                    new BasicAction(ActionType.EMIT, new Metadata(Map.of("phase", "emit"))),
                    new BasicObservation(ObservationType.RESPONSE, new Metadata(Map.of("phase", "done"))),
                    AgentState.COMPLETED
            );
        }

        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public void stop() {
            running = false;
        }
    }

    private static final class EndlessLoop implements AgentLoop {

        @Override
        public LoopResult step(AgentContext context) {
            return new LoopResult(
                    new BasicAction(ActionType.DECIDE, new Metadata(Map.of())),
                    new BasicObservation(ObservationType.STATE, new Metadata(Map.of())),
                    AgentState.RUNNING
            );
        }

        @Override
        public boolean isRunning() {
            return true;
        }

        @Override
        public void stop() {
        }
    }

} // Class end.