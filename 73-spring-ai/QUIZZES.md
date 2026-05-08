# Spring AI Quizzes - Assessment and Knowledge Check

## Quiz Overview

This quiz assesses your understanding of Spring AI concepts, implementation patterns, and best practices. Questions are organized by difficulty level.

---

## Easy Questions (1-10)

### Question 1

What is the primary purpose of Spring AI?

A) To replace Spring MVC
B) To simplify AI integration in Spring applications
C) To provide database connectivity
D) To enable GUI development

**Correct Answer**: B

**Explanation**: Spring AI provides a unified abstraction layer for integrating AI capabilities (LLMs, embeddings, vector databases) into Spring applications, following Spring's idiomatic patterns.

---

### Question 2

Which interface is the primary entry point for LLM interactions in Spring AI?

A) EmbeddingClient
B) ChatClient
C) VectorStore
D) ImageClient

**Correct Answer**: B

**Explanation**: The `ChatClient` interface is the primary interface for sending prompts to LLMs and receiving responses. It provides a fluent API for building and executing prompts.

---

### Question 3

In Spring AI, what is the purpose of the EmbeddingClient?

A) To manage conversation history
B) To convert text into numerical vectors
C) To store documents in databases
D) To handle HTTP requests

**Correct Answer**: B

**Explanation**: `EmbeddingClient` converts text into numerical vector representations (embeddings), which are essential for semantic similarity search and RAG implementations.

---

### Question 4

What annotation is typically used to inject ChatClient in Spring AI?

A) @Autowired
B) @Inject
C) Both @Autowired and constructor injection work
D) @Resource

**Correct Answer**: C

**Explanation**: ChatClient can be injected using standard Spring injection patterns. Constructor injection is generally preferred for testability.

---

### Question 5

Which file is used to configure Spring AI properties in a Spring Boot application?

A) application.properties or application.yaml
B) ai.properties
C) spring-ai-config.xml
D) beans.xml

**Correct Answer**: A

**Explanation**: Spring AI uses standard Spring Boot configuration via application.properties or application.yaml under the `spring.ai` namespace.

---

### Question 6

What is a VectorStore in Spring AI?

A) A database for storing chat messages
B) A storage system for embeddings with similarity search capability
C) A cache for API responses
D) A message queue

**Correct Answer**: B

**Explanation**: VectorStore is an abstraction for storing embeddings and performing similarity searches, enabling retrieval-augmented generation (RAG) implementations.

---

### Question 7

Which of these is a supported LLM provider in Spring AI?

A) Only OpenAI
B) OpenAI, Anthropic, Google, and Ollama
C) Only Google
D) Only Ollama

**Correct Answer**: B

**Explanation**: Spring AI supports multiple providers including OpenAI, Anthropic (Claude), Google (Gemini), and Ollama (local models). Additional providers continue to be added.

---

### Question 8

What is the purpose of PromptTemplate in Spring AI?

A) To create database queries
B) To build dynamic prompts with variable substitution
C) To format JSON responses
D) To manage API rate limits

**Correct Answer**: B

**Explanation**: PromptTemplate allows dynamic prompt construction with placeholders that can be filled at runtime, enabling reusable prompt patterns.

---

### Question 9

In Spring AI, how do you configure the model temperature?

A) Through environment variable only
B) Through @Prompt annotation
C) Through ChatOptions at runtime or configuration
D) Temperature cannot be configured

**Correct Answer**: C

**Explanation**: Temperature can be configured via ChatOptions either programmatically or through configuration files, controlling response randomness.

---

### Question 10

What does RAG stand for in the context of AI applications?

A) Real Application Generation
B) Retrieval-Augmented Generation
C) Random Access Gateway
D) Resource Allocation Group

**Correct Answer**: B

**Explanation**: RAG (Retrieval-Augmented Generation) combines document retrieval with LLM generation to provide context-aware responses grounded in specific documents.

---

## Medium Questions (11-20)

### Question 11

When implementing RAG with Spring AI, what is the typical flow?

A) User → LLM → VectorStore → Response
B) User → EmbeddingClient → VectorStore → LLM → Response
C) User → VectorStore → EmbeddingClient → Response
D) User → API → LLM → Response

**Correct Answer**: B

**Explanation**: RAG flow: Convert user query to embedding → Search vector store for relevant documents → Combine context with query → Send to LLM → Return response.

---

### Question 12

What is the purpose of function calling in Spring AI?

A) To call external REST APIs
B) To extend LLM capabilities with custom tools/functions
C) To handle database transactions
D) To manage concurrent requests

**Correct Answer**: B

**Explanation**: Function calling allows LLMs to invoke custom Java methods (tools) to perform actions like database lookups, calculations, or API calls, extending their capabilities beyond text generation.

---

### Question 13

How do you switch between different AI providers in Spring AI?

A) Rewrite all code for each provider
B) Change configuration and inject different ChatClient bean
C) Use runtime provider detection
D) Providers cannot be switched

**Correct Answer**: B

**Explanation**: Spring AI provides provider abstraction. By configuring different provider properties and injecting the appropriate ChatClient bean, you can switch providers without code changes.

---

### Question 14

What is the recommended way to handle API keys in Spring AI?

A) Hardcode in source code
B) Store in version control
C) Use environment variables or Spring's secure property management
D) Store in plain text file

**Correct Answer**: C

**Explanation**: API keys should be managed through environment variables or secure property management (like Spring Vault or encrypted properties), never stored in code or version control.

---

### Question 15

In Spring AI, what does the SearchRequest class represent?

A) HTTP search request
B) Query parameters for vector similarity search
C) Database query
D) File search parameters

**Correct Answer**: B

**Explanation**: SearchRequest is used to configure vector similarity searches, including the query text, number of results (topK), and similarity threshold.

---

### Question 16

What is the purpose of ResponseConverter in Spring AI?

A) To convert images to text
B) To transform LLM responses into specific Java types
C) To convert between different file formats
D) To handle HTTP response conversion

**Correct Answer**: B

**Explanation**: ResponseConverter transforms raw LLM responses into specific Java types, enabling structured output parsing for POJOs and records.

---

### Question 17

How does Spring AI integrate with Spring Boot Actuator?

A) It doesn't integrate
B) Through metrics and health endpoints for AI operations
C) Through JMX only
D) Through custom endpoints only

**Correct Answer**: B

**Explanation**: Spring AI integrates with Spring Boot Actuator to provide observability through metrics, health checks, and monitoring of AI operations.

---

### Question 18

What is the purpose of @Tool annotation in Spring AI function calling?

A) To define REST endpoints
B) To mark methods as callable by the LLM
C) To create database operations
D) To handle exceptions

**Correct Answer**: B

**Explanation**: The @Tool annotation marks Java methods as available functions that the LLM can call during response generation, enabling tool use.

---

### Question 19

What is a key advantage of using Spring AI over direct API clients?

A) Slower performance
B) Provider abstraction and Spring ecosystem integration
C) More complex configuration
D) Limited features

**Correct Answer**: B

**Explanation**: Spring AI provides provider abstraction, standardized patterns, Spring ecosystem integration (Boot, MVC, Test), and production-ready features like observability.

---

### Question 20

When would you use InMemoryVectorStore in Spring AI?

A) For production systems
B) For development and testing only
C) For high-volume processing
D) For distributed systems

**Correct Answer**: B

**Explanation**: InMemoryVectorStore is suitable only for development and testing. Production systems should use persistent vector databases (Pinecone, Weaviate, etc.) for data durability and scalability.

---

## Advanced Questions (21-30)

### Question 21

What patterns would you use to implement retry logic for AI API calls in Spring AI?

A) Manual try-catch only
B) Spring Retry with @Retryable annotation
C) Custom thread.sleep loops
D) No retry is needed

**Correct Answer**: B

**Explanation**: Spring Retry can be integrated with ChatClient to implement retry logic for transient failures, network issues, and rate limiting.

---

### Question 22

How would you implement custom document loading for unsupported file types?

A) Cannot be done
B) Implement DocumentLoader interface
C) Use standard Java file I/O only
D) Convert to text externally

**Correct Answer**: B

**Explanation**: Spring AI's DocumentLoader interface can be implemented to support custom file types or data sources, enabling flexible document ingestion.

---

### Question 23

What considerations are important when choosing a vector database for production?

A) Only cost
B) Scalability, latency, filtering capabilities, and ecosystem
C) Only vendor reputation
D) Only open-source license

**Correct Answer**: B

**Explanation**: Production vector database selection involves evaluating: scalability for data growth, query latency, filtering capabilities, integration ecosystem, and operational requirements.

---

### Question 24

How do you implement pagination for large document ingestion in Spring AI?

A) Not supported
B) Process in batches with checkpointing
C) Load all at once
D) Use database pagination only

**Correct Answer**: B

**Explanation**: Large document sets should be processed in batches with checkpointing to handle failures, manage memory, and provide progress tracking.

---

### Question 25

What is the purpose of a Circuit Breaker pattern in AI service integration?

A) To improve response speed
B) To prevent cascade failures when AI services are unavailable
C) To cache responses
D) To encrypt data

**Correct Answer**: B

**Explanation**: Circuit breaker prevents cascade failures by failing fast when AI services are unavailable, allowing the system to degrade gracefully and recover.

---

### Question 26

How would you implement hybrid search (keyword + vector) in Spring AI?

A) Use only vector search
B) Combine results from keyword search and vector search
C) Keyword search is not supported
D) Use random selection

**Correct Answer**: B

**Explanation**: Hybrid search combines keyword-based results (BM25) with vector similarity to improve relevance, especially for queries requiring exact term matching.

---

### Question 27

What metrics are important to track for AI service monitoring?

A) Only response time
B) Token usage, cost, latency, error rates, and cache hit rates
C) Only error rates
D) No metrics needed

**Correct Answer**: B

**Explanation**: Comprehensive AI service monitoring tracks: token consumption (cost), latency (performance), error rates (reliability), cache hit rates (optimization), and provider health.

---

### Question 28

How do you handle context window limitations in LLM interactions?

A) Ignore the limitation
B) Implement document chunking and selective context inclusion
C) Always use maximum context
D) Use smaller models only

**Correct Answer**: B

**Explanation**: Context window limitations require strategic chunking of documents, prioritizing relevant context, and potentially using summary-based approaches for long conversations.

---

### Question 29

What is the purpose of message converters in Spring AI?

A) To handle file uploads
B) To transform between different message formats and types
C) To encrypt messages
D) To compress message size

**Correct Answer**: B

**Explanation**: Message converters handle transformation of messages between different formats, enabling flexible input/output handling in AI interactions.

---

### Question 30

How would you implement security scanning for AI-generated content?

A) Not necessary
B) Implement output validation, content filtering, and sanitize user inputs
C) Trust all LLM outputs
D) Use only whitelisted responses

**Correct Answer**: B

**Explanation**: AI-generated content should be validated for safety, filtered for inappropriate content, and user inputs should be sanitized to prevent prompt injection attacks.

---

## Answer Key Summary

| Question | Answer | Difficulty |
|----------|--------|------------|
| 1 | B | Easy |
| 2 | B | Easy |
| 3 | B | Easy |
| 4 | C | Easy |
| 5 | A | Easy |
| 6 | B | Easy |
| 7 | B | Easy |
| 8 | B | Easy |
| 9 | C | Easy |
| 10 | B | Easy |
| 11 | B | Medium |
| 12 | B | Medium |
| 13 | B | Medium |
| 14 | C | Medium |
| 15 | B | Medium |
| 16 | B | Medium |
| 17 | B | Medium |
| 18 | B | Medium |
| 19 | B | Medium |
| 20 | B | Medium |
| 21 | B | Advanced |
| 22 | B | Advanced |
| 23 | B | Advanced |
| 24 | B | Advanced |
| 25 | B | Advanced |
| 26 | B | Advanced |
| 27 | B | Advanced |
| 28 | B | Advanced |
| 29 | B | Advanced |
| 30 | B | Advanced |

---

## Scoring Guide

- **Easy (1-10)**: 8+ correct - Foundation understanding
- **Medium (11-20)**: 7+ correct - Practical knowledge
- **Advanced (21-30)**: 7+ correct - Production expertise

**Overall Score Interpretation**:
- 22-30: Expert level
- 15-21: Proficient
- 10-14: Intermediate
- Below 10: Review fundamentals

---

*Continue to EXERCISES.md for hands-on practice and EDGE_CASES.md for debugging guidance.*