package com.learning.lab.module40;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;

import java.util.Map;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 40: Jaeger Lab ===\n");

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(
                        JaegerGrpcSpanExporter.builder()
                                .setEndpoint("http://localhost:14250")
                                .build()))
                .build();

        OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setPropagator(W3CTraceContextPropagator.getInstance())
                .build();

        Tracer tracer = openTelemetry.getTracer("my-app", "1.0.0");

        System.out.println("1. Tracing Configuration:");
        System.out.println("   - Jaeger Agent: localhost:6831");
        System.out.println("   - Jaeger Collector: http://localhost:14250");
        System.out.println("   - UI: http://localhost:16686");

        System.out.println("\n2. Spans:");
        createSpans(tracer);

        System.out.println("\n3. Span Propagation:");
        propagationDemo();

        System.out.println("\n4. Context Management:");
        contextManagement();

        System.out.println("\n=== Jaeger Lab Complete ===");
    }

    static void createSpans(Tracer tracer) {
        Span parentSpan = tracer.spanBuilder("parent-operation")
                .setSpanKind(SpanKind.SERVER)
                .startSpan();

        try (Scope scope = parentSpan.makeCurrent()) {
            parentSpan.setAttribute("component", "http");
            parentSpan.setAttribute("http.method", "GET");
            parentSpan.setAttribute("http.url", "/api/users");

            Span childSpan = tracer.spanBuilder("child-operation")
                    .setParent(io.opentelemetry.context.Context.current())
                    .startSpan();

            try (Scope childScope = childSpan.makeCurrent()) {
                childSpan.setAttribute("database.type", "postgres");
                childSpan.setAttribute("db.statement", "SELECT * FROM users");
                childSpan.addEvent("query-executed");
            } finally {
                childSpan.end();
            }

            parentSpan.setAttribute("http.status_code", 200);
        } finally {
            parentSpan.end();
        }

        System.out.println("   Created parent span: parent-operation");
        System.out.println("   Created child span: child-operation");
    }

    static void propagationDemo() {
        System.out.println("   W3C Trace Context Propagation:");
        System.out.println("   - Format: traceparent header");
        System.out.println("   - Example: 00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01");
        System.out.println("   - Fields: version-traceid-spansampled");

        System.out.println("\n   B3 Propagation (Zipkin):");
        System.out.println("   - Headers: X-B3-TraceId, X-B3-SpanId, X-B3-ParentSpanId");
        System.out.println("   - Sampling: X-B3-Sampled");

        System.out.println("\n   Jaeger Propagation:");
        System.out.println("   - Headers:uber-trace-id");
        System.out.println("   - Format: trace-id:span-id:parent-id:sampled");
    }

    static void contextManagement() {
        System.out.println("   Context Operations:");
        System.out.println("   - Extract: Extract context from HTTP headers");
        System.out.println("   - Inject: Inject context into outgoing requests");
        System.out.println("   - Current: Get current active span context");
        System.out.println("   - Scope: Make span current in current context");

        System.out.println("\n   Span Lifecycle:");
        System.out.println("   1. Span.start() - Begin span");
        System.out.println("   2. Span.makeCurrent() - Set as current");
        System.out.println("   3. Add attributes, events, links");
        System.out.println("   4. Span.end() - Finish span");
    }
}