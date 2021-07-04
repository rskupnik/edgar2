package com.github.rskupnik.edgar2.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar2.web.dto.DeviceLayoutDto;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LayoutLoader {

    private final Edgar edgar;
    private final ApplicationArguments args;
    private final ObjectMapper objectMapper;

    public LayoutLoader(Edgar edgar, ApplicationArguments args, ObjectMapper objectMapper) {
        this.edgar = edgar;
        this.args = args;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void loadLayouts() {
        List<String> values = args.getOptionValues("layout-file");
        if (values == null || values.size() <= 0)
            return;

        String filePath = values.get(0);
        System.out.println("Loading layouts from: " + filePath);
        try {
            var layouts = objectMapper.readValue(Paths.get(filePath).toFile(), new TypeReference<List<DeviceLayoutDto>>(){});
            edgar.registerLayouts(layouts.stream().map(DeviceLayoutDto::toDomainClass).collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
