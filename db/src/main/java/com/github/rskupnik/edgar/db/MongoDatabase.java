package com.github.rskupnik.edgar.db;

import com.github.rskupnik.edgar.Database;
import com.github.rskupnik.edgar.domain.Device;
import io.vavr.control.Option;

public class MongoDatabase implements Database {

    @Override
    public Option<Device> findDevice(String name) {
        return null;
    }

    @Override
    public void saveDevice(Device device) {

    }
}
