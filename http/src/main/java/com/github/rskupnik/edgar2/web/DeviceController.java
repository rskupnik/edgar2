package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.domain.DeviceStatus;
import com.github.rskupnik.edgar.domain.EndpointBinding;
import com.github.rskupnik.edgar.domain.EndpointLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        return edgar.getLayouts(edgar.getDevices()).stream().map(t -> {
            var dto = DeviceDto.fromDomainClass(t._1);
            var deviceStatus = edgar.getDeviceStatus(t._1.getId()).orElse(DeviceStatus.unknown());
            dto.getEndpoints().forEach(e -> {
                var layout = findEndpointLayout(t._2.getEndpoints(), e.getPath());
                if (layout.isEmpty()) {
                    System.out.println("ERROR, layout not present");
                    return;
                }
                e.setType(layout.get().getType());
                //e.setBindings(convertBindings(layout.get().getBindings(), deviceStatus));
            });
            dto.setStatus(DeviceStatusDto.fromDomainClass(deviceStatus));

            // TODO: REMOVE
            var endpoints = new HashMap<String, Map<String, String>>();
            dto.getEndpoints().forEach(e -> endpoints.put(e.getPath(), Map.of("enabled", "true")));
            dto.getStatus().setEndpoints(endpoints);

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

    private Optional<EndpointLayout> findEndpointLayout(List<EndpointLayout> list, String path) {
        return list.stream().filter(l -> l.getPath().equals(path)).findFirst();
    }

    private Map<String, String> convertBindings(List<EndpointBinding> bindings, DeviceStatus status) {
        // TODO: Refactor to be more functional
        Map<String, String> output = new HashMap<>();
        bindings.forEach(b -> {
            output.put(b.getUiParam(), status.getData().get(b.getDeviceParam()));
        });
        return output;
    }
}
