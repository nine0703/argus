package io.argus.core.model;

import java.time.Instant;

/**
 * Timestamp mixin for immutable domain values.
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:05
 */
public interface Timestamped {

    /**
     * Returns the authoritative timestamp of this value object.
     */
    Instant timestamp();

} // Class end.
