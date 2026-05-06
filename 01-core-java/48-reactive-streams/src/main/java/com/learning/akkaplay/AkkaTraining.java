package com.learning.akkaplay;

public class AkkaTraining {
    public static void main(String[] args) {
        System.out.println("=== Module 48: Reactive Streams & Akka ===");
        demonstrateAkka();
    }

    private static void demonstrateAkka() {
        System.out.println("\n--- Akka ---");
        System.out.println("Actor Model: Immutable messages, isolation");
        System.out.println("Supervision: Fault tolerance, hierarchy");
        System.out.println("Akka Cluster: Distributed actors");
        System.out.println("Akka Persistence: Event sourcing");
    }
}