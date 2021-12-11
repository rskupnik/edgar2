package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.CommandResponse;
import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceEndpoint;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

// TODO: Switch to okHttp + Retrofit?

class ApacheHttpDeviceClient implements DeviceClient {

    private static final Logger logger = LoggerFactory.getLogger(ApacheHttpDeviceClient.class);

    private final HttpClient statusHttpClient;
    private final HttpClient commandHttpClient;

    ApacheHttpDeviceClient(HttpClient statusClient, HttpClient commandClient) {
        this.statusHttpClient = statusClient;
        this.commandHttpClient = commandClient;
    }

    @Override
    public boolean isAlive(Device device) {
        logger.debug("Checking if " + device.getId() + " is alive");
        try {
            HttpResponse response = statusHttpClient.execute(new HttpGet("http://" + device.getIp() + "/isAlive"));
            return response != null && response.getStatusLine().getStatusCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CommandResponse sendCommand(Device device, DeviceEndpoint endpoint, Map<String, String> params) {

        URI uri = null;
        try {
            var uriBuilder = new URIBuilder("http://" + device.getIp() + endpoint.getPath());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        var request = switch (endpoint.getMethod()) {
            case POST -> new HttpPost(uri);
            case GET -> new HttpGet(uri);
            case PUT -> new HttpPut(uri);
            case DELETE -> new HttpDelete(uri);
        };

        try {
            HttpResponse response = commandHttpClient.execute(request);
            logger.debug("Sent request to: " + uri.toString());
            return CommandResponse.fromApacheResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return CommandResponse.error("Unknown error");
    }
}
