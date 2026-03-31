package io.argus.core.agent;

import io.argus.core.action.Action;
import io.argus.core.action.ActionType;
import io.argus.core.action.DefaultActionResult;
import io.argus.core.model.Metadata;
import io.argus.core.observation.Observation;
import io.argus.core.observation.ObservationType;
import junit.framework.TestCase;

import java.time.Instant;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 20:42
 */
public class LoopResultTest extends TestCase {

    public void testShouldCarryOptionalActionResult() {

        LoopResult result = new LoopResult(
                new Action() {
                    @Override
                    public ActionType getType() {
                        return ActionType.REQUEST;
                    }

                    @Override
                    public Metadata getMetadata() {
                        return new Metadata(Map.of("request", "fetch"));
                    }
                },
                new DefaultActionResult(true, Instant.ofEpochMilli(1L), new Metadata(Map.of("status", 200))),
                new Observation() {
                    @Override
                    public ObservationType getType() {
                        return ObservationType.RESPONSE;
                    }

                    @Override
                    public Metadata getMetadata() {
                        return new Metadata(Map.of("kind", "ok"));
                    }
                },
                AgentState.RUNNING
        );

        assertNotNull(result.getActionResult());
        assertTrue(result.getActionResult().success());
        assertEquals(200, result.getActionResult().metadata().get("status").orElse(null));
    }

} // Class end.
