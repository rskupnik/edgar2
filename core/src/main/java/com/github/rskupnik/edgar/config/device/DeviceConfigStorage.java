package com.github.rskupnik.edgar.config.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DeviceConfigStorage {

    private final Map<String, DeviceConfig> config = new HashMap<>();

    public void save(DeviceConfig config) {
        this.config.put(config.getId(), config);
    }

    public void save(List<DeviceConfig> configs) {
        configs.forEach(c -> config.put(c.getId(), c));
    }

    public Optional<DeviceConfig> get(String id) {
        return Optional.ofNullable(config.get(id));
    }
}
