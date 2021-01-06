package com.github.rskupnik.edgar;

public class Edgar {

    private final EdgarDep dep;

    public Edgar(EdgarDep dep) {
        this.dep = dep;
    }

    public String test() {
        return dep.test();
    }
}
