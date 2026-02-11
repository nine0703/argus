package io.argus.ingestion.parse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable parse input.
 * <p>
 * Represents raw content fetched from external source.
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:59
 */
public final class ParseInput {

    private final byte[] content;
    private final ContentType contentType;
    private final Map<String, Object> metadata;

    public ParseInput(
            byte[] content,
            ContentType contentType,
            Map<String, List<String>> metadata
    ) {
        this.content = content;
        this.contentType = contentType;
        this.metadata = metadata == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new HashMap<>(metadata));
    }

    public byte[] content() {
        return content;
    }

    public ContentType contentType() {
        return contentType;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

} // Class end.