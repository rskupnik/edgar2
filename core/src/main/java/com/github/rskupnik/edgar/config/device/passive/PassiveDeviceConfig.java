package com.github.rskupnik.edgar.config.device.passive;

import com.github.rskupnik.edgar.config.device.DeviceConfig;

import java.util.Collections;
import java.util.List;

public class PassiveDeviceConfig extends DeviceConfig {

    private List<ProcessorConfig> processors = Collections.emptyList();

    public List<ProcessorConfig> getProcessors() {
        return processors;
    }

    public void setProcessors(List<ProcessorConfig> processors) {
        this.processors = processors;
    }
}
