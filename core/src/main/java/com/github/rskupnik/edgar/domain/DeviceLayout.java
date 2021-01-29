package com.github.rskupnik.edgar.domain;

import java.util.List;

public class DeviceLayout {

    private final String id;
    private final List<EndpointLayout> endpoints;

    public DeviceLayout(String id, List<EndpointLayout> endpoints) {
        this.id = id;
        this.endpoints = endpoints;
    }

    public String getId() {
        return id;
    }

    public List<EndpointLayout> getEndpoints() {
        return endpoints;
    }
}
