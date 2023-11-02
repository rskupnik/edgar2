package com.github.rskupnik.edgar.assistant;

import java.util.HashMap;
import java.util.Map;

// TODO: Load in config from external file (sensitive stuff mainly)
// TODO: Pull specific tasks to separate packages, outside :core
// TODO: Do we need event to communicate between systems internally? Assistant <-> Task <-> WebCrawler <-> Discord etc
public class AssistantImpl implements Assistant {

    private Map<String, Class<? extends Task>> availableCommands = new HashMap<>();

    private Task currentTask = null;

    AssistantImpl(Map<String, String> credentials) {
        Systems.Credentials = new CredentialsFromDisk();
        credentials.forEach((key, value) -> Systems.Credentials.put(key, value));

        Systems.Assistant = this;
        Systems.UserIO = new DiscordUserIO();

        // TODO: All this logic should probably be pulled out somehwere else at some point
        registerCommand("pay gas", PayGasTask.class);
        registerCommand("check power bill", CheckPowerBillDueTask.class);
        registerCommand("test", TestTask.class);
    }

    @Override
    public void registerCommand(String cmd, Class<? extends Task> taskClass) {
        availableCommands.put(cmd, taskClass);
    }

    @Override
    public void processCommand(String cmd) {
        var task = availableCommands.get(cmd);
        if (task == null) {
            Systems.UserIO.output("Available commands: " + collateAvailableCommands());
            return;
        }

        try {
            // TODO: This only supports one task at a time
            // Which could be an issue with scheduled tasks in the future
            currentTask = task.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        currentTask.triggerNext();
    }

    private String collateAvailableCommands() {
        return availableCommands.keySet()
                .stream()
                .reduce("", (acc, key) -> acc.concat("\""+key+"\" "));
    }
}
