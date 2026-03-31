package io.argus.spring.boot.autoconfigure;

import io.argus.core.audit.AuditLog;
import io.argus.core.memory.Memory;
import io.argus.ingestion.audit.fetch.FetchAuditPublisher;
import io.argus.ingestion.fetch.FetchExecutorRegistry;
import io.argus.runtime.ArgusRuntime;
import io.argus.runtime.ArgusRuntimeFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto-configuration for the ARGUS runtime stack.
 *
 * <p>
 * This module is the integration boundary between Spring Boot
 * and the framework-agnostic runtime implementation.
 * Applications that import the ARGUS starter should obtain a usable
 * local runtime without additional manual bean wiring.
 *
 * <p>
 * 自动装配模块负责把 `argus-runtime` 中的默认运行时能力接入 Spring 容器，
 * 让业务项目在引入 Starter 后即可获得可用的内存、审计和抓取执行基础设施。
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:08
 */
@AutoConfiguration
@ConditionalOnClass(ArgusRuntime.class)
@EnableConfigurationProperties(ArgusProperties.class)
@ConditionalOnProperty(prefix = "argus", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ArgusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ArgusRuntime argusRuntime() {
        return ArgusRuntimeFactory.createDefaultRuntime();
    }

    @Bean
    @ConditionalOnMissingBean
    public Memory argusMemory(ArgusRuntime runtime) {
        return runtime.memory();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditLog argusAuditLog(ArgusRuntime runtime) {
        return runtime.auditLog();
    }

    @Bean
    @ConditionalOnMissingBean
    public FetchAuditPublisher argusFetchAuditPublisher(ArgusRuntime runtime) {
        return runtime.fetchAuditPublisher();
    }

    @Bean
    @ConditionalOnMissingBean
    public FetchExecutorRegistry argusFetchExecutorRegistry(ArgusRuntime runtime) {
        return runtime.fetchExecutorRegistry();
    }

} // Class end.
