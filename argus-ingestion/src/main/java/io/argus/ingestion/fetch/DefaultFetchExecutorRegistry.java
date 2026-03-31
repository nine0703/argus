package io.argus.ingestion.fetch;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:44
 */
public class DefaultFetchExecutorRegistry implements FetchExecutorRegistry {

    private final Map<String, FetchExecutor> executors = new ConcurrentHashMap<>();

    @Override
    public void register(FetchExecutor executor) {

        Objects.requireNonNull(executor, "executor");
        String protocol = normalize(executor.protocol());
        FetchExecutor existing = executors.putIfAbsent(protocol, executor);
        if (existing != null) {
            throw new IllegalStateException(
                    "Duplicate FetchExecutor registered for protocol: " + protocol
            );
        }
    }

    @Override
    public FetchExecutor get(FetchProtocol protocol) {
        FetchExecutor executor = executors.get(normalize(protocol));
        if (executor == null) {
            throw new IllegalStateException(
                    "No FetchExecutor registered for protocol: " + protocol.name()
            );
        }
        return executor;
    }

    @Override
    public boolean supports(FetchProtocol protocol) {
        return executors.containsKey(normalize(protocol));
    }

    @Override
    public FetchResult execute(FetchRequest request) {
        return get(request.protocol()).execute(request);
    }

    private String normalize(FetchProtocol protocol) {
        Objects.requireNonNull(protocol, "protocol");
        return protocol.name().toLowerCase(Locale.ROOT);
    }

}   // Class end.