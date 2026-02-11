package io.argus.ingestion.domain.chunk;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 15:41
 */
public final class TextChunk {

    private final String documentId;
    private final int index;
    private final String content;
    private final Map<String, Object> metadata;

    public TextChunk(
            String documentId,
            int index,
            String content,
            Map<String, Object> metadata
    ) {
        this.documentId = documentId;
        this.index = index;
        this.content = content;
        this.metadata = metadata == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new HashMap<>(metadata));
    }

    public String documentId() {
        return documentId;
    }

    public int index() {
        return index;
    }

    public String content() {
        return content;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

}   // Class end.