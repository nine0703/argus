package io.argus.ingestion.fetch;

import junit.framework.TestCase;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 21:06
 */
public class RegistryBackedFetchExecutorTest extends TestCase {

    public void testShouldDelegateProtocolSupportAndExecution() {

        DefaultFetchExecutorRegistry registry = new DefaultFetchExecutorRegistry();
        registry.register(new StubFetchExecutor());
        RegistryBackedFetchExecutor executor = new RegistryBackedFetchExecutor(registry);

        FetchResult result = executor.execute(new StubFetchRequest());

        assertEquals("registry", executor.protocol().name());
        assertTrue(executor.supports(() -> "http"));
        assertTrue(result.success());
        assertEquals("http", result.protocol().name());
    }

    private static final class StubFetchExecutor implements FetchExecutor {

        @Override
        public FetchProtocol protocol() {
            return () -> "http";
        }

        @Override
        public FetchResult execute(FetchRequest request) {
            return new FetchResult() {
                @Override
                public FetchProtocol protocol() {
                    return () -> "http";
                }

                @Override
                public byte[] body() {
                    return "ok".getBytes();
                }

                @Override
                public Map<String, List<String>> metadata() {
                    return Collections.emptyMap();
                }

                @Override
                public boolean success() {
                    return true;
                }
            };
        }
    }

    private static final class StubFetchRequest implements FetchRequest {

        @Override
        public URI resource() {
            return URI.create("http://example.com");
        }

        @Override
        public FetchProtocol protocol() {
            return () -> "http";
        }

        @Override
        public Map<String, List<String>> metadata() {
            return Collections.emptyMap();
        }
    }
}