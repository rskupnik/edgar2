package com.github.rskupnik.edgar.assistant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {

    private static final Map<Class<? extends Event>, List<Subscriber>> subscribers = new HashMap<>();

    static void subscribe(Class<? extends Event> event, Subscriber subscriber) {
        getBucket(event).add(subscriber);
    }

    static void notify(Event event) {
        getBucket(event.getClass()).forEach(s -> s.update(event));
    }

    private static List<Subscriber> getBucket(Class<? extends Event> event) {
        subscribers.putIfAbsent(event, new ArrayList<>());
        return subscribers.get(event);
    }
}
