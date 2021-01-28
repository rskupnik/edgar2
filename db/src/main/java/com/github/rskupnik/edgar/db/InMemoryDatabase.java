package com.github.rskupnik.edgar.db;

import com.github.rskupnik.edgar.Database;
import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceLayout;
import io.vavr.control.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryDatabase implements Database {

    private final Map<String, Device> devices = new HashMap<>();
    private final Map<String, DeviceLayout> deviceLayouts = new HashMap<>();

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
}
