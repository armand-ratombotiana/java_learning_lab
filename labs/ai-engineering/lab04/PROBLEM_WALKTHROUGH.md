# Problem Walkthrough: AI Agent Frameworks

## Problem 1: Budgeted ReAct Agent with Tool-Fallback Recovery — Company: OpenAI

### Interview Scenario
"You're at OpenAI on the agents team, building a ReAct agent that answers multi-part questions using tools. Two incidents came out of the pilot: an agent took the raw error string from a failed tool call and returned it to the user as the answer, and another agent looped on tool calls for 20 minutes before the on-call killed it — the bill was extreme. You need a ReAct agent with planning (split compound tasks), tool selection by keyword contract, error-driven re-planning with a fallback tool, and a hard tool-call budget that aborts runaway loops with a clear message."

### The Problem
1. Implement the `Tool` interface with `getName()`, `keywords()`, and `execute()` — upgrade the lab's name-only matching with synonym keywords
2. Implement `ReActAgent.run(task)` as a bounded Thought → Action → Observation loop over planned sub-tasks
3. When a tool returns an `Error:` observation, re-plan once and try a fallback tool instead of returning the error
4. Enforce a `maxToolCalls` budget: abort before the next call when exhausted, printing an explicit abort message
5. Keep an append-only `memory` trace of every action and observation for auditability
6. Route tasks through an orchestrator to specialist agents, as the lab does

### Solution Walkthrough
- Step 1: Reuse the lab's `Tool` interface shape but add `keywords()` — the lab's pure `contains()` matching misses real phrasing, which the demo's failed 'capital of France' lookup shows; a keyword contract fixes tool selection deterministically
- Step 2: Give `CalculatorTool` a regex that extracts `a op b` from surrounding text — 'Compute 10 + 25 for me' should return 35.0, not the lab's format error
- Step 3: Implement `plan(task)`: split on ' and ' into sub-tasks when present, else one sub-task — planning turns compound questions into scoped tool calls
- Step 4: In the loop, on an error Observation: if the fallback tool is unused, print a re-planning Thought and retry with the fallback; otherwise record the honest failure
- Step 5: Before every tool call, check `toolCalls >= maxToolCalls` and abort with the budget message — the deterministic guard against runaway cost
- Step 6: Compose `Answers: [...]` from per-sub-task results; keep `getMemory()` immutable like the lab
- Step 7: Wire an `AgentOrchestrator` that routes weather tasks to WeatherBot and calculate tasks to MathBot, mirroring the lab's `delegate()`

### Code
```java
// File: src/com/aiengineering/lab04/BudgetedReActWalkthrough.java
package com.aiengineering.lab04;

import java.util.*;

/**
 * Walkthrough: OpenAI-style ReAct agent with planning, tool fallback
 * recovery, and a hard budget on tool calls. Fixes two behaviors the
 * lab demo leaves as-is: keyword synonyms for tool selection, and
 * error observations that trigger re-planning instead of returning
 * the error as the answer.
 */
public class BudgetedReActWalkthrough {

    interface Tool {
        String getName();
        List<String> keywords();
        String execute(String input);
    }

    static class CalculatorTool implements Tool {
        public String getName() { return "calculator"; }
        public List<String> keywords() { return List.of("calculate", "calculator", "math", "+", "plus"); }
        public String execute(String input) {
            java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("(-?\\d+(?:\\.\\d+)?)\\s*([+\\-*/])\\s*(-?\\d+(?:\\.\\d+)?)")
                .matcher(input);
            if (!m.find()) return "Error: unrecognized expression: " + input;
            double a = Double.parseDouble(m.group(1));
            double b = Double.parseDouble(m.group(3));
            return switch (m.group(2)) {
                case "+" -> String.valueOf(a + b);
                case "-" -> String.valueOf(a - b);
                case "*" -> String.valueOf(a * b);
                case "/" -> b == 0 ? "Error: division by zero" : String.valueOf(a / b);
                default -> "Error: unknown operator";
            };
        }
    }

    static class SearchTool implements Tool {
        public String getName() { return "web_search"; }
        public List<String> keywords() { return List.of("search", "what is", "meaning", "capital"); }
        private final Map<String, String> knowledge = Map.of(
            "capital of france", "Paris is the capital of France.",
            "meaning of life", "42 (according to Deep Thought).",
            "java version", "Java 21 was released in September 2023."
        );
        public String execute(String input) {
            String lower = input.toLowerCase();
            for (var entry : knowledge.entrySet()) {
                if (lower.contains(entry.getKey())) return entry.getValue();
            }
            return "No results found for: " + input;
        }
    }

    static class WeatherTool implements Tool {
        public String getName() { return "weather"; }
        public List<String> keywords() { return List.of("weather", "temperature"); }
        public String execute(String input) {
            String city = input.replaceFirst("(?i)^(what is the weather in|weather in|weather)\\s*", "").trim();
            city = city.replaceAll("[?.!]+$", "");
            if (city.isEmpty() || city.equalsIgnoreCase(input)) {
                return "Error: could not extract city from: " + input;
            }
            if (city.equalsIgnoreCase("Atlantis")) return "Error: no weather data for city: " + city;
            return "The weather in " + city + " is currently 22\u00B0C and sunny.";
        }
    }

    static class ReActAgent {
        private final String name;
        private final List<Tool> tools;
        private final int maxSteps;
        private final int maxToolCalls;
        private final List<String> memory = new ArrayList<>();

        ReActAgent(String name, List<Tool> tools, int maxSteps, int maxToolCalls) {
            this.name = name;
            this.tools = tools;
            this.maxSteps = maxSteps;
            this.maxToolCalls = maxToolCalls;
        }

        List<String> plan(String task) {
            return task.contains(" and ")
                ? Arrays.stream(task.split(" and ")).map(String::trim).toList()
                : List.of(task);
        }

        Tool selectTool(String subTask, boolean fallback) {
            for (Tool t : tools) {
                for (String kw : t.keywords()) {
                    if (subTask.toLowerCase().contains(kw.toLowerCase())) {
                        return fallback && !tools.get(tools.size() - 1).equals(t)
                            ? tools.get(tools.size() - 1) : t;
                    }
                }
            }
            return tools.get(0);
        }

        String run(String task) {
            System.out.println("\n[" + name + "] Task: " + task);
            memory.add("Task: " + task);
            List<String> subTasks = plan(task);
            if (subTasks.size() > 1) System.out.println("  Plan: " + subTasks);

            List<String> answers = new ArrayList<>();
            int toolCalls = 0;
            boolean fallbackUsed = false;

            for (String sub : subTasks) {
                if (toolCalls >= maxToolCalls) {
                    String abort = "ABORTED: tool budget exhausted after " + toolCalls + " call(s) (max=" + maxToolCalls + ")";
                    memory.add("Abort: " + abort);
                    System.out.println("  " + abort);
                    return abort;
                }
                String thought = "I need to solve sub-task: " + sub;
                System.out.println("  Step — Thought: " + thought);

                for (int attempt = 0; attempt < 2; attempt++) {
                    if (toolCalls >= maxToolCalls) {
                        String abort = "ABORTED: tool budget exhausted after " + toolCalls + " call(s) (max=" + maxToolCalls + ")";
                        memory.add("Abort: " + abort);
                        System.out.println("  " + abort);
                        return abort;
                    }
                    Tool tool = selectTool(sub, fallbackUsed);
                    System.out.println("  Action: " + tool.getName() + "(\"" + sub + "\")");
                    String observation = tool.execute(sub);
                    toolCalls++;
                    System.out.println("  Observation: " + observation);
                    memory.add("Action: " + tool.getName() + " -> " + observation);

                    if (observation.startsWith("Error:")) {
                        if (!fallbackUsed) {
                            fallbackUsed = true;
                            System.out.println("  Thought: primary tool failed; falling back to another tool.");
                            continue;
                        }
                        answers.add(observation);
                        break;
                    }
                    answers.add(observation);
                    break;
                }
            }

            String finalAnswer = "Answers: " + answers;
            System.out.println("  Final: " + finalAnswer);
            return finalAnswer;
        }

        List<String> getMemory() { return List.copyOf(memory); }
    }

    static class AgentOrchestrator {
        private final Map<String, ReActAgent> agents = new HashMap<>();

        void registerAgent(String role, ReActAgent agent) { agents.put(role, agent); }

        String delegate(String task) {
            System.out.println("\n[Orchestrator] Delegating task: \"" + task + "\"");
            if (task.toLowerCase().contains("weather")) {
                return agents.getOrDefault("weather_agent", agents.values().iterator().next()).run(task);
            } else if (task.toLowerCase().contains("calculate")) {
                return agents.getOrDefault("math_agent", agents.values().iterator().next()).run(task);
            } else {
                return agents.getOrDefault("general_agent", agents.values().iterator().next()).run(task);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Walkthrough: OpenAI Budgeted ReAct Agent ===\n");

        Tool calc = new CalculatorTool();
        Tool search = new SearchTool();
        Tool weather = new WeatherTool();

        ReActAgent general = new ReActAgent("GeneralBot", List.of(search, calc, weather), 6, 5);
        ReActAgent weatherAgent = new ReActAgent("WeatherBot", List.of(weather, search), 6, 5);
        ReActAgent mathAgent = new ReActAgent("MathBot", List.of(calc, search), 6, 5);

        System.out.println("--- Single-step ReAct ---");
        general.run("What is the capital of France?");

        System.out.println("\n--- Orchestration with tool-fallback recovery ---");
        AgentOrchestrator orchestrator = new AgentOrchestrator();
        orchestrator.registerAgent("weather_agent", weatherAgent);
        orchestrator.registerAgent("math_agent", mathAgent);
        orchestrator.registerAgent("general_agent", general);
        orchestrator.delegate("What is the weather in Atlantis?");

        System.out.println("\n--- Planning: split task into sub-tasks ---");
        ReActAgent planner = new ReActAgent("PlannerBot", List.of(weather, search), 6, 3);
        planner.run("What is the weather in Berlin and the capital of France?");

        System.out.println("\n--- Budget enforcement (max 1 tool call) ---");
        ReActAgent tightBudget = new ReActAgent("TightBudgetBot", List.of(weather, search), 6, 1);
        tightBudget.run("What is the weather in Berlin and the capital of France?");

        System.out.println("\n--- Calculator direct (extracted expression) ---");
        System.out.println("  calculator(\"Compute 10 + 25 for me\") -> " + calc.execute("Compute 10 + 25 for me"));

        System.out.println("\nWalkthrough complete.");
    }
}
```

### Expected Output
```
=== Walkthrough: OpenAI Budgeted ReAct Agent ===

--- Single-step ReAct ---

[GeneralBot] Task: What is the capital of France?
  Step — Thought: I need to solve sub-task: What is the capital of France?
  Action: web_search("What is the capital of France?")
  Observation: Paris is the capital of France.
  Final: Answers: [Paris is the capital of France.]

--- Orchestration with tool-fallback recovery ---

[Orchestrator] Delegating task: "What is the weather in Atlantis?"

[WeatherBot] Task: What is the weather in Atlantis?
  Step — Thought: I need to solve sub-task: What is the weather in Atlantis?
  Action: weather("What is the weather in Atlantis?")
  Observation: Error: no weather data for city: Atlantis
  Thought: primary tool failed; falling back to another tool.
  Action: web_search("What is the weather in Atlantis?")
  Observation: No results found for: What is the weather in Atlantis?
  Final: Answers: [No results found for: What is the weather in Atlantis?]

--- Planning: split task into sub-tasks ---

[PlannerBot] Task: What is the weather in Berlin and the capital of France?
  Plan: [What is the weather in Berlin, the capital of France?]
  Step — Thought: I need to solve sub-task: What is the weather in Berlin
  Action: weather("What is the weather in Berlin")
  Observation: The weather in Berlin is currently 22°C and sunny.
  Step — Thought: I need to solve sub-task: the capital of France?
  Action: web_search("the capital of France?")
  Observation: Paris is the capital of France.
  Final: Answers: [The weather in Berlin is currently 22°C and sunny., Paris is the capital of France.]

--- Budget enforcement (max 1 tool call) ---

[TightBudgetBot] Task: What is the weather in Berlin and the capital of France?
  Plan: [What is the weather in Berlin, the capital of France?]
  Step — Thought: I need to solve sub-task: What is the weather in Berlin
  Action: weather("What is the weather in Berlin")
  Observation: The weather in Berlin is currently 22°C and sunny.
  ABORTED: tool budget exhausted after 1 call(s) (max=1)

--- Calculator direct (extracted expression) ---
  calculator("Compute 10 + 25 for me") -> 35.0

Walkthrough complete.
```

### Company Evaluation
- Oracle: Control design: step budgets, tool schema validation, and failure attribution.
- Deloitte: Process integration: escalation flows, human handoff, and operating procedures.
- Accenture: Engineering rigor: trajectory logging, fallback testing, and simulation harnesses.
- PwC: Risk governance: action auditability, tool permissions, and incident processes.
- Amazon: Scale: agent concurrency, cost limits per task, and platform telemetry.

---

## Problem 2: Multi-Agent Escalation to a Human — Company: Uber

### Interview Scenario
"You're at Uber building a rider-support agent. The coordinator classifies each ticket and routes to specialists, but when the specialist's confidence is low or the task is a refund, a human must take over. Extend the orchestrator with confidence-based escalation."

### The Problem
1. Score each delegation with a simple deterministic confidence: 1.0 when keywords match a specialist, 0.4 when falling to general agent
2. Escalate to `HUMAN` when confidence is below 0.5 or the task mentions refund/account issues
3. Log the routing decision in the agent memory trace
4. Keep the specialist routing from the lab's `AgentOrchestrator` intact

### Solution Walkthrough
- Step 1: Add `confidence(task, agent)` to the orchestrator — the lab's `delegate()` already distinguishes weather/calculate/general routes, so the confidence is derivable
- Step 2: Before delegating, check escalation triggers (low confidence, sensitive keywords); print `Escalating to HUMAN: <task>`
- Step 3: On escalation, still log the decision to the trace and return a hold message
- Step 4: Note the production design: human-in-the-loop for high-risk actions is one of the lab's listed production considerations

### Code
```java
String delegate(String task) {
    double confidence = task.toLowerCase().contains("weather") || task.toLowerCase().contains("calculate") ? 1.0 : 0.4;
    boolean sensitive = task.toLowerCase().contains("refund") || task.toLowerCase().contains("account");
    if (sensitive || confidence < 0.5) {
        System.out.printf("  Routing: HUMAN (confidence=%.1f, sensitive=%b)%n", confidence, sensitive);
        return "HOLD: ticket escalated to human agent.";
    }
    System.out.printf("  Routing: specialist (confidence=%.1f)%n", confidence);
    return super.delegate(task);
}
```
Output: `"I need a refund for my last trip"` prints `Routing: HUMAN (confidence=0.4, sensitive=true)` and returns the hold message; `"What is the weather in London?"` routes to WeatherBot at confidence 1.0. Confidence + sensitivity form the two-input escalation gate.

### Company Evaluation
- Oracle: Escalation correctness: role scoping, transfer semantics, and audit coverage.
- Deloitte: Operating model: routing rules, escalation SLAs, and service ownership.
- Accenture: Orchestration practice: multi-agent workflows, state transfer, and testing.
- PwC: Compliance: decision records, human-in-the-loop evidence, and segregation of duties.
- Amazon: Scale: routing at fleet scale, load-aware agent pools, and observability.

---

## Problem 3: Memory Trace as a Debugging Artifact — Company: Anthropic

### Interview Scenario
"You're at Anthropic investigating why the customer-support agent gave a wrong answer in production. You need to prove from the recorded trace exactly which tool call produced the wrong Observation that the agent trusted."

### The Problem
1. Run an agent task and export its `memory` trace
2. Annotate each entry with a step number
3. Identify the action whose observation seeded the wrong final answer
4. Replay the trace and show where a human reviewer would have stopped the agent

### Solution Walkthrough
- Step 1: Use the lab's `getMemory()` — the agent's trace already contains `Task:` and `Action: <tool> -> <observation>` entries
- Step 2: Print the trace with indexes; the observation that starts with `Error:` or `No results found` is the smoking gun
- Step 3: Compare the trace against the golden path — the walkthrough's Atlantis task shows the honest sequence: weather error → fallback → no results → final answer reflects the failure
- Step 4: Conclude: the trace is the replay artifact; production systems store it per request id and feed it to the evaluation pipeline

### Code
```java
List<String> trace = agent.getMemory();
for (int i = 0; i < trace.size(); i++) {
    System.out.printf("  [%d] %s%n", i, trace.get(i));
    if (trace.get(i).contains("-> No results") || trace.get(i).contains("-> Error")) {
        System.out.println("       ^ suspicious observation — agent trusted this");
    }
}
```
Output: an indexed trace where the suspicious observation is flagged inline — the memory list the lab has built since `run()` began is the audit trail, and the same loop can be replayed in the evaluation harness to score whether the agent recovered appropriately.

### Company Evaluation
- Oracle: Trace design: transcript completeness, step indexing, and replay support.
- Deloitte: Diagnostics practice: trace-driven troubleshooting and knowledge retention.
- Accenture: Debugging methodology: replayable runs, snapshot isolation, and tooling.
- PwC: Evidential control: immutable traces, audit of agent behavior, and forensics.
- Amazon: Scale: trace storage, sampling, and correlation at fleet scale.
