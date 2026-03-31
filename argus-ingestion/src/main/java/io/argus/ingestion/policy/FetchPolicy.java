package io.argus.ingestion.policy;

import io.argus.ingestion.fetch.FetchProtocol;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Aggregated policy governing whether and how a fetch may execute.
 *
 * <p>
 * {@code FetchPolicy} combines protocol allow-listing, rate limiting,
 * and robots semantics into a single immutable policy object that can
 * travel together with ingestion configuration.
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:59
 */
public final class FetchPolicy {

    private final Set<String> allowedProtocols;
    private final RateLimitPolicy rateLimitPolicy;
    private final RobotPolicy robotPolicy;

    public FetchPolicy(
            Set<String> allowedProtocols,
            RateLimitPolicy rateLimitPolicy,
            RobotPolicy robotPolicy
    ) {
        this.allowedProtocols = Collections.unmodifiableSet(normalizeProtocols(allowedProtocols));
        this.rateLimitPolicy = Objects.requireNonNull(rateLimitPolicy, "rateLimitPolicy");
        this.robotPolicy = Objects.requireNonNull(robotPolicy, "robotPolicy");
    }

    public Set<String> allowedProtocols() {
        return allowedProtocols;
    }

    public RateLimitPolicy rateLimitPolicy() {
        return rateLimitPolicy;
    }

    public RobotPolicy robotPolicy() {
        return robotPolicy;
    }

    public boolean allows(FetchProtocol protocol) {

        Objects.requireNonNull(protocol, "protocol");
        if (allowedProtocols.isEmpty()) {
            return true;
        }

        return allowedProtocols.contains(normalizeProtocol(protocol.name()));
    }

    public static FetchPolicy unrestricted() {
        return new FetchPolicy(
                Collections.emptySet(),
                RateLimitPolicy.noLimit(),
                RobotPolicy.permissive("argus")
        );
    }

    private Set<String> normalizeProtocols(Set<String> protocols) {

        if (protocols == null || protocols.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> normalized = new LinkedHashSet<>();
        for (String protocol : protocols) {
            normalized.add(normalizeProtocol(protocol));
        }
        normalized.remove("");
        return normalized;
    }

    private String normalizeProtocol(String protocol) {
        return protocol == null ? "" : protocol.trim().toLowerCase(Locale.ROOT);
    }

} // Class end.