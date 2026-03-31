package io.argus.core.action;

import io.argus.core.model.Metadata;
import io.argus.core.model.Timestamped;

/**
 * Represents the authoritative outcome of executing an {@link Action}.
 *
 * <p>
 * {@code ActionResult} captures the execution fact produced after an action
 * has been interpreted and carried out by the runtime or an external system.
 * It is intentionally execution-focused and does not replace the higher-level
 * {@code Observation} model used by agents for reasoning.
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:03
 */
public interface ActionResult extends Timestamped {

    /**
     * Whether the action execution completed successfully.
     */
    boolean success();

    /**
     * Additional execution metadata for audit or replay.
     */
    Metadata metadata();

} // Class end.
