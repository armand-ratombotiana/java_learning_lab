package com.learning.jaeger;

import io.jaegertracing.Configuration;
import io.opentracing.Tracer;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;

import java.util.HashMap;
import java.util.Map;

public class JaegerSolution {

    private final Tracer tracer;

    public JaegerSolution(String serviceName) {
        Configuration config = new Configuration(serviceName);
        config.getReporter().setLogSpans(true);
        config.getSampler().setType(Configuration.SamplerType.CONSTANT);
        config.getSampler().setParam(1);
        this.tracer = config.getTracer();
    }

    public JaegerSolution(Tracer tracer) {
        this.tracer = tracer;
    }

    public Span startSpan(String operationName) {
        return tracer.buildSpan(operationName).start();
    }

    public Span startSpan(String operationName, Span parent) {
        return tracer.buildSpan(operationName).asChildOf(parent).start();
    }

    public void injectSpanContext(Span span, TextMap carrier) {
        tracer.inject(span.context(), Format.TEXT_MAP, carrier);
    }

    public SpanContext extractSpanContext(TextMap carrier) {
        return tracer.extract(Format.TEXT_MAP, carrier);
    }

    public Map<String, String> getBaggageItems(Span span) {
        Map<String, String> baggage = new HashMap<>();
        span.getBaggageItemKeys().forEach(key ->
            baggage.put(key, span.getBaggageItem(key).orElse(""))
        );
        return baggage;
    }

    public void setBaggageItem(Span span, String key, String value) {
        span.setBaggageItem(key, value);
    }

    public Tracer getTracer() {
        return tracer;
    }
}