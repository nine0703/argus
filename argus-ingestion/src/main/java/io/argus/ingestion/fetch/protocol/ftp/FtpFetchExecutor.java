package io.argus.ingestion.fetch.protocol.ftp;

import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.fetch.FetchResult;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:46
 */
public class FtpFetchExecutor implements FetchExecutor {

    private static final FetchProtocol FTP = () -> "ftp";

    @Override
    public FetchProtocol protocol() {
        return FTP;
    }

    @Override
    public FetchResult execute(FetchRequest request) {

        if (!(request instanceof FtpFetchRequest)) {
            throw new IllegalArgumentException(
                    "Expected FtpFetchRequest but got " + request.getClass()
            );
        }

        FtpFetchRequest ftpRequest = (FtpFetchRequest) request;

        // TODO: 真正执行 FTP 下载
        throw new UnsupportedOperationException("FTP execution not implemented yet");
    }

}   // Class end.