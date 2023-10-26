package com.github.rskupnik.edgar.assistant;

import java.util.HashMap;
import java.util.Map;

public class CredentialsFromDisk implements Credentials {

    private final Map<String, String> credentials = new HashMap<>();

    @Override
    public String get(String key) {
        return credentials.get(key);
    }

    @Override
    public void put(String key, String credential) {
        credentials.put(key, credential);
    }
}
