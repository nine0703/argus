package io.argus.ingestion.source;

import io.argus.core.observation.Observation;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

/**
 * Represents the authoritative factual outcome of a single ingestion request.
 *
 * <p>
 * {@code IngestionResult} captures what was externally observed when executing
 * an {@link IngestionRequest}. It is immutable, replayable, and sufficient for
 * downstream audit or observation reconstruction.
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:57
 */
public interface IngestionResult extends Serializable {

    /**
     * Returns the ingestion command or request identifier associated with this result.
     */
    String requestId();

    /**
     * Returns the observed completion timestamp of this ingestion result.
     */
    Instant timestamp();

    /**
     * Returns whether the ingestion operation completed successfully.
     */
    boolean success();

    /**
     * Returns immutable metadata describing the ingestion outcome.
     *
     * <p>
     * Metadata should be sufficient to reconstruct a higher-level
     * {@link Observation} without requiring additional external access.
     */
    Map<String, Object> metadata();

} // Class end.