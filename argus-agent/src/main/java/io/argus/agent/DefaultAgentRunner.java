package io.argus.agent;

import io.argus.core.agent.AgentLoop;
import io.argus.core.agent.AgentState;
import io.argus.core.agent.LoopResult;
import io.argus.core.audit.AuditEvent;
import io.argus.core.audit.AuditLevel;
import io.argus.core.audit.AuditLog;
import io.argus.core.error.AgentExecutionException;
import io.argus.core.memory.Memory;
import io.argus.core.memory.MemoryEntry;
import io.argus.core.memory.MemoryScope;
import io.argus.core.model.Metadata;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Default in-process runner for {@link LoopDrivenAgent}.
 *
 * <p>
 * The runner executes one explicit loop step at a time until the loop stops,
 * the agent reaches a terminal state, or a configured safety limit is hit.
 * Each step is bridged into audit and memory infrastructure.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:08
 */
public class DefaultAgentRunner implements AgentRunner {

    private final Memory memory;
    private final AuditLog auditLog;

    public DefaultAgentRunner(Memory memory, AuditLog auditLog) {
        this.memory = Objects.requireNonNull(memory, "memory");
        this.auditLog = Objects.requireNonNull(auditLog, "auditLog");
    }

    @Override
    public AgentRunResult run(LoopDrivenAgent agent, int maxSteps) {

        Objects.requireNonNull(agent, "agent");
        if (maxSteps <= 0) {
            throw new IllegalArgumentException("maxSteps must be positive");
        }

        DefaultAgentContext context = new DefaultAgentContext(agent.initialState(), memory);
        AgentLoop loop = agent.loop();
        List<LoopResult> results = new ArrayList<>();
        int steps = 0;

        auditLog.record(auditEvent(AuditLevel.INFO, "AGENT_RUN_STARTED", agent.initialState(), 0, null));

        try {
            while (loop.isRunning() && !context.getState().isTerminal()) {
                if (steps >= maxSteps) {
                    throw new AgentExecutionException("agent run exceeded maxSteps=" + maxSteps);
                }

                LoopResult result = loop.step(context);
                results.add(result);
                steps++;
                context.setState(result.getNextState());
                storeObservation(result, steps);
                auditLog.record(auditEvent(AuditLevel.INFO, "AGENT_STEP_COMPLETED", result.getNextState(), steps, result));
            }

            AgentState terminalState = context.getState();
            auditLog.record(auditEvent(AuditLevel.INFO, "AGENT_RUN_COMPLETED", terminalState, steps, null));
            return new AgentRunResult(terminalState, steps, results);
        } catch (RuntimeException e) {
            AgentState failedState = AgentState.FAILED;
            context.setState(failedState);
            auditLog.record(auditEvent(AuditLevel.ERROR, "AGENT_RUN_FAILED", failedState, steps, null));
            if (e instanceof AgentExecutionException) {
                throw e;
            }
            throw new AgentExecutionException("agent run failed", e);
        }
    }

    private void storeObservation(LoopResult result, int stepNumber) {

        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("step", stepNumber);
        attributes.put("actionType", result.getAction().getType().name());
        attributes.put("observationType", result.getObservation().getType().name());
        attributes.put("nextState", result.getNextState().name());

        memory.store(
                new MemoryEntry(
                        UUID.randomUUID().toString(),
                        MemoryScope.WORKING,
                        result.getObservation(),
                        new Metadata(attributes),
                        Instant.now().toEpochMilli()
                )
        );
    }

    private AuditEvent auditEvent(
            AuditLevel level,
            String type,
            AgentState state,
            int stepCount,
            LoopResult result
    ) {

        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("state", state.name());
        attributes.put("stepCount", stepCount);
        if (result != null) {
            attributes.put("actionType", result.getAction().getType().name());
            attributes.put("observationType", result.getObservation().getType().name());
        }

        return new AuditEvent(
                UUID.randomUUID().toString(),
                level,
                type,
                type + " state=" + state.name(),
                new Metadata(attributes),
                Instant.now().toEpochMilli()
        );
    }

} // Class end.