package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.CommandResponse;
import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

class CachedDeviceClient implements DeviceClient {

    private static final Logger logger = LoggerFactory.getLogger(CachedDeviceClient.class);

    private final DeviceClient delegate;
    private final Function<String, Integer> cacheTimeSupplier;

    private final Map<String, CachedResponse<? extends DeviceResponse>> cache = new HashMap<>();

    public CachedDeviceClient(DeviceClient delegate, Function<String, Integer> cacheTimeSupplier) {
        this.delegate = delegate;
        this.cacheTimeSupplier = cacheTimeSupplier;
    }

    @Override
    public boolean isAlive(Device device) {
        return delegate.isAlive(device);
    }

    @Override
    public CommandResponse sendCommand(Device device, DeviceEndpoint endpoint, Map<String, String> params) {
        final String cacheKey = buildCacheKey(device.getId(), endpoint.getPath());
        logger.debug("Cache key is: " + cacheKey);
        final int cacheTime = cacheTimeSupplier.apply(cacheKey);
        logger.debug("Cache time is: " + cacheTime + "ms");
        if (cacheTime == 0) {
            logger.debug("Making a new call (no caching)");
            return delegate.sendCommand(device, endpoint, params);
        }

        var cachedResponse = cache.get(cacheKey);
        if (cachedResponse == null || Instant.now().toEpochMilli() - cacheTime > cachedResponse.timestamp) {
            return delegateAndCache(cacheKey, () -> delegate.sendCommand(device, endpoint, params));
        } else {
            logger.debug("Returning from cache");
            return (CommandResponse) cachedResponse.response;
        }
    }

    private <T extends DeviceResponse> T delegateAndCache(String key, Supplier<T> func) {
        logger.debug("Making a new call and caching it");
        T response = func.get();
        cache.put(key, new CachedResponse<>(response, Instant.now().toEpochMilli()));
        logger.debug("Caching under key: " + key);
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
