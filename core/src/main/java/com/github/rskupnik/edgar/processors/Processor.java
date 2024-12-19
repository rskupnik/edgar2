package com.github.rskupnik.edgar.processors;

public interface Processor<I, O> {
    String id();
    O process(I input);
}
