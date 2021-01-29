package com.github.rskupnik.edgar.domain;

public class EndpointBinding {

    private final String uiParam;
    private final String deviceParam;

    public EndpointBinding(String uiParam, String deviceParam) {
        this.uiParam = uiParam;
        this.deviceParam = deviceParam;
    }

    public String getUiParam() {
        return uiParam;
    }

    public String getDeviceParam() {
        return deviceParam;
    }
}
