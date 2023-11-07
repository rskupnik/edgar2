package com.github.rskupnik.edgar.assistant.events;

public sealed interface Event permits CommandIssuedEvent, RequestInputEvent, TriggerNextStepEvent {}
