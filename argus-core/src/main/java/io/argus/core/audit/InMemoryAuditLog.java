package io.argus.core.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Append-only in-memory audit log.
 * <p>
 * Intended for local runtime wiring, tests, and single-process execution.
 * @author TK.ENDO
 * @since 2026-03-31 周二 16:18
 */
public class InMemoryAuditLog implements AuditLog {

    private final CopyOnWriteArrayList<AuditEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public void record(AuditEvent event) {
        events.add(Objects.requireNonNull(event, "event"));
    }

    public List<AuditEvent> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(events));
    }

} // Class end.