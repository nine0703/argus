package io.argus.ingestion.domain.vector;

import io.argus.ingestion.domain.embedding.EmbeddingVector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 15:42
 */
public final class VectorRecord {

    private final String id;
    private final EmbeddingVector vector;
    private final Map<String, Object> metadata;

    public VectorRecord(
            String id,
            EmbeddingVector vector,
            Map<String, Object> metadata
    ) {
        this.id = id;
        this.vector = vector;
        this.metadata = metadata == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new HashMap<>(metadata));
    }

    public String id() {
        return id;
    }

    public EmbeddingVector vector() {
        return vector;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

}   // Class end.