package com.devops.monitoring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class MetricsCollector {
    private final Map<String, LongAdder> counters = new ConcurrentHashMap<>();
    private final Map<String, Double> gauges = new ConcurrentHashMap<>();

    public void incrementCounter(String name) {
        counters.computeIfAbsent(name, k -> new LongAdder()).increment();
    }

    public void recordGauge(String name, double value) {
        gauges.put(name, value);
    }

    public long getCounter(String name) {
        return counters.getOrDefault(name, new LongAdder()).sum();
    }

    public double getGauge(String name) {
        return gauges.getOrDefault(name, 0.0);
    }

    public void printAll() {
        System.out.println("=== Metrics ===");
        counters.forEach((k, v) -> System.out.println("Counter: " + k + " = " + v.sum()));
        gauges.forEach((k, v) -> System.out.println("Gauge: " + k + " = " + v));
    }

    public static void main(String[] args) {
        MetricsCollector mc = new MetricsCollector();
        mc.incrementCounter("api.requests");
        mc.incrementCounter("api.requests");
        mc.incrementCounter("api.errors");
        mc.recordGauge("memory.usage", 68.5);
        mc.recordGauge("cpu.load", 0.42);
        mc.printAll();
    }
}
