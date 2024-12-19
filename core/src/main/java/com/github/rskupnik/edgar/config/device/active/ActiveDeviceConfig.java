package com.github.rskupnik.edgar.config.device.active;

import com.github.rskupnik.edgar.config.device.DeviceConfig;

import java.util.Collections;
import java.util.List;

public class ActiveDeviceConfig extends DeviceConfig {
    private int unresponsiveTimeout = 1;
    private List<EndpointConfig> endpoints = Collections.emptyList();

    public static DeviceConfig empty() {
        return new DeviceConfig();
    }

    public int getUnresponsiveTimeout() {
        return unresponsiveTimeout;
    }

    public void setUnresponsiveTimeout(int unresponsiveTimeout) {
        this.unresponsiveTimeout = unresponsiveTimeout;
    }

    public List<EndpointConfig> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<EndpointConfig> endpoints) {
        this.endpoints = endpoints;
    }
}
