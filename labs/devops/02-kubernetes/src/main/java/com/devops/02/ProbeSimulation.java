package com.devops.kubernetes;

import java.util.Random;

public class ProbeSimulation {
    private final Random random = new Random();

    public boolean livenessProbe() {
        boolean alive = random.nextInt(10) < 9;
        System.out.println("Liveness probe: " + (alive ? "ALIVE" : "DEAD"));
        return alive;
    }

    public boolean readinessProbe() {
        boolean ready = random.nextInt(10) < 8;
        System.out.println("Readiness probe: " + (ready ? "READY" : "NOT_READY"));
        return ready;
    }

    public static void main(String[] args) {
        ProbeSimulation probe = new ProbeSimulation();
        for (int i = 0; i < 5; i++) {
            if (!probe.livenessProbe()) {
                System.out.println("Kubelet restarting container...");
            }
            if (!probe.readinessProbe()) {
                System.out.println("Removed from service endpoints");
            }
            System.out.println("---");
        }
    }
}
