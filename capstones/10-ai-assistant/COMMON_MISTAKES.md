# Common Mistakes: AI Assistant

- **Not validating tool arguments**: Malformed JSON or invalid arguments can crash the tool. Always validate against JSON Schema before execution.
- **Infinite agent loops**: LLM keeps calling tools without producing text. Set max iterations (10) and a safety timeout.
- **Context window overflow**: Long conversations exceed LLM context limit. Implement sliding window or summarization.
- **Exposing raw tool errors**: Errors from tools (e.g., "Connection refused") leak internals. Catch and return user-friendly messages.
- **No streaming**: Users wait for full response. Implement SSE streaming for better UX.
- **Tool side effects without confirmation**: Tools that send email, delete data, etc. should ask user confirmation first.
- **Prompt injection**: User input in system prompt area can override instructions. Separate system, user, and tool messages.
