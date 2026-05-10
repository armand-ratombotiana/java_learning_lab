package com.learning.lab.module54;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 54: Vert.x Lab ===\n");

        System.out.println("1. Vert.x Core Concepts:");
        System.out.println("   - Event loop model");
        System.out.println("   - Non-blocking I/O");
        System.out.println("   - Polyglot: Java, JS, Groovy, etc.");

        System.out.println("\n2. Event Bus:");
        System.out.println("   - Local event bus: eventbus.publish()");
        System.out.println("   - Point-to-point messaging");
        System.out.println("   - Pub/sub with addresses");
        System.out.println("   - Message headers and codecs");

        System.out.println("\n3. Verticles:");
        System.out.println("   - AbstractVerticle class");
        System.out.println("   - Deployment: vertx.deployVerticle()");
        System.out.println("   - Scaling: multiple instances");
        System.out.println("   - Types: Standard, Worker, Multi-threaded");

        System.out.println("\n4. Web Client:");
        System.out.println("   - VertxWebClient for HTTP");
        System.out.println("   - Non-blocking REST calls");
        System.out.println("   - Automatic retry");

        System.out.println("\n5. Database Integration:");
        System.out.println("   - MySQL/PostgreSQL clients");
        System.out.println("   - MongoDB client");
        System.out.println("   - Redis client");

        System.out.println("\n6. Circuit Breaker:");
        System.out.println("   - Resilience pattern");
        System.out.println("   - Fallback on failure");
        System.out.println("   - Configurable thresholds");

        System.out.println("\n7. Cluster Manager:");
        System.out.println("   - Hazelcast clustering");
        System.out.println("   - Infinispan clustering");
        System.out.println("   - Zookeeper clustering");

        System.out.println("\n=== Vert.x Lab Complete ===");
    }
}