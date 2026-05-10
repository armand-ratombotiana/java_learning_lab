# LangChain4j Integration - Solution

## Overview

This module provides comprehensive examples for integrating Large Language Models (LLMs) using LangChain4j. It covers chat models, prompt engineering, AI services, RAG (Retrieval Augmented Generation), and tool integration.

## Dependencies

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
    <version>0.35.0</version>
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
    <version>0.35.0</version>
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-anthropic</artifactId>
    <version>0.35.0</version>
</dependency>
```

## Key Concepts

### 1. Chat Models

The `ChatModelExample` class demonstrates:
- OpenAI GPT-4 integration
- Anthropic Claude integration
- Ollama local model support
- Model configuration (temperature, max tokens)

```java
ChatLanguageModel model = OpenAiChatModel.builder()
    .apiKey(apiKey)
    .modelName("gpt-4")
    .temperature(0.7)
    .build();
```

### 2. Prompt Templates

The `PromptTemplateExample` class covers:
- Variable substitution in templates
- Structured prompts for specific tasks
- Travel planning prompts
- Code review prompts
- Summarization prompts

### 3. AI Services (DSL)

The `AiServicesExample` class provides:
- Type-safe LLM interaction
- System and user message annotations
- Interface-based chat agents
- Domain-specific assistants

### 4. RAG (Retrieval Augmented Generation)

The `RAGExample` class implements:
- Document loading and splitting
- Embedding creation
- Vector store integration
- Context-aware question answering

### 5. Chat Memory

The `ChatMemoryExample` class demonstrates:
- Message window memory
- Conversation context management
- Memory clearing and persistence

### 6. Tools Integration

The `ToolExample` class covers:
- Tool specification and execution
- Weather service example
- Dynamic function calling

## Classes Overview

| Class | Description |
|-------|-------------|
| `ChatModelExample` | Chat model configuration for various LLM providers |
| `PromptTemplateExample` | Dynamic prompt generation with variables |
| `AiServicesExample` | Type-safe AI service builder |
| `RAGExample` | Retrieval Augmented Generation implementation |
| `ChatMemoryExample` | Conversation memory management |
| `ToolExample` | Tool integration for function calling |

## Running Tests

```bash
cd 72-langchain4j
mvn test -Dtest=Test
```

## Examples

### Creating a Chat Model

```java
ChatLanguageModel openAiModel = new ChatModelExample()
    .createOpenAIModel(System.getenv("OPENAI_API_KEY"));

String response = openAiModel.chat("Hello, how can you help me?");
```

### Building an AI Service

```java
TravelAssistant assistant = new AiServicesExample()
    .createTravelAssistant(model);

String recommendation = assistant.recommendDestination("I want a beach vacation in Europe");
```

### Implementing RAG

```java
List<TextSegment> segments = new RAGExample()
    .loadDocuments("./docs");

EmbeddingStore<TextSegment> store = new RAGExample()
    .createEmbeddingStore(segments);

String answer = new RAGExample().queryWithContext(model, store, query, embeddingModel);
```

## Best Practices

1. **API Key Management**: Use environment variables for API keys
2. **Model Selection**: Choose appropriate models based on task complexity
3. **Temperature Tuning**: Lower values (0.1-0.3) for factual tasks, higher (0.7-1.0) for creative
4. **Token Limits**: Monitor token usage to avoid exceeding limits
5. **Error Handling**: Implement retry logic and fallback strategies
6. **Caching**: Cache frequent queries to reduce API costs

## Further Reading

- [LangChain4j Documentation](https://docs.langchain4j.dev/)
- [LangChain4j Examples](https://github.com/langchain4j/langchain4j-examples)
- [OpenAI API Reference](https://platform.openai.com/docs)