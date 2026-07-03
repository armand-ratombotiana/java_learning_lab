# Mini Project: Customer Support Agent with Tools

## Objective
Build a Spring Boot application featuring an AI agent capable of answering customer support queries. The agent will use Function Calling to check order status and calculate shipping costs dynamically.

## Prerequisites
*   OpenAI API Key
*   Java 21 & Spring Boot 3.2+

## Step 1: Dependencies
Add the following to your `pom.xml`:
*   `spring-boot-starter-web`
*   `spring-ai-openai-spring-boot-starter`

## Step 2: Define the Tools (Functions)
Create Java functions that the LLM can call. Use `@Description` and `@JsonPropertyDescription` to guide the LLM.

```java
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
public class AgentTools {

    public record OrderStatusRequest(String orderId) {}
    public record OrderStatusResponse(String status, String expectedDelivery) {}

    @Bean
    @Description("Check the status of a customer order using the order ID")
    public Function<OrderStatusRequest, OrderStatusResponse> checkOrderStatus() {
        return request -> {
            // Mock database lookup
            if (request.orderId().startsWith("ORD-")) {
                return new OrderStatusResponse("Shipped", "Tomorrow");
            }
            return new OrderStatusResponse("Not Found", "N/A");
        };
    }

    public record ShippingCostRequest(String destinationZip, double weightKg) {}
    public record ShippingCostResponse(double costInUsd) {}

    @Bean
    @Description("Calculate shipping cost based on destination ZIP code and weight")
    public Function<ShippingCostRequest, ShippingCostResponse> calculateShipping() {
        return request -> {
            // Mock calculation
            double base = 5.0;
            return new ShippingCostResponse(base + (request.weightKg() * 2.5));
        };
    }
}
```

## Step 3: Create the Agent Endpoint
Configure the `ChatClient` to use the registered functions.

```java
import org.springframework.web.bind.annotation.*;
import org.springframework.ai.chat.client.ChatClient;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final ChatClient chatClient;

    public AgentController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
            .defaultSystem("You are a helpful customer support agent for an e-commerce store. Use the provided tools to assist the user.")
            .defaultFunctions("checkOrderStatus", "calculateShipping") // Register tools here
            .build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
            .user(message)
            .call()
            .content();
    }
}
```

## Step 4: Test the Agent
Run the application and test the endpoint with natural language queries:

1.  **Test Order Status**:
    `GET http://localhost:8080/api/agent/chat?message=Can you check the status of my order ORD-12345?`
    *Expected Behavior*: The LLM recognizes the intent, calls `checkOrderStatus`, Spring executes it, returns "Shipped", and the LLM formulates a polite response.

2.  **Test Shipping Calculator**:
    `GET http://localhost:8080/api/agent/chat?message=How much to ship a 4.5kg package to 90210?`
    *Expected Behavior*: The LLM calls `calculateShipping`, and responds with the calculated price.

## Extension Challenges
1.  **Structured Output**: Create a `/api/agent/summary` endpoint that takes a chat transcript and uses `BeanOutputConverter` to return a JSON object containing `sentiment` (enum), `issueResolved` (boolean), and `summary` (string).
2.  **Memory**: Implement `MessageChatMemoryAdvisor` so the agent remembers the user's `orderId` across multiple requests in the same session.