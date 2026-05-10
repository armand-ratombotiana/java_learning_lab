package com.learning.lab.module73;

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Module 73: Spring AI - ChatClient & Vector Stores");
        System.out.println("=".repeat(60));

        springAiOverview();
        chatClient();
        promptManagement();
        chatOptions();
        structuredOutput();
        imageGeneration();
        audioTranscription();
        vectorStores();
        embeddingModels();
        ragImplementation();
        evaluation();
        monitoring();
        bestPractices();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Module 73 Lab Complete!");
        System.out.println("=".repeat(60));
    }

    static void springAiOverview() {
        System.out.println("\n--- Spring AI Overview ---");
        System.out.println("Spring AI provides abstractions for AI integration in Spring applications");

        System.out.println("\nCore Features:");
        System.out.println("   - Portable AI APIs across providers");
        System.out.println("   - Chat, Image, Audio support");
        System.out.println("   - Vector store abstractions");
        System.out.println("   - RAG framework built-in");

        System.out.println("\nSupported Models:");
        String[] models = {"OpenAI", "Azure OpenAI", " Anthropic", "Google Gemini",
                         "HuggingFace", "Amazon Bedrock", "Meta Llama", "Mistral"};
        for (String model : models) {
            System.out.println("   - " + model);
        }

        System.out.println("\nArchitecture:");
        System.out.println("   +------------------+");
        System.out.println("   |  Application    |");
        System.out.println("   +--------+---------+");
        System.out.println("            |");
        System.out.println("   +--------v---------+");
        System.out.println("   |  Spring AI Core  |");
        System.out.println("   +--------+---------+");
        System.out.println("            |");
        System.out.println("   +--------v---------+");
        System.out.println("   | Model Clients    |");
        System.out.println("   +------------------+");

        System.out.println("\nDependencies:");
        System.out.println("   spring-ai-starter-chat-openai");
        System.out.println("   spring-ai-starter-embedding");
        System.out.println("   spring-ai-starter-vector-store");
    }

    static void chatClient() {
        System.out.println("\n--- ChatClient ---");
        System.out.println("Primary interface for LLM interactions in Spring AI");

        System.out.println("\nBasic Usage:");
        String basicPrompt = "Explain Spring Boot to a beginner";
        System.out.println("   Prompt: " + basicPrompt);
        System.out.println("   ChatClient chatClient = ChatClient.create(chatModel);");
        System.out.println("   String response = chatClient.prompt()");
        System.out.println("       .user(\"" + basicPrompt + "\")");
        System.out.println("       .call()");
        System.out.println("       .content();");

        System.out.println("\nChatClient Builder:");
        System.out.println("   ChatClient.builder(chatModel)");
        System.out.println("       .defaultSystem(\"You are a helpful assistant\")");
        System.out.println("       .defaultUser(\"User prompt here\")");
        System.out.println("       .build()");

        System.out.println("\nStreaming Response:");
        System.out.println("   Flux<String> stream = chatClient.prompt()");
        System.out.println("       .user(\"Write a story\")");
        System.out.println("       .stream()");
        System.out.println("       .content();");
        System.out.println("   stream.subscribe(token -> System.out.print(token));");

        System.out.println("\nMethod Chaining:");
        String chainExample = chatClientPromptExample();
        System.out.println("   " + chainExample);
    }

    static String chatClientPromptExample() {
        return "prompt().user(u -> u.text(\"question\").media(MimeType.IMAGE, imageData)).call().content()";
    }

    static void promptManagement() {
        System.out.println("\n--- Prompt Management ---");
        System.out.println("Structured way to build prompts with system messages and options");

        System.out.println("\nPrompt Structure:");
        System.out.println("   Prompt = SystemMessage + UserMessage(s) + Options");

        System.out.println("\nSystem Message:");
        String systemPrompt = "You are an expert Java developer. Provide detailed, working code examples.";
        System.out.println("   " + systemPrompt);
        System.out.println("   PromptTemplate template = new PromptTemplate(systemPrompt);");

        System.out.println("\nUser Message with Variables:");
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("topic", "Spring Security");
        variables.put("level", "intermediate");
        System.out.println("   Variables: " + variables);

        String userTemplate = "Create a tutorial about {topic} for {level} developers";
        System.out.println("   Template: " + userTemplate);

        System.out.println("\nPromptTemplate Usage:");
        System.out.println("   PromptTemplate template = new PromptTemplate(userTemplate);");
        System.out.println("   Prompt prompt = template.create(Map.of(\"topic\", \"REST APIs\", \"level\", \"beginner\"));");

        System.out.println("\nSystem Prompt Options:");
        System.out.println("   - temperature: Creativity level");
        System.out.println("   - maxTokens: Response length");
        System.out.println("   - topP: Nucleus sampling");
        System.out.println("   - frequencyPenalty: Repetition control");
        System.out.println("   - presencePenalty: Topic diversity");
    }

    static void chatOptions() {
        System.out.println("\n--- Chat Options ---");
        System.out.println("Configure model behavior with various options");

        System.out.println("\n1. Common Options:");
        Map<String, Object> commonOptions = new LinkedHashMap<>();
        commonOptions.put("temperature", 0.7);
        commonOptions.put("maxTokens", 1000);
        commonOptions.put("topP", 0.9);
        commonOptions.put("frequencyPenalty", 0.0);
        commonOptions.put("presencePenalty", 0.0);
        System.out.println("   " + commonOptions);

        System.out.println("\n2. Model-Specific Options:");
        System.out.println("   OpenAI: model, seed, responseFormat");
        System.out.println("   Gemini: temperature, maxOutputTokens, topK, topP");
        System.out.println("   Claude: maxTokens, temperature, topP, stopSequences");

        System.out.println("\n3. Applying Options:");
        System.out.println("   chatClient.prompt()");
        System.out.println("       .options(ChatOptions.builder()");
        System.out.println("           .temperature(0.8)");
        System.out.println("           .maxTokens(500)");
        System.out.println("           .build())");
        System.out.println("       .user(\"Write code\")");
        System.out.println("       .call()");

        System.out.println("\n4. Default Options (application.properties):");
        System.out.println("   spring.ai.openai.chat.options.temperature=0.7");
        System.out.println("   spring.ai.openai.chat.options.max-tokens=1000");
        System.out.println("   spring.ai.openai.chat.options.model=gpt-4");

        System.out.println("\n5. Runtime Override:");
        System.out.println("   PromptOptions options = new PromptOptions()");
        System.out.println("       .withTemperature(0.5)");
        System.out.println("       .withMaxTokens(2000)");
    }

    static void structuredOutput() {
        System.out.println("\n--- Structured Output ---");
        System.out.println("Parse LLM responses into typed objects");

        System.out.println("\nOutput Converter:");
        System.out.println("   BeanOutputConverter<Movie> converter = new BeanOutputConverter<>(Movie.class);");

        System.out.println("\nPOJO Definition:");
        System.out.println("   public class Movie {");
        System.out.println("       private String title;");
        System.out.println("       private int year;");
        System.out.println("       private String genre;");
        System.out.println("       private double rating;");
        System.out.println("   }");

        System.out.println("\nUsage:");
        System.out.println("   Movie movie = chatClient.prompt()");
        System.out.println("       .user(\"Recommend a sci-fi movie\")");
        System.out.println("       .outputConverter(converter)");
        System.out.println("       .call()");
        System.out.println("       .entity();");

        System.out.println("\nJSON Schema Output:");
        System.out.println("   Map<String, Object> schema = Map.of(");
        System.out.println("       \"type\", \"object\",");
        System.out.println("       \"properties\", Map.of(");
        System.out.println("           \"title\", Map.of(\"type\", \"string\"),");
        System.out.println("           \"rating\", Map.of(\"type\", \"number\")))");
        System.out.println("   );");

        System.out.println("\nStreaming with Structured Output:");
        System.out.println("   Flux<Movie> stream = chatClient.prompt()");
        System.out.println("       .user(\"List movies\")");
        System.out.println("       .stream()");
        System.out.println("       .entities(Movie.class);");
    }

    static void imageGeneration() {
        System.out.println("\n--- Image Generation ---");
        System.out.println("Generate images using DALL-E and other models");

        System.out.println("\nImage Model Configuration:");
        System.out.println("   @Bean");
        System.out.println("   public ImageModel imageModel() {");
        System.out.println("       return new OpenAiImageModel(new OpenAiImageOptions());");
        System.out.println("   }");

        System.out.println("\nImage Generation:");
        System.out.println("   ImageClient imageClient = ImageClient.builder()");
        System.out.println("       .imageModel(imageModel)");
        System.out.println("       .build();");
        System.out.println("   ");
        System.out.println("   ImageResponse response = imageClient.call(");
        System.out.println("       new ImagePrompt(\"A futuristic city\",");
        System.out.println("           ImageOptions.builder()");
        System.out.println("               .n(1)");
        System.out.println("               .size(ImageSize.SIZE_1024x1024)");
        System.out.println("               .quality(ImageQuality.HD)");
        System.out.println("               .build()));");

        System.out.println("\nImage Options:");
        Map<String, Object> imageOptions = new LinkedHashMap<>();
        imageOptions.put("n", 4);
        imageOptions.put("size", "1024x1024");
        imageOptions.put("quality", "hd");
        imageOptions.put("style", "vivid");
        System.out.println("   " + imageOptions);

        System.out.println("\nResponse Handling:");
        System.out.println("   for (Image generation : response.getResults()) {");
        System.out.println("       URI url = generation.getImage();");
        System.out.println("       // Save or display image");
        System.out.println("   }");
    }

    static void audioTranscription() {
        System.out.println("\n--- Audio Transcription ---");
        System.out.println("Convert audio to text using speech-to-text models");

        System.out.println("\nTranscription Setup:");
        System.out.println("   SpeechTextModel speechModel = new OpenAiSpeechTextModel(");
        System.out.println("       OpenAiSpeechTextOptions.builder()");
        System.out.println("           .language(\"en\")");
        System.out.println("           .build());");

        System.out.println("\nTranscription Usage:");
        System.out.println("   AudioTranscriptionPrompt request = new AudioTranscriptionPrompt(");
        System.out.println("       new ClassPathResource(\"speech.wav\"),");
        System.out.println("       MimeTypeUtils.APPLICATION_OCTET_STREAM);");
        System.out.println("   ");
        System.out.println("   AudioTranscriptionResponse response = speechModel.call(request);");
        System.out.println("   String text = response.getResult().getOutput().getText();");

        System.out.println("\nOptions:");
        System.out.println("   - language: Language code (en, es, fr, etc.)");
        System.out.println("   - prompt: Context for better transcription");
        System.out.println("   - responseFormat: json, text, srt, vtt");
        System.out.println("   - temperature: Creativity/accuracy balance");

        System.out.println("\nSupported Formats:");
        String[] formats = {"mp3", "mp4", "m4a", "wav", "webm", "ogg"};
        System.out.println("   " + Arrays.toString(formats));
    }

    static void vectorStores() {
        System.out.println("\n--- Vector Stores ---");
        System.out.println("Storage and retrieval of embeddings for RAG");

        System.out.println("\nSupported Vector Stores:");

        System.out.println("\n1. In-Memory:");
        System.out.println("   - For development/testing");
        System.out.println("   - No persistence");
        System.out.println("   InMemoryVectorStore store = new InMemoryVectorStore(embeddingModel);");

        System.out.println("\n2. PostgreSQL (pgvector):");
        System.out.println("   - Uses existing PostgreSQL");
        System.out.println("   - Full SQL capabilities");
        System.out.println("   spring.ai.vector.store.pgvector.enabled=true");

        System.out.println("\n3. Milvus:");
        System.out.println("   - Cloud-native scaling");
        System.out.println("   - High performance");
        System.out.println("   MilvusVectorStore store = MilvusVectorStore.builder()");

        System.out.println("\n4. Pinecone:");
        System.out.println("   - Managed service");
        System.out.println("   - Serverless option");
        System.out.println("   PineconeVectorStore store = PineconeVectorStore.builder()");

        System.out.println("\n5. Qdrant:");
        System.out.println("   - Fast, Rust-based");
        System.out.println("   - Local or cloud");
        System.out.println("   QdrantVectorStore store = QdrantVectorStore.builder()");

        System.out.println("\nVector Store Operations:");
        System.out.println("   1. add(Document): Store document");
        System.out.println("   2. delete(id): Remove document");
        System.out.println("   3. similaritySearch(SearchRequest): Find similar");
        System.out.println("   4. exists(id): Check existence");
    }

    static void embeddingModels() {
        System.out.println("\n--- Embedding Models ---");
        System.out.println("Convert text to vector representations");

        System.out.println("\nEmbedding Configuration:");
        System.out.println("   @Bean");
        System.out.println("   public EmbeddingModel embeddingModel() {");
        System.out.println("       return new OpenAiEmbeddingModel(");
        System.out.println("           new OpenAiEmbeddingOptions()");
        System.out.println("               .withModel(\"text-embedding-ada-002\")");
        System.out.println("       );");
        System.out.println("   }");

        System.out.println("\nEmbedding Options:");
        Map<String, Object> embedOptions = new LinkedHashMap<>();
        embedOptions.put("model", "text-embedding-3-small");
        embedOptions.put("dimensions", 1536);
        embedOptions.put("encodingFormat", "float");
        System.out.println("   " + embedOptions);

        System.out.println("\nEmbedding Usage:");
        System.out.println("   EmbeddingRequest request = new EmbeddingRequest(");
        System.out.println("       List.of(\"Hello\", \"World\"),");
        System.out.println("       EmbeddingOptions.empty()");
        System.out.println("   );");
        System.out.println("   EmbeddingResponse response = embeddingModel.call(request);");
        System.out.println("   List<Embedding> embeddings = response.getResults();");

        System.out.println("\nEmbedding Dimensions:");
        System.out.println("   - text-embedding-ada-002: 1536");
        System.out.println("   - text-embedding-3-small: 1536");
        System.out.println("   - text-embedding-3-large: 3072");

        System.out.println("\nBatch Embedding:");
        System.out.println("   List<String> texts = loadDocuments();");
        System.out.println("   EmbeddingRequest batchRequest = new EmbeddingRequest(texts, options);");
    }

    static void ragImplementation() {
        System.out.println("\n--- RAG Implementation ---");
        System.out.println("Complete Retrieval-Augmented Generation workflow");

        System.out.println("\nRAG Components:");

        System.out.println("\n1. Document Loading:");
        System.out.println("   TextDocumentReader reader = new TextDocumentReader(");
        System.out.println("       new ClassPathResource(\"docs/guide.txt\"));");
        System.out.println("   List<Document> documents = reader.read();");

        System.out.println("\n2. Text Splitting:");
        System.out.println("   DocumentSplitter splitter = new TokenTextSplitter(512, 100);");
        System.out.println("   List<Document> chunks = splitter.split(documents);");

        System.out.println("\n3. Embedding & Storage:");
        System.out.println("   List<Document> documents = ...;");
        System.out.println("   for (Document doc : documents) {");
        System.out.println("       doc.setEmbedding(embeddingModel.embed(doc.getText()));");
        System.out.println("       vectorStore.add(doc);");
        System.out.println("   }");

        System.out.println("\n4. Retrieval:");
        System.out.println("   String query = \"How to configure Spring Security?\";");
        System.out.println("   Embedding queryEmbedding = embeddingModel.embed(query);");
        System.out.println("   ");
        System.out.println("   SearchRequest searchRequest = SearchRequest.builder()");
        System.out.println("       .query(query)");
        System.out.println("       .topK(5)");
        System.out.println("       .similarityThreshold(0.7)");
        System.out.println("       .build();");
        System.out.println("   ");
        System.out.println("   List<Document> results = vectorStore.similaritySearch(searchRequest);");

        System.out.println("\n5. Generation:");
        System.out.println("   String context = results.stream()");
        System.out.println("       .map(Document::getText)");
        System.out.println("       .collect(Collectors.joining(\"\\n\\n\"));");
        System.out.println("   ");
        System.out.println("   String prompt = \"Based on: \" + context + \"\\nAnswer: \" + query;");
        System.out.println("   String answer = chatClient.prompt(prompt).call().content();");
    }

    static void evaluation() {
        System.out.println("\n--- Evaluation ---");
        System.out.println("Assess RAG system quality");

        System.out.println("\nEvaluation Metrics:");

        System.out.println("\n1. Retrieval Metrics:");
        double precision = 0.85;
        double recall = 0.78;
        double mrr = 0.92;
        System.out.printf("   Precision: %.2f%n", precision);
        System.out.printf("   Recall: %.2f%n", recall);
        System.out.printf("   MRR (Mean Reciprocal Rank): %.2f%n", mrr);

        System.out.println("\n2. Generation Metrics:");
        System.out.println("   - Context Precision: Relevant context retrieved");
        System.out.println("   - Context Recall: All relevant info retrieved");
        System.out.println("   - Faithfulness: Answer matches retrieved context");
        System.out.println("   - Answer Relevance: Answer addresses question");

        System.out.println("\n3. Answer Quality:");
        System.out.println("   - Correctness: Factual accuracy");
        System.out.println("   - Coherence: Logical consistency");
        System.out.println("   - Helpfulness: Utility for user");

        System.out.println("\nEvaluation Approach:");
        System.out.println("   1. Create test dataset (Q&A pairs)");
        System.out.println("   2. Run RAG pipeline");
        System.out.println("   3. Compare outputs with references");
        System.out.println("   4. Compute metrics");
        System.out.println("   5. Iterate on improvements");

        System.out.println("\nAutomated Evaluation:");
        System.out.println("   - Use LLM as judge");
        System.out.println("   - Compare multiple responses");
        System.out.println("   - Track over time");
    }

    static void monitoring() {
        System.out.println("\n--- Monitoring & Observability ---");
        System.out.println("Track AI application performance and behavior");

        System.out.println("\nMetrics Collection:");
        System.out.println("   1. Request metrics: Latency, tokens, errors");
        System.out.println("   2. Model metrics: Temperature, iterations");
        System.out.println("   3. Business metrics: Task completion rate");

        System.out.println("\nKey Metrics:");
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("llm.latency.p50", "150ms");
        metrics.put("llm.latency.p99", "500ms");
        metrics.put("llm.tokens.total", "10K/day");
        metrics.put("llm.errors.rate", "0.1%");
        metrics.put("rag.retrieval.recall", "82%");
        System.out.println("   " + metrics);

        System.out.println("\nLogging:");
        System.out.println("   - Log prompts and responses (with PII handling)");
        System.out.println("   - Log token usage for cost tracking");
        System.out.println("   - Log retrieval results for debugging");

        System.out.println("\nTracing:");
        System.out.println("   - Trace: full RAG pipeline");
        System.out.println("   - Span: individual components");
        System.out.println("   - Attributes: model, temperature, tokens");

        System.out.println("\nAlerting:");
        System.out.println("   - High error rate");
        System.out.println("   - Latency spikes");
        System.out.println("   - Cost threshold exceeded");
        System.out.println("   - Quality degradation");
    }

    static void bestPractices() {
        System.out.println("\n--- Best Practices ---");
        System.out.println("Guidelines for production Spring AI applications");

        System.out.println("\n1. Error Handling:");
        System.out.println("   - Graceful degradation");
        System.out.println("   - Retry with exponential backoff");
        System.out.println("   - Circuit breaker pattern");

        System.out.println("\n2. Security:");
        System.out.println("   - API keys in secure vault");
        System.out.println("   - Input sanitization");
        System.out.println("   - PII handling");
        System.out.println("   - Rate limiting");

        System.out.println("\n3. Cost Optimization:");
        System.out.println("   - Cache frequent queries");
        System.out.println("   - Use appropriate model tiers");
        System.out.println("   - Optimize token usage");
        System.out.println("   - Monitor usage patterns");

        System.out.println("\n4. Performance:");
        System.out.println("   - Async processing where possible");
        System.out.println("   - Connection pooling");
        System.out.println("   - Batch embedding operations");
        System.out.println("   - Streaming for large responses");

        System.out.println("\n5. Testing:");
        System.out.println("   - Unit test prompts");
        System.out.println("   - Integration test full pipeline");
        System.out.println("   - A/B testing for model comparison");
        System.out.println("   - Regression tests for updates");

        System.out.println("\n6. Deployment:");
        System.out.println("   - Environment-based configuration");
        System.out.println("   - Feature flags for model switching");
        System.out.println("   - Health checks for AI services");
        System.out.println("   - Blue-green deployments");
    }
}