package com.github.rskupnik.edgar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rskupnik.edgar.config.device.DeviceConfig;
import com.github.rskupnik.edgar.config.device.DeviceConfigStorage;
import com.github.rskupnik.edgar.config.device.EndpointConfig;
import com.github.rskupnik.edgar.db.entity.DashboardEntity;
import com.github.rskupnik.edgar.db.entity.DeviceEntity;
import com.github.rskupnik.edgar.db.repository.DashboardRepository;
import com.github.rskupnik.edgar.db.repository.DeviceRepository;
import com.github.rskupnik.edgar.domain.CommandResponse;
import com.github.rskupnik.edgar.domain.Dashboard;
import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceEndpoint;
import io.vavr.Tuple2;
import io.vavr.control.Either;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class EdgarImpl implements Edgar {

//    private final Database database;
    private final DeviceRepository deviceRepository;
    private final DashboardRepository dashboardRepository;

    private final DeviceConfigStorage deviceConfigStorage = new DeviceConfigStorage();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final DeviceClient deviceClient = new CachedDeviceClient(
            new ApacheHttpDeviceClient(),
            (key) -> getEndpointCacheTime(key) * 1000
    );


    public EdgarImpl(DeviceRepository deviceRepository, DashboardRepository dashboardRepository) {
//        this.database = database;
        this.deviceRepository = deviceRepository;
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    public Either<String, Device> registerDevice(Device device) {
        var deviceEntity = deviceRepository.find(device.getId());
        if (deviceEntity.isPresent()) {
            deviceEntity.get().setResponsive(true);
            deviceRepository.save(device.getId(), deviceEntity.get());
        } else {
            deviceRepository.save(device.getId(), DeviceEntity.fromDomainObject(device));
//            database.saveDeviceStatus(device.getId(), deviceClient.getStatus(device));  // TODO: Get rid of this and then simplify?
        }
        return Either.right(device);
    }

    @Override
    public List<Device> getDevices() {
        //refreshDeviceStatus();
        return deviceRepository.findAll().stream()
                .map(Device::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isDeviceResponsive(String deviceId) {
        return deviceRepository.find(deviceId).map(DeviceEntity::isResponsive).orElse(false);
    }

    @Override
    public CommandResponse sendCommand(String deviceId, String commandName, Map<String, String> params) {
        // Find device
        final Device device = deviceRepository.find(deviceId).map(Device::fromEntity).orElse(null);
        if (device == null) {
            System.out.println("This device doesn't exist");
            return CommandResponse.error("This device doesn't exist");
        }

        // Reject if device is unresponsive
        if (!device.isResponsive()) {
            System.out.println("Rejecting command call because device is unresponsive");
            return CommandResponse.error("This device is unresponsive");
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
//        int endpointCacheTime = getEndpointCacheTime(deviceId, endpoint.getPath());
//        if (endpointCacheTime > 0) {
//            var cachedResponse = database.getCachedCommandResponse(device, endpoint, endpointCacheTime);
//            if (cachedResponse.isPresent()) {
//                System.out.println("Returning from cache");
//                return cachedResponse.get();
//            }
//        }

//        System.out.println("Making a new call");
        // Cache the response if needed
//        var response =  sendCommand(device, endpoint, filteredParams);
//        if (endpointCacheTime > 0) {
//            System.out.println("Caching response");
//            database.cacheCommandResponse(device, endpoint, response);
//        }

        var response = deviceClient.sendCommand(device, endpoint, filteredParams);

        if (device.isResponsive() == response.isError()) {  // Only change when they differ
            handleDeviceResponsivenessChange(device.getId(), !response.isError());
        }

        if (device.isResponsive()) {
            deviceRepository.find(device.getId()).ifPresent(e -> {
                e.setLastSuccessResponseTimestamp(Instant.now().toEpochMilli());
                deviceRepository.save(e.getId(), e);
            });
        }

        return response;
    }

//    @Override
//    public void refreshDeviceStatus() {
//        deviceRepository.findAll()
//                .stream()
//                .map(Device::fromEntity)
//                .filter(d -> deviceConfigStorage.get(d.getId()).orElse(DeviceConfig.empty()).isStatusCheckEnabled())
//                .map(d -> new Tuple2<>(d, deviceClient.getStatus(d)))
//                .forEach(d -> {
//                    if (!d._2.isResponsive()) {
//                        System.out.println("Removing device: " + d._1.getId() + " at IP " + d._1.getIp());
//                        deviceRepository.delete(d._1.getId());
//                    } else {
//                        System.out.println("Saving device status for device " + d._1.getId());
//                        database.saveDeviceStatus(d._1.getId(), d._2);
//                    }
//                });
//    }

    @Override
    public void rediscoverUnresponsiveDevices() {
//        deviceRepository.findAll()
//                .stream()
//                .filter(d -> !d.isResponsive())
//                .map(d -> new Tuple2<>(d, deviceClient.getStatus(Device.fromEntity(d))))
//                .forEach(d -> {
//                    d._1.setResponsive(d._2.isResponsive());
//                    deviceRepository.save(d._1.getId(), d._1);
//                });
        deviceRepository.findAll()
                .stream()
                .filter(d -> !d.isResponsive())
                .map(d -> new Tuple2<>(deviceConfigStorage.get(d.getId()), deviceRepository.find(d.getId())))
                .filter(t -> t._1.isPresent() && t._2.isPresent())
                .map(t -> new Tuple2<>(t._1.get(), t._2.get()))
                .filter(t -> Instant.now().toEpochMilli() - (t._1.getUnresponsiveTimeout() * 1000) > t._2.getLastSuccessResponseTimestamp())
                .forEach(t -> {
                    System.out.println("Removing unresponsive device: " + t._1.getId());
                    deviceRepository.delete(t._1.getId());
                });

        deviceRepository.findAll()
                .stream()
                .filter(d -> !d.isResponsive())
                .map(d -> new Tuple2<>(d, deviceClient.isAlive(Device.fromEntity(d))))
                .forEach(d -> {
                    d._1.setResponsive(d._2);
                    deviceRepository.save(d._1.getId(), d._1);
                });
    }

//    @Override
//    public void registerLayouts(List<DeviceLayout> layouts) {
//        layouts.forEach(database::saveDeviceLayout);
//    }

//    @Override
//    public List<Tuple2<Device, DeviceLayout>> getLayouts(List<Device> devices) {
//        return devices.stream().map(d -> new Tuple2<>(d, database.findDeviceLayout(d.getId()).getOrElse(createDefaultLayout(d)))).collect(Collectors.toList());
//    }

//    @Override
//    public Optional<DeviceStatus> getDeviceStatus(String deviceId) {
//        return database.getDeviceStatus(deviceId);
//    }

    @Override
    public void loadDashboard(String id, String filename) {
        loadFile(filename, new TypeReference<DashboardEntity>() {}).ifPresent(f -> dashboardRepository.save(id, f));
        System.out.println("Loaded Dashboard: " + id);
    }

    @Override
    public Optional<Dashboard> getDashboard(String id) {
        return dashboardRepository.find(id).map(Dashboard::fromEntity);
    }

    @Override
    public void loadDeviceConfig(String filename) {
        loadFile(filename, new TypeReference<List<DeviceConfig>>() {}).ifPresent(deviceConfigStorage::save);
        System.out.println("Loaded Device Config");
    }

    private <T> Optional<T> loadFile(String filename, TypeReference<T> ref) {
        if (filename == null || !Files.exists(Path.of(filename)))
            return Optional.empty();

        try {
            var content = Files.readString(Path.of(filename));
            return Optional.of(objectMapper.readValue(content, ref));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private int getEndpointCacheTime(String cacheKey) {
        String[] split = cacheKey.split(":");
        var deviceId = split[0];
        var endpointPath = split[1];
        return deviceConfigStorage.get(deviceId).orElse(DeviceConfig.empty()).getEndpoints()
                .stream()
                .filter(c -> c.getPath().equals(endpointPath))
                .findFirst()
                .orElse(EndpointConfig.empty())
                .getCachePeriod();
    }

    private void handleDeviceResponsivenessChange(String id, boolean responsive) {
        deviceRepository.find(id).ifPresent(e -> {
            e.setResponsive(responsive);
            deviceRepository.save(id, e);
        });
    }
}
