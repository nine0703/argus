package io.argus.ingestion.fetch;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 21:05
 */
public class DefaultFetchExecutorRegistryTest extends TestCase {

    public void testShouldResolveExecutorCaseInsensitively() {

        DefaultFetchExecutorRegistry registry = new DefaultFetchExecutorRegistry();
        FetchExecutor executor = new StubFetchExecutor(() -> "HTTP");
        registry.register(executor);

        assertTrue(registry.supports(() -> "http"));
        assertSame(executor, registry.get(() -> "http"));
        assertSame(executor, registry.get(() -> "HTTP"));
    }

    public void testShouldRejectDuplicateProtocolRegistration() {

        DefaultFetchExecutorRegistry registry = new DefaultFetchExecutorRegistry();
        registry.register(new StubFetchExecutor(() -> "http"));

        try {
            registry.register(new StubFetchExecutor(() -> "HTTP"));
            fail("Expected duplicate registration failure");
        } catch (IllegalStateException expected) {
            assertEquals("Duplicate FetchExecutor registered for protocol: http", expected.getMessage());
        }
    }

    private static final class StubFetchExecutor implements FetchExecutor {

        private final FetchProtocol protocol;

        private StubFetchExecutor(FetchProtocol protocol) {
            this.protocol = protocol;
        }

        @Override
        public FetchProtocol protocol() {
            return protocol;
        }

        @Override
        public FetchResult execute(FetchRequest request) {
            return new FetchResult() {
                @Override
                public FetchProtocol protocol() {
                    return protocol;
                }

                @Override
                public byte[] body() {
                    return new byte[0];
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
}