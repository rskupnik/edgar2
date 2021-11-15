package com.github.rskupnik.edgar2.web.dto;

import com.github.rskupnik.edgar.domain.DashboardEndpoint;
import com.github.rskupnik.edgar.domain.EndpointActivationType;

import java.util.Map;

public class DashboardEndpointDto {

    private String id;
    private EndpointActivationType activationType;
    private Map<String, Object> properties;

    public DashboardEndpoint toDomainClass() {
        return new DashboardEndpoint(
                id,
                activationType,
                properties
        );
    }

    public static DashboardEndpointDto fromDomainClass(DashboardEndpoint endpoint) {
        var dto = new DashboardEndpointDto();
        dto.setId(endpoint.getId());
        dto.setActivationType(endpoint.getActivationType());
        dto.setProperties(endpoint.getProperties());
        return dto;
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
