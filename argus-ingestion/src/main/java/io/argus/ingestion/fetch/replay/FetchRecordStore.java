package io.argus.ingestion.fetch.replay;

import io.argus.ingestion.fetch.FetchRequest;

import java.util.Optional;

/**
 * Storage abstraction for fetch execution records.
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:50
 */
public interface FetchRecordStore {

    void save(FetchExecutionRecord record);

    Optional<FetchExecutionRecord> find(FetchRequest request);

}   // Class end.