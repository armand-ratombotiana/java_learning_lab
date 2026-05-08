# Spring AI Exercises - Hands-On Learning

## Exercise Overview

This exercise set provides progressive challenges to master Spring AI integration in Spring Boot applications.

---

## Level 1: Foundations (Exercises 1-3)

### Exercise 1: Basic Chat Application (30 minutes)

**Objective**: Create a Spring Boot application with basic chat functionality

**Requirements**:
1. Set up Spring Boot project with Spring AI dependencies
2. Configure OpenAI API key in application.yaml
3. Create a REST endpoint that accepts user messages
4. Return AI responses via REST API
5. Add error handling for API failures

**Success Criteria**:
- Application starts without errors
- POST /chat returns response from AI
- Invalid requests return appropriate error messages

---

### Exercise 2: Prompt Templates (30 minutes)

**Objective**: Implement dynamic prompts with templates

**Requirements**:
1. Create a translation service using Spring AI
2. Support multiple target languages
3. Implement prompt templates with variable substitution
4. Add system messages for consistent behavior

**Success Criteria**:
- Translation endpoint returns correct translations
- System message is applied to all requests

---

### Exercise 3: Embedding Client (45 minutes)

**Objective**: Implement text embedding functionality

**Requirements**:
1. Configure embedding client in application
2. Create endpoint to generate embeddings
3. Implement batch embedding for multiple texts
4. Return embedding vectors as JSON

**Success Criteria**:
- Single text generates correct embedding vector
- Batch endpoint handles multiple texts
- Embeddings are returned in response

---

## Level 2: Intermediate (Exercises 4-6)

### Exercise 4: Vector Store Implementation (60 minutes)

**Objective**: Implement document storage with similarity search

**Requirements**:
1. Configure InMemoryVectorStore
2. Create document ingestion endpoint
3. Implement similarity search endpoint
4. Add document chunking

**Success Criteria**:
- Documents can be added to vector store
- Similarity search returns relevant documents
- Chunking is applied to large documents

---

### Exercise 5: Complete RAG Pipeline (90 minutes)

**Objective**: Build full retrieval-augmented generation

**Requirements**:
1. Create document loading from filesystem
2. Implement chunking and embedding
3. Build similarity-based retrieval
4. Combine retrieval with LLM generation
5. Return answers with source references

**Success Criteria**:
- Documents are loaded and indexed
- Questions return contextually relevant answers
- Sources are cited in responses

---

### Exercise 6: Multi-Provider Support (60 minutes)

**Objective**: Support multiple AI providers

**Requirements**:
1. Configure OpenAI and Ollama providers
2. Create provider selection mechanism
3. Implement fallback between providers
4. Add provider-specific configurations

**Success Criteria**:
- Switch between providers via configuration
- Fallback works when primary fails

---

## Level 3: Advanced (Exercises 7-10)

### Exercise 7: Function Calling (60 minutes)

**Objective**: Implement custom functions callable by AI

**Requirements**:
1. Create custom @Tool methods
2. Register functions with ChatClient
3. Implement weather lookup function
4. Handle function execution results

**Success Criteria**:
- AI correctly decides when to call functions
- Function results are incorporated in response

---

### Exercise 8: Structured Output (60 minutes)

**Objective**: Generate type-safe structured responses

**Requirements**:
1. Define POJO for structured response
2. Configure response converter
3. Handle parsing errors gracefully
4. Add validation to outputs

**Success Criteria**:
- Responses are parsed into POJO
- Malformed responses are handled gracefully

---

### Exercise 9: Production Configuration (90 minutes)

**Objective**: Configure for production deployment

**Requirements**:
1. Implement environment-based configuration
2. Add health check endpoint
3. Configure metrics collection
4. Implement circuit breaker
5. Add proper logging

**Success Criteria**:
- Application works in multiple environments
- Health endpoint returns status
- Metrics are collected

---

### Exercise 10: Performance Optimization (60 minutes)

**Objective**: Optimize AI service performance

**Requirements**:
1. Implement response caching
2. Add connection pooling
3. Configure rate limiting
4. Optimize batch operations

**Success Criteria**:
- Repeated queries are cached
- Connection pooling is configured
- Rate limiting prevents service overload

---

## Completion Checklist

| Exercise | Difficulty | Completed |
|----------|------------|-----------|
| 1 | Easy | [ ] |
| 2 | Easy | [ ] |
| 3 | Easy | [ ] |
| 4 | Medium | [ ] |
| 5 | Medium | [ ] |
| 6 | Medium | [ ] |
| 7 | Hard | [ ] |
| 8 | Hard | [ ] |
| 9 | Hard | [ ] |
| 10 | Hard | [ ] |

---

*Complete exercises to master Spring AI integration. See DEEP_DIVE.md for concepts and QUIZZES.md for assessment.*