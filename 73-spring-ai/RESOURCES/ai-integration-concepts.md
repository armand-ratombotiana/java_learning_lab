# AI Integration Concepts

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    Spring AI Architecture                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    Your Application                     │   │
│  │                                                          │   │
│  │   ┌──────────────┐  ┌──────────────┐  ┌────────────┐  │   │
│  │   │  ChatClient  │  │   Image      │  │ Embedding  │  │   │
│  │   │              │  │   Client     │  │ Client     │  │   │
│  │   └──────┬───────┘  └──────┬───────┘  └──────┬─────┘  │   │
│  │          │                  │                  │        │   │
│  │   ┌──────┴──────────────────┴──────────────────┴─────┐  │   │
│  │   │              AI Model Clients                     │  │   │
│  │   │         (OpenAI, Anthropic, Ollama, etc.)       │  │   │
│  │   └────────────────────────┬─────────────────────────┘  │   │
│  └────────────────────────────┼────────────────────────────┘   │
│                               │                                  │
│        ┌──────────────────────┼──────────────────────┐           │
│        │                      │                      │           │
│        ▼                      ▼                      ▼           │
│  ┌──────────┐          ┌──────────┐          ┌──────────┐       │
│  │  OpenAI  │          │ Claude   │          │ Ollama   │       │
│  └──────────┘          └──────────┘          └──────────┘       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## ChatClient Usage

```java
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;

    public String chat(String userMessage) {
        return chatClient.prompt()
            .user(userMessage)
            .call()
            .content();
    }

    public String chatWithSystemPrompt(String userMessage, String systemPrompt) {
        return chatClient.prompt()
            .system(systemPrompt)
            .user(userMessage)
            .call()
            .content();
    }

    public Mono<String> streamChat(String userMessage) {
        return chatClient.prompt()
            .user(userMessage)
            .stream()
            .content();
    }
}
```

## Prompt Templates

```java
// Simple template
String response = chatClient.prompt()
    .prompt("Tell me about {topic}")
    .param("topic", "Java")
    .call()
    .content();

// Structured output
record Book(String title, String author, int year) {}

Book book = chatClient.prompt()
    .prompt("Extract book info: {text}")
    .param("text", "The Great Gatsby by F. Scott Fitzgerald (1925)")
    .advisors(new StructuredOutputAdvisor<>(Book.class))
    .call()
    .entity(Book.class);

// List output
List<String> items = chatClient.prompt()
    .prompt("List 3 colors")
    .call()
    .asList(String.class);
```

## Embeddings & Vector Store

```java
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final EmbeddingClient embeddingClient;
    private final VectorStore vectorStore;

    public void indexDocuments(List<String> documents) {
        List<Document> docs = documents.stream()
            .map(doc -> Document.builder()
                .text(doc)
                .metadata(Map.of("source", "manual"))
                .build())
            .toList();

        vectorStore.add(docs);
    }

    public List<String> semanticSearch(String query) {
        // Generate embedding for query
        Embedding embedding = embeddingClient.embed(query);

        // Search vector store
        return vectorStore.similaritySearch(SearchRequest.query(query)
            .withTopK(5))
            .stream()
            .map(Document::getText)
            .toList();
    }
}
```

## RAG Pattern (Retrieval Augmented Generation)

```
┌─────────────────────────────────────────────────────────────────┐
│  RAG ARCHITECTURE                                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. INDEXING PHASE                                              │
│     ─────────────────                                            │
│     Documents → Chunk → Embed → Vector Store                   │
│                                                                  │
│  2. QUERY PHASE                                                 │
│     ─────────────                                               │
│     User Query → Embed → Vector Store → Context                 │
│                                      ↓                          │
│                          LLM + Context → Response               │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  User Query: "How do I configure datasource in Spring?"  │   │
│  │                         ↓                                │   │
│  │  Vector Search → Find relevant docs                      │   │
│  │                         ↓                                │   │
│  │  Context: "In application.properties: spring.datasource  │   │
│  │            url=jdbc:postgresql://localhost:5432/mydb..." │   │
│  │                         ↓                                │   │
│  │  LLM Response with context                                │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

## Function Calling (Tools)

```java
@Bean
public ChatClient chatClient(ChatClient.Builder builder) {
    return builder
        .defaultTools(weatherTool(), searchTool())
        .build();
}

@Tool(description = "Get current weather for a city")
String getWeather(String city) {
    return weatherService.getCurrentWeather(city);
}

// Usage
String response = chatClient.prompt()
    .user("What's the weather in Tokyo?")
    .call()
    .content();
```

## Model Configuration

```yaml
# application.yml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
          temperature: 0.7
          max-tokens: 1000
    embedding:
      client: openai
      options:
        model: text-embedding-3-small

  ai.vector.store:
    chroma:
      initialize-schema: true
```

## Common Patterns

| Pattern | Description | Use Case |
|---------|-------------|----------|
| RAG | Retrieve context + LLM generation | Knowledge bases |
| Chain | Sequential LLM calls | Multi-step tasks |
| Branch | Parallel paths | Multiple options |
| Memory | Conversation history | Chatbots |
| Function | Tool integration | Actions, data fetch |

## Best Practices

1. **Chunk documents wisely** - 500-1000 tokens typical
2. **Use appropriate embedding model** - task-specific
3. **Implement proper error handling** - API failures
4. **Add caching for repeated queries** - cost optimization
5. **Monitor token usage** - cost tracking
6. **Validate LLM outputs** - especially for structured data
7. **Use streaming for long responses** - better UX