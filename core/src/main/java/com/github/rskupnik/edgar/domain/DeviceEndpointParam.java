package com.github.rskupnik.edgar.domain;

public class DeviceEndpointParam {

    private final String name;
    private final Type type;

    public DeviceEndpointParam(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        BOOL, INT, STRING
    }
}
