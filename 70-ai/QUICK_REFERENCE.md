# AI Fundamentals Quick Reference

## AI Integration Options

| Framework | Description | Use Case |
|-----------|-------------|----------|
| Spring AI | Spring ecosystem AI integration | Enterprise AI apps |
| Langchain4j | Java port of LangChain | LLM chaining |
| OpenAI Java SDK | Direct API access | OpenAI models |

## Key APIs

### Spring AI
```java
ChatClient chatClient = ChatClient.builder(openAiChatModel).build();
String response = chatClient.prompt().user("What is Java?").call().content();
```

### OpenAI SDK
```java
OkHttpClient client = new OkHttpClient();
Request request = new Request.Builder()
    .url("https://api.openai.com/v1/chat/completions")
    .header("Authorization", "Bearer " + apiKey)
    .post(RequestBody.create(json, JSON))
    .build();
```

## Common Use Cases
- Chatbots and conversational interfaces
- Code generation and analysis
- Document summarization
- Sentiment analysis
- Data extraction