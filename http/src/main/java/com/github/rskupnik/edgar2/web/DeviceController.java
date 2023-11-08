package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.domain.CommandResponse;
import com.github.rskupnik.edgar2.web.dto.DeviceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("devices/{deviceId}/**")
    public ResponseEntity<?> sendCommand(@PathVariable("deviceId") String deviceId, HttpServletRequest request,
                                              @RequestParam Map<String, String> requestParams) {
        String command = request.getRequestURI()
                .split(request.getContextPath() + "/devices/" + deviceId)[1];

        logger.info("Sending command " + command + " to device " + deviceId);
        requestParams.forEach((k, v) -> logger.debug(k + ": " + v));

        CommandResponse response = edgar.sendCommand(deviceId, command, requestParams);
        logger.debug("Result: " + (response.getStatusCode() == 200));

        var springResponse = ResponseEntity.status(response.getStatusCode());
        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            springResponse.header(entry.getKey(), entry.getValue());
        }
        return springResponse.body(response.getBody());
    }
}
