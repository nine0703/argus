package io.argus.agent;

import io.argus.core.model.Metadata;
import io.argus.core.observation.Observation;
import io.argus.core.observation.ObservationType;

import java.util.Objects;

/**
 * Minimal immutable observation implementation for default agent execution.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:07
 */
public final class BasicObservation implements Observation {

    private final ObservationType type;
    private final Metadata metadata;

    public BasicObservation(ObservationType type, Metadata metadata) {
        this.type = Objects.requireNonNull(type, "type");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
    }

    @Override
    public ObservationType getType() {
        return type;
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

} // Class end.