package com.devops.docker;

import java.util.concurrent.atomic.AtomicInteger;

public class HealthcheckSimulation {
    private final AtomicInteger healthCounter = new AtomicInteger(0);

    public boolean check() {
        int count = healthCounter.incrementAndGet();
        boolean healthy = count % 3 != 0;
        System.out.println("Healthcheck #" + count + ": " + (healthy ? "PASS" : "FAIL"));
        return healthy;
    }

    public static void main(String[] args) throws InterruptedException {
        HealthcheckSimulation hc = new HealthcheckSimulation();
        for (int i = 0; i < 6; i++) {
            if (!hc.check()) {
                System.out.println("Container marked unhealthy, restarting...");
            }
            Thread.sleep(500);
        }
    }
}
