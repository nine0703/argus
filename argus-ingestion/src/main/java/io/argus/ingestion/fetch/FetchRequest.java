package io.argus.ingestion.fetch;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Immutable snapshot of a resource fetch request.
 * <p>
 * A FetchRequest represents a deterministic input
 * to a FetchExecutor.
 * <p>
 * Implementations MUST be immutable.
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:58
 */
public interface FetchRequest {

    /**
     * Target resource identifier.
     */
    URI resource();

    /**
     * Protocol of this request.
     */
    FetchProtocol protocol();

    /**
     * Optional metadata for auditing.
     * Must not affect execution semantics.
     */
    Map<String, List<String>> metadata();

} // Class end.