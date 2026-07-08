package com.distributed.monitoring;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class AlertEvaluator {
    private final List<AlertRule> rules = new ArrayList<>();
    private final Map<String, AlertState> alertStates = new ConcurrentHashMap<>();
    private final AtomicLong alertCounter = new AtomicLong(0);

    public record AlertRule(
        String name,
        String metricName,
        double threshold,
        Comparison comparison,
        Duration pendingDuration,
        String severity
    ) {
        public enum Comparison { GREATER_THAN, LESS_THAN, EQUAL }
    }

    public record AlertEvent(
        String id, String ruleName, String severity,
        double value, double threshold, long timestamp
    ) {}

    public enum AlertState { PENDING, FIRING, RESOLVED }

    public record Duration(long millis) {
        public static Duration ofMillis(long ms) { return new Duration(ms); }
        public static Duration ofSeconds(long s) { return new Duration(s * 1000); }
    }

    public void addRule(AlertRule rule) {
        rules.add(rule);
    }

    public List<AlertEvent> evaluate(Supplier<Map<String, Number>> metricProvider) {
        List<AlertEvent> events = new ArrayList<>();
        Map<String, Number> metrics = metricProvider.get();

        for (AlertRule rule : rules) {
            Number value = metrics.get(rule.metricName());
            if (value == null) continue;

            boolean fired = switch (rule.comparison()) {
                case GREATER_THAN -> value.doubleValue() > rule.threshold();
                case LESS_THAN -> value.doubleValue() < rule.threshold();
                case EQUAL -> value.doubleValue() == rule.threshold();
            };

            String stateKey = rule.name();
            AlertState currentState = alertStates.getOrDefault(stateKey, AlertState.RESOLVED);

            if (fired) {
                if (currentState == AlertState.RESOLVED) {
                    alertStates.put(stateKey, AlertState.PENDING);
                } else if (currentState == AlertState.PENDING) {
                    alertStates.put(stateKey, AlertState.FIRING);
                    events.add(new AlertEvent(
                        "alert-" + alertCounter.incrementAndGet(),
                        rule.name(), rule.severity(),
                        value.doubleValue(), rule.threshold(), System.currentTimeMillis()
                    ));
                }
            } else {
                if (currentState != AlertState.RESOLVED) {
                    alertStates.put(stateKey, AlertState.RESOLVED);
                }
            }
        }
        return events;
    }

    public Map<String, AlertState> getStates() { return Map.copyOf(alertStates); }
    public List<AlertRule> getRules() { return List.copyOf(rules); }
}
