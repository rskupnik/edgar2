package com.github.rskupnik.edgar.domain;

public class DeviceEndpointParam {

    private final String name;
    private final DeviceEndpointParamType type;

    public DeviceEndpointParam(String name, DeviceEndpointParamType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public DeviceEndpointParamType getType() {
        return type;
    }
}
