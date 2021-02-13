package com.github.rskupnik.edgar.domain;

import java.util.Collections;
import java.util.Map;

public class DeviceStatus {

    private final boolean responsive;
    private final Map<String, String> params;

    public DeviceStatus(boolean responsive, Map<String, String> params) {
        this.responsive = responsive;
        this.params = params;
    }

    public static DeviceStatus unknown() {
        return new DeviceStatus(false, Collections.emptyMap());
    }

    public boolean isResponsive() {
        return responsive;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
