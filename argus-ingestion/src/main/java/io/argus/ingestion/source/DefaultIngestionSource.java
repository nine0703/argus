package io.argus.ingestion.source;

import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.fetch.replay.FetchReplayMode;
import io.argus.ingestion.orchestration.IngestionCommand;
import io.argus.ingestion.orchestration.IngestionOrchestrator;
import io.argus.ingestion.policy.FetchPolicy;
import io.argus.ingestion.policy.RobotsTxtService;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Default source implementation bridging request snapshots into the
 * canonical ingestion orchestrator.
 *
 * <p>
 * This implementation is responsible for:
 * <ul>
 *   <li>validating the declared fetch policy before live access</li>
 *   <li>enforcing replay-mode compatibility</li>
 *   <li>supporting deterministic dry-run validation</li>
 *   <li>enriching ingestion results with source-level execution metadata</li>
 * </ul>
 *
 * @author TK.ENDO
 * @since 2026-03-31
 */
public final class DefaultIngestionSource implements IngestionSource {

    private final IngestionOrchestrator orchestrator;
    private final FetchPolicy defaultFetchPolicy;
    private final FetchReplayMode fetchReplayMode;
    private final RobotsTxtService robotsTxtService;
    private final ConcurrentMap<String, Instant> lastAccessByResource;

    public DefaultIngestionSource(
            IngestionOrchestrator orchestrator,
            FetchPolicy defaultFetchPolicy,
            FetchReplayMode fetchReplayMode
    ) {
        this(orchestrator, defaultFetchPolicy, fetchReplayMode, null);
    }

    public DefaultIngestionSource(
            IngestionOrchestrator orchestrator,
            FetchPolicy defaultFetchPolicy,
            FetchReplayMode fetchReplayMode,
            RobotsTxtService robotsTxtService
    ) {
        this.orchestrator = Objects.requireNonNull(orchestrator, "orchestrator");
        this.defaultFetchPolicy = Objects.requireNonNull(defaultFetchPolicy, "defaultFetchPolicy");
        this.fetchReplayMode = Objects.requireNonNull(fetchReplayMode, "fetchReplayMode");
        this.robotsTxtService = robotsTxtService;
        this.lastAccessByResource = new ConcurrentHashMap<>();
    }

    @Override
    public IngestionResult ingest(IngestionRequest request, IngestionMode mode) {

        IngestionRequest resolvedRequest = Objects.requireNonNull(request, "request");
        IngestionMode resolvedMode = Objects.requireNonNull(mode, "mode");
        FetchPolicy policy = resolvedRequest.fetchPolicy() == null
                ? defaultFetchPolicy
                : resolvedRequest.fetchPolicy();

        validateModeCompatibility(resolvedMode);
        validatePolicy(resolvedRequest.fetchRequest(), policy, resolvedMode);

        if (resolvedMode == IngestionMode.DRY_RUN) {
            return dryRunResult(resolvedRequest, policy);
        }

        IngestionResult result = orchestrator.ingest(
                new IngestionCommand(
                        resolvedRequest.id(),
                        resolvedRequest.fetchRequest(),
                        resolvedRequest.options()
                )
        );

        if (resolvedMode == IngestionMode.LIVE) {
            lastAccessByResource.put(rateLimitKey(resolvedRequest.fetchRequest()), Instant.now());
        }

        return enrichResult(result, resolvedRequest, policy, resolvedMode);
    }

    private void validateModeCompatibility(IngestionMode mode) {
        if (mode == IngestionMode.REPLAY && fetchReplayMode == FetchReplayMode.LIVE) {
            throw new IllegalStateException(
                    "IngestionSource cannot execute REPLAY mode when fetch replay mode is LIVE"
            );
        }
    }

    private void validatePolicy(FetchRequest request, FetchPolicy policy, IngestionMode mode) {

        if (!policy.allows(request.protocol())) {
            throw new IllegalStateException(
                    "Fetch policy does not allow protocol: " + request.protocol().name()
            );
        }

        if (mode == IngestionMode.LIVE) {
            String key = rateLimitKey(request);
            Instant now = Instant.now();
            Instant lastAccess = lastAccessByResource.get(key);
            if (!policy.rateLimitPolicy().allows(lastAccess, now)) {
                throw new IllegalStateException(
                        "Fetch policy rate limit violated for resource: " + request.resource()
                );
            }
        }

        if (mode != IngestionMode.DRY_RUN
                && policy.robotPolicy().obeyRobotsTxt()
                && isHttp(request)
                && robotsTxtService != null
                && !robotsTxtService.isAllowed(request, policy.robotPolicy())) {
            throw new IllegalStateException(
                    "robots.txt disallows resource: " + request.resource()
            );
        }
    }

    private IngestionResult dryRunResult(
            IngestionRequest request,
            FetchPolicy policy
    ) {

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("mode", IngestionMode.DRY_RUN.name());
        metadata.put("validated", true);
        metadata.put("fetchReplayMode", fetchReplayMode.name());
        metadata.put("resource", request.fetchRequest().resource().toString());
        metadata.put("protocol", request.fetchRequest().protocol().name());
        metadata.put("robotsChecked", false);
        metadata.put("namespace", request.options().namespace());
        metadata.put("embeddingEnabled", request.options().enableEmbedding());
        metadata.put("vectorStoreEnabled", request.options().enableVectorStore());
        applyPolicyMetadata(metadata, policy);

        return new DefaultIngestionResult(
                request.id(),
                Instant.now(),
                true,
                metadata
        );
    }

    private IngestionResult enrichResult(
            IngestionResult result,
            IngestionRequest request,
            FetchPolicy policy,
            IngestionMode mode
    ) {

        Map<String, Object> metadata = new LinkedHashMap<>(result.metadata());
        metadata.put("mode", mode.name());
        metadata.put("fetchReplayMode", fetchReplayMode.name());
        metadata.put("resource", request.fetchRequest().resource().toString());
        metadata.put("protocol", request.fetchRequest().protocol().name());
        metadata.put("robotsChecked", policy.robotPolicy().obeyRobotsTxt() && isHttp(request.fetchRequest()));
        applyPolicyMetadata(metadata, policy);

        return new DefaultIngestionResult(
                result.requestId(),
                result.timestamp(),
                result.success(),
                metadata
        );
    }

    private void applyPolicyMetadata(Map<String, Object> metadata, FetchPolicy policy) {
        metadata.put("allowedProtocols", policy.allowedProtocols());
        metadata.put("rateLimitMillis", policy.rateLimitPolicy().minInterval().toMillis());
        metadata.put("obeyRobotsTxt", policy.robotPolicy().obeyRobotsTxt());
        metadata.put("userAgent", policy.robotPolicy().userAgent());
    }

    private String rateLimitKey(FetchRequest request) {
        return request.resource().toString();
    }

    private boolean isHttp(FetchRequest request) {
        String scheme = request.resource().getScheme();
        return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
    }

} // Class end.
