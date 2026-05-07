package com.learning.serverless.functions;

public class ServerlessLab {

    public static void main(String[] args) {
        System.out.println("=== Serverless Functions Lab ===\n");

        System.out.println("1. Java Function Example:");
        System.out.println("   @Bean");
        System.out.println("   public Function<String, String> uppercase() {");
        System.out.println("       return value -> value.toUpperCase();");
        System.out.println("   }");

        System.out.println("\n2. Cloud Functions:");
        System.out.println("   - AWS Lambda: Handler implements RequestHandler");
        System.out.println("   - Google Cloud Functions: HttpFunction interface");
        System.out.println("   - Azure Functions: @FunctionName annotation");

        System.out.println("\n3. Serverless Framework:");
        System.out.println("   service: my-java-function");
        System.out.println("   provider:");
        System.out.println("     name: aws");
        System.out.println("     runtime: java21");
        System.out.println("   functions:");
        System.out.println("     hello:");
        System.out.println("       handler: com.example.HelloHandler");

        System.out.println("\n=== Serverless Functions Lab Complete ===");
    }
}