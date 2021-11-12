package com.github.rskupnik.edgar.config.device;

import java.util.Collections;
import java.util.List;

public class DeviceConfig {

    private String id;
    private boolean statusCheckEnabled = true;
    private List<EndpointConfig> endpoints = Collections.emptyList();

    public static DeviceConfig empty() {
        return new DeviceConfig();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isStatusCheckEnabled() {
        return statusCheckEnabled;
    }

    public void setStatusCheckEnabled(boolean statusCheckEnabled) {
        this.statusCheckEnabled = statusCheckEnabled;
    }

    public List<EndpointConfig> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<EndpointConfig> endpoints) {
        this.endpoints = endpoints;
    }
}
