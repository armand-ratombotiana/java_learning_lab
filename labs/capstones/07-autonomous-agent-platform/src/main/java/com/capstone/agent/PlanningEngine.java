package com.capstone.agent;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlanningEngine {
    private final ToolRegistry toolRegistry;
    private final List<Plan> plans = new CopyOnWriteArrayList<>();
    private int maxReActCycles = 5;

    public record Plan(String id, String goal, List<PlanStep> steps, PlanStatus status, long createdAt) {}
    public record PlanStep(int order, String action, String input, String expectedOutput, boolean completed) {}
    public enum PlanStatus { DRAFT, IN_PROGRESS, COMPLETED, FAILED }

    public PlanningEngine(ToolRegistry toolRegistry) { this.toolRegistry = toolRegistry; }

    public String generateThought(String observation, List<String> recentActions) {
        if (recentActions.isEmpty()) return "I need to understand the task: " + observation;
        String lastAction = recentActions.get(recentActions.size() - 1);
        return "Based on the last action '" + lastAction + "', I should continue working toward the goal.";
    }

    public List<String> createPlan(String goal) {
        List<String> actionSequence = new ArrayList<>();
        Set<String> availableTools = toolRegistry.getToolNames();
        if (goal.toLowerCase().contains("search") && availableTools.contains("search")) {
            actionSequence.add("search");
            actionSequence.add(goal);
        } else if (goal.toLowerCase().contains("calculate") && availableTools.contains("calculate")) {
            actionSequence.add("calculate");
            actionSequence.add(goal);
        } else if (goal.toLowerCase().contains("remember") && availableTools.contains("remember")) {
            actionSequence.add("remember");
            actionSequence.add(goal);
        } else {
            actionSequence.add("think");
            actionSequence.add(goal);
        }
        return actionSequence;
    }

    public Plan createStructuredPlan(String goal) {
        String planId = UUID.randomUUID().toString().substring(0, 8);
        List<PlanStep> steps = decomposeGoal(goal);
        Plan plan = new Plan(planId, goal, steps, PlanStatus.DRAFT, System.currentTimeMillis());
        plans.add(plan);
        return plan;
    }

    public Plan executePlan(String planId) {
        Plan plan = plans.stream().filter(p -> p.id().equals(planId)).findFirst().orElse(null);
        if (plan == null) throw new IllegalArgumentException("Plan not found: " + planId);
        List<PlanStep> updatedSteps = new ArrayList<>();
        boolean allCompleted = true;
        for (PlanStep step : plan.steps()) {
            if (!step.completed()) {
                try {
                    toolRegistry.execute(step.action(), step.input());
                    updatedSteps.add(new PlanStep(step.order(), step.action(), step.input(), step.expectedOutput(), true));
                } catch (Exception e) {
                    updatedSteps.add(step);
                    allCompleted = false;
                    break;
                }
            } else {
                updatedSteps.add(step);
            }
        }
        PlanStatus status = allCompleted ? PlanStatus.COMPLETED : PlanStatus.FAILED;
        Plan updatedPlan = new Plan(planId, plan.goal(), updatedSteps, status, plan.createdAt());
        plans.replaceAll(p -> p.id().equals(planId) ? updatedPlan : p);
        return updatedPlan;
    }

    public List<Plan> getPlans() { return List.copyOf(plans); }
    public void setMaxReActCycles(int max) { this.maxReActCycles = max; }

    private List<PlanStep> decomposeGoal(String goal) {
        List<PlanStep> steps = new ArrayList<>();
        String[] parts = goal.split("\\.");
        int order = 0;
        for (String part : parts) {
            if (!part.isBlank()) {
                steps.add(new PlanStep(order++, toolRegistry.getToolNames().contains("search") ? "search" : "think",
                    part.trim(), "Completed: " + part.trim(), false));
            }
        }
        if (steps.isEmpty()) {
            steps.add(new PlanStep(0, "think", goal, "Completed", false));
        }
        return steps;
    }
}
