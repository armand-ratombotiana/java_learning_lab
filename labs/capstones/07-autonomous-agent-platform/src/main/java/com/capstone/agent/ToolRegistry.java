package com.capstone.agent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ToolRegistry {
    private final Map<String, ToolDefinition> tools = new ConcurrentHashMap<>();

    public record ToolDefinition(String name, String description, List<String> parameters,
                                  Function<Map<String, String>, String> executor) {
        public ToolDefinition { parameters = List.copyOf(parameters); }
    }

    public void registerTool(String name, String description, List<String> parameters,
                             Function<Map<String, String>, String> executor) {
        tools.put(name, new ToolDefinition(name, description, parameters, executor));
    }

    public void registerTool(ToolDefinition tool) {
        tools.put(tool.name(), tool);
    }

    public String execute(String name, String input) {
        ToolDefinition tool = tools.get(name);
        if (tool == null) throw new IllegalArgumentException("Tool not found: " + name);
        Map<String, String> params = parseInput(input);
        return tool.executor().apply(params);
    }

    public Optional<ToolDefinition> getTool(String name) {
        return Optional.ofNullable(tools.get(name));
    }

    public List<ToolDefinition> getAllTools() { return List.copyOf(tools.values()); }

    public Set<String> getToolNames() { return tools.keySet(); }

    public int toolCount() { return tools.size(); }

    public void clear() { tools.clear(); }

    private Map<String, String> parseInput(String input) {
        Map<String, String> params = new HashMap<>();
        if (input == null || input.isBlank()) return params;
        String[] pairs = input.split("\\s+");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) params.put(kv[0].trim(), kv[1].trim());
            else params.put("value", pair.trim());
        }
        return params;
    }

    public static ToolRegistry createDefaultTools() {
        ToolRegistry registry = new ToolRegistry();
        registry.registerTool("search", "Search the web", List.of("query"),
            params -> "Search results for: " + params.getOrDefault("query", "unknown"));
        registry.registerTool("calculate", "Perform calculation", List.of("expression"),
            params -> "Result: " + params.getOrDefault("expression", "0"));
        registry.registerTool("remember", "Store in memory", List.of("key", "value"),
            params -> "Stored: " + params.getOrDefault("key", "?") + "=" + params.getOrDefault("value", "?"));
        registry.registerTool("finish", "Complete the task", List.of("result"),
            params -> "Task completed with: " + params.getOrDefault("result", "done"));
        return registry;
    }
}
