package io.argus.ingestion.fetch;

import java.util.Locale;
import java.util.Objects;

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

    /**
     * Whether this executor can handle the given protocol.
     *
     * <p>
     * Most executors support a single protocol and can use the default
     * implementation. Composite executors may override this method.
     */
    default boolean supports(FetchProtocol protocol) {
        Objects.requireNonNull(protocol, "protocol");
        return protocol().name().toLowerCase(Locale.ROOT)
                .equals(protocol.name().toLowerCase(Locale.ROOT));
    }

    FetchResult execute(FetchRequest request);

}