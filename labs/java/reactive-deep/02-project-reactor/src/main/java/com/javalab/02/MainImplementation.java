package com.javalab.02;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.time.Duration;
import java.util.function.Function;

public class MainImplementation {
    
    public Flux<String> createFlux(String... items) { return Flux.fromArray(items); }
    
    public Flux<Integer> range(int start, int count) { return Flux.range(start, count); }
    
    public Flux<String> transform(Flux<String> source, Function<String, String> mapper) {
        return source.map(mapper).subscribeOn(Schedulers.parallel());
    }
    
    public Mono<String> asyncOperation(String input) {
        return Mono.just(input).map(String::toUpperCase).subscribeOn(Schedulers.boundedElastic());
    }
    
    public Flux<Integer> fibonacci(int count) {
        return Flux.generate(() -> new int[]{0, 1}, (state, sink) -> {
            sink.next(state[0]);
            int next = state[0] + state[1];
            state[0] = state[1];
            state[1] = next;
            if (state[0] > count) sink.complete();
            return state;
        });
    }
}
