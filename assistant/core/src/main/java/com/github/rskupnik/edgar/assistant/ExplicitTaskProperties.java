package com.github.rskupnik.edgar.assistant;

import java.util.Map;

public class ExplicitTaskProperties implements TaskProperties {

    private final Map<String, String> credentials;

    public ExplicitTaskProperties(Map<String, String> credentials) {
        this.credentials = credentials;
    }

    @Override
    public String get(String key) {
        return credentials.get(key);
    }

    @Override
    public void put(String key, String credential) {
        credentials.put(key, credential);
    }
}
