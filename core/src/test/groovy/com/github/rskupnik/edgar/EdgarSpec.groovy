package com.github.rskupnik.edgar

import com.github.rskupnik.edgar.db.entity.DeviceEntity
import com.github.rskupnik.edgar.db.repository.DashboardRepository
import com.github.rskupnik.edgar.db.repository.DeviceRepository
import spock.lang.Specification

class EdgarSpec extends Specification {

    private final DeviceRepository deviceRepository = Mock(DeviceRepository)
    private final DashboardRepository dashboardRepository = Mock(DashboardRepository)
    private final Edgar edgar = new EdgarImpl(deviceRepository, dashboardRepository)

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

    def "should just pass"() {
        given:
        int arg1 = 2
        int arg2 = 2

        when:
        int result = arg1 + arg2

        then:
        result == 4
    }
}
