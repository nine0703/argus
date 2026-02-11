package io.argus.ingestion.fetch.protocol.http;

import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:36
 */
public final class HttpFetchResult implements FetchResult {

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
        this.body = body;
        this.headers = headers == null ? Collections.emptyMap() : headers;
    }

    @Override
    public FetchProtocol protocol() {
        return HTTP;
    }

    @Override
    public byte[] body() {
        return body;
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

} // Class end.