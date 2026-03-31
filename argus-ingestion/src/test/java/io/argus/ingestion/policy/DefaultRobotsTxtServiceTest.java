package io.argus.ingestion.policy;

import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.fetch.FetchResult;
import io.argus.ingestion.fetch.protocol.http.HttpFetchRequest;
import io.argus.ingestion.fetch.protocol.http.HttpFetchResult;
import io.argus.ingestion.fetch.protocol.http.HttpMethod;
import junit.framework.TestCase;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-03-31
 */
public class DefaultRobotsTxtServiceTest extends TestCase {

    public void testShouldAllowPathWhenRobotsContainsAllowRule() {

        StubFetchExecutor executor = new StubFetchExecutor();
        executor.register(
                "http://example.com/robots.txt",
                new HttpFetchResult(
                        200,
                        ("User-agent: argus\nAllow: /private/open\nDisallow: /private").getBytes(),
                        Collections.emptyMap()
                )
        );
        DefaultRobotsTxtService service = new DefaultRobotsTxtService(executor, Duration.ofMinutes(5));

        boolean allowed = service.isAllowed(
                new HttpFetchRequest(
                        URI.create("http://example.com/private/open/doc"),
                        HttpMethod.GET,
                        Collections.emptyMap(),
                        null
                ),
                RobotPolicy.strict("argus")
        );

        assertTrue(allowed);
        assertEquals(1, executor.count("http://example.com/robots.txt"));
    }

    public void testShouldRejectPathWhenRobotsDisallowRuleMatches() {

        StubFetchExecutor executor = new StubFetchExecutor();
        executor.register(
                "http://example.com/robots.txt",
                new HttpFetchResult(
                        200,
                        ("User-agent: *\nDisallow: /private").getBytes(),
                        Collections.emptyMap()
                )
        );
        DefaultRobotsTxtService service = new DefaultRobotsTxtService(executor, Duration.ofMinutes(5));

        boolean allowed = service.isAllowed(
                new HttpFetchRequest(
                        URI.create("http://example.com/private/doc"),
                        HttpMethod.GET,
                        Collections.emptyMap(),
                        null
                ),
                RobotPolicy.strict("argus")
        );

        assertFalse(allowed);
    }

    public void testShouldTreatNotFoundRobotsAsAllowAll() {

        StubFetchExecutor executor = new StubFetchExecutor();
        executor.register(
                "http://example.com/robots.txt",
                new HttpFetchResult(404, new byte[0], Collections.emptyMap())
        );
        DefaultRobotsTxtService service = new DefaultRobotsTxtService(executor, Duration.ofMinutes(5));

        boolean allowed = service.isAllowed(
                new HttpFetchRequest(
                        URI.create("http://example.com/any/path"),
                        HttpMethod.GET,
                        Collections.emptyMap(),
                        null
                ),
                RobotPolicy.strict("argus")
        );

        assertTrue(allowed);
    }

    private static final class StubFetchExecutor implements FetchExecutor {

        private final Map<String, FetchResult> results = new HashMap<>();
        private final Map<String, Integer> counts = new HashMap<>();

        @Override
        public FetchProtocol protocol() {
            return () -> "http";
        }

        @Override
        public FetchResult execute(FetchRequest request) {
            String key = request.resource().toString();
            counts.put(key, counts.getOrDefault(key, 0) + 1);
            FetchResult result = results.get(key);
            if (result == null) {
                throw new AssertionError("No stubbed fetch result for " + key);
            }
            return result;
        }

        private void register(String resource, FetchResult result) {
            results.put(resource, result);
        }

        private int count(String resource) {
            return counts.getOrDefault(resource, 0);
        }
    }

} // Class end.