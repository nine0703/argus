package io.argus.ingestion.domain.embedding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 15:42
 */
public final class EmbeddingVector {

    private final String chunkId;
    private final List<Float> values;

    public EmbeddingVector(String chunkId, List<Float> values) {
        this.chunkId = chunkId;
        this.values = values == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(values));
    }

    public String chunkId() {
        return chunkId;
    }

    public List<Float> values() {
        return values;
    }

    public int dimension() {
        return values.size();
    }

}   // Class end.