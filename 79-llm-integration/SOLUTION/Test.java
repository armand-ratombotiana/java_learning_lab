package com.learning.llm;

import java.util.*;

public class Test {

    public static void main(String[] args) {
        System.out.println("Running LLM Integration Tests\n");

        testMemoryCreation();
        testToolCalculation();
        testCaching();
        testChainOfThought();
        testConversationFlow();

        System.out.println("\nAll tests passed!");
    }

    private static void testMemoryCreation() {
        System.out.println("Test: Memory Creation");
        Solution.MemoryExample memoryExample = new Solution.MemoryExample();

        ChatMemory memory = memoryExample.createMessageWindowMemory(10);
        assert memory != null : "Memory should not be null";

        ChatMemory inMemory = memoryExample.createInMemoryMemory();
        assert inMemory != null : "In-memory memory should not be null";

        System.out.println("  - Memory created successfully");
    }

    private static void testToolCalculation() {
        System.out.println("Test: Tool Calculation");
        Solution.ToolCallingExample.Calculator calculator = new Solution.ToolCallingExample.Calculator();

        String result = calculator.calculate("5 + 3");
        assert result.equals("8.0") : "Calculation should be 8.0";

        String tempResult = calculator.convertTemperature(100, "C", "F");
        assert tempResult.equals("212.0") : "100C should be 212F";

        System.out.println("  - Calculator: 5 + 3 = " + result);
    }

    private static void testCaching() {
        System.out.println("Test: Caching");
        Solution.CachingExample caching = new Solution.CachingExample();

        caching.cacheResponse("Hello", "Hi there!");
        String cached = caching.getCachedResponse("Hello");

        assert cached.equals("Hi there!") : "Should retrieve cached response";
        assert caching.getCacheSize() == 1 : "Cache should have 1 entry";

        caching.clearCache();
        assert caching.getCacheSize() == 0 : "Cache should be empty after clear";

        System.out.println("  - Caching works correctly");
    }

    private static void testChainOfThought() {
        System.out.println("Test: Chain of Thought");
        Solution.ChainOfThoughtExample cotExample = new Solution.ChainOfThoughtExample();

        assert cotExample != null : "Chain of thought example should not be null";
        System.out.println("  - Chain of thought reasoning ready");
    }

    private static void testConversationFlow() {
        System.out.println("Test: Conversation Flow");
        Solution.ConversationFlowExample flow = new Solution.ConversationFlowExample();

        String greeting = flow.handleGreeting("John");
        assert greeting.contains("John") : "Greeting should contain name";

        Map<String, String> entities = new HashMap<>();
        entities.put("query", "weather");
        String response = flow.handleIntent("search", entities);
        assert response.contains("weather") : "Response should contain query";

        System.out.println("  - Conversation flow works: " + greeting);
    }
}