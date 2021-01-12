package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.Device;
import io.vavr.control.Either;

import java.util.List;

class EdgarImpl implements Edgar {

    private final Database database;

    public EdgarImpl(Database database) {
        this.database = database;
    }

    @Override
    public Either<String, Device> registerDevice(Device device) {
        if (database.findDevice(device.getName()).isDefined()) {
            return Either.left("This device is already registered");
        }

        database.saveDevice(device);
        return Either.right(device);
    }

    @Override
    public List<Device> getDevices() {
        return database.getAll();
    }
}
