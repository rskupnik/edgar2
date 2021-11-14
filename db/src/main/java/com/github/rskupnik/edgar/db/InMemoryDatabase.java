package com.github.rskupnik.edgar.db;

 import com.github.rskupnik.edgar.Database;
import com.github.rskupnik.edgar.domain.DeviceStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryDatabase implements Database {

    private final Map<String, DeviceStatus> deviceStatus = new HashMap<>();

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
}
