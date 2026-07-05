package com.sd.scalability;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class AutoScaler {

    public static class MetricCollector {
        private final List<Double> cpuUsage = new CopyOnWriteArrayList<>();
        private final List<Double> requestRate = new CopyOnWriteArrayList<>();

        public void recordCpu(double cpu) {
            cpuUsage.add(cpu);
            if (cpuUsage.size() > 10) cpuUsage.remove(0);
        }

        public void recordRequests(double rate) {
            requestRate.add(rate);
            if (requestRate.size() > 10) requestRate.remove(0);
        }

        public double getAverageCpu() {
            return cpuUsage.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        }

        public double getAverageRequestRate() {
            return requestRate.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        }
    }

    public static class Scaler {
        private final AtomicInteger instanceCount;
        private final int minInstances;
        private final int maxInstances;
        private final double scaleUpThreshold;
        private final double scaleDownThreshold;
        private final MetricCollector metrics;

        public Scaler(int initial, int min, int max, double up, double down, MetricCollector metrics) {
            this.instanceCount = new AtomicInteger(initial);
            this.minInstances = min;
            this.maxInstances = max;
            this.scaleUpThreshold = up;
            this.scaleDownThreshold = down;
            this.metrics = metrics;
        }

        public void evaluate() {
            double avgCpu = metrics.getAverageCpu();
            System.out.println("Avg CPU: " + String.format("%.1f", avgCpu) + "% | Instances: " + instanceCount.get());

            if (avgCpu > scaleUpThreshold && instanceCount.get() < maxInstances) {
                int old = instanceCount.getAndIncrement();
                System.out.println("  -> Scaling UP from " + old + " to " + (old + 1));
            } else if (avgCpu < scaleDownThreshold && instanceCount.get() > minInstances) {
                int old = instanceCount.getAndDecrement();
                System.out.println("  -> Scaling DOWN from " + old + " to " + (old - 1));
            }
        }

        public int getInstanceCount() { return instanceCount.get(); }
    }

    public static void main(String[] args) throws Exception {
        MetricCollector metrics = new MetricCollector();
        Scaler scaler = new Scaler(2, 2, 10, 70.0, 30.0, metrics);

        System.out.println("=== Auto Scaler ===");

        double[] loadPattern = {20, 35, 55, 75, 85, 90, 95, 80, 60, 40, 25, 15};
        for (double load : loadPattern) {
            metrics.recordCpu(load);
            scaler.evaluate();
            Thread.sleep(100);
        }

        System.out.println("\nFinal instance count: " + scaler.getInstanceCount());
    }
}
