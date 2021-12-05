package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.domain.CommandResponse;
import com.github.rskupnik.edgar2.web.dto.DeviceDto;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class DeviceController {

    private final Edgar edgar;

    @Autowired
    public DeviceController(Edgar edgar) {
        this.edgar = edgar;
    }

    @PostMapping("devices")
    public void registerDevice(@RequestBody DeviceDto device, HttpServletRequest request) {
        device.setIp(device.getIp() != null ? device.getIp() : request.getRemoteAddr());

        edgar.registerDevice(device.toDomainClass());   // TODO: Do something with the error case, change return code etc.
        System.out.println("Registered a new device");
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

        System.out.println("Sending command " + command + " to device " + deviceId);
        requestParams.forEach((k, v) -> System.out.println(k + ": " + v));

        CommandResponse response = edgar.sendCommand(deviceId, command, requestParams);
        System.out.println("Result: " + (response.getStatusCode() == HttpStatus.SC_OK));

        var springResponse = ResponseEntity.status(response.getStatusCode());
        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            springResponse.header(entry.getKey(), entry.getValue());
        }
        return springResponse.body(response.getBody());
    }
}
