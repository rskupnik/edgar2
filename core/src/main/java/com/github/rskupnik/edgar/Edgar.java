package com.github.rskupnik.edgar;

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
        return new EdgarImpl(deviceRepository, dashboardRepository);
    }
}