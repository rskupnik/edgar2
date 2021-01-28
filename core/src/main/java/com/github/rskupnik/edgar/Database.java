package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceLayout;
import io.vavr.control.Option;

import java.util.List;

public interface Database {

    Option<Device> findDevice(String id);
    void saveDevice(Device device);
    List<Device> getAll();
    void removeDevice(String id);

    Option<DeviceLayout> findDeviceLayout(String id);
    void saveDeviceLayout(DeviceLayout deviceLayout);
}
