package com.github.rskupnik.edgar.domain;

import com.github.rskupnik.edgar.db.entity.DeviceEndpointParamEntity;

public class DeviceEndpointParam {

    private final String name;
    private final DeviceEndpointParamType type;

    public DeviceEndpointParam(String name, DeviceEndpointParamType type) {
        this.name = name;
        this.type = type;
    }

    public static DeviceEndpointParam fromEntity(DeviceEndpointParamEntity entity) {
        return new DeviceEndpointParam(
                entity.getName(),
                entity.getType()
        );
    }

    public String getName() {
        return name;
    }

    public DeviceEndpointParamType getType() {
        return type;
    }
}
