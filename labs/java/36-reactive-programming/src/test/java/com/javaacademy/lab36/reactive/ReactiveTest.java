package com.javaacademy.lab36.reactive;

import org.junit.jupiter.api.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ReactiveTest {

    private FluxExamples fluxEx;
    private MonoExamples monoEx;

    @BeforeEach
    void setUp() {
        fluxEx = new FluxExamples();
        monoEx = new MonoExamples();
    }

    @Test
    @DisplayName("Flux.range emits expected values")
    void fluxRange() {
        Flux<Integer> flux = fluxEx.range(1, 5);
        StepVerifier.create(flux)
            .expectNext(1, 2, 3, 4, 5)
            .verifyComplete();
    }

    @Test
    @DisplayName("Flux.map doubles each value")
    void fluxMap() {
        Flux<Integer> source = Flux.just(1, 2, 3);
        StepVerifier.create(fluxEx.mapExample(source))
            .expectNext(2, 4, 6)
            .verifyComplete();
    }

    @Test
    @DisplayName("Flux.filter keeps only even numbers")
    void fluxFilter() {
        Flux<Integer> source = Flux.range(1, 10);
        StepVerifier.create(fluxEx.filterEven(source))
            .expectNext(2, 4, 6, 8, 10)
            .verifyComplete();
    }

    @Test
    @DisplayName("Flux.reduce sums all values")
    void fluxReduce() {
        Flux<Integer> source = Flux.range(1, 5);
        StepVerifier.create(fluxEx.reduceExample(source))
            .expectNext(15)
            .verifyComplete();
    }

    @Test
    @DisplayName("Flux.zip combines two streams")
    void fluxZip() {
        Flux<String> names = Flux.just("Alice", "Bob");
        Flux<String> cities = Flux.just("NYC", "LA");
        StepVerifier.create(fluxEx.zipExample(names, cities))
            .expectNext("Alice from NYC", "Bob from LA")
            .verifyComplete();
    }

    @Test
    @DisplayName("Mono.just emits single value")
    void monoJust() {
        StepVerifier.create(monoEx.justExample())
            .expectNext("Hello Reactive World")
            .verifyComplete();
    }

    @Test
    @DisplayName("Mono.empty emits no value")
    void monoEmpty() {
        StepVerifier.create(monoEx.emptyExample())
            .verifyComplete();
    }

    @Test
    @DisplayName("Mono.error emits error signal")
    void monoError() {
        StepVerifier.create(monoEx.errorExample())
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    @DisplayName("Mono.map transforms value")
    void monoMap() {
        StepVerifier.create(monoEx.mapExample(Mono.just("hello")))
            .expectNext("HELLO")
            .verifyComplete();
    }

    @Test
    @DisplayName("ReactiveService returns user by ID")
    void reactiveServiceGetUser() {
        ReactiveService svc = new ReactiveService();
        StepVerifier.create(svc.getUserById(0))
            .expectNext("Alice")
            .verifyComplete();
    }

    @Test
    @DisplayName("ReactiveService returns all users")
    void reactiveServiceAllUsers() {
        ReactiveService svc = new ReactiveService();
        StepVerifier.create(svc.getAllUsers())
            .expectNext("Alice", "Bob", "Charlie")
            .verifyComplete();
    }

    @Test
    @DisplayName("Error handling with fallback")
    void errorHandlingFallback() {
        ErrorHandlingExample err = new ErrorHandlingExample();
        StepVerifier.create(err.fallbackExample())
            .expectNext("Fallback response")
            .verifyComplete();
    }

    @Test
    @DisplayName("Error handling with retry")
    void errorHandlingRetry() {
        ErrorHandlingExample err = new ErrorHandlingExample();
        StepVerifier.create(err.retryExample())
            .expectNextMatches(s -> s.contains("Success"))
            .verifyComplete();
    }

    @Test
    @DisplayName("Backpressure buffer strategy")
    void backpressureBuffer() {
        BackpressureExample bp = new BackpressureExample();
        Flux<Integer> source = Flux.range(1, 100);
        StepVerifier.create(bp.bufferStrategy(source, 10))
            .expectNextCount(100)
            .verifyComplete();
    }
}
