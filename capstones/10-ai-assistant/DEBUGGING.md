# Debugging: AI Assistant

## Common Issues

### LLM not calling tools when expected
- Tool descriptions may be unclear — improve descriptions
- System prompt may constrain the LLM too much
- Tool parameters may be too complex — simplify schemas
- Check if tools are actually included in the request

### Tool returning errors
- Validate tool arguments against schema
- Check tool implementation for exceptions
- External tool dependencies may be unavailable (database, API)
- Rate limits exceeded on external APIs

### Agent loop never terminates
- LLM keeps calling tools — increase maxIterations or improve system prompt
- Tool results may be confusing the LLM (return clear results)
- Check for circular tool calling (tool A calls tool B calls tool A)

### Streaming is slow
- LLM provider latency (use faster model for simple responses)
- SSE implementation may be buffering
- Network latency between assistant and LLM provider

### Responses are nonsensical
- Context window may have corrupted messages
- Tool results may be too large (truncate large results)
- System prompt may be too complex
