package com.javalab.03;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.function.Function;

public class MainImplementation {
    
    public Mono<String> welcome(String name) {
        return Mono.just("Hello, " + name + "!");
    }
    
    public Flux<String> streamMessages(String... messages) {
        return Flux.fromArray(messages).delayElements(Duration.ofMillis(100));
    }
    
    public <T> Flux<T> retryOnError(Flux<T> source, int maxRetries) {
        return source.retry(maxRetries);
    }
    
    public <T, R> Flux<R> transformAsync(Flux<T> source, Function<T, Mono<R>> transformer) {
        return source.flatMap(transformer);
    }
}
