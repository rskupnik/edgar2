package com.github.rskupnik.edgar.domain;

public class DeviceEndpointParam {

    private final String name;
    private final Type type;
    private final String description;

    public DeviceEndpointParam(String name, Type type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public enum Type {
        BOOL, INT, STRING
    }
}
