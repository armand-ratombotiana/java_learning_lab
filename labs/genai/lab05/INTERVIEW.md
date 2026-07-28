# Lab 05: Interview Questions

## Q1: What is the ReAct pattern?
**A:** ReAct interleaves reasoning (thoughts) and actions (tool calls). The agent thinks about what to do, executes an action, observes the result, and continues.

## Q2: How does an LLM agent choose which tool to use?
**A:** The prompt lists available tools with descriptions. The LLM generates a structured action (e.g., JSON with tool name and params), which the runtime parses and executes.

## Q3: What are common failure modes for LLM agents?
**A:** Infinite loops, hallucinated tool calls, incorrect parameters, context window overflow, tool execution errors.

## Q4: How do you handle tool execution errors in an agent loop?
**A:** Catch exceptions, format error as observation, feed back to the LLM with retry. Set max retries per action to prevent infinite loops.

## Q5: Compare single-agent vs multi-agent architectures.
**A:** Single-agent is simpler but limited by context. Multi-agent allows specialization (planner, researcher, coder) but adds coordination complexity.
