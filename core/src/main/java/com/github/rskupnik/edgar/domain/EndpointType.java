package com.github.rskupnik.edgar.domain;

public class EndpointType {

    private final String path;
    private final String type;

    public EndpointType(String path, String type) {
        this.path = path;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }
}
