package com.github.rskupnik.edgar.db;

import com.github.rskupnik.edgar.Database;
import com.github.rskupnik.edgar.domain.*;
import io.vavr.control.Option;

import java.util.*;

public class InMemoryDatabase implements Database {

    private final Map<String, Device> devices = new HashMap<>();
    private final Map<String, DeviceLayout> deviceLayouts = new HashMap<>();
    private final Map<String, DeviceStatus> deviceStatus = new HashMap<>();
    private final Map<String, ActivationPeriods> deviceActivationPeriods = new HashMap<>();
    private final Map<String, Dashboard> dashboards = new HashMap<>();

    @Override
    public Option<Device> findDevice(String id) {
        return Option.of(devices.get(id));
    }

    @Override
    public void saveDevice(Device device) {
        devices.put(device.getId(), device);
    }

    @Override
    public List<Device> getAll() {
        return new ArrayList<>(devices.values());
    }

    @Override
    public void removeDevice(String id) {
        devices.remove(id);
    }

    @Override
    public Option<DeviceLayout> findDeviceLayout(String id) {
        return Option.of(deviceLayouts.get(id));
    }

    @Override
    public void saveDeviceLayout(DeviceLayout deviceLayout) {
        deviceLayouts.put(deviceLayout.getId(), deviceLayout);
    }

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
}
