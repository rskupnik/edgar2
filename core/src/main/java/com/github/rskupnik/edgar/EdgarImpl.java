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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

class EdgarImpl implements Edgar {

    private final Database database;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Device> autoActivatedDevices = new ArrayList<>();

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
        // TODO: Validate - hours: 0-23, minutes: 0-59, cannot overlap
        database.saveActivationPeriods(deviceId, new ActivationPeriods(periods));
    }

    @Override
    public void checkActivationPeriods() {
        // TODO: Go through all devices' activation periods and (de)activate them if needed
        database.getAll().stream()
                .map(d -> new Pair<>(database.findDevice(d.getId()), database.getActivationPeriods(d.getId())))
                .filter(p -> p.getRight().isPresent() && p.getLeft().isDefined())
                .forEach(p -> resolveActivationPeriod(p.left.get(), p.right.get()));
    }

    @Override
    public void loadDashboard(String id, String filename) {
        if (filename == null || !Files.exists(Path.of(filename)))
            return;

        try {
            var content = Files.readString(Path.of(filename));
            var converted = objectMapper.readValue(content, new TypeReference<HashMap<String, Object>>() {});
            database.saveDashboard(id, fromMap(converted));
            System.out.println("Loaded dashboard: " + id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Dashboard> getDashboard(String id) {
        return database.getDashboard(id);
    }

    // TODO: Do this in a better way
    private Dashboard fromMap(Map<String, Object> map) {
        var tiles = (List<Map<String, Object>>) map.get("tiles");
        return new Dashboard(tiles.stream().map(t -> new Tile(
                (String) t.get("deviceId"),
                (String) t.get("deviceType"),
                (int) t.get("x"), (int) t.get("y"),
                (String) t.get("type")
        )).collect(Collectors.toList()));
    }

    private void resolveActivationPeriod(Device device, ActivationPeriods activationPeriods) {
        if (autoActivatedDevices.contains(device)) {
            checkDeactivation(device, activationPeriods);
        } else {
            checkActivation(device, activationPeriods);
        }
    }

    private void checkDeactivation(Device device, ActivationPeriods activationPeriods) {
        LocalDateTime ldt = LocalDateTime.now();
        for (ActivationPeriod period : activationPeriods.getPeriods()) {
            if (ldt.getHour() > period.getEndHour() || (period.getEndHour() == ldt.getHour() && ldt.getMinute() >= period.getEndMinute())) {
                autoActivatedDevices.remove(device);
                deactivate(device);
            }
        }
    }

    private void deactivate(Device device) {
        for (DeviceEndpoint endpoint : device.getEndpoints()) {
            if (endpoint.getPath().equals("/off")) {
                sendCommand(device, endpoint, Collections.emptyMap());
            }
        }
    }

    private void checkActivation(Device device, ActivationPeriods activationPeriods) {
        LocalDateTime ldt = LocalDateTime.now();
        for (ActivationPeriod period : activationPeriods.getPeriods()) {
            if (
                    (ldt.getHour() > period.getStartHour() || (ldt.getHour() == period.getStartHour() || ldt.getMinute() >= period.getStartMinute()))
                    && (ldt.getHour() < period.getEndHour() || (ldt.getHour() == period.getEndHour() && ldt.getMinute() <= period.getEndMinute()))
            ) {
                autoActivatedDevices.add(device);
                activate(device);
            }
        }
    }

    private void activate(Device device) {
        for (DeviceEndpoint endpoint : device.getEndpoints()) {
            if (endpoint.getPath().equals("/on")) {
                sendCommand(device, endpoint, Collections.emptyMap());
            }
        }
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

    private static class Pair<L, R> {

        private final L left;
        private final R right;

        private Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public L getLeft() {
            return left;
        }

        public R getRight() {
            return right;
        }
    }
}
