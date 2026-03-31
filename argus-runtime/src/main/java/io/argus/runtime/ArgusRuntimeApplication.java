package io.argus.runtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entrypoint for the ARGUS runtime container.
 * <p>
 * ARGUS Runtime 作为运行时装配与启动入口，
 * 负责承载 Agent、Ingestion 与后续基础设施集成。
 * @author TK.ENDO
 * @since 2026-03-31 周二 15:30
 */
@SpringBootApplication
public class ArgusRuntimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArgusRuntimeApplication.class, args);
    }

} // Class end.
