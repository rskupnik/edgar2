package com.github.rskupnik.edgar.assistant;

import com.github.rskupnik.edgar.assistant.events.*;
import com.github.rskupnik.edgar.assistant.task.Task;
import com.github.rskupnik.edgar.assistant.task.TaskRegistration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

// TODO: Pull specific tasks to separate packages, outside :core
public class AssistantImpl implements Assistant, Subscriber {

    private final Supplier<WebCrawler> webCrawlerSupplier;
    private final UserIO userIO;
    private final Credentials credentials;

    private final Map<String, Class<? extends Task>> availableCommands = new HashMap<>();

    private Task currentTask = null;

    AssistantImpl(
            UserIO userIO,
            Credentials credentials,
            Supplier<WebCrawler> webCrawlerSupplier,
            TaskRegistration... taskRegistrations
    ) {
        this.webCrawlerSupplier = webCrawlerSupplier;
        this.credentials = credentials;
        this.userIO = userIO;

        System.out.println("STARTING ASSISTANT");

        EventManager.subscribe(CommandIssuedEvent.class, this);
        EventManager.subscribe(TriggerNextStepEvent.class, this);
        EventManager.subscribe(TerminationCheckEvent.class, this);

        EventManager.subscribe(RequestInputEvent.class, userIO);
        EventManager.subscribe(TaskTerminatedEvent.class, userIO);

        Arrays.stream(taskRegistrations).forEach(reg -> registerCommand(reg.command(), reg.task()));

        startJanitorThread();
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
            // TODO: This only supports one task at a time - might be fine if we implement queuing, not fine if multitasking
            currentTask = task
                    .getDeclaredConstructor(Credentials.class, UserIO.class, Supplier.class)
                    .newInstance(credentials, userIO, webCrawlerSupplier);
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
        switch (event) {
            case CommandIssuedEvent commandIssuedEvent -> processCommand(commandIssuedEvent.command());
            case TriggerNextStepEvent triggerNextStepEvent -> {
                if (currentTask != null) {
                    currentTask.triggerNext();
                }
            }
            case TerminationCheckEvent terminationCheckEvent -> {
                if (currentTask == null)
                    return;

                if (currentTask.shouldBeTerminated()) {
                    currentTask.terminate();
                    currentTask = null;
                }
            }
            default -> {}
        }
    }

    private void startJanitorThread() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(() -> {
                    EventManager.notify(new TerminationCheckEvent());
                },10, 10, TimeUnit.SECONDS);
    }
}
