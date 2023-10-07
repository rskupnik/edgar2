package com.github.rskupnik.edgar.db.entity;

import com.github.rskupnik.edgar.domain.DataEntry;

public class DataEntryEntity implements DbEntity {

    private String id;
    private byte[] contents;

    public DataEntryEntity() {}

    public DataEntryEntity(String id, byte[] contents) {
        this.id = id;
        this.contents = contents;
    }

    public static DataEntryEntity fromDomainObject(DataEntry data) {
        return new DataEntryEntity(
                data.getId(),
                data.getContents()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }
}
