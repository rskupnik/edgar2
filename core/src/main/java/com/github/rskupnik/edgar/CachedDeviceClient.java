package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.CommandResponse;
import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceEndpoint;
import com.github.rskupnik.edgar.domain.DeviceStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

class CachedDeviceClient implements DeviceClient {

    private final DeviceClient delegate;
    private final Function<String, Integer> cacheTimeSupplier;

    private final Map<String, CachedResponse<? extends DeviceResponse>> cache = new HashMap<>();

    public CachedDeviceClient(DeviceClient delegate, Function<String, Integer> cacheTimeSupplier) {
        this.delegate = delegate;
        this.cacheTimeSupplier = cacheTimeSupplier;
    }

    @Override
    public DeviceStatus getStatus(Device device) {
        return delegate.getStatus(device);
    }

    @Override
    public boolean isAlive(Device device) {
        return delegate.isAlive(device);
    }

    @Override
    public CommandResponse sendCommand(Device device, DeviceEndpoint endpoint, Map<String, String> params) {
        final String cacheKey = buildCacheKey(device.getId(), endpoint.getPath());
        System.out.println("Cache key is: " + cacheKey);
        final int cacheTime = cacheTimeSupplier.apply(cacheKey);
        System.out.println("Cache time is: " + cacheTime + "ms");
        if (cacheTime == 0) {
            System.out.println("Making a new call (no caching)");
            return delegate.sendCommand(device, endpoint, params);
        }

        var cachedResponse = cache.get(cacheKey);
        if (cachedResponse == null || Instant.now().toEpochMilli() - cacheTime > cachedResponse.timestamp) {
            return delegateAndCache(cacheKey, () -> delegate.sendCommand(device, endpoint, params));
        } else {
            System.out.println("Returning from cache");
            return (CommandResponse) cachedResponse.response;
        }
    }

    private <T extends DeviceResponse> T delegateAndCache(String key, Supplier<T> func) {
        System.out.println("Making a new call and caching it");
        T response = func.get();
        cache.put(key, new CachedResponse<>(response, Instant.now().toEpochMilli()));
        System.out.println("Caching under key: " + key);
        return response;
    }

    private String buildCacheKey(String deviceId, String endpoint) {
        return deviceId + ":" + endpoint;
    }

    private static class CachedResponse<T extends DeviceResponse> {
        private final T response;
        private final long timestamp;

        public CachedResponse(T commandResponse, long timestamp) {
            this.response = commandResponse;
            this.timestamp = timestamp;
        }
    }
}
