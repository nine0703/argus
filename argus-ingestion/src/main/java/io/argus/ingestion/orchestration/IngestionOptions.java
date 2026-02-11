package io.argus.ingestion.orchestration;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 16:02
 */
public final class IngestionOptions {

    private final boolean enableChunking;
    private final boolean enableEmbedding;
    private final boolean enableVectorStore;
    private final String namespace;

    public IngestionOptions(
            boolean enableChunking,
            boolean enableEmbedding,
            boolean enableVectorStore,
            String namespace
    ) {
        this.enableChunking = enableChunking;
        this.enableEmbedding = enableEmbedding;
        this.enableVectorStore = enableVectorStore;
        this.namespace = namespace;
    }

    public boolean enableChunking() {
        return enableChunking;
    }

    public boolean enableEmbedding() {
        return enableEmbedding;
    }

    public boolean enableVectorStore() {
        return enableVectorStore;
    }

    public String namespace() {
        return namespace;
    }

    public static IngestionOptions defaultOptions() {
        return new IngestionOptions(
                true,
                true,
                true,
                "default"
        );
    }

}   // Class end.