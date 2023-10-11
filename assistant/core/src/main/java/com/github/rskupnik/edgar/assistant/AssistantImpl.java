package com.github.rskupnik.edgar.assistant;

public class AssistantImpl implements Assistant {

    private Task currentTask = null;

    AssistantImpl() {
        Systems.Assistant = this;
        Systems.UserIO = new DiscordUserIO();
    }

    @Override
    public void processCommand(String cmd) {
        if (cmd.equals("pay gas")) {
            currentTask = new PayGasTask();
            currentTask.triggerNext();
        }
    }
}
