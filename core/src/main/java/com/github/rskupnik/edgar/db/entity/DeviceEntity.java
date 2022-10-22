package com.github.rskupnik.edgar.db.entity;

import com.github.rskupnik.edgar.domain.Device;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceEntity implements DbEntity {

    private String id;
    private String name;
    private String ip;
    private boolean responsive = true;
    private long lastSuccessResponseTimestamp = Instant.now().toEpochMilli();
    private List<DeviceEndpointEntity> endpoints;

    public DeviceEntity() {}

    public DeviceEntity(String id, String name, String ip, boolean responsive, List<DeviceEndpointEntity> endpoints) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.responsive = responsive;
        this.endpoints = endpoints;
    }

    public static DeviceEntity fromDomainObject(Device device) {
        return new DeviceEntity(
                device.getId(),
                device.getName(),
                device.getIp(),
                device.isResponsive(),
                device.getEndpoints().stream().map(DeviceEndpointEntity::fromDomainObject).collect(Collectors.toList())
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isResponsive() {
        return responsive;
    }

    public void setResponsive(boolean responsive) {
        this.responsive = responsive;
    }

    public long getLastSuccessResponseTimestamp() {
        return lastSuccessResponseTimestamp;
    }

    public void setLastSuccessResponseTimestamp(long lastSuccessResponseTimestamp) {
        this.lastSuccessResponseTimestamp = lastSuccessResponseTimestamp;
    }

    public List<DeviceEndpointEntity> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<DeviceEndpointEntity> endpoints) {
        this.endpoints = endpoints;
    }
}
