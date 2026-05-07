package com.learning.ai;

public class AILab {

    public static void main(String[] args) {
        System.out.println("=== Java AI Integration Lab ===\n");

        System.out.println("1. AI Integration Options:");
        System.out.println("   - Spring AI: Spring ecosystem AI integration");
        System.out.println("   - Langchain4j: Java port of LangChain");
        System.out.println("   - OpenAI Java SDK: Direct API access");

        System.out.println("\n2. Spring AI Example:");
        System.out.println("   ChatClient chatClient = ChatClient.builder(openAiChatModel).build();");
        System.out.println("   String response = chatClient.prompt()");
        System.out.println("       .user(\"What is Java?\")");
        System.out.println("       .call()");
        System.out.println("       .content();");

        System.out.println("\n3. Use Cases:");
        System.out.println("   - Chatbots and conversational interfaces");
        System.out.println("   - Code generation and analysis");
        System.out.println("   - Document summarization");
        System.out.println("   - Sentiment analysis");
        System.out.println("   - Data extraction");

        System.out.println("\n=== Java AI Integration Lab Complete ===");
    }
}