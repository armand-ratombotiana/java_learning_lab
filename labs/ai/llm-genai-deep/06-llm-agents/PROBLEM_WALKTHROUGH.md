# PROBLEM WALKTHROUGH: Tool-Using LLM Agent

## Problem Statement

**Difficulty: Hard | Category: LLM Agents / ReAct**

Implement a ReAct-style (Reasoning + Acting) agent that can understand a task, select appropriate tools, execute them, and incorporate observations into its reasoning loop. The agent should support dynamic tool discovery, multi-step reasoning, and error recovery.

**Interview Context:** LLM agents are the most hyped area in AI (2023-2026). Interviewers want to see your understanding of the ReAct loop, tool calling protocols, observation parsing, and how to handle the failure modes of autonomous agents.

### Requirements

1. **Tool Registry:** Maintain a registry of available tools with name, description, and parameter schema.
2. **ReAct Loop:** Implement the Thought → Action → Observation cycle (max N steps).
3. **Tool Execution:** Call tools with parsed arguments and return structured observations.
4. **Error Handling:** Detect failed tool calls, retry or skip.
5. **Memory:** Maintain conversation/action history for multi-step reasoning.
6. **Stopping:** Terminate when the agent produces a final answer or reaches max steps.

### Input/Output Contract

```
Input:  Task description (string), tool registry,
        max_steps=10, stop_condition="final_answer"
Output: Final answer with trace of all thoughts, actions, and observations
```

---

## Step-by-Step Solution Walkthrough

### 1. The ReAct Paradigm

ReAct (Yao et al., 2023) combines reasoning traces with action execution:

```
Thought: I need to find the population of Paris.
         I can use the search tool to look up this information.
Action: search("Population of Paris")
Observation: Paris has a population of 2.16 million.
Thought: I have the information. The answer is 2.16 million.
Action: finish("Paris has a population of 2.16 million.")
```

The key insight is that the **thought** helps the LLM reason about what to do next, while the **action** interacts with the external world.

### 2. Tool Registry Design

Each tool has:
- **Name:** Unique identifier (snake_case).
- **Description:** What the tool does, when to use it.
- **Parameters:** JSON schema describing required/optional arguments.
- **Function:** The actual implementation or API call.

The LLM sees the tools as a list of descriptions and chooses which to call.

### 3. The Observed Failure Modes of Agents

1. **Infinite loops:** Agent keeps calling the same tool with the same arguments.
2. **Hallucinated actions:** Agent invents tools that don't exist.
3. **Stuck in reasoning:** Agent keeps "thinking" without taking action.
4. **Context overflow:** Too many thoughts/actions exceed the context window.
5. **Tool errors:** API failures, network errors, invalid arguments.

Our implementation must handle all of these.

### 4. Action Parsing

The LLM outputs structured actions that must be parsed:

```
Action: search(query="Paris population")
Action: calculator(expression="2.16 * 1000000")
Action: finish(answer="The population is 2,160,000.")
```

We use a regex pattern to parse: `Action: tool_name(arg1="value1", arg2="value2")`.

### 5. Observation Processing

After a tool executes, the observation is:
1. Added to the message history.
2. Used by the LLM to generate the next thought.

Observations should be concise to avoid blowing up the context. Long outputs should be summarized.

### 6. Stopping Conditions

The agent stops when:
1. It outputs `Action: finish(...)`.
2. It reaches `max_steps`.
3. The LLM output doesn't contain a valid action (assumed done).
4. An unrecoverable error occurs.

### 7. The ReAct Prompt Template

The LLM is prompted with a structured description of available tools:

```
You are an agent with access to these tools:

- search(query: str): Search the web for information.
- calculator(expression: str): Evaluate a mathematical expression.

To use a tool, output:
Action: tool_name(arg1="value1", arg2="value2")

After each action, you will receive an observation.
When you have the answer, output:
Action: finish(answer="Your final answer here")

Begin!

Task: {{task}}
Thought:
```

### 8. Loop Detection

To prevent infinite loops, maintain a set of (action, observation) pairs seen so far. If an identical pair repeats, force the agent to try a different approach or terminate.

---

## Java Implementation

```java
package com.llm.genai.deep.agent;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implements a ReAct-style (Reasoning + Acting) LLM agent that dynamically
 * selects and executes tools to accomplish a task through multi-step reasoning.
 * <p>
 * The agent follows a Thought → Action → Observation loop, maintaining
 * conversation history and handling errors gracefully.
 */
public class LLMAgent {

    private final Map<String, Tool> toolRegistry;
    private final int maxSteps;
    private final LLMInterface llm;

    /**
     * Represents a callable tool with name, description, parameter schema, and implementation.
     */
    public static class Tool {
        public final String name;
        public final String description;
        public final List<Parameter> parameters;
        public final Function<Map<String, String>, String> executor;

        /**
         * Defines a tool parameter.
         */
        public static class Parameter {
            public final String name;
            public final String type;
            public final boolean required;

            public Parameter(String name, String type, boolean required) {
                this.name = name;
                this.type = type;
                this.required = required;
            }
        }

        /**
         * Constructs a tool.
         *
         * @param name        tool identifier (snake_case)
         * @param description description of when/why to use this tool
         * @param parameters  list of parameter definitions
         * @param executor    function implementing the tool logic
         */
        public Tool(String name, String description,
                    List<Parameter> parameters,
                    Function<Map<String, String>, String> executor) {
            this.name = name;
            this.description = description;
            this.parameters = parameters;
            this.executor = executor;
        }

        /**
         * Returns the tool's signature suitable for the prompt.
         */
        public String getSignature() {
            String params = parameters.stream()
                    .map(p -> p.name + ": " + p.type + (p.required ? " (required)" : " (optional)"))
                    .collect(Collectors.joining(", "));
            return "- " + name + "(" + params + "): " + description;
        }
    }

    /**
     * Interface for the underlying LLM. In production, this wraps an API call.
     */
    @FunctionalInterface
    public interface LLMInterface {
        /**
         * Generates a response given the conversation history.
         *
         * @param messages list of message strings in order
         * @return generated text
         */
        String generate(List<String> messages);
    }

    /**
     * Represents a single step in the agent's execution trace.
     */
    public static class Step {
        public final String thought;
        public final String action;
        public final String actionName;
        public final Map<String, String> actionArgs;
        public final String observation;

        Step(String thought, String action, String actionName,
             Map<String, String> actionArgs, String observation) {
            this.thought = thought;
            this.action = action;
            this.actionName = actionName;
            this.actionArgs = actionArgs;
            this.observation = observation;
        }
    }

    /**
     * The result of the agent's execution.
     */
    public static class AgentResult {
        public final boolean success;
        public final String finalAnswer;
        public final List<Step> trace;
        public final int stepsTaken;
        public final String errorMessage;

        AgentResult(boolean success, String finalAnswer, List<Step> trace,
                    int stepsTaken, String errorMessage) {
            this.success = success;
            this.finalAnswer = finalAnswer;
            this.trace = trace;
            this.stepsTaken = stepsTaken;
            this.errorMessage = errorMessage;
        }

        public void printTrace() {
            System.out.println("=== Agent Trace (" + stepsTaken + " steps) ===");
            for (int i = 0; i < trace.size(); i++) {
                Step s = trace.get(i);
                System.out.println("Step " + (i + 1) + ":");
                System.out.println("  Thought: " + s.thought);
                System.out.println("  Action: " + s.action);
                System.out.println("  Observation: " + s.observation);
                System.out.println();
            }
            System.out.println("Final Answer: " + finalAnswer);
            System.out.println("Success: " + success);
        }
    }

    /**
     * Constructs an LLMAgent with given tools and configuration.
     *
     * @param tools    list of tools the agent can use
     * @param maxSteps maximum reasoning steps before forced termination
     * @param llm      interface to the underlying LLM
     */
    public LLMAgent(List<Tool> tools, int maxSteps, LLMInterface llm) {
        this.toolRegistry = new LinkedHashMap<>();
        for (Tool t : tools) {
            toolRegistry.put(t.name, t);
        }
        this.maxSteps = maxSteps;
        this.llm = llm;
    }

    /**
     * Runs the agent on a given task.
     *
     * @param task the task description
     * @return the agent's execution result with trace
     */
    public AgentResult run(String task) {
        List<Step> trace = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        Set<String> seenActionObservationPairs = new HashSet<>();

        // Build the initial system prompt
        String systemPrompt = buildSystemPrompt();
        messages.add(systemPrompt);
        messages.add("Task: " + task);
        messages.add("Thought:");

        for (int step = 0; step < maxSteps; step++) {
            // Generate LLM response
            String response = llm.generate(messages);

            // Extract thought (everything before "Action:")
            String thought = extractThought(response);
            if (thought == null) thought = response;

            // Extract action
            ActionInfo actionInfo = extractAction(response);
            if (actionInfo == null) {
                // No action found — agent may be done
                return new AgentResult(true, response, trace, step + 1, null);
            }

            // Check for finish action
            if (actionInfo.name.equals("finish")) {
                String answer = actionInfo.args.getOrDefault("answer",
                        actionInfo.args.getOrDefault("message", ""));
                trace.add(new Step(thought, actionInfo.rawAction,
                        "finish", actionInfo.args, ""));
                return new AgentResult(true, answer, trace, step + 1, null);
            }

            // Validate tool exists
            Tool tool = toolRegistry.get(actionInfo.name);
            if (tool == null) {
                String error = "Error: Unknown tool '" + actionInfo.name
                        + "'. Available tools: " + toolRegistry.keySet();
                messages.add("Action: " + actionInfo.rawAction);
                messages.add("Observation: " + error);
                trace.add(new Step(thought, actionInfo.rawAction,
                        actionInfo.name, actionInfo.args, error));
                continue;
            }

            // Validate required parameters
            StringBuilder validationError = new StringBuilder();
            for (Tool.Parameter param : tool.parameters) {
                if (param.required && !actionInfo.args.containsKey(param.name)) {
                    validationError.append("Missing required parameter: ")
                            .append(param.name).append(". ");
                }
            }
            if (validationError.length() > 0) {
                messages.add("Action: " + actionInfo.rawAction);
                messages.add("Observation: " + validationError.toString());
                trace.add(new Step(thought, actionInfo.rawAction,
                        actionInfo.name, actionInfo.args,
                        validationError.toString()));
                continue;
            }

            // Execute tool
            String observation;
            try {
                observation = tool.executor.apply(actionInfo.args);
            } catch (Exception e) {
                observation = "Error executing " + actionInfo.name + ": " + e.getMessage();
            }

            // Loop detection
            String actionObsPair = actionInfo.name + ":" + observation;
            if (seenActionObservationPairs.contains(actionObsPair)) {
                observation += " [WARNING: This action-observation pair was seen before. "
                        + "Try a different approach.]";
            }
            seenActionObservationPairs.add(actionObsPair);

            messages.add("Action: " + actionInfo.rawAction);
            messages.add("Observation: " + observation);
            trace.add(new Step(thought, actionInfo.rawAction,
                    actionInfo.name, actionInfo.args, observation));
        }

        // Max steps reached without finish
        return new AgentResult(false, "Max steps (" + maxSteps + ") reached without final answer.",
                trace, maxSteps, "agent_timeout");
    }

    /**
     * Builds the system prompt describing available tools.
     */
    private String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an AI agent with access to the following tools:\n\n");
        for (Tool tool : toolRegistry.values()) {
            sb.append(tool.getSignature()).append("\n");
        }
        sb.append("\n").append(buildActionFormat());
        return sb.toString();
    }

    /**
     * Builds the action format instruction.
     */
    private String buildActionFormat() {
        return "To use a tool, output:\n"
                + "Action: tool_name(param1=\"value1\", param2=\"value2\")\n\n"
                + "After each action, you will receive an observation.\n"
                + "When you have the answer, output:\n"
                + "Action: finish(answer=\"Your final answer here\")\n\n"
                + "Begin!\n";
    }

    /**
     * Extracts the thought from the LLM response (text before "Action:").
     */
    private String extractThought(String response) {
        int actionIdx = response.indexOf("Action:");
        if (actionIdx > 0) {
            return response.substring(0, actionIdx).replace("Thought:", "").trim();
        }
        return null;
    }

    /**
     * Extracts action information from the LLM response.
     */
    private ActionInfo extractAction(String response) {
        Pattern pattern = Pattern.compile(
                "Action:\\s*(\\w+)\\(([^)]*)\\)",
                Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            String actionName = matcher.group(1);
            String argsStr = matcher.group(2);
            Map<String, String> args = parseArguments(argsStr);
            return new ActionInfo(actionName, args, matcher.group().trim());
        }
        return null;
    }

    /**
     * Parses key="value" arguments from the action string.
     */
    private Map<String, String> parseArguments(String argsStr) {
        Map<String, String> args = new LinkedHashMap<>();
        Pattern pattern = Pattern.compile("(\\w+)=\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(argsStr);
        while (matcher.find()) {
            args.put(matcher.group(1), matcher.group(2));
        }
        return args;
    }

    /**
     * Holds parsed action information.
     */
    private static class ActionInfo {
        final String name;
        final Map<String, String> args;
        final String rawAction;

        ActionInfo(String name, Map<String, String> args, String rawAction) {
            this.name = name;
            this.args = args;
            this.rawAction = rawAction;
        }
    }

    /**
     * Creates a simple search tool (simulated with a local knowledge base).
     */
    public static Tool createSearchTool(Map<String, String> knowledgeBase) {
        return new Tool(
                "search",
                "Search for information on a topic. Use this to look up facts.",
                List.of(new Tool.Parameter("query", "string", true)),
                args -> {
                    String query = args.get("query").toLowerCase();
                    return knowledgeBase.entrySet().stream()
                            .filter(e -> e.getKey().toLowerCase().contains(query))
                            .map(e -> e.getKey() + ": " + e.getValue())
                            .findFirst()
                            .orElse("No results found for '" + query + "'.");
                }
        );
    }

    /**
     * Creates a calculator tool that evaluates arithmetic expressions.
     */
    public static Tool createCalculatorTool() {
        return new Tool(
                "calculator",
                "Evaluate a mathematical expression. Supports +, -, *, /, parentheses.",
                List.of(new Tool.Parameter("expression", "string", true)),
                args -> {
                    try {
                        String expr = args.get("expression");
                        // Simple arithmetic evaluator (supports +,-,*,/)
                        double result = evaluateSimpleExpression(expr);
                        return String.valueOf(result);
                    } catch (Exception e) {
                        return "Error evaluating expression: " + e.getMessage();
                    }
                }
        );
    }

    /**
     * Simple arithmetic expression evaluator (no external dependencies).
     */
    private static double evaluateSimpleExpression(String expr) {
        // Remove whitespace
        expr = expr.replaceAll("\\s+", "");
        // Find and resolve innermost parentheses recursively
        while (expr.contains("(")) {
            int openIdx = expr.lastIndexOf('(');
            int closeIdx = expr.indexOf(')', openIdx);
            String inner = expr.substring(openIdx + 1, closeIdx);
            double result = evaluateSimpleExpression(inner);
            expr = expr.substring(0, openIdx) + result + expr.substring(closeIdx + 1);
        }
        // Evaluate multiplication and division
        Pattern mulDiv = Pattern.compile("(-?\\d+\\.?\\d*)([*/])(-?\\d+\\.?\\d*)");
        Matcher m = mulDiv.matcher(expr);
        while (m.find()) {
            double a = Double.parseDouble(m.group(1));
            double b = Double.parseDouble(m.group(3));
            double r = m.group(2).equals("*") ? a * b : a / b;
            expr = expr.substring(0, m.start()) + r + expr.substring(m.end());
            m = mulDiv.matcher(expr);
        }
        // Evaluate addition and subtraction
        Pattern addSub = Pattern.compile("(-?\\d+\\.?\\d*)([+-])(-?\\d+\\.?\\d*)");
        m = addSub.matcher(expr);
        while (m.find()) {
            double a = Double.parseDouble(m.group(1));
            double b = Double.parseDouble(m.group(3));
            double r = m.group(2).equals("+") ? a + b : a - b;
            expr = expr.substring(0, m.start()) + r + expr.substring(m.end());
            m = addSub.matcher(expr);
        }
        return Double.parseDouble(expr);
    }

    /**
     * Main method demonstrating the agent with search and calculator tools.
     */
    public static void main(String[] args) {
        // Simulated knowledge base
        Map<String, String> knowledge = new HashMap<>();
        knowledge.put("Paris", "Population 2.16 million. Capital of France.");
        knowledge.put("France", "Country in Western Europe. Population 67 million.");
        knowledge.put("Eiffel Tower", "Located in Paris. Height 330m. Built 1889.");

        // Create tools
        Tool search = createSearchTool(knowledge);
        Tool calc = createCalculatorTool();

        // Simple LLM simulation for demo (in production, call OpenAI/Anthropic)
        LLMInterface mockLLM = messages -> {
            String lastMsg = messages.get(messages.size() - 1);
            // Simple pattern matching to simulate ReAct
            String fullContext = String.join("\n", messages);

            if (fullContext.contains("population of Paris")) {
                if (fullContext.contains("Observation: Paris")) {
                    return "Thought: I found the population of Paris.\n"
                            + "Action: finish(answer=\"Paris has a population of 2.16 million.\")";
                } else {
                    return "Thought: I need to search for the population of Paris.\n"
                            + "Action: search(query=\"Paris population\")";
                }
            }
            if (fullContext.contains("calculate") || fullContext.contains("compute")) {
                return "Thought: I need to calculate this.\n"
                        + "Action: calculator(expression=\"2.5 * 60\")";
            }
            // Default: finish with a simple answer
            return "Thought: I have enough information.\n"
                    + "Action: finish(answer=\"I completed the task.\")";
        };

        LLMAgent agent = new LLMAgent(List.of(search, calc), 10, mockLLM);
        AgentResult result = agent.run("What is the population of Paris?");
        result.printTrace();
    }
}
```

---

## Complexity Analysis

### Time Complexity

- **Per step:** O(T + P) where T = LLM inference time (dominant), P = tool execution time.
- **Total:** O(S × (T + P)) for S steps. LLM inference dominates (hundreds of ms to seconds).
- **Action parsing:** O(|R|) where R = LLM response length in characters. Negligible.
- **Loop detection:** O(1) per step (hash set lookup).

### Space Complexity

- **Message history:** O(S × (R + O)) for S steps, each with a response of length R and observation of length O. This grows linearly and can exceed the context window.
- **Memory management:** The agent should summarize or truncate old messages after a certain number of steps.

### Bottlenecks

1. **LLM latency:** The agent is I/O bound on LLM inference. Each step requires a full round-trip.
2. **Context window:** After 10-15 steps, the conversation history may exceed the model's context limit, forcing summarization.
3. **Tool latency:** External API calls (search, database) add latency per step.
4. **Error cascades:** One bad action can derail subsequent reasoning.

---

## Follow-Up Questions

### Q1: How does ReAct compare to Plan-and-Solve or Tree-of-Thoughts?

**Answer:** Three major agent paradigms:

| Aspect | ReAct | Plan-and-Solve | Tree-of-Thoughts |
|--------|-------|----------------|-----------------|
| Approach | Interleaved reasoning + acting | Plan first, then execute steps | Explore multiple reasoning paths |
| Flexibility | High (adapts per observation) | Low (fixed plan) | Medium (branching) |
| Error recovery | Natural (re-plan after error) | Requires full re-plan | Can backtrack |
| Compute cost | Low per step | Moderate (planning) | High (multiple branches) |
| Best for | Dynamic environments | Well-defined procedures | Creative problem-solving |

ReAct is the most popular because it balances flexibility and cost. Tree-of-Thoughts is powerful but expensive for most production use cases.

### Q2: How do you secure an agent from executing harmful actions?

**Answer:** Security in agents is critical:

1. **Tool permissions:** Each tool should have a permission level. "Delete file" requires explicit user confirmation.
2. **Input sanitization:** Validate all parameters against allowed values. No shell injection via tool arguments.
3. **Rate limiting:** Limit how often a tool can be called per minute.
4. **Human-in-the-loop:** Require approval for high-risk actions (financial transactions, data deletion).
5. **Action validation:** Check that the tool call makes sense before executing. "search(user_password = '...')" should be rejected.
6. **Audit trails:** Log every action with timestamp and parameters for post-hoc analysis.

### Q3: How do you handle agent context window exhaustion?

**Answer:** Several strategies:

1. **Sliding window:** Keep the last N steps, summarize older ones into a "summary" message.
2. **Summarization:** Periodically ask the LLM to summarize the conversation: "Summarize progress so far in one paragraph."
3. **Selective retention:** Keep only observations that contain important facts (filtered by keyword or embedding similarity).
4. **External memory:** Store observations in a vector database and retrieve relevant ones when needed (RAG for agents).
5. **Compression:** Use a smaller model to compress observations before adding them to history.

### Q4: How do you evaluate an agent's performance?

**Answer:** Beyond "did it finish?", evaluate:

1. **Success rate:** % of tasks completed successfully (over N trials).
2. **Efficiency:** Average steps to completion. Lower is better.
3. **Tool usage diversity:** Does the agent use all available tools, or just one?
4. **Error rate:** % of actions that resulted in errors.
5. **Loop frequency:** % of runs that entered a detectable loop.
6. **Cost:** Total LLM inference tokens + API calls per task.
7. **Recovery rate:** After an error, does the agent successfully re-plan?

### Q5: How do you implement parallel tool calls?

**Answer:** Standard ReAct is sequential, but parallel execution can speed things up:

1. **Batch actions:** Allow the LLM to output multiple actions: "Action: search(...) and search(...)".
2. **Deferred execution:** Parse all actions, execute them in parallel, return observations together.
3. **Sub-agents:** For complex tasks, spawn sub-agents that each handle one sub-task independently.
4. **Planning phase:** First, have the agent output all actions it plans to take, then execute them in parallel.

**Trade-off:** Parallelism reduces wall-clock time but may waste compute (some results may not be needed) and makes the agent's reasoning harder to follow.

---

## Test Cases

### Test Case 1: Simple Fact Lookup

```
Task: "What is the population of Paris?"
Tools: search(knowledge base)
Expected: Agent calls search("Paris population"), observes "Population 2.16 million", finishes with answer.
Steps: 2 (search + finish)
```

### Test Case 2: Multi-Step Calculation

```
Task: "A train travels at 60 mph for 2.5 hours. How far does it go?"
Tools: calculator
Expected: Agent calls calculator(expression="60 * 2.5"), observes "150.0", finishes with "150 miles".
Steps: 2
```

### Test Case 3: Unknown Tool Invocation

```
Task: "Delete all files"
Tools: search only (no delete tool)
Expected: Agent tries Action: delete(...), gets "Unknown tool 'delete'. Available tools: [search]".
Agent recovers by trying search or finishes with an apology.
```

### Test Case 4: Missing Required Parameter

```
Task: "Search for something"
Tools: search(query required)
Agent output: Action: search(results=10)
Expected: "Missing required parameter: query." Agent should retry with correct parameter.
```

### Test Case 5: Loop Detection

```
Task: "Find the weather"
Tools: search
Agent keeps calling search("weather") and getting same result.
Expected: After 2 identical pairs, the observation includes a WARNING.
Agent should try a different query.
```

### Test Case 6: Max Steps Reached

```
Task: "Solve world hunger"
Tools: search
Expected: After maxSteps (e.g., 3) without finish action, agent returns timeout error.
Success = false, errorMessage = "Max steps reached without final answer."
```

### Test Case 7: Calculator Error

```
Task: "Calculate 10/0"
Tools: calculator
Expected: calculator("10/0") returns "Error evaluating expression: Infinity" or similar.
Agent should handle the error gracefully.
```

### Test Case 8: Finish Action Parsing

```
Agent output: "Action: finish(answer=\"The answer is 42.\")"
Expected: Parsed as action=finish, args={answer: "The answer is 42."}
Agent returns with finalAnswer="The answer is 42.", success=true.
```

---

## Summary

This walkthrough implemented a complete ReAct-style LLM agent with:
1. **Dynamic tool registry** with parameter schemas for flexible tool integration.
2. **Thought → Action → Observation loop** for structured multi-step reasoning.
3. **Action parsing** via regex to extract tool calls and arguments.
4. **Error handling** for unknown tools, missing parameters, and execution failures.
5. **Loop detection** to prevent infinite cycles.
6. **Conversation memory** for maintaining context across steps.

The agent architecture demonstrates the core pattern behind modern AI assistants like ChatGPT plugins, AutoGPT, and Gemini extensions. The key engineering challenges are ensuring reliability (handling LLM mistakes), managing context limits, and preventing harmful actions.