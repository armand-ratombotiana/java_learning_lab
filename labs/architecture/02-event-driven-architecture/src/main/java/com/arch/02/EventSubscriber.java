package com.arch.eventdriven;

@FunctionalInterface
public interface EventSubscriber {
    void onEvent(Event event);
}
