package com.github.rskupnik.edgar.domain;

public class Tile {

    private final String deviceId;
    private final int x, y;
    private final String type;

    public Tile(String deviceId, int x, int y, String type) {
        this.deviceId = deviceId;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
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
