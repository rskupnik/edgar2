package com.github.rskupnik.edgar.assistant;

import java.util.HashMap;
import java.util.Map;

// TODO: Pull specific tasks to separate packages, outside :core
public class AssistantImpl implements Assistant, Subscriber {

    private final UserIO userIO;
    private final Credentials credentials;

    private final Map<String, Class<? extends Task>> availableCommands = new HashMap<>();

    private Task currentTask = null;

    AssistantImpl(Map<String, String> credentials) {
        this.credentials = new CredentialsFromDisk();
        credentials.forEach(this.credentials::put);

        this.userIO = new DiscordUserIO(this.credentials);

        EventManager.subscribe(CommandIssuedEvent.class, this);
        EventManager.subscribe(TriggerNextStepEvent.class, this);

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
            userIO.output("Available commands: " + collateAvailableCommands());
            return;
        }

        try {
            // TODO: This only supports one task at a time
            // Which could be an issue with scheduled tasks in the future
            currentTask = task.getDeclaredConstructor().newInstance();
            currentTask.credentials = credentials;
            currentTask.userIO = userIO;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: Queue tasks?
        currentTask.triggerNext();
    }

    private String collateAvailableCommands() {
        return availableCommands.keySet()
                .stream()
                .reduce("", (acc, key) -> acc.concat("\""+key+"\" "));
    }

    @Override
    public void update(Event event) {
        if (event instanceof CommandIssuedEvent) {
            processCommand(((CommandIssuedEvent) event).command());
        } else if (event instanceof TriggerNextStepEvent) {
            if (currentTask != null) {
                currentTask.triggerNext();
            }
        }
    }
}
