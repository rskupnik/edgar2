package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.CommandResponse;
import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceEndpoint;
import com.github.rskupnik.edgar.domain.DeviceStatus;

import java.util.Optional;

public interface Database {

    // TODO: Can probably be removed once toggle device stops using params.enabled
    void saveDeviceStatus(String deviceId, DeviceStatus status);
    void removeDeviceStatus(String deviceId);
    Optional<DeviceStatus> getDeviceStatus(String deviceId);


    // TODO: Move this to a separate object that deals with cache?
    void cacheCommandResponse(Device device, DeviceEndpoint endpoint, CommandResponse commandResponse);
    Optional<CommandResponse> getCachedCommandResponse(Device device, DeviceEndpoint endpoint, int maxSecondsAgo);
}
