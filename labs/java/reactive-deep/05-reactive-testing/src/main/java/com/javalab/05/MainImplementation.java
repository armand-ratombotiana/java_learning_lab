package com.javalab.05;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;
import java.time.Duration;
import java.util.function.Predicate;

public class MainImplementation {
    
    public Flux<Integer> processNumbers(Flux<Integer> source) {
        return source.filter(n -> n > 0).map(n -> n * 2);
    }
    
    public Mono<String> validateAndTransform(String input) {
        return Mono.justOrEmpty(input)
            .filter(s -> s.length() >= 3)
            .map(String::toUpperCase)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Input too short")));
    }
    
    public Flux<Integer> delayedSequence(int start, int count, Duration delay) {
        return Flux.range(start, count).delayElements(delay);
    }
}
