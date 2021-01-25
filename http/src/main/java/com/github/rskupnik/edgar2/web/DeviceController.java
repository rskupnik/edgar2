package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.domain.DeviceLayout;
import com.github.rskupnik.edgar.domain.EndpointType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

        List<DeviceLayout> testLayouts = new ArrayList<>();
        List<EndpointType> endpointTypes = new ArrayList<>();
        var endpointType = new EndpointType("/on", "toggle-large");
        endpointTypes.add(endpointType);
        var testLayout = new DeviceLayout("test4", endpointTypes);
        testLayouts.add(testLayout);
        edgar.registerLayouts(testLayouts);
    }

    @PostMapping("devices")
    public void registerDevice(@RequestBody DeviceDto device, HttpServletRequest request) {
        device.setIp(device.getIp() != null ? device.getIp() : request.getRemoteAddr());

        edgar.registerDevice(device.toDomainClass());   // TODO: Do something with the error case, change return code etc.
        System.out.println("Registered a new device");
    }

    @GetMapping("devices")
    public List<DeviceDto> getDevices() {
        return edgar.getLayouts(edgar.getDevices()).stream().map(t -> {
            var dto = DeviceDto.fromDomainClass(t._1);
            dto.getEndpoints().forEach(e -> e.setType(t._2.get(e.getPath())));
            return dto;
        }).collect(Collectors.toList());
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
