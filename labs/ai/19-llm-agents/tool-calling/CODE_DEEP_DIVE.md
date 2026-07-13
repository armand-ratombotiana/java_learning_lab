# Tool Calling Code Deep Dive

This lab provides a pure Java simulation of a ReAct Agent loop, demonstrating how an application acts as the intermediary between the LLM and the local functions.

## 💻 Pure Java Implementation

```java file="labs/ai/19-llm-agents/tool-calling/SOLUTION/ReActAgentSim.java"
package ai.genai.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simulation of a Reasoning + Acting (ReAct) Agent loop.
 * In a real application, the "LLM" is an API call to OpenAI or Anthropic.
 */
public class ReActAgentSim {

    // --- 1. The Tools (Local Java Functions) ---
    
    public String getWeather(String location) {
        System.out.println("[SYSTEM] Executing Tool: getWeather(\"" + location + "\")");
        if (location.toLowerCase().contains("paris")) return "12C and Rainy";
        if (location.toLowerCase().contains("tokyo")) return "25C and Sunny";
        return "Unknown";
    }

    public String getStockPrice(String ticker) {
        System.out.println("[SYSTEM] Executing Tool: getStockPrice(\"" + ticker + "\")");
        if (ticker.equalsIgnoreCase("AAPL")) return "$150.00";
        return "Unknown";
    }

    // --- 2. The Agent Loop ---

    public void runAgentLoop(String userQuery) {
        System.out.println("USER: " + userQuery);
        
        List<String> conversationHistory = new ArrayList<>();
        conversationHistory.add("User: " + userQuery);
        
        int maxIterations = 5;
        for (int i = 0; i < maxIterations; i++) {
            // 1. Send history to LLM
            String llmResponse = simulateLlmCall(conversationHistory);
            System.out.println("\n[LLM]:\n" + llmResponse);
            
            // 2. Check if LLM wants to call a tool
            if (llmResponse.contains("ACTION:")) {
                // Parse the tool name and argument
                String toolResult = executeTool(llmResponse);
                
                // 3. Append observation to history and loop again
                conversationHistory.add(llmResponse);
                conversationHistory.add("OBSERVATION: " + toolResult);
                System.out.println("[OBSERVATION]: " + toolResult);
            } else {
                // 4. LLM provided the final answer. Break the loop.
                System.out.println("\n--- AGENT FINISHED ---");
                break;
            }
        }
    }

    /**
     * Parses the LLM's text output to extract the tool name and argument, then executes the local Java method.
     */
    private String executeTool(String llmResponse) {
        Pattern pattern = Pattern.compile("ACTION: (\\w+)\\((.*?)\\)");
        Matcher matcher = pattern.matcher(llmResponse);
        
        if (matcher.find()) {
            String functionName = matcher.group(1);
            String argument = matcher.group(2).replace("\"", ""); // Strip quotes
            
            return switch (functionName) {
                case "getWeather" -> getWeather(argument);
                case "getStockPrice" -> getStockPrice(argument);
                default -> "Error: Unknown tool " + functionName;
            };
        }
        return "Error: Could not parse action.";
    }

    /**
     * SIMULATOR: This mocks the behavior of a real LLM like GPT-4.
     * It looks at the conversation history and generates the next logical step.
     */
    private String simulateLlmCall(List<String> history) {
        String fullContext = String.join("\n", history);
        
        if (fullContext.contains("Paris") && !fullContext.contains("OBSERVATION")) {
            return "THOUGHT: The user wants to know the weather in Paris. I don't have real-time data, so I must use a tool.\n" +
                   "ACTION: getWeather(\"Paris\")";
        } 
        else if (fullContext.contains("OBSERVATION: 12C and Rainy")) {
            return "THOUGHT: I now have the weather data. I can construct the final answer for the user.\n" +
                   "FINAL_ANSWER: It is currently 12C and rainy in Paris. Don't forget your umbrella!";
        }
        
        return "FINAL_ANSWER: I'm sorry, I don't understand the request.";
    }

    public static void main(String[] args) {
        ReActAgentSim agent = new ReActAgentSim();
        agent.runAgentLoop("What is the weather like in Paris right now?");
    }
}
```

## 🔍 Key Takeaways
1. **The Intermediary**: The LLM *never* executes code. It just outputs text formatted as `ACTION: functionName(args)`. The Java application is the engine that parses that text, invokes the actual Java method, and feeds the result back as an `OBSERVATION`.
2. **The ReAct Loop**: If you run the `main` method, you will see the loop in action. The LLM pauses its generation to wait for the system to provide the `OBSERVATION`. Once it has the observation, it reasons again (`THOUGHT`) and decides it has enough information to provide the `FINAL_ANSWER`.
3. **LangChain4j**: In a real production application, you would not parse regex manually. Frameworks like LangChain4j handle the JSON Schema generation, the HTTP API calls to OpenAI, and the reflection required to automatically invoke your annotated Java methods (e.g., `@Tool("Get the weather") public String getWeather()`).