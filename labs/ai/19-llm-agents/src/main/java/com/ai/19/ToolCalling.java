package com.ai19;

import java.util.*;

public class ToolCalling {
    private Map<String, java.util.function.Function<String, String>> tools;

    public ToolCalling() {
        this.tools = new HashMap<>();
    }

    public void registerTool(String name, java.util.function.Function<String, String> handler) {
        tools.put(name, handler);
    }

    public String callTool(String name, String input) {
        java.util.function.Function<String, String> tool = tools.get(name);
        if (tool == null) return "Error: Tool '" + name + "' not found";
        try {
            return tool.apply(input);
        } catch (Exception e) {
            return "Error executing tool: " + e.getMessage();
        }
    }

    public String parseAndExecute(String naturalLanguageCommand) {
        for (String toolName : tools.keySet()) {
            if (naturalLanguageCommand.toLowerCase().contains(toolName.toLowerCase())) {
                String input = naturalLanguageCommand.replaceAll("(?i)" + toolName, "").trim();
                return toolName + " result: " + callTool(toolName, input);
            }
        }
        return "No matching tool found for: " + naturalLanguageCommand;
    }

    public List<String> listTools() {
        return new ArrayList<>(tools.keySet());
    }

    public static void main(String[] args) {
        System.out.println("=== Tool Calling Demo ===");
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
                default -> throw new IllegalArgumentException("Unknown op");
            };
            return String.valueOf(result);
        });
        tc.registerTool("weather", input -> "The weather in " + input + " is sunny, 22°C");
        tc.registerTool("search", input -> "Search results for '" + input + "': found 3 relevant documents");
        System.out.println("Available tools: " + tc.listTools());
        System.out.println(tc.parseAndExecute("calculator 5 + 3"));
        System.out.println(tc.parseAndExecute("weather London"));
        System.out.println(tc.parseAndExecute("search neural networks tutorial"));
    }
}
