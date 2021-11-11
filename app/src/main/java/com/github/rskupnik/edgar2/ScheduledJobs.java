package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.Edgar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledJobs {

    private final Edgar edgar;

    @Autowired
    public ScheduledJobs(Edgar edgar) {
        this.edgar = edgar;
    }

    @Scheduled(fixedDelayString = "${devices.statusCheck.delay}", initialDelayString = "${devices.statusCheck.initialDelay}")
    public void checkDeviceStatus() {
        System.out.println("Checking device status");
        edgar.refreshDeviceStatus();
    }

    @Scheduled(fixedDelayString = "${devices.activationPeriodsCheck.delay}", initialDelayString = "${devices.activationPeriodsCheck.initialDelay}")
    public void checkActivationPeriods() {
        System.out.println("Checking activation periods");
        edgar.checkActivationPeriods();
    }

    @Scheduled(fixedDelayString = "${devices.rediscoverUnresponsiveDevices.delay}", initialDelayString = "${devices.rediscoverUnresponsiveDevices.initialDelay}")
    public void rediscoverUnresponsiveDevices() {
        System.out.println("Rediscovering unresponsive devices");
        edgar.rediscoverUnresponsiveDevices();
    }
}
