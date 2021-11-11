package com.github.rskupnik.edgar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rskupnik.edgar.domain.CommandResponse;
import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceEndpoint;
import com.github.rskupnik.edgar.domain.DeviceStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// TODO: Switch to okHttp + Retrofit?
// TODO: Use

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

    private final ObjectMapper objectMapper = new ObjectMapper();

    // TODO: Use isAlive instead
    @Override
    public DeviceStatus getStatus(Device device) {
        System.out.println("Getting status for " + device.getId());
        try (CloseableHttpResponse response = statusHttpClient.execute(new HttpGet("http://" + device.getIp() + "/status"))) {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return new DeviceStatus(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK, Collections.emptyMap(), Collections.emptyMap());
            }
            String body = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            Map<String, String> params = objectMapper.readValue(body, new TypeReference<HashMap<String, String>>() {});
            Map<String, Map<String, String>> endpointData = objectMapper.readValue(params.getOrDefault("endpoints", "{}"), new TypeReference<HashMap<String, Map<String, String>>>() {});

            return new DeviceStatus(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK, params, endpointData);
        } catch (IOException e) {
            e.printStackTrace();
            return new DeviceStatus(false, Collections.emptyMap(), Collections.emptyMap());
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
