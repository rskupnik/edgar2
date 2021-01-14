package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceEndpoint;
import io.vavr.control.Either;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

class EdgarImpl implements Edgar {

    private final Database database;

    public EdgarImpl(Database database) {
        this.database = database;
    }

    @Override
    public Either<String, Device> registerDevice(Device device) {
        if (database.findDevice(device.getName()).isDefined()) {
            return Either.left("This device is already registered");
        }

        database.saveDevice(device);
        return Either.right(device);
    }

    @Override
    public List<Device> getDevices() {
        return database.getAll();
    }

    @Override
    public boolean sendCommand(String deviceName, String commandName, Map<String, String> params) {
        // Find device
        final Device device = database.findDevice(deviceName).getOrNull();
        if (device == null) {
            return false;   // TODO: Handle errors with Either
        }

        // Find the command in the device
        final DeviceEndpoint endpoint = device.getEndpoints().stream().filter(e -> e.getPath().equals(commandName)).findFirst().orElse(null);
        if (endpoint == null) {
            return false;
        }

        return sendCommand(device, endpoint, params);
    }

    private boolean sendCommand(Device device, DeviceEndpoint endpoint, Map<String, String> params) {

        URI uri = null;
        try {
            var uriBuilder = new URIBuilder("http://" + device.getIp() + "/" + endpoint.getPath());
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

        var httpClient = HttpClients.createDefault();
        try (CloseableHttpResponse response = httpClient.execute(request)) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
