package io.argus.ingestion.fetch.protocol.ftp;

import io.argus.ingestion.error.FetchFailedException;
import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.fetch.FetchResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:46
 */
public class FtpFetchExecutor implements FetchExecutor {

    private static final FetchProtocol FTP = () -> "ftp";
    private static final int CONNECT_TIMEOUT_MILLIS = 10_000;
    private static final int READ_TIMEOUT_MILLIS = 30_000;

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

        try {
            URI target = normalizeUri(ftpRequest);
            URLConnection connection = target.toURL().openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
            connection.setReadTimeout(READ_TIMEOUT_MILLIS);

            byte[] body;
            try (InputStream input = connection.getInputStream()) {
                body = readAllBytes(input);
            }

            return new FtpFetchResult(
                    true,
                    body,
                    connection.getHeaderField(0)
            );

        } catch (IOException e) {
            throw new FetchFailedException(
                    "FTP fetch failed for " + ftpRequest.resource(),
                    e
            );
        } catch (URISyntaxException e) {
            throw new FetchFailedException(
                    "Invalid FTP URI for " + ftpRequest.resource(),
                    e
            );
        }
    }

    private URI normalizeUri(FtpFetchRequest request) throws URISyntaxException {

        URI resource = request.resource();
        String userInfo = resource.getUserInfo();

        if ((userInfo == null || userInfo.isEmpty())
                && request.username() != null
                && !request.username().isEmpty()) {
            userInfo = request.username();
            if (request.password() != null && !request.password().isEmpty()) {
                userInfo = userInfo + ":" + request.password();
            }
        }

        String path = resource.getRawPath();
        if (path == null || path.isEmpty()) {
            path = "/";
        }

        if (!path.contains(";type=")) {
            path = path + transferModeSuffix(request.transferMode());
        }

        return new URI(
                resource.getScheme(),
                userInfo,
                resource.getHost(),
                resource.getPort(),
                path,
                resource.getRawQuery(),
                resource.getRawFragment()
        );
    }

    private String transferModeSuffix(FtpTransferMode mode) {
        return mode == FtpTransferMode.ASCII ? ";type=a" : ";type=i";
    }

    private byte[] readAllBytes(InputStream input) throws IOException {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;

        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }

        return output.toByteArray();
    }

}   // Class end.
