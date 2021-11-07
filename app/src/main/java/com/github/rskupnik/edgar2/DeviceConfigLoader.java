package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.Edgar;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DeviceConfigLoader {

    private final Edgar edgar;
    private final ApplicationArguments args;

    public DeviceConfigLoader(Edgar edgar, ApplicationArguments args) {
        this.edgar = edgar;
        this.args = args;
    }

    @PostConstruct
    private void loadDeviceConfig() {
        var optionValues = args.getOptionValues("deviceConfig");
        if (optionValues == null || optionValues.size() == 0)
            return;

        var filename = optionValues.get(0);
        System.out.println("Loading device config from file: " + filename);
        edgar.loadDeviceConfig(filename);
    }
}
