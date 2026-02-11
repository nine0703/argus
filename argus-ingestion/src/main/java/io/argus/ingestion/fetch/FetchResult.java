package io.argus.ingestion.fetch;

import java.util.List;
import java.util.Map;

/**
 * Immutable result of a fetch execution.
 * <p>
 * FetchResult must contain enough information
 * to support replay and audit.
 * <p>
 * Implementations MUST be immutable.
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:58
 */
public interface FetchResult {

    /**
     * Protocol of the execution.
     */
    FetchProtocol protocol();

    /**
     * Raw payload bytes.
     */
    byte[] body();

    /**
     * Metadata returned from fetch execution.
     */
    Map<String, List<String>> metadata();

    /**
     * Whether fetch succeeded.
     */
    boolean success();

} // Class end.