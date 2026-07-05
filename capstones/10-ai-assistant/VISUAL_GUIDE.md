# Visual Guide: AI Assistant

## Agent Loop
```
User: "What's the weather in Tokyo?"
    |
    v
[Assemble Messages] (system prompt + history + user msg + tools)
    |
    v
[LLM Call] (OpenAI / Claude / vLLM)
    |
    v
[Parse Response]
    |
    +-- Text: "Let me check the weather..."
    |        |
    |        v
    |   [Stream to User] -> Done
    |
    +-- Tool Call: {"name": "get_weather", "args": {"city": "Tokyo"}}
             |
             v
        [Execute Tool] -> Weather result: "22C, sunny"
             |
             v
        [Append Tool Result to Messages]
             |
             v
        [LLM Call Again (with tool result)]
             |
             v
        [Parse Response -> Text: "The weather in Tokyo is 22C and sunny."]
             |
             v
        [Stream to User] -> Done
```

## Architecture Diagram
```
[Web UI / API Client] --> [REST API] --> [ChatService]
                                              |
                                +-------------+-------------+
                                |             |             |
                          [LLM Client]  [ToolRegistry] [ConversationManager]
                                |             |             |
                          [OpenAI API]  [Calculator]   [Context Window]
                          [Anthropic]   [WebSearch]    [Token Counter]
                          [vLLM Local]  [EmailTool]    [Message Store]
```

## Streaming Response (SSE)
```
data: {"type": "token", "content": "The"}
data: {"type": "token", "content": " weather"}
data: {"type": "token", "content": " in"}
data: {"type": "token", "content": " Tokyo"}
data: {"type": "token", "content": " is"}
data: {"type": "token", "content": " 22"}
data: {"type": "token", "content": " degrees"}
data: {"type": "token", "content": "."}
data: {"type": "done"}
```
