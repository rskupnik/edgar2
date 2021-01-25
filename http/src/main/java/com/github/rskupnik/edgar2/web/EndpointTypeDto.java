package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.domain.EndpointType;

public class EndpointTypeDto {

    public String path;
    public String type;

    public EndpointTypeDto(String path, String type) {
        this.path = path;
        this.type = type;
    }

    public EndpointType toDomainClass() {
        return new EndpointType(path, type);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
