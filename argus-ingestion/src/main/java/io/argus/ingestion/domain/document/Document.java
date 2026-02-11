package io.argus.ingestion.domain.document;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 15:40
 */
public final class Document {

    private final String id;
    private final String content;
    private final Map<String, Object> metadata;

    public Document(String id, String content, Map<String, Object> metadata) {
        this.id = id;
        this.content = content;
        this.metadata = metadata == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new HashMap<>(metadata));
    }

    public String id() {
        return id;
    }

    public String content() {
        return content;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

}   // Class end.