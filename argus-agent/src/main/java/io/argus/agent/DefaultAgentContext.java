package io.argus.agent;

import io.argus.core.agent.AgentContext;
import io.argus.core.agent.AgentState;
import io.argus.core.memory.Memory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Mutable default {@link AgentContext} implementation.
 *
 * <p>
 * This context is designed for live execution only. It keeps the current
 * state reference plus a small attribute bag for transient coordination data.
 * The attribute bag is intentionally not part of replay semantics.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:06
 */
public class DefaultAgentContext implements AgentContext {

    private AgentState state;
    private final Memory memory;
    private final Map<String, Object> attributes = new LinkedHashMap<>();

    public DefaultAgentContext(AgentState state, Memory memory) {
        this.state = Objects.requireNonNull(state, "state");
        this.memory = Objects.requireNonNull(memory, "memory");
    }

    @Override
    public AgentState getState() {
        return state;
    }

    @Override
    public Memory getMemory() {
        return memory;
    }

    public void setState(AgentState state) {
        this.state = Objects.requireNonNull(state, "state");
    }

    public void putAttribute(String key, Object value) {
        attributes.put(Objects.requireNonNull(key, "key"), value);
    }

    public Optional<Object> getAttribute(String key) {
        return Optional.ofNullable(attributes.get(key));
    }

    public Map<String, Object> snapshotAttributes() {
        return new LinkedHashMap<>(attributes);
    }

} // Class end.