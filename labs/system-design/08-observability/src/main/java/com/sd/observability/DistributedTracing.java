package com.sd.observability;

import java.util.*;
import java.util.concurrent.*;

public class DistributedTracing {

    public static class Span {
        public final String spanId;
        public final String parentSpanId;
        public final String traceId;
        public final String serviceName;
        public final String operationName;
        public final long startTime;
        public long endTime;
        public final Map<String, String> tags = new HashMap<>();

        public Span(String traceId, String spanId, String parentSpanId,
                    String serviceName, String operationName) {
            this.traceId = traceId;
            this.spanId = spanId;
            this.parentSpanId = parentSpanId;
            this.serviceName = serviceName;
            this.operationName = operationName;
            this.startTime = System.currentTimeMillis();
            this.endTime = -1;
        }

        public void finish() { this.endTime = System.currentTimeMillis(); }
        public long durationMs() { return endTime - startTime; }

        public void tag(String key, String value) { tags.put(key, value); }

        @Override
        public String toString() {
            return serviceName + "." + operationName + " [" + durationMs() + "ms] trace="
                + traceId + " span=" + spanId;
        }
    }

    public static class Tracer {
        private final String serviceName;
        private final List<Span> spans = new CopyOnWriteArrayList<>();

        public Tracer(String serviceName) { this.serviceName = serviceName; }

        public Span startSpan(String operationName, String traceId, String parentSpanId) {
            String spanId = UUID.randomUUID().toString().substring(0, 8);
            Span span = new Span(traceId, spanId, parentSpanId, serviceName, operationName);
            spans.add(span);
            System.out.println("Started: " + span);
            return span;
        }

        public void endSpan(Span span) {
            span.finish();
            System.out.println("Ended: " + span);
        }

        public List<Span> getSpans() { return spans; }

        public void printTrace(String traceId) {
            System.out.println("\n=== Trace: " + traceId + " ===");
            for (Span s : spans) {
                if (s.traceId.equals(traceId)) {
                    String indent = s.parentSpanId == null ? "" : "  ";
                    System.out.println(indent + s.serviceName + "." + s.operationName
                        + " [" + s.durationMs() + "ms]");
                }
            }
        }
    }

    public static void main(String[] args) {
        Tracer tracer = new Tracer("api-gateway");
        String traceId = UUID.randomUUID().toString().substring(0, 16);

        Span root = tracer.startSpan("handleRequest", traceId, null);
        root.tag("http.method", "GET");
        root.tag("http.path", "/api/users");

        Tracer userSvcTracer = new Tracer("user-service");
        Span child = userSvcTracer.startSpan("getUser", traceId, root.spanId);
        child.tag("db.query", "SELECT * FROM users");

        try { Thread.sleep(50); } catch (InterruptedException e) {}
        userSvcTracer.endSpan(child);

        try { Thread.sleep(30); } catch (InterruptedException e) {}
        tracer.endSpan(root);

        tracer.printTrace(traceId);
    }
}
