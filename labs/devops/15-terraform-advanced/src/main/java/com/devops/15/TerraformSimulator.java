package com.devops.fifteen;

import java.time.Instant;
import java.util.Random;

public class TerraformSimulator {
    private final String name;
    private final Random random = new Random();
    private long operationCount = 0;

    public TerraformSimulator(String name) {
        this.name = name;
    }

    public SimulationResult simulateOperation(String operationType) {
        operationCount++;
        long startTime = System.currentTimeMillis();
        boolean success = random.nextDouble() > 0.1;

        try {
            Thread.sleep(random.nextInt(50) + 10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long duration = System.currentTimeMillis() - startTime;
        SimulationResult result = new SimulationResult(operationType, success, duration, Instant.now().toString());
        System.out.printf("[%s] %s operation '%s': %s (%dms)%n",
            result.timestamp(), name, operationType, success ? "SUCCESS" : "FAILURE", duration);
        return result;
    }

    public long getOperationCount() { return operationCount; }

    public record SimulationResult(String operationType, boolean success, long durationMs, String timestamp) {}

    public static void main(String[] args) {
        TerraformSimulator sim = new TerraformSimulator("TerraformSim");
        for (int i = 0; i < 5; i++) {
            sim.simulateOperation("deploy-" + (i + 1));
        }
        System.out.println("Total operations: " + sim.getOperationCount());
    }
}
