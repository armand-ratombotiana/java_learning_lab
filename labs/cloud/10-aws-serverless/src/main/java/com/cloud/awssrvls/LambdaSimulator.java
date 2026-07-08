package com.cloud.awssrvls;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class LambdaSimulator {

    public record Context(String functionName, int memoryMB, int timeoutSeconds) {}

    public interface LambdaFunction {
        Object handleRequest(Object event, Context context);
    }

    public static class StepFunctionWorkflow {
        public record State(String name, String type, String next, Map<String, Object> config) {}
        private final Map<String, State> states = new LinkedHashMap<>();

        public void addState(State state) { states.put(state.name(), state); }

        public List<String> execute(Map<String, Object> input) {
            List<String> executionPath = new ArrayList<>();
            String currentState = states.keySet().iterator().next();
            Map<String, Object> currentInput = new HashMap<>(input);

            while (currentState != null) {
                State state = states.get(currentState);
                executionPath.add(state.name());

                switch (state.type()) {
                    case "Pass" -> currentInput = (Map<String, Object>) state.config().get("result");
                    case "Wait" -> { /* simulate delay */ }
                    case "Choice" -> {
                        String condition = (String) state.config().get("condition");
                        currentState = (boolean) state.config().getOrDefault(condition, false) ?
                            (String) state.config().get("trueNext") :
                            (String) state.config().get("falseNext");
                        continue;
                    }
                    case "Fail" -> { return executionPath; }
                }

                currentState = state.next();
            }
            return executionPath;
        }
    }

    public static class EventBridgeBus {
        private final Map<String, List<Consumer<Map<String, Object>>>> subscribers = new ConcurrentHashMap<>();

        public void subscribe(String eventType, Consumer<Map<String, Object>> handler) {
            subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
        }

        public void publish(String eventType, Map<String, Object> event) {
            List<Consumer<Map<String, Object>>> handlers = subscribers.get(eventType);
            if (handlers != null) {
                handlers.forEach(h -> {
                    try { h.accept(event); } catch (Exception e) { System.err.println("Handler error: " + e); }
                });
            }
        }
    }

    public static void main(String[] args) {
        StepFunctionWorkflow workflow = new StepFunctionWorkflow();
        workflow.addState(new StepFunctionWorkflow.State("Start", "Pass", "Process", Map.of("result", Map.of("status", "started"))));
        workflow.addState(new StepFunctionWorkflow.State("Process", "Pass", "End", Map.of("result", Map.of("status", "processed"))));
        workflow.addState(new StepFunctionWorkflow.State("End", "Pass", null, Map.of()));

        List<String> path = workflow.execute(Map.of("orderId", "123"));
        System.out.println("Execution path: " + path);
    }
}
