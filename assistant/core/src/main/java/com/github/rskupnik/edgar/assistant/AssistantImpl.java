package com.github.rskupnik.edgar.assistant;

import com.github.rskupnik.edgar.assistant.events.*;
import com.github.rskupnik.edgar.assistant.tasks.CheckPowerBillDueTask;
import com.github.rskupnik.edgar.assistant.tasks.PayGasTask;
import com.github.rskupnik.edgar.assistant.tasks.Task;
import com.github.rskupnik.edgar.assistant.tasks.TestTask;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// TODO: Pull specific tasks to separate packages, outside :core
public class AssistantImpl implements Assistant, Subscriber {

    private final Supplier<WebCrawler> webCrawlerSupplier;
    private final UserIO userIO;
    private final Credentials credentials;

    private final Map<String, Class<? extends Task>> availableCommands = new HashMap<>();

    private Task currentTask = null;

    AssistantImpl(UserIO userIO, Credentials credentials, Supplier<WebCrawler> webCrawlerSupplier) {
        this.webCrawlerSupplier = webCrawlerSupplier;
        this.credentials = credentials;
        this.userIO = userIO;

        EventManager.subscribe(CommandIssuedEvent.class, this);
        EventManager.subscribe(TriggerNextStepEvent.class, this);

        EventManager.subscribe(RequestInputEvent.class, userIO);

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
            // TODO: Pass in constructor somehow?
            currentTask.credentials = credentials;
            currentTask.userIO = userIO;
            currentTask.webCrawlerSupplier = webCrawlerSupplier;
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
