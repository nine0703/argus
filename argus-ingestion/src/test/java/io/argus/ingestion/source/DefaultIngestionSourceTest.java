package io.argus.ingestion.source;

import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.fetch.protocol.http.HttpFetchRequest;
import io.argus.ingestion.fetch.protocol.http.HttpMethod;
import io.argus.ingestion.fetch.replay.FetchReplayMode;
import io.argus.ingestion.orchestration.IngestionOptions;
import io.argus.ingestion.orchestration.IngestionOrchestrator;
import io.argus.ingestion.policy.FetchPolicy;
import io.argus.ingestion.policy.RateLimitPolicy;
import io.argus.ingestion.policy.RobotPolicy;
import io.argus.ingestion.policy.RobotsTxtService;
import junit.framework.TestCase;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;

/**
 * @author TK.ENDO
 * @since 2026-03-31
 */
public class DefaultIngestionSourceTest extends TestCase {

    public void testShouldReturnDryRunValidationWithoutCallingOrchestrator() {

        StubIngestionOrchestrator orchestrator = new StubIngestionOrchestrator();
        DefaultIngestionSource source = new DefaultIngestionSource(
                orchestrator,
                FetchPolicy.unrestricted(),
                FetchReplayMode.HYBRID
        );

        IngestionResult result = source.ingest(request(FetchPolicy.unrestricted()), IngestionMode.DRY_RUN);

        assertTrue(result.success());
        assertEquals("DRY_RUN", result.metadata().get("mode"));
        assertEquals(Boolean.TRUE, result.metadata().get("validated"));
        assertEquals(Boolean.FALSE, result.metadata().get("robotsChecked"));
        assertEquals(0, orchestrator.invocationCount);
    }

    public void testShouldRejectDisallowedProtocolBeforeDelegating() {

        StubIngestionOrchestrator orchestrator = new StubIngestionOrchestrator();
        DefaultIngestionSource source = new DefaultIngestionSource(
                orchestrator,
                FetchPolicy.unrestricted(),
                FetchReplayMode.HYBRID
        );
        FetchPolicy policy = new FetchPolicy(
                Set.of("ftp"),
                RateLimitPolicy.noLimit(),
                RobotPolicy.strict("argus-bot")
        );

        try {
            source.ingest(request(policy), IngestionMode.LIVE);
            fail("Expected protocol validation to fail");
        } catch (IllegalStateException expected) {
            assertEquals("Fetch policy does not allow protocol: http", expected.getMessage());
        }

        assertEquals(0, orchestrator.invocationCount);
    }

    public void testShouldRejectReplayModeWhenRuntimeIsConfiguredLiveOnly() {

        DefaultIngestionSource source = new DefaultIngestionSource(
                new StubIngestionOrchestrator(),
                FetchPolicy.unrestricted(),
                FetchReplayMode.LIVE
        );

        try {
            source.ingest(request(FetchPolicy.unrestricted()), IngestionMode.REPLAY);
            fail("Expected replay compatibility validation to fail");
        } catch (IllegalStateException expected) {
            assertEquals(
                    "IngestionSource cannot execute REPLAY mode when fetch replay mode is LIVE",
                    expected.getMessage()
            );
        }
    }

    public void testShouldApplyRateLimitForLiveAccess() {

        DefaultIngestionSource source = new DefaultIngestionSource(
                new StubIngestionOrchestrator(),
                FetchPolicy.unrestricted(),
                FetchReplayMode.HYBRID
        );
        FetchPolicy policy = new FetchPolicy(
                Collections.singleton("http"),
                new RateLimitPolicy(Duration.ofMinutes(1)),
                RobotPolicy.permissive("argus-test")
        );

        source.ingest(request(policy), IngestionMode.LIVE);

        try {
            source.ingest(request(policy), IngestionMode.LIVE);
            fail("Expected rate limit validation to fail");
        } catch (IllegalStateException expected) {
            assertEquals(
                    "Fetch policy rate limit violated for resource: http://example.com/source",
                    expected.getMessage()
            );
        }
    }

    public void testShouldRejectLiveAccessWhenRobotsDisallow() {

        DefaultIngestionSource source = new DefaultIngestionSource(
                new StubIngestionOrchestrator(),
                FetchPolicy.unrestricted(),
                FetchReplayMode.HYBRID,
                new DenyAllRobotsTxtService()
        );
        FetchPolicy policy = new FetchPolicy(
                Collections.singleton("http"),
                RateLimitPolicy.noLimit(),
                RobotPolicy.strict("argus-test")
        );

        try {
            source.ingest(request(policy), IngestionMode.LIVE);
            fail("Expected robots validation to fail");
        } catch (IllegalStateException expected) {
            assertEquals(
                    "robots.txt disallows resource: http://example.com/source",
                    expected.getMessage()
            );
        }
    }

    private DefaultIngestionRequest request(FetchPolicy policy) {
        return new DefaultIngestionRequest(
                "req-1",
                new HttpFetchRequest(
                        URI.create("http://example.com/source"),
                        HttpMethod.GET,
                        Collections.emptyMap(),
                        null
                ),
                new IngestionOptions(true, true, true, "docs"),
                policy
        );
    }

    private static final class StubIngestionOrchestrator implements IngestionOrchestrator {

        private int invocationCount;

        @Override
        public IngestionResult ingest(io.argus.ingestion.orchestration.IngestionCommand command) {
            invocationCount++;
            return new DefaultIngestionResult(
                    command.id(),
                    Instant.parse("2026-03-31T12:00:00Z"),
                    true,
                    Collections.singletonMap("delegate", "called")
            );
        }
    }

    private static final class DenyAllRobotsTxtService implements RobotsTxtService {

        @Override
        public boolean isAllowed(FetchRequest request, RobotPolicy policy) {
            return false;
        }
    }

} // Class end.