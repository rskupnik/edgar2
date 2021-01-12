package com.github.rskupnik.edgar.domain;

import java.util.List;

public class DeviceEndpoint {

    private final String path;
    private final HttpMethod method;
    private final String description;
    private final List<DeviceEndpointParam> params;

    public DeviceEndpoint(String path, HttpMethod method, String description, List<DeviceEndpointParam> params) {
        this.path = path;
        this.method = method;
        this.description = description;
        this.params = params;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getDescription() {
        return description;
    }

    public List<DeviceEndpointParam> getParams() {
        return params;
    }

    public enum HttpMethod {
        GET, POST, PUT, DELETE
    }
}
