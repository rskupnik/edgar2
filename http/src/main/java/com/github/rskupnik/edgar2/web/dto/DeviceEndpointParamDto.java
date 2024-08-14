package com.github.rskupnik.edgar2.web.dto;

import com.github.rskupnik.edgar.domain.DeviceEndpointParam;
import com.github.rskupnik.edgar.domain.DeviceEndpointParamType;

public class DeviceEndpointParamDto {

    private String name;
    private DeviceEndpointParamType type;

    public DeviceEndpointParamDto() {

    }

    public DeviceEndpointParam toDomainClass() {
        return new DeviceEndpointParam(name, type);
    }

    public static DeviceEndpointParamDto fromDomainClass(DeviceEndpointParam param) {
        DeviceEndpointParamDto dto = new DeviceEndpointParamDto();
        dto.setName(param.getName());
        dto.setType(param.getType());
        return dto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceEndpointParamType getType() {
        return type;
    }

    public void setType(DeviceEndpointParamType type) {
        this.type = type;
    }
}
