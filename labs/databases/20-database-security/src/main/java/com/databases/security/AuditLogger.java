package com.databases.security;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class AuditLogger {
    private final Queue<AuditEvent> events = new ConcurrentLinkedQueue<>();
    private final int maxEvents;
    private final AtomicLong eventId = new AtomicLong(1);

    public record AuditEvent(long id, String timestamp, String userId, String action,
        String resource, String result, String details, long latencyMs) {}

    public AuditLogger(int maxEvents) { this.maxEvents = maxEvents; }

    public AuditEvent log(String userId, String action, String resource,
        String result, String details, long latencyMs) {
        var event = new AuditEvent(
            eventId.getAndIncrement(),
            Instant.now().toString(),
            userId, action, resource, result, details, latencyMs
        );
        events.offer(event);
        if (events.size() > maxEvents) events.poll();
        return event;
    }

    public AuditEvent logAccess(String userId, String resource, boolean granted, long latencyMs) {
        return log(userId, granted ? "ACCESS_GRANTED" : "ACCESS_DENIED",
            resource, granted ? "SUCCESS" : "DENIED",
            "Access check for " + resource, latencyMs);
    }

    public AuditEvent logDataAccess(String userId, String operation, String table,
        String recordId, long latencyMs) {
        return log(userId, operation, table + "/" + recordId,
            "SUCCESS", "Data access", latencyMs);
    }

    public List<AuditEvent> queryByUser(String userId) {
        return events.stream()
            .filter(e -> e.userId().equals(userId))
            .sorted(Comparator.comparingLong(AuditEvent::id).reversed())
            .toList();
    }

    public List<AuditEvent> queryByAction(String action) {
        return events.stream()
            .filter(e -> e.action().equals(action))
            .sorted(Comparator.comparingLong(AuditEvent::id).reversed())
            .toList();
    }

    public List<AuditEvent> queryRecent(int count) {
        return events.stream()
            .sorted(Comparator.comparingLong(AuditEvent::id).reversed())
            .limit(count)
            .toList();
    }

    public int getEventCount() { return events.size(); }
    public void clear() { events.clear(); }

    public String generateReport() {
        var sb = new StringBuilder();
        sb.append("Audit Report\n");
        sb.append("Total events: ").append(events.size()).append("\n");
        var byAction = new HashMap<String, Long>();
        events.forEach(e -> byAction.merge(e.action(), 1L, Long::sum));
        sb.append("By action:\n");
        byAction.forEach((action, count) -> sb.append("  ").append(action).append(": ").append(count).append("\n"));
        return sb.toString();
    }
}
