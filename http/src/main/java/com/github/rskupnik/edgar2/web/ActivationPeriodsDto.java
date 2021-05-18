package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.domain.ActivationPeriods;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ActivationPeriodsDto {

    private List<ActivationPeriodDto> activationPeriods;

    public ActivationPeriodsDto() {

    }

    public ActivationPeriods toDomainClass() {
        return new ActivationPeriods(
                activationPeriods == null ? Collections.emptyList() : activationPeriods.stream().map(ActivationPeriodDto::toDomainClass).collect(Collectors.toList())
        );
    }

    public List<ActivationPeriodDto> getActivationPeriods() {
        return activationPeriods;
    }

    public void setActivationPeriods(List<ActivationPeriodDto> activationPeriods) {
        this.activationPeriods = activationPeriods;
    }
}
