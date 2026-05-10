package com.learning.lab.module53;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 53: Helidon Lab ===\n");

        System.out.println("1. Helidon MP (MicroProfile):");
        System.out.println("   - Jakarta EE MicroProfile");
        System.out.println("   - JAX-RS, CDI, JSON-P/B");
        System.out.println("   - Config, Health, Metrics");

        System.out.println("\n2. Helidon NP (Nima/Project Loom):");
        System.out.println("   - Virtual threads (Project Loom)");
        System.out.println("   - Non-blocking I/O");
        System.out.println("   - High throughput");

        System.out.println("\n3. Health Checks:");
        System.out.println("   - @Readiness: Liveness probe");
        System.out.println("   - @Liveness: Ready to serve");
        System.out.println("   - /health/live, /health/ready");

        System.out.println("\n4. Metrics:");
        System.out.println("   - MicroProfile Metrics");
        System.out.println("   - /metrics endpoint");
        System.out.println("   - Counter, Gauge, Timer, Meter");

        System.out.println("\n5. Database Access:");
        System.out.println("   - Helidon DB Client");
        System.out.println("   - Reactive database access");
        System.out.println("   - Health checks for DB");

        System.out.println("\n6. gRPC Support:");
        System.out.println("   - gRPC server");
        System.out.println("   - gRPC client");
        System.out.println("   - Protobuf integration");

        System.out.println("\n7. CLI Tool:");
        System.out.println("   - helidon CLI for dev");
        System.out.println("   - helidon build");

        System.out.println("\n=== Helidon Lab Complete ===");
    }
}