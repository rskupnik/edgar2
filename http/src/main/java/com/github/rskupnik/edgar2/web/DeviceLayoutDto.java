package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.domain.DeviceLayout;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceLayoutDto {

    public String id;
    public List<EndpointTypeDto> endpointTypes;

    public DeviceLayoutDto(String id, List<EndpointTypeDto> endpointTypes) {
        this.id = id;
        this.endpointTypes = endpointTypes;
    }

    public DeviceLayout toDomainClass() {
        return new DeviceLayout(id, endpointTypes == null ? Collections.emptyList() : endpointTypes.stream().map(EndpointTypeDto::toDomainClass).collect(Collectors.toList()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<EndpointTypeDto> getEndpointTypes() {
        return endpointTypes;
    }

    public void setEndpointTypes(List<EndpointTypeDto> endpointTypes) {
        this.endpointTypes = endpointTypes;
    }
}
