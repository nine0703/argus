package io.argus.ingestion.fetch;

import io.argus.core.action.Action;
import io.argus.core.action.ActionType;
import io.argus.core.model.Metadata;

/**
 * Agent-level action wrapping a FetchRequest.
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:58
 */
public final class FetchAction implements Action {

    private final FetchRequest request;

    public FetchAction(FetchRequest request) {
        this.request = request;
    }

    public FetchRequest request() {
        return request;
    }

    @Override
    public ActionType getType() {
        return null;
    }

    @Override
    public Metadata getMetadata() {
        return null;
    }

} // Class end.