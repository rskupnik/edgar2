package com.github.rskupnik.edgar.domain;

import com.github.rskupnik.edgar.db.entity.DashboardEndpointEntity;
import com.github.rskupnik.edgar.db.entity.DashboardEntity;

import java.util.Map;

public class DashboardEndpoint {

    private final String id;
    private final EndpointActivationType activationType;
    private final Map<String, Object> properties;

    public DashboardEndpoint(String id, EndpointActivationType activationType, Map<String, Object> properties) {
        this.id = id;
        this.activationType = activationType;
        this.properties = properties;
    }

    public static DashboardEndpoint fromEntity(DashboardEndpointEntity entity) {
        return new DashboardEndpoint(
                entity.getId(),
                entity.getActivationType(),
                entity.getProperties()
        );
    }

    public String getId() {
        return id;
    }

    public EndpointActivationType getActivationType() {
        return activationType;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
