package com.devops.monitoring;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogAggregator {
    private final List<LogEntry> logs = new CopyOnWriteArrayList<>();

    public void log(String level, String source, String message) {
        LogEntry entry = new LogEntry(Instant.now(), level, source, message);
        logs.add(entry);
        System.out.println("[" + level + "] [" + source + "] " + message);
    }

    public List<LogEntry> getByLevel(String level) {
        return logs.stream().filter(l -> l.level.equals(level)).toList();
    }

    public List<LogEntry> getBySource(String source) {
        return logs.stream().filter(l -> l.source.equals(source)).toList();
    }

    public static class LogEntry {
        private final Instant timestamp;
        private final String level;
        private final String source;
        private final String message;

        public LogEntry(Instant timestamp, String level, String source, String message) {
            this.timestamp = timestamp;
            this.level = level;
            this.source = source;
            this.message = message;
        }
    }

    public static void main(String[] args) {
        LogAggregator agg = new LogAggregator();
        agg.log("INFO", "api-gateway", "Request received: /api/users");
        agg.log("ERROR", "payment-service", "Payment declined: insufficient funds");
        agg.log("WARN", "auth-service", "Rate limit approaching");
        agg.log("INFO", "api-gateway", "Response sent: 200 OK");

        System.out.println("\nErrors: " + agg.getByLevel("ERROR").size());
        System.out.println("API Gateway logs: " + agg.getBySource("api-gateway").size());
    }
}
