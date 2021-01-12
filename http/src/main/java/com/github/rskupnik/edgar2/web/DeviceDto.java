package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.domain.Device;

import java.util.List;
import java.util.stream.Collectors;

public class DeviceDto {

    private String name;
    private String ip;
    private List<DeviceEndpointDto> endpoints;

    public DeviceDto() {

    }

    public Device toDomainClass() {
        return new Device(name, ip, endpoints.stream().map(DeviceEndpointDto::toDomainClass).collect(Collectors.toList()));
    }

    public static DeviceDto fromDomainClass(Device device) {
        DeviceDto dto = new DeviceDto();
        dto.setName(device.getName());
        dto.setIp(device.getIp());
        dto.setEndpoints(device.getEndpoints().stream().map(DeviceEndpointDto::fromDomainClass).collect(Collectors.toList()));
        return dto;
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

    public List<DeviceEndpointDto> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<DeviceEndpointDto> endpoints) {
        this.endpoints = endpoints;
    }
}
