package com.github.rskupnik.edgar.processors;

public class ToStringProcessor implements Processor<Object, String> {

    @Override
    public String id() {
        return "toString";
    }

    @Override
    public String process(Object input) {
        return input.toString();
    }
}
