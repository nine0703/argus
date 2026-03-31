package io.argus.ingestion.policy;

import junit.framework.TestCase;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:26
 */
public class FetchPolicyTest extends TestCase {

    public void testShouldAllowConfiguredProtocol() {

        FetchPolicy policy = new FetchPolicy(
                Collections.singleton("http"),
                RateLimitPolicy.noLimit(),
                RobotPolicy.strict("argus-bot")
        );

        assertTrue(policy.allows(() -> "http"));
        assertFalse(policy.allows(() -> "ftp"));
    }

    public void testShouldApplyRateLimitPolicy() {

        RateLimitPolicy policy = new RateLimitPolicy(Duration.ofSeconds(5));
        Instant lastAccess = Instant.parse("2026-03-31T10:00:00Z");

        assertFalse(policy.allows(lastAccess, Instant.parse("2026-03-31T10:00:03Z")));
        assertTrue(policy.allows(lastAccess, Instant.parse("2026-03-31T10:00:05Z")));
    }

} // Class end.
