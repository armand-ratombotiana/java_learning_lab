# LLM Integration Projects - Module 79

This module covers LLM patterns including RAG, fine-tuning, prompt engineering, and integration patterns.

## Mini-Project: LLM Chat Integration (2-4 hours)

### Overview
Build a Java application integrating with LLMs for chat, prompt engineering, and structured output.

### Project Structure
```
llm-integration-mini/
├── pom.xml
├── src/main/java/com/learning/llmintegration/
│   ├── MiniProjectApplication.java
│   ├── service/
│   │   ├── LlmChatService.java
│   │   └── PromptEngineeringService.java
│   └── config/
│       └── LlmConfig.java
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
    <artifactId>llm-integration-mini</artifactId>
    <version>1.0.0</version>
    <name>LLM Integration Mini Project</name>
    
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
    </dependencies>
</project>
```

#### LLM Chat Service
```java
package com.learning.llmintegration.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class LlmChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(LlmChatService.class);
    
    private final ChatLanguageModel chatModel;
    
    public LlmChatService(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }
    
    public String chat(String message) {
        logger.info("Processing chat message");
        
        return chatModel.chat(message);
    }
    
    public String chatWithSystem(String system, String userMessage) {
        String prompt = system + "\n\nUser: " + userMessage;
        
        return chatModel.chat(prompt);
    }
    
    public Map<String, String> extractStructured(String text, String schema) {
        String prompt = String.format("""
            Extract information from the following text according to this schema:
            Schema: %s
            
            Text: %s
            
            Return as JSON.
            """, schema, text);
        
        String response = chatModel.chat(prompt);
        
        return parseJson(response);
    }
    
    private Map<String, String> parseJson(String json) {
        Map<String, String> result = new HashMap<>();
        
        return result;
    }
}
```

### Build and Run

```bash
export OPENAI_API_KEY=your-key

mvn clean package
java -jar target/llm-integration-mini-1.0.0.jar
```

---

## Real-World Project: Enterprise RAG with LLM (8+ hours)

### Project Structure
```
llm-integration-production/
├── pom.xml
├── src/main/java/com/learning/llmintegration/
│   ├── ProductionApplication.java
│   ├── rag/
│   │   ├── RagService.java
│   │   └── DocumentProcessor.java
│   ├── service/
│   │   ├── PromptService.java
│   │   └── ChainService.java
│   ├── controller/
│   │   └── LlmController.java
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
    <artifactId>llm-integration-production</artifactId>
    <version>1.0.0</version>
    <name>LLM Integration Production</name>
    
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
    </dependencies>
</project>
```

#### RAG Service
```java
package com.learning.llmintegration.rag;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RagService {
    
    private static final Logger logger = LoggerFactory.getLogger(RagService.class);
    
    private final ChatLanguageModel chatModel;
    private final Map<String, String> documentStore;
    
    public RagService(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        this.documentStore = new HashMap<>();
    }
    
    public void addDocument(String id, String content) {
        documentStore.put(id, content);
        logger.info("Added document: {}", id);
    }
    
    public String answer(String question) {
        logger.info("Processing question: {}", question);
        
        String context = retrieveRelevantContext(question);
        
        String prompt = String.format("""
            Based on the following context, answer the question.
            
            Context:
            %s
            
            Question: %s
            """, context, question);
        
        return chatModel.chat(prompt);
    }
    
    private String retrieveRelevantContext(String query) {
        if (documentStore.isEmpty()) {
            return "No documents available.";
        }
        
        return String.join("\n\n", documentStore.values().stream().limit(3).toList());
    }
    
    public String answerWithCitations(String question) {
        String answer = answer(question);
        
        return answer + "\n\n[Sources: docs in index]";
    }
}
```

#### Chain Service
```java
package com.learning.llmintegration.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChainService {
    
    private final ChatLanguageModel chatModel;
    
    public ChainService(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }
    
    public String executeChain(String input, List<String> steps) {
        String currentInput = input;
        
        for (String step : steps) {
            String prompt = String.format("Step: %s\nInput: %s", step, currentInput);
            currentInput = chatModel.chat(prompt);
        }
        
        return currentInput;
    }
    
    public Map<String, String> parallelProcessing(String input, List<String> operations) {
        Map<String, String> results = new HashMap<>();
        
        for (String operation : operations) {
            String prompt = String.format("Operation: %s\nInput: %s", operation, input);
            results.put(operation, chatModel.chat(prompt));
        }
        
        return results;
    }
}
```

### Build and Run

```bash
export OPENAI_API_KEY=your-key

mvn clean package
java -jar target/llm-integration-production-1.0.0.jar

curl -X POST http://localhost:8080/api/llm/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "What is Java?"}'
```

### Extension Ideas
1. Add fine-tuning integration
2. Implement agent framework
3. Add multi-modal support
4. Implement conversation memory
5. Add tool use patterns