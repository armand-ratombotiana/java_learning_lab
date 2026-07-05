package com.devops.monitoring;

import java.util.function.Supplier;

public class AlertingRule {
    private final String name;
    private final Supplier<Boolean> condition;
    private final String message;

    public AlertingRule(String name, Supplier<Boolean> condition, String message) {
        this.name = name;
        this.condition = condition;
        this.message = message;
    }

    public boolean evaluate() {
        boolean triggered = condition.get();
        if (triggered) {
            System.out.println("[ALERT] " + name + ": " + message);
        }
        return triggered;
    }

    public static void main(String[] args) {
        MetricsCollector metrics = new MetricsCollector();

        AlertingRule highCpu = new AlertingRule(
            "HighCPU",
            () -> metrics.getGauge("cpu.load") > 0.8,
            "CPU load exceeded threshold"
        );

        AlertingRule errorRate = new AlertingRule(
            "HighErrorRate",
            () -> {
                long total = metrics.getCounter("api.requests");
                long errors = metrics.getCounter("api.errors");
                return total > 0 && (double) errors / total > 0.05;
            },
            "Error rate exceeded 5%"
        );

        metrics.incrementCounter("api.requests");
        metrics.incrementCounter("api.requests");
        metrics.incrementCounter("api.errors");
        metrics.recordGauge("cpu.load", 0.91);

        highCpu.evaluate();
        errorRate.evaluate();
    }
}
