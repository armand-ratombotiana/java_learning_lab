package com.learning.microservices;

import java.util.*;

public class MicroservicesTraining {

    public static void main(String[] args) {
        System.out.println("=== Microservices Architecture Training ===");

        demonstrateServiceDesign();
        demonstrateCommunicationPatterns();
        demonstrateContainersOrchestration();
        demonstrateServiceMesh();
    }

    private static void demonstrateServiceDesign() {
        System.out.println("\n--- Microservice Design Principles ---");

        String[] principles = {
            "Single Responsibility - one service, one domain",
            "Loose Coupling - services independent",
            "High Cohesion - related functionality together",
            "Business Boundaries - domain-driven design",
            "Infrastructure Agnostic - portable services"
        };

        for (int i = 0; i < principles.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, principles[i]);
        }

        System.out.println("\nService Decomposition Example:");
        Map<String, String> services = new LinkedHashMap<>();
        services.put("user-service", "Authentication, profiles, preferences");
        services.put("order-service", "Orders, carts, checkout");
        services.put("inventory-service", "Products, stock, warehouses");
        services.put("payment-service", "Transactions, billing, refunds");
        services.put("notification-service", "Email, SMS, push notifications");

        services.forEach((name, desc) -> System.out.printf("  %s: %s%n", name, desc));
    }

    private static void demonstrateCommunicationPatterns() {
        System.out.println("\n--- Inter-Service Communication ---");

        System.out.println("Synchronous (Request-Response):");
        String[] sync = {
            "REST - HTTP + JSON (most common)",
            "gRPC - Protocol Buffers (high performance)",
            "GraphQL - Query language for APIs"
        };
        for (String s : sync) System.out.println("  - " + s);

        System.out.println("\nAsynchronous (Event-Driven):");
        String[] async = {
            "Message Queues - RabbitMQ, Kafka",
            "Event Sourcing - immutable event logs",
            "CQRS - Command Query Responsibility Segregation"
        };
        for (String a : async) System.out.println("  - " + a);

        System.out.println("\nAPI Design Best Practices:");
        String[] practices = {
            "Use versioning (v1, v2)",
            "Implement proper error handling",
            "Add rate limiting and caching",
            "Document with OpenAPI/Swagger"
        };
        for (String p : practices) System.out.println("  - " + p);
    }

    private static void demonstrateContainersOrchestration() {
        System.out.println("\n--- Containers & Orchestration ---");

        System.out.println("Docker Fundamentals:");
        String[] docker = {
            "FROM - base image",
            "RUN - execute commands",
            "COPY/ADD - add files",
            "EXPOSE - port mapping",
            "CMD/ENTRYPOINT - run command"
        };
        for (String d : docker) System.out.println("  " + d);

        System.out.println("\nDocker Compose Example:");
        String compose = """
            services:
              api:
                build: .
                ports:
                  - "8080:8080"
              db:
                image: postgres:15
                volumes:
                  - data:/var/lib/postgresql/data
            """;
        System.out.println(compose);

        System.out.println("Kubernetes Concepts:");
        String[] k8s = {
            "Pod - smallest deployable unit",
            "Service - stable network endpoint",
            "Deployment - declarative updates",
            "ConfigMap/Secret - configuration",
            "Ingress - external access"
        };
        for (String k : k8s) System.out.println("  - " + k);
    }

    private static void demonstrateServiceMesh() {
        System.out.println("\n--- Service Mesh Features ---");

        Map<String, String> features = new LinkedHashMap<>();
        features.put("Service Discovery", "Dynamic service registration and lookup");
        features.put("Load Balancing", "Traffic distribution across instances");
        features.put("Circuit Breaker", "Prevent cascade failures");
        features.put("Observability", "Distributed tracing, metrics, logging");
        features.put("Security", "mTLS, authentication, authorization");

        features.forEach((name, desc) -> System.out.printf("  %s: %s%n", name, desc));

        System.out.println("\nPopular Service Meshes:");
        String[] meshes = {"Istio", "Linkerd", "Consul Connect", "AWS App Mesh"};
        for (String mesh : meshes) System.out.println("  - " + mesh);

        System.out.println("\nMicroservices Anti-Patterns:");
        String[] anti = {
            "Distributed Monolith - tightly coupled services",
            "Chatty APIs - too many small requests",
            "Shared Database - tight coupling via data",
            "Ignoring Failure - no circuit breakers"
        };
        for (String a : anti) System.out.println("  - " + a);
    }
}