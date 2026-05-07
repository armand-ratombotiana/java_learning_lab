package com.learning.testcontainers;

public class TestContainersLab {

    public static void main(String[] args) {
        System.out.println("=== TestContainers Lab ===\n");

        System.out.println("1. PostgreSQL Container Example:");
        System.out.println("   try (PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(\"postgres:15-alpie\"))");
        System.out.println("       .withDatabaseName(\"testdb\")");
        System.out.println("       .withUsername(\"test\")");
        System.out.println("       .withPassword(\"test\")) {");
        System.out.println("       postgres.start();");
        System.out.println("       System.out.println(postgres.getJdbcUrl());");
        System.out.println("   }");

        System.out.println("\n2. TestContainers Concepts:");
        System.out.println("   - @Testcontainers annotation for JUnit 5");
        System.out.println("   - @Container annotation for container lifecycle");
        System.out.println("   - GenericContainer for custom Docker images");
        System.out.println("   - Network for multi-container setups");
        System.out.println("   - Wait strategies for readiness checks");

        System.out.println("\n=== TestContainers Lab Complete ===");
    }
}