package com.github.rskupnik.edgar.domain;

import java.util.List;

public class ActivationPeriods {

    private final List<ActivationPeriod> periods;

    public ActivationPeriods(List<ActivationPeriod> periods) {
        this.periods = periods;
    }

    public List<ActivationPeriod> getPeriods() {
        return periods;
    }
}
