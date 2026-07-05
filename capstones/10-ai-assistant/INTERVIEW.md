# Interview: AI Assistant

## Common Questions

### Q: Design an AI assistant with tool calling for customer support.
LLM-powered chat with tools: search_knowledge_base (RAG), check_order_status (API), cancel_order, escalate_to_human. Agent loop with fallback: if tools can't resolve, transfer to human agent. Guardrails for safe responses.

### Q: How do you handle prompt injection attacks?
Separate system/user/tool message roles. Never embed user input in system prompt. Validate tool call arguments against schemas. Sanitize tool results before returning. Rate limit to prevent probing.

### Q: How do you manage context windows for long conversations?
Sliding window (keep last N messages based on token count). Summarization (use LLM to compress old messages). RAG retrieval (store past conversations, retrieve relevant ones). Hierarchical memory (short-term = recent messages, long-term = summaries).

### Q: How do you decide which tools an LLM should use?
Tool description engineering: clear, concise descriptions of when to use each tool. The system prompt should guide tool selection. For complex scenarios, use a router LLM that decides which sub-agent/tool to invoke.

### Q: How do you ensure the assistant is safe and doesn't cause harm?
Content moderation (input and output). Tool confirmation for destructive actions (email, delete). Rate limiting. Human-in-the-loop for high-risk actions. Audit logging of all tool calls. Regular red-teaming of the assistant.
