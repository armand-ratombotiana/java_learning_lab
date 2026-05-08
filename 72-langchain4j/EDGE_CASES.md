# Edge Cases, Pitfalls, and Best Practices - LangChain4j

## Table of Contents
1. [Common Pitfalls](#common-pitfalls)
2. [Debugging Tips](#debugging-tips)
3. [Best Practices](#best-practices)
4. [Troubleshooting Guide](#troubleshooting-guide)
5. [Performance Anti-Patterns](#performance-anti-patterns)

---

## 1. Common Pitfalls

### Pitfall 1: API Key Not Set

**Symptom**: `IllegalStateException: OPENAI_API_KEY environment variable not set`

**Cause**: Environment variable not configured or not loaded properly

**Solution**:
```java
public class Config {
    public static String getApiKey() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                "Please set OPENAI_API_KEY environment variable");
        }
        return apiKey;
    }
}
```

**Prevention**: Create a startup check that validates required environment variables

---

### Pitfall 2: Memory Leak with InMemoryChatMemory

**Symptom**: Application memory grows unbounded over time

**Cause**: ChatMemory stores all messages without cleanup

**Solution**:
```java
// Always use bounded memory
ChatMemory chatMemory = MessageWindowChatMemory.builder()
    .maxMessages(20)  // Limit to recent messages
    .build();

// Or use token-based memory
ChatMemory tokenMemory = TokenWindowChatMemory.builder()
    .maxTokens(2000)
    .build();
```

---

### Pitfall 3: Embedding Dimension Mismatch

**Symptom**: `IllegalArgumentException: Dimension mismatch`

**Cause**: Using different embedding models for indexing and retrieval

**Solution**:
```java
// Always use the SAME embedding model for both storage and retrieval
EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

// Indexing
EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
    .embeddingModel(embeddingModel)  // Same model
    .embeddingStore(store)
    .build();

// Retrieval
RetrievalAugmentor augmentor = DefaultRetrievalAugmentor.builder()
    .embeddingModel(embeddingModel)  // Same model!
    .embeddingStore(store)
    .build();
```

---

### Pitfall 4: Chunk Size Too Large or Too Small

**Symptom**: Poor retrieval results or context truncation

**Solution**:
```java
// Good default: 500 characters with 50 overlap
DocumentSplitter splitter = DocumentSplitters
    .byParagraph(500, 50);

// For code, use smaller chunks
DocumentSplitter codeSplitter = DocumentSplitters
    .byParagraph(200, 20);  // Smaller for code

// For narrative text, larger chunks work
DocumentSplitter narrativeSplitter = DocumentSplitters
    .byParagraph(1000, 100);
```

---

### Pitfall 5: Not Handling Rate Limits

**Symptom**: `RateLimitException` or connection failures under load

**Solution**:
```java
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class RateLimitedChatModel {
    
    public static ChatLanguageModel createRateLimitedModel() {
        return OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .maxRetries(3)
            .timeout(Duration.ofSeconds(60))
            .build();
    }
}
```

---

### Pitfall 6: Null Pointer in Tool Execution

**Symptom**: `NullPointerException` when tool returns null

**Cause**: Tool method returns null instead of empty string

**Solution**:
```java
@Tool("Search for information")
String search(String query) {
    if (query == null || query.isEmpty()) {
        return "Please provide a search query";  // Never return null
    }
    // ... search logic
    return results;  // Always return a string
}
```

---

### Pitfall 7: Not Handling Network Timeouts

**Symptom**: Application hangs indefinitely

**Solution**:
```java
ChatLanguageModel model = OpenAiChatModel.builder()
    .apiKey(System.getenv("OPENAI_API_KEY"))
    .timeout(Duration.ofSeconds(30))  // Set timeout
    .connectTimeout(Duration.ofSeconds(10))
    .readTimeout(Duration.ofSeconds(30))
    .build();
```

---

### Pitfall 8: Ignoring Response Caching for Repeated Queries

**Symptom**: High API costs and slow responses for common queries

**Solution**:
```java
ChatLanguageModel baseModel = OpenAiChatModel.builder()
    .apiKey(System.getenv("OPENAI_API_KEY"))
    .build();

ChatLanguageModel cachedModel = CachingChatLanguageModel.builder()
    .delegatee(baseModel)
    .cache(InMemoryChatCache.builder()
        .maxCacheSize(1000)
        .build())
    .build();
```

---

### Pitfall 9: Mixing Synchronous and Async Code

**Symptom**: Deadlocks or unexpected blocking

**Solution**:
```java
// Either use blocking or async consistently
// Option 1: All synchronous
String response = model.chat("Hello");

// Option 2: All async
CompletableFuture<String> future = CompletableFuture.supplyAsync(
    () -> model.chat("Hello")
);
```

---

### Pitfall 10: Not Validating User Input

**Symptom**: Prompt injection or unexpected behavior

**Solution**:
```java
public class InputValidator {
    
    public static String sanitize(String input) {
        if (input == null) return "";
        
        // Remove potential prompt injection patterns
        return input
            .replaceAll("(?i)(system:|ignore previous)", "")
            .replaceAll("[<>]", "")  // Remove HTML
            .trim();
    }
    
    public static boolean isValidLength(String input) {
        return input != null && 
               input.length() > 0 && 
               input.length() < 10000;
    }
}
```

---

## 2. Debugging Tips

### Tip 1: Enable Logging

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Enable DEBUG logging for LangChain4j
// In application.properties or logback.xml:
// logger.dev.langchain4j=DEBUG

public class DebugExample {
    
    private static final Logger log = 
        LoggerFactory.getLogger(DebugExample.class);
    
    public static void main(String[] args) {
        log.debug("Starting chat request");
        String response = model.chat("Hello");
        log.debug("Received response: {}", response);
    }
}
```

### Tip 2: Print Generated Prompts

```java
// Use to debug what prompt is being sent to LLM
public class PromptDebugger {
    
    public static void printPrompt(Assistant assistant) {
        // Add system message to print prompts
        System.out.println("=== DEBUG: Check prompt in logs ===");
    }
}
```

### Tip 3: Verify Embedding Similarity

```java
public class EmbeddingDebugger {
    
    public static void debugSimilarity(
            EmbeddingModel model,
            String text1,
            String text2) {
        
        Embedding embedding1 = model.embed(text1).content();
        Embedding embedding2 = model.embed(text2).content();
        
        double similarity = CosineSimilarity.between(embedding1, embedding2);
        
        System.out.println("Similarity between '" + text1 + "' and '" + text2 + "' = " + similarity);
    }
}
```

### Tip 4: Inspect Retrieved Documents

```java
public class RetrievalDebugger {
    
    public static void inspectRetrieval(
            ContentRetriever retriever,
            String query) {
        
        List<Content> contents = retriever.retrieve(query);
        
        System.out.println("=== Retrieved " + contents.size() + " documents ===");
        for (int i = 0; i < contents.size(); i++) {
            System.out.println("Document " + (i+1) + ":");
            System.out.println(contents.get(i).textSegment().text());
            System.out.println("---");
        }
    }
}
```

### Tip 5: Test with Small Dataset First

```java
public class TestHelper {
    
    public static List<Document> createTestDocuments() {
        return List.of(
            new Document("Java is a programming language"),
            new Document("Python is a programming language"),
            new Document("JavaScript is a web programming language")
        );
    }
    
    public static String testQuery() {
        return "Tell me about Java programming";
    }
}
```

---

## 3. Best Practices

### Best Practice 1: Environment-Based Configuration

```java
public class LLMConfig {
    
    private static final String PROVIDER = 
        System.getenv().getOrDefault("LLM_PROVIDER", "openai");
    private static final String API_KEY = 
        System.getenv("OPENAI_API_KEY");
    
    public static ChatLanguageModel createModel() {
        return switch (PROVIDER) {
            case "ollama" -> OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama2")
                .build();
            case "openai", default -> OpenAiChatModel.builder()
                .apiKey(API_KEY)
                .modelName("gpt-4")
                .build();
        };
    }
}
```

### Best Practice 2: Graceful Degradation

```java
public class ResilientAssistant {
    
    private final ChatLanguageModel primaryModel;
    private final ChatLanguageModel fallbackModel;
    
    public String chat(String message) {
        try {
            return primaryModel.chat(message);
        } catch (Exception e) {
            log.warn("Primary model failed, using fallback", e);
            return fallbackModel.chat(message);
        }
    }
}
```

### Best Practice 3: Structured Logging

```java
public class LoggedAssistant {
    
    public String chat(String userId, String message) {
        long startTime = System.currentTimeMillis();
        
        log.info("User {} sending message: {}", userId, message);
        
        String response = model.chat(message);
        
        log.info("User {} received response in {}ms", 
            userId, 
            System.currentTimeMillis() - startTime);
        
        return response;
    }
}
```

### Best Practice 4: Connection Pooling

```java
public class PooledChatModel {
    
    public static ChatLanguageModel create() {
        OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(
                10,  // max idle connections
                5,   // keep-alive duration minutes
                TimeUnit.MINUTES))
            .build();
        
        return OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .okHttpClient(client)
            .build();
    }
}
```

### Best Practice 5: Resource Cleanup

```java
public class ResourceManagement {
    
    public static void main(String[] args) {
        ChatLanguageModel model = null;
        try {
            model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();
            // Use model
        } finally {
            if (model instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) model).close();
                } catch (Exception e) {
                    log.error("Error closing model", e);
                }
            }
        }
    }
}
```

---

## 4. Troubleshooting Guide

### Issue: "Model does not support tools"

**Cause**: Using a model that doesn't support function calling

**Solution**: Use GPT-4 or GPT-3.5 Turbo models which support tools

---

### Issue: Empty response from model

**Cause**: Prompt too restrictive or temperature too low

**Solution**: 
```java
OpenAiChatModel.builder()
    .temperature(0.7)  // Increase from 0
    .build();
```

---

### Issue: "Document too large" errors

**Cause**: Documents exceed model's context window

**Solution**: Implement proper document chunking with overlap

---

### Issue: Slow retrieval performance

**Cause**: Too many documents or inefficient similarity search

**Solution**: Use approximate nearest neighbor (ANN) index

---

### Issue: Inconsistent responses

**Cause**: Temperature too high or different model versions

**Solution**:
```java
OpenAiChatModel.builder()
    .temperature(0.1)  // Lower for consistency
    .build();
```

---

## 5. Performance Anti-Patterns

### Anti-Pattern 1: Creating New Model Per Request

**BAD**:
```java
public String chat(String message) {
    ChatLanguageModel model = OpenAiChatModel.builder()
        .apiKey(System.getenv("OPENAI_API_KEY"))
        .build();  // NEW model each time!
    return model.chat(message);
}
```

**GOOD**:
```java
public class ChatService {
    private final ChatLanguageModel model;  // Reuse
    
    public ChatService() {
        this.model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();
    }
    
    public String chat(String message) {
        return model.chat(message);
    }
}
```

---

### Anti-Pattern 2: Synchronous Blocking in Web Framework

**BAD**:
```java
@PostMapping("/chat")
public String chat(@RequestBody ChatRequest request) {
    return model.chat(request.getMessage());  // Blocks thread
}
```

**GOOD**:
```java
@PostMapping("/chat")
public CompletableFuture<String> chat(@RequestBody ChatRequest request) {
    return CompletableFuture.supplyAsync(() -> 
        model.chat(request.getMessage()));
}
```

---

### Anti-Pattern 3: Not Using Batch Processing for Embeddings

**BAD**:
```java
for (String text : texts) {
    Embedding embedding = model.embed(text).content();  // Sequential
    store.add(embedding, TextSegment.from(text));
}
```

**GOOD**:
```java
List<TextSegment> segments = texts.stream()
    .map(TextSegment::from)
    .collect(Collectors.toList());

List<Embedding> embeddings = embeddingModel.embedAll(
    segments.stream()
        .map(TextSegment::text)
        .collect(Collectors.toList())
).content();

for (int i = 0; i < embeddings.size(); i++) {
    store.add(embeddings.get(i), segments.get(i));
}
```

---

### Anti-Pattern 4: No Caching for Repeated Embeddings

**BAD**:
```java
public Embedding getEmbedding(String text) {
    return model.embed(text).content();  // Always calls API
}
```

**GOOD**:
```java
public class CachedEmbeddingModel {
    
    private final EmbeddingModel model;
    private final Cache<String, Embedding> cache;
    
    public Embedding getEmbedding(String text) {
        String key = text;  // Or use hash for semantic equivalence
        return cache.get(key, () -> model.embed(text).content());
    }
}
```

---

### Anti-Pattern 5: Ignoring Context Window Limits

**BAD**:
```java
String hugePrompt = loadEntireBook();
model.chat(hugePrompt);  // May exceed context limit
```

**GOOD**:
```java
public String truncateToContextLimit(String text, int maxTokens) {
    // Use tokenizer to count tokens and truncate
    // Or rely on RAG to retrieve only relevant chunks
}
```

---

## Summary Checklist

- [ ] Set API keys via environment variables
- [ ] Configure timeouts (30-60 seconds)
- [ ] Use bounded chat memory (message window or token limit)
- [ ] Use same embedding model for indexing and retrieval
- [ ] Implement proper document chunking
- [ ] Add retry logic for transient failures
- [ ] Enable logging for debugging
- [ ] Implement input validation/sanitization
- [ ] Reuse ChatModel instances
- [ ] Implement caching for repeated queries
- [ ] Add structured logging for observability
- [ ] Test with production-like data volumes

---

*This edge cases guide helps you avoid common mistakes and build robust LangChain4j applications. Combined with the DEEP_DIVE.md for concepts and QUIZZES.md for assessment, you have a complete learning path.*