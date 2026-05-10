package com.learning.jaeger;

import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.TextMapAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JaegerSolutionTest {

    private JaegerSolution solution;
    private Tracer mockTracer;

    @BeforeEach
    void setUp() {
        solution = new JaegerSolution("test-service");
    }

    @Test
    void testStartSpan() {
        Span span = solution.startSpan("test-operation");
        assertNotNull(span);
        span.finish();
    }

    @Test
    void testStartSpanWithParent() {
        Span parent = solution.startSpan("parent");
        Span child = solution.startSpan("child", parent);
        assertNotNull(child);
        parent.finish();
        child.finish();
    }

    @Test
    void testInjectSpanContext() {
        Span span = solution.startSpan("test");
        Map<String, String> carrier = new HashMap<>();
        solution.injectSpanContext(span, new TextMapAdapter(carrier));
        assertFalse(carrier.isEmpty());
        span.finish();
    }

    @Test
    void testExtractSpanContext() {
        Map<String, String> carrier = new HashMap<>();
        carrier.put("uber-trace-id", "abc:def:0:1");
        var context = solution.extractSpanContext(new TextMapAdapter(carrier));
        assertNotNull(context);
    }

    @Test
    void testBaggageItems() {
        Span span = solution.startSpan("test");
        solution.setBaggageItem(span, "user-id", "123");
        Map<String, String> baggage = solution.getBaggageItems(span);
        assertEquals("123", baggage.get("user-id"));
        span.finish();
    }
}