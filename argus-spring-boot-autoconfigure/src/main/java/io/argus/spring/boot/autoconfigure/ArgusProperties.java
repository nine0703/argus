package io.argus.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized configuration entry for ARGUS Spring Boot integration.
 *
 * <p>
 * The initial property surface is intentionally small.
 * ARGUS defaults to enabled and provides in-memory local infrastructure.
 * More operational properties can be added once runtime contracts stabilize.
 *
 * <p>
 * 当前属性面保持最小化，
 * 默认启用 ARGUS 自动装配并装配本地内存/审计/抓取运行时能力。
 * 后续若 runtime 契约继续稳定，再逐步扩展可配置项。
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:08
 */
@ConfigurationProperties(prefix = "argus")
public class ArgusProperties {

    /**
     * Whether ARGUS auto-configuration should be activated.
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

} // Class end.
