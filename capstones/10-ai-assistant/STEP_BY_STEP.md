# Step by Step: AI Assistant

## User Asks for a Calculation

1. User: "Calculate 15% tip on a $47.50 bill"
2. System prompt: "You are a helpful assistant with access to tools. Use tools when needed."
3. Messages: [system prompt, user message]
4. LLM request includes `tools`: [{name: "calculator", description: "Evaluates expressions", parameters: {...}}]
5. LLM responds: tool_calls = [{name: "calculator", arguments: {expression: "47.50 * 0.15"}}]
6. AgentExecutor validates arguments, calls CalculatorTool.execute("47.50 * 0.15")
7. Tool returns: 7.125
8. Tool result appended as {role: "tool", content: "7.125", tool_call_id: "..."}
9. LLM called again with updated messages
10. LLM responds with text: "The 15% tip on a $47.50 bill is $7.13."
11. Response streamed to user via SSE
