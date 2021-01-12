package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.domain.DeviceEndpoint;

import java.util.List;
import java.util.stream.Collectors;

public class DeviceEndpointDto {

    private String path;
    private DeviceEndpoint.HttpMethod method;
    private String description;
    private List<DeviceEndpointParamDto> params;

    public DeviceEndpointDto() {

    }

    public DeviceEndpoint toDomainClass() {
        return new DeviceEndpoint(path, method, description, params.stream()
                .map(DeviceEndpointParamDto::toDomainClass).collect(Collectors.toList()));
    }

    public static DeviceEndpointDto fromDomainClass(DeviceEndpoint endpoint) {
        DeviceEndpointDto dto = new DeviceEndpointDto();
        dto.setPath(endpoint.getPath());
        dto.setMethod(endpoint.getMethod());
        dto.setDescription(endpoint.getDescription());
        dto.setParams(endpoint.getParams().stream().map(DeviceEndpointParamDto::fromDomainClass).collect(Collectors.toList()));
        return dto;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public DeviceEndpoint.HttpMethod getMethod() {
        return method;
    }

    public void setMethod(DeviceEndpoint.HttpMethod method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DeviceEndpointParamDto> getParams() {
        return params;
    }

    public void setParams(List<DeviceEndpointParamDto> params) {
        this.params = params;
    }
}
