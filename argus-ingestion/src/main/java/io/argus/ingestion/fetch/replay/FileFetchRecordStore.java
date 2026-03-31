package io.argus.ingestion.fetch.replay;

import io.argus.ingestion.fetch.FetchRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * File-backed {@link FetchRecordStore} implementation.
 *
 * <p>
 * Records are cached in memory and flushed to a single on-disk snapshot
 * after every write, which keeps replay semantics deterministic for local
 * development and starter use cases.
 *
 * @author TK.ENDO
 * @since 2026-03-31
 */
public class FileFetchRecordStore implements FetchRecordStore {

    private final Path filePath;
    private final Map<String, FetchExecutionRecord> store;

    public FileFetchRecordStore(Path filePath) {
        this.filePath = filePath.toAbsolutePath().normalize();
        this.store = new ConcurrentHashMap<>();
        load();
    }

    @Override
    public synchronized void save(FetchExecutionRecord record) {
        store.put(FetchRequestFingerprint.create(record.request()), record);
        persist();
    }

    @Override
    public synchronized Optional<FetchExecutionRecord> find(FetchRequest request) {
        return Optional.ofNullable(store.get(FetchRequestFingerprint.create(request)));
    }

    public synchronized int size() {
        return store.size();
    }

    public Path filePath() {
        return filePath;
    }

    @SuppressWarnings("unchecked")
    private void load() {

        if (!Files.exists(filePath)) {
            return;
        }

        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(filePath))) {
            Object object = input.readObject();
            if (object instanceof Map<?, ?>) {
                store.clear();
                store.putAll((Map<String, FetchExecutionRecord>) object);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Failed to load fetch record store from " + filePath, e);
        }
    }

    private void persist() {

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                output.writeObject(new LinkedHashMap<>(store));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to persist fetch record store to " + filePath, e);
        }
    }

} // Class end.
