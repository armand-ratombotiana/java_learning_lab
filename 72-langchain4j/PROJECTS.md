# LangChain4j Projects - Module 72

This module covers LLM integration, prompt templates, chains, and memory management using LangChain4j.

## Mini-Project: LLM Chat Application with Prompt Templates (2-4 hours)

### Overview
Build a Java application using LangChain4j to create a conversational AI assistant with dynamic prompt templates, structured output parsing, and conversation history management. This project demonstrates core LangChain4j concepts including chat models, prompts, and output parsers.

### Project Structure
```
langchain4j-mini/
├── pom.xml
├── src/main/java/com/learning/langchain4j/
│   ├── MiniProjectApplication.java
│   ├── service/
│   │   ├── ChatService.java
│   │   ├── PromptTemplateService.java
│   │   └── OutputParserService.java
│   ├── model/
│   │   └── ChatResponse.java
│   └── config/
│       └── LangChainConfig.java
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
    <artifactId>langchain4j-mini</artifactId>
    <version>1.0.0</version>
    <name>LangChain4j Mini Project</name>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <langchain4j.version>0.35.0</langchain4j.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j</artifactId>
            <version>${langchain4j.version}</version>
        </dependency>
        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j-open-ai</artifactId>
            <version>${langchain4j.version}</version>
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

#### LangChain Configuration
```java
package com.learning.langchain4j.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfig {
    
    @Value("${langchain4j.openai.api-key:}")
    private String apiKey;
    
    @Value("${langchain4j.openai.model-name:gpt-3.5-turbo}")
    private String modelName;
    
    @Value("${langchain4j.openai.temperature:0.7}")
    private double temperature;
    
    @Value("${langchain4j.openai.max-tokens:1000}")
    private int maxTokens;
    
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
            .apiKey(apiKey)
            .modelName(OpenAiChatModelName.GPT_3_5_TURBO)
            .temperature(temperature)
            .maxTokenCount(maxTokens)
            .build();
    }
    
    @Bean
    public ChatLanguageModel creativeChatModel() {
        return OpenAiChatModel.builder()
            .apiKey(apiKey)
            .modelName(OpenAiChatModelName.GPT_3_5_TURBO)
            .temperature(0.9)
            .maxTokenCount(2000)
            .build();
    }
}
```

#### Chat Service
```java
package com.learning.langchain4j.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ChatService {
    String chat(String message);
    String chatWithContext(String userMessage, List<String> context);
    String summarize(String text);
    String translate(String text, String targetLanguage);
}

@Service
public class ChatServiceImpl implements ChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);
    
    private final ChatLanguageModel chatModel;
    private final ConversationalAssistant assistant;
    
    public ChatServiceImpl(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        this.assistant = AiServices.builder(ConversationalAssistant.class)
            .chatLanguageModel(chatModel)
            .build();
    }
    
    @Override
    public String chat(String message) {
        logger.info("Processing chat message: {}", message);
        
        String response = assistant.chat(message);
        
        logger.info("Response: {}", response);
        return response;
    }
    
    @Override
    public String chatWithContext(String userMessage, List<String> context) {
        String contextString = String.join("\n- ", context);
        
        String prompt = String.format("""
            Based on the following context, answer the user's question.
            
            Context:
            - %s
            
            User Question: %s
            """, contextString, userMessage);
        
        return chatModel.chat(prompt);
    }
    
    @Override
    public String summarize(String text) {
        String prompt = String.format("""
            Provide a concise summary of the following text in 2-3 sentences:
            
            %s
            """, text);
        
        return chatModel.chat(prompt);
    }
    
    @Override
    public String translate(String text, String targetLanguage) {
        String prompt = String.format("""
            Translate the following text to %s:
            
            %s
            """, targetLanguage, text);
        
        return chatModel.chat(prompt);
    }
}

interface ConversationalAssistant {
    String chat(String message);
    
    @SystemMessage("You are a helpful, knowledgeable assistant that provides clear and accurate information.")
    String helpfulChat(@UserMessage String userMessage);
}
```

#### Prompt Template Service
```java
package com.learning.langchain4j.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PromptTemplateService {
    
    private static final Logger logger = LoggerFactory.getLogger(PromptTemplateService.class);
    
    private final ChatLanguageModel chatModel;
    private final Map<String, PromptTemplate> templates;
    
    public PromptTemplateService(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        this.templates = new HashMap<>();
        initializeTemplates();
    }
    
    private void initializeTemplates() {
        templates.put("analyze_code", PromptTemplate.builder()
            .template("""
                Analyze the following {{language}} code and provide:
                1. Purpose of the code
                2. Potential bugs or issues
                3. Suggestions for improvement
                
                Code:
                ```
                {{code}}
                ```
                """)
            .build());
        
        templates.put("generate_tests", PromptTemplate.builder()
            .template("""
                Generate unit tests for the following {{language}} code.
                Include both positive and negative test cases.
                
                Code:
                ```
                {{code}}
                ```
                
                Test Framework: {{testFramework}}
                """)
            .build());
        
        templates.put("explain_error", PromptTemplate.builder()
            .template("""
                Explain the following error in simple terms and suggest how to fix it:
                
                Error: {{errorMessage}}
                
                Context: {{context}}
                """)
            .build());
        
        templates.put("review_pr", PromptTemplate.builder()
            .template("""
                Perform a code review of the following pull request.
                
                Title: {{title}}
                Description: {{description}}
                
                Changes:
                ```diff
                {{diff}}
                ```
                
                Provide:
                1. Overall assessment
                2. Potential issues
                3. Suggestions for improvement
                """)
            .build());
    }
    
    public String execute(String templateName, Map<String, String> variables) {
        PromptTemplate template = templates.get(templateName);
        
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateName);
        }
        
        Prompt prompt = template.apply(variables);
        
        logger.info("Executing template: {} with variables: {}", templateName, variables.keySet());
        
        return chatModel.chat(prompt.text());
    }
    
    public String analyzeCode(String language, String code) {
        Map<String, String> variables = Map.of("language", language, "code", code);
        return execute("analyze_code", variables);
    }
    
    public String generateTests(String language, String code, String testFramework) {
        Map<String, String> variables = Map.of(
            "language", language,
            "code", code,
            "testFramework", testFramework
        );
        return execute("generate_tests", variables);
    }
    
    public String explainError(String errorMessage, String context) {
        Map<String, String> variables = Map.of(
            "errorMessage", errorMessage,
            "context", context
        );
        return execute("explain_error", variables);
    }
    
    public void addTemplate(String name, String templateString) {
        templates.put(name, PromptTemplate.builder()
            .template(templateString)
            .build());
    }
}
```

#### Output Parser Service
```java
package com.learning.langchain4j.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OutputParserService {
    
    private static final Logger logger = LoggerFactory.getLogger(OutputParserService.class);
    
    private final ChatLanguageModel chatModel;
    
    public OutputParserService(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }
    
    public ParsedResponse parseStructuredOutput(String prompt, String format) {
        String fullPrompt = prompt + "\n\nFormat your response as: " + format;
        
        String response = chatModel.chat(fullPrompt);
        
        return parseResponse(response, format);
    }
    
    private ParsedResponse parseResponse(String response, String format) {
        ParsedResponse parsed = new ParsedResponse();
        parsed.setRawResponse(response);
        
        switch (format.toLowerCase()) {
            case "json" -> parsed.setJsonData(parseJson(response));
            case "yaml" -> parsed.setYamlData(parseYaml(response));
            case "list" -> parsed.setListItems(parseList(response));
            default -> parsed.setText(response);
        }
        
        return parsed;
    }
    
    private Map<String, String> parseJson(String json) {
        return Map.of("parsed", json);
    }
    
    private Map<String, String> parseYaml(String yaml) {
        return Map.of("parsed", yaml);
    }
    
    private List<String> parseList(String text) {
        return java.util.Arrays.asList(text.split("\n"))
            .stream()
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();
    }
    
    public List<String> extractItems(String text) {
        Response<String> response = chatModel.chat(
            "Extract all items from the following text as a comma-separated list:\n\n" + text
        );
        
        return java.util.Arrays.asList(response.content().split(","))
            .stream()
            .map(String::trim)
            .toList();
    }
    
    public Map<String, String> extractKeyValuePairs(String text) {
        return Map.of("text", text);
    }
}

class ParsedResponse {
    private String rawResponse;
    private String text;
    private Map<String, String> jsonData;
    private Map<String, String> yamlData;
    private List<String> listItems;
    
    public String getRawResponse() { return rawResponse; }
    public void setRawResponse(String rawResponse) { this.rawResponse = rawResponse; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Map<String, String> getJsonData() { return jsonData; }
    public void setJsonData(Map<String, String> jsonData) { this.jsonData = jsonData; }
    public Map<String, String> getYamlData() { return yamlData; }
    public void setYamlData(Map<String, String> yamlData) { this.yamlData = yamlData; }
    public List<String> getListItems() { return listItems; }
    public void setListItems(List<String> listItems) { this.listItems = listItems; }
}
```

#### Chat Controller
```java
package com.learning.langchain4j.controller;

import com.learning.langchain4j.service.ChatService;
import com.learning.langchain4j.service.PromptTemplateService;
import com.learning.langchain4j.service.OutputParserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    
    private final ChatService chatService;
    private final PromptTemplateService promptTemplateService;
    private final OutputParserService outputParserService;
    
    public ChatController(ChatService chatService,
                          PromptTemplateService promptTemplateService,
                          OutputParserService outputParserService) {
        this.chatService = chatService;
        this.promptTemplateService = promptTemplateService;
        this.outputParserService = outputParserService;
    }
    
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String response = chatService.chat(request.message());
        return ResponseEntity.ok(new ChatResponse(response));
    }
    
    @PostMapping("/context")
    public ResponseEntity<ChatResponse> chatWithContext(@RequestBody ContextChatRequest request) {
        String response = chatService.chatWithContext(request.message(), request.context());
        return ResponseEntity.ok(new ChatResponse(response));
    }
    
    @PostMapping("/summarize")
    public ResponseEntity<ChatResponse> summarize(@RequestBody String text) {
        String response = chatService.summarize(text);
        return ResponseEntity.ok(new ChatResponse(response));
    }
    
    @PostMapping("/translate")
    public ResponseEntity<ChatResponse> translate(@RequestBody TranslateRequest request) {
        String response = chatService.translate(request.text(), request.targetLanguage());
        return ResponseEntity.ok(new ChatResponse(response));
    }
    
    @PostMapping("/template")
    public ResponseEntity<ChatResponse> executeTemplate(@RequestBody TemplateRequest request) {
        String response = promptTemplateService.execute(request.templateName(), request.variables());
        return ResponseEntity.ok(new ChatResponse(response));
    }
    
    @PostMapping("/parse")
    public ResponseEntity<ParsedResponse> parseOutput(@RequestBody ParseRequest request) {
        ParsedResponse response = outputParserService.parseStructuredOutput(
            request.prompt(), request.format());
        return ResponseEntity.ok(response);
    }
}

record ChatRequest(String message) {}
record ChatResponse(String response) {}
record ContextChatRequest(String message, List<String> context) {}
record TranslateRequest(String text, String targetLanguage) {}
record TemplateRequest(String templateName, Map<String, String> variables) {}
record ParseRequest(String prompt, String format) {}
```

### Build and Run

```bash
# Set OpenAI API key
export OPENAI_API_KEY=your-api-key

# Build and run
mvn clean package
java -jar target/langchain4j-mini-1.0.0.jar

# Test chat endpoint
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "What is Java?"}'

# Test with context
curl -X POST http://localhost:8080/api/chat/context \
  -H "Content-Type: application/json" \
  -d '{"message": "How do I fix it?", "context": ["Error: NullPointerException at line 42", "Code is checking user.getName()"]}'
```

### Sample Output
```json
{
  "response": "Java is a high-level, object-oriented programming language designed to be portable and platform-independent. It follows the 'Write Once, Run Anywhere' principle."
}
```

### Extension Ideas
1. Add conversation memory with LangChain4j Memory API
2. Implement RAG with document loaders
3. Create chain with multiple LLM calls
4. Add streaming response support

---

## Real-World Project: Enterprise Document Processing with RAG and Chains (8+ hours)

### Overview
Build a comprehensive document processing system using LangChain4j that implements Retrieval-Augmented Generation (RAG), multi-step chains, and conversation memory. This project processes various document types, creates embeddings, and provides intelligent Q&A capabilities.

### Project Structure
```
enterprise-document-processor/
├── pom.xml
├── src/main/java/com/learning/langchain4j/
│   ├── DocumentProcessorApplication.java
│   ├── config/
│   │   ├── LangChainConfig.java
│   │   ├── EmbeddingConfig.java
│   │   └── VectorStoreConfig.java
│   ├── service/
│   │   ├── DocumentService.java
│   │   ├── EmbeddingService.java
│   │   ├── RagService.java
│   │   ├── ChainService.java
│   │   └── MemoryService.java
│   ├── model/
│   │   ├── Document.java
│   │   └── ProcessedChunk.java
│   ├── chain/
│   │   ├── DocumentAnalysisChain.java
│   │   └── SummaryChain.java
│   ├── controller/
│   │   └── DocumentController.java
│   └── dto/
│       └── DocumentRequest.java
├── src/main/resources/
│   └── application.yml
└── data/
    └── documents/
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
    <artifactId>enterprise-document-processor</artifactId>
    <version>1.0.0</version>
    <name>Enterprise Document Processor</name>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <langchain4j.version>0.35.0</langchain4j.version>
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
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j</artifactId>
            <version>${langchain4j.version}</version>
        </dependency>
        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j-open-ai</artifactId>
            <version>${langchain4j.version}</version>
        </dependency>
        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j-embeddings-all-minilm-l6-v2</artifactId>
            <version>${langchain4j.version}</version>
        </dependency>
        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j-document-loader-text</artifactId>
            <version>${langchain4j.version}</version>
        </dependency>
        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j-pdf</artifactId>
            <version>${langchain4j.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
</project>
```

#### Document Model
```java
package com.learning.langchain4j.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "documents")
public class Document {
    
    @Id
    private String id;
    private String title;
    private String content;
    private String documentType;
    private Map<String, String> metadata;
    private List<ProcessedChunk> chunks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Document() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Document(String title, String content, String documentType) {
        this();
        this.title = title;
        this.content = content;
        this.documentType = documentType;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    public List<ProcessedChunk> getChunks() { return chunks; }
    public void setChunks(List<ProcessedChunk> chunks) { this.chunks = chunks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

class ProcessedChunk {
    private int chunkIndex;
    private String content;
    private float[] embedding;
    private int startIndex;
    private int endIndex;
    
    public ProcessedChunk() {}
    
    public ProcessedChunk(int chunkIndex, String content, int startIndex, int endIndex) {
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }
    
    public int getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(int chunkIndex) { this.chunkIndex = chunkIndex; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public float[] getEmbedding() { return embedding; }
    public void setEmbedding(float[] embedding) { this.embedding = embedding; }
    public int getStartIndex() { return startIndex; }
    public void setStartIndex(int startIndex) { this.startIndex = startIndex; }
    public int getEndIndex() { return endIndex; }
    public void setEndIndex(int endIndex) { this.endIndex = endIndex; }
}
```

#### Embedding Configuration
```java
package com.learning.langchain4j.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2EmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingConfig {
    
    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }
}
```

#### Embedding Service
```java
package com.learning.langchain4j.service;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmbeddingService.class);
    
    private final EmbeddingModel embeddingModel;
    
    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }
    
    public float[] embed(String text) {
        logger.debug("Embedding text of length: {}", text.length());
        
        EmbeddingResponse response = embeddingModel.embed(text);
        
        return response.contents().get(0).vectorAsList().stream()
            .map(Float::floatValue)
            .toArray(float[]::new);
    }
    
    public List<float[]> embedAll(List<String> texts) {
        logger.info("Embedding {} text chunks", texts.size());
        
        EmbeddingResponse response = embeddingModel.embed(texts);
        
        List<float[]> embeddings = new ArrayList<>();
        for (var content : response.contents()) {
            embeddings.add(content.vectorAsList().stream()
                .map(Float::floatValue)
                .toArray(float[]::new));
        }
        
        return embeddings;
    }
    
    public float cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors must have same dimension");
        }
        
        float dotProduct = 0;
        float normA = 0;
        float normB = 0;
        
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        return dotProduct / (float) (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    public List<SearchResult> findSimilar(String query, List<String> chunks, int topK) {
        float[] queryEmbedding = embed(query);
        List<float[]> chunkEmbeddings = embedAll(chunks);
        
        List<SearchResult> results = new ArrayList<>();
        
        for (int i = 0; i < chunks.size(); i++) {
            float similarity = cosineSimilarity(queryEmbedding, chunkEmbeddings.get(i));
            results.add(new SearchResult(i, chunks.get(i), similarity));
        }
        
        return results.stream()
            .sorted((a, b) -> Float.compare(b.similarity, a.similarity))
            .limit(topK)
            .toList();
    }
}

record SearchResult(int index, String content, float similarity) {}
```

#### Document Service
```java
package com.learning.langchain4j.service;

import com.learning.langchain4j.model.Document;
import com.learning.langchain4j.model.ProcessedChunk;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    
    private final EmbeddingService embeddingService;
    private final List<Document> documentStore;
    
    public DocumentService(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
        this.documentStore = new ArrayList<>();
    }
    
    public Document processDocument(MultipartFile file) throws IOException {
        logger.info("Processing document: {}", file.getOriginalFilename());
        
        String content = new String(file.getBytes());
        
        List<String> chunks = splitIntoChunks(content, 500);
        logger.info("Split into {} chunks", chunks.size());
        
        List<ProcessedChunk> processedChunks = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            float[] embedding = embeddingService.embed(chunk);
            
            ProcessedChunk processedChunk = new ProcessedChunk(i, chunk, 
                i * 500, Math.min((i + 1) * 500, content.length()));
            processedChunk.setEmbedding(embedding);
            
            processedChunks.add(processedChunk);
        }
        
        Document document = new Document(
            file.getOriginalFilename(),
            content,
            determineDocumentType(file.getOriginalFilename())
        );
        document.setChunks(processedChunks);
        
        documentStore.add(document);
        
        logger.info("Document processed and stored with ID: {}", document.getId());
        
        return document;
    }
    
    public void processDirectory(String directoryPath) throws IOException {
        Path path = Path.of(directoryPath);
        
        Files.walk(path)
            .filter(Files::isRegularFile)
            .filter(p -> isSupportedFile(p.toString()))
            .forEach(filePath -> {
                try {
                    String content = Files.readString(filePath);
                    
                    List<String> chunks = splitIntoChunks(content, 500);
                    
                    List<ProcessedChunk> processedChunks = new ArrayList<>();
                    for (int i = 0; i < chunks.size(); i++) {
                        float[] embedding = embeddingService.embed(chunks.get(i));
                        
                        ProcessedChunk chunk = new ProcessedChunk(i, chunks.get(i),
                            i * 500, Math.min((i + 1) * 500, content.length()));
                        chunk.setEmbedding(embedding);
                        processedChunks.add(chunk);
                    }
                    
                    Document doc = new Document(
                        filePath.getFileName().toString(),
                        content,
                        determineDocumentType(filePath.toString())
                    );
                    doc.setChunks(processedChunks);
                    
                    documentStore.add(doc);
                    
                    logger.info("Processed file: {}", filePath);
                    
                } catch (IOException e) {
                    logger.error("Error processing file: {}", filePath, e);
                }
            });
    }
    
    private List<String> splitIntoChunks(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        
        String[] sentences = text.split("(?<=[.!?])\\s+");
        
        StringBuilder currentChunk = new StringBuilder();
        
        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() > chunkSize) {
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());
                }
                currentChunk = new StringBuilder();
            }
            currentChunk.append(sentence).append(" ");
        }
        
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }
        
        return chunks;
    }
    
    private String determineDocumentType(String filename) {
        String lower = filename.toLowerCase();
        
        if (lower.endsWith(".pdf")) return "PDF";
        if (lower.endsWith(".txt")) return "TEXT";
        if (lower.endsWith(".md")) return "MARKDOWN";
        if (lower.endsWith(".html")) return "HTML";
        
        return "UNKNOWN";
    }
    
    private boolean isSupportedFile(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".txt") || lower.endsWith(".md") || 
               lower.endsWith(".pdf");
    }
    
    public Document findById(String id) {
        return documentStore.stream()
            .filter(d -> d.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    public List<Document> search(String query) {
        float[] queryEmbedding = embeddingService.embed(query);
        
        return documentStore.stream()
            .map(doc -> {
                double maxSimilarity = doc.getChunks().stream()
                    .map(chunk -> embeddingService.cosineSimilarity(queryEmbedding, chunk.getEmbedding()))
                    .mapToDouble(Double::doubleValue)
                    .max()
                    .orElse(0.0);
                return new SearchResultDocument(doc, maxSimilarity);
            })
            .sorted((a, b) -> Double.compare(b.similarity, a.similarity))
            .map(r -> r.document)
            .toList();
    }
}

record SearchResultDocument(Document document, double similarity) {}
```

#### RAG Service
```java
package com.learning.langchain4j.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {
    
    private static final Logger logger = LoggerFactory.getLogger(RagService.class);
    
    private final ChatLanguageModel chatModel;
    private final DocumentService documentService;
    private final EmbeddingService embeddingService;
    private final RAGAssistant assistant;
    
    public RagService(ChatLanguageModel chatModel,
                      DocumentService documentService,
                      EmbeddingService embeddingService) {
        this.chatModel = chatModel;
        this.documentService = documentService;
        this.embeddingService = embeddingService;
        this.assistant = AiServices.builder(RAGAssistant.class)
            .chatLanguageModel(chatModel)
            .build();
    }
    
    public String answerQuestion(String question) {
        logger.info("Processing question: {}", question);
        
        List<String> allChunks = documentService.findById("").getChunks().stream()
            .map(c -> c.getContent())
            .toList();
        
        if (allChunks.isEmpty()) {
            return "No documents have been indexed yet. Please upload documents first.";
        }
        
        var similarChunks = embeddingService.findSimilar(question, allChunks, 5);
        
        String context = String.join("\n\n", 
            similarChunks.stream()
                .map(c -> c.content())
                .toList());
        
        String prompt = String.format("""
            Based ONLY on the following context, answer the question.
            If the context doesn't contain enough information to answer, say so.
            
            Context:
            %s
            
            Question: %s
            """, context, question);
        
        String answer = chatModel.chat(prompt);
        
        logger.info("Generated answer with {} relevant chunks", similarChunks.size());
        
        return answer;
    }
    
    public String answerWithSources(String question) {
        var similarChunks = embeddingService.findSimilar(
            question,
            documentService.findById("").getChunks().stream()
                .map(c -> c.content())
                .toList(),
            3
        );
        
        String context = String.join("\n\n", 
            similarChunks.stream().map(c -> c.content()).toList());
        
        String prompt = String.format("""
            Based ONLY on the following context, answer the question.
            Include the source document reference in your answer.
            
            Context:
            %s
            
            Question: %s
            """, context, question);
        
        String answer = chatModel.chat(prompt);
        
        String sources = "\n\nSources:\n" + similarChunks.stream()
            .map(c -> String.format("- Chunk %d (similarity: %.2f)", 
                c.index(), c.similarity()))
            .reduce((a, b) -> a + "\n" + b)
            .orElse("");
        
        return answer + sources;
    }
    
    public String generateSummary(String documentId) {
        Document doc = documentService.findById(documentId);
        
        if (doc == null) {
            return "Document not found";
        }
        
        String prompt = String.format("""
            Generate a comprehensive summary of the following document.
            Include:
            1. Main topics
            2. Key points
            3. Structure overview
            
            Document:
            %s
            """, doc.getContent().substring(0, Math.min(5000, doc.getContent().length())));
        
        return chatModel.chat(prompt);
    }
}

interface RAGAssistant {
    String answer(@UserMessage String question);
}
```

#### Chain Service
```java
package com.learning.langchain4j.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChainService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChainService.class);
    
    private final ChatLanguageModel chatModel;
    
    public ChainService(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }
    
    public ChainResult executeAnalysisChain(String documentContent) {
        logger.info("Executing analysis chain");
        
        String extractEntities = chatModel.chat(
            "Extract all named entities (people, organizations, locations) from the following text:\n\n" + 
            documentContent.substring(0, Math.min(3000, documentContent.length()))
        );
        
        String extractKeywords = chatModel.chat(
            "Extract the main keywords and topics from the following text:\n\n" + 
            documentContent.substring(0, Math.min(3000, documentContent.length()))
        );
        
        String sentiment = chatModel.chat(
            "Analyze the sentiment of the following text (positive, negative, or neutral):\n\n" +
            documentContent.substring(0, Math.min(2000, documentContent.length()))
        );
        
        return new ChainResult(extractEntities, extractKeywords, sentiment);
    }
    
    public String executeSummaryChain(String documentContent, int numPoints) {
        logger.info("Executing summary chain with {} key points", numPoints);
        
        String prompt = String.format("""
            Perform multi-step summarization of the following text:
            
            Step 1: Identify the main theme
            Step 2: Extract %d key points
            Step 3: Provide a concise conclusion
            
            Text:
            %s
            """, numPoints, documentContent.substring(0, Math.min(5000, documentContent.length())));
        
        return chatModel.chat(prompt);
    }
    
    public ComparisonResult compareDocuments(String doc1, String doc2) {
        logger.info("Executing document comparison chain");
        
        String comparison = chatModel.chat(
            String.format("""
                Compare and contrast the following two documents.
                Identify:
                1. Similarities
                2. Differences
                3. Complementary information
                
                Document 1:
                %s
                
                Document 2:
                %s
                """,
                doc1.substring(0, Math.min(2000, doc1.length())),
                doc2.substring(0, Math.min(2000, doc2.length())))
        );
        
        return new ComparisonResult(comparison, List.of("Similarities", "Differences", "Complementary info"));
    }
    
    public List<FollowUpQuestion> generateFollowUpQuestions(String answer, String originalQuestion) {
        String prompt = String.format("""
            Based on the following Q&A, suggest 3 follow-up questions:
            
            Q: %s
            A: %s
            
            Format each question on a new line starting with a number.
            """, originalQuestion, answer);
        
        String response = chatModel.chat(prompt);
        
        List<FollowUpQuestion> questions = new ArrayList<>();
        String[] lines = response.split("\n");
        
        for (int i = 0; i < Math.min(3, lines.length); i++) {
            String q = lines[i].replaceFirst("^\\d+\\.\\s*", "").trim();
            if (!q.isEmpty()) {
                questions.add(new FollowUpQuestion(i + 1, q));
            }
        }
        
        return questions;
    }
    
    public String executeExtractionChain(String text, ExtractionType type) {
        String prompt = switch (type) {
            case EMAIL -> String.format(
                "Extract all email addresses from:\n%s", text);
            case PHONE -> String.format(
                "Extract all phone numbers from:\n%s", text);
            case URL -> String.format(
                "Extract all URLs from:\n%s", text);
            case DATES -> String.format(
                "Extract all dates from:\n%s", text);
        };
        
        return chatModel.chat(prompt);
    }
    
    public enum ExtractionType {
        EMAIL, PHONE, URL, DATES
    }
}

record ChainResult(String entities, String keywords, String sentiment) {}
record ComparisonResult(String comparison, List<String> aspects) {}
record FollowUpQuestion(int number, String question) {}
```

#### Memory Service
```java
package com.learning.langchain4j.service;

import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MemoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(MemoryService.class);
    
    private final Map<String, ConversationHistory> userHistories;
    private final ConversationalWithMemory assistant;
    
    public MemoryService() {
        this.userHistories = new ConcurrentHashMap<>();
        
        this.assistant = AiServices.builder(ConversationalWithMemory.class)
            .build();
    }
    
    public String chatWithMemory(String userId, String message) {
        ConversationHistory history = userHistories.computeIfAbsent(
            userId, k -> new ConversationHistory());
        
        history.addUserMessage(message);
        
        String context = history.buildContext();
        
        String response = assistant.chatWithContext(context + "\n\nUser: " + message);
        
        history.addAssistantMessage(response);
        
        logger.info("Chat with memory for user: {}, history size: {}", 
            userId, history.messages.size());
        
        return response;
    }
    
    public void clearHistory(String userId) {
        userHistories.remove(userId);
        logger.info("Cleared history for user: {}", userId);
    }
    
    public int getHistorySize(String userId) {
        return userHistories.getOrDefault(userId, new ConversationHistory())
            .messages.size();
    }
    
    public List<String> getConversation(String userId) {
        return userHistories.getOrDefault(userId, new ConversationHistory())
            .messages;
    }
    
    public String summarizeConversation(String userId) {
        ConversationHistory history = userHistories.get(userId);
        
        if (history == null || history.messages.isEmpty()) {
            return "No conversation history";
        }
        
        String prompt = "Summarize the following conversation in a few sentences:\n\n" +
            String.join("\n", history.messages);
        
        return prompt;
    }
    
    private static class ConversationHistory {
        private final List<String> messages = new ArrayList<>();
        
        void addUserMessage(String message) {
            messages.add("User: " + message);
        }
        
        void addAssistantMessage(String message) {
            messages.add("Assistant: " + message);
        }
        
        String buildContext() {
            if (messages.isEmpty()) {
                return "";
            }
            
            int recentMessages = Math.min(10, messages.size());
            List<String> recent = messages.subList(
                messages.size() - recentMessages, messages.size());
            
            return "Conversation history:\n" + String.join("\n", recent) + "\n\n";
        }
    }
}

interface ConversationalWithMemory {
    String chatWithContext(String contextAndMessage);
}
```

#### Document Controller
```java
package com.learning.langchain4j.controller;

import com.learning.langchain4j.model.Document;
import com.learning.langchain4j.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    
    private final DocumentService documentService;
    private final RagService ragService;
    private final ChainService chainService;
    private final MemoryService memoryService;
    
    public DocumentController(DocumentService documentService,
                              RagService ragService,
                              ChainService chainService,
                              MemoryService memoryService) {
        this.documentService = documentService;
        this.ragService = ragService;
        this.chainService = chainService;
        this.memoryService = memoryService;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            Document document = documentService.processDocument(file);
            return ResponseEntity.ok(new UploadResponse(
                document.getId(),
                document.getTitle(),
                document.getChunks().size()
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .build();
        }
    }
    
    @PostMapping("/upload-batch")
    public ResponseEntity<BatchUploadResponse> uploadDocuments(
            @RequestParam("files") MultipartFile[] files) {
        
        int successCount = 0;
        int failCount = 0;
        
        for (MultipartFile file : files) {
            try {
                documentService.processDocument(file);
                successCount++;
            } catch (IOException e) {
                failCount++;
            }
        }
        
        return ResponseEntity.ok(new BatchUploadResponse(successCount, failCount));
    }
    
    @PostMapping("/ask")
    public ResponseEntity<AskResponse> askQuestion(@RequestBody AskRequest request) {
        String answer = ragService.answerQuestion(request.question());
        
        List<FollowUpQuestion> followUps = chainService.generateFollowUpQuestions(
            answer, request.question());
        
        return ResponseEntity.ok(new AskResponse(answer, followUps));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Document>> search(@RequestParam String query) {
        List<Document> results = documentService.search(query);
        return ResponseEntity.ok(results);
    }
    
    @PostMapping("/analyze")
    public ResponseEntity<AnalyzeResponse> analyze(@RequestBody AnalyzeRequest request) {
        ChainResult result = chainService.executeAnalysisChain(request.content());
        
        return ResponseEntity.ok(new AnalyzeResponse(
            result.entities(),
            result.keywords(),
            result.sentiment()
        ));
    }
    
    @PostMapping("/summarize/{id}")
    public ResponseEntity<SummarizeResponse> summarize(@PathVariable String id) {
        String summary = ragService.generateSummary(id);
        return ResponseEntity.ok(new SummarizeResponse(summary));
    }
    
    @PostMapping("/extract")
    public ResponseEntity<ExtractResponse> extract(
            @RequestBody ExtractRequest request) {
        String extracted = chainService.executeExtractionChain(
            request.text(), 
            ChainService.ExtractionType.valueOf(request.type().toUpperCase())
        );
        
        return ResponseEntity.ok(new ExtractResponse(extracted));
    }
}

record UploadResponse(String id, String title, int chunkCount) {}
record BatchUploadResponse(int successCount, int failCount) {}
record AskRequest(String question) {}
record AskResponse(String answer, List<FollowUpQuestion> followUpQuestions) {}
record AnalyzeRequest(String content) {}
record AnalyzeResponse(String entities, String keywords, String sentiment) {}
record SummarizeResponse(String summary) {}
record ExtractRequest(String text, String type) {}
record ExtractResponse(String extracted) {}
```

### Build and Run

```bash
# Set API key
export OPENAI_API_KEY=your-api-key

# Build and run
mvn clean package
java -jar target/enterprise-document-processor-1.0.0.jar

# Upload document
curl -X POST http://localhost:8080/api/documents/upload \
  -F "file=@document.txt"

# Ask question
curl -X POST http://localhost:8080/api/documents/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "What is the main topic?"}'

# Analyze document
curl -X POST http://localhost:8080/api/documents/analyze \
  -H "Content-Type: application/json" \
  -d '{"content": "Your document text here"}'
```

### Sample Output
```json
{
  "answer": "The document discusses machine learning applications in healthcare...",
  "followUpQuestions": [
    {"number": 1, "question": "What specific ML algorithms are mentioned?"},
    {"number": 2, "question": "What are the implementation challenges?"},
    {"number": 3, "question": "What are the future prospects?"}
  ]
}
```

### Extension Ideas
1. Implement multi-modal RAG with image support
2. Add permission-based document access
3. Implement document versioning
4. Add real-time collaboration features
5. Implement custom chain components