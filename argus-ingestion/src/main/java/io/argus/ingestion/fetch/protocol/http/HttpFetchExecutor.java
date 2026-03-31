package io.argus.ingestion.fetch.protocol.http;

import io.argus.ingestion.error.FetchFailedException;
import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.fetch.FetchResult;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:45
 */
public class HttpFetchExecutor implements FetchExecutor {

    private static final FetchProtocol HTTP = () -> "http";
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);
    private static final HttpClient CLIENT =
            HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .build();

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

        try {
            HttpRequest outboundRequest = toOutboundRequest(httpRequest);

            HttpResponse<byte[]> response =
                    CLIENT.send(outboundRequest, HttpResponse.BodyHandlers.ofByteArray());

            return new HttpFetchResult(
                    response.statusCode(),
                    response.body(),
                    response.headers().map()
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FetchFailedException(
                    "HTTP fetch interrupted for " + httpRequest.resource(),
                    e
            );
        } catch (IOException e) {
            throw new FetchFailedException(
                    "HTTP fetch failed for " + httpRequest.resource(),
                    e
            );
        }
    }

    private HttpRequest toOutboundRequest(HttpFetchRequest request) {

        HttpMethod method = request.method() == null ? HttpMethod.GET : request.method();
        HttpRequest.Builder builder =
                HttpRequest.newBuilder(request.resource())
                        .timeout(REQUEST_TIMEOUT);

        applyHeaders(builder, request.metadata());
        builder.method(method.name(), bodyPublisher(method, request.bodyPayload()));

        return builder.build();
    }

    private void applyHeaders(
            HttpRequest.Builder builder,
            Map<String, List<String>> headers
    ) {

        if (headers == null || headers.isEmpty()) {
            return;
        }

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            List<String> values = entry.getValue();
            if (values == null || values.isEmpty()) {
                continue;
            }

            for (String value : values) {
                if (value != null) {
                    builder.header(entry.getKey(), value);
                }
            }
        }
    }

    private HttpRequest.BodyPublisher bodyPublisher(HttpMethod method, byte[] body) {

        if (body == null || body.length == 0) {
            return HttpRequest.BodyPublishers.noBody();
        }

        if (method == HttpMethod.HEAD) {
            throw new IllegalArgumentException("HEAD request must not contain body");
        }

        return HttpRequest.BodyPublishers.ofByteArray(body);
    }

}   // Class end.
