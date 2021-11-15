package com.github.rskupnik.edgar.db.entity;

import com.github.rskupnik.edgar.domain.DashboardEndpoint;
import com.github.rskupnik.edgar.domain.EndpointActivationType;

import java.util.Map;

public class DashboardEndpointEntity {

    private String id;
    private EndpointActivationType activationType;
    private Map<String, Object> properties;

    public DashboardEndpointEntity() {}

    public DashboardEndpointEntity(String id, EndpointActivationType activationType, Map<String, Object> properties) {
        this.id = id;
        this.activationType = activationType;
        this.properties = properties;
    }

    public static DashboardEndpointEntity fromDomainObject(DashboardEndpoint endpoint) {
        return new DashboardEndpointEntity(
                endpoint.getId(),
                endpoint.getActivationType(),
                endpoint.getProperties()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EndpointActivationType getActivationType() {
        return activationType;
    }

    public void setActivationType(EndpointActivationType activationType) {
        this.activationType = activationType;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
