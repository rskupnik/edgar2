package com.github.rskupnik.edgar.domain;

public class Tile {

    private final String deviceId;
    private final String deviceType;
    private final int x, y;
    private final String type;

    public Tile(String deviceId, String deviceType, int x, int y, String type) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getType() {
        return type;
    }
}
