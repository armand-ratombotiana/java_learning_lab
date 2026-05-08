# LangChain4j Exercises

## Exercise 1: Basic Chat
Create a simple chat application:
- Connect to OpenAI GPT-4
- Send messages and receive responses
- Handle API errors gracefully

## Exercise 2: Prompt Templates
Build dynamic prompts:
- Create template with variables
- Apply template with user input
- Handle missing variables

## Exercise 3: Chat Memory
Implement conversation memory:
- Window-based memory (last N messages)
- Token-based memory management
- Persistent storage with database

## Exercise 4: Document Q&A
Build a document Q&A system:
- Parse PDF documents
- Split into chunks
- Create embeddings and store in vector DB
- Implement retrieval and answer generation

## Exercise 5: RAG Pipeline
Build complete RAG system:
- Load and preprocess documents
- Generate embeddings with BGE model
- Store in embedding store
- Implement similarity search

## Exercise 6: AI Service
Create declarative AI service:
- Define interface with annotations
- Use @SystemMessage for behavior
- Implement with AiServices.builder()

## Exercise 7: Streaming Response
Implement streaming:
- Handle token-by-token responses
- Display progressively
- Manage backpressure