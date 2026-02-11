package io.argus.ingestion.domain.vector;

import io.argus.ingestion.domain.embedding.EmbeddingVector;

import java.util.List;

/**
 * Abstraction of vector storage backend.
 *
 * <p>
 * Supports:
 * - insert / upsert
 * - delete
 * - similarity search
 *
 * <p>
 * Implementations:
 * - Qdrant
 * - Milvus
 * - PGVector
 * - In-memory store
 * @author TK.ENDO
 * @since 2026-02-11 周三 15:46
 */
public interface VectorStore {

    /**
     * Store or update vectors.
     */
    void upsert(List<VectorRecord> records);

    /**
     * Delete by id.
     */
    void delete(List<String> ids);

    /**
     * Search similar vectors.
     * @param queryVector embedding vector
     * @param topK        number of results
     */
    List<VectorRecord> search(EmbeddingVector queryVector, int topK);

    /**
     * Optional namespace support.
     */
    default String namespace() {
        return "default";
    }

}
