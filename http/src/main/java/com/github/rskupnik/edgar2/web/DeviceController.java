package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.domain.CommandResponse;
import com.github.rskupnik.edgar2.web.dto.DeviceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class DeviceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    private final Edgar edgar;

    @Autowired
    public DeviceController(Edgar edgar) {
        this.edgar = edgar;
    }

    @PostMapping("devices")
    public void registerDevice(@RequestBody DeviceDto device, HttpServletRequest request) {
        device.setIp(device.getIp() != null ? device.getIp() : request.getRemoteAddr());

        edgar.registerDevice(device.toDomainClass());   // TODO: Do something with the error case, change return code etc.

        logger.info("Registered a new device");
    }

    @GetMapping("devices")
    public List<DeviceDto> getDevices() {
        return edgar.getDevices()
                .stream()
                .map(DeviceDto::fromDomainClass)
                .collect(Collectors.toList());
    }

    @PostMapping("devices/command/{deviceId}/**")
    public ResponseEntity<?> sendCommand(@PathVariable("deviceId") String deviceId, HttpServletRequest request,
                                              @RequestParam Map<String, String> requestParams) {
        String command = request.getRequestURI()
                .split(request.getContextPath() + "/devices/" + deviceId)[1];

        logger.info("Sending command " + command + " to device " + deviceId);
        requestParams.forEach((k, v) -> logger.debug(k + ": " + v));

        CommandResponse response = edgar.sendCommand(deviceId, command, requestParams);

        return convertToSpringResponse(response);
    }

    @PostMapping(value = "devices/data/{deviceId}")
    public ResponseEntity<?> postDeviceData(@PathVariable("deviceId") String deviceId, HttpServletRequest request) throws IOException {
        logger.info("Received request for device: {}", deviceId);
        logger.info("Bytes available: {}", request.getInputStream().available());
        CommandResponse response = edgar.storeData(deviceId, request.getInputStream().readAllBytes());
        return convertToSpringResponse(response);
    }

    @GetMapping(value = "devices/data/{deviceId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> getDeviceData(@PathVariable("deviceId") String deviceId) {
        return ResponseEntity.ok(edgar.getData(deviceId));
    }

    private ResponseEntity<?> convertToSpringResponse(CommandResponse response) {
        logger.debug("Response: {}", response.getStatusCode());
        var springResponse = ResponseEntity.status(response.getStatusCode());
        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            springResponse.header(entry.getKey(), entry.getValue());
        }

        if (response.getBody() != null && response.getBody().length > 0)
            return springResponse.body(response.getBody());
        else
            return springResponse.build();
    }
}
