package com.github.rskupnik.edgar.domain;

import com.github.rskupnik.edgar.db.entity.TileEntity;

import java.util.Map;

public class Tile {

    private final String name;
    private final String deviceId;
    private final String endpointId;
    private final String deviceType;
    private final int x, y;
    private final String type;
    private final Map<String, Object> properties;

    public Tile(String name, String deviceId, String endpointId, String deviceType, int x, int y, String type, Map<String, Object> properties) {
        this.name = name;
        this.deviceId = deviceId;
        this.endpointId = endpointId;
        this.deviceType = deviceType;
        this.x = x;
        this.y = y;
        this.type = type;
        this.properties = properties;
    }

    public static Tile fromEntity(TileEntity entity) {
        return new Tile(
                entity.getName(),
                entity.getDeviceId(),
                entity.getEndpointId(),
                entity.getDeviceType(),
                entity.getX(),
                entity.getY(),
                entity.getType(),
                entity.getProperties()
        );
    }

    public String getName() {
        return name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getEndpointId() {
        return endpointId;
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

    public Map<String, Object> getProperties() {
        return properties;
    }
}
