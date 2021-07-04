package com.github.rskupnik.edgar2.web.dto;

import com.github.rskupnik.edgar.domain.EndpointLayout;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EndpointLayoutDto {

    public String path;
    public String type;
    public List<EndpointBindingDto> bindings;

    public EndpointLayoutDto() {

    }

    public EndpointLayout toDomainClass() {
        return new EndpointLayout(path, type, bindings != null ? bindings.stream().map(EndpointBindingDto::toDomainClass).collect(Collectors.toList()) : Collections.emptyList());
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

    public List<EndpointBindingDto> getBindings() {
        return bindings;
    }

    public void setBindings(List<EndpointBindingDto> bindings) {
        this.bindings = bindings;
    }
}
