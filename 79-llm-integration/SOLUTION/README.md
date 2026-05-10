# LLM Integration Patterns - Solution

## Overview

This module provides comprehensive examples for integrating Large Language Models (LLMs) into Java applications. It covers agent creation, memory management, tool calling, chain-of-thought reasoning, RAG integration, caching, and rate limiting.

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
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-memory</artifactId>
    <version>0.35.0</version>
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-tools</artifactId>
    <version>0.35.0</version>
</dependency>
```

## Key Concepts

### 1. LLM Providers

The `LLMProviderExample` class demonstrates:
- OpenAI model configuration
- Anthropic model configuration
- Text generation
- System prompt handling

### 2. Agents

The `AgentExample` class covers:
- Chat agent creation
- Specialized agents (Research, Code)

### 3. Memory Management

The `MemoryExample` class provides:
- Message window memory
- In-memory chat memory
- Memory clearing

### 4. Tool Calling

The `ToolCallingExample` class implements:
- Calculator tool
- Temperature converter
- Dynamic tool binding

### 5. Chain of Thought

The `ChainOfThoughtExample` class demonstrates:
- Step-by-step reasoning
- Few-shot prompting

### 6. RAG Integration

The `RAGIntegrationExample` class covers:
- Context retrieval
- RAG assistant creation

### 7. Caching

The `CachingExample` class provides:
- Response caching
- Cache management

### 8. Rate Limiting

The `RateLimitingExample` class implements:
- Request rate limiting
- Time-based resets

## Classes Overview

| Class | Description |
|-------|-------------|
| `LLMProviderExample` | LLM provider configuration |
| `AgentExample` | Agent creation and management |
| `MemoryExample` | Conversation memory |
| `ToolCallingExample` | Tool integration |
| `ChainOfThoughtExample` | Reasoning patterns |
| `RAGIntegrationExample` | RAG implementation |
| `CachingExample` | Response caching |
| `RateLimitingExample` | Rate limit handling |

## Running Tests

```bash
cd 79-llm-integration
mvn test -Dtest=Test
```

## Examples

### Creating an Agent

```java
Assistant agent = AiServices.builder(Assistant.class)
    .chatLanguageModel(model)
    .build();

String response = agent.chat("Hello!");
```

### Tool Calling

```java
AssistantWithCalculator agent = AiServices.builder(AssistantWithCalculator.class)
    .chatLanguageModel(model)
    .tools(calculator)
    .build();

String result = agent.ask("What is 5 + 5?");
```

### Caching

```java
String response = chatWithCache(model, prompt);
```

## Best Practices

1. **API Keys**: Use environment variables for sensitive data
2. **Model Selection**: Choose appropriate models based on task
3. **Temperature Tuning**: Adjust for creativity vs. accuracy
4. **Token Limits**: Monitor usage to avoid exceeding limits
5. **Error Handling**: Implement proper exception handling
6. **Caching**: Cache repeated queries for efficiency

## Further Reading

- [LangChain4j Documentation](https://docs.langchain4j.dev/)
- [LangChain4j GitHub](https://github.com/langchain4j/langchain4j)
- [LLM Best Practices](https://platform.openai.com/docs/guides/text-generation)