# Debugging Spring AI Issues

## Common Failure Scenarios

### ChatClient Issues

The ChatClient is the primary interface in Spring AI, but configuration mistakes cause many issues. The most common is not specifying the correct model type—using `ChatModel` when you need `StreamingChatModel` or vice versa. The interfaces look similar but have different semantics.

Message construction errors are common. The ChatClient expects specific message types (`UserMessage`, `SystemMessage`, `AssistantMessage`). Using wrong message types or improperly constructing messages causes serialization issues that manifest as cryptic errors.

The ChatClient builder pattern requires specific configuration steps. Forgetting to set the base URL, API key, or model name results in requests that either fail immediately or silently use defaults that may not be what you expect.

### Stack Trace Examples

**Missing ChatModel configuration:**
```
java.lang.IllegalStateException: No ChatModel found. Please configure a ChatModel bean or ensure auto-configuration is enabled.
    at org.springframework.ai.chat.client.ChatClientBuilder.getDefaultChatModel(ChatClientBuilder.java:125)
```

**Message type mismatch:**
```
org.springframework.messaging.MessageDeliveryException: MessageConversionException: Cannot convert message; 
nested exception is org.springframework.messaging.converter.MessageConversionException: Cannot convert from 
'java.lang.String' to 'dev.langchain4j.data.message.UserMessage'
```

**API key not configured:**
```
org.springframework.ai.api_CLIENTInitializationException: Failed to initialize AI client
    at org.springframework.ai.chat.client.DefaultChatClient.initialize(DefaultChatClient.java:150)
```

## Debugging Techniques

### Diagnosing ChatClient Problems

Enable DEBUG logging for Spring AI packages: `logging.level.org.springframework.ai=DEBUG`. This shows the requests being built and responses being parsed. You'll see the actual messages and parameters being sent to the LLM.

Verify your `ChatModel` bean is properly configured. Check that the bean is created in your configuration and that it has all required properties (base URL, API key, model name). Use `@Autowired` to inject and log the bean to verify it's not null.

Inspect the `ChatOptions` being passed to the ChatClient. Default options might not match your expectations—each call can override defaults. Verify that temperature, max tokens, and other parameters are set to expected values.

### PromptTemplate Issues

PromptTemplate parsing errors often stem from syntax mistakes in the template string. The `{variable}` syntax must match exactly, including spaces. Use consistent variable naming and check for typos.

If prompts aren't being populated correctly, verify the `Map` passed to the template contains all required keys. Missing keys result in template variables staying in the output, which breaks downstream parsing.

Debug PromptTemplate by calling `render()` manually with test data to see the output before sending to the LLM. This isolates template issues from LLM interaction issues.

## Best Practices

Use the ChatClient builder to create a client that's specific to your use case. Don't share the same client across different prompt patterns. Create separate ChatClient instances with different default options for different use cases.

Always provide system prompts explicitly using `.system()` on the ChatClient. Don't rely on default system prompts unless you explicitly configure them.

Configure the ChatClient once at startup and inject it where needed. Don't create new clients for each request—connection pooling and initialization costs make this inefficient.

Use `@SystemPrompt` and `@UserPrompt` annotations for cleaner prompt management in services. These allow prompt templates to be externalized to messages.properties files, making them easier to modify without code changes.

Handle errors explicitly with specific exception handling. Spring AI throws different exception types for different failure modes—catch and handle each appropriately rather than using generic exception handlers.