package com.github.rskupnik.edgar.db.entity;

import com.github.rskupnik.edgar.domain.DeviceEndpoint;

import java.util.List;
import java.util.stream.Collectors;

public class DeviceEndpointEntity implements DbEntity {

    private String path;
    private DeviceEndpoint.HttpMethod method;
    private List<DeviceEndpointParamEntity> params;

    public DeviceEndpointEntity() {}

    public DeviceEndpointEntity(String path, DeviceEndpoint.HttpMethod method, List<DeviceEndpointParamEntity> params) {
        this.path = path;
        this.method = method;
        this.params = params;
    }

    public static DeviceEndpointEntity fromDomainObject(DeviceEndpoint deviceEndpoint) {
        return new DeviceEndpointEntity(
                deviceEndpoint.getPath(),
                deviceEndpoint.getMethod(),
                deviceEndpoint.getParams().stream().map(DeviceEndpointParamEntity::fromDomainObject).collect(Collectors.toList())
        );
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public DeviceEndpoint.HttpMethod getMethod() {
        return method;
    }

    public void setMethod(DeviceEndpoint.HttpMethod method) {
        this.method = method;
    }

    public List<DeviceEndpointParamEntity> getParams() {
        return params;
    }

    public void setParams(List<DeviceEndpointParamEntity> params) {
        this.params = params;
    }
}
