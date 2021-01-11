package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.Device;
import io.vavr.control.Either;

public interface Edgar {

    Either<String, Device> registerDevice(String name, String ip);

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

Ad 1:
Edgar should expose an HTTP endpoint that the devices will use to register themselves. This endpoint should
gather information such as the device name, IP address, and a list of endpoints it exposes.
Example structure:
{
    "name": "exampleDevice",
    "ip": "127.0.0.1",
    "endpoints": [
        {
            "path": "on",    // <device IP>/<path>
            "method": "POST",
            "description": "Turn the device on"
        },
        {
            "path": "off",
            "method": "POST",
            "description": "Turn the device off"
        },
        {
            "path": "toggle",
            "method": "POST",
            "description": "Turn the device on or off",
            "params": [
                {
                    "name": "enable",
                    "type": "bool",
                    "description": "Whether to enable the device or disable it"
                }
            ]
        }
    ]
}

Ad 2:
Expose a single endpoint that accepts commands for devices. The devices are distinguished by the device name/id that
it was registered with. Edgar will then find the device's IP and pass the command to it.

Ad 3:
Simply expose a website that will serve as a dashboard and use the command endpoint to pass commands to devices.
This dashboard should display all devices and their current status.
 */

// TODO: How to monitor for device status? Ping them every minute to check if they respond?