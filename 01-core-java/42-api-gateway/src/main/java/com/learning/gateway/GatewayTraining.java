package com.learning.gateway;

public class GatewayTraining {
    public static void main(String[] args) {
        System.out.println("=== Module 42: API Gateway ===");
        demonstrateGateway();
    }

    private static void demonstrateGateway() {
        System.out.println("\n--- API Gateway Features ---");
        System.out.println("- Request routing");
        System.out.println("- Authentication & Authorization");
        System.out.println("- Rate limiting");
        System.out.println("- Request/Response transformation");
        System.out.println("- Circuit breaker");
        System.out.println("- Service discovery integration");
        System.out.println("\nTools: Spring Cloud Gateway, Kong, Nginx");
    }
}