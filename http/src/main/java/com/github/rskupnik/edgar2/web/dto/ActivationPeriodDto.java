package com.github.rskupnik.edgar2.web.dto;

import com.github.rskupnik.edgar.domain.ActivationPeriod;

public class ActivationPeriodDto {

    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    public ActivationPeriodDto() {}

    public ActivationPeriod toDomainClass() {
        return new ActivationPeriod(startHour, startMinute, endHour, endMinute);
    }

    public static ActivationPeriodDto fromDomainClass(ActivationPeriod activationPeriod) {
        ActivationPeriodDto dto = new ActivationPeriodDto();
        dto.setStartHour(activationPeriod.getStartHour());
        dto.setStartMinute(activationPeriod.getStartMinute());
        dto.setEndHour(activationPeriod.getEndHour());
        dto.setEndMinute(activationPeriod.getEndMinute());
        return dto;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }
}
