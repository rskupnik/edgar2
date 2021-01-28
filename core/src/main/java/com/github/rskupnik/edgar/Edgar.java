package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceLayout;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import io.vavr.control.Option;

import java.util.List;
import java.util.Map;

public interface Edgar {

    Either<String, Device> registerDevice(Device device);
    List<Device> getDevices();
    boolean sendCommand(String deviceId, String commandName, Map<String, String> params);
    void refreshDeviceStatus();
    void registerLayouts(List<DeviceLayout> layouts);
    List<Tuple2<Device, Map<String, String>>> getLayouts(List<Device> devices);

    static Edgar defaultImplementation(Database database) {
        return new EdgarImpl(database);
    }
}

// TODO: Requirements
/*
Edgar should be able to:
1. Allow for new devices to register themselves
2. Pass commands to registered devices, received through various means, HTTP for start
3. Expose a dashboard
4. Perpetually monitor devices' state

Ad 1:
Edgar should expose an HTTP endpoint that the devices will use to register themselves. This endpoint should
gather information such as the device name, IP address, and a list of endpoints it exposes.
Example structure:
{
    "id": "exampleDevice",
    "name": "Example device",
    "ip": "127.0.0.1",
    "endpoints": [
        {
            "name": "ON",
            "path": "/on",    // <device IP>/<path>
            "method": "POST",
        },
        {
            "name": "OFF",
            "path": "/off",
            "method": "POST",
        },
        {
            "name": "Toggle",
            "path": "/toggle",
            "method": "POST",
            "params": [
                {
                    "name": "enable",
                    "type": "bool"
                }
            ]
        }
    ]
}

[
    {
        "id": "exampleDevice",
        "type": "toggle-single-large"
    }
]

Ad 2:
Expose a single endpoint that accepts commands for devices. The devices are distinguished by the device name/id that
it was registered with. Edgar will then find the device's IP and pass the command to it.

Ad 3:
Simply expose a website that will serve as a dashboard and use the command endpoint to pass commands to devices.
This dashboard should display all devices and their current status.

Ad 4:
Need to check every minute (configurable) every device's /status endpoint
 */

// TODO: Add some validation to device register endpoint
// TODO: Introduce a proper logger in place of system outputs
// TODO: Make a device re-register every minute so the dashboard can discover it after going down
// TODO: Ask for device status on register or send the same data in register
// TODO: Save device status
// TODO: Make frontend poll for device status every 10s? and use the status to update the display