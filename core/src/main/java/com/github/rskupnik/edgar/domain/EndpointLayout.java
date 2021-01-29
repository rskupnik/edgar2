package com.github.rskupnik.edgar.domain;

import java.util.List;

public class EndpointLayout {

    private final String path;
    private final String type;
    private final List<EndpointBinding> bindings;

    public EndpointLayout(String path, String type, List<EndpointBinding> bindings) {
        this.path = path;
        this.type = type;
        this.bindings = bindings;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public List<EndpointBinding> getBindings() {
        return bindings;
    }
}
