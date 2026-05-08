# Edge Cases, Pitfalls, and Best Practices - Spring AI

## Common Pitfalls

### Pitfall 1: ChatClient Not Autowired

**Symptom**: `NullPointerException: ChatClient is null`

**Cause**: Not properly injecting the ChatClient bean

**Solution**:
```java
@RestController
public class ChatController {
    
    private final ChatClient chatClient;
    
    // Constructor injection (preferred)
    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    // Or field injection (not recommended)
    // @Autowired
    // private ChatClient chatClient;
}
```

---

### Pitfall 2: Null Response from ChatClient

**Symptom**: NullPointerException when accessing response content

**Solution**:
```java
String content = chatClient.prompt("Hello")
    .call()
    .content();  // May return null

// Better: Check for null or use optional
String content = Optional.ofNullable(
    chatClient.prompt("Hello").call().content()
).orElse("No response");
```

---

### Pitfall 3: Incompatible Embedding Dimensions

**Symptom**: `IllegalArgumentException: Dimension mismatch`

**Cause**: Using different embedding models for indexing and retrieval

**Solution**: Always use the same embedding client for both operations.

---

### Pitfall 4: Large Document Memory Issues

**Symptom**: OutOfMemoryError when processing large documents

**Solution**:
```java
@Bean
public TextSplitter textSplitter() {
    // Use smaller chunk sizes and proper overlap
    return new DocumentByParagraphTextSplitter(500, 50);
}
```

---

### Pitfall 5: API Key Not Found

**Symptom**: `IllegalStateException: No API key found`

**Solution**:
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
```
Ensure the environment variable is set before starting the application.

---

### Pitfall 6: Vector Store Not Initialized

**Symptom**: `NullPointerException` when adding to vector store

**Solution**:
```java
@Configuration
public class VectorStoreConfig {
    
    @Bean
    public VectorStore vectorStore(EmbeddingClient embeddingClient) {
        return new InMemoryVectorStore(embeddingClient);
    }
}
```

---

### Pitfall 7: ChatOptions Not Applied

**Symptom**: Temperature or other options not being applied

**Solution**:
```java
// Options must be applied to the prompt
chatClient.prompt()
    .options(ChatOptions.builder()
        .temperature(0.5)
        .maxTokens(500)
        .build())
    .call()
    .content();
```

---

### Pitfall 8: Mixing Provider Configurations

**Symptom**: Configuration conflicts or unexpected behavior

**Solution**: Use separate configuration classes or profiles for different providers.

---

## Debugging Tips

### Tip 1: Enable Debug Logging

```yaml
logging:
  level:
    org.springframework.ai: DEBUG
    dev.langchain4j: DEBUG
```

### Tip 2: Inspect Generated Prompts

```java
// Add system out for debugging
String result = chatClient.prompt()
    .user("Your message")
    .call()
    .content();

System.out.println("Response: " + result);
```

### Tip 3: Check Vector Store Contents

```java
// Debug vector store
vectorStore.similaritySearch(SearchRequest.query("*").topK(10))
    .forEach(doc -> System.out.println(doc.getContent()));
```

### Tip 4: Test Embedding Client

```java
// Test embedding output
List<Double> embedding = embeddingClient
    .embed(new UserMessage("test"))
    .getResult().getOutput();

System.out.println("Embedding size: " + embedding.size());
```

---

## Best Practices

### Best Practice 1: Environment-Based Configuration

```java
@Configuration
public class AIConfiguration {
    
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem("You are a helpful assistant.")
            .build();
    }
}
```

### Best Practice 2: Graceful Error Handling

```java
@Service
public class ResilientChatService {
    
    public String chat(String message) {
        try {
            return chatClient.prompt(message).call().content();
        } catch (Exception e) {
            log.error("Chat failed", e);
            return "I apologize, but I'm temporarily unavailable.";
        }
    }
}
```

### Best Practice 3: Resource Cleanup

```java
// Implement Closeable if needed
@Component
public class AIClientManager implements Closeable {
    
    @Override
    public void close() {
        // Clean up resources
    }
}
```

---

## Troubleshooting Guide

| Issue | Cause | Solution |
|-------|-------|----------|
| Null response | API key or model issue | Check API key, model name |
| Slow responses | Large context | Implement RAG, reduce context |
| Out of memory | Large documents | Implement chunking |
| Rate limiting | Too many requests | Implement backoff |

---

## Performance Anti-Patterns

### Anti-Pattern: Creating New Clients Per Request

**BAD**:
```java
public String chat(String msg) {
    ChatClient client = ChatClient.builder().build(); // New each time
    return client.prompt(msg).call().content();
}
```

**GOOD**:
```java
@Service
public class ChatService {
    private final ChatClient chatClient; // Reused
    
    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
}
```

---

## Summary Checklist

- [ ] Use constructor injection for ChatClient
- [ ] Configure API keys via environment variables
- [ ] Use consistent embedding models
- [ ] Implement proper document chunking
- [ ] Add error handling for API failures
- [ ] Enable appropriate logging levels
- [ ] Use vector stores appropriate for environment
- [ ] Monitor token usage and costs
- [ ] Implement retry logic for transient failures
- [ ] Test with production-like data volumes

---

*This guide complements DEEP_DIVE.md and QUIZZES.md for comprehensive Spring AI learning.*