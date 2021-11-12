package com.github.rskupnik.edgar.db;

import com.github.rskupnik.edgar.Database;
import com.github.rskupnik.edgar.domain.*;
import io.vavr.control.Option;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class InMemoryDatabase implements Database {

    //private final Map<String, Device> devices = new HashMap<>();
//    private final Map<String, DeviceLayout> deviceLayouts = new HashMap<>();
    private final Map<String, DeviceStatus> deviceStatus = new HashMap<>();
    private final Map<String, ActivationPeriods> deviceActivationPeriods = new HashMap<>();
    private final Map<String, Dashboard> dashboards = new HashMap<>();
    private final Map<String, Map<String, CachedCommandResponse>> commandResponseCache = new HashMap<>();
    private final Map<String, Boolean> deviceResponsiveness = new HashMap<>();
    private final Map<String, Object> deviceConfig = new HashMap<>();

//    @Override
//    public Option<Device> findDevice(String id) {
//        return Option.of(devices.get(id));
//    }
//
//    @Override
//    public void saveDevice(Device device) {
//        devices.put(device.getId(), device);
//    }
//
//    @Override
//    public List<Device> getAll() {
//        return new ArrayList<>(devices.values());
//    }
//
//    @Override
//    public void removeDevice(String id) {
//        devices.remove(id);
//    }

//    @Override
//    public Option<DeviceLayout> findDeviceLayout(String id) {
//        return Option.of(deviceLayouts.get(id));
//    }
//
//    @Override
//    public void saveDeviceLayout(DeviceLayout deviceLayout) {
//        deviceLayouts.put(deviceLayout.getId(), deviceLayout);
//    }

    @Override
    public void saveDeviceStatus(String deviceId, DeviceStatus status) {
        deviceStatus.put(deviceId, status);
    }

    @Override
    public void removeDeviceStatus(String deviceId) {
        deviceStatus.remove(deviceId);
    }

    @Override
    public Optional<DeviceStatus> getDeviceStatus(String deviceId) {
        return Optional.ofNullable(deviceStatus.get(deviceId));
    }

    @Override
    public void saveActivationPeriods(String deviceId, ActivationPeriods activationPeriods) {
        deviceActivationPeriods.put(deviceId, activationPeriods);
    }

    @Override
    public Optional<ActivationPeriods> getActivationPeriods(String deviceId) {
        return Optional.ofNullable(deviceActivationPeriods.get(deviceId));
    }

    @Override
    public void saveDashboard(String dashboardId, Dashboard dashboard) {
        dashboards.put(dashboardId, dashboard);
    }

    @Override
    public Optional<Dashboard> getDashboard(String id) {
        return Optional.ofNullable(dashboards.get(id));
    }

    @Override
    public void cacheCommandResponse(Device device, DeviceEndpoint endpoint, CommandResponse commandResponse) {
        commandResponseCache.putIfAbsent(device.getId(), new HashMap<>());
        var cachedEndpoints = commandResponseCache.get(device.getId());
        cachedEndpoints.put(endpoint.getPath(), new CachedCommandResponse(commandResponse, Instant.now().toEpochMilli()));
    }

    @Override
    public Optional<CommandResponse> getCachedCommandResponse(Device device, DeviceEndpoint endpoint, int maxSecondsAgo) {
        var cachedEndpoints = commandResponseCache.get(device.getId());
        if (cachedEndpoints == null) {
            return Optional.empty();
        }

        var cachedResponse = cachedEndpoints.get(endpoint.getPath());
        if (cachedResponse == null || Instant.now().toEpochMilli() - (maxSecondsAgo * 1000) > cachedResponse.getTimestamp()) {
            return Optional.empty();
        } else return Optional.of(cachedResponse.commandResponse);
    }

    @Override
    public void markDeviceResponsive(String deviceId, boolean responsive) {
        deviceResponsiveness.put(deviceId, responsive);
    }

    @Override
    public boolean getDeviceResponsive(String deviceId) {
        return deviceResponsiveness.getOrDefault(deviceId, true);
    }

    @Override
    public void saveDeviceConfig(Map<String, Object> config) {
        this.deviceConfig.putAll(config);
    }

    @Override
    public Map<String, Object> getDeviceConfig() {
        // TODO: This return the real map underneath instead of a copy
        return deviceConfig;
    }

    // TODO: Move to Cache
    private class CachedCommandResponse {
        private final CommandResponse commandResponse;
        private final long timestamp;

        public CachedCommandResponse(CommandResponse commandResponse, long timestamp) {
            this.commandResponse = commandResponse;
            this.timestamp = timestamp;
        }

        public CommandResponse getCommandResponse() {
            return commandResponse;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
