package com.aiassistant.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class Planner {

    private final List<Agent> agents;

    public Plan createPlan(String userMessage, List<Conversation.Message> context) {
        log.info("Creating plan for: {}", userMessage);

        Plan plan = new Plan();
        plan.setGoal(extractGoal(userMessage));

        if (containsKnowledgeQuery(userMessage)) {
            plan.addStep(new Plan.Step("RETRIEVE", "Search knowledge base for relevant information", null));
        }

        if (containsCodeRequest(userMessage)) {
            plan.addStep(new Plan.Step("CODE", "Write and execute code", null));
        }

        if (containsCalculation(userMessage)) {
            plan.addStep(new Plan.Step("CALCULATE", "Perform calculations", null));
        }

        if (containsSearchQuery(userMessage)) {
            plan.addStep(new Plan.Step("SEARCH", "Search the web for information", null));
        }

        plan.addStep(new Plan.Step("RESPOND", "Generate final response", null));

        log.info("Created plan with {} steps", plan.getSteps().size());
        return plan;
    }

    private String extractGoal(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("explain") || lower.contains("what is")) {
            return "EXPLAIN";
        } else if (lower.contains("write") || lower.contains("create") || lower.contains("generate")) {
            return "GENERATE";
        } else if (lower.contains("calculate") || lower.contains("compute")) {
            return "CALCULATE";
        } else if (lower.contains("find") || lower.contains("search")) {
            return "SEARCH";
        }
        return "RESPOND";
    }

    private boolean containsKnowledgeQuery(String message) {
        String lower = message.toLowerCase();
        return lower.contains("what") || lower.contains("know") || 
               lower.contains("remember") || lower.contains("tell me about");
    }

    private boolean containsCodeRequest(String message) {
        String lower = message.toLowerCase();
        return lower.contains("code") || lower.contains("program") ||
               lower.contains("script") || lower.contains("function");
    }

    private boolean containsCalculation(String message) {
        String lower = message.toLowerCase();
        return lower.contains("calculate") || lower.contains("sum") ||
               lower.contains("multiply") || lower.contains("what is") &&
               (lower.contains("+") || lower.contains("-") || 
                lower.contains("*") || lower.contains("/"));
    }

    private boolean containsSearchQuery(String message) {
        String lower = message.toLowerCase();
        return lower.contains("search") || lower.contains("find") ||
               lower.contains("look up") || lower.contains("google");
    }

    public static class Plan {
        private String goal;
        private List<Step> steps;
        private int currentStep;

        public Plan() {
            this.steps = new ArrayList<>();
            this.currentStep = 0;
        }

        public void setGoal(String goal) { this.goal = goal; }
        public String getGoal() { return goal; }
        public List<Step> getSteps() { return steps; }

        public void addStep(Step step) { steps.add(step); }

        public Step getNextStep() {
            if (currentStep < steps.size()) {
                return steps.get(currentStep++);
            }
            return null;
        }

        public boolean hasMoreSteps() { return currentStep < steps.size(); }

        public record Step(String action, String description, Map<String, Object> context) {}
    }

    public interface Agent {
        String getName();
        String execute(String input, Map<String, Object> context);
    }
}