package com.github.rskupnik.edgar2.web.dto;

import com.github.rskupnik.edgar.domain.Device;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceDto {

    private String id;
    private String name;
    private String ip;
    private Boolean responsive = true;
    private List<DeviceEndpointDto> endpoints;

    public DeviceDto() {

    }

    public Device toDomainClass() {
        return new Device(
            id,
            name,
            ip,
            responsive,
            endpoints == null ? Collections.emptyList() : endpoints.stream().map(DeviceEndpointDto::toDomainClass).collect(Collectors.toList())
        );
    }

    public static DeviceDto fromDomainClass(Device device) {
        DeviceDto dto = new DeviceDto();
        dto.setId(device.getId());
        dto.setName(device.getName());
        dto.setIp(device.getIp());
        dto.setResponsive(device.isResponsive());
        dto.setEndpoints(device.getEndpoints() == null ? Collections.emptyList() : device.getEndpoints().stream().map(DeviceEndpointDto::fromDomainClass).collect(Collectors.toList()));
        return dto;
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

    public Boolean getResponsive() {
        return responsive;
    }

    public void setResponsive(Boolean responsive) {
        this.responsive = responsive;
    }

    public List<DeviceEndpointDto> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<DeviceEndpointDto> endpoints) {
        this.endpoints = endpoints;
    }
}
