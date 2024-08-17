package com.github.rskupnik.edgar.db.entity;

public class DeviceDataEntity implements DbEntity {

    private String id;
    private byte[] data;

    public DeviceDataEntity() {}

    public DeviceDataEntity(String id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
