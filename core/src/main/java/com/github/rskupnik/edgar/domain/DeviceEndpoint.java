package com.github.rskupnik.edgar.domain;

import com.github.rskupnik.edgar.db.entity.DeviceEndpointEntity;

import java.util.List;
import java.util.stream.Collectors;

public class DeviceEndpoint {

    private final String path;
    private final HttpMethod method;
    private final List<DeviceEndpointParam> params;

    public DeviceEndpoint(String path, HttpMethod method, List<DeviceEndpointParam> params) {
        this.path = path;
        this.method = method;
        this.params = params;
    }

    public static DeviceEndpoint fromEntity(DeviceEndpointEntity entity) {
        return new DeviceEndpoint(
                entity.getPath(),
                entity.getMethod(),
                entity.getParams().stream().map(DeviceEndpointParam::fromEntity).collect(Collectors.toList())
        );
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
}
