# AI Agent Frameworks — Deep Dive Guide

## The ReAct Pattern

ReAct (Reasoning + Acting) interleaves chain-of-thought reasoning with tool-use actions in a loop:

```
Thought → Action → Observation → Thought → Action → ... → Final Answer
```

1. **Thought**: The agent reasons about what to do next given the task and previous observations
2. **Action**: The agent selects and invokes a tool (calculator, search, weather, etc.)
3. **Observation**: The tool's output is fed back into the reasoning loop
4. **Repeat** until the agent determines it has enough information to answer

## Tool Interface Design

Tools are the agent's interface to the external world. Each tool has:

- **Name**: Unique identifier for routing
- **Description**: What the tool does (used by the LLM for selection)
- **Input Schema**: Expected parameters
- **Execute**: The actual implementation

## Code Walkthrough: ReActAgent

The `ReActAgent` class demonstrates:

- A `List<Tool>` injected at construction for dependency flexibility
- A `run(task)` method that drives the ReAct loop for up to `maxSteps` iterations
- Simple keyword-based tool selection (in production, the LLM chooses the tool)
- Step-by-step logging of Thought/Action/Observation
- An in-memory `memory` list that records the full reasoning trace

## Multi-Agent Orchestration

The `AgentOrchestrator` demonstrates:

- **Specialized agents**: Each agent has a focused set of tools (MathBot, WeatherBot, GeneralBot)
- **Task routing**: The orchestrator inspects the task and delegates to the appropriate specialist
- **Coordination**: Multiple agents work together under a single coordinator

## Memory and State

Three types of agent memory:

| Type | Scope | Implementation |
|------|-------|----------------|
| Conversation History | Session | In-memory list of turns |
| Working Memory | Current task | Scratchpad for intermediate reasoning |
| Long-Term Memory | Cross-session | Vector database (external) |

## Production Considerations

- Use structured tool definitions (OpenAPI/JSON Schema) for LLM tool calling
- Implement retry logic and timeout per tool invocation
- Add human-in-the-loop approval for high-risk actions
- Persist agent memory to a database for multi-turn conversations
- Rate-limit tool usage to prevent runaway costs