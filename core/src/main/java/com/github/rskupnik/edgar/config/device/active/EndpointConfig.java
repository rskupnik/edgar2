package com.github.rskupnik.edgar.config.device.active;

public class EndpointConfig {

    private String path;
    private int cachePeriod = 0;

    public static EndpointConfig empty() {
        return new EndpointConfig();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCachePeriod() {
        return cachePeriod;
    }

    public void setCachePeriod(int cachePeriod) {
        this.cachePeriod = cachePeriod;
    }
}
