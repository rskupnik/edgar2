package com.github.rskupnik.edgar2.web.dto;

import com.github.rskupnik.edgar.domain.Tile;

import java.util.Map;

public class TileDto {

    private String name;
    private String deviceId;
    private String endpointId;
    private String deviceType;
    private Integer x, y;
    private String type;
    private Map<String, Object> properties;

    public TileDto() {

    }

    public Tile toDomainClass() {
        return new Tile(name, deviceId, endpointId, deviceType, x, y, type, properties);
    }

    public static TileDto fromDomainClass(Tile tile) {
        TileDto dto = new TileDto();
        dto.setName(tile.getName());
        dto.setDeviceId(tile.getDeviceId());
        dto.setEndpointId(tile.getEndpointId());
        dto.setDeviceType(tile.getDeviceType());
        dto.setX(tile.getX());
        dto.setY(tile.getY());
        dto.setType(tile.getType());
        dto.setProperties(tile.getProperties());
        return dto;
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

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
