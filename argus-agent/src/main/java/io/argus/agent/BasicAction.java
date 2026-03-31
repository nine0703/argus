package io.argus.agent;

import io.argus.core.action.Action;
import io.argus.core.action.ActionType;
import io.argus.core.model.Metadata;

import java.util.Objects;

/**
 * Minimal immutable action implementation for default agent execution.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:07
 */
public final class BasicAction implements Action {

    private final ActionType type;
    private final Metadata metadata;

    public BasicAction(ActionType type, Metadata metadata) {
        this.type = Objects.requireNonNull(type, "type");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
    }

    @Override
    public ActionType getType() {
        return type;
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

} // Class end.