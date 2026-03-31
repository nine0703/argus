package io.argus.ingestion.domain.embedding;

import io.argus.ingestion.domain.chunk.TextChunk;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic local embedding model based on SHA-256 hashing.
 *
 * <p>
 * This implementation is not intended for semantic quality.
 * It exists as a stable default embedding baseline for local testing,
 * starter bootstrapping, and deterministic replay-friendly execution.
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:41
 */
public class HashEmbeddingModel implements EmbeddingModel {

    private static final int DEFAULT_DIMENSION = 16;

    private final int dimension;

    public HashEmbeddingModel() {
        this(DEFAULT_DIMENSION);
    }

    public HashEmbeddingModel(int dimension) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("dimension must be positive");
        }
        this.dimension = dimension;
    }

    @Override
    public List<EmbeddingVector> embed(List<TextChunk> chunks) {

        List<EmbeddingVector> vectors = new ArrayList<>();
        for (TextChunk chunk : chunks) {
            vectors.add(embedChunk(chunk));
        }
        return vectors;
    }

    @Override
    public String modelName() {
        return "hash-embedding-model";
    }

    @Override
    public int dimension() {
        return dimension;
    }

    private EmbeddingVector embedChunk(TextChunk chunk) {

        byte[] digest = digest(chunk.content());
        List<Float> values = new ArrayList<>(dimension);

        for (int i = 0; i < dimension; i++) {
            int digestIndex = i % digest.length;
            float normalized = (digest[digestIndex] & 0xff) / 255.0f;
            values.add(normalized);
        }

        return new EmbeddingVector(chunk.documentId() + "#" + chunk.index(), values);
    }

    private byte[] digest(String content) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest((content == null ? "" : content).getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

} // Class end.
