package com.learning.springai;

public class SpringAILab {

    public static void main(String[] args) {
        System.out.println("=== Spring AI Framework Lab ===\n");

        System.out.println("1. Spring AI Configuration:");
        System.out.println("   spring.ai.openai.api-key=${OPENAI_API_KEY}");
        System.out.println("   spring.ai.openai.chat.model=gpt-4");
        System.out.println("   spring.ai.openai.chat.temperature=0.7");
        System.out.println("   spring.ai.openai.embedding.model=text-embedding-ada-002");

        System.out.println("\n2. Chat Client:");
        System.out.println("   @Service");
        System.out.println("   public class ChatService {");
        System.out.println("       private final ChatClient chatClient;");
        System.out.println("       public ChatService(ChatClient.Builder builder) {");
        System.out.println("           this.chatClient = builder.build();");
        System.out.println("       }");
        System.out.println("       public String chat(String message) {");
        System.out.println("           return chatClient.prompt()");
        System.out.println("               .user(u -> u.text(message))");
        System.out.println("               .call()");
        System.out.println("               .content();");
        System.out.println("       }");
        System.out.println("   }");

        System.out.println("\n3. Structured Output:");
        System.out.println("   record Person(String name, int age, String occupation) {}");
        System.out.println("   Person person = chatClient.prompt()");
        System.out.println("       .user(\"Extract: John is 30, a developer\")");
        System.out.println("       .call()");
        System.out.println("       .entity(Person.class);");

        System.out.println("\n4. Embedding Models:");
        System.out.println("   @Autowired EmbeddingModel embeddingModel;");
        System.out.println("   double[] embeddings = embeddingModel.embed(\"Spring AI\");");
        System.out.println("   List<Document> similar = vectorStore.similaritySearch(");
        System.out.println("       SearchRequest.query(\"Spring AI\").withTopK(5));");

        System.out.println("\n5. Document Processing:");
        System.out.println("   DocumentReader reader = new PdfDocumentReader(\"file.pdf\");");
        System.out.println("   DocumentTransformer transformer = new TextContentTransformer();");
        System.out.println("   DocumentWriter writer = new VectorStoreDocumentWriter(vectorStore);");

        System.out.println("\n6. Advisors & Templates:");
        System.out.println("   chatClient.prompt()");
        System.out.println("       .advisors(new QuestionAnswerAdvisor(vectorStore))");
        System.out.println("       .advisors(new LoggerAdvisor())");
        System.out.println("       .user(\"Tell me about the document\")");
        System.out.println("       .call();");

        System.out.println("\n7. Supported AI Providers:");
        System.out.println("   - OpenAI (GPT-3.5, GPT-4, GPT-4-turbo)");
        System.out.println("   - Anthropic (Claude 3 Opus, Sonnet, Haiku)");
        System.out.println("   - Google Vertex AI (Gemini, PaLM 2)");
        System.out.println("   - Ollama (Llama 2, Mistral, CodeLlama)");
        System.out.println("   - Azure OpenAI (GPT models on Azure)");
        System.out.println("   - HuggingFace (various open-source models)");

        System.out.println("\n=== Spring AI Lab Complete ===");
    }
}