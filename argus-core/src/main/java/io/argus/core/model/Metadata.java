package io.argus.core.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:05
 */
public final class Metadata {

    private final Map<String, Object> attributes;

    public Metadata(Map<String, Object> attributes) {
        this.attributes = Collections.unmodifiableMap(
                attributes == null ? Collections.emptyMap() : new HashMap<>(attributes)
        );
    }

    public Optional<Object> get(String key) {
        return Optional.ofNullable(attributes.get(key));
    }

    public Map<String, Object> asMap() {
        return attributes;
    }

    public boolean isEmpty() {
        return attributes.isEmpty();
    }

} // Class end.