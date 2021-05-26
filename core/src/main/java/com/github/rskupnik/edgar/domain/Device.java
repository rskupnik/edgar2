package com.github.rskupnik.edgar.domain;

import java.util.List;
import java.util.Objects;

public class Device {

    private final String id;
    private final String name;
    private final String ip;
    private final List<DeviceEndpoint> endpoints;
    private final List<ActivationPeriod> activationPeriods;

    public Device(String id, String name, String ip, List<DeviceEndpoint> endpoints, List<ActivationPeriod> activationPeriods) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.endpoints = endpoints;
        this.activationPeriods = activationPeriods;
    }

    public String getId() {
        return id;
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

    public List<ActivationPeriod> getActivationPeriods() {
        return activationPeriods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Device)) return false;
        Device device = (Device) o;
        return id.equals(device.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
