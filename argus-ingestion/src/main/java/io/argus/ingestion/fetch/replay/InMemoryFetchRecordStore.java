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

    private final Map<String, FetchExecutionRecord> store =
            new ConcurrentHashMap<>();

    @Override
    public void save(FetchExecutionRecord record) {
        store.put(FetchRequestFingerprint.create(record.request()), record);
    }

    @Override
    public Optional<FetchExecutionRecord> find(FetchRequest request) {
        return Optional.ofNullable(store.get(FetchRequestFingerprint.create(request)));
    }

    public int size() {
        return store.size();
    }

}   // Class end.