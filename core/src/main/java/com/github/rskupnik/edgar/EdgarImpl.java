package com.github.rskupnik.edgar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rskupnik.edgar.domain.*;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

class EdgarImpl implements Edgar {

    private final Database database;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EdgarImpl(Database database) {
        this.database = database;
    }

    @Override
    public Either<String, Device> registerDevice(Device device) {
        if (database.findDevice(device.getId()).isDefined()) {
            return Either.left("This device is already registered");
        }

        database.saveDevice(device);
        database.saveDeviceStatus(device.getId(), getStatus(device));
        return Either.right(device);
    }

    @Override
    public List<Device> getDevices() {
        return database.getAll();
    }

    @Override
    public boolean sendCommand(String deviceId, String commandName, Map<String, String> params) {
        // Find device
        final Device device = database.findDevice(deviceId).getOrNull();
        if (device == null) {
            System.out.println("This device doesn't exist");
            return false;   // TODO: Handle errors with Either
        }

        // Find the command in the device
        final DeviceEndpoint endpoint = device.getEndpoints().stream().filter(e -> e.getPath().equals(commandName)).findFirst().orElse(null);
        if (endpoint == null) {
            System.out.println("Endpoint " + commandName + " doesn't exist");
            return false;
        }

        // Filter out params that are not defined on the endpoint
        var filteredParams = params.entrySet().stream()
                .filter(e -> endpoint.getParams().stream().anyMatch(edp -> edp.getName().equals(e.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return sendCommand(device, endpoint, filteredParams);
    }

    @Override
    public void refreshDeviceStatus() {
        database.getAll().stream()
                .map(d -> new Tuple2<>(d, getStatus(d)))
                .forEach(d -> {
                    if (!d._2.isResponsive()) {
                        System.out.println("Removing device: " + d._1.getId() + " at IP " + d._1.getIp());
                        database.removeDevice(d._1.getId());
                    } else {
                        System.out.println("Saving device status for device " + d._1.getId());
                        database.saveDeviceStatus(d._1.getId(), d._2);
                    }
                });
    }

    @Override
    public void registerLayouts(List<DeviceLayout> layouts) {
        layouts.forEach(database::saveDeviceLayout);
    }

    @Override
    public List<Tuple2<Device, DeviceLayout>> getLayouts(List<Device> devices) {
        return devices.stream().map(d -> new Tuple2<>(d, database.findDeviceLayout(d.getId()).getOrElse(createDefaultLayout(d)))).collect(Collectors.toList());
    }

    @Override
    public Optional<DeviceStatus> getDeviceStatus(String deviceId) {
        return database.getDeviceStatus(deviceId);
    }

    @Override
    public void setActivationPeriods(String deviceId, List<ActivationPeriod> periods) {
        database.saveActivationPeriods(deviceId, new ActivationPeriods(periods));
        // TODO: An endpoint to set activation periods
        // TODO: A scheduled job that looks at activation periods and activates/deactivates device if needed
    }

    private DeviceLayout createDefaultLayout(Device device) {
        return new DeviceLayout(device.getId(), device.getEndpoints().stream().map(e -> new EndpointLayout(e.getPath(), "default", Collections.emptyList())).collect(Collectors.toList()));
    }

    private DeviceStatus getStatus(Device device) {
        var httpClient = HttpClients.createDefault();
        try (CloseableHttpResponse response = httpClient.execute(new HttpGet("http://" + device.getIp() + "/status"))) {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return new DeviceStatus(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK, Collections.emptyMap(), Collections.emptyMap());
            }
            String body = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            Map<String, String> params = objectMapper.readValue(body, new TypeReference<>() {});
            Map<String, Map<String, String>> endpointData = objectMapper.readValue(params.getOrDefault("endpoints", "{}"), new TypeReference<>() {});
            return new DeviceStatus(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK, params, endpointData);
        } catch (IOException e) {
            e.printStackTrace();
            return new DeviceStatus(false, Collections.emptyMap(), Collections.emptyMap());
        }
    }

    private boolean sendCommand(Device device, DeviceEndpoint endpoint, Map<String, String> params) {

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

        var httpClient = HttpClients.createDefault();
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            System.out.println("Sent request to: " + uri.toString());
            return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
