package io.argus.runtime;

import io.argus.core.audit.AuditLog;
import io.argus.core.audit.InMemoryAuditLog;
import io.argus.core.memory.InMemoryMemory;
import io.argus.core.memory.Memory;
import io.argus.ingestion.audit.fetch.AuditingFetchExecutor;
import io.argus.ingestion.audit.fetch.FetchAuditPublisher;
import io.argus.ingestion.fetch.DefaultFetchExecutorRegistry;
import io.argus.ingestion.fetch.FetchExecutorRegistry;
import io.argus.ingestion.fetch.protocol.ftp.FtpFetchExecutor;
import io.argus.ingestion.fetch.protocol.http.HttpFetchExecutor;

/**
 * Creates default runtime infrastructure for ARGUS.
 *
 * <p>
 * This factory defines the out-of-the-box local runtime composition:
 * <ul>
 *   <li>in-memory {@link Memory}</li>
 *   <li>in-memory {@link AuditLog}</li>
 *   <li>audit-backed fetch publication</li>
 *   <li>HTTP and FTP fetch executor registration</li>
 * </ul>
 *
 * <p>
 * The produced runtime is intentionally minimal and deterministic,
 * suitable for starter auto-configuration and local embedding.
 *
 * <p>
 * 该工厂提供 ARGUS 的默认本地运行时装配，
 * 供 Spring Boot 自动装配模块和非 Spring 场景共同复用。
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:06
 */
public final class ArgusRuntimeFactory {

    private ArgusRuntimeFactory() {
    }

    public static ArgusRuntime createDefaultRuntime() {

        AuditLog auditLog = createDefaultAuditLog();
        Memory memory = createDefaultMemory();
        FetchAuditPublisher fetchAuditPublisher =
                createDefaultFetchAuditPublisher(auditLog);
        FetchExecutorRegistry fetchExecutorRegistry =
                createDefaultFetchExecutorRegistry(fetchAuditPublisher);

        return createRuntime(
                memory,
                auditLog,
                fetchAuditPublisher,
                fetchExecutorRegistry
        );
    }

    public static Memory createDefaultMemory() {
        return new InMemoryMemory();
    }

    public static AuditLog createDefaultAuditLog() {
        return new InMemoryAuditLog();
    }

    public static FetchAuditPublisher createDefaultFetchAuditPublisher(AuditLog auditLog) {
        return new AuditLogBackedFetchAuditPublisher(auditLog);
    }

    public static ArgusRuntime createRuntime(
            Memory memory,
            AuditLog auditLog,
            FetchAuditPublisher fetchAuditPublisher,
            FetchExecutorRegistry fetchExecutorRegistry
    ) {
        return new ArgusRuntime(
                memory,
                auditLog,
                fetchAuditPublisher,
                fetchExecutorRegistry
        );
    }

    public static FetchExecutorRegistry createDefaultFetchExecutorRegistry(
            FetchAuditPublisher publisher
    ) {

        DefaultFetchExecutorRegistry registry = new DefaultFetchExecutorRegistry();
        registry.register(new AuditingFetchExecutor(new HttpFetchExecutor(), publisher));
        registry.register(new AuditingFetchExecutor(new FtpFetchExecutor(), publisher));
        return registry;
    }

} // Class end.
