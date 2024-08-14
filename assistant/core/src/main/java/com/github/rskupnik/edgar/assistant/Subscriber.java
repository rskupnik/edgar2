package com.github.rskupnik.edgar.assistant;

import com.github.rskupnik.edgar.assistant.events.Event;

public interface Subscriber {
    void update(Event event);
}
