package io.argus.ingestion.fetch.replay;

import io.argus.ingestion.fetch.FetchRequest;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:54
 */
public class InMemoryFetchRecordStore implements FetchRecordStore {

    private final Map<Integer, FetchExecutionRecord> store =
            new ConcurrentHashMap<>();

    @Override
    public void save(FetchExecutionRecord record) {
        store.put(record.request().hashCode(), record);
    }

    @Override
    public Optional<FetchExecutionRecord> find(FetchRequest request) {
        return Optional.ofNullable(store.get(request.hashCode()));
    }

}   // Class end.