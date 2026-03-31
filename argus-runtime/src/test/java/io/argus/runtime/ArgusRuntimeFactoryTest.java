package io.argus.runtime;

import io.argus.ingestion.fetch.DefaultFetchExecutorRegistry;
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
        assertNotNull(runtime.fetchExecutorRegistry());
        assertTrue(runtime.fetchExecutorRegistry() instanceof DefaultFetchExecutorRegistry);
    }

} // Class end.
