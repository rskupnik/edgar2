package com.github.rskupnik.edgar2.web.dto;

import com.github.rskupnik.edgar.domain.Tile;

public class TileDto {

    private String deviceId;
    private Integer x, y;
    private String type;

    public TileDto() {

    }

    public Tile toDomainClass() {
        return new Tile(deviceId, x, y, type);
    }

    public static TileDto fromDomainClass(Tile tile) {
        TileDto dto = new TileDto();
        dto.setDeviceId(tile.getDeviceId());
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
}
