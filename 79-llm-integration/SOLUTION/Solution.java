package com.learning.llm;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.InMemoryChatMemory;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.service.tools.ToolExecutor;
import dev.langchain4j.service.tools.ToolExecutorManager;

import java.util.*;
import java.util.function.Function;

public class Solution {

    public static class LLMProviderExample {

        public ChatLanguageModel createOpenAIModel(String apiKey, String model) {
            return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .temperature(0.7)
                .maxTokens(2000)
                .build();
        }

        public ChatLanguageModel createAnthropicModel(String apiKey) {
            return AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName("claude-3-sonnet-20240229")
                .temperature(0.7)
                .maxTokens(2000)
                .build();
        }

        public String generateText(ChatLanguageModel model, String prompt) {
            return model.chat(prompt);
        }

        public String generateWithSystemPrompt(ChatLanguageModel model, String systemPrompt, String userPrompt) {
            return model.chat(systemPrompt + "\n\n" + userPrompt);
        }
    }

    public static class AgentExample {

        public interface Assistant {
            @SystemMessage("You are a helpful assistant.")
            String chat(@UserMessage String message);
        }

        public Assistant createAgent(ChatLanguageModel model) {
            return AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .build();
        }

        public interface ResearchAgent {
            @SystemMessage("You are a research assistant that searches for information.")
            String research(@UserMessage String topic);
        }

        public ResearchAgent createResearchAgent(ChatLanguageModel model) {
            return AiServices.builder(ResearchAgent.class)
                .chatLanguageModel(model)
                .build();
        }

        public interface CodeAgent {
            @SystemMessage("You are a code generation assistant.")
            String generateCode(@UserMessage String request);
        }

        public CodeAgent createCodeAgent(ChatLanguageModel model) {
            return AiServices.builder(CodeAgent.class)
                .chatLanguageModel(model)
                .build();
        }
    }

    public static class MemoryExample {

        public ChatMemory createMessageWindowMemory(int maxMessages) {
            return MessageWindowChatMemory.builder()
                .maxMessages(maxMessages)
                .build();
        }

        public ChatMemory createInMemoryMemory() {
            return new InMemoryChatMemory();
        }

        public String chatWithMemory(ChatLanguageModel model, ChatMemory memory, String message) {
            List<dev.langchain4j.service.Message> messages = new ArrayList<>();
            messages.add(dev.langchain4j.service.UserMessage.userMessage(message));
            return model.chat(messages);
        }

        public void clearMemory(ChatMemory memory) {
            memory.clear();
        }

        public int getMessageCount(ChatMemory memory) {
            return 0;
        }
    }

    public static class ToolCallingExample {

        public static class Calculator {
            @Tool("Calculate the result of a mathematical expression")
            public String calculate(String expression) {
                try {
                    String expr = expression.replace("x", "*");
                    double result = new javax.script.ScriptEngineManager()
                        .getEngineByName("JavaScript")
                        .eval(expr);
                    return String.valueOf(result);
                } catch (Exception e) {
                    return "Error: " + e.getMessage();
                }
            }

            @Tool("Convert temperature between Celsius and Fahrenheit")
            public String convertTemperature(double value, String from, String to) {
                if (from.equals("C") && to.equals("F")) {
                    return String.valueOf((value * 9/5) + 32);
                } else if (from.equals("F") && to.equals("C")) {
                    return String.valueOf((value - 32) * 5/9);
                }
                return "Invalid conversion";
            }
        }

        public interface AssistantWithCalculator {
            @SystemMessage("You have access to a calculator tool.")
            String ask(@UserMessage String question);
        }

        public AssistantWithCalculator createToolAgent(ChatLanguageModel model, Calculator calculator) {
            return AiServices.builder(AssistantWithCalculator.class)
                .chatLanguageModel(model)
                .tools(calculator)
                .build();
        }

        public ToolSpecification createToolSpec(String name, String description, String params) {
            return ToolSpecification.builder()
                .name(name)
                .description(description)
                .build();
        }
    }

    public static class ChainOfThoughtExample {

        public String solveWithChainOfThought(ChatLanguageModel model, String problem) {
            String prompt = """
                Solve this problem step by step:
                Problem: %s

                Show your reasoning:
                """.formatted(problem);

            return model.chat(prompt);
        }

        public String solveWithFewShot(ChatLanguageModel model, String problem) {
            String prompt = """
                Example:
                Problem: If there are 5 apples and you buy 3 more, how many apples do you have?
                Solution: You have 5 + 3 = 8 apples.

                Problem: %s
                Solution:
                """.formatted(problem);

            return model.chat(prompt);
        }
    }

    public static class RAGIntegrationExample {

        public interface RagAssistant {
            @SystemMessage("Use the provided context to answer questions.")
            String answer(@UserMessage String question);
        }

        public RagAssistant createRagAssistant(ChatLanguageModel model) {
            return AiServices.builder(RagAssistant.class)
                .chatLanguageModel(model)
                .build();
        }

        public String retrieveContext(String query) {
            return "Retrieved context for: " + query;
        }

        public String formatContext(List<String> context) {
            return context.stream()
                .collect(java.util.stream.Collectors.joining("\n\n"));
        }
    }

    public static class OutputParserExample {

        public interface JsonParser {
            @SystemMessage("Return your response as valid JSON.")
            Map<String, Object> parse(@UserMessage String input);
        }

        public JsonParser createJsonParser(ChatLanguageModel model) {
            return AiServices.builder(JsonParser.class)
                .chatLanguageModel(model)
                .build();
        }

        public String extractStructuredData(ChatLanguageModel model, String text, String format) {
            String prompt = "Extract data from the following text in %s format:\n%s"
                .formatted(format, text);
            return model.chat(prompt);
        }
    }

    public static class ConversationFlowExample {

        public String handleGreeting(String name) {
            return "Hello " + name + "! How can I help you today?";
        }

        public String handleIntent(String intent, Map<String, String> entities) {
            switch (intent.toLowerCase()) {
                case "search":
                    return "Searching for " + entities.get("query");
                case "book":
                    return "Booking " + entities.get("item");
                case "help":
                    return "How can I assist you?";
                default:
                    return "I'm not sure how to help with that.";
            }
        }

        public String handleError(String error) {
            return "I encountered an error: " + error + ". Please try again.";
        }
    }

    public static class RateLimitingExample {

        public interface RateLimitedModel {
            String chat(String message);
        }

        private int requestCount = 0;
        private long lastResetTime = System.currentTimeMillis();

        public String chatWithRateLimit(ChatLanguageModel model, String message) {
            if (requestCount >= 60) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastResetTime >= 60000) {
                    requestCount = 0;
                    lastResetTime = currentTime;
                } else {
                    return "Rate limit exceeded. Please wait.";
                }
            }
            requestCount++;
            return model.chat(message);
        }
    }

    public static class CachingExample {

        private Map<String, String> cache = new HashMap<>();

        public String getCachedResponse(String prompt) {
            return cache.get(prompt);
        }

        public void cacheResponse(String prompt, String response) {
            cache.put(prompt, response);
        }

        public String chatWithCache(ChatLanguageModel model, String prompt) {
            String cached = getCachedResponse(prompt);
            if (cached != null) {
                return cached;
            }
            String response = model.chat(prompt);
            cacheResponse(prompt, response);
            return response;
        }

        public void clearCache() {
            cache.clear();
        }

        public int getCacheSize() {
            return cache.size();
        }
    }

    public static void main(String[] args) {
        System.out.println("LLM Integration Solutions");
        System.out.println("===========================");

        LLMProviderExample llmProvider = new LLMProviderExample();
        System.out.println("LLM provider examples available");

        AgentExample agentExample = new AgentExample();
        System.out.println("Agent examples ready");

        MemoryExample memoryExample = new MemoryExample();
        ChatMemory memory = memoryExample.createMessageWindowMemory(10);
        System.out.println("Memory created with 10 message capacity");

        ToolCallingExample toolExample = new ToolCallingExample();
        ToolCallingExample.Calculator calculator = new ToolCallingExample.Calculator();
        System.out.println("Tools available: " + calculator.calculate("5 + 3"));

        ChainOfThoughtExample cotExample = new ChainOfThoughtExample();
        System.out.println("Chain of thought reasoning ready");

        RAGIntegrationExample ragExample = new RAGIntegrationExample();
        System.out.println("RAG integration ready");

        CachingExample cachingExample = new CachingExample();
        System.out.println("Caching utilities ready");
    }
}