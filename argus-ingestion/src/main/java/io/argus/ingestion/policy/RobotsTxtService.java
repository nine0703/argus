package io.argus.ingestion.policy;

import io.argus.ingestion.fetch.FetchRequest;

/**
 * Resolves and evaluates robots.txt access rules for a fetch request.
 * @author TK.ENDO
 * @since 2026-03-31
 */
public interface RobotsTxtService {

    boolean isAllowed(FetchRequest request, RobotPolicy policy);

} // Class end.
