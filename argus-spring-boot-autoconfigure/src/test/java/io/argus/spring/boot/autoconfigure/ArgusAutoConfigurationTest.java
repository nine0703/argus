package io.argus.spring.boot.autoconfigure;

import io.argus.core.audit.AuditLog;
import io.argus.core.memory.Memory;
import io.argus.ingestion.audit.fetch.FetchAuditPublisher;
import io.argus.ingestion.fetch.FetchExecutorRegistry;
import io.argus.runtime.ArgusRuntime;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import junit.framework.TestCase;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:09
 */
public class ArgusAutoConfigurationTest extends TestCase {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(ArgusAutoConfiguration.class));

    public void testShouldAutoConfigureArgusRuntimeBeans() {

        contextRunner.run(context -> {
            assertNotNull(context.getBean(ArgusRuntime.class));
            assertNotNull(context.getBean(Memory.class));
            assertNotNull(context.getBean(AuditLog.class));
            assertNotNull(context.getBean(FetchAuditPublisher.class));
            assertNotNull(context.getBean(FetchExecutorRegistry.class));
        });
    }

    public void testShouldBackOffWhenDisabled() {

        contextRunner
                .withPropertyValues("argus.enabled=false")
                .run(context -> assertFalse(context.containsBean("argusRuntime")));
    }

} // Class end.
