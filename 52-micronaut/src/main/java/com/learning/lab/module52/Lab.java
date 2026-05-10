package com.learning.lab.module52;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 52: Micronaut Lab ===\n");

        System.out.println("1. Micronaut AOT (Ahead-of-Time):");
        System.out.println("   - Compile-time dependency injection");
        System.out.println("   - No reflection-based DI");
        System.out.println("   - Fast startup, low memory");

        System.out.println("\n2. Dependency Injection:");
        System.out.println("   - @Singleton, @Factory, @Bean");
        System.out.println("   - Configuration properties: @ConfigurationProperties");
        System.out.println("   - Qualifiers: @Named, @Requires");

        System.out.println("\n3. Data Access:");
        System.out.println("   - Micronaut Data JDBC");
        System.out.println("   - Micronaut Data R2DBC");
        System.out.println("   - Micronaut Hibernate Reactive");

        System.out.println("\n4. HTTP Client:");
        System.out.println("   - @Client annotation for declarative");
        System.out.println("   - Compile-time HTTP client generation");
        System.out.println("   - Non-blocking HTTP client");

        System.out.println("\n5. Messaging:");
        System.out.println("   - Kafka integration");
        System.out.println("   - RabbitMQ integration");
        System.out.println("   - Event publishing with @EventListener");

        System.out.println("\n6. GraalVM Native:");
        System.out.println("   - Native image compilation");
        System.out.println("   - mvn micronaut:build-image");
        System.out.println("   - Lambda deployment support");

        System.out.println("\n7. Testing:");
        System.out.println("   - @MicronautTest");
        System.out.println("   - Embedded server for tests");
        System.out.println("   - Testcontainers integration");

        System.out.println("\n=== Micronaut Lab Complete ===");
    }
}