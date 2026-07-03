# Deep Dive: Advanced Prompt Engineering & AI Agents

## 1. Advanced Prompting Techniques
While basic prompting involves simply asking an LLM a question, advanced techniques structure the input to guide the model toward more accurate, logical, and structured outputs.

### Few-Shot Prompting
Providing examples (shots) within the prompt to demonstrate the desired output format or reasoning process.
*   **Zero-Shot**: "Classify this review: 'Great product!' ->"
*   **Few-Shot**: "Review: 'Terrible.' Sentiment: Negative. Review: 'Loved it.' Sentiment: Positive. Review: 'Okay.' Sentiment: ->"

### Chain of Thought (CoT)
Encouraging the model to explain its reasoning step-by-step before arriving at a final answer. This significantly improves performance on complex logic or math problems.
*   **Prompt**: "Think step-by-step to solve this problem: If John has 5 apples and gives 2 to Mary..."

### ReAct (Reason + Act)
A paradigm where the LLM interleaves reasoning traces with actions (tool calls). The model thinks about what to do, acts by calling a tool, observes the result, and then thinks about the next step.

## 2. Structured Output
In backend systems, LLMs must return data in predictable formats (usually JSON) so the application can parse and use it.
*   **Spring AI**: Uses `StructuredOutputConverter` (like `BeanOutputConverter`) to automatically generate prompt instructions that ask for JSON matching a specific Java class, and then parses the resulting JSON string back into a Java object.

## 3. Function Calling (Tools)
Function calling allows LLMs to connect to the outside world. Instead of answering a query directly, the LLM can output a JSON object indicating that a specific function should be executed with specific arguments.

### How it works in Spring AI
1.  **Define a Tool**: Create a Java `java.util.function.Function` bean and annotate it with `@Description`.
2.  **Register the Tool**: Pass the function name to the `ChatClient` options.
3.  **Execution**: When the user asks a relevant question, the LLM returns a tool call request. Spring AI intercepts this, executes the local Java function, and sends the result back to the LLM to generate the final response.

```java
@Bean
@Description("Get the current weather in a location")
public Function<WeatherRequest, WeatherResponse> weatherFunction() {
    return new WeatherService();
}

// Usage in ChatClient
chatClient.prompt()
    .user("What's the weather in London?")
    .functions("weatherFunction")
    .call()
    .content();
```

## 4. AI Agents
An AI Agent is an autonomous system powered by an LLM that can perceive its environment, make decisions, and take actions using tools to achieve a specific goal.
*   **Components**:
    *   **Brain**: The LLM (handles reasoning and planning).
    *   **Memory**: Short-term (chat history) and Long-term (Vector DB).
    *   **Tools**: Functions it can call (APIs, database queries, calculators).
*   **Frameworks**: LangChain, LangGraph, AutoGen (and conceptually implemented using Spring AI's function calling and advisors).