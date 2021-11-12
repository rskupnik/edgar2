package com.github.rskupnik.edgar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rskupnik.edgar.db.entity.DeviceEntity;
import com.github.rskupnik.edgar.db.repository.DeviceRepository;
import com.github.rskupnik.edgar.domain.*;
import io.vavr.Tuple2;
import io.vavr.control.Either;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

class EdgarImpl implements Edgar {

    private final Database database;
    private final DeviceRepository deviceRepository;

    private final DeviceClient deviceClient = new ApacheHttpDeviceClient();
    private final ObjectMapper objectMapper = new ObjectMapper();


    public EdgarImpl(Database database, DeviceRepository deviceRepository) {
        this.database = database;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Either<String, Device> registerDevice(Device device) {
        if (deviceRepository.find(device.getId()).isPresent()) {
            return Either.left("This device is already registered");
        }

        deviceRepository.save(device.getId(), DeviceEntity.fromDomainObject(device));
        database.saveDeviceStatus(device.getId(), deviceClient.getStatus(device));
        return Either.right(device);
    }

    @Override
    public List<Device> getDevices() {
        refreshDeviceStatus();
        return deviceRepository.findAll().stream()
                .map(Device::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isDeviceResponsive(String deviceId) {
        return database.getDeviceResponsive(deviceId);
    }

    @Override
    public CommandResponse sendCommand(String deviceId, String commandName, Map<String, String> params) {
        // Find device
        final Device device = deviceRepository.find(deviceId).map(Device::fromEntity).orElse(null);
        if (device == null) {
            System.out.println("This device doesn't exist");
            return CommandResponse.error("This device doesn't exist");
        }

        // Find the command in the device
        final DeviceEndpoint endpoint = device.getEndpoints().stream().filter(e -> e.getPath().equals(commandName)).findFirst().orElse(null);
        if (endpoint == null) {
            System.out.println("Endpoint " + commandName + " doesn't exist");
            return CommandResponse.error("Endpoint " + commandName + " doesn't exist");
        }

        // Filter out params that are not defined on the endpoint
        var filteredParams = params.entrySet().stream()
                .filter(e -> endpoint.getParams().stream().anyMatch(edp -> edp.getName().equals(e.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // If this endpoint is cachable, check cache first
        int endpointCacheTime = getEndpointCacheTime(deviceId, endpoint.getPath());
        if (endpointCacheTime > 0) {
            var cachedResponse = database.getCachedCommandResponse(device, endpoint, endpointCacheTime);
            if (cachedResponse.isPresent()) {
                System.out.println("Returning from cache");
                return cachedResponse.get();
            }
        }

        System.out.println("Making a new call");
        // Cache the response if needed
        var response =  sendCommand(device, endpoint, filteredParams);
        if (endpointCacheTime > 0) {
            System.out.println("Caching response");
            database.cacheCommandResponse(device, endpoint, response);
        }

        return response;
    }

    @Override
    public void refreshDeviceStatus() {
        deviceRepository.findAll()
                .stream()
                .map(Device::fromEntity)
                .filter(d -> getStatusCheckEnabled(d.getId()))
                .map(d -> new Tuple2<>(d, deviceClient.getStatus(d)))
                .forEach(d -> {
                    if (!d._2.isResponsive()) {
                        System.out.println("Removing device: " + d._1.getId() + " at IP " + d._1.getIp());
                        deviceRepository.delete(d._1.getId());
                    } else {
                        System.out.println("Saving device status for device " + d._1.getId());
                        database.saveDeviceStatus(d._1.getId(), d._2);
                    }
                });
    }

    @Override
    public void rediscoverUnresponsiveDevices() {
        deviceRepository.findAll()
                .stream()
                .map(Device::fromEntity)
                .filter(d -> !database.getDeviceResponsive(d.getId()))
                .map(d -> new Tuple2<>(d, deviceClient.getStatus(d)))
                .forEach(d -> database.markDeviceResponsive(d._1.getId(), d._2.isResponsive()));
    }

//    @Override
//    public void registerLayouts(List<DeviceLayout> layouts) {
//        layouts.forEach(database::saveDeviceLayout);
//    }

//    @Override
//    public List<Tuple2<Device, DeviceLayout>> getLayouts(List<Device> devices) {
//        return devices.stream().map(d -> new Tuple2<>(d, database.findDeviceLayout(d.getId()).getOrElse(createDefaultLayout(d)))).collect(Collectors.toList());
//    }

    @Override
    public Optional<DeviceStatus> getDeviceStatus(String deviceId) {
        return database.getDeviceStatus(deviceId);
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

    @Override
    public void loadDeviceConfig(String filename) {
        if (filename == null || !Files.exists(Path.of(filename)))
            return;

        try {
            var content = Files.readString(Path.of(filename));
            var converted = objectMapper.readValue(content, new TypeReference<HashMap<String, Object>>() {});
            database.saveDeviceConfig(converted);
            System.out.println("Loaded Device Config");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: Do this in a better way
    private Dashboard fromMap(Map<String, Object> map) {
        var tiles = (List<Map<String, Object>>) map.get("tiles");
        return new Dashboard(tiles.stream().map(t -> new Tile(
                (String) t.get("name"),
                (String) t.get("deviceId"),
                (String) t.get("endpointId"),
                (String) t.get("deviceType"),
                (int) t.get("x"), (int) t.get("y"),
                (String) t.get("type"),
                (Map<String, Object>) t.getOrDefault("properties", Collections.emptyMap())
        )).collect(Collectors.toList()));
    }

    private CommandResponse sendCommand(Device device, DeviceEndpoint endpoint, Map<String, String> params) {
        var response = deviceClient.sendCommand(device, endpoint, params);
        database.markDeviceResponsive(device.getId(), !response.isError());
        return response;
    }

    private DeviceLayout createDefaultLayout(Device device) {
        return new DeviceLayout(device.getId(), device.getEndpoints().stream().map(e -> new EndpointLayout(e.getPath(), "default", Collections.emptyList())).collect(Collectors.toList()));
    }

    private boolean getStatusCheckEnabled(String deviceId) {
        // TODO: Jesus Christ, blast this abomination back to hell
        var deviceConfig = database.getDeviceConfig();
        var deviceObj = deviceConfig.get(deviceId);
        if (deviceObj == null) {
            return true;
        }

        var statusCheckEnabled = ((Map<String, Object>) deviceObj).get("statusCheckEnabled");
        return statusCheckEnabled == null || (boolean) statusCheckEnabled;
    }

    private int getEndpointCacheTime(String deviceId, String endpointPath) {
        // TODO: Jesus Christ, blast this abomination back to hell
        var deviceConfig = database.getDeviceConfig();
        var deviceObj = deviceConfig.get(deviceId);
        if (deviceObj == null) {
            return 0;
        }

        var allEndpointsObj = ((Map<String, Object>) deviceObj).get("endpoints");
        if (allEndpointsObj == null) {
            return 0;
        }

        var endpointObj = ((Map<String, Object>) allEndpointsObj).get(endpointPath);
        if (endpointObj == null) {
            return 0;
        }

        var cachePeriod = ((Map<String, Object>) endpointObj).get("cachePeriod");
        if (cachePeriod != null) {
            return (int) cachePeriod;
        }

        return 0;
    }
}
