package com.distributed.monitoring;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TracingSpan implements AutoCloseable {
    private static final AtomicLong SPAN_ID_GEN = new AtomicLong(0);
    private final String traceId;
    private final String spanId;
    private final String parentSpanId;
    private final String operationName;
    private final long startTimeNanos;
    private final Map<String, String> attributes = new ConcurrentHashMap<>();
    private final List<LogEntry> logs = new ArrayList<>();
    private volatile long endTimeNanos;
    private volatile Status status = Status.OK;

    public enum Status { OK, ERROR, UNSET }

    public record LogEntry(long timestamp, String message, Map<String, String> fields) {}

    public TracingSpan(String traceId, String parentSpanId, String operationName) {
        this.traceId = traceId;
        this.spanId = Long.toHexString(SPAN_ID_GEN.incrementAndGet());
        this.parentSpanId = parentSpanId;
        this.operationName = operationName;
        this.startTimeNanos = System.nanoTime();
    }

    public TracingSpan(String traceId, String operationName) {
        this(traceId, null, operationName);
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public void addEvent(String name) {
        addEvent(name, Map.of());
    }

    public void addEvent(String name, Map<String, String> fields) {
        logs.add(new LogEntry(System.currentTimeMillis(), name, fields));
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void end() {
        this.endTimeNanos = System.nanoTime();
    }

    @Override
    public void close() {
        if (endTimeNanos == 0) end();
    }

    public long getDurationMicros() {
        return (endTimeNanos - startTimeNanos) / 1000;
    }

    public String getTraceId() { return traceId; }
    public String getSpanId() { return spanId; }
    public String getParentSpanId() { return parentSpanId; }
    public String getOperationName() { return operationName; }
    public Status getStatus() { return status; }
    public Map<String, String> getAttributes() { return Map.copyOf(attributes); }
    public List<LogEntry> getLogs() { return List.copyOf(logs); }

    @Override
    public String toString() {
        return "Span{trace=" + traceId + ", span=" + spanId + ", op=" + operationName
            + ", duration=" + getDurationMicros() + "us, status=" + status + "}";
    }
}
