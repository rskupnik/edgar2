package com.github.rskupnik.edgar.db;

import com.github.rskupnik.edgar.Database;
import com.github.rskupnik.edgar.domain.Device;
import io.vavr.control.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryDatabase implements Database {

    private final Map<String, Device> devices = new HashMap<>();

    @Override
    public Option<Device> findDevice(String name) {
        return Option.of(devices.get(name));
    }

    @Override
    public void saveDevice(Device device) {
        devices.put(device.getName(), device);
    }

    @Override
    public List<Device> getAll() {
        return new ArrayList<>(devices.values());
    }

    @Override
    public void removeDevice(String name) {
        devices.remove(name);
    }
}
