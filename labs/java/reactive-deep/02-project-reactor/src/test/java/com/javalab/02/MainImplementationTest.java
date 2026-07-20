package com.javalab.02;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.time.Duration;

class MainImplementationTest {
    private MainImplementation service;
    @BeforeEach void setUp() { service = new MainImplementation(); }
    @Test @DisplayName("Flux creation should work") void testFluxCreation() {
        Flux<String> flux = service.createFlux("a", "b", "c");
        StepVerifier.create(flux).expectNext("a", "b", "c").verifyComplete();
    }
    @Test @DisplayName("Range should emit correct numbers") void testRange() {
        StepVerifier.create(service.range(1, 5)).expectNext(1,2,3,4,5).verifyComplete();
    }
    @Test @DisplayName("Transform should apply function") void testTransform() {
        Flux<String> source = Flux.just("hello", "world");
        StepVerifier.create(service.transform(source, String::toUpperCase))
            .expectNext("HELLO", "WORLD").verifyComplete();
    }
}
