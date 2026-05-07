package com.learning.llmintegration;

public class LLMIntegrationLab {

    public static void main(String[] args) {
        System.out.println("=== LLM Integration Lab ===\n");

        System.out.println("1. OpenAI API with Java:");
        System.out.println("   // Using OkHttp and Gson");
        System.out.println("   OkHttpClient client = new OkHttpClient();");
        System.out.println("   String json = \"{\\\"model\\\": \\\"gpt-4\\\", \\\"messages\\\": ");
        System.out.println("       [{\\\"role\\\": \\\"user\\\", \\\"content\\\": \\\"Hello\\\"}]}\";");
        System.out.println("   Request request = new Request.Builder()");
        System.out.println("       .url(\"https://api.openai.com/v1/chat/completions\")");
        System.out.println("       .header(\"Authorization\", \"Bearer \" + apiKey)");
        System.out.println("       .post(RequestBody.create(json, JSON))");
        System.out.println("       .build();");

        System.out.println("\n2. Ollama (Local LLMs):");
        System.out.println("   // Pull model: ollama pull llama2");
        System.out.println("   HttpClient client = HttpClient.newHttpClient();");
        System.out.println("   HttpRequest request = HttpRequest.newBuilder()");
        System.out.println("       .uri(URI.create(\"http://localhost:11434/api/generate\"))");
        System.out.println("       .header(\"Content-Type\", \"application/json\")");
        System.out.println("       .POST(HttpRequest.BodyPublishers.ofString(");
        System.out.println("           \"{\\\"model\\\": \\\"llama2\\\", \\\"prompt\\\": \\\"Hello\\\"}\"))");
        System.out.println("       .build();");
        System.out.println("   HttpResponse<String> response = client.send(request, ...);");

        System.out.println("\n3. Llama C++/Java Bindings:");
        System.out.println("   // llama-java provides JNI bindings");
        System.out.println("   LlamaModel model = new LlamaModel(");
        System.out.println("       new ModelParameters().setNContext(2048));");
        System.out.println("   model.load(\"models/llama-2-7b.gguf\");");
        System.out.println("   String output = model.generate(");
        System.out.println("       \"What is Java?\",");
        System.out.println("       new InferenceParameters().setNPredict(100));");

        System.out.println("\n4. HuggingFace Inference API:");
        System.out.println("   HttpClient client = HttpClient.newHttpClient();");
        System.out.println("   HttpRequest req = HttpRequest.newBuilder()");
        System.out.println("       .uri(URI.create(");
        System.out.println("           \"https://api-inference.huggingface.co/models/gpt2\"))");
        System.out.println("       .header(\"Authorization\", \"Bearer \" + apiKey)");
        System.out.println("       .POST(ofString(\"{\\\"inputs\\\": \\\"Hello\\\"}\"))");
        System.out.println("       .build();");

        System.out.println("\n5. Anthropic Claude with Java:");
        System.out.println("   // Messages API");
        System.out.println("   \"model\": \"claude-3-opus-20240229\",");
        System.out.println("   \"messages\": [{\"role\": \"user\", \"content\": \"Hello Claude\"}]");
        System.out.println("   // POST to https://api.anthropic.com/v1/messages");

        System.out.println("\n6. Azure OpenAI:");
        System.out.println("   // Uses Azure OpenAI endpoint");
        System.out.println("   String endpoint = \"https://my-resource.openai.azure.com\";");
        System.out.println("   // Custom header for API Key");
        System.out.println("   .header(\"api-key\", azureApiKey)");

        System.out.println("\n7. Streaming Responses:");
        System.out.println("   // Server-Sent Events (SSE)");
        System.out.println("   HttpRequest req = HttpRequest.newBuilder()");
        System.out.println("       .uri(URI.create(url))");
        System.out.println("       .header(\"Accept\", \"text/event-stream\")");
        System.out.println("       .POST(...)");
        System.out.println("       .build();");
        System.out.println("   client.send(req, BodyHandlers.ofLines())");
        System.out.println("       .body()");
        System.out.println("       .forEach(System.out::print);");

        System.out.println("\n8. LLM Use Cases:");
        System.out.println("   - Code generation and code review");
        System.out.println("   - Text summarization and translation");
        System.out.println("   - Chatbots and virtual assistants");
        System.out.println("   - Data extraction and classification");
        System.out.println("   - Question answering from documents");
        System.out.println("   - Content creation and editing");

        System.out.println("\n=== LLM Integration Lab Complete ===");
    }
}