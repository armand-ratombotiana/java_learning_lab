package com.sd.observability;

import java.util.*;
import java.time.*;
import java.time.format.*;

public class StructuredLogging {

    public enum LogLevel { DEBUG, INFO, WARN, ERROR }

    public static class StructuredLogEvent {
        public final String timestamp;
        public final LogLevel level;
        public final String service;
        public final String message;
        public final Map<String, Object> fields;
        public final String traceId;

        public StructuredLogEvent(LogLevel level, String service, String message,
                                  Map<String, Object> fields, String traceId) {
            this.timestamp = Instant.now().toString();
            this.level = level;
            this.service = service;
            this.message = message;
            this.fields = fields;
            this.traceId = traceId;
        }

        public String toJson() {
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"ts\":\"").append(timestamp).append("\",");
            sb.append("\"level\":\"").append(level).append("\",");
            sb.append("\"service\":\"").append(service).append("\",");
            sb.append("\"message\":\"").append(message).append("\"");
            if (traceId != null) {
                sb.append(",\"traceId\":\"").append(traceId).append("\"");
            }
            if (fields != null && !fields.isEmpty()) {
                sb.append(",\"fields\":{");
                boolean first = true;
                for (Map.Entry<String, Object> e : fields.entrySet()) {
                    if (!first) sb.append(",");
                    sb.append("\"").append(e.getKey()).append("\":\"").append(e.getValue()).append("\"");
                    first = false;
                }
                sb.append("}");
            }
            sb.append("}");
            return sb.toString();
        }
    }

    public static class Logger {
        private final String service;

        public Logger(String service) { this.service = service; }

        public void info(String msg, Map<String, Object> fields, String traceId) {
            log(LogLevel.INFO, msg, fields, traceId);
        }

        public void warn(String msg, Map<String, Object> fields, String traceId) {
            log(LogLevel.WARN, msg, fields, traceId);
        }

        public void error(String msg, Map<String, Object> fields, String traceId) {
            log(LogLevel.ERROR, msg, fields, traceId);
        }

        private void log(LogLevel level, String msg, Map<String, Object> fields, String traceId) {
            StructuredLogEvent event = new StructuredLogEvent(level, service, msg, fields, traceId);
            System.out.println(event.toJson());
        }
    }

    public static class LogAggregator {
        private final List<StructuredLogEvent> events = new CopyOnWriteArrayList<>();

        public void collect(StructuredLogEvent event) {
            events.add(event);
        }

        public List<StructuredLogEvent> findByLevel(LogLevel level) {
            return events.stream().filter(e -> e.level == level).toList();
        }

        public List<StructuredLogEvent> findByService(String service) {
            return events.stream().filter(e -> e.service.equals(service)).toList();
        }

        public long countErrors() {
            return events.stream().filter(e -> e.level == LogLevel.ERROR).count();
        }
    }

    public static void main(String[] args) {
        Logger logger = new Logger("order-service");
        LogAggregator aggregator = new LogAggregator();

        System.out.println("=== Structured Logging ===");

        StructuredLogEvent e1 = new StructuredLogEvent(LogLevel.INFO, "order-service",
            "Order created", Map.of("orderId", "12345", "amount", "99.99"), "trace-abc");
        System.out.println(e1.toJson());
        aggregator.collect(e1);

        StructuredLogEvent e2 = new StructuredLogEvent(LogLevel.WARN, "order-service",
            "Slow query detected", Map.of("query", "SELECT *", "duration_ms", "1500"), "trace-def");
        System.out.println(e2.toJson());
        aggregator.collect(e2);

        StructuredLogEvent e3 = new StructuredLogEvent(LogLevel.ERROR, "order-service",
            "Payment failed", Map.of("errorCode", "INSUFFICIENT_FUNDS"), "trace-ghi");
        System.out.println(e3.toJson());
        aggregator.collect(e3);

        System.out.println("\nError count: " + aggregator.countErrors());
    }
}
