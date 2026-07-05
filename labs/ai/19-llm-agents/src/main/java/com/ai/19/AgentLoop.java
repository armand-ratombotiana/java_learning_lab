package com.ai19;

import java.util.*;

public class AgentLoop {
    private ToolCalling toolSystem;
    private List<String> history;
    private int maxIterations;

    public AgentLoop(ToolCalling toolSystem, int maxIterations) {
        this.toolSystem = toolSystem;
        this.history = new ArrayList<>();
        this.maxIterations = maxIterations;
    }

    public String run(String task) {
        history.clear();
        history.add("Task: " + task);
        String currentTask = task;
        for (int iter = 0; iter < maxIterations; iter++) {
            String action = decideAction(currentTask);
            history.add("Iteration " + iter + " action: " + action);
            String observation = toolSystem.parseAndExecute(action);
            history.add("Observation: " + observation);
            if (isComplete(observation, currentTask)) {
                history.add("Task complete!");
                return observation;
            }
            currentTask = refineTask(currentTask, action, observation);
        }
        history.add("Max iterations reached");
        return "Could not complete task: " + task;
    }

    private String decideAction(String task) {
        List<String> tools = toolSystem.listTools();
        for (String tool : tools) {
            if (task.toLowerCase().contains(tool.toLowerCase()))
                return tool + " " + task.replaceAll("(?i)" + tool, "").trim();
        }
        return "search " + task;
    }

    private boolean isComplete(String observation, String task) {
        return !observation.startsWith("No matching tool") && !observation.startsWith("Error");
    }

    private String refineTask(String task, String action, String observation) {
        String[] parts = task.split("then", 2);
        if (parts.length > 1) return parts[1].trim();
        return task;
    }

    public List<String> getHistory() { return history; }

    public static void main(String[] args) {
        System.out.println("=== Agent Loop Demo ===");
        ToolCalling tc = new ToolCalling();
        tc.registerTool("calculator", input -> {
            String[] parts = input.trim().split("\\s+");
            double a = Double.parseDouble(parts[0]);
            String op = parts[1];
            double b = Double.parseDouble(parts[2]);
            double result = switch (op) {
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                case "/" -> a / b;
                default -> 0;
            };
            return String.valueOf(result);
        });
        tc.registerTool("weather", input -> "Weather in " + input + ": 22°C, sunny");
        AgentLoop agent = new AgentLoop(tc, 5);
        String result = agent.run("calculator 10 + 5");
        System.out.println("Final result: " + result);
        System.out.println("\nHistory:");
        for (String h : agent.getHistory())
            System.out.println("  " + h);
    }
}
