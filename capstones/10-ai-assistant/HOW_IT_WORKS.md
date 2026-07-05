# How It Works: AI Assistant

1. User sends a message (POST /api/v1/chat)
2. System prompt + conversation history + user message are assembled into the LLM request
3. Available tools (functions with JSON schemas) are included in the request
4. LLM generates response — either text or a tool call (JSON with tool name + arguments)
5. If text: response is streamed back to user via SSE (Server-Sent Events)
6. If tool call: the AgentExecutor validates the arguments, executes the tool
7. Tool result is appended to the conversation as a "tool" message
8. LLM is called again with the updated conversation (including tool result)
9. Loop continues until LLM produces text or max iterations (default: 10) reached
10. Final response is streamed to the user
