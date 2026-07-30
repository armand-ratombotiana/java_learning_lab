package com.distributedsystems.deep.lab02;

import java.util.*;
import java.util.concurrent.*;

/**
 * DistributedTransactionsLab — implements 2PC, Saga orchestration,
 * and TCC patterns with simulated participants.
 */
public class DistributedTransactionsLab {

    // --- 2PC ---
    static class Participant {
        final String name;
        boolean prepared = false;
        Participant(String name) { this.name = name; }

        boolean prepare() { prepared = true; return true; }
        void commit() { System.out.println(name + " committed"); }
        void abort() { prepared = false; System.out.println(name + " aborted"); }
    }

    static boolean execute2PC(List<Participant> participants, boolean simulateFailure) {
        System.out.println("=== 2PC ===");
        for (var p : participants) {
            if (simulateFailure && p.name.equals("Payment")) {
                System.out.println(p.name + " failed to prepare!");
                for (var p2 : participants) p2.abort();
                return false;
            }
            if (!p.prepare()) { for (var p2 : participants) p2.abort(); return false; }
        }
        for (var p : participants) p.commit();
        return true;
    }

    // --- Saga ---
    record SagaStep(String name, Runnable action, Runnable compensate) {}

    static void executeSaga(List<SagaStep> steps, boolean failAfter) {
        System.out.println("\n=== Saga Orchestration ===");
        var executed = new ArrayDeque<SagaStep>();
        for (int i = 0; i < steps.size(); i++) {
            var step = steps.get(i);
            try {
                if (failAfter && i == 2) throw new RuntimeException("Saga failed at " + step.name());
                step.action().run();
                executed.push(step);
            } catch (Exception e) {
                System.out.println("Failure: " + e.getMessage());
                System.out.println("Compensating...");
                for (var s : executed) s.compensate().run();
                return;
            }
        }
        System.out.println("Saga completed successfully");
    }

    // --- TCC ---
    static class TCCService {
        final String name;
        String resourceState = "FREE";
        TCCService(String name) { this.name = name; }

        boolean tryPhase() { resourceState = "RESERVED"; return true; }
        boolean confirm() { resourceState = "CONFIRMED"; return true; }
        boolean cancel() { resourceState = "FREE"; return true; }
    }

    public static void main(String[] args) {
        var participants = List.of(new Participant("Inventory"), new Participant("Payment"), new Participant("Shipping"));
        execute2PC(participants, false);
        execute2PC(participants, true);

        var saga = List.of(
            new SagaStep("ReserveInventory", () -> System.out.println("  Inventory reserved"), () -> System.out.println("  Inventory released")),
            new SagaStep("ProcessPayment", () -> System.out.println("  Payment processed"), () -> System.out.println("  Payment refunded")),
            new SagaStep("UpdateLoyalty", () -> System.out.println("  Loyalty updated"), () -> System.out.println("  Loyalty rollback"))
        );
        executeSaga(saga, false);
        executeSaga(saga, true);

        System.out.println("\n=== TCC ===");
        var tcc = new TCCService("RoomBooking");
        tcc.tryPhase(); System.out.println("After Try: " + tcc.resourceState);
        tcc.confirm(); System.out.println("After Confirm: " + tcc.resourceState);
    }
}