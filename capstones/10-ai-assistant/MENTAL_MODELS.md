# Mental Models: AI Assistant

- **LLM = Brain**: Generates text and decides which tools to use.
- **Tools = Hands**: Extend the assistant's capabilities beyond text (calculate, search, act).
- **System Prompt = Personality + Rules**: Defines who the assistant is and what it can do.
- **Context Window = Short-Term Memory**: What the assistant remembers from the current conversation.
- **Agent Loop = Think-Act-Observe**: Think (LLM), Act (tool call), Observe (result), Repeat.
- **Streaming = Thinking Out Loud**: Generate response word by word so the user sees progress.
- **Function Calling = Structured Decisions**: LLM outputs JSON for tool invocation rather than plain text.
