package io.argus.spring.boot.autoconfigure;

import io.argus.ingestion.fetch.replay.FetchReplayMode;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized configuration entry for ARGUS Spring Boot integration.
 *
 * <p>
 * The initial property surface stays intentionally compact while still
 * exposing the minimum operational knobs required by the default ingestion
 * pipeline assembled by the starter.
 *
 * <p>
 * Setter methods fail fast on invalid values so misconfiguration is surfaced
 * during container bootstrap rather than during the first runtime request.
 *
 * @author TK.ENDO
 * @since 2026-03-31 鍛ㄤ簩 17:08
 */
@ConfigurationProperties(prefix = "argus")
public class ArgusProperties {

    /**
     * Whether ARGUS auto-configuration should be activated.
     */
    private boolean enabled = true;
    /**
     * Ingestion-specific defaults exposed by the starter.
     */
    private final Ingestion ingestion = new Ingestion();
    /**
     * Fetch-specific defaults exposed by the starter.
     */
    private final Fetch fetch = new Fetch();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Ingestion getIngestion() {
        return ingestion;
    }

    public Fetch getFetch() {
        return fetch;
    }

    /**
     * Property group for the default local ingestion pipeline.
     */
    public static class Ingestion {

        /**
         * Fixed-size chunk length used by the default chunk strategy.
         */
        private int chunkSize = 1000;
        /**
         * Embedding vector dimension used by the default deterministic model.
         */
        private int embeddingDimension = 16;
        /**
         * Namespace assigned to the default in-memory vector store.
         */
        private String vectorNamespace = "default";

        public int getChunkSize() {
            return chunkSize;
        }

        public void setChunkSize(int chunkSize) {
            if (chunkSize <= 0) {
                throw new IllegalArgumentException("argus.ingestion.chunk-size must be positive");
            }
            this.chunkSize = chunkSize;
        }

        public int getEmbeddingDimension() {
            return embeddingDimension;
        }

        public void setEmbeddingDimension(int embeddingDimension) {
            if (embeddingDimension <= 0) {
                throw new IllegalArgumentException("argus.ingestion.embedding-dimension must be positive");
            }
            this.embeddingDimension = embeddingDimension;
        }

        public String getVectorNamespace() {
            return vectorNamespace;
        }

        public void setVectorNamespace(String vectorNamespace) {
            if (vectorNamespace == null || vectorNamespace.trim().isEmpty()) {
                throw new IllegalArgumentException("argus.ingestion.vector-namespace must not be blank");
            }
            this.vectorNamespace = vectorNamespace.trim();
        }

    }

    /**
     * Property group for fetch execution and replay defaults.
     */
    public static class Fetch {

        private FetchReplayMode replayMode = FetchReplayMode.LIVE;
        private final Policy policy = new Policy();

        public FetchReplayMode getReplayMode() {
            return replayMode;
        }

        public void setReplayMode(FetchReplayMode replayMode) {
            this.replayMode = Objects.requireNonNull(replayMode, "replayMode");
        }

        public Policy getPolicy() {
            return policy;
        }
    }

    /**
     * Property group for the default fetch policy declaration.
     */
    public static class Policy {

        private Set<String> allowedProtocols = Collections.emptySet();
        private Duration rateLimit = Duration.ZERO;
        private boolean obeyRobotsTxt;
        private String userAgent = "argus";

        public Set<String> getAllowedProtocols() {
            return allowedProtocols;
        }

        public void setAllowedProtocols(Set<String> allowedProtocols) {
            if (allowedProtocols == null || allowedProtocols.isEmpty()) {
                this.allowedProtocols = Collections.emptySet();
                return;
            }
            this.allowedProtocols = Collections.unmodifiableSet(new LinkedHashSet<>(allowedProtocols));
        }

        public Duration getRateLimit() {
            return rateLimit;
        }

        public void setRateLimit(Duration rateLimit) {
            this.rateLimit = Objects.requireNonNull(rateLimit, "rateLimit");
            if (this.rateLimit.isNegative()) {
                throw new IllegalArgumentException("argus.fetch.policy.rate-limit must not be negative");
            }
        }

        public boolean isObeyRobotsTxt() {
            return obeyRobotsTxt;
        }

        public void setObeyRobotsTxt(boolean obeyRobotsTxt) {
            this.obeyRobotsTxt = obeyRobotsTxt;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            if (userAgent == null || userAgent.trim().isEmpty()) {
                throw new IllegalArgumentException("argus.fetch.policy.user-agent must not be blank");
            }
            this.userAgent = userAgent.trim();
        }
    }

} // Class end.