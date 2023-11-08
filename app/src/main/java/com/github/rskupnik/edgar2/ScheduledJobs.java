package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.Edgar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledJobs {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledJobs.class);

    private final Edgar edgar;

    @Autowired
    public ScheduledJobs(Edgar edgar) {
        this.edgar = edgar;
    }

    @Scheduled(fixedDelayString = "${devices.rediscoverUnresponsiveDevices.delay}", initialDelayString = "${devices.rediscoverUnresponsiveDevices.initialDelay}")
    public void rediscoverUnresponsiveDevices() {
        logger.info("Cleaning up and rediscovering unresponsive devices");
        edgar.cleanupUnresponsiveDevices();
        edgar.rediscoverUnresponsiveDevices();
    }
}
