package com.github.rskupnik.edgar.domain;

import com.github.rskupnik.edgar.db.entity.DeviceEntity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Device {

    private final String id;
    private final String name;
    private final String ip;
    private final boolean responsive;
    private final List<DeviceEndpoint> endpoints;

    public Device(String id, String name, String ip, boolean responsive, List<DeviceEndpoint> endpoints) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.responsive = responsive;
        this.endpoints = endpoints;
    }

    public static Device fromEntity(DeviceEntity entity) {
        return new Device(
                entity.getId(),
                entity.getName(),
                entity.getIp(),
                entity.isResponsive(),
                entity.getEndpoints().stream().map(DeviceEndpoint::fromEntity).collect(Collectors.toList())
        );
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

    public Boolean isResponsive() {
        return responsive;
    }

    public List<DeviceEndpoint> getEndpoints() {
        return endpoints;
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
