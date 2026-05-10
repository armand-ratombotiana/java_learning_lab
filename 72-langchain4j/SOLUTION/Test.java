package com.learning.langchain4j;

import java.util.Map;

public class Test {

    public static void main(String[] args) {
        System.out.println("Running LangChain4j Tests\n");

        testPromptTemplateCreation();
        testPromptVariableReplacement();
        testTravelPrompt();
        testCodeReviewPrompt();
        testChatMemoryCreation();
        testToolExample();

        System.out.println("\nAll tests passed!");
    }

    private static void testPromptTemplateCreation() {
        System.out.println("Test: Prompt Template Creation");
        Solution.PromptTemplateExample promptExample = new Solution.PromptTemplateExample();

        String template = "Hello {{name}}, welcome to {{place}}!";
        String result = promptExample.createPrompt(template, Map.of(
            "name", "John",
            "place", "Paris"
        ));

        assert result.equals("Hello John, welcome to Paris!") : "Prompt should replace variables";
        System.out.println("  - Prompt template creation works correctly");
    }

    private static void testPromptVariableReplacement() {
        System.out.println("Test: Prompt Variable Replacement");
        Solution.PromptTemplateExample promptExample = new Solution.PromptTemplateExample();

        String template = "Review this {{language}} code: {{code}}";
        String result = promptExample.createPrompt(template, Map.of(
            "language", "Java",
            "code", "public class Test {}"
        ));

        assert result.contains("Java") : "Should contain language";
        assert result.contains("public class Test {}") : "Should contain code";
        System.out.println("  - Variable replacement works correctly");
    }

    private static void testTravelPrompt() {
        System.out.println("Test: Travel Prompt Generation");
        Solution.PromptTemplateExample promptExample = new Solution.PromptTemplateExample();

        String prompt = promptExample.createTravelPrompt("Tokyo", 7, "$3000");

        assert prompt.contains("Tokyo") : "Should contain destination";
        assert prompt.contains("7") : "Should contain days";
        assert prompt.contains("$3000") : "Should contain budget";
        System.out.println("  - Travel prompt generated: " + prompt.substring(0, 60) + "...");
    }

    private static void testCodeReviewPrompt() {
        System.out.println("Test: Code Review Prompt");
        Solution.PromptTemplateExample promptExample = new Solution.PromptTemplateExample();

        String code = "public void process() { for(int i=0; i<10; i++) {} }";
        String prompt = promptExample.createCodeReviewPrompt(code, "Java");

        assert prompt.contains("Java") : "Should contain language";
        assert prompt.contains("Review") : "Should contain instruction";
        System.out.println("  - Code review prompt created");
    }

    private static void testChatMemoryCreation() {
        System.out.println("Test: Chat Memory Creation");
        Solution.ChatMemoryExample memoryExample = new Solution.ChatMemoryExample();

        var memory = memoryExample.createWindowMemory(5);

        assert memory != null : "Memory should not be null";
        System.out.println("  - Chat memory created with 5 message capacity");
    }

    private static void testToolExample() {
        System.out.println("Test: Tool Example");
        Solution.ToolExample.WeatherService weatherService = new Solution.ToolExample.WeatherService();

        String weather = weatherService.getWeather("London");
        String forecast = weatherService.getForecast("London", 3);

        assert weather.contains("London") : "Should contain location";
        assert forecast.contains("3") : "Should contain days";
        System.out.println("  - Weather tool works: " + weather);
    }
}