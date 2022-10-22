package com.github.rskupnik.edgar.db.entity;

import com.github.rskupnik.edgar.domain.Tile;

import java.util.List;
import java.util.stream.Collectors;

public class TileEntity implements DbEntity {

    private String name;
    private String deviceId;
    private String deviceType;
    private int x, y;
    private String type;
    private List<DashboardEndpointEntity> endpoints;

    public TileEntity() {}

    public TileEntity(String name, String deviceId, String deviceType, int x, int y, String type, List<DashboardEndpointEntity> endpoints) {
        this.name = name;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.x = x;
        this.y = y;
        this.type = type;
        this.endpoints = endpoints;
    }

    public static TileEntity fromDomainObject(Tile tile) {
        return new TileEntity(
                tile.getName(),
                tile.getDeviceId(),
                tile.getDeviceType(),
                tile.getX(),
                tile.getY(),
                tile.getType(),
                tile.getEndpoints().stream().map(DashboardEndpointEntity::fromDomainObject).collect(Collectors.toList())
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DashboardEndpointEntity> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<DashboardEndpointEntity> endpoints) {
        this.endpoints = endpoints;
    }
}
