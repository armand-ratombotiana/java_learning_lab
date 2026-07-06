package com.javaacademy.lab36.reactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;

public class ErrorHandlingExample {

    public Mono<String> fallbackExample() {
        return Mono.error(new RuntimeException("Service failure"))
            .onErrorReturn("Fallback response");
    }

    public Mono<String> fallbackWithMethod() {
        return Mono.error(new RuntimeException("Failure"))
            .onErrorResume(e -> {
                System.err.println("Error: " + e.getMessage());
                return Mono.just("Recovered from: " + e.getMessage());
            });
    }

    public Mono<String> retryExample() {
        return Mono.fromCallable(() -> {
            if (Math.random() > 0.3) throw new RuntimeException("Transient error");
            return "Success after retry";
        }).retry(3);
    }

    public Mono<String> retryWithBackoff() {
        return Mono.fromCallable(() -> {
            if (Math.random() > 0.4) throw new RuntimeException("Timeout");
            return "Success with backoff";
        }).retryWhen(
            reactor.util.retry.Retry.backoff(3, Duration.ofMillis(100))
                .doBeforeRetry(rs -> System.out.println("Retrying: " + rs.failure().getMessage()))
        );
    }

    public Flux<Integer> recoverWithFallbackSequence() {
        return Flux.concat(
            Flux.range(1, 3),
            Flux.error(new RuntimeException("Error in stream")),
            Flux.range(4, 3)
        ).onErrorResume(e -> Flux.just(10, 20, 30));
    }

    public Flux<Integer> timeoutExample() {
        return Flux.interval(Duration.ofMillis(200))
            .map(i -> (int) (long) i)
            .timeout(Duration.ofMillis(100))
            .onErrorResume(e -> Flux.just(-1, -2));
    }

    public Mono<String> doFinallyExample() {
        return Mono.just("Data")
            .doFinally(signal -> System.out.println("Cleanup: " + signal));
    }
}
