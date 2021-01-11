package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.Device;
import io.vavr.control.Option;

public interface Database {

    Option<Device> findDevice(String name);
    void saveDevice(Device device);
}
