# Spring AI Projects - Module 73

This module covers Spring AI framework, multi-model support, and vector stores for building intelligent Java applications.

## Mini-Project: Multi-Model AI Chat Service (2-4 hours)

### Overview
Build a Java application using Spring AI to create a flexible chat service supporting multiple LLM providers (OpenAI, Anthropic, Azure OpenAI). This project demonstrates Spring AI's model abstraction, chat client, and structured output capabilities.

### Project Structure
```
spring-ai-mini/
├── pom.xml
├── src/main/java/com/learning/springai/
│   ├── MiniProjectApplication.java
│   ├── config/
│   │   ├── AiConfig.java
│   │   └── ModelConfig.java
│   ├── service/
│   │   ├── ChatService.java
│   │   ├── MultiModelService.java
│   │   └── StructuredOutputService.java
│   ├── controller/
│   │   └── ChatController.java
│   └── dto/
│       └── ChatRequest.java
└── src/main/resources/
    └── application.yml
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>spring-ai-mini</artifactId>
    <version>1.0.0</version>
    <name>Spring AI Mini Project</name>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <spring-ai.version>1.0.0-M2</spring-ai.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
            <version>${spring-ai.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
    
    <repositories>
        <repository>
            <id>spring-milestones</id>
            <url>https://repo.spring.io/milestone</url>
        </repository>
    </repositories>
</project>
```

#### AI Configuration
```java
package com.learning.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    
    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;
    
    @Bean
    public ChatModel chatModel() {
        return OpenAiChatModel.builder()
            .apiKey(apiKey)
            .defaultOptions(OpenAiChatOptions.builder()
                .model("gpt-3.5-turbo")
                .temperature(0.7)
                .maxTokens(1000)
                .build())
            .build();
    }
    
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem("You are a helpful AI assistant.")
            .build();
    }
    
    @Bean
    public ChatClient structuredOutputClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem("You are a data extraction assistant. Always respond in valid JSON.")
            .build();
    }
}
```

#### Chat Service
```java
package com.learning.springai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatService {
    
    private final ChatClient chatClient;
    
    public ChatService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }
    
    public String chat(String message) {
        return chatClient.prompt()
            .user(message)
            .call()
            .content();
    }
    
    public String chatWithSystem(String userMessage, String systemMessage) {
        return chatClient.prompt()
            .system(systemMessage)
            .user(userMessage)
            .call()
            .content();
    }
    
    public Flux<String> chatStream(String message) {
        return chatClient.prompt()
            .user(message)
            .stream()
            .content();
    }
    
    public String generateWithOptions(String message, double temperature, int maxTokens) {
        return chatClient.prompt()
            .user(message)
            .options(o -> o
                .temperature(temperature)
                .maxTokens(maxTokens))
            .call()
            .content();
    }
}
```

#### Multi-Model Service
```java
package com.learning.springai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MultiModelService {
    
    private final Map<String, ChatClient> modelClients;
    private final ChatClient defaultClient;
    
    public MultiModelService() {
        this.modelClients = new ConcurrentHashMap<>();
        this.defaultClient = ChatClient.builder().build();
    }
    
    public String chatWithModel(String model, String message) {
        ChatClient client = modelClients.getOrDefault(model, defaultClient);
        
        return client.prompt()
            .user(message)
            .call()
            .content();
    }
    
    public String chatWithOpenAI(String message) {
        return chatWithModel("openai", message);
    }
    
    public String chatWithAnthropic(String message) {
        return chatWithModel("anthropic", message);
    }
    
    public String generateCreative(String message) {
        return defaultClient.prompt()
            .system("You are a creative writer. Be imaginative and engaging.")
            .user(message)
            .options(o -> o.temperature(0.9).maxTokens(2000))
            .call()
            .content();
    }
    
    public String generateTechnical(String message) {
        return defaultClient.prompt()
            .system("You are a technical expert. Provide precise, detailed explanations.")
            .user(message)
            .options(o -> o.temperature(0.3).maxTokens(1500))
            .call()
            .content();
    }
    
    public void registerModel(String modelName, ChatClient client) {
        modelClients.put(modelName, client);
    }
}
```

#### Structured Output Service
```java
package com.learning.springai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class StructuredOutputService {
    
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    
    public StructuredOutputService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
        this.objectMapper = new ObjectMapper();
    }
    
    public <T> T extractJson(String prompt, Class<T> clazz) {
        String jsonResponse = chatClient.prompt()
            .system("Respond only with valid JSON. No other text.")
            .user(prompt)
            .call()
            .content();
        
        try {
            String cleanJson = jsonResponse
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();
            
            return objectMapper.readValue(cleanJson, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        }
    }
    
    public ExtractedData extractData(String text) {
        String prompt = String.format("""
            Extract the following information from the text:
            - Name
            - Email
            - Phone number
            
            Text: %s
            """, text);
        
        return extractJson(prompt, ExtractedData.class);
    }
    
    public SentimentResult analyzeSentiment(String text) {
        String prompt = String.format("""
            Analyze the sentiment of: %s
            
            Return JSON with: sentiment (positive/negative/neutral), confidence (0-1), explanation
            """, text);
        
        return extractJson(prompt, SentimentResult.class);
    }
}

class ExtractedData {
    private String name;
    private String email;
    private String phone;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}

class SentimentResult {
    private String sentiment;
    private double confidence;
    private String explanation;
    
    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
```

#### Controller
```java
package com.learning.springai.controller;

import com.learning.springai.service.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai")
public class ChatController {
    
    private final ChatService chatService;
    private final MultiModelService multiModelService;
    private final StructuredOutputService structuredOutputService;
    
    public ChatController(ChatService chatService,
                         MultiModelService multiModelService,
                         StructuredOutputService structuredOutputService) {
        this.chatService = chatService;
        this.multiModelService = multiModelService;
        this.structuredOutputService = structuredOutputService;
    }
    
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String response = chatService.chat(request.message());
        return ResponseEntity.ok(new ChatResponse(response));
    }
    
    @PostMapping("/chat/system")
    public ResponseEntity<ChatResponse> chatWithSystem(@RequestBody SystemChatRequest request) {
        String response = chatService.chatWithSystem(request.message(), request.system());
        return ResponseEntity.ok(new ChatResponse(response));
    }
    
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody String message) {
        return chatService.chatStream(message);
    }
    
    @PostMapping("/model/{model}")
    public ResponseEntity<ChatResponse> chatWithModel(
            @PathVariable String model,
            @RequestBody ChatRequest request) {
        String response = multiModelService.chatWithModel(model, request.message());
        return ResponseEntity.ok(new ChatResponse(response));
    }
    
    @PostMapping("/creative")
    public ResponseEntity<ChatResponse> creativeChat(@RequestBody ChatRequest request) {
        String response = multiModelService.generateCreative(request.message());
        return ResponseEntity.ok(new ChatResponse(response));
    }
    
    @PostMapping("/technical")
    public ResponseEntity<ChatResponse> technicalChat(@RequestBody ChatRequest request) {
        String response = multiModelService.generateTechnical(request.message());
        return ResponseEntity.ok(new ChatResponse(response));
    }
    
    @PostMapping("/extract")
    public ResponseEntity<ExtractedData> extractData(@RequestBody String text) {
        ExtractedData data = structuredOutputService.extractData(text);
        return ResponseEntity.ok(data);
    }
    
    @PostMapping("/sentiment")
    public ResponseEntity<SentimentResult> analyzeSentiment(@RequestBody String text) {
        SentimentResult result = structuredOutputService.analyzeSentiment(text);
        return ResponseEntity.ok(result);
    }
}

record ChatRequest(String message) {}
record ChatResponse(String response) {}
record SystemChatRequest(String message, String system) {}
```

### Build and Run

```bash
export OPENAI_API_KEY=your-api-key

mvn clean package
java -jar target/spring-ai-mini-1.0.0.jar

curl -X POST http://localhost:8080/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello!"}'
```

---

## Real-World Project: Enterprise RAG System with Vector Store (8+ hours)

### Overview
Build a comprehensive RAG (Retrieval-Augmented Generation) system using Spring AI with vector stores (PostgreSQL, Pinecone, Chroma), embedding models, and multi-document Q&A capabilities. This system handles large document collections with semantic search.

### Project Structure
```
enterprise-rag-system/
├── pom.xml
├── src/main/java/com/learning/springai/
│   ├── RagSystemApplication.java
│   ├── config/
│   │   ├── VectorStoreConfig.java
│   │   ├── EmbeddingConfig.java
│   │   └── RedisConfig.java
│   ├── service/
│   │   ├── DocumentService.java
│   │   ├── EmbeddingService.java
│   │   ├── VectorStoreService.java
│   │   ├── RagService.java
│   │   └── ChatService.java
│   ├── model/
│   │   ├── Document.java
│   │   └── DocumentChunk.java
│   ├── controller/
│   │   └── RagController.java
│   └── dto/
│       └── RagRequest.java
├── src/main/resources/
│   └── application.yml
└── docker-compose.yml
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>enterprise-rag-system</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <spring-ai.version>1.0.0-M2</spring-ai.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-spring-boot-starter</artifactId>
            <version>${spring-ai.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
            <version>${spring-ai.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-vector-stores-spring-data-redis</artifactId>
            <version>${spring-ai.version}</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
    
    <repositories>
        <repository>
            <id>spring-milestones</id>
            <url>https://repo.spring.io/milestone</url>
        </repository>
    </repositories>
</project>
```

#### Vector Store Configuration
```java
package com.learning.springai.config;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStoreConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {
    
    @Value("${spring.ai.openai.api-key:}")
    private String openAiApiKey;
    
    @Value("${spring.ai.vectorstore.redis.host:localhost}")
    private String redisHost;
    
    @Value("${spring.ai.vectorstore.redis.port:6379}")
    private int redisPort;
    
    @Value("${spring.ai.vectorstore.redis.dimensions:1536}")
    private int dimensions;
    
    @Bean
    public EmbeddingClient embeddingClient() {
        return new OpenAiEmbeddingClient(openAiApiKey);
    }
    
    @Bean
    public RedisVectorStoreConfig redisVectorStoreConfig() {
        return RedisVectorStoreConfig.builder()
            .host(redisHost)
            .port(redisPort)
            .dimensions(dimensions)
            .indexName("enterprise-rag")
            .build();
    }
    
    @Bean
    public VectorStore vectorStore(EmbeddingClient embeddingClient,
                                    RedisVectorStoreConfig config) {
        return new RedisVectorStore(embeddingClient, config);
    }
}
```

#### Embedding Service
```java
package com.learning.springai.service;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {
    
    private final EmbeddingClient embeddingClient;
    
    public EmbeddingService(EmbeddingClient embeddingClient) {
        this.embeddingClient = embeddingClient;
    }
    
    public float[] embed(String text) {
        EmbeddingResponse response = embeddingClient.embed(text);
        return response.contents().get(0).vectorAsList().stream()
            .map(Float::floatValue)
            .toArray(float[]::new);
    }
    
    public List<float[]> embedAll(List<String> texts) {
        EmbeddingResponse response = embeddingClient.embed(texts);
        
        return response.contents().stream()
            .map(c -> c.vectorAsList().stream()
                .map(Float::floatValue)
                .toArray(float[]::new))
            .toList();
    }
}
```

#### Document Service
```java
package com.learning.springai.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DocumentService {
    
    private final VectorStore vectorStore;
    private final Map<String, DocumentMetadata> documentStore;
    
    public DocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.documentStore = new HashMap<>();
    }
    
    public String ingestDocument(MultipartFile file) throws IOException {
        String content = new String(file.getBytes());
        
        List<String> chunks = splitIntoChunks(content, 500);
        
        List<Document> documents = new ArrayList<>();
        
        for (int i = 0; i < chunks.size(); i++) {
            String chunkId = UUID.randomUUID().toString();
            
            Document doc = new Document(
                chunkId,
                chunks.get(i),
                Map.of(
                    "source", file.getOriginalFilename(),
                    "chunkIndex", String.valueOf(i),
                    "totalChunks", String.valueOf(chunks.size()),
                    "timestamp", LocalDateTime.now().toString()
                )
            );
            
            documents.add(doc);
        }
        
        vectorStore.add(documents);
        
        documentStore.put(file.getOriginalFilename(),
            new DocumentMetadata(file.getOriginalFilename(), chunks.size()));
        
        return file.getOriginalFilename();
    }
    
    public void ingestFromDirectory(String directory) throws IOException {
        Files.walk(Path.of(directory))
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(".txt") || p.toString().endsWith(".md"))
            .forEach(path -> {
                try {
                    String content = Files.readString(path);
                    ingestTextFile(path.getFileName().toString(), content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }
    
    public void ingestTextFile(String filename, String content) {
        List<String> chunks = splitIntoChunks(content, 500);
        
        List<Document> documents = chunks.stream()
            .map(chunk -> new Document(
                UUID.randomUUID().toString(),
                chunk,
                Map.of("source", filename)
            ))
            .toList();
        
        vectorStore.add(documents);
    }
    
    private List<String> splitIntoChunks(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        
        String[] sentences = text.split("(?<=[.!?])\\s+");
        StringBuilder current = new StringBuilder();
        
        for (String sentence : sentences) {
            if (current.length() + sentence.length() > chunkSize) {
                if (current.length() > 0) {
                    chunks.add(current.toString().trim());
                }
                current = new StringBuilder();
            }
            current.append(sentence).append(" ");
        }
        
        if (current.length() > 0) {
            chunks.add(current.toString().trim());
        }
        
        return chunks;
    }
    
    public List<Document> search(String query, int topK) {
        return vectorStore.similaritySearch(query, topK);
    }
    
    public List<Document> searchWithThreshold(String query, int topK, float threshold) {
        var results = vectorStore.similaritySearch(query, topK);
        
        return results.stream()
            .filter(d -> {
                Double score = (Double) d.getMetadata().get("score");
                return score == null || score >= threshold;
            })
            .toList();
    }
}

class DocumentMetadata {
    private String filename;
    private int chunkCount;
    
    public DocumentMetadata(String filename, int chunkCount) {
        this.filename = filename;
        this.chunkCount = chunkCount;
    }
}
```

#### RAG Service
```java
package com.learning.springai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {
    
    private final ChatClient chatClient;
    private final DocumentService documentService;
    
    public RagService(ChatClient.Builder builder, DocumentService documentService) {
        this.chatClient = builder.build();
        this.documentService = documentService;
    }
    
    public String answer(String question) {
        return answerWithSources(question).answer();
    }
    
    public RagAnswer answerWithSources(String question) {
        List<Document> relevantDocs = documentService.search(question, 5);
        
        if (relevantDocs.isEmpty()) {
            return new RagAnswer(
                "No relevant documents found. Please add documents first.",
                List.of()
            );
        }
        
        String context = relevantDocs.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n\n"));
        
        String prompt = String.format("""
            Based ONLY on the following context, answer the user's question.
            If the context doesn't contain enough information, say so clearly.
            
            Context:
            %s
            
            Question: %s
            """, context, question);
        
        String answer = chatClient.prompt()
            .system("You are a helpful assistant that answers based only on provided context.")
            .user(prompt)
            .call()
            .content();
        
        List<String> sources = relevantDocs.stream()
            .map(d -> d.getMetadata().get("source").toString())
            .distinct()
            .toList();
        
        return new RagAnswer(answer, sources);
    }
    
    public String answerWithCitations(String question) {
        List<Document> relevantDocs = documentService.search(question, 3);
        
        String context = relevantDocs.stream()
            .map(d -> "[Source: " + d.getMetadata().get("source") + 
                     " Chunk: " + d.getMetadata().get("chunkIndex") + "]\n" + 
                     d.getText())
            .collect(Collectors.joining("\n\n"));
        
        String prompt = String.format("""
            Answer based on the following context. Cite sources using the provided references.
            
            Context:
            %s
            
            Question: %s
            """, context, question);
        
        return chatClient.prompt()
            .user(prompt)
            .call()
            .content();
    }
    
    public String generateSummary(String documentName) {
        List<Document> docs = documentService.search(documentName, 10);
        
        String context = docs.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n\n"));
        
        String prompt = String.format("""
            Generate a comprehensive summary of the following document.
            Include main topics, key points, and structure.
            
            Document content:
            %s
            """, context.substring(0, Math.min(5000, context.length())));
        
        return chatClient.prompt()
            .system("You are a summarization expert.")
            .user(prompt)
            .call()
            .content();
    }
}

record RagAnswer(String answer, List<String> sources) {}
```

#### Chat Service
```java
package com.learning.springai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    
    private final ChatClient chatClient;
    private final RagService ragService;
    
    public ChatService(ChatClient.Builder builder, RagService ragService) {
        this.chatClient = builder.build();
        this.ragService = ragService;
    }
    
    public String chat(String message) {
        return chatClient.prompt()
            .user(message)
            .call()
            .content();
    }
    
    public String ragChat(String message) {
        RagAnswer answer = ragService.answerWithSources(message);
        
        if (answer.sources().isEmpty()) {
            return answer.answer();
        }
        
        return answer.answer() + "\n\nSources: " + String.join(", ", answer.sources());
    }
    
    public String multiDocumentComparison(String query, List<String> documentNames) {
        String prompt = String.format("""
            Compare the following documents based on: %s
            """, query);
        
        return chatClient.prompt()
            .user(prompt)
            .call()
            .content();
    }
}
```

#### RAG Controller
```java
package com.learning.springai.controller;

import com.learning.springai.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/rag")
public class RagController {
    
    private final DocumentService documentService;
    private final RagService ragService;
    private final ChatService chatService;
    
    public RagController(DocumentService documentService,
                         RagService ragService,
                         ChatService chatService) {
        this.documentService = documentService;
        this.ragService = ragService;
        this.chatService = chatService;
    }
    
    @PostMapping("/documents")
    public ResponseEntity<IngestResponse> ingestDocument(
            @RequestParam("file") MultipartFile file) {
        try {
            String filename = documentService.ingestDocument(file);
            return ResponseEntity.ok(new IngestResponse(filename, "Document ingested"));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/ask")
    public ResponseEntity<AskResponse> ask(@RequestBody AskRequest request) {
        RagAnswer answer = ragService.answerWithSources(request.question());
        
        return ResponseEntity.ok(new AskResponse(
            answer.answer(),
            answer.sources()
        ));
    }
    
    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK) {
        
        var docs = documentService.search(query, topK);
        
        List<SearchResult> results = docs.stream()
            .map(d -> new SearchResult(
                d.getText(),
                d.getMetadata().get("source").toString()
            ))
            .toList();
        
        return ResponseEntity.ok(new SearchResponse(query, results));
    }
    
    @PostMapping("/summarize/{document}")
    public ResponseEntity<SummaryResponse> summarize(@PathVariable String document) {
        String summary = ragService.generateSummary(document);
        return ResponseEntity.ok(new SummaryResponse(summary));
    }
    
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String answer = request.useRag() 
            ? chatService.ragChat(request.message())
            : chatService.chat(request.message());
        
        return ResponseEntity.ok(new ChatResponse(answer));
    }
}

record IngestResponse(String filename, String message) {}
record AskRequest(String question) {}
record AskResponse(String answer, List<String> sources) {}
record SearchResponse(String query, List<SearchResult> results) {}
record SearchResult(String content, String source) {}
record SummaryResponse(String summary) {}
record ChatRequest(String message, boolean useRag) {}
record ChatResponse(String response) {}
```

### Build and Run

```bash
# Start Redis
docker run -d -p 6379:6379 redis:latest

# Build and run
mvn clean package
java -jar target/enterprise-rag-system-1.0.0.jar

# Ingest document
curl -X POST http://localhost:8080/api/rag/documents \
  -F "file=@document.txt"

# Ask question
curl -X POST http://localhost:8080/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "What is covered in the document?"}'
```

### Extension Ideas
1. Add multi-modal RAG with image support
2. Implement hybrid search (keyword + semantic)
3. Add document chunk optimization
4. Implement caching layer
5. Add real-time indexing