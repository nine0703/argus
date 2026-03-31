package io.argus.ingestion.fetch.replay;

import io.argus.ingestion.fetch.FetchResult;
import io.argus.ingestion.fetch.protocol.http.HttpFetchRequest;
import io.argus.ingestion.fetch.protocol.http.HttpFetchResult;
import io.argus.ingestion.fetch.protocol.http.HttpMethod;
import junit.framework.TestCase;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;

/**
 * @author TK.ENDO
 * @since 2026-03-31
 */
public class FileFetchRecordStoreTest extends TestCase {

    public void testShouldPersistRecordsAcrossStoreInstances() throws Exception {

        Path tempFile = Files.createTempFile("argus-fetch-record-store", ".bin");
        Files.deleteIfExists(tempFile);

        HttpFetchRequest request = new HttpFetchRequest(
                java.net.URI.create("http://example.com/replay"),
                HttpMethod.GET,
                Collections.emptyMap(),
                null
        );
        FetchResult result = new HttpFetchResult(200, "ok".getBytes(), Collections.emptyMap());

        FileFetchRecordStore writer = new FileFetchRecordStore(tempFile);
        writer.save(
                FetchExecutionRecord.success(
                        request,
                        result,
                        Instant.parse("2026-03-31T10:00:00Z"),
                        Instant.parse("2026-03-31T10:00:01Z")
                )
        );

        FileFetchRecordStore reader = new FileFetchRecordStore(tempFile);
        FetchExecutionRecord restored = reader.find(request).orElse(null);

        assertNotNull(restored);
        assertTrue(restored.isSuccess());
        assertEquals(1, reader.size());
        assertEquals("http", restored.result().protocol().name());
        assertEquals("ok", new String(restored.result().body()));
    }

} // Class end.