package io.argus.ingestion.fetch.replay;

/**
 * Defines how fetch execution behaves regarding replay.
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:50
 */
public enum FetchReplayMode {

    /**
     * Always execute live network call.
     */
    LIVE,

    /**
     * Always replay from recorded store.
     * Fails if record not found.
     */
    REPLAY_ONLY,

    /**
     * Replay if exists, otherwise execute and record.
     */
    HYBRID

}   // Class end.