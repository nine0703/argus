package io.argus.ingestion.domain.embedding;

import io.argus.ingestion.domain.chunk.TextChunk;

import java.util.List;

/**
 * Abstraction of embedding model.
 *
 * <p>
 * Implementations may call:
 * - remote LLM APIs
 * - local model runtimes
 * - GPU inference services
 *
 * <p>
 * Must be:
 * - deterministic under same model version
 * - side-effect controlled
 * @author TK.ENDO
 * @since 2026-02-11 周三 15:45
 */
public interface EmbeddingModel {

    /**
     * Embed multiple chunks.
     * @param chunks input chunks
     * @return embedding vectors aligned with input order
     */
    List<EmbeddingVector> embed(List<TextChunk> chunks);

    /**
     * Convenience method for single chunk.
     */
    default EmbeddingVector embed(TextChunk chunk) {
        return embed(List.of(chunk)).get(0);
    }

    /**
     * Model identifier (e.g. "text-embedding-3-small").
     */
    String modelName();

    /**
     * Vector dimension.
     */
    int dimension();

}
