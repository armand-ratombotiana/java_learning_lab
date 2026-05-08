# Deep Dive: LangChain4j - Building LLM-Powered Applications in Java

## Table of Contents
1. [Introduction to LangChain4j](#introduction)
2. [Core Concepts](#core-concepts)
3. [Architecture Overview](#architecture-overview)
4. [Detailed Implementation Guide](#detailed-implementation)
5. [Advanced Patterns](#advanced-patterns)
6. [Real-World Applications](#real-world-applications)
7. [Performance Optimization](#performance-optimization)
8. [Security Considerations](#security-considerations)

---

## 1. Introduction to LangChain4j

LangChain4j is a Java library that brings the power of Large Language Models (LLMs) to the Java ecosystem. It provides a clean, fluent API for building AI-powered applications without requiring deep knowledge of the underlying LLM APIs.

### What Makes LangChain4j Special?

LangChain4j distinguishes itself from other LLM libraries through several key features:

- **Unified API**: Single interface for multiple LLM providers (OpenAI, Anthropic, Ollama, etc.)
- **Type Safety**: Full Java type system integration with compile-time checks
- **Modular Architecture**: Composable components for chat memory, prompts, tools, and RAG
- **Production-Ready**: Built-in error handling, retry logic, and streaming support
- **Active Development**: Open-source project with regular updates and community contributions

### Why Java for LLM Applications?

While Python dominates the AI/ML space, Java offers significant advantages for enterprise applications:

- **Enterprise Integration**: Seamless integration with existing Java ecosystems (Spring, Jakarta EE)
- **Performance**: Better throughput for high-volume applications
- **Type Safety**: Catch errors at compile time rather than runtime
- **Mature Tooling**: Extensive debugging, profiling, and testing frameworks
- **Deployment**: Robust containerization and orchestration (Kubernetes)

---

## 2. Core Concepts

### 2.1 Language Models (ChatModels)

The foundation of LangChain4j is the `ChatModel` interface, which abstracts away the differences between LLM providers.

```java
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.ChatModels;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class BasicChatExample {
    public static void main(String[] args) {
        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName("gpt-4")
            .temperature(0.7)
            .maxTokens(1000)
            .build();
        
        String response = model.chat("What is the capital of France?");
        System.out.println(response);
    }
}
```

**Key Concepts:**
- `ChatLanguageModel`: Interface for chat-based language models
- `OpenAiChatModel`: Implementation for OpenAI's GPT models
- `temperature`: Controls randomness (0 = deterministic, 2 = very creative)
- `maxTokens`: Maximum tokens in the response
- `topP`: Alternative to temperature for controlling output distribution

### 2.2 Prompt Templates

Prompt templates allow dynamic prompt construction with variables, making applications flexible and maintainable.

```java
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public class PromptTemplateExample {
    
    interface Assistant {
        @SystemMessage("You are a {{language}} programming assistant.")
        @UserMessage("Explain {{concept}} in simple terms.")
        String explain(String language, String concept);
    }
    
    public static void main(String[] args) {
        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();
        
        Assistant assistant = AiServices.builder(Assistant.class)
            .chatLanguageModel(model)
            .build();
        
        String explanation = assistant.explain("Java", "polymorphism");
        System.out.println(explanation);
    }
}
```

**Template Variables:**
- `{{variable}}`: Single curly braces for single-value substitution
- `{{#each items}}...{{/each}}`: Loop constructs for list processing
- Conditional logic with `{{#if condition}}...{{/if}}`

### 2.3 Chat Memory

Chat memory enables conversational context preservation across multiple interactions.

```java
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.chatMemory.ChatMemory;
import dev.langchain4j.service.chat_memory.InMemoryChatMemory;

public class ChatMemoryExample {
    
    interface Assistant {
        String chat(String userMessage);
    }
    
    public static void main(String[] args) {
        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();
        
        ChatMemory chatMemory = InMemoryChatMemory.builder()
            .maxMessages(10)
            .build();
        
        Assistant assistant = AiServices.builder(Assistant.class)
            .chatLanguageModel(model)
            .chatMemory(chatMemory)
            .build();
        
        // First interaction
        String response1 = assistant.chat("My name is John");
        System.out.println("Bot: " + response1);
        
        // Second interaction - model remembers the name
        String response2 = assistant.chat("What's my name?");
        System.out.println("Bot: " + response2);
    }
}
```

**Memory Types:**
- `InMemoryChatMemory`: Simple in-memory storage (not persistent)
- `MessageWindowChatMemory`: Fixed-size window of recent messages
- `TokenWindowChatMemory`: Token-based memory management
- Custom implementations for database-backed storage

### 2.4 Embeddings and Vector Stores

Embeddings convert text into numerical vectors, enabling semantic similarity searches.

```java
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.data.embedding.Embedding;

public class EmbeddingExample {
    public static void main(String[] args) {
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName("text-embedding-ada-002")
            .build();
        
        // Create embeddings
        Embedding embedding1 = embeddingModel.embed("Java is a programming language").content();
        Embedding embedding2 = embeddingModel.embed("Python is a programming language").content();
        
        // Store in embedding store
        EmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
        store.add(embedding1, TextSegment.from("Java is a programming language"));
        store.add(embedding2, TextSegment.from("Python is a programming language"));
        
        // Search similar documents
        Embedding queryEmbedding = embeddingModel.embed("Tell me about Java").content();
        List<EmbeddingMatch<TextSegment>> matches = store.findRelevant(queryEmbedding, 3);
        
        matches.forEach(match -> 
            System.out.println("Score: " + match.score() + 
                ", Text: " + match.embedded().text()));
    }
}
```

### 2.5 Retrieval-Augmented Generation (RAG)

RAG combines document retrieval with LLM generation for accurate, context-aware responses.

```java
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;

public class RAGExample {
    
    interface DocumentAssistant {
        String answer(String question);
    }
    
    public static void main(String[] args) {
        // Load documents
        List<Document> documents = FileSystemDocumentLoader
            .loadDocuments(Path.of("src/main/resources/documents"));
        
        // Split into chunks
        DocumentSplitter splitter = DocumentSplitters
            .byParagraph(100, 10);
        List<TextSegment> segments = splitter.split(documents);
        
        // Create embeddings and store
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();
        
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        segments.forEach(segment -> {
            Embedding embedding = embeddingModel.embed(segment.text()).content();
            embeddingStore.add(embedding, segment);
        });
        
        // Build RAG assistant
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();
        
        RetrievalAugmentor augmentor = DefaultRetrievalAugmentor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .build();
        
        DocumentAssistant assistant = AiServices.builder(DocumentAssistant.class)
            .chatLanguageModel(chatModel)
            .retrievalAugmentor(augmentor)
            .build();
        
        String answer = assistant.answer("What is the main topic?");
        System.out.println(answer);
    }
}
```

---

## 3. Architecture Overview

### 3.1 System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Application Layer                                  │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    AiServices (Declarative API)                      │   │
│  │   @SystemMessage | @UserMessage | @Tool | @ChatMemory              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            Service Layer                                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  ChatService │  │  Embedding   │  │  Retrieval   │  │    Tool      │  │
│  │              │  │    Service   │  │   Service    │  │   Executor   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Model Layer                                        │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                      LangChain4j Core                                 │  │
│  │   ChatModel │ EmbeddingModel │ EmbeddingStore │ ContentRetriever    │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         Provider Layer                                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │  OpenAI  │  │Anthropic │  │  Ollama  │  │ Hugging  │  │  Azure   │  │
│  │          │  │  Claude  │  │          │  │   Face   │  │  OpenAI  │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Data Flow for RAG Pipeline

```
User Query
    │
    ▼
┌─────────────────────┐
│  Query Embedding    │  EmbeddingModel.embed(query)
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│  Vector Search      │  EmbeddingStore.findRelevant()
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│  Context Retrieval  │  Get top-k relevant documents
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│  Prompt Augmentation│  Combine query + context
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│  LLM Generation     │  ChatModel.chat(prompt)
└─────────────────────┘
    │
    ▼
Response to User
```

### 3.3 Component Interaction Diagram

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│    User      │────▶│  AI Service  │────▶│  ChatModel   │
└──────────────┘     └──────────────┘     └──────────────┘
                           │                        │
                           │                        ▼
                     ┌──────────────┐     ┌──────────────┐
                     │ ChatMemory   │     │ LLM Provider │
                     │              │     │ (OpenAI,     │
                     └──────────────┘     │ Anthropic)   │
                           │              └──────────────┘
                           ▼
                     ┌──────────────┐
                     │  Tools       │
                     │ (if needed)  │
                     └──────────────┘
```

---

## 4. Detailed Implementation Guide

### 4.1 Setting Up LangChain4j in Your Project

**Maven Dependencies:**

```xml
<dependencies>
    <!-- Core LangChain4j -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j</artifactId>
        <version>0.35.0</version>
    </dependency>
    
    <!-- OpenAI Integration -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-open-ai</artifactId>
        <version>0.35.0</version>
    </dependency>
    
    <!-- Embedding Models -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-embeddings</artifactId>
        <version>0.35.0</version>
    </dependency>
    
    <!-- Document Processing -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-document-loader-html</artifactId>
        <version>0.35.0</version>
    </dependency>
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-document-loader-pdf</artifactId>
        <version>0.35.0</version>
    </dependency>
    
    <!-- In-Memory Embedding Store (for development) -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-embeddings-all-minilm-l6-v2</artifactId>
        <version>0.35.0</version>
    </dependency>
</dependencies>
```

### 4.2 Building a Production-Ready Chatbot

```java
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.chatmemory.ChatMemory;
import dev.langchain4j.service.chatmemory.MessageWindowChatMemory;

public class ProductionChatbot {
    
    interface CustomerSupportBot {
        @SystemMessage("""
            You are a professional customer support agent for a SaaS company.
            Your role is to:
            1. Greet customers warmly and professionally
            2. Understand their issue by asking clarifying questions
            3. Provide accurate solutions based on your knowledge base
            4. Escalate complex issues to human agents when necessary
            
            Guidelines:
            - Be empathetic and patient
            - Use simple, clear language
            - Don't make up information
            - Always confirm if the customer is satisfied
            """)
        String chat(String userMessage);
    }
    
    public static void main(String[] args) {
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName("gpt-4")
            .temperature(0.3) // Lower temperature for consistent responses
            .maxTokens(500)
            .timeout(Duration.ofSeconds(30))
            .maxRetries(3)
            .build();
        
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
            .maxMessages(20)
            .build();
        
        CustomerSupportBot bot = AiServices.builder(CustomerSupportBot.class)
            .chatLanguageModel(chatModel)
            .chatMemory(chatMemory)
            .build();
        
        // Simulate conversation
        System.out.println("Customer: I can't log into my account");
        String response1 = bot.chat("I can't log into my account");
        System.out.println("Bot: " + response1);
        
        System.out.println("\nCustomer: I've tried resetting but no email arrived");
        String response2 = bot.chat("I've tried resetting but no email arrived");
        System.out.println("Bot: " + response2);
    }
}
```

### 4.3 Multi-Modal RAG Implementation

```java
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.pdf.PdfDocumentParser;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;

public class MultiModalRAG {
    
    interface ResearchAssistant {
        String answer(String question);
    }
    
    public static void main(String[] args) throws Exception {
        // Load multiple document types
        List<Path> pdfFiles = Files.walk(Path.of("docs"))
            .filter(p -> p.toString().endsWith(".pdf"))
            .collect(Collectors.toList());
        
        PdfDocumentParser pdfParser = new PdfDocumentParser();
        TextDocumentParser textParser = new TextDocumentParser();
        
        List<Document> allDocuments = new ArrayList<>();
        
        for (Path pdf : pdfFiles) {
            List<Document> docs = FileSystemDocumentLoader
                .loadDocuments(pdf, pdfParser);
            allDocuments.addAll(docs);
        }
        
        // Ingest documents into embedding store
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .build();
        
        ingestor.ingest(allDocuments);
        
        // Build RAG assistant
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();
        
        RetrievalAugmentor augmentor = DefaultRetrievalAugmentor.builder()
            .contentRetriever(EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .build())
            .build();
        
        ResearchAssistant assistant = AiServices.builder(ResearchAssistant.class)
            .chatLanguageModel(chatModel)
            .retrievalAugmentor(augmentor)
            .build();
        
        String answer = assistant.answer(
            "What are the key findings about machine learning in the documents?");
        System.out.println(answer);
    }
}
```

### 4.4 Tool Use and Agents

LangChain4j allows LLMs to use tools for extended capabilities:

```java
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.Tool;

public class ToolExample {
    
    static class Calculator {
        @Tool("Calculates the arithmetic operation")
        double calculate(String expression) {
            // Simple evaluation (in production, use proper parser)
            StringTokenizer tokenizer = new StringTokenizer(expression, " +-*/", true);
            // Implementation...
            return 0.0;
        }
        
        @Tool("Converts temperature between Celsius and Fahrenheit")
        double convertTemperature(double value, String fromUnit, String toUnit) {
            if ("C".equals(fromUnit) && "F".equals(toUnit)) {
                return (value * 9/5) + 32;
            } else if ("F".equals(fromUnit) && "C".equals(toUnit)) {
                return (value - 32) * 5/9;
            }
            return value;
        }
    }
    
    interface AssistantWithTools {
        @SystemMessage("You are a helpful assistant with access to tools.")
        String chat(String userMessage);
    }
    
    public static void main(String[] args) {
        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();
        
        Calculator calculator = new Calculator();
        
        AssistantWithTools assistant = AiServices.builder(AssistantWithTools.class)
            .chatLanguageModel(model)
            .tools(calculator)
            .build();
        
        String response = assistant.chat(
            "What is 25°C in Fahrenheit?");
        System.out.println(response);
    }
}
```

---

## 5. Advanced Patterns

### 5.1 Streaming Responses

For better user experience, stream responses token-by-token:

```java
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

public class StreamingExample {
    public static void main(String[] args) {
        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName("gpt-4")
            .build();
        
        model.chat("Write a short story about a robot", response -> {
            System.out.print(response.token().text());
            System.out.flush();
        });
        
        // Wait for completion
        Thread.sleep(10000);
    }
}
```

### 5.2 Structured Output

Generate type-safe responses:

```java
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.data.document.Document;

public class StructuredOutputExample {
    
    record ProductReview(
        String productName,
        int rating,
        String sentiment,
        List<String> pros,
        List<String> cons,
        String summary
    ) {}
    
    interface ReviewAnalyzer {
        @UserMessage("Analyze this review: {{review}}")
        ProductReview analyzeReview(String review);
    }
    
    public static void main(String[] args) {
        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();
        
        ReviewAnalyzer analyzer = AiServices.builder(ReviewAnalyzer.class)
            .chatLanguageModel(model)
            .build();
        
        ProductReview review = analyzer.analyzeReview(
            "The new iPhone is amazing! Great camera, fast processor, " +
            "but the battery life could be better. Overall, highly recommend!");
        
        System.out.println("Product: " + review.productName());
        System.out.println("Rating: " + review.rating() + "/5");
        System.out.println("Sentiment: " + review.sentiment());
    }
}
```

### 5.3 Custom Content Retrievers

Implement domain-specific retrieval:

```java
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.data.embedding.Embedding;

public class CustomRetriever {
    
    public static ContentRetriever createCustomRetriever(
            EmbeddingStore<TextSegment> store,
            EmbeddingModel embeddingModel) {
        
        return EmbeddingStoreContentRetriever.builder()
            .embeddingStore(store)
            .embeddingModel(embeddingModel)
            .maxResults(10)
            .minScore(0.7) // Minimum similarity threshold
            .build();
    }
}
```

### 5.4 Error Handling and Resilience

```java
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.chat.common.CommonChatLanguageModel;

public class ResilientChatModel {
    
    public static ChatLanguageModel createResilientModel() {
        // Add retry logic and fallback
        return CommonChatLanguageModel.builder()
            .delegatee(OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build())
            .build();
    }
}
```

---

## 6. Real-World Applications

### 6.1 Enterprise Knowledge Base Chatbot

```java
public class EnterpriseKnowledgeBase {
    
    interface KBAssistant {
        @SystemMessage("""
            You are an enterprise knowledge base assistant.
            Use the provided documents to answer questions accurately.
            If information is not in the documents, say so clearly.
            Cite sources when possible.
            """)
        String answer(String question);
    }
    
    public static void main(String[] args) throws Exception {
        // Load company documents
        Path docsPath = Path.of("company-docs");
        List<Document> documents = FileSystemDocumentLoader.loadDocuments(docsPath);
        
        // Process and index
        DocumentSplitter splitter = DocumentSplitters.byParagraph(500, 50);
        List<TextSegment> segments = splitter.split(documents);
        
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        EmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
        
        for (TextSegment segment : segments) {
            Embedding embedding = embeddingModel.embed(segment.text()).content();
            store.add(embedding, segment);
        }
        
        // Build assistant
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();
        
        RetrievalAugmentor augmentor = DefaultRetrievalAugmentor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(store)
            .build();
        
        KBAssistant assistant = AiServices.builder(KBAssistant.class)
            .chatLanguageModel(chatModel)
            .retrievalAugmentor(augmentor)
            .build();
        
        // Answer questions
        String answer = assistant.answer("What is our vacation policy?");
        System.out.println(answer);
    }
}
```

### 6.2 Code Review Assistant

```java
public class CodeReviewAssistant {
    
    interface CodeReviewer {
        @SystemMessage("""
            You are an expert code reviewer specializing in Java.
            Review the provided code for:
            1. Security vulnerabilities
            2. Performance issues
            3. Code quality and best practices
            4. Potential bugs
            
            Provide specific, actionable feedback.
            """)
        @UserMessage("Review this code:\n{{code}}")
        String reviewCode(String code);
    }
    
    public static void main(String[] args) {
        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .temperature(0.2) // More deterministic for code review
            .build();
        
        CodeReviewer reviewer = AiServices.builder(CodeReviewer.class)
            .chatLanguageModel(model)
            .build();
        
        String code = """
            public class UserService {
                public User getUser(String id) {
                    return database.query("SELECT * FROM users WHERE id = " + id);
                }
            }
            """;
        
        String review = reviewer.reviewCode(code);
        System.out.println(review);
    }
}
```

### 6.3 Data Analysis Assistant

```java
public class DataAnalysisAssistant {
    
    interface DataAnalyzer {
        @SystemMessage("""
            You are a data analysis assistant. Help users understand their data,
            suggest appropriate analyses, and interpret results.
            """)
        String analyze(String question);
    }
    
    public static void main(String[] args) {
        // Use tools for data operations
        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();
        
        DataAnalyzer analyzer = AiServices.builder(DataAnalyzer.class)
            .chatLanguageModel(model)
            .build();
        
        String result = analyzer.analyze(
            "What patterns exist in our sales data?");
    }
}
```

---

## 7. Performance Optimization

### 7.1 Caching Strategies

```java
import dev.langchain4j.model.chat.CachingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.cache.InMemoryChatCache;

public class CachedChatModel {
    
    public static ChatLanguageModel createCachedModel() {
        return CachingChatLanguageModel.builder()
            .delegatee(OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build())
            .cache(InMemoryChatCache.builder()
                .maxCacheSize(1000)
                .build())
            .build();
    }
}
```

### 7.2 Batch Processing

```java
public class BatchProcessing {
    
    public static void processBatch(
            List<String> inputs,
            ChatLanguageModel model,
            int batchSize) {
        
        for (int i = 0; i < inputs.size(); i += batchSize) {
            List<String> batch = inputs.subList(
                i, Math.min(i + batchSize, inputs.size()));
            
            batch.parallelStream().forEach(input -> {
                String response = model.chat(input);
                // Process response
            });
        }
    }
}
```

### 7.3 Async Operations

```java
import java.util.concurrent.CompletableFuture;

public class AsyncChatModel {
    
    public static CompletableFuture<String> asyncChat(
            ChatLanguageModel model,
            String message) {
        
        return CompletableFuture.supplyAsync(() -> model.chat(message));
    }
}
```

---

## 8. Security Considerations

### 8.1 API Key Management

```java
public class SecureConfig {
    
    // Never hardcode API keys
    // Use environment variables or secure vault
    
    public static String getApiKey() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException(
                "OPENAI_API_KEY environment variable not set");
        }
        return apiKey;
    }
}
```

### 8.2 Input Sanitization

```java
public class InputSanitizer {
    
    public static String sanitize(String input) {
        if (input == null) return "";
        
        return input
            .replaceAll("[<>\"']", "") // Remove potential HTML/script
            .replaceAll("\\s+", " ")   // Normalize whitespace
            .trim();
    }
}
```

### 8.3 Rate Limiting

```java
public class RateLimitedChatModel implements ChatLanguageModel {
    
    private final ChatLanguageModel delegate;
    private final RateLimiter rateLimiter;
    
    public RateLimitedChatModel(ChatLanguageModel delegate) {
        this.delegate = delegate;
        this.rateLimiter = new TokenBucketRateLimiter(50, Duration.ofMinutes(1));
    }
    
    @Override
    public String chat(String userMessage) {
        rateLimiter.acquire();
        return delegate.chat(userMessage);
    }
}
```

---

## Key Concepts Summary

| Concept | Description |
|---------|-------------|
| ChatModel | Interface for LLM interaction |
| AiServices | Declarative AI service builder |
| ChatMemory | Conversation context management |
| EmbeddingModel | Text-to-vector conversion |
| EmbeddingStore | Vector storage and retrieval |
| RetrievalAugmentor | RAG pipeline orchestration |
| PromptTemplate | Dynamic prompt construction |
| Tool | Extend LLM capabilities |
| StreamingChatModel | Token-by-token response streaming |
| Structured Output | Type-safe response generation |

---

## Additional Resources

- [LangChain4j Official Documentation](https://docs.langchain4j.dev/)
- [LangChain4j GitHub Repository](https://github.com/langchain4j/langchain4j)
- [OpenAI API Documentation](https://platform.openai.com/docs)
- [RAG Architecture Patterns](https://arxiv.org/abs/2312.10997)
- [Prompt Engineering Guide](https://www.promptingguide.ai/)

---

*This deep dive provides foundational knowledge for building production-ready LLM applications with LangChain4j. Continue to QUIZZES.md for assessment and EDGE_CASES.md for practical debugging guidance.*