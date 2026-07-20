package com.javalab.04;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class MainImplementationTest {
    private MainImplementation service;
    @BeforeEach void setUp() { service = new MainImplementation(); }
    @Test @DisplayName("Fire and forget should store value")
    void testFireAndForget() {
        service.fireAndForget("key1", "value1").block();
        StepVerifier.create(service.requestResponse("key1")).expectNext("value1").verifyComplete();
    }
    @Test @DisplayName("Request stream should filter by prefix")
    void testRequestStream() {
        service.fireAndForget("user:1", "Alice").block();
        service.fireAndForget("user:2", "Bob").block();
        service.fireAndForget("admin:1", "Charlie").block();
        StepVerifier.create(service.requestStream("user:")).expectNext("Alice", "Bob").verifyComplete();
    }
    @Test @DisplayName("Channel should process requests")
    void testChannel() {
        Flux<String> requests = Flux.just("req1", "req2");
        StepVerifier.create(service.channel(requests))
            .expectNext("Processed: req1", "Processed: req2").verifyComplete();
    }
}
