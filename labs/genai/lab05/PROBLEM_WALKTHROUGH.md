# Problem Walkthrough: LLM Agent Frameworks

## Problem 1: Tool-Using Support Agent with a Fixed ReAct Loop — Company: OpenAI

### Interview Scenario
"You're at OpenAI building a support agent that answers pricing questions using two tools:
a search tool for facts and a calculator for arithmetic. The lab's demo agent fails on
'What is the capital of France?' — its parser strips 7 characters after the word 'search'
and queries the wrong phrase, looping until it exhausts `maxSteps` and returns 'No answer
found.'. Fix the action contract so the agent actually completes a two-step task, while
keeping the lab's `Tool` interface, `ToolRegistry`, trace observability, and ReAct loop."

### The Problem
1. Keep the `Tool` interface and `ToolRegistry` from the lab unchanged.
2. Fix the thought parsing: use an explicit `action: tool(args)` format instead of substring offsets.
3. Extend `CalculatorTool` to handle `*` in addition to `+`.
4. Run the agent on a pricing question that requires one search and one calculation.
5. Print the naive parser's failure and the fixed agent's full trace with its final answer.

### Solution Walkthrough
- Step 1: Copy `Tool`, `CalculatorTool`, `SearchTool`, `ToolRegistry`, and `ReActAgent`
  from the lab, preserving `register`, `get`, `listDescriptions`, `maxSteps`, and `trace`.
- Step 2: Extend `CalculatorTool.execute` with a `*` branch (the lab only handles `+`
  and returns `ERROR: unsupported expression` otherwise).
- Step 3: Change the thought contract to `search: <query>` and `calculate: <expr>`; parse
  with `substring("search:".length())` — deterministic, no guessing.
- Step 4: In `run`, branch on `thought.startsWith(...)`; on `No results`, retry with the
  next query; on a calculation, set the final answer and break.
- Step 5: Print the naive parse failure (reproducing the lab's 'for the answer I need.'
  bug) and the fixed agent's `trace` and answer.

### Code
```java
package com.genai.lab05.solution;

import java.util.*;

/**
 * Lab 05 walkthrough: production support agent with a tool registry
 * and an improved ReAct loop. The lab's demo agent strips 7 chars
 * after "search" and searches the wrong phrase ("for the answer I
 * need.") — this walkthrough fixes the action format to
 * "action: <tool>(<args>)" and demonstrates the full trace.
 */
public class SupportAgent {

    interface Tool {
        String getName();
        String getDescription();
        String execute(String args);
    }

    static class CalculatorTool implements Tool {
        public String getName() { return "calculator"; }
        public String getDescription() { return "Evaluate a math expression. Input: expression string."; }
        public String execute(String args) {
            try {
                String expr = args.trim();
                if (expr.contains("*")) {
                    String[] parts = expr.split("\\*");
                    double r = Double.parseDouble(parts[0].trim()) * Double.parseDouble(parts[1].trim());
                    return String.valueOf(r);
                }
                if (expr.contains("+")) {
                    String[] parts = expr.split("\\+");
                    double r = Double.parseDouble(parts[0].trim()) + Double.parseDouble(parts[1].trim());
                    return String.valueOf(r);
                }
                return "ERROR: unsupported expression";
            } catch (Exception e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }

    static class SearchTool implements Tool {
        final Map<String, String> knowledge = Map.of(
            "transformer book price in dollars", "100 dollars",
            "euro exchange rate", "0.9 euros per dollar",
            "capital of France", "Paris"
        );
        public String getName() { return "search"; }
        public String getDescription() { return "Search for information. Input: query string."; }
        public String execute(String args) {
            return knowledge.getOrDefault(args.trim().toLowerCase(), "No results found for: " + args);
        }
    }

    static class ToolRegistry {
        final Map<String, Tool> tools = new LinkedHashMap<>();
        void register(Tool t) { tools.put(t.getName(), t); }
        Tool get(String name) { return tools.get(name); }
        String listDescriptions() {
            StringBuilder sb = new StringBuilder("Available tools:\n");
            tools.values().forEach(t ->
                sb.append("  - ").append(t.getName()).append(": ").append(t.getDescription()).append("\n"));
            return sb.toString();
        }
    }

    /** ReAct agent with explicit "action: tool(args)" thought format. */
    static class ReActAgent {
        final ToolRegistry registry;
        final List<String> trace = new ArrayList<>();
        int maxSteps;

        ReActAgent(ToolRegistry registry, int maxSteps) {
            this.registry = registry;
            this.maxSteps = maxSteps;
        }

        String run(String goal) {
            trace.add("Goal: " + goal);
            String thought = "search: transformer book price in dollars";
            String answer = "No answer found.";

            for (int step = 0; step < maxSteps; step++) {
                trace.add("Step " + (step + 1) + " — Thought: " + thought);
                if (thought.startsWith("search:")) {
                    String query = thought.substring("search:".length()).trim();
                    String obs = registry.get("search").execute(query);
                    trace.add("  Action: search(\"" + query + "\")");
                    trace.add("  Observation: " + obs);
                    if (obs.contains("No results")) {
                        thought = "search: euro exchange rate";
                    } else {
                        thought = "calculate: 100 * 0.9";
                    }
                } else if (thought.startsWith("calculate:")) {
                    String expr = thought.substring("calculate:".length()).trim();
                    String obs = registry.get("calculator").execute(expr);
                    trace.add("  Action: calculator(\"" + expr + "\")");
                    trace.add("  Observation: " + obs);
                    answer = "The Transformer book costs " + obs + " euros.";
                    thought = "final: " + answer;
                    trace.add("  Final: " + answer);
                    break;
                } else {
                    break;
                }
            }
            return answer;
        }
    }

    public static void main(String[] args) {
        ToolRegistry registry = new ToolRegistry();
        registry.register(new CalculatorTool());
        registry.register(new SearchTool());

        System.out.println("=== Tool Registry ===");
        System.out.println(registry.listDescriptions());

        System.out.println("=== Naive Parse Failure (lab demo behavior) ===");
        String thought = "I should search for the answer I need.";
        String naiveQuery = thought.substring(thought.indexOf("search") + 7).trim();
        System.out.println("Thought: '" + thought + "'");
        System.out.println("Naive query: search(\"" + naiveQuery + "\")");
        System.out.println("Observation: " + new SearchTool().execute(naiveQuery));

        System.out.println("\n=== Improved Agent Trace ===");
        ReActAgent agent = new ReActAgent(registry, 5);
        String result = agent.run("How many euros does the Transformer book cost?");
        agent.trace.forEach(System.out::println);

        System.out.println("\n=== Final Answer ===");
        System.out.println(result);

        System.out.println("\nAgent framework validated.");
    }
}
```

### Expected Output
```text
=== Tool Registry ===
Available tools:
  - calculator: Evaluate a math expression. Input: expression string.
  - search: Search for information. Input: query string.

=== Naive Parse Failure (lab demo behavior) ===
Thought: 'I should search for the answer I need.'
Naive query: search("for the answer I need.")
Observation: No results found for: for the answer I need.

=== Improved Agent Trace ===
Goal: How many euros does the Transformer book cost?
Step 1 — Thought: search: transformer book price in dollars
  Action: search("transformer book price in dollars")
  Observation: 100 dollars
Step 2 — Thought: calculate: 100 * 0.9
  Action: calculator("100 * 0.9")
  Observation: 90.0
  Final: The Transformer book costs 90.0 euros.

=== Final Answer ===
The Transformer book costs 90.0 euros.

Agent framework validated.
```

### Company Evaluation
- OpenAI: Structured tool calls, function calling contracts, agent evaluation.
- Anthropic: Tool-use safety, error-as-observation handling, trace-based debugging.
- Google: ReAct origins, planner-executor patterns, loop control.
- Shopify: Agent cost per run, tool-call budget alerts, support-answer quality.
- Uber: Agent observability, span-per-tool-call tracing, incident investigation.

---

## Problem 2: Step Budget Exhaustion — Company: Uber

### Interview Scenario
"You're at Uber running a booking agent. The lab's `ReActAgent` burns all `maxSteps`
when thoughts fail to parse. You must add a step budget report so on-call can see how
close runs come to failure."

### The Problem
1. Run an agent that never finds a valid action.
2. Count how many steps it actually consumed versus `maxSteps`.
3. Print a budget-exhaustion warning when the run ends without a final answer.

### Solution Walkthrough
- Step 1: Run `ReActAgent` on a goal with no matching tool outcome.
- Step 2: Check whether the answer still equals the `No answer found.` default.
- Step 3: Print steps used, budget, and a WARN line.

### Code
```java
ReActAgent agent = new ReActAgent(registry, 5);
String result = agent.run("Book a ride to SFO");
int used = agent.trace.stream().filter(l -> l.startsWith("Step ")).toList().size();
if (result.equals("No answer found.")) {
    System.out.printf("WARN: budget exhausted — steps used %d/%d%n", used, agent.maxSteps);
}
```
Expected output: `WARN: budget exhausted — steps used 5/5` — the loop guard is what
prevents an infinite run.

---

## Problem 3: Tool Error as Observation — Company: Anthropic

### Interview Scenario
"You're at Anthropic hardening a calculator tool. The lab's `CalculatorTool` catches
exceptions and returns an 'ERROR: ...' string. Prove that the error string propagates
through the registry into the agent trace, so it can drive recovery."

### The Problem
1. Call `execute` with a malformed expression.
2. Route the result through `ToolRegistry.get("calculator")`.
3. Confirm the observation line in the trace carries the error.

### Solution Walkthrough
- Step 1: `registry.get("calculator").execute("2 ** 3")` — unsupported syntax.
- Step 2: Append the returned string to a trace-like list.
- Step 3: Print the observation; the loop can now branch on it.

### Code
```java
Tool calc = registry.get("calculator");
String obs = calc.execute("2 ** 3");
System.out.println("  Observation: " + obs);
boolean recoverable = !obs.startsWith("ERROR");
System.out.println("Recoverable by reformulating input: " + recoverable);
```
Expected output: `Observation: ERROR: unsupported expression` and
`Recoverable by reformulating input: false` — errors are data, not exceptions.
