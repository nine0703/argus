package io.argus.core.action;

import io.argus.core.model.Metadata;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable default implementation of {@link ActionResult}.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:40
 */
public final class DefaultActionResult implements ActionResult {

    private final boolean success;
    private final Instant timestamp;
    private final Metadata metadata;

    public DefaultActionResult(boolean success, Instant timestamp, Metadata metadata) {
        this.success = success;
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public Instant timestamp() {
        return timestamp;
    }

    @Override
    public Metadata metadata() {
        return metadata;
    }

} // Class end.
