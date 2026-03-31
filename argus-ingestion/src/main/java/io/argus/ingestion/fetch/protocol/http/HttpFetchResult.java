package io.argus.ingestion.fetch.protocol.http;

import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchResult;

import java.io.Serializable;
import java.util.*;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:36
 */
public final class HttpFetchResult implements FetchResult, Serializable {

    private static final long serialVersionUID = 1L;

    private static final FetchProtocol HTTP = () -> "http";

    private final int status;
    private final byte[] body;
    private final Map<String, List<String>> headers;

    public HttpFetchResult(
            int status,
            byte[] body,
            Map<String, List<String>> headers
    ) {
        this.status = status;
        this.body = body == null ? null : body.clone();
        this.headers = copyHeaders(headers);
    }

    @Override
    public FetchProtocol protocol() {
        return HTTP;
    }

    @Override
    public byte[] body() {
        return body == null ? null : body.clone();
    }

    @Override
    public Map<String, List<String>> metadata() {
        return headers;
    }

    @Override
    public boolean success() {
        return status >= 200 && status < 300;
    }

    public int status() {
        return status;
    }

    private Map<String, List<String>> copyHeaders(Map<String, List<String>> headers) {

        if (headers == null || headers.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> copy = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            List<String> values = entry.getValue() == null
                    ? Collections.emptyList()
                    : Collections.unmodifiableList(new ArrayList<>(entry.getValue()));
            copy.put(entry.getKey(), values);
        }
        return Collections.unmodifiableMap(copy);
    }

} // Class end.
