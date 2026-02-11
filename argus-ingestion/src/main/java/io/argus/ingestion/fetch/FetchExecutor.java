package io.argus.ingestion.fetch;

/**
 * Executes a FetchRequest.
 * <p>
 * Implementations must be:
 * - deterministic
 * - side-effect controlled
 * - auditable
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:34
 */
public interface FetchExecutor {

    FetchProtocol protocol();

    FetchResult execute(FetchRequest request);

}
