package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceLayout;
import com.github.rskupnik.edgar.domain.DeviceStatus;
import io.vavr.control.Option;

import java.util.List;
import java.util.Optional;

public interface Database {

    Option<Device> findDevice(String id);
    void saveDevice(Device device);
    List<Device> getAll();
    void removeDevice(String id);

    Option<DeviceLayout> findDeviceLayout(String id);
    void saveDeviceLayout(DeviceLayout deviceLayout);

    void saveDeviceStatus(String deviceId, DeviceStatus status);
    void removeDeviceStatus(String deviceId);
    Optional<DeviceStatus> getDeviceStatus(String deviceId);
}
