package com.dataeng.eleven;

import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SlaMonitor {
    private final Map<String, SlaDefinition> slas = new ConcurrentHashMap<>();
    private final Map<String, List<SlaBreach>> breaches = new ConcurrentHashMap<>();

    public record SlaDefinition(String dagId, String taskId, Duration maxDuration, Duration maxLag, String severity) {}
    public record SlaBreach(String dagId, String taskId, String metric, double actualValue, double threshold, Instant timestamp) {}

    public void registerSla(SlaDefinition sla) {
        slas.put(sla.dagId() + "." + sla.taskId(), sla);
    }

    public void checkDuration(String dagId, String taskId, Duration actual) {
        SlaDefinition sla = slas.get(dagId + "." + taskId);
        if (sla != null && actual.compareTo(sla.maxDuration()) > 0) {
            var breach = new SlaBreach(dagId, taskId, "duration",
                actual.toMillis() / 1000.0, sla.maxDuration().toMillis() / 1000.0, Instant.now());
            breaches.computeIfAbsent(dagId, k -> new CopyOnWriteArrayList<>()).add(breach);
            alert(breach);
        }
    }

    private void alert(SlaBreach b) {
        System.err.printf("SLA BREACH: %s/%s - %s (%.1fs > %.1fs)%n",
            b.dagId(), b.taskId(), b.metric(), b.actualValue(), b.threshold());
    }

    public List<SlaBreach> getBreaches(String dagId) {
        return breaches.getOrDefault(dagId, Collections.emptyList());
    }
}
