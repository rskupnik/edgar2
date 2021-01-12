package com.github.rskupnik.edgar.domain;

import java.util.List;

public class Device {

    private final String name;
    private final String ip;
    private final List<DeviceEndpoint> endpoints;

    public Device(String name, String ip, List<DeviceEndpoint> endpoints) {
        this.name = name;
        this.ip = ip;
        this.endpoints = endpoints;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public List<DeviceEndpoint> getEndpoints() {
        return endpoints;
    }
}
