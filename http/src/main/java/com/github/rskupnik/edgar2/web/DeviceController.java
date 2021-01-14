package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.domain.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DeviceController {

    private final Edgar edgar;

    @Autowired
    public DeviceController(Edgar edgar) {
        this.edgar = edgar;
    }

    @PostMapping("devices")
    public void registerDevice(@RequestBody DeviceDto device) {
        edgar.registerDevice(device.toDomainClass());   // TODO: Do something with the error case, change return code etc.
        System.out.println("Registered a new device");
    }

    @GetMapping("devices")
    public List<DeviceDto> getDevices() {
        return edgar.getDevices().stream().map(DeviceDto::fromDomainClass).collect(Collectors.toList());
    }

    @PostMapping("devices/{deviceName}/**")
    public void sendCommand(@PathVariable("deviceName") String deviceName, HttpServletRequest request,
                            @RequestParam Map<String, String> requestParams) {
        String command = request.getRequestURI()
                .split(request.getContextPath() + "/devices/" + deviceName)[1];
        System.out.println("Sending command " + command + " to device " + deviceName);
        requestParams.forEach((k, v) -> System.out.println(k + ": " + v));
        boolean success = edgar.sendCommand(deviceName, command, requestParams);
        System.out.println("Result: " + success);
    }
}
