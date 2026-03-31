package io.argus.ingestion.fetch;

/**
 * Registry for FetchExecutor implementations.
 * <p>
 * Responsible for routing FetchRequest to the correct executor
 * based on FetchProtocol.
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:43
 */
public interface FetchExecutorRegistry {

    /**
     * Register an executor.
     */
    void register(FetchExecutor executor);

    /**
     * Find executor by protocol.
     */
    FetchExecutor get(FetchProtocol protocol);

    /**
     * Whether an executor is registered for the given protocol.
     */
    boolean supports(FetchProtocol protocol);

    /**
     * Execute a request.
     */
    FetchResult execute(FetchRequest request);

}