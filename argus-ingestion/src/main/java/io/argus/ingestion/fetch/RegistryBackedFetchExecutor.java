package io.argus.ingestion.fetch;

import java.util.Objects;

/**
 * Adapter that exposes a {@link FetchExecutorRegistry} as a {@link FetchExecutor}.
 *
 * <p>
 * This is useful when higher-level orchestration depends on the single
 * {@code FetchExecutor} abstraction but runtime wiring prefers registry-based
 * protocol dispatch.
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:41
 */
public class RegistryBackedFetchExecutor implements FetchExecutor {

    private static final FetchProtocol REGISTRY = () -> "registry";

    private final FetchExecutorRegistry registry;

    public RegistryBackedFetchExecutor(FetchExecutorRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    @Override
    public FetchProtocol protocol() {
        return REGISTRY;
    }

    @Override
    public boolean supports(FetchProtocol protocol) {
        return registry.supports(protocol);
    }

    @Override
    public FetchResult execute(FetchRequest request) {
        return registry.execute(request);
    }

} // Class end.