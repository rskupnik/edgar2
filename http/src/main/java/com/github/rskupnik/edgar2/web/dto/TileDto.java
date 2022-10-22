package com.github.rskupnik.edgar2.web.dto;

import com.github.rskupnik.edgar.domain.Tile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TileDto {

    private String name;
    private String deviceId;
    private String deviceType;
    private Integer x, y;
    private String type;
    private List<DashboardEndpointDto> endpoints;

    public TileDto() {

    }

    public Tile toDomainClass() {
        return new Tile(
                name,
                deviceId,
                deviceType,
                x, y,
                type,
                endpoints.stream().map(DashboardEndpointDto::toDomainClass).collect(Collectors.toList())
        );
    }

    public static TileDto fromDomainClass(Tile tile) {
        TileDto dto = new TileDto();
        dto.setName(tile.getName());
        dto.setDeviceId(tile.getDeviceId());
        dto.setDeviceType(tile.getDeviceType());
        dto.setX(tile.getX());
        dto.setY(tile.getY());
        dto.setType(tile.getType());
        dto.setEndpoints(tile.getEndpoints().stream().map(DashboardEndpointDto::fromDomainClass).collect(Collectors.toList()));
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

    public List<DashboardEndpointDto> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<DashboardEndpointDto> endpoints) {
        this.endpoints = endpoints;
    }
}
