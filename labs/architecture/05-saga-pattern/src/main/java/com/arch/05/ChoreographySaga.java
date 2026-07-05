package com.arch.saga;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChoreographySaga {
    private final Map<String, StepHandler> handlers = new ConcurrentHashMap<>();

    public void registerHandler(String eventType, StepHandler handler) {
        handlers.put(eventType, handler);
    }

    public void onEvent(String eventType, String context) {
        StepHandler handler = handlers.get(eventType);
        if (handler != null) {
            handler.handle(context);
        }
    }

    @FunctionalInterface
    public interface StepHandler {
        void handle(String context);
    }
}
