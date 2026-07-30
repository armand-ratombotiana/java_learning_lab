package com.aiengineering.lab04;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * Demonstrates AI Agent Frameworks: ReAct (Reasoning + Acting) pattern,
 * tool use, and multi-agent orchestration with a coordinator agent.
 * <p>
 * The ReAct loop: Thought -> Action -> Observation -> Thought (repeat).
 */
public class AiAgentFrameworksDemo {

    // ---------- Tool Interface ----------

    interface Tool {
        String getName();
        String execute(String input);
    }

    static class CalculatorTool implements Tool {
        public String getName() { return "calculator"; }
        public String execute(String input) {
            try {
                String[] parts = input.split(" ");
                if (parts.length != 3) return "Error: expected format 'a op b'";
                double a = Double.parseDouble(parts[0]);
                double b = Double.parseDouble(parts[2]);
                return switch (parts[1]) {
                    case "+" -> String.valueOf(a + b);
                    case "-" -> String.valueOf(a - b);
                    case "*" -> String.valueOf(a * b);
                    case "/" -> b == 0 ? "Error: division by zero" : String.valueOf(a / b);
                    default -> "Error: unknown operator " + parts[1];
                };
            } catch (NumberFormatException e) {
                return "Error: invalid number";
            }
        }
    }

    static class SearchTool implements Tool {
        public String getName() { return "web_search"; }
        private final Map<String, String> knowledge = Map.of(
            "capital of France", "Paris is the capital of France.",
            "meaning of life", "42 (according to Deep Thought).",
            "Java version", "Java 21 was released in September 2023."
        );
        public String execute(String input) {
            return knowledge.getOrDefault(input.toLowerCase(),
                "No results found for: " + input);
        }
    }

    static class WeatherTool implements Tool {
        public String getName() { return "weather"; }
        public String execute(String input) {
            return "The weather in " + input + " is currently 22°C and sunny.";
        }
    }

    // ---------- ReAct Agent ----------

    static class ReActAgent {
        private final String name;
        private final List<Tool> tools;
        private final List<String> memory = new ArrayList<>();

        ReActAgent(String name, List<Tool> tools) {
            this.name = name;
            this.tools = tools;
        }

        String run(String task) {
            System.out.println("\n[" + name + "] Task: " + task);
            memory.add("Task: " + task);

            String thought = "I need to solve: " + task;
            int maxSteps = 5;
            for (int step = 0; step < maxSteps; step++) {
                System.out.println("  Step " + (step + 1) + " — Thought: " + thought);

                // Decide which tool to use based on task keywords
                Tool selectedTool = null;
                String toolInput = task;
                for (Tool t : tools) {
                    if (task.toLowerCase().contains(t.getName().replace("_", " ").toLowerCase())
                            || task.toLowerCase().contains(t.getName().toLowerCase())) {
                        selectedTool = t;
                        break;
                    }
                }

                if (selectedTool == null && !tools.isEmpty()) {
                    selectedTool = tools.get(0); // fallback
                }

                if (selectedTool != null) {
                    System.out.println("  Action: " + selectedTool.getName() + "(\"" + toolInput + "\")");
                    String observation = selectedTool.execute(toolInput);
                    System.out.println("  Observation: " + observation);
                    memory.add("Action: " + selectedTool.getName() + " -> " + observation);

                    thought = "Based on observation '" + observation + "', I can answer.";
                    return observation;
                }

                thought = "I need more information.";
            }
            return "Unable to complete task.";
        }

        List<String> getMemory() { return List.copyOf(memory); }
    }

    // ---------- Multi-Agent Orchestrator ----------

    static class AgentOrchestrator {
        private final String name;
        private final Map<String, ReActAgent> agents = new HashMap<>();

        AgentOrchestrator(String name) { this.name = name; }

        void registerAgent(String role, ReActAgent agent) {
            agents.put(role, agent);
            System.out.println("  Registered agent: " + role);
        }

        String delegate(String task) {
            System.out.println("\n[" + name + "] Delegating task: \"" + task + "\"");
            // Route to appropriate agent based on task content
            if (task.contains("weather")) {
                return agents.getOrDefault("weather_agent", agents.values().iterator().next()).run(task);
            } else if (task.contains("calculate") || task.contains("+") || task.contains("-")) {
                return agents.getOrDefault("math_agent", agents.values().iterator().next()).run(task);
            } else {
                return agents.getOrDefault("general_agent", agents.values().iterator().next()).run(task);
            }
        }
    }

    // ---------- Main Demo ----------

    public static void main(String[] args) {
        System.out.println("=== AI Engineering Academy — Lab 04: AI Agent Frameworks ===\n");

        // Create tools
        Tool calc = new CalculatorTool();
        Tool search = new SearchTool();
        Tool weather = new WeatherTool();

        // Create specialized agents
        ReActAgent mathAgent = new ReActAgent("MathBot", List.of(calc, search));
        ReActAgent weatherAgent = new ReActAgent("WeatherBot", List.of(weather, search));
        ReActAgent generalAgent = new ReActAgent("GeneralBot", List.of(search, calc, weather));

        System.out.println("--- Single ReAct Agent ---");
        String result1 = generalAgent.run("What is the capital of France?");
        System.out.println("  Final: " + result1);

        System.out.println("\n--- Multi-Agent Orchestration ---");
        AgentOrchestrator orchestrator = new AgentOrchestrator("Orchestrator");
        orchestrator.registerAgent("math_agent", mathAgent);
        orchestrator.registerAgent("weather_agent", weatherAgent);
        orchestrator.registerAgent("general_agent", generalAgent);

        String result2 = orchestrator.delegate("What is the weather in London?");
        System.out.println("  Final: " + result2);

        String result3 = orchestrator.delegate("Please calculate 10 + 25");
        System.out.println("  Final: " + result3);

        String result4 = orchestrator.delegate("What is the meaning of life?");
        System.out.println("  Final: " + result4);

        System.out.println("\nDemo complete. 3 agents created, 1 orchestrator, 4 tasks executed.");
    }
}
