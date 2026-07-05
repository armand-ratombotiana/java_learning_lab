# Internals: AI Assistant

## Core Components
- **ChatService**: Handles conversation session management
- **LLMClient**: HTTP client for LLM API (OpenAI, Anthropic, local vLLM)
- **ToolRegistry**: Registry of available tools with JSON schemas
- **AgentExecutor**: Orchestrates the agent loop: LLM -> tool -> LLM -> tool -> ...
- **StreamingResponse**: SSE-based streaming for token-by-token output
- **ConversationManager**: Manages context window (sliding window, token counting)
- **Memory**: Short-term (conversation history) and long-term (RAG-based summary)

## Tool Definition
Each tool has: name, description, JSON Schema for parameters (type, properties, required), and an executor implementation (Java method or HTTP call).

## Built-in Tools
- `calculator`: Evaluates arithmetic expressions (safe eval sandbox)
- `current_time`: Returns current date/time
- `search_web`: Web search via API (if configured)
- `search_docs`: RAG search on uploaded documents
- `send_email`: Sends email (if configured)

## Conversation Format
```json
[
  {"role": "system", "content": "You are a helpful assistant."},
  {"role": "user", "content": "What's 25 * 4?"},
  {"role": "assistant", "content": null, "tool_calls": [
    {"id": "call_123", "type": "function", "function": {"name": "calculator", "arguments": "{\"expression\": \"25 * 4\"}"}}
  ]},
  {"role": "tool", "tool_call_id": "call_123", "content": "100"},
  {"role": "assistant", "content": "25 * 4 = 100."}
]
```
