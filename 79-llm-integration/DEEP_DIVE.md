# Deep Dive: LLM Integration in Java

## Table of Contents
1. [Introduction](#introduction)
2. [OpenAI Integration](#openai)
3. [Anthropic Claude](#anthropic)
4. [Ollama (Local)](#ollama)
5. [Azure OpenAI](#azure)
6. [HuggingFace](#huggingface)
7. [Streaming](#streaming)
8. [Best Practices](#best-practices)

---

## 1. Introduction

Direct LLM integration provides maximum control over AI interactions. This module covers integrating with major LLM providers using Java HTTP clients.

### Provider Comparison

| Provider | Model | Strengths | Best For |
|----------|-------|-----------|----------|
| OpenAI | GPT-4, GPT-3.5 | Versatile, well-documented | General use |
| Anthropic | Claude | Long context, helpful | Complex reasoning |
| Ollama | Llama, Mistral | Local, private | Privacy, offline |
| Azure OpenAI | GPT models | Enterprise compliance | Corporate |

---

## 2. OpenAI Integration

### Using OkHttp

```java
import okhttp3.*;
import com.google.gson.*;

public class OpenAIClient {
    
    private static final String API_URL = 
        "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client;
    private final String apiKey;
    private final Gson gson;
    
    public OpenAIClient(String apiKey) {
        this.client = new OkHttpClient();
        this.apiKey = apiKey;
        this.gson = new Gson();
    }
    
    public String chat(String model, String message) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", new Object[]{
            Map.of("role", "user", "content", message)
        });
        
        Request request = new Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(
                gson.toJson(requestBody),
                MediaType.get("application/json; charset=utf-8")))
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }
            
            Map<String, Object> responseMap = gson.fromJson(
                response.body().string(), 
                Map.class
            );
            
            List<Map<String, Object>> choices = 
                (List<Map<String, Object>>) responseMap.get("choices");
            
            return (String) choices.get(0)
                .get("message");
        }
    }
}
```

### Using Java HttpClient (Java 11+)

```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JavaHttpClient {
    
    private final HttpClient httpClient;
    private final String apiKey;
    
    public JavaHttpClient(String apiKey) {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = apiKey;
    }
    
    public String chat(String model, String message) 
            throws Exception {
        
        String requestBody = String.format("""
            {
                "model": "%s",
                "messages": [{"role": "user", "content": "%s"}]
            }
            """, model, message.replace("\"", "\\\""));
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.openai.com/v1/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
        
        HttpResponse<String> response = httpClient.send(request,
            HttpResponse.BodyHandlers.ofString());
        
        // Parse response
        return extractMessage(response.body());
    }
    
    private String extractMessage(String json) {
        // Parse JSON to extract message
    }
}
```

---

## 3. Anthropic Claude Integration

```java
public class AnthropicClient {
    
    private static final String API_URL = 
        "https://api.anthropic.com/v1/messages";
    private final OkHttpClient client;
    private final String apiKey;
    
    public AnthropicClient(String apiKey) {
        this.client = new OkHttpClient();
        this.apiKey = apiKey;
    }
    
    public String chat(String message, String model) throws Exception {
        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", new Object[]{
                Map.of("role", "user", "content", message)
            },
            "max_tokens", 1024
        );
        
        Request request = new Request.Builder()
            .url(API_URL)
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(
                new Gson().toJson(requestBody),
                MediaType.get("application/json")))
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            return parseResponse(response.body().string());
        }
    }
}
```

---

## 4. Ollama (Local) Integration

```java
public class OllamaClient {
    
    private static final String BASE_URL = "http://localhost:11434";
    private final OkHttpClient client;
    
    public OllamaClient() {
        this.client = new OkHttpClient();
    }
    
    public String chat(String model, String message) throws Exception {
        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", new Object[]{
                Map.of("role", "user", "content", message)
            }
        );
        
        Request request = new Request.Builder()
            .url(BASE_URL + "/api/chat")
            .post(RequestBody.create(
                new Gson().toJson(requestBody),
                MediaType.get("application/json")))
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            return parseChatResponse(response.body().string());
        }
    }
    
    public String generate(String model, String prompt) throws Exception {
        Map<String, Object> requestBody = Map.of(
            "model", model,
            "prompt", prompt,
            "stream", false
        );
        
        Request request = new Request.Builder()
            .url(BASE_URL + "/api/generate")
            .post(RequestBody.create(
                new Gson().toJson(requestBody),
                MediaType.get("application/json")))
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            return parseGenerateResponse(response.body().string());
        }
    }
    
    public void listModels() {
        Request request = new Request.Builder()
            .url(BASE_URL + "/api/tags")
            .get()
            .build();
        // List available models
    }
}
```

---

## 5. Azure OpenAI Integration

```java
public class AzureOpenAIClient {
    
    private final String endpoint;
    private final String apiKey;
    private final String deploymentName;
    private final OkHttpClient client;
    
    public AzureOpenAIClient(
            String endpoint, 
            String apiKey, 
            String deploymentName) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
        this.deploymentName = deploymentName;
        this.client = new OkHttpClient();
    }
    
    public String chat(String message) throws Exception {
        String url = String.format(
            "%s/openai/deployments/%s/chat/completions?api-version=2024-02-01",
            endpoint, deploymentName);
        
        Map<String, Object> requestBody = Map.of(
            "messages", new Object[]{
                Map.of("role", "user", "content", message)
            }
        );
        
        Request request = new Request.Builder()
            .url(url)
            .addHeader("api-key", apiKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(
                new Gson().toJson(requestBody),
                MediaType.get("application/json")))
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            return extractContent(response.body().string());
        }
    }
}
```

---

## 6. HuggingFace Integration

```java
public class HuggingFaceClient {
    
    private static final String API_URL = 
        "https://api-inference.huggingface.co/models";
    private final OkHttpClient client;
    private final String apiKey;
    
    public HuggingFaceClient(String apiKey) {
        this.client = new OkHttpClient();
        this.apiKey = apiKey;
    }
    
    public String chat(String model, String message) throws Exception {
        // Use text-generation endpoint
        String url = API_URL + "/" + model;
        
        Map<String, Object> requestBody = Map.of(
            "inputs", message,
            "parameters", Map.of(
                "max_new_tokens", 250,
                "temperature", 0.7
            )
        );
        
        Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(
                new Gson().toJson(requestBody),
                MediaType.get("application/json")))
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            return parseResponse(response.body().string());
        }
    }
}
```

---

## 7. Streaming Responses

### Server-Sent Events (SSE)

```java
import okhttp3.*;
import java.io.IOException;

public class StreamingClient {
    
    public void streamChat(String model, String message) 
            throws IOException {
        
        OkHttpClient client = new OkHttpClient.Builder()
            .build();
        
        String json = String.format("""
            {"model": "%s", "messages": [{"role": "user", "content": "%s"}], 
             "stream": true}
            """, model, message.replace("\"", "\\\""));
        
        Request request = new Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"))
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(json, MediaType.get("application/json")))
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }
            
            BufferedSource source = response.body().source();
            
            while (!source.exhausted()) {
                String line = source.readUtf8Line();
                if (line.startsWith("data: ")) {
                    String data = line.substring(6);
                    if ("[DONE]".equals(data)) break;
                    
                    // Parse and extract content
                    processStreamChunk(data);
                }
            }
        }
    }
    
    private void processStreamChunk(String data) {
        // Parse JSON and extract delta content
    }
}
```

---

## 8. Best Practices

### Error Handling

```java
public class ResilientLLMClient {
    
    public String chatWithRetry(String message, int maxRetries) {
        int attempt = 0;
        Exception lastException = null;
        
        while (attempt < maxRetries) {
            try {
                return chat(message);
            } catch (Exception e) {
                lastException = e;
                attempt++;
                
                if (isRetryable(e)) {
                    try {
                        Thread.sleep(calculateBackoff(attempt));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        
        throw new RuntimeException("Failed after " + maxRetries + 
            " attempts", lastException);
    }
    
    private boolean isRetryable(Exception e) {
        return e instanceof IOException ||
               e instanceof RateLimitException;
    }
    
    private long calculateBackoff(int attempt) {
        return (long) Math.pow(2, attempt) * 1000;
    }
}
```

### Rate Limiting

```java
public class RateLimitedClient {
    
    private final AtomicInteger requestCount = 
        new AtomicInteger(0);
    private Instant windowStart = Instant.now();
    private final int maxRequestsPerMinute = 60;
    
    public void acquire() {
        Instant now = Instant.now();
        
        if (Duration.between(windowStart, now).getSeconds() >= 60) {
            requestCount.set(0);
            windowStart = now;
        }
        
        if (requestCount.incrementAndGet() > maxRequestsPerMinute) {
            throw new RateLimitException("Rate limit exceeded");
        }
    }
}
```

---

## Summary

Java LLM integration with HTTP clients:

1. **OkHttp**: Popular, feature-rich HTTP client
2. **Java HttpClient**: Built-in since Java 11
3. **Gson**: JSON parsing
4. **SSE**: Streaming responses

Key patterns: Error handling, rate limiting, retries

---

*Continue to QUIZZES.md and EDGE_CASES.md.*