package com.github.rskupnik.edgar

import com.github.rskupnik.edgar.config.device.DeviceConfig
import com.github.rskupnik.edgar.config.device.active.EndpointConfig
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

    static final Device DEVICE_UNRESPONSIVE = new Device(
            "dummyDeviceId",
            "dummyDevice",
            "127.0.0.1",
            false,
            Arrays.asList(DEVICE_ENDPOINT)
    )

    static deviceConfig(String id, int unresponsiveTimeout, List<EndpointConfig> endpointConfigs) {
        DeviceConfig config = new DeviceConfig()
        config.setId(id)
        config.setUnresponsiveTimeout(unresponsiveTimeout)
        config.setEndpoints(endpointConfigs)
        return config
    }

    static endpointConfig(String path, int cachePeriod) {
        EndpointConfig config = EndpointConfig.empty()
        config.setPath(path)
        config.setCachePeriod(cachePeriod)
        return config
    }
}
