package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.CommandResponse;
import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceEndpoint;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

// TODO: Switch to okHttp + Retrofit?

class ApacheHttpDeviceClient implements DeviceClient {

    private final CloseableHttpClient statusHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(
            RequestConfig.custom()
                    .setConnectTimeout(2000)
                    .setConnectionRequestTimeout(2000)
                    .setSocketTimeout(2000).build()
    ).build();
    private final CloseableHttpClient commandHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(
            RequestConfig.custom()
                    .setConnectTimeout(4000)
                    .setConnectionRequestTimeout(4000)
                    .setSocketTimeout(4000).build()
    ).build();

    @Override
    public boolean isAlive(Device device) {
        System.out.println("Checking if " + device.getId() + " is alive");
        try (CloseableHttpResponse response = statusHttpClient.execute(new HttpGet("http://" + device.getIp() + "/isAlive"))) {
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

        try (CloseableHttpResponse response = commandHttpClient.execute(request)) {
            System.out.println("Sent request to: " + uri.toString());
            //database.markDeviceResponsive(device.getId(), true);
            return CommandResponse.fromApacheResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            //database.markDeviceResponsive(device.getId(), false);
        }

        return CommandResponse.error("Unknown error");
    }
}
