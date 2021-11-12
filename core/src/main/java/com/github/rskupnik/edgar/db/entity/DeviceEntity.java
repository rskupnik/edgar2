package com.github.rskupnik.edgar.db.entity;

import com.github.rskupnik.edgar.domain.Device;

import java.util.List;
import java.util.stream.Collectors;

public class DeviceEntity implements DbEntity {

    private String id;
    private String name;
    private String ip;
    private List<DeviceEndpointEntity> endpoints;

    public DeviceEntity() {}

    public DeviceEntity(String id, String name, String ip, List<DeviceEndpointEntity> endpoints) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.endpoints = endpoints;
    }

    public static DeviceEntity fromDomainObject(Device device) {
        return new DeviceEntity(
                device.getId(),
                device.getName(),
                device.getIp(),
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

    public List<DeviceEndpointEntity> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<DeviceEndpointEntity> endpoints) {
        this.endpoints = endpoints;
    }
}
