package io.argus.ingestion.source;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable default implementation of {@link IngestionResult}.
 *
 * @author TK.ENDO
 * @since 2026-02-11 周三 15:59
 */
public final class DefaultIngestionResult implements IngestionResult {

    private final String requestId;
    private final Instant timestamp;
    private final boolean success;
    private final Map<String, Object> metadata;

    public DefaultIngestionResult(
            String requestId,
            Instant timestamp,
            boolean success,
            Map<String, Object> metadata
    ) {
        this.requestId = Objects.requireNonNull(requestId, "requestId");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.success = success;
        this.metadata = metadata == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new HashMap<>(metadata));
    }

    @Override
    public String requestId() {
        return requestId;
    }

    @Override
    public Instant timestamp() {
        return timestamp;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public Map<String, Object> metadata() {
        return metadata;
    }

} // Class end.