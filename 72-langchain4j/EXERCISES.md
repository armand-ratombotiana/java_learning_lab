# LangChain4j Exercises - Hands-On Learning

## Exercise Overview

This exercise set provides progressive hands-on challenges to master LangChain4j. Each exercise builds upon concepts from previous ones, starting from basic implementations to production-ready features.

---

## Level 1: Foundations (Exercises 1-3)

### Exercise 1: Basic Chat Application (30 minutes)

**Objective**: Build a simple console-based chat application using LangChain4j

**Requirements**:
1. Create a Maven project with LangChain4j dependencies
2. Configure OpenAI ChatModel with API key from environment
3. Implement basic chat functionality that sends user input and displays responses
4. Add error handling for API failures
5. Create a loop that allows multiple exchanges

**Starter Code**:
```java
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class SimpleChat {
    public static void main(String[] args) {
        // TODO: Initialize ChatLanguageModel
        // TODO: Create chat loop
        // TODO: Handle user input and display responses
    }
}
```

**Success Criteria**:
- Application connects to OpenAI API successfully
- User can type messages and receive responses
- Application handles invalid API keys gracefully
- Application can be terminated with "quit" command

**Solution Hint**:
```java
// Step 1: Create the model
ChatLanguageModel model = OpenAiChatModel.builder()
    .apiKey(System.getenv("OPENAI_API_KEY"))
    .temperature(0.7)
    .build();

// Step 2: Chat loop
try (Scanner scanner = new Scanner(System.in)) {
    while (true) {
        System.out.print("You: ");
        String input = scanner.nextLine();
        if ("quit".equalsIgnoreCase(input)) break;
        
        String response = model.chat(input);
        System.out.println("Bot: " + response);
    }
}
```

---

### Exercise 2: Prompt Templates (30 minutes)

**Objective**: Implement dynamic prompt templates with variable substitution

**Requirements**:
1. Create a translation service using prompt templates
2. Support multiple languages (English, Spanish, French, German)
3. Allow customization of tone (formal, casual, technical)
4. Handle missing template variables gracefully

**Starter Code**:
```java
public interface TranslationService {
    @SystemMessage("You are a professional translator")
    @UserMessage("Translate the following {{sourceLanguage}} text to {{targetLanguage}}: {{text}}")
    String translate(String text, String sourceLanguage, String targetLanguage);
}
```

**Success Criteria**:
- Successfully translates text between supported languages
- Applies correct tone based on parameter
- Returns error message for unsupported language pairs

**Solution Hint**: Use `@SystemMessage` and `@UserMessage` annotations with template variables. Create the service using `AiServices.builder(TranslationService.class)`.

---

### Exercise 3: Chat Memory (45 minutes)

**Objective**: Implement conversation memory to maintain context across messages

**Requirements**:
1. Add in-memory chat memory to the basic chat application
2. Limit memory to last 10 messages
3. Persist memory to file (JSON format) between sessions
4. Load previous conversation history on startup
5. Add a "clear" command to reset memory

**Starter Code**:
```java
import dev.langchain4j.service.chatmemory.ChatMemory;
import dev.langchain4j.service.chat_memory.InMemoryChatMemory;

public class ChatWithMemory {
    // TODO: Implement chat memory with persistence
}
```

**Success Criteria**:
- Bot remembers previous messages within session
- Memory persists across application restarts
- Clear command successfully resets memory
- Memory doesn't grow unbounded

**Solution Hint**:
```java
// Create bounded memory
ChatMemory chatMemory = InMemoryChatMemory.builder()
    .maxMessages(10)
    .build();

// Build service with memory
Assistant assistant = AiServices.builder(Assistant.class)
    .chatLanguageModel(model)
    .chatMemory(chatMemory)
    .build();
```

---

## Level 2: Intermediate (Exercises 4-6)

### Exercise 4: Document Q&A System (60 minutes)

**Objective**: Build a system that can answer questions about documents

**Requirements**:
1. Load text documents from a directory
2. Split documents into chunks (500 characters, 50 overlap)
3. Create embeddings using local embedding model (AllMiniLmL6V2)
4. Store embeddings in in-memory vector store
5. Implement question-answering using similarity search

**Starter Code**:
```java
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;

public class DocumentQA {
    // TODO: Load, chunk, embed, and search documents
}
```

**Success Criteria**:
- Successfully loads and processes multiple documents
- Returns relevant context for questions
- Handles documents of varying lengths
- Shows retrieval scores for debugging

**Solution Hint**:
```java
// Load documents
List<Document> documents = FileSystemDocumentLoader
    .loadDocuments(Path.of("documents"));

// Split into chunks
DocumentSplitter splitter = DocumentSplitters.byParagraph(500, 50);
List<TextSegment> segments = splitter.split(documents);

// Create embeddings
EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
List<Embedding> embeddings = embeddingModel.embedAll(
    segments.stream().map(TextSegment::text).collect(Collectors.toList())
).content();

// Store and query
EmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
for (int i = 0; i < embeddings.size(); i++) {
    store.add(embeddings.get(i), segments.get(i));
}
```

---

### Exercise 5: Complete RAG Pipeline (90 minutes)

**Objective**: Implement full Retrieval-Augmented Generation with multiple document types

**Requirements**:
1. Support PDF and text documents
2. Use OpenAI for both embeddings and chat
3. Implement hybrid retrieval (keyword + vector)
4. Add source citation in responses
5. Cache embeddings to reduce API calls
6. Add minimum relevance threshold filtering

**Starter Code**:
```java
public class ProductionRAG {
    // TODO: Implement complete RAG with PDF support
}
```

**Success Criteria**:
- Processes PDF and text files correctly
- Returns answers with source citations
- Only returns results above relevance threshold
- Implements caching for embeddings

**Solution Hint**: Use `PdfDocumentParser` for PDFs, `TextDocumentParser` for text. Build a `RetrievalAugmentor` with custom content retriever that filters by score.

---

### Exercise 6: AI Service with Tools (60 minutes)

**Objective**: Create an AI service that can use external tools

**Requirements**:
1. Create a calculator tool with basic operations (+, -, *, /)
2. Create a weather lookup tool (mock implementation)
3. Register tools with the AI service
4. Ensure the LLM correctly decides when to call tools
5. Handle tool execution errors gracefully

**Starter Code**:
```java
class Calculator {
    @Tool("Performs basic arithmetic operations")
    public double calculate(String expression) {
        // Implement basic calculator
    }
}
```

**Success Criteria**:
- LLM correctly identifies when tool use is needed
- Calculator performs correct arithmetic
- Errors in tool execution are handled gracefully
- Results are incorporated into final response

**Solution Hint**:
```java
// Register tools when building service
AssistantWithTools assistant = AiServices.builder(AssistantWithTools.class)
    .chatLanguageModel(model)
    .tools(new Calculator(), new WeatherService())
    .build();
```

---

## Level 3: Advanced (Exercises 7-10)

### Exercise 7: Streaming Response Handler (45 minutes)

**Objective**: Implement streaming responses for real-time feedback

**Requirements**:
1. Convert to StreamingChatLanguageModel
2. Implement token-by-token response handling
3. Add progress indicator during processing
4. Handle stream completion and errors
5. Implement backpressure handling

**Starter Code**:
```java
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

public class StreamingChat {
    // TODO: Implement streaming response handling
}
```

**Success Criteria**:
- Responses appear incrementally (not all at once)
- Progress indicator shows during generation
- Stream handles errors without crashing
- Backpressure prevents memory issues

**Solution Hint**:
```java
StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
    .apiKey(System.getenv("OPENAI_API_KEY"))
    .build();

model.chat("Write a story", response -> {
    System.out.print(response.token().text());
    System.out.flush();
});
```

---

### Exercise 8: Structured Output Parser (60 minutes)

**Objective**: Generate type-safe structured responses

**Requirements**:
1. Define Java records for structured output (e.g., ProductReview)
2. Create an AI service that returns typed responses
3. Handle parsing errors when model returns invalid format
4. Add validation to ensure output meets requirements
5. Implement retry logic for malformed outputs

**Starter Code**:
```java
record ProductReview(
    String productName,
    int rating,
    String sentiment,
    List<String> pros,
    List<String> cons
) {}

interface ReviewAnalyzer {
    @UserMessage("Analyze this review: {{review}}")
    ProductReview analyze(String review);
}
```

**Success Criteria**:
- Returns properly typed Java records
- Handles JSON parsing errors gracefully
- Validates output against business rules
- Retries on malformed responses

**Solution Hint**: Use AiServices with record return types. Implement custom exception handling for parsing failures.

---

### Exercise 9: Multi-Modal Content Processing (90 minutes)

**Objective**: Build a system that processes multiple content types

**Requirements**:
1. Process HTML documents (extract text and metadata)
2. Process JSON data files
3. Process CSV data with column awareness
4. Combine all processed content into unified embedding store
5. Implement content-type-aware retrieval

**Starter Code**:
```java
public class MultiModalProcessor {
    // TODO: Handle multiple content types
}
```

**Success Criteria**:
- Correctly extracts text from HTML, JSON, and CSV
- Preserves metadata from different formats
- Unified search across all content types
- Returns content with source type information

---

### Exercise 10: Production Deployment Configuration (60 minutes)

**Objective**: Configure a LangChain4j application for production

**Requirements**:
1. Implement environment-based configuration
2. Add health check endpoint
3. Configure metrics collection (token usage, latency)
4. Implement graceful shutdown
5. Add circuit breaker pattern
6. Configure log levels appropriately

**Starter Code**:
```java
public class ProductionConfig {
    // TODO: Implement production-ready configuration
}
```

**Success Criteria**:
- Application reads config from environment variables
- Health endpoint returns status
- Metrics are logged/exported
- Shutdown waits for in-flight requests
- Circuit breaker prevents cascade failures

---

## Additional Challenges

### Challenge A: Build a Chatbot with Personality

Create a chatbot with:
- Defined personality traits
- Custom system prompt
- Conversation flow management
- Topic detection and routing

### Challenge B: Implement Rate Limiting

Build a rate limiter that:
- Tracks requests per minute
- Implements token bucket algorithm
- Returns 429 status when limit exceeded
- Queues excess requests

### Challenge C: Create a Knowledge Graph RAG

Implement RAG that:
- Extracts entities from documents
- Builds relationships between entities
- Queries knowledge graph for retrieval
- Combines with vector search

---

## Exercise Completion Checklist

| Exercise | Topic | Difficulty | Completed |
|----------|-------|------------|-----------|
| 1 | Basic Chat | Easy | [ ] |
| 2 | Prompt Templates | Easy | [ ] |
| 3 | Chat Memory | Easy | [ ] |
| 4 | Document Q&A | Medium | [ ] |
| 5 | Complete RAG | Medium | [ ] |
| 6 | AI Tools | Medium | [ ] |
| 7 | Streaming | Hard | [ ] |
| 8 | Structured Output | Hard | [ ] |
| 9 | Multi-Modal | Hard | [ ] |
| 10 | Production Config | Hard | [ ] |

---

## References

- LangChain4j Documentation: https://docs.langchain4j.dev/
- OpenAI API: https://platform.openai.com/docs
- Spring AI Integration: https://docs.spring.io/spring-ai/

---

*Complete all exercises to build production-ready LangChain4j applications. Refer to DEEP_DIVE.md for conceptual understanding and EDGE_CASES.md for debugging help.*