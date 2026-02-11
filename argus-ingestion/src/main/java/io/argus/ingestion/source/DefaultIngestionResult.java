package io.argus.ingestion.source;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
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
        this.requestId = requestId;
        this.timestamp = timestamp;
        this.success = success;
        this.metadata = metadata == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new HashMap<>(metadata));
    }

    public String requestId() {
        return requestId;
    }

    public Instant timestamp() {
        return timestamp;
    }

    public boolean success() {
        return success;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

}   // Class end.