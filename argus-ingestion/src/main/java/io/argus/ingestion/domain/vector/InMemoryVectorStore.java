package io.argus.ingestion.domain.vector;

import io.argus.ingestion.domain.embedding.EmbeddingVector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory vector store implementation.
 *
 * <p>
 * This implementation is intended for local bootstrapping, tests,
 * and starter-level default execution rather than production-scale search.
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:41
 */
public class InMemoryVectorStore implements VectorStore {

    private final Map<String, VectorRecord> records = new ConcurrentHashMap<>();
    private final String namespace;

    public InMemoryVectorStore() {
        this("default");
    }

    public InMemoryVectorStore(String namespace) {
        this.namespace = Objects.requireNonNull(namespace, "namespace");
    }

    @Override
    public void upsert(List<VectorRecord> records) {
        for (VectorRecord record : records) {
            this.records.put(record.id(), record);
        }
    }

    @Override
    public void delete(List<String> ids) {
        for (String id : ids) {
            records.remove(id);
        }
    }

    @Override
    public List<VectorRecord> search(EmbeddingVector queryVector, int topK) {

        List<Map.Entry<VectorRecord, Float>> scored = new ArrayList<>();
        for (VectorRecord record : records.values()) {
            scored.add(Map.entry(record, cosineSimilarity(queryVector, record.vector())));
        }

        scored.sort(Comparator.comparing(Map.Entry<VectorRecord, Float>::getValue).reversed());

        List<VectorRecord> result = new ArrayList<>();
        for (int i = 0; i < Math.min(topK, scored.size()); i++) {
            result.add(scored.get(i).getKey());
        }

        return result;
    }

    @Override
    public String namespace() {
        return namespace;
    }

    public Map<String, VectorRecord> snapshot() {
        return new LinkedHashMap<>(records);
    }

    private float cosineSimilarity(EmbeddingVector left, EmbeddingVector right) {

        int dimension = Math.min(left.dimension(), right.dimension());
        if (dimension == 0) {
            return 0.0f;
        }

        float dot = 0.0f;
        float leftNorm = 0.0f;
        float rightNorm = 0.0f;

        for (int i = 0; i < dimension; i++) {
            float l = left.values().get(i);
            float r = right.values().get(i);
            dot += l * r;
            leftNorm += l * l;
            rightNorm += r * r;
        }

        if (leftNorm == 0.0f || rightNorm == 0.0f) {
            return 0.0f;
        }

        return (float) (dot / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm)));
    }

} // Class end.
