package com.github.rskupnik.edgar

import com.github.rskupnik.edgar.domain.Device
import com.github.rskupnik.edgar.domain.DeviceEndpoint
import com.github.rskupnik.edgar.domain.HttpMethod

class Dummies {

    static final DeviceEndpoint DEVICE_ENDPOINT = new DeviceEndpoint(
            "/dummy",
            HttpMethod.POST,
            Collections.emptyList()
    )

    static final Device DEVICE = new Device(
            "dummyDeviceId",
            "dummyDevice",
            "127.0.0.1",
            true,
            Arrays.asList(DEVICE_ENDPOINT)
    )
}
