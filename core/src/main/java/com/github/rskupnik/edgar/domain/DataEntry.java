package com.github.rskupnik.edgar.domain;

import com.github.rskupnik.edgar.db.entity.DataEntryEntity;

import java.util.Objects;

public class DataEntry {

    private final String id;
    private final byte[] contents;

    public DataEntry(String id, byte[] contents) {
        this.id = id;
        this.contents = contents;
    }

    public static DataEntry fromEntity(DataEntryEntity entity) {
        return new DataEntry(
                entity.getId(),
                entity.getContents()
        );
    }

    public String getId() {
        return id;
    }

    public byte[] getContents() {
        return contents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataEntry dataEntry = (DataEntry) o;
        return id.equals(dataEntry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
