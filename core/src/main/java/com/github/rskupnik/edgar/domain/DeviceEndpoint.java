package com.github.rskupnik.edgar.domain;

import java.util.List;

public class DeviceEndpoint {

    private final String path;
    private final HttpMethod method;
    private final List<DeviceEndpointParam> params;

    public DeviceEndpoint(String path, HttpMethod method, List<DeviceEndpointParam> params) {
        this.path = path;
        this.method = method;
        this.params = params;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public List<DeviceEndpointParam> getParams() {
        return params;
    }

    public enum HttpMethod {
        GET, POST, PUT, DELETE
    }
}
