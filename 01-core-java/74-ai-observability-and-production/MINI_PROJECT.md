# Mini Project: Resilient & Observable AI Service

## Objective
Build a Spring Boot service that interacts with an LLM, incorporating production-grade features: metrics (observability), resilience (circuit breakers and retries), and basic PII masking.

## Prerequisites
*   OpenAI API Key (or a local Ollama instance)
*   Java 21 & Spring Boot 3.2+

## Step 1: Dependencies
Add the following to your `pom.xml`:
*   `spring-boot-starter-web`
*   `spring-boot-starter-actuator`
*   `spring-ai-openai-spring-boot-starter`
*   `resilience4j-spring-boot3`
*   `micrometer-registry-prometheus` (optional, for exporting metrics)

## Step 2: Configuration
Configure `application.yml` for Actuator, Resilience4j, and Spring AI:

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    tags:
      application: resilient-ai-service

resilience4j:
  circuitbreaker:
    instances:
      llmService:
        slidingWindowSize: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 2
  retry:
    instances:
      llmService:
        maxAttempts: 3
        waitDuration: 2s
```

## Step 3: The Resilient Service
Create a service that uses Resilience4j annotations to protect the LLM call and implements a fallback method.

```java
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ResilientAiService {

    private final ChatClient chatClient;

    public ResilientAiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Retry(name = "llmService", fallbackMethod = "fallbackResponse")
    @CircuitBreaker(name = "llmService", fallbackMethod = "fallbackResponse")
    public String generateResponse(String prompt) {
        // Basic PII Masking (e.g., masking emails)
        String sanitizedPrompt = prompt.replaceAll("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", "[REDACTED EMAIL]");
        
        return chatClient.prompt()
                .user(sanitizedPrompt)
                .call()
                .content();
    }

    // Fallback method must have the same signature + Throwable
    public String fallbackResponse(String prompt, Throwable t) {
        return "The AI service is currently unavailable. Please try again later. (Error: " + t.getMessage() + ")";
    }
}
```

## Step 4: The Controller and Observability
Create a controller to expose the endpoint and configure a `SimpleLoggerAdvisor` to demonstrate observability.

```java
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final ResilientAiService aiService;

    public AiController(ResilientAiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ask")
    public String ask(@RequestBody String question) {
        return aiService.generateResponse(question);
    }
}
```
*Note: To see the `SimpleLoggerAdvisor` in action, you can add it to the `ChatClient.Builder` in the service constructor: `builder.defaultAdvisors(new SimpleLoggerAdvisor()).build();`*

## Step 5: Testing
1.  **Test Normal Flow**: Send a POST request to `/api/ai/ask` with a valid question.
2.  **Test PII Masking**: Send a question containing an email address (e.g., "My email is test@example.com"). Verify the LLM doesn't see the email.
3.  **Test Circuit Breaker/Fallback**: Temporarily invalidate your API key or disconnect your network, then send requests. You should see the fallback response returned quickly after the circuit breaker opens.
4.  **Check Metrics**: Navigate to `http://localhost:8080/actuator/metrics` to see the available metrics, including Spring AI token usage (if configured).