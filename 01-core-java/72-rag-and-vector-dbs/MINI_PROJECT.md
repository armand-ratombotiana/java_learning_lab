# Mini Project: PDF Chatbot with Spring AI and pgvector

## Objective
Build a Spring Boot application that can ingest a PDF document, store its contents in a local PostgreSQL database using the `pgvector` extension, and allow users to ask questions about the document using a Retrieval-Augmented Generation (RAG) approach.

## Prerequisites
*   Docker (to run PostgreSQL with pgvector)
*   OpenAI API Key (for embeddings and chat generation)
*   Java 21 & Spring Boot 3.2+

## Step 1: Setup Infrastructure
Run a PostgreSQL container with the pgvector extension enabled:
```bash
docker run -d \
  --name pgvector \
  -e POSTGRES_USER=myuser \
  -e POSTGRES_PASSWORD=mypassword \
  -e POSTGRES_DB=vectordb \
  -p 5432:5432 \
  pgvector/pgvector:pg16
```

## Step 2: Dependencies
Add the following to your `pom.xml`:
*   `spring-boot-starter-web`
*   `spring-ai-openai-spring-boot-starter`
*   `spring-ai-pgvector-store-spring-boot-starter`
*   `spring-ai-pdf-document-reader`

## Step 3: Configuration
Configure `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/vectordb
    username: myuser
    password: mypassword
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
    vectorstore:
      pgvector:
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        dimensions: 1536
```

## Step 4: Document Ingestion Service
Create a service to load, split, and store the PDF:
```java
@Service
public class IngestionService {
    private final VectorStore vectorStore;

    public IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingestPdf(Resource pdfResource) {
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfResource);
        List<Document> documents = pdfReader.get();
        
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(documents);
        
        vectorStore.accept(chunks);
    }
}
```

## Step 5: Chat Endpoint
Create a REST controller to handle user queries:
```java
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ChatController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    @GetMapping
    public String chat(@RequestParam String message) {
        // 1. Retrieve similar documents
        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(3));
        
        // 2. Construct context
        String context = similarDocuments.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n"));
            
        // 3. Generate response
        String systemPrompt = "You are a helpful assistant. Use the following context to answer the user's question. Context: " + context;
        
        return chatClient.prompt()
            .system(systemPrompt)
            .user(message)
            .call()
            .content();
    }
}
```

## Extension Challenges
1.  **Metadata Filtering**: Add metadata (e.g., author, year) to chunks during ingestion, and modify the `SearchRequest` to filter by that metadata before retrieving.
2.  **Streaming**: Convert the chat endpoint to use `Flux<String>` and stream the response back to the client using Server-Sent Events (SSE).