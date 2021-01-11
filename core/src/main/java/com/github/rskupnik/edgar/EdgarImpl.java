package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.Device;
import io.vavr.control.Either;

class EdgarImpl implements Edgar {

    private final Database database;

    public EdgarImpl(Database database) {
        this.database = database;
    }

    @Override
    public Either<String, Device> registerDevice(String name, String ip) {
        if (database.findDevice(name).isDefined()) {
            return Either.left("This device is already registered");
        }

        final Device device = new Device(name, ip);
        database.saveDevice(device);
        return Either.right(device);
    }
}
