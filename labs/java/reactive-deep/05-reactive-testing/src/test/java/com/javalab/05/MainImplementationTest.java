package com.javalab.05;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;
import java.time.Duration;

class MainImplementationTest {
    private MainImplementation service;
    @BeforeEach void setUp() { service = new MainImplementation(); }
    @Test @DisplayName("processNumbers should filter and double")
    void testProcessNumbers() {
        Flux<Integer> source = Flux.just(-2, -1, 0, 1, 2, 3);
        StepVerifier.create(service.processNumbers(source))
            .expectNext(2, 4, 6).verifyComplete();
    }
    @Test @DisplayName("validateAndTransform should reject short input")
    void testValidation() {
        StepVerifier.create(service.validateAndTransform("ab"))
            .verifyError(IllegalArgumentException.class);
    }
    @Test @DisplayName("validateAndTransform should process valid input")
    void testValidInput() {
        StepVerifier.create(service.validateAndTransform("hello"))
            .expectNext("HELLO").verifyComplete();
    }
    @Test @DisplayName("Virtual time should work with delayed sequence")
    void testVirtualTime() {
        StepVerifier.withVirtualTime(() -> service.delayedSequence(1, 3, Duration.ofHours(1)))
            .thenAwait(Duration.ofHours(3))
            .expectNext(1, 2, 3)
            .verifyComplete();
    }
}
