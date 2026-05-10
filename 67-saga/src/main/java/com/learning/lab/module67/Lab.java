package com.learning.lab.module67;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 67: Saga Lab ===\n");

        System.out.println("1. Saga Pattern:");
        System.out.println("   - Distributed transaction alternative");
        System.out.println("   - Choreography vs Orchestration");
        System.out.println("   - Compensation-based transactions");
        System.out.println("   - Eventual consistency");

        System.out.println("\n2. Saga Steps:");
        System.out.println("   - Each step has forward action");
        System.out.println("   - Each step has compensating action");
        System.out.println("   - Execute forward, rollback if failed");
        System.out.println("   - Idempotent operations");

        System.out.println("\n3. Choreography:");
        System.out.println("   - Each service publishes events");
        System.out.println("   - Other services subscribe and react");
        System.out.println("   - No central coordinator");
        System.out.println("   - Distributed intelligence");

        System.out.println("\n4. Orchestration:");
        System.out.println("   - Central orchestrator manages flow");
        System.out.println("   - Command/Reply pattern");
        System.out.println("   - Clear visibility of process");
        System.out.println("   - Single point of failure (mitigate)");

        System.out.println("\n5. Compensation:");
        System.out.println("   - Reverse previous step");
        System.out.println("   - Sagas don't guarantee ACID");
        System.out.println("   - Semantic rollback");
        System.out.println("   - Saga state persisted");

        System.out.println("\n6. Failure Handling:");
        System.out.println("   - Retry with backoff");
        System.out.println("   - Circuit breaker");
        System.out.println("   - Dead letter queue");
        System.out.println("   - Alerting and monitoring");

        System.out.println("\n7. Implementation:");
        System.out.println("   - Eventuate Tram Saga");
        System.out.println("   - Axon Framework");
        System.out.println("   - Temporal workflow engine");

        System.out.println("\n=== Saga Lab Complete ===");
    }
}