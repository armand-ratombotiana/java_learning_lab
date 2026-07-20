package com.javalab.03;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.Duration;

class MainImplementationTest {
    private MainImplementation service;
    @BeforeEach void setUp() { service = new MainImplementation(); }
    @Test @DisplayName("Welcome should return greeting") void testWelcome() {
        StepVerifier.create(service.welcome("Alice")).expectNext("Hello, Alice!").verifyComplete();
    }
    @Test @DisplayName("Stream messages should emit all") void testStream() {
        StepVerifier.withVirtualTime(() -> service.streamMessages("a", "b", "c"))
            .thenAwait(Duration.ofSeconds(1)).expectNext("a", "b", "c").verifyComplete();
    }
    @Test @DisplayName("Retry should work on error") void testRetry() {
        Flux<Integer> failing = Flux.concat(Flux.just(1), Flux.error(new RuntimeException("Fail")));
        StepVerifier.create(service.retryOnError(failing, 1))
            .expectNext(1).expectNext(1).verifyError();
    }
}
