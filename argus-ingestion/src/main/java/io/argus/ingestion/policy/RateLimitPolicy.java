package io.argus.ingestion.policy;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Immutable rate-limiting policy for ingestion access cadence.
 *
 * <p>
 * The policy models the minimum interval between two accesses to the same
 * logical source. It is deterministic and side-effect free.
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:59
 */
public final class RateLimitPolicy {

    private final Duration minInterval;

    public RateLimitPolicy(Duration minInterval) {
        this.minInterval = Objects.requireNonNull(minInterval, "minInterval");
        if (minInterval.isNegative()) {
            throw new IllegalArgumentException("minInterval must not be negative");
        }
    }

    public Duration minInterval() {
        return minInterval;
    }

    public boolean allows(Instant lastAccess, Instant currentAccess) {

        Objects.requireNonNull(currentAccess, "currentAccess");
        if (lastAccess == null) {
            return true;
        }

        return !currentAccess.isBefore(lastAccess.plus(minInterval));
    }

    public static RateLimitPolicy noLimit() {
        return new RateLimitPolicy(Duration.ZERO);
    }

} // Class end.