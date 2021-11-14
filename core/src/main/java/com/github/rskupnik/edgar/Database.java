package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.DeviceStatus;

import java.util.Optional;

public interface Database {

    // TODO: Can probably be removed once toggle device stops using params.enabled
    void saveDeviceStatus(String deviceId, DeviceStatus status);
    void removeDeviceStatus(String deviceId);
    Optional<DeviceStatus> getDeviceStatus(String deviceId);
}
