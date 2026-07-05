package com.devops.orchestration;

public class RollingUpdateStrategy {
    private final int totalPods;
    private final int maxSurge;
    private final int maxUnavailable;

    public RollingUpdateStrategy(int totalPods, int maxSurge, int maxUnavailable) {
        this.totalPods = totalPods;
        this.maxSurge = maxSurge;
        this.maxUnavailable = maxUnavailable;
    }

    public void execute() {
        int targetPods = totalPods + maxSurge;
        int available = totalPods - maxUnavailable;

        System.out.println("Rolling update: " + totalPods + " pods, surge=" + maxSurge
            + ", unavailable=" + maxUnavailable);

        for (int i = 0; i < targetPods; i++) {
            if (i < available) {
                System.out.println("  Pod " + (i + 1) + " updated (new version)");
            } else {
                System.out.println("  Pod " + (i + 1) + " terminating (old version)");
            }
        }
    }

    public static void main(String[] args) {
        RollingUpdateStrategy strategy = new RollingUpdateStrategy(5, 1, 1);
        strategy.execute();
    }
}
