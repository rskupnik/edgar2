package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.config.device.DeviceConfig;
import com.github.rskupnik.edgar.config.device.DeviceConfigStorage;
import com.github.rskupnik.edgar.config.device.EndpointConfig;
import com.github.rskupnik.edgar.db.repository.DashboardRepository;
import com.github.rskupnik.edgar.db.repository.DeviceRepository;
import com.github.rskupnik.edgar.domain.*;
import io.vavr.Tuple2;
import io.vavr.control.Either;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Edgar {

    Either<String, Device> registerDevice(Device device);
    List<Device> getDevices();
    CommandResponse sendCommand(String deviceId, String commandName, Map<String, String> params);
    void loadDashboard(String name, String filename);
    Optional<Dashboard> getDashboard(String id);
    void loadDeviceConfig(String filename);
    void rediscoverUnresponsiveDevices();

    static Edgar defaultImplementation( DeviceRepository deviceRepository, DashboardRepository dashboardRepository) {
        final DeviceConfigStorage deviceConfigStorage = new DeviceConfigStorage();
        return new EdgarImpl(
                deviceRepository, dashboardRepository,
                deviceConfigStorage,
                new CachedDeviceClient(
                    new ApacheHttpDeviceClient(),
                    (key) -> {
                        String[] split = key.split(":");
                        var deviceId = split[0];
                        var endpointPath = split[1];
                        return deviceConfigStorage.get(deviceId).orElse(DeviceConfig.empty()).getEndpoints()
                                .stream()
                                .filter(c -> c.getPath().equals(endpointPath))
                                .findFirst()
                                .orElse(EndpointConfig.empty())
                                .getCachePeriod() * 1000;
                    }
                )
        );
    }
}