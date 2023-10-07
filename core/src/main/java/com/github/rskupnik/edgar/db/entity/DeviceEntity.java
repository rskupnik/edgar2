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
    private boolean passive = false;
    private long lastSuccessResponseTimestamp = Instant.now().toEpochMilli();
    private List<DeviceEndpointEntity> endpoints;
    private List<DataEntryEntity> data;

    public DeviceEntity() {}

    public DeviceEntity(String id, String name, String ip, boolean responsive, boolean passive, List<DeviceEndpointEntity> endpoints,
                        List<DataEntryEntity> data) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.responsive = responsive;
        this.passive = passive;
        this.endpoints = endpoints;
        this.data = data;
    }

    public static DeviceEntity fromDomainObject(Device device) {
        return new DeviceEntity(
                device.getId(),
                device.getName(),
                device.getIp(),
                device.isResponsive(),
                device.isPassive(),
                device.getEndpoints().stream().map(DeviceEndpointEntity::fromDomainObject).collect(Collectors.toList()),
                device.getData().stream().map(DataEntryEntity::fromDomainObject).collect(Collectors.toList())
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

    public boolean isPassive() {
        return passive;
    }

    public void setPassive(boolean passive) {
        this.passive = passive;
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

    public List<DataEntryEntity> getData() {
        return data;
    }

    public void setData(List<DataEntryEntity> data) {
        this.data = data;
    }
}
