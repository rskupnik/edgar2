package com.github.rskupnik.edgar

import com.github.rskupnik.edgar.config.device.DeviceConfigStorage
import com.github.rskupnik.edgar.db.entity.DeviceEntity
import com.github.rskupnik.edgar.db.repository.DashboardRepository
import com.github.rskupnik.edgar.db.repository.DeviceRepository
import com.github.rskupnik.edgar.domain.CommandResponse
import com.github.rskupnik.edgar.domain.Device
import spock.lang.Specification

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

        when:
        edgar.sendCommand(deviceId, commandName, params)

        then:
        2 * deviceRepository.find(deviceId) >> Optional.of(entity)
        1 * deviceClient.sendCommand(_, _, _) >> new CommandResponse(200, new byte[] {}, Collections.emptyMap())
        1 * deviceRepository.save(deviceId, entity)
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

        when: "command is sent to device"
        edgar.sendCommand(deviceId, commandName, params)

        then: "all logic was executed"
        3 * deviceRepository.find(deviceId) >> Optional.of(entity)
        1 * deviceClient.sendCommand(_, _, _) >> new CommandResponse(500, new byte[] {}, Collections.emptyMap())
        2 * deviceRepository.save(deviceId, entity)

        and: "device was marked as not responsive"
        !entity.responsive
    }
}
