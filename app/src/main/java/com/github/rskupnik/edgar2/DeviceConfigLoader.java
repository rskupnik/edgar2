package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.Edgar;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
public class DeviceConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(DeviceConfigLoader.class);

    private final Edgar edgar;
    private final ApplicationArguments args;

    public DeviceConfigLoader(Edgar edgar, ApplicationArguments args) {
        this.edgar = edgar;
        this.args = args;
    }

    @PostConstruct
    public void loadDeviceConfig() {
        System.out.println("Device Config Loader");
        logger.info("Looking for device config");
        var optionValues = args.getOptionValues("deviceConfig");
        if (optionValues == null || optionValues.size() == 0) {
            logger.info("Device config not found");
            return;
        }

        var filename = optionValues.get(0);
//        var filename = "device-config.json";
        logger.info("Loading device config from file: " + filename);
        System.out.println("Loading device config from file: " + filename);
        edgar.loadDeviceConfig(filename);
    }
}
