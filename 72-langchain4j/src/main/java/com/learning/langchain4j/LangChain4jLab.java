package com.learning.langchain4j;

public class LangChain4jLab {

    public static void main(String[] args) {
        System.out.println("=== LangChain4j Integration Lab ===\n");

        System.out.println("1. Basic LLM Chat:");
        System.out.println("   ChatLanguageModel model = ChatLanguageModel.builder()");
        System.out.println("       .apiKey(System.getenv(\"OPENAI_API_KEY\"))");
        System.out.println("       .modelName(\"gpt-4\")");
        System.out.println("       .temperature(0.7)");
        System.out.println("       .build();");
        System.out.println("   String answer = model.chat(\"What is Java?\");");

        System.out.println("\n2. Prompt Templates:");
        System.out.println("   PromptTemplate template = PromptTemplate.from(");
        System.out.println("       \"Answer the question: {{question}}\");");
        System.out.println("   Prompt prompt = template.apply(Map.of(\"question\", \"What is AI?\"));");
        System.out.println("   String response = model.chat(prompt.text());");

        System.out.println("\n3. Chat Memory:");
        System.out.println("   ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);");
        System.out.println("   PersistentChatMemoryStore store = new PersistentChatMemoryStore();");

        System.out.println("\n4. Document Processing:");
        System.out.println("   DocumentParser parser = new ApachePdfBoxParser();");
        System.out.println("   Document document = parser.parse(\"file.pdf\");");
        System.out.println("   DocumentSplitter splitter = DocumentSplitters.recursive(");
        System.out.println("       1000, 200, new SentenceSplitter());");

        System.out.println("\n5. RAG (Retrieval-Augmented Generation):");
        System.out.println("   EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();");
        System.out.println("   EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();");
        System.out.println("   ContentRetriever retriever = new EmbeddingStoreContentRetriever(");
        System.out.println("       embeddingStore, embeddingModel, 3);");

        System.out.println("\n6. AI Services:");
        System.out.println("   interface CustomerSupportAgent {");
        System.out.println("       @SystemMessage(\"You are a helpful assistant\")");
        System.out.println("       String chat(@UserMessage String message);");
        System.out.println("   }");
        System.out.println("   CustomerSupportAgent agent = AiServices.create(");
        System.out.println("       CustomerSupportAgent.class, model);");

        System.out.println("\n7. Streaming:");
        System.out.println("   model.chat(\"Tell me a story\", new StreamingResponseHandler() {");
        System.out.println("       void onNext(String token) { System.out.print(token); }");
        System.out.println("       void onComplete() { System.out.println(); }");
        System.out.println("   });");

        System.out.println("\n=== LangChain4j Lab Complete ===");
    }
}