package com.github.rskupnik.edgar.db.entity;

import com.github.rskupnik.edgar.domain.Tile;

import java.util.Map;

public class TileEntity implements DbEntity {

    private String name;
    private String deviceId;
    private String endpointId;
    private String deviceType;
    private int x, y;
    private String type;
    private Map<String, Object> properties;

    public TileEntity() {}

    public TileEntity(String name, String deviceId, String endpointId, String deviceType, int x, int y, String type, Map<String, Object> properties) {
        this.name = name;
        this.deviceId = deviceId;
        this.endpointId = endpointId;
        this.deviceType = deviceType;
        this.x = x;
        this.y = y;
        this.type = type;
        this.properties = properties;
    }

    public static TileEntity fromDomainObject(Tile tile) {
        return new TileEntity(
                tile.getName(),
                tile.getDeviceId(),
                tile.getEndpointId(),
                tile.getDeviceType(),
                tile.getX(),
                tile.getY(),
                tile.getType(),
                tile.getProperties()
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

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
