# Debugging LangChain4j Issues

## Common Failure Scenarios

### LLM API Issues

Connecting to LLM providers often fails due to configuration issues. The most common error is `Unauthorized` or invalid API key messages. Verify your API key is correct and has the right permissions—some providers have separate keys for different API versions or environments.

Rate limiting is a frequent issue with LLM APIs. Providers enforce limits on requests per minute (RPM) and tokens per minute (TPM). When you exceed these, the API returns 429 errors. Implement exponential backoff and respect retry-after headers. Some providers allow increasing limits through paid plans.

Model version mismatches can cause issues—requests specifying a model that doesn't exist or isn't available in your region fail. Provider APIs are strict about model names, so verify the exact model identifier in your provider's documentation.

### Timeout Handling

LLM operations can take significant time, and default timeouts are often insufficient. Network timeouts cause `ReadTimeoutException` or `SocketTimeoutException`. Set appropriate timeout values based on expected response times for your use case. Chat models typically respond within 30-60 seconds, but complex queries can take longer.

Streaming responses require different timeout handling than synchronous responses. If you timeout during streaming, the response is incomplete and parsing fails. Adjust streaming timeouts to accommodate longer response generation.

Connection pool exhaustion occurs when you make too many concurrent requests. Each request holds a connection until complete. Use connection pooling with appropriate pool sizes, and implement request queuing to avoid overwhelming the LLM provider.

### Stack Trace Examples

**API authentication failure:**
```
dev.langchain4j.service.InvalidApiKeyException: Invalid API key
    at dev.langchain4j.model.chat.ChatLanguageModel.invoke(ChatLanguageModel.java:45)
```

**Rate limit exceeded:**
```
dev.langchain4j.service.RateLimitException: Rate limit exceeded. Please retry after 60 seconds
    at dev.langchain4j.model.openai.OpenAiChatModel.get(OpenAiChatModel.java:78)
```

**Timeout during streaming:**
```
java.util.concurrent.TimeoutException: Timeout of 30000ms encountered
    at dev.langchain4j.model.chat.ChatLanguageModel.stream(ChatLanguageModel.java:92)
```

## Debugging Techniques

### Diagnosing API Issues

Enable DEBUG logging for LangChain4j to see request and response details. Set `logging.level.dev.langchain4j=DEBUG` to log HTTP requests, headers, and response bodies. This helps identify malformed requests or unexpected responses.

Verify your provider's base URL is correct. Some providers have different endpoints for different API versions or regions. The default OpenAI endpoint won't work with other providers like Anthropic or Google.

Use wiretap or HTTP logging at the HTTP client level to see exact requests being sent. The underlying HTTP client (OkHttp or Apache HttpClient) has logging options that show request/response bodies.

### Handling Timeouts and Retries

Configure timeouts appropriately for your use case. Use `ChatLanguageModelBuilder` to set connect and read timeouts. For production, set connect timeout to 10-15 seconds and read timeout to 60-120 seconds depending on expected response times.

Implement custom retry logic with exponential backoff for transient failures. The built-in retry mechanisms may not be sufficient for LLM APIs where rate limits and timeouts are common.

Use circuit breaker patterns when calling external LLM APIs. This prevents cascading failures when the LLM provider is experiencing issues. Track failure counts and temporarily stop calling the API after threshold failures.

## Best Practices

Always configure explicit timeouts rather than relying on defaults. Default timeouts are often too short for LLM operations. Use `Timeout.ofDuration()` with values appropriate for your expected response times.

Use environment variables for API keys rather than hardcoding them. This allows different configurations for development, staging, and production without code changes. Never commit API keys to version control.

Implement proper error handling with specific exception types. Catch `ApiException` for API-level errors, `TimeoutException` for timeout issues, and generic `Exception` for unexpected errors. Log appropriately and provide meaningful error messages to users.

Monitor LLM API costs by tracking token usage. Most providers charge based on tokens, and unexpected costs indicate issues like infinite loops in prompts or excessive response generation.