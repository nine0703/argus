package io.argus.ingestion.fetch.protocol.ftp;

import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:41
 */
public class FtpFetchResult implements FetchResult {

    private static final FetchProtocol FTP = () -> "ftp";

    private final boolean success;
    private final byte[] body;
    private final String serverMessage;

    public FtpFetchResult(
            boolean success,
            byte[] body,
            String serverMessage
    ) {
        this.success = success;
        this.body = body;
        this.serverMessage = serverMessage;
    }

    @Override
    public FetchProtocol protocol() {
        return FTP;
    }

    @Override
    public byte[] body() {
        return body;
    }

    @Override
    public Map<String, List<String>> metadata() {
        return Collections.emptyMap();
    }

    @Override
    public boolean success() {
        return success;
    }

    public String serverMessage() {
        return serverMessage;
    }

}   // Class end.