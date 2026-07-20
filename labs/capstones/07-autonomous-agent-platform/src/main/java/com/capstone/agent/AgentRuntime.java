package com.capstone.agent;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class AgentRuntime {
    private final String agentId;
    private final Map<String, Object> state = new ConcurrentHashMap<>();
    private final List<AgentStep> steps = new CopyOnWriteArrayList<>();
    private final ToolRegistry toolRegistry;
    private final AgentMemory memory;
    private final PlanningEngine planner;
    private final AtomicLong stepCount = new AtomicLong(0);
    private volatile boolean running = false;

    public record AgentStep(long stepNumber, String observation, String thought,
                             String action, String actionInput, String observationResult,
                             Instant timestamp) {}

    public AgentRuntime(String agentId, ToolRegistry toolRegistry, AgentMemory memory, PlanningEngine planner) {
        this.agentId = agentId;
        this.toolRegistry = toolRegistry;
        this.memory = memory;
        this.planner = planner;
    }

    public AgentStep executeStep(String observation) {
        long stepNum = stepCount.incrementAndGet();
        String thought = planner.generateThought(observation, memory.getShortTerm());
        List<String> actionPlan = planner.createPlan(observation);
        String action = actionPlan.isEmpty() ? "think" : actionPlan.get(0);
        String actionInput = actionPlan.size() > 1 ? actionPlan.get(1) : observation;
        String result;
        try {
            result = toolRegistry.execute(action, actionInput);
        } catch (Exception e) {
            result = "Error: " + e.getMessage();
        }
        memory.addToShortTerm(observation);
        memory.addToLongTerm(observation + " -> " + result);
        AgentStep step = new AgentStep(stepNum, observation, thought, action, actionInput, result, Instant.now());
        steps.add(step);
        return step;
    }

    public void start() { running = true; }

    public void stop() { running = false; }

    public boolean isRunning() { return running; }

    public void runLoop(String initialObservation, int maxSteps) {
        start();
        String currentObservation = initialObservation;
        for (int i = 0; i < maxSteps && running; i++) {
            AgentStep step = executeStep(currentObservation);
            currentObservation = step.observationResult();
            if (step.action().equals("finish")) { stop(); break; }
        }
        stop();
    }

    public List<AgentStep> getSteps() { return List.copyOf(steps); }
    public String getAgentId() { return agentId; }
    public long getStepCount() { return stepCount.get(); }
    public Map<String, Object> getState() { return Map.copyOf(state); }
    public void setState(String key, Object value) { state.put(key, value); }
    public void clear() { steps.clear(); state.clear(); memory.clear(); stepCount.set(0); running = false; }
}
