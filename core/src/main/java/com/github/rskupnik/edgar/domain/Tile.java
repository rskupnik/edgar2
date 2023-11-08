package com.github.rskupnik.edgar.domain;

import com.github.rskupnik.edgar.db.entity.TileEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Tile {

    private final String name;
    private final String deviceId;
    private final String deviceType;
    private final int x, y;
    private final String type;
    private final List<DashboardEndpoint> endpoints;

    public Tile(String name, String deviceId, String deviceType, int x, int y, String type, List<DashboardEndpoint> endpoints) {
        this.name = name;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.x = x;
        this.y = y;
        this.type = type;
        this.endpoints = endpoints;
    }

    public static Tile fromEntity(TileEntity entity) {
        return new Tile(
                entity.getName(),
                entity.getDeviceId(),
                entity.getDeviceType(),
                entity.getX(),
                entity.getY(),
                entity.getType(),
                entity.getEndpoints().stream().map(DashboardEndpoint::fromEntity).collect(Collectors.toList())
        );
    }

    public String getName() {
        return name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getType() {
        return type;
    }

    public List<DashboardEndpoint> getEndpoints() {
        return endpoints;
    }
}
