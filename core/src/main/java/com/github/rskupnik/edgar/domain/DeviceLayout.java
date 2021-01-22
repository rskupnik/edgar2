package com.github.rskupnik.edgar.domain;

public class DeviceLayout {

    private final String id;
    private final String type;

    public DeviceLayout(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
