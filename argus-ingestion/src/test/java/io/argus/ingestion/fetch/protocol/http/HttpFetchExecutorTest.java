package io.argus.ingestion.fetch.protocol.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 11:41
 */
public class HttpFetchExecutorTest extends TestCase {

    private HttpServer server;

    @Override
    protected void setUp() throws Exception {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/echo", this::handleEcho);
        server.start();
    }

    @Override
    protected void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    public void testShouldExecuteHttpRequest() throws Exception {

        HttpFetchExecutor executor = new HttpFetchExecutor();
        URI uri = new URI("http://localhost:" + server.getAddress().getPort() + "/echo");

        HttpFetchResult result =
                (HttpFetchResult) executor.execute(
                        new HttpFetchRequest(
                                uri,
                                HttpMethod.POST,
                                Collections.singletonMap("X-Test", Collections.singletonList("v1")),
                                "ping".getBytes(StandardCharsets.UTF_8)
                        )
                );

        assertTrue(result.success());
        assertEquals(200, result.status());
        assertEquals("ok:ping", new String(result.body(), StandardCharsets.UTF_8));
        assertEquals(
                "text/plain; charset=utf-8",
                result.metadata().get("Content-type").get(0)
        );
    }

    public void testShouldRejectHeadRequestWithBody() throws Exception {

        HttpFetchExecutor executor = new HttpFetchExecutor();
        URI uri = new URI("http://localhost:" + server.getAddress().getPort() + "/echo");

        try {
            executor.execute(
                    new HttpFetchRequest(
                            uri,
                            HttpMethod.HEAD,
                            Collections.emptyMap(),
                            "body".getBytes(StandardCharsets.UTF_8)
                    )
            );
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("HEAD request must not contain body", expected.getMessage());
        }
    }

    private void handleEcho(HttpExchange exchange) throws IOException {

        byte[] requestBody = readAllBytes(exchange.getRequestBody());
        byte[] responseBody =
                ("ok:" + new String(requestBody, StandardCharsets.UTF_8))
                        .getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().put(
                "Content-Type",
                Collections.singletonList("text/plain; charset=utf-8")
        );
        exchange.sendResponseHeaders(200, responseBody.length);
        exchange.getResponseBody().write(responseBody);
        exchange.close();
    }

    private byte[] readAllBytes(InputStream input) throws IOException {

        byte[] buffer = new byte[4096];
        int read;
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();

        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }

        return output.toByteArray();
    }

} // Class end.
