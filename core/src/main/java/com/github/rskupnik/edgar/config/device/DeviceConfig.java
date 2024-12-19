package com.github.rskupnik.edgar.config.device;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.rskupnik.edgar.config.device.active.ActiveDeviceConfig;
import com.github.rskupnik.edgar.config.device.passive.PassiveDeviceConfig;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ActiveDeviceConfig.class, name = "Active"),
        @JsonSubTypes.Type(value = PassiveDeviceConfig.class, name = "Passive"),
})
public class DeviceConfig {

    private String id;
    private DeviceType type;

    public static DeviceConfig empty() {
        return new DeviceConfig();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }
}
