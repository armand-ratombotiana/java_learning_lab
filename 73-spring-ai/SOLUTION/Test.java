package com.learning.springai;

import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        System.out.println("Running Spring AI Tests\n");

        testPromptTemplateCreation();
        testPromptVariableReplacement();
        testSummarizationPrompt();
        testTranslationPrompt();
        testChatMemoryCreation();
        testVectorStoreSearch();

        System.out.println("\nAll tests passed!");
    }

    private static void testPromptTemplateCreation() {
        System.out.println("Test: Prompt Template Creation");
        Solution.PromptTemplateExample promptExample = new Solution.PromptTemplateExample();

        var prompt = promptExample.createPrompt(
            "Hello {name}, welcome to {place}!",
            Map.of("name", "Alice", "place", "Spring AI")
        );

        assert prompt != null : "Prompt should not be null";
        System.out.println("  - Prompt template created successfully");
    }

    private static void testPromptVariableReplacement() {
        System.out.println("Test: Prompt Variable Replacement");
        Solution.PromptTemplateExample promptExample = new Solution.PromptTemplateExample();

        var prompt = promptExample.createPrompt(
            "Translate to {language}: {text}",
            Map.of("language", "French", "text", "Hello World")
        );

        assert prompt.getContents().get(0).getText().contains("French");
        System.out.println("  - Variable replacement works correctly");
    }

    private static void testSummarizationPrompt() {
        System.out.println("Test: Summarization Prompt");
        Solution.PromptTemplateExample promptExample = new Solution.PromptTemplateExample();

        var prompt = promptExample.createSummarizationPrompt(
            "This is a long text that needs to be summarized into a few sentences.",
            2
        );

        assert prompt != null : "Prompt should not be null";
        System.out.println("  - Summarization prompt created");
    }

    private static void testTranslationPrompt() {
        System.out.println("Test: Translation Prompt");
        Solution.PromptTemplateExample promptExample = new Solution.PromptTemplateExample();

        var prompt = promptExample.createTranslationPrompt("Hello", "Spanish");

        assert prompt != null : "Prompt should not be null";
        System.out.println("  - Translation prompt created");
    }

    private static void testChatMemoryCreation() {
        System.out.println("Test: Chat Memory Creation");
        Solution.ChatMemoryExample memoryExample = new Solution.ChatMemoryExample();

        Solution.ChatMemory memory = memoryExample.createInMemoryMemory();

        assert memory != null : "Memory should not be null";
        System.out.println("  - Chat memory created successfully");
    }

    private static void testVectorStoreSearch() {
        System.out.println("Test: Vector Store Search");
        Solution.VectorStoreExample vectorStoreExample = new Solution.VectorStoreExample();

        var searchRequest = org.springframework.ai.vectorstore.SearchRequest.builder()
            .query("test query")
            .topK(5)
            .build();

        assert searchRequest != null : "Search request should not be null";
        System.out.println("  - Search request created successfully");
    }
}