package io.argus.runtime;

import io.argus.agent.AgentRunner;
import io.argus.core.lifecycle.LifecyclePhase;
import io.argus.ingestion.audit.IngestionAuditPublisher;
import io.argus.ingestion.fetch.DefaultFetchExecutorRegistry;
import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.orchestration.IngestionOrchestrator;
import junit.framework.TestCase;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:07
 */
public class ArgusRuntimeFactoryTest extends TestCase {

    public void testShouldCreateDefaultRuntime() {

        ArgusRuntime runtime = ArgusRuntimeFactory.createDefaultRuntime();

        assertNotNull(runtime.memory());
        assertNotNull(runtime.auditLog());
        assertNotNull(runtime.fetchAuditPublisher());
        assertNotNull(runtime.ingestionAuditPublisher());
        assertNotNull(runtime.fetchExecutorRegistry());
        assertNotNull(runtime.fetchExecutor());
        assertNotNull(runtime.ingestionOrchestrator());
        assertNotNull(runtime.agentRunner());
        assertEquals(LifecyclePhase.RUNNING, runtime.phase());
        assertTrue(runtime.isRunning());
        assertTrue(runtime.fetchExecutorRegistry() instanceof DefaultFetchExecutorRegistry);
        assertTrue(runtime.agentRunner() instanceof AgentRunner);
        assertTrue(runtime.fetchExecutor() instanceof FetchExecutor);
        assertTrue(runtime.ingestionOrchestrator() instanceof IngestionOrchestrator);
        assertTrue(runtime.ingestionAuditPublisher() instanceof IngestionAuditPublisher);
    }

} // Class end.