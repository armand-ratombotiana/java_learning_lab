# Exercises: AI Assistant

## Beginner
1. Implement a simple echo tool that returns the input back
2. Add a new tool: `get_time` that returns current time in a timezone
3. Implement conversation history storage in PostgreSQL

## Intermediate
4. Implement tool calling: LLM requests to use calculator, execute and return result
5. Add SSE streaming for token-by-token response
6. Implement context window management (sliding window by token count)
7. Add rate limiting per user session

## Advanced
8. Implement parallel tool execution (multiple tool calls in one LLM response)
9. Add RAG memory: store conversations in vector DB, retrieve relevant past conversations
10. Implement guardrails: content moderation before and after LLM calls
11. Build a plugin system: external tools registered via REST API
12. Implement multi-agent orchestration: specialized sub-agents for different domains
