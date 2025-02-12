package com.github.rskupnik.edgar2.web.dto;

public class TaskTriggerRequest {

    private String taskId;
    private String data;

    public TaskTriggerRequest() {

    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
