package com.learning.springmodulith;

public class SpringModulithLab {

    public static void main(String[] args) {
        System.out.println("=== Spring Modulith Lab ===\n");

        System.out.println("1. Module Structure:");
        System.out.println("   @ApplicationModule(displayName = \"Order Management\")");
        System.out.println("   class OrderModule {");
        System.out.println("       Order createOrder(String product, double amount) { ... }");
        System.out.println("   }");

        System.out.println("\n2. Module Dependencies:");
        System.out.println("   @ApplicationModule(dependencies = \"order-management\")");
        System.out.println("   class PaymentModule {");
        System.out.println("       Payment processPayment(String orderId, String method) { ... }");
        System.out.println("   }");

        System.out.println("\n3. Benefits:");
        System.out.println("   - Modular monolith architecture");
        System.out.println("   - Clear module boundaries");
        System.out.println("   - Easy migration to microservices");
        System.out.println("   - Module verification at build time");

        System.out.println("\n=== Spring Modulith Lab Complete ===");
    }
}