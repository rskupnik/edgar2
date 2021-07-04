package com.github.rskupnik.edgar2.web.dto;

import com.github.rskupnik.edgar.domain.EndpointBinding;

public class EndpointBindingDto {

    public String uiParam;
    public String deviceParam;

    public EndpointBindingDto() {

    }

    public static EndpointBindingDto fromDomainClass(EndpointBinding binding) {
        var dto = new EndpointBindingDto();
        dto.setUiParam(binding.getUiParam());
        dto.setDeviceParam(binding.getDeviceParam());
        return dto;
    }

    public EndpointBinding toDomainClass() {
        return new EndpointBinding(uiParam, deviceParam);
    }

    public String getUiParam() {
        return uiParam;
    }

    public void setUiParam(String uiParam) {
        this.uiParam = uiParam;
    }

    public String getDeviceParam() {
        return deviceParam;
    }

    public void setDeviceParam(String deviceParam) {
        this.deviceParam = deviceParam;
    }
}
