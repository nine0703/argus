package io.argus.ingestion.parse;

import io.argus.ingestion.domain.document.Document;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default deterministic parser for plain text-like content.
 *
 * <p>
 * This parser intentionally keeps semantics conservative:
 * <ul>
 *   <li>TEXT and UNKNOWN content are decoded as UTF-8 text</li>
 *   <li>HTML content is converted to text by stripping tags</li>
 *   <li>JSON content is preserved as normalized UTF-8 text</li>
 * </ul>
 *
 * <p>
 * It is designed to provide a minimal ingestion baseline rather than
 * a full fidelity HTML or structured data parser.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:40
 */
public class SimpleDocumentParser implements Parser {

    @Override
    public ParseResult parse(ParseInput input) {

        String content = decode(input);
        Map<String, Object> metadata = new LinkedHashMap<>(input.metadata());
        metadata.put("parser", name());
        metadata.put("contentType", input.contentType().name());

        return new ParseResult(
                new Document(
                        resolveDocumentId(metadata, content),
                        content,
                        metadata
                )
        );
    }

    @Override
    public String name() {
        return "simple-document-parser";
    }

    private String decode(ParseInput input) {

        byte[] raw = input.content();
        if (raw == null || raw.length == 0) {
            return "";
        }

        String text = new String(raw, StandardCharsets.UTF_8);
        if (input.contentType() == ContentType.HTML) {
            return stripHtml(text);
        }

        return text;
    }

    private String stripHtml(String text) {

        String normalized = text.replaceAll("(?is)<script.*?>.*?</script>", " ");
        normalized = normalized.replaceAll("(?is)<style.*?>.*?</style>", " ");
        normalized = normalized.replaceAll("(?s)<[^>]+>", " ");
        normalized = normalized.replace("&nbsp;", " ");
        normalized = normalized.replaceAll("\\s+", " ").trim();
        return normalized;
    }

    private String resolveDocumentId(Map<String, Object> metadata, String content) {
        Object resource = metadata.get("resource");
        return resource == null ? digest(content) : resource.toString();
    }

    private String digest(String content) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder("doc-");
            for (int i = 0; i < 12 && i < bytes.length; i++) {
                builder.append(String.format("%02x", bytes[i]));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

} // Class end.