package io.argus.ingestion.parse;

import io.argus.ingestion.domain.document.Document;

/**
 * Result of parsing operation.
 * <p>
 * Contains structured document extracted from raw content.
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:59
 */
public final class ParseResult {

    private final Document document;

    public ParseResult(Document document) {
        this.document = document;
    }

    public Document document() {
        return document;
    }

} // Class end.