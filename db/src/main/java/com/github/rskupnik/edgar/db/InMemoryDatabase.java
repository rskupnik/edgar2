package com.github.rskupnik.edgar.db;

import com.github.rskupnik.edgar.Database;
import com.github.rskupnik.edgar.domain.CommandResponse;
import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceEndpoint;
import com.github.rskupnik.edgar.domain.DeviceStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryDatabase implements Database {

    private final Map<String, DeviceStatus> deviceStatus = new HashMap<>();
    private final Map<String, Map<String, CachedCommandResponse>> commandResponseCache = new HashMap<>();

    @Override
    public void saveDeviceStatus(String deviceId, DeviceStatus status) {
        deviceStatus.put(deviceId, status);
    }

    @Override
    public void removeDeviceStatus(String deviceId) {
        deviceStatus.remove(deviceId);
    }

    @Override
    public Optional<DeviceStatus> getDeviceStatus(String deviceId) {
        return Optional.ofNullable(deviceStatus.get(deviceId));
    }

    @Override
    public void cacheCommandResponse(Device device, DeviceEndpoint endpoint, CommandResponse commandResponse) {
        commandResponseCache.putIfAbsent(device.getId(), new HashMap<>());
        var cachedEndpoints = commandResponseCache.get(device.getId());
        cachedEndpoints.put(endpoint.getPath(), new CachedCommandResponse(commandResponse, Instant.now().toEpochMilli()));
    }

    @Override
    public Optional<CommandResponse> getCachedCommandResponse(Device device, DeviceEndpoint endpoint, int maxSecondsAgo) {
        var cachedEndpoints = commandResponseCache.get(device.getId());
        if (cachedEndpoints == null) {
            return Optional.empty();
        }

        var cachedResponse = cachedEndpoints.get(endpoint.getPath());
        if (cachedResponse == null || Instant.now().toEpochMilli() - (maxSecondsAgo * 1000) > cachedResponse.getTimestamp()) {
            return Optional.empty();
        } else return Optional.of(cachedResponse.commandResponse);
    }

    // TODO: Move to Cache
    private class CachedCommandResponse {
        private final CommandResponse commandResponse;
        private final long timestamp;

        public CachedCommandResponse(CommandResponse commandResponse, long timestamp) {
            this.commandResponse = commandResponse;
            this.timestamp = timestamp;
        }

        public CommandResponse getCommandResponse() {
            return commandResponse;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
