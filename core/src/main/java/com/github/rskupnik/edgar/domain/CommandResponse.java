package com.github.rskupnik.edgar.domain;

import com.github.rskupnik.edgar.DeviceResponse;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandResponse implements DeviceResponse {

    private final int statusCode;
    private final byte[] body;
    private final Map<String, String> headers;

    public CommandResponse(int statusCode, byte[] body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public static CommandResponse error(String msg) {
        return new CommandResponse(
                500,
                msg.getBytes(),
                Collections.emptyMap()
        );
    }

    public static CommandResponse fromApacheResponse(CloseableHttpResponse response) throws IOException {
        return new CommandResponse(
                response.getStatusLine().getStatusCode(),
                EntityUtils.toByteArray(response.getEntity()),
                Arrays.stream(response.getAllHeaders()).collect(Collectors.toMap(Header::getName, Header::getValue))
        );
    }

    public boolean isError() {
        return statusCode >= 400;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public byte[] getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
