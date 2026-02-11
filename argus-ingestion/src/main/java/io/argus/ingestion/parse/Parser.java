package io.argus.ingestion.parse;

/**
 * Transforms raw parse input into structured Document.
 *
 * <p>
 * Parser must be:
 * - deterministic
 * - side-effect free
 * - independent from embedding or vector storage
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:59
 */
public interface Parser {

    ParseResult parse(ParseInput input);

    /**
     * Parser name (for audit & replay).
     */
    String name();

} // Class end.