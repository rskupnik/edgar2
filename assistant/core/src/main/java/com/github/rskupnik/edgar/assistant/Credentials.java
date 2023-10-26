package com.github.rskupnik.edgar.assistant;

public interface Credentials {
    String get(String key);
    void put(String key, String credential);
}
