# AI Projects - Module 70

This module covers AI/ML integration, LLMs, and neural networks basics.

## Mini-Project: LLM Integration with Spring AI (2-4 hours)

### Overview
Build a Java application using Spring AI to integrate with LLMs for text generation and analysis.

### Project Structure
```
ai-demo/
├── src/main/java/com/learning/ai/
│   ├── AiDemoApplication.java
│   ├── controller/AiController.java
│   ├── service/ChatService.java
│   └── config/AiConfig.java
├── pom.xml
└── run.sh
```

### Implementation
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>ai-demo</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
            <version>1.0.0-M2</version>
        </dependency>
    </dependencies>
    
    <repositories>
        <repository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/milestone</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>
</project>

// application.yml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
          temperature: 0.7

// ChatService.java
package com.learning.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatService {
    
    private final ChatClient chatClient;
    
    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    
    public String generate(String prompt) {
        return chatClient.prompt()
            .user(prompt)
            .call()
            .content();
    }
    
    public String generateWithContext(String userPrompt, String context) {
        SystemMessage systemMessage = SystemMessage.builder()
            .text("You are a helpful assistant. Use the following context to answer: " + context)
            .build();
        
        return chatClient.prompt()
            .system(systemMessage)
            .user(userPrompt)
            .call()
            .content();
    }
    
    public Flux<String> generateStream(String prompt) {
        return chatClient.prompt()
            .user(prompt)
            .stream()
            .content();
    }
    
    public String summarize(String text) {
        return chatClient.prompt()
            .system("You are a summarization expert. Provide a concise summary.")
            .user("Summarize: " + text)
            .call()
            .content();
    }
}

// AiController.java
package com.learning.ai.controller;

import com.learning.ai.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    
    private final ChatService chatService;
    
    public AiController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String response = chatService.generateWithContext(
            request.getUserMessage(), 
            request.getContext()
        );
        
        return ResponseEntity.ok(new ChatResponse(response));
    }
    
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody String prompt) {
        return chatService.generateStream(prompt);
    }
    
    @PostMapping("/summarize")
    public ResponseEntity<SummarizeResponse> summarize(@RequestBody String text) {
        String summary = chatService.summarize(text);
        return ResponseEntity.ok(new SummarizeResponse(summary));
    }
}

record ChatRequest(String userMessage, String context) {}
record ChatResponse(String response) {}
record SummarizeResponse(String summary) {}
```

---

## Real-World Project: AI-Powered Customer Support System (8+ hours)

### Overview
Build an AI-powered customer support system using LLM integration, embeddings, and vector storage.

### Project Structure
```
ai-support-system/
├── src/main/java/com/learning/ai/
│   ├── controller/
│   ├── service/
│   ├── embedding/
│   ├── vector/
│   └── config/
├── pom.xml
└── docker-compose.yml
```

### Implementation
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>ai-support-system</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
            <version>1.0.0-M2</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-vector-stores-spring-data-redis</artifactId>
            <version>1.0.0-M2</version>
        </dependency>
    </dependencies>
</project>

// VectorStore Configuration
package com.learning.ai.config;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStoreConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {
    
    @Bean
    public VectorStore vectorStore(EmbeddingClient embeddingClient) {
        RedisVectorStoreConfig config = RedisVectorStoreConfig.builder()
            .withIndexName("support-articles")
            .withNamespace("support")
            .build();
        
        return new RedisVectorStore(embeddingClient, config);
    }
}

// Support Article Entity
package com.learning.ai.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("support_articles")
public class SupportArticle {
    
    @Id
    private String id;
    private String title;
    private String content;
    private String category;
    private String[] tags;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }
}

// Embedding Service
package com.learning.ai.service;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import com.learning.ai.model.SupportArticle;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EmbeddingService {
    
    private final EmbeddingClient embeddingClient;
    private final VectorStore vectorStore;
    
    public EmbeddingService(EmbeddingClient embeddingClient, VectorStore vectorStore) {
        this.embeddingClient = embeddingClient;
        this.vectorStore = vectorStore;
    }
    
    public void indexArticle(SupportArticle article) {
        article.setId(UUID.randomUUID().toString());
        
        Document document = new Document(
            article.getId(),
            article.getTitle() + " " + article.getContent(),
            Map.of("title", article.getTitle(),
                   "category", article.getCategory())
        );
        
        vectorStore.add(List.of(document));
    }
    
    public List<SupportArticle> findSimilarArticles(String query, int limit) {
        List<Document> results = vectorStore.similaritySearch(query, limit);
        
        return results.stream()
            .map(doc -> {
                SupportArticle article = new SupportArticle();
                article.setId(doc.getId());
                article.setTitle(doc.getMetadata().get("title").toString());
                article.setContent(doc.getContent());
                article.setCategory(doc.getMetadata().get("category").toString());
                return article;
            })
            .toList();
    }
}

// RAG (Retrieval-Augmented Generation) Service
package com.learning.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {
    
    private final ChatClient chatClient;
    private final EmbeddingService embeddingService;
    
    public RagService(ChatClient.Builder chatClientBuilder, 
                      EmbeddingService embeddingService) {
        this.chatClient = chatClientBuilder.build();
        this.embeddingService = embeddingService;
    }
    
    public String answerQuestion(String question) {
        List<SupportArticle> relevantArticles = embeddingService
            .findSimilarArticles(question, 3);
        
        if (relevantArticles.isEmpty()) {
            return "I couldn't find any relevant information to answer your question.";
        }
        
        String context = relevantArticles.stream()
            .map(a -> "Title: " + a.getTitle() + "\n" + a.getContent())
            .collect(Collectors.joining("\n\n"));
        
        SystemMessage systemMessage = SystemMessage.builder()
            .text("""
                You are a customer support assistant. Use the provided context 
                to answer the user's question. If the context doesn't contain 
                enough information to answer the question, say so clearly.
                
                Context:
                """ + context)
            .build();
        
        return chatClient.prompt()
            .system(systemMessage)
            .user(question)
            .call()
            .content();
    }
}

// Customer Support Controller
package com.learning.ai.controller;

import com.learning.ai.service.RagService;
import com.learning.ai.service.EmbeddingService;
import com.learning.ai.model.SupportArticle;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
public class SupportController {
    
    private final RagService ragService;
    private final EmbeddingService embeddingService;
    
    public SupportController(RagService ragService, EmbeddingService embeddingService) {
        this.ragService = ragService;
        this.embeddingService = embeddingService;
    }
    
    @PostMapping("/ask")
    public ResponseEntity<SupportResponse> askQuestion(@RequestBody SupportRequest request) {
        String answer = ragService.answerQuestion(request.getQuestion());
        return ResponseEntity.ok(new SupportResponse(answer));
    }
    
    @PostMapping("/articles")
    public ResponseEntity<ArticleResponse> addArticle(@RequestBody SupportArticle article) {
        embeddingService.indexArticle(article);
        return ResponseEntity.ok(new ArticleResponse("Article indexed successfully"));
    }
    
    @GetMapping("/articles/search")
    public ResponseEntity<List<SupportArticle>> searchArticles(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit) {
        
        List<SupportArticle> articles = embeddingService.findSimilarArticles(query, limit);
        return ResponseEntity.ok(articles);
    }
}

record SupportRequest(String question) {}
record SupportResponse(String answer) {}
record ArticleResponse(String message) {}

// Sentiment Analysis Service
package com.learning.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SentimentAnalysisService {
    
    private final ChatClient chatClient;
    
    public SentimentAnalysisService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    
    public SentimentResult analyze(String text) {
        String prompt = """
            Analyze the sentiment of the following text. 
            Return a JSON object with 'sentiment' (positive, negative, or neutral) 
            and 'score' (a number from -1 to 1).
            
            Text: """ + text;
        
        String result = chatClient.prompt()
            .system("You are a sentiment analysis expert. Return only valid JSON.")
            .user(prompt)
            .call()
            .content();
        
        return parseSentiment(result);
    }
    
    private SentimentResult parseSentiment(String json) {
        try {
            String sentiment = json.contains("positive") ? "POSITIVE" 
                : json.contains("negative") ? "NEGATIVE" : "NEUTRAL";
            
            double score = 0.0;
            if (json.contains("\"score\"")) {
                String scoreStr = json.split("\"score\"")[1].split(":")[1]
                    .replaceAll("[^0-9.-]", "").trim();
                score = Double.parseDouble(scoreStr.split(",")[0]);
            }
            
            return new SentimentResult(sentiment, score);
        } catch (Exception e) {
            return new SentimentResult("NEUTRAL", 0.0);
        }
    }
}

record SentimentResult(String sentiment, double score) {}

// Ticket Classification Service
package com.learning.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class TicketClassificationService {
    
    private final ChatClient chatClient;
    
    public TicketClassificationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    
    public TicketClassification classify(String ticketTitle, String ticketDescription) {
        String prompt = String.format("""
            Classify this support ticket into one of the following categories:
            - BILLING
            - TECHNICAL
            - ACCOUNT
            - SHIPPING
            - GENERAL
            
            Title: %s
            Description: %s
            
            Return JSON with 'category' and 'priority' (low, medium, high).
            """, ticketTitle, ticketDescription);
        
        String result = chatClient.prompt()
            .system("You are a ticket classification system.")
            .user(prompt)
            .call()
            .content();
        
        return parseClassification(result);
    }
    
    private TicketClassification parseClassification(String json) {
        String category = "GENERAL";
        String priority = "MEDIUM";
        
        if (json.contains("BILLING")) category = "BILLING";
        else if (json.contains("TECHNICAL")) category = "TECHNICAL";
        else if (json.contains("ACCOUNT")) category = "ACCOUNT";
        else if (json.contains("SHIPPING")) category = "SHIPPING";
        
        if (json.contains("high")) priority = "HIGH";
        else if (json.contains("low")) priority = "LOW";
        
        return new TicketClassification(category, priority);
    }
}

record TicketClassification(String category, String priority) {}
```

### Build and Run
```bash
# Export OpenAI API key
export OPENAI_API_KEY=your-api-key

# Start Redis for vector storage
docker run -d -p 6379:6379 redis:latest

# Build the application
mvn clean package

# Run the application
java -jar target/ai-support-system-1.0.0.jar

# Test the API
curl -X POST http://localhost:8080/api/support/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"How do I reset my password?"}'

curl -X GET "http://localhost:8080/api/support/articles/search?query=password&limit=5"
```

### Learning Outcomes
- Integrate LLMs with Spring AI
- Implement RAG (Retrieval-Augmented Generation)
- Build vector embeddings
- Create semantic search
- Perform sentiment analysis
- Build AI-powered applications