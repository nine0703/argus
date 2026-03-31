package io.argus.ingestion.fetch;

import io.argus.core.action.ActionType;
import io.argus.ingestion.fetch.protocol.http.HttpFetchRequest;
import io.argus.ingestion.fetch.protocol.http.HttpMethod;
import junit.framework.TestCase;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 11:40
 */
public class FetchActionTest extends TestCase {

    public void testShouldExposeFetchTypeAndSnapshotMetadata() throws Exception {

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("X-Test", new ArrayList<>());
        headers.get("X-Test").add("v1");

        FetchAction action =
                new FetchAction(
                        new HttpFetchRequest(
                                new URI("http://example.com/resource"),
                                HttpMethod.GET,
                                headers,
                                null
                        )
                );

        headers.get("X-Test").add("mutated");

        assertEquals(ActionType.FETCH, action.getType());
        assertEquals(
                "http://example.com/resource",
                action.getMetadata().get("resource").orElse(null)
        );
        assertEquals("http", action.getMetadata().get("protocol").orElse(null));

        @SuppressWarnings("unchecked")
        Map<String, List<String>> requestMetadata =
                (Map<String, List<String>>) action.getMetadata()
                        .get("requestMetadata")
                        .orElseThrow(AssertionError::new);

        assertEquals(1, requestMetadata.get("X-Test").size());
        assertEquals("v1", requestMetadata.get("X-Test").get(0));
    }

} // Class end.
