package com.github.rskupnik.edgar.domain;

import java.util.List;

public class DeviceLayout {

    private final String id;
    private final List<EndpointType> endpointTypes;

    public DeviceLayout(String id, List<EndpointType> endpointTypes) {
        this.id = id;
        this.endpointTypes = endpointTypes;
    }

    public String getId() {
        return id;
    }

    public List<EndpointType> getEndpointTypes() {
        return endpointTypes;
    }
}
