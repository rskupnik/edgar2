package com.github.rskupnik.edgar.domain;

import java.util.Collections;
import java.util.Map;

public class DeviceStatus {

    private final boolean responsive;
    private final Map<String, String> data;
    private final Map<String, Map<String, String>> endpoints;

    public DeviceStatus(boolean responsive, Map<String, String> data, Map<String, Map<String, String>> endpoints) {
        this.responsive = responsive;
        this.data = data;
        this.endpoints = endpoints;
    }

    public static DeviceStatus unknown() {
        return new DeviceStatus(false, Collections.emptyMap(), Collections.emptyMap());
    }

    public boolean isResponsive() {
        return responsive;
    }

    public Map<String, String> getData() {
        return data;
    }

    public Map<String, Map<String, String>> getEndpoints() {
        return endpoints;
    }
}
