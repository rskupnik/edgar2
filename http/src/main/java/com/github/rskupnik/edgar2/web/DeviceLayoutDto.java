package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.domain.DeviceLayout;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceLayoutDto {

    public String id;
    public List<EndpointLayoutDto> endpoints;

    public DeviceLayoutDto(String id, List<EndpointLayoutDto> endpoints) {
        this.id = id;
        this.endpoints = endpoints;
    }

    public DeviceLayout toDomainClass() {
        return new DeviceLayout(id, endpoints == null ? Collections.emptyList() : endpoints.stream().map(EndpointLayoutDto::toDomainClass).collect(Collectors.toList()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<EndpointLayoutDto> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<EndpointLayoutDto> endpoints) {
        this.endpoints = endpoints;
    }
}
