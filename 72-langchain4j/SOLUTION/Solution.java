package com.learning.langchain4j;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.User;
import dev.langchain4j.service.System;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.OnnxBertBiEncoder;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestion;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;

public class Solution {

    public static class ChatModelExample {

        public ChatLanguageModel createOpenAIModel(String apiKey) {
            return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4")
                .temperature(0.7)
                .maxTokens(1000)
                .build();
        }

        public ChatLanguageModel createAnthropicModel(String apiKey) {
            return AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName("claude-3-sonnet")
                .temperature(0.7)
                .maxTokens(1000)
                .build();
        }

        public ChatLanguageModel createOllamaModel(String modelName) {
            return OllamaChatModel.builder()
                .modelName(modelName)
                .temperature(0.7)
                .build();
        }

        public String chat(ChatLanguageModel model, String userMessage) {
            return model.chat(userMessage);
        }

        public String chatWithSystemPrompt(ChatLanguageModel model, String systemPrompt, String userMessage) {
            return model.chat(systemPrompt + "\n\n" + userMessage);
        }
    }

    public static class PromptTemplateExample {

        public String createPrompt(String template, Map<String, Object> variables) {
            String result = template;
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                result = result.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
            }
            return result;
        }

        public String createTravelPrompt(String destination, int days, String budget) {
            String template = "Create a {{days}}-day travel itinerary for {{destination}} " +
                           "with a budget of {{budget}}. Include daily activities, " +
                           "meal recommendations, and estimated costs.";
            return createPrompt(template, Map.of(
                "destination", destination,
                "days", String.valueOf(days),
                "budget", budget
            ));
        }

        public String createCodeReviewPrompt(String code, String language) {
            String template = "Review the following {{language}} code and provide feedback " +
                           "on: 1) Code quality, 2) Potential bugs, 3) Performance suggestions, " +
                           "4) Best practices violations.\n\nCode:\n```{{language}}\n{{code}}\n```";
            return createPrompt(template, Map.of(
                "language", language,
                "code", code
            ));
        }

        public String createSummarizationPrompt(String text, int maxSentences) {
            return String.format(
                "Summarize the following text in exactly %d sentences:\n\n%s",
                maxSentences, text
            );
        }
    }

    public static class AiServicesExample {

        public interface TravelAssistant {
            @SystemMessage("You are a travel assistant with extensive knowledge of destinations worldwide.")
            String recommendDestination(@UserMessage String preferences);

            @SystemMessage("You are a budget travel expert.")
            String planBudgetTrip(@UserMessage String destination);
        }

        public TravelAssistant createTravelAssistant(ChatLanguageModel model) {
            return AiServices.builder(TravelAssistant.class)
                .chatLanguageModel(model)
                .build();
        }

        public interface CodeHelper {
            @SystemMessage("You are an expert software developer. Provide clear, concise code solutions.")
            String explainCode(@UserMessage String code);

            @SystemMessage("You are a code reviewer focusing on security and performance.")
            String reviewCode(@UserMessage String code);
        }

        public CodeHelper createCodeHelper(ChatLanguageModel model) {
            return AiServices.builder(CodeHelper.class)
                .chatLanguageModel(model)
                .build();
        }
    }

    public static class RAGExample {

        public List<TextSegment> loadDocuments(String directoryPath) {
            List<File> files = FileSystemDocumentLoader.listFiles(new File(directoryPath));
            return files.stream()
                .map(file -> FileSystemDocumentLoader.loadDocument(file))
                .map(document -> DocumentSplitters.recursive(500, 100).split(document))
                .flatMap(List::stream)
                .toList();
        }

        public EmbeddingStore<TextSegment> createEmbeddingStore(List<TextSegment> segments) {
            EmbeddingModel embeddingModel = new OnnxBertBiEncoder();
            return EmbeddingStoreIngestion.builder()
                .embeddingModel(embeddingModel)
                .build()
                .ingest(segments);
        }

        public String queryWithContext(ChatLanguageModel model, EmbeddingStore<TextSegment> store,
                                        String query, EmbeddingModel embeddingModel) {
            List<TextSegment> relevantSegments = store
                .findRelevant(embeddingModel.embed(query).content(), 5)
                .stream()
                .map(r -> r.embedded())
                .toList();

            String context = relevantSegments.stream()
                .map(TextSegment::text)
                .collect(java.util.stream.Collectors.joining("\n\n"));

            String prompt = "Based on the following context, answer the question.\n\n" +
                          "Context:\n" + context + "\n\n" +
                          "Question: " + query;

            return model.chat(prompt);
        }
    }

    public static class ChatMemoryExample {

        public ChatMemory createWindowMemory(int maxMessages) {
            return MessageWindowChatMemory.builder()
                .maxMessages(maxMessages)
                .build();
        }

        public String chatWithMemory(ChatLanguageModel model, ChatMemory memory, String message) {
            memory.add(dev.langchain4j.service.UserMessage.userMessage(message));
            String response = model.chat(memory.messages());
            memory.add(dev.langchain4j.service.AiMessage.aiMessage(response));
            return response;
        }

        public void clearMemory(ChatMemory memory) {
            memory.clear();
        }
    }

    public static class ToolExample {

        public static class WeatherService {
            public String getWeather(String location) {
                return "Sunny, 25°C in " + location;
            }

            public String getForecast(String location, int days) {
                return days + "-day forecast for " + location + ": Sunny, partly cloudy, rain possible";
            }
        }

        public interface AssistantWithTools {
            @Tool("Get current weather for a location")
            String getWeather(String location);

            @Tool("Get weather forecast for a location")
            String getForecast(String location, int days);
        }

        public AssistantWithTools createAssistantWithTools(ChatLanguageModel model, WeatherService weatherService) {
            return AiServices.builder(AssistantWithTools.class)
                .chatLanguageModel(model)
                .tools(weatherService)
                .build();
        }
    }

    public static class StreamingExample {

        public void streamResponse(ChatLanguageModel model, String userMessage) {
            model.chat(userMessage, response -> {
                System.out.print(response.token().text());
            });
        }
    }

    public static class OutputParsingExample {

        public interface JsonExtractor {
            @SystemMessage("Extract JSON data from the response")
            Map<String, Object> extractJson(@UserMessage String text);
        }

        public JsonExtractor createJsonExtractor(ChatLanguageModel model) {
            return AiServices.builder(JsonExtractor.class)
                .chatLanguageModel(model)
                .build();
        }
    }

    public static void main(String[] args) {
        System.out.println("LangChain4j Solutions");
        System.out.println("=====================");

        ChatModelExample chatExample = new ChatModelExample();
        System.out.println("Chat model examples available");

        PromptTemplateExample promptExample = new PromptTemplateExample();
        String travelPrompt = promptExample.createTravelPrompt("Paris", 5, "$2000");
        System.out.println("Generated travel prompt: " + travelPrompt.substring(0, 50) + "...");

        AiServicesExample aiServices = new AiServicesExample();
        System.out.println("AI Services builder available");

        RAGExample ragExample = new RAGExample();
        System.out.println("RAG (Retrieval Augmented Generation) examples ready");

        ChatMemoryExample memoryExample = new ChatMemoryExample();
        ChatMemory memory = memoryExample.createWindowMemory(10);
        System.out.println("Chat memory created with 10 message capacity");

        ToolExample.WeatherService weatherService = new ToolExample.WeatherService();
        System.out.println("Tool integration example available");
    }
}