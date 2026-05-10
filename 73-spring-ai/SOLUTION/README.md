# Spring AI - Solution

## Overview

This module provides comprehensive examples for building AI-powered applications using Spring AI. It covers ChatClient, vector stores, RAG (Retrieval Augmented Generation), and function calling.

## Dependencies

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai</artifactId>
    <version>0.8.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-pinecone</artifactId>
    <version>0.8.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-milvus</artifactId>
    <version>0.8.1</version>
</dependency>
```

## Key Concepts

### 1. ChatClient

The `ChatClientExample` class demonstrates:
- Simple chat interactions
- System prompt configuration
- Memory-backed conversations
- Response handling

```java
ChatClient client = ChatClient.builder(chatModel).build();
String response = client.prompt("Hello").call().content();
```

### 2. Prompt Templates

The `PromptTemplateExample` class covers:
- Dynamic prompt generation
- Variable substitution
- Structured prompts for specific tasks

### 3. Vector Stores

The `VectorStoreExample` class provides:
- Pinecone vector store integration
- Milvus vector store integration
- Chroma vector store integration
- Document addition and similarity search

### 4. RAG (Retrieval Augmented Generation)

The `RAGExample` class implements:
- Context-aware question answering
- Filtered vector search
- Metadata-enabled document storage

### 5. Chat Memory

The `ChatMemoryExample` class demonstrates:
- In-memory chat memory
- Vector store-backed memory

### 6. Function Calling

The `FunctionCallingExample` class covers:
- Dynamic function binding
- Tool execution through chat

## Classes Overview

| Class | Description |
|-------|-------------|
| `ChatClientExample` | ChatClient configuration and usage |
| `PromptTemplateExample` | Dynamic prompt generation |
| `VectorStoreExample` | Vector store integrations |
| `RAGExample` | RAG implementation patterns |
| `ChatMemoryExample` | Memory management |
| `StreamingExample` | Streaming response handling |
| `MultiModalExample` | Image and audio processing |

## Running Tests

```bash
cd 73-spring-ai
mvn test -Dtest=Test
```

## Examples

### Creating a ChatClient

```java
ChatClient client = new ChatClientExample()
    .createChatClient(chatModel);

String response = client.prompt("Hello").call().content();
```

### Vector Store Search

```java
VectorStoreExample vectorStore = new VectorStoreExample();
vectorStore.addDocuments(store, List.of("Document 1", "Document 2"));

List<Document> results = vectorStore.searchSimilar(store, "query", 5);
```

### RAG Query

```java
String answer = new RAGExample()
    .queryWithContext(client, vectorStore, "What is Spring AI?");
```

## Best Practices

1. **Environment Variables**: Use environment variables for API keys
2. **Model Selection**: Choose appropriate models based on requirements
3. **Vector Store**: Select appropriate vector store based on scale and features
4. **Memory Management**: Use appropriate memory strategy for conversation length
5. **Error Handling**: Implement proper error handling for API failures

## Further Reading

- [Spring AI Documentation](https://docs.spring.io/spring-ai/)
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai)
- [Vector Store Reference](https://docs.spring.io/spring-ai/reference/api/vectore stores.html)