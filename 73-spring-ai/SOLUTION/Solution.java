package com.learning.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.PineconeVectorStore;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.ai.vectorstore.ChromaVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.mistralai.MistralAiChatModel;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Solution {

    public static class ChatClientExample {

        public ChatClient createChatClient(ChatModel chatModel) {
            return ChatClient.builder(chatModel).build();
        }

        public String simpleChat(ChatClient client, String message) {
            return client.prompt(message).call().content();
        }

        public String chatWithSystemPrompt(ChatClient client, String systemPrompt, String userMessage) {
            return client.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();
        }

        public String chatWithMemory(ChatClient client, String message, ChatMemory memory) {
            return client.prompt()
                .advisors(new MessageChatMemoryAdvisor(memory))
                .user(message)
                .call()
                .content();
        }
    }

    public static class PromptTemplateExample {

        public Prompt createPrompt(String template, Map<String, Object> variables) {
            PromptTemplate promptTemplate = new PromptTemplate(template);
            return promptTemplate.create(variables);
        }

        public Prompt createSummarizationPrompt(String text, int maxSentences) {
            PromptTemplate template = new PromptTemplate(
                "Summarize the following text in exactly {maxSentences} sentences:\n\n{text}"
            );
            return template.create(Map.of(
                "maxSentences", maxSentences,
                "text", text
            ));
        }

        public Prompt createExtractionPrompt(String text, String extractionType) {
            PromptTemplate template = new PromptTemplate(
                "Extract {type} information from the following text:\n\n{text}"
            );
            return template.create(Map.of(
                "type", extractionType,
                "text", text
            ));
        }

        public Prompt createTranslationPrompt(String text, String targetLanguage) {
            String template = """
                Translate the following text to {targetLanguage}.
                Maintain the original meaning and tone.

                Text: {text}
                """;
            PromptTemplate promptTemplate = new PromptTemplate(template);
            return promptTemplate.create(Map.of(
                "targetLanguage", targetLanguage,
                "text", text
            ));
        }
    }

    public static class VectorStoreExample {

        public VectorStore createPineconeStore(EmbeddingModel embeddingModel,
                                                String apiKey, String environment, String index) {
            return PineconeVectorStore.builder()
                .embeddingModel(embeddingModel)
                .apiKey(apiKey)
                .environment(environment)
                .indexName(index)
                .build();
        }

        public VectorStore createMilvusStore(EmbeddingModel embeddingModel,
                                              String host, int port, String collection) {
            return MilvusVectorStore.builder()
                .embeddingModel(embeddingModel)
                .host(host)
                .port(port)
                .collectionName(collection)
                .build();
        }

        public VectorStore createChromaStore(EmbeddingModel embeddingModel, String baseUrl) {
            return ChromaVectorStore.builder()
                .embeddingModel(embeddingModel)
                .baseUrl(baseUrl)
                .build();
        }

        public void addDocuments(VectorStore store, List<String> documents) {
            store.add(documents.stream()
                .map(text -> org.springframework.ai.document.Document.builder()
                    .text(text)
                    .build())
                .toList());
        }

        public List<org.springframework.ai.document.Document> searchSimilar(
                VectorStore store, String query, int topK) {

            SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();

            return store.similaritySearch(searchRequest);
        }

        public void deleteByIds(VectorStore store, List<String> ids) {
            store.delete(ids);
        }
    }

    public static class RAGExample {

        public String queryWithContext(ChatClient client, VectorStore vectorStore, String query) {
            return client.prompt()
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                    .build())
                .user(query)
                .call()
                .content();
        }

        public String queryWithFilter(ChatClient client, VectorStore vectorStore,
                                      String query, String filterKey, String filterValue) {

            SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(5)
                .filterExpression(filterKey + " == '" + filterValue + "'")
                .build();

            return client.prompt()
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                    .searchRequest(searchRequest)
                    .build())
                .user(query)
                .call()
                .content();
        }

        public void addWithMetadata(VectorStore store, String text, Map<String, Object> metadata) {
            var document = org.springframework.ai.document.Document.builder()
                .text(text)
                .metadata(metadata)
                .build();
            store.add(List.of(document));
        }
    }

    public static class ChatMemoryExample {

        public ChatMemory createInMemoryMemory() {
            return new org.springframework.ai.chat.memory.InMemoryChatMemory();
        }

        public ChatMemory createVectorStoreMemory(VectorStore vectorStore) {
            return org.springframework.ai.chat.memory.vector.VectorStoreChatMemory.builder()
                .vectorStore(vectorStore)
                .build();
        }
    }

    public static class StreamingExample {

        public void streamChat(ChatClient client, String message) {
            client.prompt(message)
                .stream()
                .content()
                .doOnNext(chunk -> System.out.print(chunk))
                .block();
        }
    }

    public static class MultiModalExample {

        public String chatWithImage(ChatClient client, String imageUrl, String question) {
            return client.prompt()
                .user(user -> user
                    .text(question)
                    .media(org.springframework.ai.chat.messages.MediaType.IMAGE_URL, imageUrl))
                .call()
                .content();
        }

        public String chatWithAudio(ChatClient client, String audioUrl, String question) {
            return client.prompt()
                .user(user -> user
                    .text(question)
                    .media(org.springframework.ai.chat.messages.MediaType.AUDIO_URL, audioUrl))
                .call()
                .content();
        }
    }

    public static class FunctionCallingExample {

        public interface WeatherService {
            @org.springframework.ai.function.annotation.Function
            String getCurrentWeather(String location);

            @org.springframework.ai.function.annotation.Function
            String getForecast(String location, int days);
        }

        public ChatClient createWithFunctions(ChatModel chatModel, WeatherService weatherService) {
            return ChatClient.builder(chatModel)
                .defaultFunctions("weatherFunctions", weatherService)
                .build();
        }
    }

    static class ChatMemory {
    }

    public static void main(String[] args) {
        System.out.println("Spring AI Solutions");
        System.out.println("====================");

        ChatClientExample chatClientExample = new ChatClientExample();
        System.out.println("ChatClient examples available");

        PromptTemplateExample promptExample = new PromptTemplateExample();
        Prompt summarizationPrompt = promptExample.createSummarizationPrompt("Sample text", 3);
        System.out.println("Created summarization prompt: " + summarizationPrompt.getContents().size() + " messages");

        VectorStoreExample vectorStoreExample = new VectorStoreExample();
        System.out.println("Vector store examples ready");

        RAGExample ragExample = new RAGExample();
        System.out.println("RAG examples available");

        ChatMemoryExample memoryExample = new ChatMemoryExample();
        ChatMemory memory = memoryExample.createInMemoryMemory();
        System.out.println("Chat memory created");
    }
}