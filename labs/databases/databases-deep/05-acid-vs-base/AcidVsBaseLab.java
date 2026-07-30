package com.databases.deep.lab05;

import java.util.*;
import java.util.concurrent.*;

/**
 * AcidVsBaseLab — demonstrates 2PC coordinator, Saga pattern with compensations,
 * and isolation level anomalies (write skew).
 */
public class AcidVsBaseLab {

    // --- 2PC Simulation ---
    static class Participant {
        final String name;
        boolean prepared = false;

        Participant(String name) { this.name = name; }

        boolean prepare() { prepared = true; return true; }
        void commit() { System.out.println(name + " committed"); }
        void abort() { prepared = false; System.out.println(name + " aborted"); }
    }

    static boolean twoPC(List<Participant> participants) {
        // Phase 1: prepare
        for (var p : participants) {
            if (!p.prepare()) {
                for (var p2 : participants) p2.abort();
                return false;
            }
        }
        // Phase 2: commit
        for (var p : participants) p.commit();
        return true;
    }

    // --- Saga Simulation ---
    static class SagaStep {
        final String name;
        final Runnable action;
        final Runnable compensate;

        SagaStep(String name, Runnable action, Runnable compensate) {
            this.name = name; this.action = action; this.compensate = compensate;
        }
    }

    static void executeSaga(List<SagaStep> steps) {
        Deque<SagaStep> executed = new ArrayDeque<>();
        try {
            for (var step : steps) {
                step.action.run();
                executed.push(step);
            }
        } catch (Exception e) {
            System.out.println("Saga failed at step: " + executed.peek().name + ", compensating...");
            for (var step : executed) step.compensate.run();
        }
    }

    // --- Write Skew Simulation ---
    static class DoctorShift {
        int onCall = 2;
    }

    public static void main(String[] args) {
        System.out.println("=== 2PC ===");
        var participants = List.of(new Participant("DB-1"), new Participant("DB-2"), new Participant("Queue"));
        boolean committed = twoPC(participants);
        System.out.println("2PC " + (committed ? "committed" : "aborted") + "\n");

        System.out.println("=== Saga ===");
        var saga = List.of(
            new SagaStep("ReserveInventory", () -> System.out.println("Inventory reserved"), () -> System.out.println("Inventory released")),
            new SagaStep("ProcessPayment", () -> System.out.println("Payment processed"), () -> System.out.println("Payment refunded")),
            new SagaStep("ShipOrder", () -> { throw new RuntimeException("Shipping failed"); }, () -> System.out.println("Shipment cancelled"))
        );
        executeSaga(saga);

        System.out.println("\n=== Write Skew Simulation ===");
        DoctorShift shift = new DoctorShift();
        // Thread 1 checks onCall > 1 and decrements
        // Thread 2 checks onCall > 1 and decrements
        // With Snapshot Isolation, both might see onCall=2 and both decrement -> onCall=0 (constraint violated)
        System.out.println("Initial on-call: " + shift.onCall);
        System.out.println("Under SI, two concurrent transactions could both decrement -> onCall=0 (write skew)");
    }
}