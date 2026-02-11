package io.argus.ingestion.fetch;

/**
 * Identifies a fetch protocol.
 * <p>
 * Protocol is a semantic discriminator used to route
 * FetchRequest to its corresponding FetchExecutor.
 * <p>
 * Protocol itself contains no behavior.
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:58
 */
public interface FetchProtocol {

    /**
     * Unique protocol name.
     * Example: "http", "file", "s3"
     */
    String name();

} // Class end.