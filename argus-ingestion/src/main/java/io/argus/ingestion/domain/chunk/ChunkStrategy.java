package io.argus.ingestion.domain.chunk;

import io.argus.ingestion.domain.document.Document;

import java.util.List;

/**
 * Strategy for splitting a Document into TextChunks.
 *
 * <p>
 * Implementations must:
 * - be deterministic
 * - not mutate input Document
 * - not depend on embedding or vector store
 *
 * <p>
 * Chunking is a pure transformation step in ingestion pipeline.
 * @author TK.ENDO
 * @since 2026-02-11 周三 15:44
 */
public interface ChunkStrategy {

    /**
     * Split a document into ordered chunks.
     * @param document source document
     * @return ordered list of chunks
     */
    List<TextChunk> chunk(Document document);

    /**
     * Strategy name (for audit & replay).
     */
    String name();

}   // Class end.