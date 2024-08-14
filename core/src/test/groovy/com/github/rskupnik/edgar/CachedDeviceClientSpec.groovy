package com.github.rskupnik.edgar

import com.github.rskupnik.edgar.domain.CommandResponse
import com.github.rskupnik.edgar.domain.Device
import com.github.rskupnik.edgar.domain.DeviceEndpoint
import spock.lang.Specification

class CachedDeviceClientSpec extends Specification {

    private final Map<String, Integer> cacheTimeStorage = new HashMap<>()
    private final DeviceClient delegateClient = Mock(DeviceClient)
    private final DeviceClient deviceClient = new CachedDeviceClient(delegateClient, {
        key -> cacheTimeStorage.get(key)
    })

    def "should directly delegate isAlive call"() {
        given:
        Device device = Dummies.DEVICE

        when:
        deviceClient.isAlive(device)

        then:
        1 * delegateClient.isAlive(device)
    }

    def "should delegate command without caching if cache time is 0"() {
        given: "a device"
        Device device = Dummies.DEVICE
        DeviceEndpoint endpoint = device.endpoints.get(0)
        Map<String, String> params = Collections.emptyMap()

        and: "cache time is stored as 0"
        cacheTimeStorage.put(device.getId() + ":" + endpoint.getPath(), 0)

        and: "an expected response"
        CommandResponse expectedResponse = new CommandResponse(200, "body".getBytes(), Collections.emptyMap())

        when:
        CommandResponse response = deviceClient.sendCommand(device, endpoint, params)

        then:
        1 * delegateClient.sendCommand(device, endpoint, params) >> expectedResponse
        response == expectedResponse
    }

    def "should cache response and return the cached response on next call"() {
        given: "a device"
        Device device = Dummies.DEVICE
        DeviceEndpoint endpoint = device.endpoints.get(0)
        Map<String, String> params = Collections.emptyMap()

        and: "cache time is stored as 5s"
        cacheTimeStorage.put(device.getId() + ":" + endpoint.getPath(), 5000)

        and: "predefined responses"
        CommandResponse predefinedFirstResponse = new CommandResponse(200, "success".getBytes(), Collections.emptyMap())
        CommandResponse predefinedSecondResponse = new CommandResponse(500, "fail".getBytes(), Collections.emptyMap())

        when:
        CommandResponse firstResponse = deviceClient.sendCommand(device, endpoint, params)
        CommandResponse secondResponse = deviceClient.sendCommand(device, endpoint, params)

        then:
        1 * delegateClient.sendCommand(device, endpoint, params) >>> [predefinedFirstResponse, predefinedSecondResponse]
        firstResponse == predefinedFirstResponse
        secondResponse == predefinedFirstResponse
    }

    def "should cache response but ignore it if cache time has passed"() {
        given: "input parameters"
        Device device = Dummies.DEVICE
        DeviceEndpoint endpoint = device.endpoints.get(0)
        Map<String, String> params = Collections.emptyMap()

        and: "cache time is stored as 50ms"
        cacheTimeStorage.put(device.getId() + ":" + endpoint.getPath(), 50)

        and: "predefined responses"
        CommandResponse predefinedFirstResponse = new CommandResponse(200, "success".getBytes(), Collections.emptyMap())
        CommandResponse predefinedSecondResponse = new CommandResponse(500, "fail".getBytes(), Collections.emptyMap())

        when: "first call is made and cached"
        CommandResponse firstResponse = deviceClient.sendCommand(device, endpoint, params)

        and: "some time has passed (more than cache time)"
        Thread.sleep(60)

        and: "second call is made"
        CommandResponse secondResponse = deviceClient.sendCommand(device, endpoint, params)

        then: "two calls were delegate and responses differ"
        2 * delegateClient.sendCommand(device, endpoint, params) >>> [predefinedFirstResponse, predefinedSecondResponse]
        firstResponse == predefinedFirstResponse
        secondResponse == predefinedSecondResponse
    }
}
