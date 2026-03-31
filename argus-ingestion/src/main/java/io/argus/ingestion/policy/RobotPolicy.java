package io.argus.ingestion.policy;

import java.util.Objects;

/**
 * Immutable robots policy declaration for ingestion execution.
 *
 * <p>
 * The policy does not fetch or parse robots.txt by itself.
 * It only captures how a caller intends to interpret robots directives.
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 15:00
 */
public final class RobotPolicy {

    private final boolean obeyRobotsTxt;
    private final String userAgent;

    public RobotPolicy(boolean obeyRobotsTxt, String userAgent) {
        this.obeyRobotsTxt = obeyRobotsTxt;
        this.userAgent = Objects.requireNonNull(userAgent, "userAgent").trim();
        if (this.userAgent.isEmpty()) {
            throw new IllegalArgumentException("userAgent must not be blank");
        }
    }

    public boolean obeyRobotsTxt() {
        return obeyRobotsTxt;
    }

    public String userAgent() {
        return userAgent;
    }

    public static RobotPolicy permissive(String userAgent) {
        return new RobotPolicy(false, userAgent);
    }

    public static RobotPolicy strict(String userAgent) {
        return new RobotPolicy(true, userAgent);
    }

} // Class end.