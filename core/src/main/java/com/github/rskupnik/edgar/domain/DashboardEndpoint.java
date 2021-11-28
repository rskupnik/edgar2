package com.github.rskupnik.edgar.domain;

import com.github.rskupnik.edgar.db.entity.DashboardEndpointEntity;

import java.util.Map;

public class DashboardEndpoint {

    private final String id;
    private final EndpointActivationType activationType;
    private final String responseType;
    private final Map<String, Object> properties;

    public DashboardEndpoint(String id, EndpointActivationType activationType, String responseType, Map<String, Object> properties) {
        this.id = id;
        this.activationType = activationType;
        this.responseType = responseType;
        this.properties = properties;
    }

    public static DashboardEndpoint fromEntity(DashboardEndpointEntity entity) {
        return new DashboardEndpoint(
                entity.getId(),
                entity.getActivationType(),
                entity.getResponseType(),
                entity.getProperties()
        );
    }

    public String getId() {
        return id;
    }

    public EndpointActivationType getActivationType() {
        return activationType;
    }

    public String getResponseType() {
        return responseType;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
