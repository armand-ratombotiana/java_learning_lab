package com.learning.jaeger;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Lab {
    record SpanContext(String traceId, String spanId, String parentSpanId) {}

    static class Span {
        String traceId, spanId, parentSpanId;
        String operationName;
        String serviceName;
        Instant startTime, endTime;
        Map<String, String> tags = new LinkedHashMap<>();
        List<Span> childSpans = new ArrayList<>();

        Span(String traceId, String spanId, String parentSpanId, String opName, String service) {
            this.traceId = traceId; this.spanId = spanId; this.parentSpanId = parentSpanId;
            this.operationName = opName; this.serviceName = service;
            this.startTime = Instant.now();
        }

        void finish() { this.endTime = Instant.now(); }

        void setTag(String k, String v) { tags.put(k, v); }

        Duration duration() { return Duration.between(startTime, endTime); }

        String toString(boolean showChildren) {
            var sb = new StringBuilder();
            sb.append("  Span{").append(operationName).append("} @ ").append(serviceName);
            sb.append(" [").append(spanId.substring(0, 8)).append("...]");
            if (endTime != null) sb.append(" ").append(duration().toMillis()).append("ms");
            if (!tags.isEmpty()) sb.append(" tags=").append(tags);
            if (showChildren && !childSpans.isEmpty()) {
                sb.append("\n    Children:");
                for (var child : childSpans)
                    sb.append("\n").append(child.toString(true).replaceAll("(?m)^", "    "));
            }
            return sb.toString();
        }
    }

    static class Tracer {
        final String serviceName;
        final Random random = new Random();

        Tracer(String serviceName) { this.serviceName = serviceName; }

        String genId() { return Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong()); }

        Span startTrace(String operationName) {
            String traceId = genId();
            return new Span(traceId, genId(), "", operationName, serviceName);
        }

        Span startChildSpan(Span parent, String operationName) {
            var child = new Span(parent.traceId, genId(), parent.spanId, operationName, serviceName);
            parent.childSpans.add(child);
            return child;
        }

        Span startChildSpan(String traceId, String parentSpanId, String operationName, String service) {
            var span = new Span(traceId, genId(), parentSpanId, operationName, service);
            return span;
        }

        SpanContext inject(Span span) {
            return new SpanContext(span.traceId, span.spanId, span.parentSpanId);
        }

        Span joinTrace(SpanContext ctx, String operationName) {
            return startChildSpan(ctx.traceId(), ctx.spanId(), operationName, serviceName);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Jaeger / Distributed Tracing Lab ===\n");

        System.out.println("1. Creating Traces & Spans:");

        Tracer orderTracer = new Tracer("order-service");
        Tracer paymentTracer = new Tracer("payment-service");
        Tracer inventoryTracer = new Tracer("inventory-service");

        Span root = orderTracer.startTrace("POST /api/orders");
        root.setTag("http.method", "POST");
        root.setTag("http.url", "/api/orders");
        root.setTag("order.id", "ORD-12345");
        Thread.sleep(10);

        Span validateSpan = orderTracer.startChildSpan(root, "validate-order");
        validateSpan.setTag("validation", "passed");
        Thread.sleep(5);
        validateSpan.finish();

        Span checkInventory = orderTracer.startChildSpan(root, "check-inventory");
        checkInventory.setTag("product", "LAPTOP-001");
        checkInventory.setTag("quantity", "1");

        Span inventoryCall = orderTracer.startChildSpan(checkInventory, "inventory-service-call");
        inventoryCall.setTag("peer.service", "inventory-service");

        Span checkStock = inventoryTracer.joinTrace(
            orderTracer.inject(inventoryCall), "check-stock");
        checkStock.setTag("stock_level", "in_stock");
        Thread.sleep(8);
        checkStock.finish();

        inventoryCall.finish();
        checkInventory.finish();

        Span processPayment = orderTracer.startChildSpan(root, "process-payment");
        processPayment.setTag("amount", "1200.00");

        Span paymentCall = orderTracer.startChildSpan(processPayment, "payment-service-call");
        Span chargeCard = paymentTracer.joinTrace(
            orderTracer.inject(paymentCall), "charge-card");
        chargeCard.setTag("payment_method", "visa");
        Thread.sleep(12);
        chargeCard.finish();
        paymentCall.finish();

        Thread.sleep(5);
        processPayment.finish();
        root.finish();

        System.out.println("\n   Trace structure:");
        System.out.println(root.toString(true));

        System.out.println("\n2. Trace ID propagation (context headers):");
        var ctx = orderTracer.inject(root);
        System.out.println("   uber-trace-id: " + ctx.traceId().substring(0, 16) + ":" +
            ctx.spanId().substring(0, 16) + ":" + ctx.parentSpanId().substring(0, 16) + ":01");

        System.out.println("\n3. Span Lifecycle:");
        System.out.println("   Start: span created with start timestamp");
        System.out.println("   Tags: key-value metadata (http.method, error, peer.service)");
        System.out.println("   Logs: timestamped events (exception, message)");
        System.out.println("   Finish: span ended with duration");

        System.out.println("\n4. Sampling strategies:");
        System.out.println("   - Const: sample all (1) or none (0)");
        System.out.println("   - Probabilistic: sample X% of traces");
        System.out.println("   - Rate-limiting: max traces per second");
        System.out.println("   - Remote: dynamic sampling from Jaeger agent");

        System.out.println("\n5. Baggage items (context propagation):");
        System.out.println("   Key-value pairs propagated across all spans in trace");
        System.out.println("   Used for: tenant-id, user-id, correlation-id");

        System.out.println("\n6. Trace comparison:");
        Span root2 = orderTracer.startTrace("GET /api/orders/12345");
        root2.setTag("http.status", "200");
        Thread.sleep(3);
        root2.finish();

        System.out.println("   Trace 1: " + root.operationName + " (" + root.duration().toMillis() + "ms)");
        System.out.println("   Trace 2: " + root2.operationName + " (" + root2.duration().toMillis() + "ms)");

        System.out.println("\n7. Jaeger UI Features:");
        System.out.println("   - Trace Detail View: waterfall of spans");
        System.out.println("   - Service Graph: dependency graph");
        System.out.println("   - Compare Traces: side-by-side");
        System.out.println("   - Search: by service, operation, tags, duration");

        System.out.println("\n=== Lab Complete ===");
    }
}
