# LLM Integration Quick Reference

## OpenAI API with Java
```java
OkHttpClient client = new OkHttpClient();
String json = "{\"model\": \"gpt-4\", \"messages\": "
    + "[{\"role\": \"user\", \"content\": \"Hello\"}]}";
Request request = new Request.Builder()
    .url("https://api.openai.com/v1/chat/completions")
    .header("Authorization", "Bearer " + apiKey)
    .post(RequestBody.create(json, JSON))
    .build();
```

## Ollama (Local LLMs)
```java
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("http://localhost:11434/api/generate"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(
        "{\"model\": \"llama2\", \"prompt\": \"Hello\"}"))
    .build();
```

## Llama C++/Java Bindings
```java
LlamaModel model = new LlamaModel(
    new ModelParameters().setNContext(2048));
model.load("models/llama-2-7b.gguf");
String output = model.generate(
    "What is Java?",
    new InferenceParameters().setNPredict(100));
```

## HuggingFace Inference API
```java
HttpRequest req = HttpRequest.newBuilder()
    .uri(URI.create("https://api-inference.huggingface.co/models/gpt2"))
    .header("Authorization", "Bearer " + apiKey)
    .POST(ofString("{\"inputs\": \"Hello\"}"))
    .build();
```

## Anthropic Claude
```java
// POST to https://api.anthropic.com/v1/messages
"model": "claude-3-opus-20240229",
"messages": [{"role": "user", "content": "Hello Claude"}]
```

## Azure OpenAI
```java
String endpoint = "https://my-resource.openai.azure.com";
.header("api-key", azureApiKey)
```

## Streaming Responses (SSE)
```java
HttpRequest req = HttpRequest.newBuilder()
    .uri(URI.create(url))
    .header("Accept", "text/event-stream")
    .POST(...)
    .build();
client.send(req, BodyHandlers.ofLines())
    .body()
    .forEach(System.out::print);
```

## Use Cases
- Code generation and code review
- Text summarization and translation
- Chatbots and virtual assistants
- Data extraction and classification
- Question answering from documents
- Content creation and editing