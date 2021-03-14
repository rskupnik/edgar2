package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.domain.DeviceStatus;

import java.util.Map;

public class DeviceStatusDto {

    public boolean responsive;
    public Map<String, String> params;
    public Map<String, Map<String, String>> endpoints;

    public DeviceStatusDto() {

    }

    public static DeviceStatusDto fromDomainClass(DeviceStatus status) {
        if (status == null) {
            return null;
        }

        var dto = new DeviceStatusDto();
        dto.setResponsive(status.isResponsive());
        dto.setParams(status.getData());
        dto.setEndpoints(status.getEndpoints());
        return dto;
    }

    public boolean isResponsive() {
        return responsive;
    }

    public void setResponsive(boolean responsive) {
        this.responsive = responsive;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, Map<String, String>> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<String, Map<String, String>> endpoints) {
        this.endpoints = endpoints;
    }
}
