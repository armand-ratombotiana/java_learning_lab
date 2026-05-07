package com.learning.saga;

public class SagaLab {

    public static void main(String[] args) {
        System.out.println("=== Saga Pattern Lab ===\n");

        System.out.println("1. Saga Flow:");
        System.out.println("   Order Service: Create order -> Payment Service");
        System.out.println("   Payment Service: Process payment -> Inventory Service");
        System.out.println("   Inventory Service: Reserve stock -> Shipping Service");
        System.out.println("   Shipping Service: Create shipment -> Order Service (confirm)");

        System.out.println("\n2. Compensating Transactions (Rollback):");
        System.out.println("   If Payment fails -> Cancel order");
        System.out.println("   If Inventory fails -> Refund payment, Cancel order");
        System.out.println("   If Shipping fails -> Release inventory, Refund payment, Cancel order");

        System.out.println("\n3. Implementation:");
        System.out.println("   - Choreography: Events trigger each step");
        System.out.println("   - Orchestration: Central coordinator manages flow");

        System.out.println("\n=== Saga Pattern Lab Complete ===");
    }
}