# Autonomous Agent Platform

An AI agent platform built in Java implementing the observe-think-act loop, tool registry with dynamic execution, short-term and long-term memory with consolidation, ReAct-style planning, multi-agent orchestration, and comprehensive agent monitoring.

## Architecture Overview

```
┌──────────────┐     ┌──────────────────┐     ┌──────────────┐
│  Perception  │────►│  Agent Runtime   │────►│  Action      │
│  (Input)     │     │  observe-think-  │     │  Execution   │
│              │     │  act loop        │     │  (Tool Call)  │
└──────────────┘     └──────┬───────────┘     └──────────────┘
                            │
              ┌─────────────┼─────────────┐
              │             │             │
        ┌─────┴────┐ ┌─────┴────┐ ┌─────┴────┐
        │  Tool    │ │  Memory  │ │ Planning │
        │ Registry │ │ (short/  │ │ Engine   │
        │          │ │  long)   │ │ (ReAct)  │
        └──────────┘ └──────────┘ └──────────┘

┌────────────────────┐     ┌──────────────────┐
│ Multi-Agent        │────►│ Agent Monitor    │
│ Orchestrator       │     │ (metrics/events) │
│ (Coordinator)      │     │                  │
└────────────────────┘     └──────────────────┘
```

## Features

- **AgentRuntime**: Observe-think-act loop with step tracking, start/stop/run control, state management
- **ToolRegistry**: Pluggable tool definitions with parameter parsing, default tools (search, calculate, remember, finish)
- **AgentMemory**: Short-term (observations) and long-term (experiences) memory, recall by query, episodic indexing, automatic consolidation
- **PlanningEngine**: ReAct-style thought generation, goal decomposition into structured plans, sequential plan execution
- **MultiAgentOrchestrator**: Agent registration, message passing, parallel task distribution, coordinator pattern
- **AgentMonitor**: Per-agent metrics (steps, success/fail, latency), event logging, top-performer/struggling detection

## Usage

```java
var tools = ToolRegistry.createDefaultTools();
var memory = new AgentMemory();
var planner = new PlanningEngine(tools);
var agent = new AgentRuntime("agent-1", tools, memory, planner);

agent.runLoop("Search for the capital of France and remember it", 10);
var steps = agent.getSteps();

var orchestrator = new MultiAgentOrchestrator();
orchestrator.registerAgent("coordinator", agent);
orchestrator.registerAgent("worker-1", new AgentRuntime("worker-1", tools, memory, planner));
orchestrator.orchestrate("coordinator", List.of("worker-1"), "Process data batch");
```
