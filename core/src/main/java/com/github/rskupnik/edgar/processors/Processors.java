package com.github.rskupnik.edgar.processors;

import com.github.rskupnik.edgar.config.device.passive.PassiveDeviceConfig;
import com.github.rskupnik.edgar.config.device.passive.ProcessorConfig;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Processors {

    private Processors() {}

    public Optional<Processor> byId(String id) {
        return switch(id) {
            case "toString" -> Optional.of(new ToStringProcessor());
            case "outputDiscordImage" -> Optional.of(null); // TODO
            default -> Optional.empty();
        };
    }

    // TODO: Call this when data is received
    // TODO: Refactor to untangle dependency on Config class? Could just accept Object input and a ProcessorConfig
    public void process(byte[] input, PassiveDeviceConfig device) {
        var processorConfigs = device.getProcessors();
        for (ProcessorConfig processorConfig : processorConfigs) {
            processInner(input, processorConfig);
        }
    }

    private void processInner(Object input, ProcessorConfig processorConfig) {
        var processor = byId((String) processorConfig.get("id"));
        if (processor.isEmpty())
            return;

        var output = processor.get().process(input);

        List<Map<String, Object>> furtherProcessors = (List<Map<String, Object>>) processorConfig.getOrDefault("processors", Collections.emptyMap());
        if (furtherProcessors.isEmpty())
            return;

        List<ProcessorConfig> furtherProcessorConfigs = furtherProcessors.stream().map(m -> {
            var pc = new ProcessorConfig();
            pc.putAll(m);
            return pc;
        }).toList();

        for (ProcessorConfig furtherProcessorConfig : furtherProcessorConfigs) {
            processInner(output, furtherProcessorConfig);
        }
    }
}
