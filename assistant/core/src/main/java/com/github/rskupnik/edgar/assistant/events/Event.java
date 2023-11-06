package com.github.rskupnik.edgar.assistant.events;

// TODO: sealed?
public sealed interface Event permits CommandIssuedEvent, RequestInputEvent, TriggerNextStepEvent {
}
