package com.learning.otel;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {
    record SpanContext(String traceId, String spanId, String parentSpanId) {
        SpanContext child() {
            return new SpanContext(traceId, UUID.randomUUID().toString().replace("-", "").substring(0, 16), spanId);
        }
    }

    static class Span {
        final String name;
        final SpanContext context;
        final long startTime;
        long endTime;
        final Map<String, String> attributes = new HashMap<>();
        final List<Span> children = new ArrayList<>();
        String status = "OK";

        Span(String name, SpanContext context) {
            this.name = name;
            this.context = context;
            this.startTime = System.nanoTime();
        }

        void setAttribute(String key, String value) { attributes.put(key, value); }
        void setStatus(String status) { this.status = status; }
        void end() { this.endTime = System.nanoTime(); }

        Span startChild(String name) {
            var child = new Span(name, context.child());
            children.add(child);
            return child;
        }

        long durationMs() { return (endTime - startTime) / 1_000_000; }

        void print(int indent) {
            var pad = "  ".repeat(indent);
            System.out.println(pad + "Span[" + name + "] trace=" + context.traceId().substring(0, 8) + "... span=" + context.spanId() + " dur=" + durationMs() + "ms status=" + status);
            if (!attributes.isEmpty()) System.out.println(pad + "  attributes: " + attributes);
            children.forEach(c -> c.print(indent + 1));
        }
    }

    static class Tracer {
        final String serviceName;

        Tracer(String serviceName) { this.serviceName = serviceName; }

        Span startSpan(String name) {
            var traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
            var spanId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            return new Span(name, new SpanContext(traceId, spanId, null));
        }
    }

    static class TracerProvider {
        final Map<String, Tracer> tracers = new ConcurrentHashMap<>();
        Tracer getTracer(String serviceName) {
            return tracers.computeIfAbsent(serviceName, Tracer::new);
        }
    }

    // Context propagation (simulated)
    static class ContextPropagator {
        static final ThreadLocal<Span> currentSpan = new ThreadLocal<>();

        static void inject(Span span) { currentSpan.set(span); }
        static Span extract() { return currentSpan.get(); }
        static void clear() { currentSpan.remove(); }
    }

    public static void main(String[] args) {
        System.out.println("=== OpenTelemetry: Distributed Tracing ===");

        var provider = new TracerProvider();
        var tracer = provider.getTracer("order-service");

        // Root span
        var rootSpan = tracer.startSpan("POST /api/orders");
        rootSpan.setAttribute("http.method", "POST");
        rootSpan.setAttribute("http.route", "/api/orders");

        ContextPropagator.inject(rootSpan);

        // Child spans
        var validationSpan = rootSpan.startChild("validate-order");
        validationSpan.setAttribute("order.id", "ORD-12345");
        try { Thread.sleep(50); } catch (InterruptedException e) {}
        validationSpan.end();

        var dbSpan = rootSpan.startChild("save-order");
        dbSpan.setAttribute("db.system", "postgresql");
        dbSpan.setAttribute("db.statement", "INSERT INTO orders...");
        try { Thread.sleep(80); } catch (InterruptedException e) {}
        dbSpan.end();

        var paymentSpan = rootSpan.startChild("process-payment");
        paymentSpan.setAttribute("payment.amount", "99.99");
        paymentSpan.setAttribute("payment.method", "credit_card");
        try { Thread.sleep(120); } catch (InterruptedException e) {}
        paymentSpan.end();

        rootSpan.end();
        rootSpan.print(0);

        // Error scenario
        System.out.println("\n--- Span with Error ---");
        var errorSpan = tracer.startSpan("GET /api/orders/404");
        errorSpan.setAttribute("http.status_code", "404");
        errorSpan.setStatus("ERROR");
        errorSpan.setAttribute("error.message", "Order not found");
        try { Thread.sleep(20); } catch (InterruptedException e) {}
        errorSpan.end();
        errorSpan.print(0);

        // Trace across services (simulated)
        System.out.println("\n--- Distributed Trace (Service A -> Service B) ---");
        var serviceASpan = tracer.startSpan("ServiceA.handleRequest");
        ContextPropagator.inject(serviceASpan);
        try { Thread.sleep(30); } catch (InterruptedException e) {}

        // Simulate downstream call
        var serviceBSpan = serviceASpan.startChild("ServiceB.process");
        var propagatedCtx = ContextPropagator.extract();
        System.out.println("  Propagated traceId: " + propagatedCtx.context.traceId().substring(0, 8) + "...");
        serviceBSpan.setAttribute("service", "ServiceB");
        try { Thread.sleep(60); } catch (InterruptedException e) {}
        serviceBSpan.end();

        serviceASpan.end();
        serviceASpan.print(0);

        System.out.println("\n--- Exporter (Simulated) ---");
        System.out.println("  Span data batch exported to OpenTelemetry Collector");
        System.out.println("  Total spans created: 5");
    }
}
