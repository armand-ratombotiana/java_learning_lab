package com.genai.lab05;

import java.util.*;
import java.util.function.Function;

/**
 * LLM Agent Frameworks
 * 
 * Demonstrates ReAct agent loop, tool registry, multi-step reasoning,
 * and agent observability in Java.
 */
public class Main {

    /** Tool interface. */
    interface Tool {
        String getName();
        String getDescription();
        String execute(String args);
    }

    static class CalculatorTool implements Tool {
        public String getName() { return "calculator"; }
        public String getDescription() { return "Evaluate a math expression. Input: expression string."; }
        public String execute(String args) {
            try {
                String expr = args.trim();
                if (expr.contains("+")) {
                    String[] parts = expr.split("\\+");
                    double r = Double.parseDouble(parts[0].trim()) + Double.parseDouble(parts[1].trim());
                    return String.valueOf(r);
                }
                return "ERROR: unsupported expression";
            } catch (Exception e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }

    static class SearchTool implements Tool {
        final Map<String, String> knowledge = Map.of(
            "capital of France", "Paris",
            "Transformer", "Neural network architecture using self-attention",
            "GPT", "Decoder-only language model"
        );
        public String getName() { return "search"; }
        public String getDescription() { return "Search for information. Input: query string."; }
        public String execute(String args) {
            return knowledge.getOrDefault(args.trim().toLowerCase(), "No results found for: " + args);
        }
    }

    /** Tool registry. */
    static class ToolRegistry {
        final Map<String, Tool> tools = new LinkedHashMap<>();
        void register(Tool t) { tools.put(t.getName(), t); }
        Tool get(String name) { return tools.get(name); }
        String listDescriptions() {
            StringBuilder sb = new StringBuilder("Available tools:\n");
            tools.values().forEach(t ->
                sb.append("  - ").append(t.getName()).append(": ").append(t.getDescription()).append("\n"));
            return sb.toString();
        }
    }

    /** ReAct Agent with observability. */
    static class ReActAgent {
        final ToolRegistry registry;
        final List<String> trace = new ArrayList<>();
        int maxSteps;

        ReActAgent(ToolRegistry registry, int maxSteps) {
            this.registry = registry;
            this.maxSteps = maxSteps;
        }

        String run(String goal) {
            trace.add("Goal: " + goal);
            String thought = "I need to find information step by step.";
            String answer = "No answer found.";

            for (int step = 0; step < maxSteps; step++) {
                trace.add("Step " + (step + 1) + " — Thought: " + thought);

                if (thought.contains("search")) {
                    String query = thought.substring(thought.indexOf("search") + 7).trim();
                    String obs = registry.get("search").execute(query);
                    trace.add("  Action: search(\"" + query + "\")");
                    trace.add("  Observation: " + obs);

                    if (obs.contains("capital")) {
                        answer = "The " + goal.toLowerCase() + " is " + obs;
                        thought = "I have the answer.";
                        trace.add("  Final: " + answer);
                        break;
                    }
                    thought = "I have " + obs + ". Now I will answer.";
                } else if (thought.contains("calculate")) {
                    String expr = thought.substring(thought.indexOf("calculate") + 10).trim();
                    String obs = registry.get("calculator").execute(expr);
                    trace.add("  Action: calculator(\"" + expr + "\")");
                    trace.add("  Observation: " + obs);
                    answer = "The result is " + obs;
                    thought = "I have the answer.";
                    trace.add("  Final: " + answer);
                    break;
                } else {
                    thought = "I should search for the answer I need.";
                }
            }
            return answer;
        }
    }

    public static void main(String[] args) {
        ToolRegistry registry = new ToolRegistry();
        registry.register(new CalculatorTool());
        registry.register(new SearchTool());

        System.out.println("=== Tool Registry ===");
        System.out.println(registry.listDescriptions());

        ReActAgent agent = new ReActAgent(registry, 5);
        String result = agent.run("What is the capital of France?");

        System.out.println("=== Agent Trace ===");
        agent.trace.forEach(System.out::println);

        System.out.println("\n=== Final Answer ===");
        System.out.println(result);

        System.out.println("\nAgent framework validated.");
    }
}
