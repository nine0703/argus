package io.argus.agent;

import io.argus.core.agent.Agent;
import io.argus.core.agent.AgentLoop;

/**
 * Executable agent contract backed by an explicit {@link AgentLoop}.
 *
 * <p>
 * The core module intentionally keeps {@link Agent} minimal. This extension
 * bridges that contract to a concrete loop-driven execution model without
 * polluting {@code argus-core} with runtime concerns.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:05
 */
public interface LoopDrivenAgent extends Agent {

    AgentLoop loop();

} // Class end.