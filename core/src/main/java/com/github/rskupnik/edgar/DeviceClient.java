package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.domain.CommandResponse;
import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceEndpoint;
import com.github.rskupnik.edgar.domain.DeviceStatus;

import java.util.Map;

interface DeviceClient {

    DeviceStatus getStatus(Device device);
    CommandResponse sendCommand(Device device, DeviceEndpoint endpoint, Map<String, String> params);
}
