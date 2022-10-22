package com.github.rskupnik.edgar.db.entity;

import com.github.rskupnik.edgar.domain.DeviceEndpointParam;
import com.github.rskupnik.edgar.domain.DeviceEndpointParamType;

public class DeviceEndpointParamEntity implements DbEntity{

    private String name;
    private DeviceEndpointParamType type;

    public DeviceEndpointParamEntity() {}

    public DeviceEndpointParamEntity(String name, DeviceEndpointParamType type) {
        this.name = name;
        this.type = type;
    }

    public static DeviceEndpointParamEntity fromDomainObject(DeviceEndpointParam param) {
        return new DeviceEndpointParamEntity(
                param.getName(),
                param.getType()
        );
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
