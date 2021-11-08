package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.*;
import io.vavr.control.Option;

import java.util.List;
import java.util.Map;
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

    void saveActivationPeriods(String deviceId, ActivationPeriods activationPeriods);
    Optional<ActivationPeriods> getActivationPeriods(String deviceId);

    void saveDashboard(String dashboardId, Dashboard dashboard);
    Optional<Dashboard> getDashboard(String id);

    void saveDeviceConfig(Map<String, Object> config);
    Map<String, Object> getDeviceConfig();

    void markDeviceResponsive(String deviceId, boolean responsive);
    boolean getDeviceResponsive(String deviceId);

    // TODO: Move this to a separate object that deals with cache?
    void cacheCommandResponse(Device device, DeviceEndpoint endpoint, CommandResponse commandResponse);
    Optional<CommandResponse> getCachedCommandResponse(Device device, DeviceEndpoint endpoint, int maxSecondsAgo);
}
