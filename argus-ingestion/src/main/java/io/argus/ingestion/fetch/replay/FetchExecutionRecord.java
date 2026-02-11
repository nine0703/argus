package io.argus.ingestion.fetch.replay;

import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.fetch.FetchResult;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable record of a fetch execution.
 * <p>
 * Contains both request snapshot and result snapshot.
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:50
 */
public final class FetchExecutionRecord {

    private final FetchRequest request;
    private final FetchResult result;
    private final ExceptionSnapshot failure;

    private final Instant startTime;
    private final Instant endTime;

    private FetchExecutionRecord(
            FetchRequest request,
            FetchResult result,
            ExceptionSnapshot failure,
            Instant startTime,
            Instant endTime
    ) {
        this.request = Objects.requireNonNull(request);
        this.result = result;
        this.failure = failure;
        this.startTime = Objects.requireNonNull(startTime);
        this.endTime = Objects.requireNonNull(endTime);
    }

    // ========================
    // Factory Methods
    // ========================

    public static FetchExecutionRecord success(
            FetchRequest request,
            FetchResult result,
            Instant start,
            Instant end
    ) {
        return new FetchExecutionRecord(
                request,
                Objects.requireNonNull(result),
                null,
                start,
                end
        );
    }

    public static FetchExecutionRecord failure(
            FetchRequest request,
            Throwable throwable,
            Instant start,
            Instant end
    ) {
        return new FetchExecutionRecord(
                request,
                null,
                ExceptionSnapshot.from(throwable),
                start,
                end
        );
    }

    // ========================
    // Semantic Methods
    // ========================

    public boolean isSuccess() {
        return failure == null;
    }

    public boolean isFailure() {
        return failure != null;
    }

    public long durationMillis() {
        return endTime.toEpochMilli() - startTime.toEpochMilli();
    }

    // ========================
    // Getters
    // ========================

    public FetchRequest request() {
        return request;
    }

    public FetchResult result() {
        return result;
    }

    public ExceptionSnapshot failure() {
        return failure;
    }

    public Instant startTime() {
        return startTime;
    }

    public Instant endTime() {
        return endTime;
    }

}   // Class end.