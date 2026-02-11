package io.argus.ingestion.fetch.protocol.http;

import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchRequest;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:35
 */
public class HttpFetchRequest implements FetchRequest {

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
        this.headers = headers == null ? Collections.emptyMap() : headers;
        this.body = body;
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
        return body;
    }

} // Class end.