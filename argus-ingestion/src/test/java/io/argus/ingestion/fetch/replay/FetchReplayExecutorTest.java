package io.argus.ingestion.fetch.replay;

import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.fetch.FetchResult;
import io.argus.ingestion.fetch.protocol.http.HttpFetchRequest;
import io.argus.ingestion.fetch.protocol.http.HttpFetchResult;
import io.argus.ingestion.fetch.protocol.http.HttpMethod;
import junit.framework.TestCase;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 21:07
 */
public class FetchReplayExecutorTest extends TestCase {

    public void testShouldReplayRecordedResultForEquivalentRequestInstance() {

        InMemoryFetchRecordStore store = new InMemoryFetchRecordStore();
        RecordingFetchExecutor recording = new RecordingFetchExecutor(new HttpStubFetchExecutor(), store);

        HttpFetchRequest first = new HttpFetchRequest(
                URI.create("http://example.com/resource"),
                HttpMethod.GET,
                Map.of("Accept", List.of("text/plain")),
                null
        );
        HttpFetchRequest equivalent = new HttpFetchRequest(
                URI.create("http://example.com/resource"),
                HttpMethod.GET,
                Map.of("Accept", List.of("text/plain")),
                null
        );

        recording.execute(first);
        ReplayableFetchExecutor replayable = new ReplayableFetchExecutor(
                new FailingFetchExecutor(),
                store,
                FetchReplayMode.REPLAY_ONLY
        );

        FetchResult replayed = replayable.execute(equivalent);

        assertTrue(replayed.success());
        assertEquals(1, store.size());
        assertEquals("http", replayed.protocol().name());
    }

    public void testShouldThrowHelpfulMessageWhenReplayMisses() {

        ReplayableFetchExecutor replayable = new ReplayableFetchExecutor(
                new FailingFetchExecutor(),
                new InMemoryFetchRecordStore(),
                FetchReplayMode.REPLAY_ONLY
        );

        try {
            replayable.execute(new HttpFetchRequest(
                    URI.create("http://example.com/miss"),
                    HttpMethod.GET,
                    Collections.emptyMap(),
                    null
            ));
            fail("Expected replay miss");
        } catch (IllegalStateException expected) {
            assertEquals(
                    "No recorded execution found for request: http://example.com/miss",
                    expected.getMessage()
            );
        }
    }

    private static final class HttpStubFetchExecutor implements FetchExecutor {

        @Override
        public FetchProtocol protocol() {
            return () -> "http";
        }

        @Override
        public FetchResult execute(FetchRequest request) {
            return new HttpFetchResult(200, "ok".getBytes(), Collections.emptyMap());
        }
    }

    private static final class FailingFetchExecutor implements FetchExecutor {

        @Override
        public FetchProtocol protocol() {
            return () -> "http";
        }

        @Override
        public FetchResult execute(FetchRequest request) {
            throw new AssertionError("should not execute live delegate");
        }
    }
}