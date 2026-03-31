package io.argus.ingestion.policy;

import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.fetch.FetchResult;
import io.argus.ingestion.fetch.protocol.http.HttpFetchRequest;
import io.argus.ingestion.fetch.protocol.http.HttpFetchResult;
import io.argus.ingestion.fetch.protocol.http.HttpMethod;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Default robots.txt resolver backed by the configured {@link FetchExecutor}.
 *
 * <p>
 * When robots enforcement is enabled, this service fetches the target
 * origin's robots.txt, parses minimal allow/disallow rules, and caches the
 * parsed result for a bounded duration.
 *
 * @author TK.ENDO
 * @since 2026-03-31
 */
public class DefaultRobotsTxtService implements RobotsTxtService {

    private final FetchExecutor fetchExecutor;
    private final Duration cacheTtl;
    private final ConcurrentMap<String, CachedRules> cache;

    public DefaultRobotsTxtService(FetchExecutor fetchExecutor, Duration cacheTtl) {
        this.fetchExecutor = Objects.requireNonNull(fetchExecutor, "fetchExecutor");
        this.cacheTtl = Objects.requireNonNull(cacheTtl, "cacheTtl");
        if (cacheTtl.isNegative()) {
            throw new IllegalArgumentException("cacheTtl must not be negative");
        }
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public boolean isAllowed(FetchRequest request, RobotPolicy policy) {

        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(policy, "policy");

        URI resource = request.resource();
        if (!isHttp(resource)) {
            return true;
        }

        URI robotsUri = robotsUri(resource);
        RobotsRules rules = resolveRules(robotsUri, policy.userAgent());
        return rules.isAllowed(policy.userAgent(), normalizeResourcePath(resource));
    }

    private RobotsRules resolveRules(URI robotsUri, String userAgent) {

        String cacheKey = robotsUri.toString();
        CachedRules cached = cache.get(cacheKey);
        Instant now = Instant.now();
        if (cached != null && !cached.isExpired(now)) {
            return cached.rules();
        }

        FetchResult result = fetchExecutor.execute(
                new HttpFetchRequest(
                        robotsUri,
                        HttpMethod.GET,
                        Collections.singletonMap("User-Agent", Collections.singletonList(userAgent)),
                        null
                )
        );

        RobotsRules rules = parseRules(result, robotsUri);
        if (!cacheTtl.isZero()) {
            cache.put(cacheKey, new CachedRules(rules, now.plus(cacheTtl)));
        }
        return rules;
    }

    private RobotsRules parseRules(FetchResult result, URI robotsUri) {

        if (result instanceof HttpFetchResult) {
            HttpFetchResult http = (HttpFetchResult) result;
            if (http.status() == 404) {
                return RobotsRules.allowAll();
            }
            if (http.status() < 200 || http.status() >= 300) {
                throw new IllegalStateException(
                        "robots.txt fetch failed for " + robotsUri + " with status " + http.status()
                );
            }
        } else if (!result.success()) {
            throw new IllegalStateException("robots.txt fetch failed for " + robotsUri);
        }

        byte[] body = result.body();
        if (body == null || body.length == 0) {
            return RobotsRules.allowAll();
        }

        return RobotsRules.parse(new String(body, StandardCharsets.UTF_8));
    }

    private boolean isHttp(URI resource) {
        String scheme = resource.getScheme();
        return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
    }

    private URI robotsUri(URI resource) {
        return URI.create(resource.getScheme() + "://" + resource.getAuthority() + "/robots.txt");
    }

    private String normalizeResourcePath(URI resource) {

        String path = resource.getRawPath();
        if (path == null || path.isEmpty()) {
            path = "/";
        }
        if (resource.getRawQuery() != null && !resource.getRawQuery().isEmpty()) {
            path = path + "?" + resource.getRawQuery();
        }
        return path;
    }

    private static final class CachedRules {

        private final RobotsRules rules;
        private final Instant expiresAt;

        private CachedRules(RobotsRules rules, Instant expiresAt) {
            this.rules = rules;
            this.expiresAt = expiresAt;
        }

        private RobotsRules rules() {
            return rules;
        }

        private boolean isExpired(Instant now) {
            return !expiresAt.isAfter(now);
        }
    }

    private static final class RobotsRules {

        private final List<RuleGroup> groups;

        private RobotsRules(List<RuleGroup> groups) {
            this.groups = groups;
        }

        private static RobotsRules allowAll() {
            return new RobotsRules(Collections.emptyList());
        }

        private static RobotsRules parse(String content) {

            List<RuleGroup> groups = new ArrayList<>();
            RuleGroup current = null;

            for (String rawLine : content.split("\\R")) {
                String line = stripComment(rawLine).trim();
                if (line.isEmpty()) {
                    continue;
                }

                int separator = line.indexOf(':');
                if (separator < 0) {
                    continue;
                }

                String directive = line.substring(0, separator).trim().toLowerCase(Locale.ROOT);
                String value = line.substring(separator + 1).trim();

                if ("user-agent".equals(directive)) {
                    if (current == null || !current.rules.isEmpty()) {
                        current = new RuleGroup();
                        groups.add(current);
                    }
                    current.userAgents.add(value.toLowerCase(Locale.ROOT));
                    continue;
                }

                if (current == null) {
                    continue;
                }

                if ("allow".equals(directive)) {
                    current.rules.add(new RobotsRule(true, value));
                } else if ("disallow".equals(directive) && !value.isEmpty()) {
                    current.rules.add(new RobotsRule(false, value));
                }
            }

            return new RobotsRules(groups);
        }

        private boolean isAllowed(String userAgent, String path) {

            String normalizedUserAgent = userAgent.toLowerCase(Locale.ROOT);
            List<RuleGroup> selected = selectGroups(normalizedUserAgent);
            if (selected.isEmpty()) {
                return true;
            }

            RobotsRule best = null;
            for (RuleGroup group : selected) {
                for (RobotsRule rule : group.rules) {
                    if (!path.startsWith(rule.pattern)) {
                        continue;
                    }
                    if (best == null
                            || rule.pattern.length() > best.pattern.length()
                            || (rule.pattern.length() == best.pattern.length() && rule.allow)) {
                        best = rule;
                    }
                }
            }

            return best == null || best.allow;
        }

        private List<RuleGroup> selectGroups(String userAgent) {

            List<RuleGroup> matches = new ArrayList<>();
            int bestSpecificity = -1;

            for (RuleGroup group : groups) {
                int specificity = matchSpecificity(group, userAgent);
                if (specificity < 0) {
                    continue;
                }
                if (specificity > bestSpecificity) {
                    matches.clear();
                    bestSpecificity = specificity;
                }
                if (specificity == bestSpecificity) {
                    matches.add(group);
                }
            }

            return matches;
        }

        private int matchSpecificity(RuleGroup group, String userAgent) {

            int best = -1;
            for (String declaredUserAgent : group.userAgents) {
                if ("*".equals(declaredUserAgent)) {
                    best = Math.max(best, 0);
                    continue;
                }
                if (userAgent.startsWith(declaredUserAgent)) {
                    best = Math.max(best, declaredUserAgent.length());
                }
            }
            return best;
        }

        private static String stripComment(String line) {
            int comment = line.indexOf('#');
            return comment < 0 ? line : line.substring(0, comment);
        }
    }

    private static final class RuleGroup {

        private final List<String> userAgents = new ArrayList<>();
        private final List<RobotsRule> rules = new ArrayList<>();
    }

    private static final class RobotsRule {

        private final boolean allow;
        private final String pattern;

        private RobotsRule(boolean allow, String pattern) {
            this.allow = allow;
            this.pattern = pattern;
        }
    }

} // Class end.
