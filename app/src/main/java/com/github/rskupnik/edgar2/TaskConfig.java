package com.github.rskupnik.edgar2;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties
public class TaskConfig {

    private Map<String, Object> tasks = new HashMap<>();

    public Map<String, Object> getTasks() {
        return tasks;
    }

    public void setTasks(Map<String, Object> tasks) {
        this.tasks = tasks;
    }
}
