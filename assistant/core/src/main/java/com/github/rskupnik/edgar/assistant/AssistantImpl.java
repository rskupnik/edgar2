package com.github.rskupnik.edgar.assistant;

import com.github.rskupnik.edgar.assistant.events.*;
import com.github.rskupnik.edgar.assistant.task.StepTask;
import com.github.rskupnik.edgar.assistant.task.Task;
import com.github.rskupnik.edgar.assistant.task.TaskRegistration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

// TODO: Pull specific tasks to separate packages, outside :core
public class AssistantImpl implements Assistant, Subscriber {

    private final Supplier<WebCrawler> webCrawlerSupplier;
    private final UserIO userIO;
    private final TaskProperties taskProperties;

    private final Map<String, TaskDescriptor> availableCommands = new HashMap<>();

    private Task currentTask = null;

    AssistantImpl(
            UserIO userIO,
            TaskProperties taskProperties,
            Supplier<WebCrawler> webCrawlerSupplier,
            TaskRegistration... taskRegistrations
    ) {
        this.webCrawlerSupplier = webCrawlerSupplier;
        this.taskProperties = taskProperties;
        this.userIO = userIO;

        System.out.println("STARTING ASSISTANT");

        EventManager.subscribe(CommandIssuedEvent.class, this);
        EventManager.subscribe(TriggerNextStepEvent.class, this);
        EventManager.subscribe(TerminationCheckEvent.class, this);

        EventManager.subscribe(RequestInputEvent.class, userIO);
        EventManager.subscribe(TaskTerminatedEvent.class, userIO);

        Arrays.stream(taskRegistrations).forEach(reg -> registerCommand(reg.command(), new TaskDescriptor(reg.task(), reg.params())));

        startJanitorThread();
    }

    @Override
    public void registerCommand(String cmd, TaskDescriptor taskDescriptor) {
        availableCommands.put(cmd, taskDescriptor);
    }

    @Override
    public void processCommand(String cmd) {
        var taskDescriptor = availableCommands.get(cmd);
        if (taskDescriptor == null) {
            userIO.output("Available commands: " + collateAvailableCommands());
            return;
        }

        launchTask(taskDescriptor);
    }

    @Override
    public void processCommandHeadless(String cmd, String data) {
        var taskDescriptor = availableCommands.get(cmd);
        if (taskDescriptor == null)
            return;

        // TODO: Very ugly hack but what can you do
        var bla = new HashMap<>(taskDescriptor.params());
        bla.put("data", data);
        var td = new TaskDescriptor(taskDescriptor.taskClass(), bla);
        launchTask(td);
    }

    private void launchTask(TaskDescriptor taskDescriptor) {
        try {
            // TODO: This only supports one task at a time - might be fine if we implement queuing, not fine if multitasking
            currentTask = taskDescriptor
                    .taskClass()
                    .getDeclaredConstructor(TaskProperties.class, UserIO.class, Supplier.class, Map.class)
                    .newInstance(taskProperties, userIO, webCrawlerSupplier, taskDescriptor.params());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (currentTask instanceof StepTask st) {
            // TODO: Queue tasks?
            st.triggerNext();
        }
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
                if (currentTask != null && currentTask instanceof StepTask st) {
                    st.triggerNext();
                }
            }
            case TerminationCheckEvent terminationCheckEvent -> {
                if (currentTask == null)
                    return;

                if (currentTask instanceof StepTask st && st.shouldBeTerminated()) {
                    st.terminate();
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
