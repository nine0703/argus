package io.argus.ingestion.fetch.protocol.ftp;

import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchRequest;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:39
 */
public class FtpFetchRequest implements FetchRequest {

    private static final FetchProtocol FTP = () -> "ftp";

    private final URI uri;
    private final String username;
    private final String password;
    private final FtpTransferMode transferMode;

    public FtpFetchRequest(
            URI uri,
            String username,
            String password,
            FtpTransferMode transferMode
    ) {
        this.uri = uri;
        this.username = username;
        this.password = password;
        this.transferMode = transferMode;
    }

    @Override
    public URI resource() {
        return uri;
    }

    @Override
    public FetchProtocol protocol() {
        return FTP;
    }

    @Override
    public Map<String, List<String>> metadata() {
        return Collections.emptyMap();
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public FtpTransferMode transferMode() {
        return transferMode;
    }

}   // Class end.