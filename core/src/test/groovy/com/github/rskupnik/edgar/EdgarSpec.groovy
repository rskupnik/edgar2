package com.github.rskupnik.edgar

import com.github.rskupnik.edgar.config.device.DeviceConfig
import com.github.rskupnik.edgar.config.device.DeviceConfigStorage
import com.github.rskupnik.edgar.db.entity.DeviceEntity
import com.github.rskupnik.edgar.db.repository.DashboardRepository
import com.github.rskupnik.edgar.db.repository.DeviceRepository
import com.github.rskupnik.edgar.domain.CommandResponse
import com.github.rskupnik.edgar.domain.Device
import com.github.rskupnik.edgar.domain.DeviceEndpoint
import spock.lang.Specification

import java.time.Instant

class EdgarSpec extends Specification {

    private final DeviceRepository deviceRepository = Mock(DeviceRepository)
    private final DashboardRepository dashboardRepository = Mock(DashboardRepository)
    private final DeviceConfigStorage deviceConfigStorage = new DeviceConfigStorage()
    private final DeviceClient deviceClient = Mock(DeviceClient)
    private final Edgar edgar = new EdgarImpl(deviceRepository, dashboardRepository, deviceConfigStorage, deviceClient)

    def "should register new device"() {
        given:
        def device = Dummies.DEVICE

        when:
        edgar.registerDevice(device)

        then:
        1 * deviceRepository.find(device.getId()) >> Optional.empty()
        1 * deviceRepository.save(device.getId(), _)
    }

    def "should set entity responsive on register if device already exists"() {
        given:
        def device = Dummies.DEVICE
        def entity = DeviceEntity.fromDomainObject(device)
        entity.setResponsive(false)

        when:
        edgar.registerDevice(device)

        then:
        1 * deviceRepository.find(device.getId()) >> Optional.of(entity)
        entity.isResponsive()
        1 * deviceRepository.save(device.getId(), _)
    }

    def "should retrieve devices"() {
        when:
        List<Device> devices = edgar.getDevices()

        then:
        1 * deviceRepository.findAll() >> Arrays.asList(DeviceEntity.fromDomainObject(Dummies.DEVICE))
        devices.size() == 1
        devices.get(0) == Dummies.DEVICE
    }

    def "should send command to device"() {
        given:
        String deviceId = Dummies.DEVICE.getId()
        String commandName = Dummies.DEVICE_ENDPOINT.path
        def params = new HashMap<String, String>()
        DeviceEntity entity = DeviceEntity.fromDomainObject(Dummies.DEVICE)
        long lastSuccessfulResponseTimestamp = entity.getLastSuccessResponseTimestamp()

        when:
        edgar.sendCommand(deviceId, commandName, params)

        and: "a tiny bit of time has passed to emulate network call"
        Thread.sleep(10)

        then: "all logic executed"
        2 * deviceRepository.find(deviceId) >> Optional.of(entity)
        1 * deviceClient.sendCommand(_, _, _) >> new CommandResponse(200, new byte[] {}, Collections.emptyMap())
        1 * deviceRepository.save(deviceId, entity)

        and: "last successful response timestamp updated"
        entity.getLastSuccessResponseTimestamp() > lastSuccessfulResponseTimestamp
    }

    def "should not send command if device doesn't exist"() {
        given:
        String deviceId = Dummies.DEVICE.getId()
        String commandName = Dummies.DEVICE_ENDPOINT.path
        def params = new HashMap<String, String>()
        DeviceEntity entity = DeviceEntity.fromDomainObject(Dummies.DEVICE)

        when:
        CommandResponse response = edgar.sendCommand(deviceId, commandName, params)

        then:
        1 * deviceRepository.find(deviceId) >> Optional.empty()
        0 * deviceClient.sendCommand(_, _, _)
        0 * deviceRepository.save(deviceId, entity)
        response.error
    }

    def "should not send command if device is unresponsive"() {
        given:
        String deviceId = Dummies.DEVICE.getId()
        String commandName = Dummies.DEVICE_ENDPOINT.path
        def params = new HashMap<String, String>()
        DeviceEntity entity = DeviceEntity.fromDomainObject(Dummies.DEVICE)
        entity.responsive = false

        when:
        CommandResponse response = edgar.sendCommand(deviceId, commandName, params)

        then:
        1 * deviceRepository.find(deviceId) >> Optional.of(entity)
        0 * deviceClient.sendCommand(_, _, _) >> new CommandResponse(200, new byte[] {}, Collections.emptyMap())
        0 * deviceRepository.save(deviceId, entity)
        response.error
    }

    def "should not send command if endpoint not found"() {
        given:
        String deviceId = Dummies.DEVICE.getId()
        String commandName = Dummies.DEVICE_ENDPOINT.path
        def params = new HashMap<String, String>()
        DeviceEntity entity = DeviceEntity.fromDomainObject(Dummies.DEVICE)
        entity.endpoints = Collections.emptyList()

        when:
        CommandResponse response = edgar.sendCommand(deviceId, commandName, params)

        then:
        1 * deviceRepository.find(deviceId) >> Optional.of(entity)
        0 * deviceClient.sendCommand(_, _, _) >> new CommandResponse(200, new byte[] {}, Collections.emptyMap())
        0 * deviceRepository.save(deviceId, entity)
        response.error
    }

    def "should send command to device and set responsive to false on error result"() {
        given: "a properly defined device"
        String deviceId = Dummies.DEVICE.getId()
        String commandName = Dummies.DEVICE_ENDPOINT.path
        def params = new HashMap<String, String>()
        DeviceEntity entity = DeviceEntity.fromDomainObject(Dummies.DEVICE)
        long lastSuccessfulResponseTimestamp = entity.getLastSuccessResponseTimestamp()

        when: "command is sent to device"
        edgar.sendCommand(deviceId, commandName, params)

        then: "all logic was executed"
        2 * deviceRepository.find(deviceId) >> Optional.of(entity)
        1 * deviceClient.sendCommand(_, _, _) >> new CommandResponse(500, new byte[] {}, Collections.emptyMap())
        1 * deviceRepository.save(deviceId, entity)

        and: "device was marked as not responsive"
        !entity.responsive

        and: "last successful response timestamp was not changed"
        entity.getLastSuccessResponseTimestamp() == lastSuccessfulResponseTimestamp
    }

    def "should delete unresponsive device that timed out"() {
        given: "an unresponsive device"
        Device device = Dummies.DEVICE_UNRESPONSIVE
        DeviceEndpoint endpoint = device.endpoints.get(0)
        DeviceEntity entity = DeviceEntity.fromDomainObject(device)

        and: "timeout set to 1s"
        int timeout = 1

        and: "config with timeout"
        DeviceConfig config = Dummies.deviceConfig(device.getId(), timeout, Arrays.asList(Dummies.endpointConfig(endpoint.path, 0)))
        deviceConfigStorage.save(config)

        and: "last known successful response was from before the timeout"
        entity.setLastSuccessResponseTimestamp(Instant.now().toEpochMilli() - (timeout * 1002))

        when:
        edgar.cleanupUnresponsiveDevices()

        then:
        1 * deviceRepository.findAll() >> Arrays.asList(entity)
        1 * deviceRepository.delete(device.getId())
    }

    def "should not delete unresponsive device that didn't time out yet"() {
        given: "an unresponsive device"
        Device device = Dummies.DEVICE_UNRESPONSIVE
        DeviceEndpoint endpoint = device.endpoints.get(0)
        DeviceEntity entity = DeviceEntity.fromDomainObject(device)

        and: "timeout set to 1s"
        int timeout = 1

        and: "config with timeout"
        DeviceConfig config = Dummies.deviceConfig(device.getId(), timeout, Arrays.asList(Dummies.endpointConfig(endpoint.path, 0)))
        deviceConfigStorage.save(config)

        and: "last known successful response was from after the timeout"
        entity.setLastSuccessResponseTimestamp(Instant.now().toEpochMilli() - 50)

        when:
        edgar.cleanupUnresponsiveDevices()

        then:
        1 * deviceRepository.findAll() >> Arrays.asList(entity)
        0 * deviceRepository.delete(device.getId())
    }

    def "should not delete unresponsive device if config not found"() {
        given: "an unresponsive device"
        Device device = Dummies.DEVICE_UNRESPONSIVE
        DeviceEntity entity = DeviceEntity.fromDomainObject(device)

        when:
        edgar.cleanupUnresponsiveDevices()

        then:
        1 * deviceRepository.findAll() >> Arrays.asList(entity)
        0 * deviceRepository.delete(device.getId())
    }

    def "should not delete a responsive device"() {
        given: "a responsive device"
        Device device = Dummies.DEVICE
        DeviceEntity entity = DeviceEntity.fromDomainObject(device)

        when:
        edgar.cleanupUnresponsiveDevices()

        then:
        1 * deviceRepository.findAll() >> Arrays.asList(entity)
        0 * deviceRepository.delete(device.getId())
    }

    def "should rediscover unresponsive device"() {
        given: "an unresponsive device"
        Device device = Dummies.DEVICE_UNRESPONSIVE
        DeviceEntity entity = DeviceEntity.fromDomainObject(device)

        when:
        edgar.rediscoverUnresponsiveDevices()

        then:
        1 * deviceRepository.findAll() >> Arrays.asList(entity)
        1 * deviceClient.isAlive(_) >> true
        1 * deviceRepository.save(device.getId(), entity)
        entity.responsive
    }

    def "should not rediscover responsive device"() {
        given: "a responsive device"
        Device device = Dummies.DEVICE
        DeviceEntity entity = DeviceEntity.fromDomainObject(device)

        when:
        edgar.rediscoverUnresponsiveDevices()

        then:
        1 * deviceRepository.findAll() >> Arrays.asList(entity)
        0 * deviceClient.isAlive(_) >> true
        0 * deviceRepository.save(device.getId(), entity)
        entity.responsive
    }
}
