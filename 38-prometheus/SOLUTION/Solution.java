package com.learning.prometheus;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class PrometheusSolution {

    private final MeterRegistry registry;
    private final Counter requestCounter;
    private final Timer requestTimer;

    public PrometheusSolution(MeterRegistry registry) {
        this.registry = registry;
        this.requestCounter = Counter.builder("http_requests_total")
            .description("Total HTTP requests")
            .tag("service", "my-service")
            .register(registry);

        this.requestTimer = Timer.builder("http_request_duration")
            .description("HTTP request duration")
            .register(registry);
    }

    public void incrementCounter(String... labels) {
        requestCounter.increment();
    }

    public void recordTimer(long duration, TimeUnit unit) {
        requestTimer.record(duration, unit);
    }

    public Counter getCounter(String name) {
        return Counter.builder(name).register(registry);
    }

    public Timer getTimer(String name) {
        return Timer.builder(name).register(registry);
    }

    public void registerGauge(String name, Supplier<Number> supplier) {
        Gauge.builder(name, supplier)
            .description("Gauge metric")
            .register(registry);
    }

    public MeterRegistry getRegistry() {
        return registry;
    }
}