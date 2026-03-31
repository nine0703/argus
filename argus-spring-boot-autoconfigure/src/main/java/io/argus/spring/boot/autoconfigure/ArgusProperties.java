package io.argus.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized configuration entry for ARGUS Spring Boot integration.
 *
 * <p>
 * The initial property surface stays intentionally compact while still
 * exposing the minimum operational knobs required by the default ingestion
 * pipeline assembled by the starter.
 *
 * <p>
 * Setter methods fail fast on invalid values so misconfiguration is surfaced
 * during container bootstrap rather than during the first runtime request.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:08
 */
@ConfigurationProperties(prefix = "argus")
public class ArgusProperties {

    /**
     * Whether ARGUS auto-configuration should be activated.
     */
    private boolean enabled = true;
    /**
     * Ingestion-specific defaults exposed by the starter.
     */
    private final Ingestion ingestion = new Ingestion();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Ingestion getIngestion() {
        return ingestion;
    }

    /**
     * Property group for the default local ingestion pipeline.
     */
    public static class Ingestion {

        /**
         * Fixed-size chunk length used by the default chunk strategy.
         */
        private int chunkSize = 1000;
        /**
         * Embedding vector dimension used by the default deterministic model.
         */
        private int embeddingDimension = 16;
        /**
         * Namespace assigned to the default in-memory vector store.
         */
        private String vectorNamespace = "default";

        public int getChunkSize() {
            return chunkSize;
        }

        public void setChunkSize(int chunkSize) {
            if (chunkSize <= 0) {
                throw new IllegalArgumentException("argus.ingestion.chunk-size must be positive");
            }
            this.chunkSize = chunkSize;
        }

        public int getEmbeddingDimension() {
            return embeddingDimension;
        }

        public void setEmbeddingDimension(int embeddingDimension) {
            if (embeddingDimension <= 0) {
                throw new IllegalArgumentException("argus.ingestion.embedding-dimension must be positive");
            }
            this.embeddingDimension = embeddingDimension;
        }

        public String getVectorNamespace() {
            return vectorNamespace;
        }

        public void setVectorNamespace(String vectorNamespace) {
            if (vectorNamespace == null || vectorNamespace.trim().isEmpty()) {
                throw new IllegalArgumentException("argus.ingestion.vector-namespace must not be blank");
            }
            this.vectorNamespace = vectorNamespace.trim();
        }

    }

} // Class end.