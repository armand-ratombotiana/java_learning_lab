package com.arch.saga;

public class SagaDemo {
    public static void main(String[] args) {
        SagaOrchestrator orchestrator = new SagaOrchestrator();

        orchestrator.addStep(new SagaStep() {
            public void execute(String ctx) { System.out.println("Reserved inventory for " + ctx); }
            public void compensate(String ctx) { System.out.println("Released inventory for " + ctx); }
        });

        orchestrator.addStep(new SagaStep() {
            public void execute(String ctx) { System.out.println("Charged payment for " + ctx); }
            public void compensate(String ctx) { System.out.println("Refunded payment for " + ctx); }
        });

        orchestrator.addStep(new SagaStep() {
            public void execute(String ctx) {
                throw new RuntimeException("Shipping service unavailable");
            }
            public void compensate(String ctx) { System.out.println("Compensated shipping for " + ctx); }
        });

        orchestrator.execute("ORDER-123");
    }
}
