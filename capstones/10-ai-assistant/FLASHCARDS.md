# Flashcards: AI Assistant

Front: What is tool calling (function calling)? | Back: The LLM outputs a structured JSON object specifying a tool name and arguments, which the system executes and returns the result.

Front: What is the agent loop? | Back: The iterative process of LLM -> tool call -> execute tool -> LLM with result -> repeat until LLM produces final text.

Front: What is prompt injection? | Back: An attack where user input overrides the system prompt, causing the LLM to behave outside its intended constraints.

Front: What is SSE streaming? | Back: Server-Sent Events — a protocol for streaming tokens from server to client over HTTP, enabling real-time text generation display.

Front: What is the context window? | Back: The maximum number of tokens an LLM can process in a single request. Managed by truncating or summarizing old messages.

Front: What is a tool's JSON schema? | Back: A description of the tool's parameters including type, properties, and required fields, used by the LLM to generate valid tool calls.
