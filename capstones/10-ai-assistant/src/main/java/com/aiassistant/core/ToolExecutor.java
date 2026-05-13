package com.aiassistant.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ToolExecutor {

    private final Map<String, Tool> tools;

    public ToolExecutor() {
        this.tools = Map.of(
            "calculator", new CalculatorTool(),
            "web_search", new WebSearchTool(),
            "knowledge_retrieve", new KnowledgeRetrieveTool(),
            "code_executor", new CodeExecutorTool()
        );
    }

    public String execute(String toolName, Map<String, Object> arguments) {
        Tool tool = tools.get(toolName);
        if (tool == null) {
            log.warn("Tool not found: {}", toolName);
            return "Tool not available: " + toolName;
        }

        try {
            log.info("Executing tool: {} with args: {}", toolName, arguments);
            return tool.execute(arguments);
        } catch (Exception e) {
            log.error("Tool execution failed: {}", e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    public interface Tool {
        String getName();
        String getDescription();
        String execute(Map<String, Object> arguments);
    }

    public static class CalculatorTool implements Tool {
        @Override
        public String getName() { return "calculator"; }
        @Override
        public String getDescription() { return "Perform mathematical calculations"; }

        @Override
        public String execute(Map<String, Object> arguments) {
            double a = ((Number) arguments.getOrDefault("a", 0)).doubleValue();
            double b = ((Number) arguments.getOrDefault("b", 0)).doubleValue();
            String operation = (String) arguments.getOrDefault("operation", "add");

            return switch (operation) {
                case "add" -> String.valueOf(a + b);
                case "subtract" -> String.valueOf(a - b);
                case "multiply" -> String.valueOf(a * b);
                case "divide" -> b != 0 ? String.valueOf(a / b) : "Division by zero";
                case "power" -> String.valueOf(Math.pow(a, b));
                default -> "Unknown operation: " + operation;
            };
        }
    }

    public static class WebSearchTool implements Tool {
        @Override
        public String getName() { return "web_search"; }
        @Override
        public String getDescription() { return "Search the web for information"; }

        @Override
        public String execute(Map<String, Object> arguments) {
            String query = (String) arguments.getOrDefault("query", "");
            return "Web search results for '" + query + "': [Simulated search result based on query]";
        }
    }

    public static class KnowledgeRetrieveTool implements Tool {
        @Override
        public String getName() { return "knowledge_retrieve"; }
        @Override
        public String getDescription() { return "Retrieve information from knowledge base"; }

        @Override
        public String execute(Map<String, Object> arguments) {
            String query = (String) arguments.getOrDefault("query", "");
            return "Retrieved knowledge for '" + query + "': [Simulated knowledge retrieval result]";
        }
    }

    public static class CodeExecutorTool implements Tool {
        @Override
        public String getName() { return "code_executor"; }
        @Override
        public String getDescription() { return "Execute code snippets"; }

        @Override
        public String execute(Map<String, Object> arguments) {
            String code = (String) arguments.getOrDefault("code", "");
            return "Code execution for '" + code + "': [Simulated code execution result]";
        }
    }

    public Tool getTool(String name) {
        return tools.get(name);
    }
}