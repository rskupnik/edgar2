package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.domain.DeviceEndpointParam;

public class DeviceEndpointParamDto {

    private String name;
    private DeviceEndpointParam.Type type;
    private String description;

    public DeviceEndpointParamDto() {

    }

    public DeviceEndpointParam toDomainClass() {
        return new DeviceEndpointParam(name, type, description);
    }

    public static DeviceEndpointParamDto fromDomainClass(DeviceEndpointParam param) {
        DeviceEndpointParamDto dto = new DeviceEndpointParamDto();
        dto.setName(param.getName());
        dto.setType(param.getType());
        dto.setDescription(param.getDescription());
        return dto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceEndpointParam.Type getType() {
        return type;
    }

    public void setType(DeviceEndpointParam.Type type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
