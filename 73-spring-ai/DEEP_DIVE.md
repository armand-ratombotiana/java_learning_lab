# Deep Dive: Spring AI - Enterprise AI Integration for Spring Applications

## Table of Contents
1. [Introduction to Spring AI](#introduction)
2. [Core Concepts and Architecture](#core-concepts)
3. [Chat Client Implementation](#chat-client)
4. [Embedding Models and Vector Stores](#embeddings)
5. [RAG Pipeline Implementation](#rag)
6. [Multi-Provider Support](#multi-provider)
7. [Structured Output and Function Calling](#structured-output)
8. [Security and Best Practices](#security)
9. [Real-World Applications](#applications)

---

## 1. Introduction to Spring AI

Spring AI is Spring Framework's answer to integrating artificial intelligence capabilities into enterprise applications. It provides a unified, Spring-idiomatic API for working with various AI providers, embedding models, and vector databases.

### What is Spring AI?

Spring AI is an abstraction layer that simplifies AI integration by providing:

- **Unified API**: Single interface for multiple AI providers (OpenAI, Anthropic, Google, etc.)
- **Spring Ecosystem Integration**: Native support for Spring Boot, Spring MVC, and WebFlux
- **Type Safety**: Full integration with Spring's type system
- **Configuration Management**: Externalized configuration via application.properties
- **Observability**: Integration with Spring Boot Actuator and Micrometer

### Why Spring AI for Enterprise?

- **Developer Productivity**: Familiar Spring patterns applied to AI
- **Testability**: Mocking and testing with standard Spring testing tools
- **Production-Ready**: Built on proven Spring infrastructure
- **Vendor Independence**: Switch providers without code changes

---

## 2. Core Concepts and Architecture

### 2.1 Spring AI Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     Application Layer                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐    │
│  │   @RestController  │  │  @Service   │  │  @Component     │    │
│  └─────────────┘  └─────────────┘  └─────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        AI Client Layer                          │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐    │
│  │ ChatClient    │  │ EmbeddingClient│  │ ImageClient    │    │
│  └────────────────┘  └────────────────┘  └────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Model Abstraction Layer                     │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐    │
│  │ Prompt        │  │  Options       │  │  Response      │    │
│  │ Template      │  │  (temperature, │  │  Transformer   │    │
│  │               │  │  maxTokens)    │  │                │    │
│  └────────────────┘  └────────────────┘  └────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Provider Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────────────┐│
│  │ OpenAI   │  │Anthropic │  │  Google  │  │     Ollama      ││
│  │          │  │  Claude  │  │   Gemini │  │                 ││
│  └──────────┘  └──────────┘  └──────────┘  └────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 Key Components

**ChatClient**: The primary interface for LLM interactions
```java
@RestController
public class ChatController {
    
    private final ChatClient chatClient;
    
    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return chatClient.prompt(message).call().content();
    }
}
```

**EmbeddingClient**: For text-to-vector conversion
```java
@Service
public class EmbeddingService {
    
    private final EmbeddingClient embeddingClient;
    
    public List<Double> embed(String text) {
        EmbeddingResponse response = embeddingClient.embed(
            new org.springframework.ai.chat.client.UserMessage(text)
        );
        return response.getResult().getOutput();
    }
}
```

**VectorStore**: For storing and searching embeddings
```java
@Service
public class DocumentService {
    
    private final VectorStore vectorStore;
    
    public void addDocuments(List<Document> docs) {
        vectorStore.add(docs);
    }
    
    public List<Document> search(String query, int topK) {
        return vectorStore.similaritySearch(
            SearchRequest.query(query).topK(topK)
        );
    }
}
```

---

## 3. Chat Client Implementation

### 3.1 Basic Chat Client Setup

**Maven Dependencies**:
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
    <version>1.0.0-M4</version>
</dependency>
```

**Configuration (application.yaml)**:
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
          temperature: 0.7
```

**Programmatic Configuration**:
```java
@Configuration
public class AIConfig {
    
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem("You are a helpful assistant.")
            .defaultUser("Hello")
            .build();
    }
}
```

### 3.2 Prompt Templates

```java
@Service
public class TranslationService {
    
    private final ChatClient chatClient;
    
    public TranslationService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }
    
    public String translate(String text, String targetLanguage) {
        return chatClient.prompt()
            .system("You are a professional translator.")
            .user("Translate to " + targetLanguage + ": " + text)
            .call()
            .content();
    }
}
```

### 3.3 Advanced Prompt Options

```java
public String advancedPrompt(String context, String question) {
    return chatClient.prompt()
        .system(systemMessage -> systemMessage
            .text("You are a helpful assistant.")
            .param("context", context))
        .user(userMessage -> userMessage
            .text(question)
            .options(ChatOptions.builder()
                .temperature(0.3)
                .maxTokens(500)
                .build()))
        .call()
        .content();
}
```

---

## 4. Embedding Models and Vector Stores

### 4.1 Embedding Client

```java
@Service
public class EmbeddingService {
    
    private final EmbeddingClient embeddingClient;
    
    public EmbeddingService(EmbeddingClient embeddingClient) {
        this.embeddingClient = embeddingClient;
    }
    
    public List<Double> embedText(String text) {
        EmbeddingResponse response = embeddingClient.embed(
            new UserMessage(text)
        );
        return response.getResult().getOutput();
    }
    
    public List<List<Double>> embedBatch(List<String> texts) {
        return embeddingClient.embedAll(texts).stream()
            .map(r -> r.getResult().getOutput())
            .collect(Collectors.toList());
    }
}
```

### 4.2 Vector Store Configuration

```java
@Configuration
public class VectorStoreConfig {
    
    @Value("${spring.ai.vectorstore.pinecone.api-key}")
    private String pineconeApiKey;
    
    @Bean
    public VectorStore vectorStore(EmbeddingClient embeddingClient) {
        return new PineconeVectorStore(embeddingClient, pineconeApiKey);
    }
}
```

### 4.3 In-Memory Vector Store (Development)

```java
@Bean
public VectorStore inMemoryVectorStore(EmbeddingClient embeddingClient) {
    return new InMemoryVectorStore(embeddingClient);
}
```

### 4.4 Custom Vector Store Implementation

```java
@Service
public class CustomVectorStore implements VectorStore {
    
    private final EmbeddingClient embeddingClient;
    private final Map<String, Document> documentStore = new ConcurrentHashMap<>();
    
    @Override
    public void add(List<Document> documents) {
        for (Document doc : documents) {
            List<Double> embedding = embeddingClient
                .embed(new UserMessage(doc.getContent()))
                .getResult().getOutput();
            // Store with embedding
        }
    }
    
    @Override
    public List<Document> similar(SearchRequest request) {
        // Implement similarity search
    }
}
```

---

## 5. RAG Pipeline Implementation

### 5.1 Complete RAG Implementation

```java
@Service
public class RagService {
    
    private final ChatClient chatClient;
    private final EmbeddingClient embeddingClient;
    private final VectorStore vectorStore;
    
    public RagService(
            ChatClient.Builder chatClientBuilder,
            EmbeddingClient embeddingClient,
            VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.embeddingClient = embeddingClient;
        this.vectorStore = vectorStore;
    }
    
    public String answer(String question) {
        // 1. Convert question to embedding
        List<Double> queryEmbedding = embeddingClient
            .embed(new UserMessage(question))
            .getResult().getOutput();
        
        // 2. Search similar documents
        List<Document> relevantDocs = vectorStore.similaritySearch(
            SearchRequest.query(question).topK(5)
        );
        
        // 3. Build context from documents
        String context = relevantDocs.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n\n"));
        
        // 4. Generate answer with context
        return chatClient.prompt()
            .system("Use the following context to answer the question.")
            .user("Context:\n" + context + "\n\nQuestion: " + question)
            .call()
            .content();
    }
    
    public void ingestDocument(Document document) {
        vectorStore.add(List.of(document));
    }
}
```

### 5.2 Document Loading and Processing

```java
@Service
public class DocumentIngestionService {
    
    private final DocumentLoader documentLoader;
    private final TextSplitter textSplitter;
    private final VectorStore vectorStore;
    
    public void ingestFromPath(Path path) throws Exception {
        // Load document
        Document document = documentLoader.load(path);
        
        // Split into chunks
        List<Document> chunks = textSplitter.split(document);
        
        // Store in vector store
        vectorStore.add(chunks);
    }
}
```

### 5.3 PDF Document Processing

```java
@Bean
public DocumentLoader pdfDocumentLoader() {
    return new ClassPathResourceDocumentLoader("documents");
}

@Bean
public TextSplitter textSplitter() {
    return new DocumentByParagraphTextSplitter(500, 50);
}
```

---

## 6. Multi-Provider Support

### 6.1 OpenAI Configuration

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
          temperature: 0.7
    embedding:
      options:
        model: text-embedding-ada-002
```

### 6.2 Anthropic Configuration

```yaml
spring:
  ai:
    anthropic:
      api-key: ${ANTHROPIC_API_KEY}
      chat:
        options:
          model: claude-3-sonnet-20240229
          max-tokens: 1024
```

### 6.3 Google Gemini Configuration

```yaml
spring:
  ai:
    google:
      api-key: ${GOOGLE_API_KEY}
      gemini:
        chat:
          options:
            model: gemini-pro
            temperature: 0.9
```

### 6.4 Ollama (Local) Configuration

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama2
```

### 6.5 Provider Abstraction

```java
@Service
public class AiProviderService {
    
    private final Map<String, ChatClient> chatClients;
    
    public AiProviderService(
            @Qualifier("openaiChatClient") ChatClient openai,
            @Qualifier("anthropicChatClient") ChatClient anthropic,
            @Qualifier("ollamaChatClient") ChatClient ollama) {
        this.chatClients = Map.of(
            "openai", openai,
            "anthropic", anthropic,
            "ollama", ollama
        );
    }
    
    public String chat(String provider, String message) {
        ChatClient client = chatClients.get(provider);
        if (client == null) {
            throw new IllegalArgumentException("Unknown provider: " + provider);
        }
        return client.prompt(message).call().content();
    }
}
```

---

## 7. Structured Output and Function Calling

### 7.1 Structured Output with POJO

```java
record ProductReview(
    String productName,
    int rating,
    String sentiment,
    List<String> pros,
    List<String> cons
) {}

@Service
public class ReviewAnalysisService {
    
    private final ChatClient chatClient;
    
    public ReviewAnalysisService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }
    
    public ProductReview analyzeReview(String reviewText) {
        return chatClient.prompt()
            .user("Analyze: " + reviewText)
            .responseConverter(new Jackson2JsonResponseConverter())
            .call()
            .entity(ProductReview.class);
    }
}
```

### 7.2 Function Calling

```java
@Component
public class WeatherFunctions {
    
    @Tool(description = "Get current weather for a location")
    public String getWeather(@ToolParam("location") String location) {
        // Call weather API
        return "Current weather in " + location + ": 22°C, sunny";
    }
}

@Service
public class ChatWithFunctionsService {
    
    private final ChatClient chatClient;
    
    public ChatWithFunctionsService(ChatClient.Builder builder) {
        this.chatClient = builder
            .defaultFunctions(new WeatherFunctions())
            .build();
    }
    
    public String chatWithTools(String message) {
        return chatClient.prompt(message).call().content();
    }
}
```

### 7.3 Advanced Function Calling

```java
public class AdvancedFunctions {
    
    @Tool(description = "Search products in database")
    public List<Product> searchProducts(
            @ToolParam("query") String query,
            @ToolParam("maxResults") int maxResults) {
        // Search logic
    }
    
    @Tool(description = "Calculate order total")
    public double calculateTotal(
            @ToolParam("items") List<OrderItem> items,
            @ToolParam("shipping") double shipping) {
        return items.stream().mapToDouble(OrderItem::price).sum() + shipping;
    }
}
```

---

## 8. Security and Best Practices

### 8.1 API Key Management

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/health").permitAll()
                .anyRequest().authenticated())
            .build();
    }
}
```

**Environment-based configuration**:
```java
@Component
public class ApiKeyValidator {
    
    @Value("${spring.ai.openai.api-key}")
    private String openAiKey;
    
    @PostConstruct
    public void validate() {
        if (openAiKey == null || openAiKey.isBlank()) {
            throw new IllegalStateException(
                "OPENAI_API_KEY must be configured");
        }
    }
}
```

### 8.2 Input Validation

```java
@Service
public class ValidatedChatService {
    
    private final ChatClient chatClient;
    
    public ValidatedChatService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }
    
    public String chat(String message) {
        // Validate input
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        if (message.length() > 10000) {
            throw new IllegalArgumentException("Message too long");
        }
        
        // Sanitize
        String sanitized = message.replaceAll("[<>]", "");
        
        return chatClient.prompt(sanitized).call().content();
    }
}
```

### 8.3 Rate Limiting

```java
@Service
public class RateLimitedChatService {
    
    private final ChatClient chatClient;
    private final RateLimiter rateLimiter;
    
    public RateLimitedChatService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
        this.rateLimiter = new TokenBucketRateLimiter(50, Duration.ofMinutes(1));
    }
    
    public String chat(String message) {
        if (!rateLimiter.tryAcquire()) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }
        return chatClient.prompt(message).call().content();
    }
}
```

---

## 9. Real-World Applications

### 9.1 Enterprise Knowledge Base

```java
@RestController
public class KnowledgeBaseController {
    
    private final RagService ragService;
    private final DocumentService documentService;
    
    public KnowledgeBaseController(RagService ragService, 
                                   DocumentService documentService) {
        this.ragService = ragService;
        this.documentService = documentService;
    }
    
    @PostMapping("/kb/ask")
    public ResponseEntity<String> ask(@RequestBody AskRequest request) {
        String answer = ragService.answer(request.question());
        return ResponseEntity.ok(answer);
    }
    
    @PostMapping("/kb/ingest")
    public ResponseEntity<Void> ingest(@RequestParam("file") MultipartFile file) 
            throws Exception {
        documentService.ingest(file.getBytes(), file.getOriginalFilename());
        return ResponseEntity.accepted().build();
    }
}
```

### 9.2 Customer Support Chatbot

```java
@Service
public class SupportChatbot {
    
    private final ChatClient chatClient;
    private final TicketService ticketService;
    
    public SupportChatbot(ChatClient.Builder builder, TicketService ticketService) {
        this.chatClient = builder
            .defaultSystem("You are a customer support agent...")
            .build();
        this.ticketService = ticketService;
    }
    
    public String handleCustomerQuery(String query) {
        if (isEscalationNeeded(query)) {
            return escalateToHuman(query);
        }
        return chatClient.prompt(query).call().content();
    }
    
    private boolean isEscalationNeeded(String query) {
        return query.toLowerCase().contains("speak to human") ||
               query.toLowerCase().contains("manager");
    }
}
```

### 9.3 Data Analysis Assistant

```java
@Service
public class DataAnalysisService {
    
    private final ChatClient chatClient;
    private final QueryExecutor queryExecutor;
    
    public String analyze(String question, String dataContext) {
        // Generate SQL from natural language
        String sql = generateSQL(question);
        
        // Execute query
        List<Map<String, Object>> results = queryExecutor.execute(sql);
        
        // Generate analysis
        return chatClient.prompt()
            .user("Data: " + results + "\nQuestion: " + question)
            .call()
            .content();
    }
}
```

---

## Summary

| Component | Description |
|-----------|-------------|
| ChatClient | Unified interface for LLM interactions |
| EmbeddingClient | Text-to-vector conversion |
| VectorStore | Embedding storage and retrieval |
| Prompt | Template-based prompt construction |
| ChatOptions | Runtime configuration (temperature, tokens) |
| Function Calling | Extend LLM capabilities with tools |

---

## Additional Resources

- [Spring AI Reference Documentation](https://docs.spring.io/spring-ai/)
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai)
- [OpenAI API Documentation](https://platform.openai.com/docs)

---

*This deep dive provides comprehensive coverage of Spring AI for enterprise applications. Continue to QUIZZES.md for assessment and EDGE_CASES.md for debugging.*