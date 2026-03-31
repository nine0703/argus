package io.argus.ingestion.fetch.replay;

import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.fetch.protocol.ftp.FtpFetchRequest;
import io.argus.ingestion.fetch.protocol.http.HttpFetchRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Builds deterministic replay keys for {@link FetchRequest}.
 *
 * <p>
 * Replay storage must not rely on object identity or default hashCode
 * implementations because request instances are recreated across executions.
 * This utility derives a semantic fingerprint from request content instead.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 21:04
 */
public final class FetchRequestFingerprint {

    private FetchRequestFingerprint() {
    }

    public static String create(FetchRequest request) {

        Objects.requireNonNull(request, "request");
        StringBuilder builder = new StringBuilder();
        builder.append("protocol=").append(request.protocol().name()).append(';');
        builder.append("resource=").append(request.resource()).append(';');
        appendMetadata(builder, request.metadata());
        appendRequestSpecificParts(builder, request);
        return digest(builder.toString());
    }

    private static void appendMetadata(StringBuilder builder, Map<String, List<String>> metadata) {

        if (metadata == null || metadata.isEmpty()) {
            builder.append("metadata=[];");
            return;
        }

        List<String> entries = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : metadata.entrySet()) {
            List<String> values = entry.getValue() == null ? List.of() : new ArrayList<>(entry.getValue());
            values.sort(String::compareTo);
            entries.add(entry.getKey().toLowerCase() + "=" + String.join(",", values));
        }
        entries.sort(String::compareTo);
        builder.append("metadata=").append(entries).append(';');
    }

    private static void appendRequestSpecificParts(StringBuilder builder, FetchRequest request) {

        if (request instanceof HttpFetchRequest) {
            HttpFetchRequest http = (HttpFetchRequest) request;
            builder.append("httpMethod=").append(http.method()).append(';');
            builder.append("httpBody=").append(digest(http.bodyPayload() == null ? new byte[0] : http.bodyPayload())).append(';');
            return;
        }

        if (request instanceof FtpFetchRequest) {
            FtpFetchRequest ftp = (FtpFetchRequest) request;
            builder.append("ftpUser=").append(ftp.username()).append(';');
            builder.append("ftpMode=").append(ftp.transferMode()).append(';');
            return;
        }

        builder.append("requestType=").append(request.getClass().getName()).append(';');
    }

    private static String digest(String value) {
        return digest(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String digest(byte[] bytes) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(bytes);
            StringBuilder builder = new StringBuilder();
            for (byte hashedByte : hashed) {
                builder.append(String.format("%02x", hashedByte));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

} // Class end.