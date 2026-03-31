package io.argus.ingestion.fetch;

import io.argus.core.action.Action;
import io.argus.core.action.ActionType;
import io.argus.core.model.Metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Agent-level action wrapping a FetchRequest.
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:58
 */
public final class FetchAction implements Action {

    private final FetchRequest request;
    private final Metadata metadata;

    public FetchAction(FetchRequest request) {
        this.request = Objects.requireNonNull(request, "request");
        this.metadata = buildMetadata(request);
    }

    public FetchRequest request() {
        return request;
    }

    @Override
    public ActionType getType() {
        return ActionType.FETCH;
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    private static Metadata buildMetadata(FetchRequest request) {

        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("resource", request.resource().toString());
        attributes.put("protocol", request.protocol().name());

        Map<String, List<String>> requestMetadata = request.metadata();
        if (requestMetadata != null && !requestMetadata.isEmpty()) {
            attributes.put("requestMetadata", snapshot(requestMetadata));
        }

        return new Metadata(attributes);
    }

    private static Map<String, List<String>> snapshot(Map<String, List<String>> source) {

        Map<String, List<String>> copy = new LinkedHashMap<>();

        for (Map.Entry<String, List<String>> entry : source.entrySet()) {
            List<String> values = entry.getValue();
            if (values == null) {
                copy.put(entry.getKey(), Collections.emptyList());
                continue;
            }

            copy.put(
                    entry.getKey(),
                    Collections.unmodifiableList(new ArrayList<>(values))
            );
        }

        return Collections.unmodifiableMap(copy);
    }

} // Class end.