package io.argus.core.action;

import io.argus.core.model.Metadata;
import junit.framework.TestCase;

import java.time.Instant;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:41
 */
public class DefaultActionResultTest extends TestCase {

    public void testShouldExposeImmutableActionResultFields() {

        Instant timestamp = Instant.ofEpochMilli(123L);
        Metadata metadata = new Metadata(Map.of("channel", "http"));
        DefaultActionResult result = new DefaultActionResult(true, timestamp, metadata);

        assertTrue(result.success());
        assertEquals(timestamp, result.timestamp());
        assertEquals("http", result.metadata().get("channel").orElse(null));
    }

} // Class end.
