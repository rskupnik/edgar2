package com.github.rskupnik.edgar.assistant.task;

public record TaskRegistration(String command, Class<? extends Task> task) {}
