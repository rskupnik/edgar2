package com.github.rskupnik.edgar.domain;

public class ActivationPeriod {

    private final int startHour;
    private final int startMinute;
    private final int endHour;
    private final int endMinute;

    public ActivationPeriod(int startHour, int startMinute, int endHour, int endMinute) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }
}
