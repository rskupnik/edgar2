package com.github.rskupnik.edgar.assistant;

import java.util.Map;

public class ExplicitCredentials implements Credentials {

    private final Map<String, String> credentials;

    public ExplicitCredentials(Map<String, String> credentials) {
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
