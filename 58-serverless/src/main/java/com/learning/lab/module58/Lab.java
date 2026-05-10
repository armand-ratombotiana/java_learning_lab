package com.learning.lab.module58;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 58: Serverless Lab ===\n");

        System.out.println("1. AWS Lambda:");
        System.out.println("   - Handler: RequestStreamHandler");
        System.out.println("   - Context: LambdaLogger, AWSRequest");
        System.out.println("   - Runtime: Java 17/21");

        System.out.println("\n2. Spring Cloud Functions:");
        System.out.println("   - @FunctionName annotation");
        System.out.println("   - Universal function adapter");
        System.out.println("   - Local and cloud deployment");

        System.out.println("\n3. Function Deployment:");
        System.out.println("   - SAM CLI: sam deploy");
        System.out.println("   - Serverless Framework");
        System.out.println("   - CDK for infrastructure");

        System.out.println("\n4. Cold Start Optimization:");
        System.out.println("   - GraalVM native image");
        System.out.println("   - Provisioned concurrency");
        System.out.println("   - SnapStart (Java)");

        System.out.println("\n5. Triggers:");
        System.out.println("   - API Gateway: HTTP endpoints");
        System.out.println("   - S3: File events");
        System.out.println("   - SQS: Queue messages");
        System.out.println("   - CloudWatch: Scheduled");

        System.out.println("\n6. Serverless Patterns:");
        System.out.println("   - Lambda + SQS");
        System.out.println("   - Step Functions workflow");
        System.out.println("   - Event-driven architecture");

        System.out.println("\n7. Local Development:");
        System.out.println("   - SAM Local");
        System.out.println("   - Test containers");
        System.out.println("   - Offline simulation");

        System.out.println("\n=== Serverless Lab Complete ===");
    }
}