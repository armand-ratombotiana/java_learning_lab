package com.devops.orchestration;

public class AutoScalingSimulation {
    private int currentReplicas;
    private final int minReplicas;
    private final int maxReplicas;
    private double cpuThreshold;

    public AutoScalingSimulation(int min, int max, double threshold) {
        this.minReplicas = min;
        this.maxReplicas = max;
        this.cpuThreshold = threshold;
        this.currentReplicas = min;
    }

    public void evaluate(double currentCpu) {
        System.out.println("CPU: " + (currentCpu * 100) + "% | Replicas: " + currentReplicas);
        if (currentCpu > cpuThreshold && currentReplicas < maxReplicas) {
            currentReplicas++;
            System.out.println("  Scaling UP to " + currentReplicas);
        } else if (currentCpu < cpuThreshold * 0.5 && currentReplicas > minReplicas) {
            currentReplicas--;
            System.out.println("  Scaling DOWN to " + currentReplicas);
        } else {
            System.out.println("  No scaling action");
        }
    }

    public static void main(String[] args) {
        AutoScalingSimulation as = new AutoScalingSimulation(2, 10, 0.75);
        double[] cpuLoads = {0.3, 0.6, 0.8, 0.9, 0.85, 0.4, 0.3};

        for (double cpu : cpuLoads) {
            as.evaluate(cpu);
        }
    }
}
