# Spring AI Exercises

## Exercise 1: Chat Client Setup
Configure Spring AI with OpenAI:
- Add spring-ai-openai dependency
- Configure API key in application.yml
- Create ChatService with ChatClient

## Exercise 2: Structured Output
Extract structured data from AI:
- Define POJO for response
- Use entity() method for parsing
- Handle parsing errors

## Exercise 3: Embedding Integration
Implement text embeddings:
- Configure embedding model
- Generate embeddings for text
- Understand embedding dimensions

## Exercise 4: Vector Store Setup
Set up vector database:
- Configure InMemoryVectorStore
- Implement similarity search
- Store and retrieve documents

## Exercise 5: Document Processing
Build document pipeline:
- Use DocumentReader for PDFs
- Apply DocumentTransformer
- Store in VectorStore

## Exercise 6: RAG Implementation
Create complete RAG system:
- Use QuestionAnswerAdvisor
- Implement PDF Q&A
- Configure retrieval parameters

## Exercise 7: Multi-Provider
Support multiple LLM providers:
- Configure Ollama
- Switch between providers
- Handle provider-specific features