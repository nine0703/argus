package io.argus.ingestion.fetch.protocol.http;

import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchRequest;

import java.io.Serializable;
import java.net.URI;
import java.util.*;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:35
 */
public class HttpFetchRequest implements FetchRequest, Serializable {

    private static final long serialVersionUID = 1L;

    private static final FetchProtocol HTTP = () -> "http";

    private final URI uri;
    private final HttpMethod method;
    private final Map<String, List<String>> headers;
    private final byte[] body;

    public HttpFetchRequest(
            URI uri,
            HttpMethod method,
            Map<String, List<String>> headers,
            byte[] body
    ) {
        this.uri = uri;
        this.method = method;
        this.headers = copyHeaders(headers);
        this.body = body == null ? null : body.clone();
    }

    @Override
    public URI resource() {
        return uri;
    }

    @Override
    public FetchProtocol protocol() {
        return HTTP;
    }

    @Override
    public Map<String, List<String>> metadata() {
        return headers;
    }

    public HttpMethod method() {
        return method;
    }

    public byte[] bodyPayload() {
        return body == null ? null : body.clone();
    }

    private Map<String, List<String>> copyHeaders(Map<String, List<String>> headers) {

        if (headers == null || headers.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> copy = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            List<String> values = entry.getValue() == null
                    ? Collections.emptyList()
                    : Collections.unmodifiableList(new ArrayList<>(entry.getValue()));
            copy.put(entry.getKey(), values);
        }
        return Collections.unmodifiableMap(copy);
    }

} // Class end.
