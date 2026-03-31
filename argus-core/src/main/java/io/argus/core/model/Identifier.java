package io.argus.core.model;

/**
 * Stable identifier mixin for immutable domain values.
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:05
 */
public interface Identifier {

    /**
     * Returns the stable identifier of this value object.
     */
    String identifier();

} // Class end.
