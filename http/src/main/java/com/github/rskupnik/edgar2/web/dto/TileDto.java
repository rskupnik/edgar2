package com.github.rskupnik.edgar2.web.dto;

import com.github.rskupnik.edgar.domain.Tile;

public class TileDto {

    private String deviceId;
    private String endpointId;
    private String deviceType;
    private Integer x, y;
    private String type;

    public TileDto() {

    }

    public Tile toDomainClass() {
        return new Tile(deviceId, endpointId, deviceType, x, y, type);
    }

    public static TileDto fromDomainClass(Tile tile) {
        TileDto dto = new TileDto();
        dto.setDeviceId(tile.getDeviceId());
        dto.setEndpointId(tile.getEndpointId());
        dto.setDeviceType(tile.getDeviceType());
        dto.setX(tile.getX());
        dto.setY(tile.getY());
        dto.setType(tile.getType());
        return dto;
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
}
