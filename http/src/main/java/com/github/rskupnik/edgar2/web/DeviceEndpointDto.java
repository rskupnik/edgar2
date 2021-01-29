package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.domain.DeviceEndpoint;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceEndpointDto {

    private String path;
    private DeviceEndpoint.HttpMethod method;
    private String type;
    private List<DeviceEndpointParamDto> params;
    private List<EndpointBindingDto> bindings;

    public DeviceEndpointDto() {

    }

    public DeviceEndpoint toDomainClass() {
        return new DeviceEndpoint(path, method, params == null ? Collections.emptyList() : params.stream()
                .map(DeviceEndpointParamDto::toDomainClass).collect(Collectors.toList()));
    }

    public static DeviceEndpointDto fromDomainClass(DeviceEndpoint endpoint) {
        DeviceEndpointDto dto = new DeviceEndpointDto();
        dto.setPath(endpoint.getPath());
        dto.setMethod(endpoint.getMethod());
        dto.setParams(endpoint.getParams() == null ? Collections.emptyList() : endpoint.getParams().stream().map(DeviceEndpointParamDto::fromDomainClass).collect(Collectors.toList()));
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

    public List<DeviceEndpointParamDto> getParams() {
        return params;
    }

    public void setParams(List<DeviceEndpointParamDto> params) {
        this.params = params;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<EndpointBindingDto> getBindings() {
        return bindings;
    }

    public void setBindings(List<EndpointBindingDto> bindings) {
        this.bindings = bindings;
    }
}
