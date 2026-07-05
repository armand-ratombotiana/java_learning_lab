# Reflection: AI Assistant

## What I Learned
- LLM integration patterns: chat completion, streaming, function calling
- Agent loop orchestration and iteration management
- Tool definition, registration, and execution pipeline
- Prompt engineering for system prompts and tool descriptions
- Context window management for extended conversations

## Challenges
- Getting the LLM to reliably call the correct tool with correct arguments
- Handling tool errors gracefully in the agent loop
- Designing safe tool execution (calculator without code injection)
- Balancing context window size vs LLM performance

## What I'd Do Differently
- Build a tool testing framework first (validate tool calls without real LLM)
- Implement streaming from the start (non-streaming felt broken)
- Add more comprehensive guardrails earlier
- Create a visual conversation debugger (see all messages including tool calls)
- Use a local LLM for development to reduce costs and iteration time
