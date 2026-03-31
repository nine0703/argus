package io.argus.ingestion.domain.chunk;

import io.argus.ingestion.domain.document.Document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Splits a document into deterministic fixed-size text chunks.
 *
 * <p>
 * Chunk boundaries are calculated using character count only,
 * making the strategy simple, stable, and replay-friendly.
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:40
 */
public class FixedSizeChunkStrategy implements ChunkStrategy {

    private final int chunkSize;

    public FixedSizeChunkStrategy(int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be positive");
        }
        this.chunkSize = chunkSize;
    }

    @Override
    public List<TextChunk> chunk(Document document) {

        Objects.requireNonNull(document, "document");
        List<TextChunk> chunks = new ArrayList<>();
        String content = document.content() == null ? "" : document.content();

        if (content.isEmpty()) {
            chunks.add(new TextChunk(document.id(), 0, "", baseMetadata(document, 0)));
            return chunks;
        }

        int index = 0;
        for (int start = 0; start < content.length(); start += chunkSize) {
            int end = Math.min(start + chunkSize, content.length());
            String chunkContent = content.substring(start, end);
            chunks.add(
                    new TextChunk(
                            document.id(),
                            index,
                            chunkContent,
                            baseMetadata(document, index)
                    )
            );
            index++;
        }

        return chunks;
    }

    @Override
    public String name() {
        return "fixed-size-chunk-strategy";
    }

    public int chunkSize() {
        return chunkSize;
    }

    private Map<String, Object> baseMetadata(Document document, int index) {

        Map<String, Object> metadata = new LinkedHashMap<>(document.metadata());
        metadata.put("chunkStrategy", name());
        metadata.put("chunkIndex", index);
        return metadata;
    }

} // Class end.
