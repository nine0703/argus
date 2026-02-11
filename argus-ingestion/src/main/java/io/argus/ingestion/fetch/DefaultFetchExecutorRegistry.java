package io.argus.ingestion.fetch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:44
 */
public class DefaultFetchExecutorRegistry implements FetchExecutorRegistry {

    private final Map<String, FetchExecutor> executors = new ConcurrentHashMap<>();

    @Override
    public void register(FetchExecutor executor) {
        executors.put(executor.protocol().name(), executor);
    }

    @Override
    public FetchExecutor get(FetchProtocol protocol) {
        FetchExecutor executor = executors.get(protocol.name());
        if (executor == null) {
            throw new IllegalStateException(
                    "No FetchExecutor registered for protocol: " + protocol.name()
            );
        }
        return executor;
    }

    @Override
    public FetchResult execute(FetchRequest request) {
        return get(request.protocol()).execute(request);
    }

}   // Class end.