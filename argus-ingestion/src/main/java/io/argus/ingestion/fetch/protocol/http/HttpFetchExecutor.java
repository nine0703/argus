package io.argus.ingestion.fetch.protocol.http;

import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.fetch.FetchResult;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:45
 */
public class HttpFetchExecutor implements FetchExecutor {

    private static final FetchProtocol HTTP = () -> "http";

    @Override
    public FetchProtocol protocol() {
        return HTTP;
    }

    @Override
    public FetchResult execute(FetchRequest request) {

        if (!(request instanceof HttpFetchRequest)) {
            throw new IllegalArgumentException(
                    "Expected HttpFetchRequest but got " + request.getClass()
            );
        }

        HttpFetchRequest httpRequest = (HttpFetchRequest) request;

        // TODO: 真正执行 HTTP 请求
        throw new UnsupportedOperationException("HTTP execution not implemented yet");
    }

}   // Class end.