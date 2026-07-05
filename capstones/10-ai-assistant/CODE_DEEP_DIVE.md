# Code Deep Dive: AI Assistant

## Agent Executor

`AgentExecutor` implements the agent loop. It takes the conversation messages, calls `LLMClient.chat()`, and processes the response. If the response contains `tool_calls`, it iterates through each: validates arguments against JSON Schema, invokes the tool via `ToolRegistry`, appends the result as a new message, and recurses. A `maxIterations` guard (default 10) prevents infinite loops.

## LLM Client

`LLMClient` abstracts multiple LLM providers behind a common interface. OpenAI-compatible: constructs messages array with system prompt, tools as functions array. Streaming: uses SSE (Server-Sent Events) reader, emitting tokens as they arrive via `SseEmitter`. Supports `stop` sequences and `max_tokens` limits.

## Tool Registry

`ToolRegistry` maintains a `Map<String, ToolDefinition>`. `ToolDefinition` includes name, description, `JsonSchema` for parameters, and a `Function<JsonNode, Object>` executor. Tools are registered at startup via Spring `@PostConstruct` or discovered via annotation.

## Conversation Manager

`ConversationManager` tracks the current context window. It counts tokens using a tokenizer (GPT-2 BPE or tiktoken). When the token count exceeds `maxContextTokens`, it drops oldest messages. Optionally, it can summarize dropped messages using a separate LLM call and insert the summary.

## Tool: Calculator

`CalculatorTool` evaluates math expressions using a safe sandbox (no `eval()`). It parses the expression AST with a custom expression parser that supports +, -, *, /, pow, sqrt, sin, cos, log. This avoids arbitrary code execution while providing useful calculation capability.
