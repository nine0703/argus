package io.argus.ingestion.source;

/**
 * Defines the execution mode of an {@link IngestionSource}.
 *
 * <p>
 * IngestionMode determines whether external side effects are allowed
 * and how ingestion results are produced.
 *
 * <p>
 * Implementations MUST strictly adhere to the semantic guarantees
 * defined by each mode.
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:57
 */
public enum IngestionMode {

    /**
     * LIVE mode allows real interaction with the external world.
     *
     * <p>
     * In this mode:
     * <ul>
     *   <li>Network and file access are permitted</li>
     *   <li>New {@link IngestionResult} instances may be created</li>
     *   <li>External side effects are allowed</li>
     *   <li>Audit events MUST be emitted</li>
     * </ul>
     *
     * <p>
     * LIVE mode is non-deterministic by nature, as it depends
     * on the state of the external world at execution time.
     */
    LIVE,

    /**
     * REPLAY mode forbids external world access.
     *
     * <p>
     * In this mode:
     * <ul>
     *   <li>No network or file access is permitted</li>
     *   <li>No new IngestionResult may be created</li>
     *   <li>All results MUST be reconstructed from previously recorded facts</li>
     * </ul>
     *
     * <p>
     * If a corresponding recorded IngestionResult is not available,
     * the implementation MUST fail deterministically.
     *
     * <p>
     * REPLAY mode guarantees deterministic reconstruction
     * of prior ingestion behavior.
     */
    REPLAY,

    /**
     * DRY_RUN mode validates ingestion intent without producing facts.
     *
     * <p>
     * In this mode:
     * <ul>
     *   <li>External world access is forbidden</li>
     *   <li>No IngestionResult is persisted</li>
     *   <li>Validation and policy checks may be performed</li>
     * </ul>
     *
     * <p>
     * DRY_RUN is intended for planning, testing,
     * and safety verification scenarios.
     */
    DRY_RUN
} // Class end.