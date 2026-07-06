package com.javaacademy.lab36.reactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class FluxExamples {

    public Flux<Integer> range(int start, int count) {
        return Flux.range(start, count);
    }

    public Flux<String> fromList(List<String> items) {
        return Flux.fromIterable(items);
    }

    public Flux<Integer> mapExample(Flux<Integer> source) {
        return source.map(x -> x * 2);
    }

    public Flux<Integer> filterEven(Flux<Integer> source) {
        return source.filter(x -> x % 2 == 0);
    }

    public Flux<Integer> flatMapExample(Flux<Integer> source) {
        return source.flatMap(x -> Flux.just(x, x * 10));
    }

    public Flux<Integer> reduceExample(Flux<Integer> source) {
        return source.reduce(0, Integer::sum).flux();
    }

    public Flux<String> zipExample(Flux<String> names, Flux<String> cities) {
        return Flux.zip(names, cities, (name, city) -> name + " from " + city);
    }

    public Flux<Integer> mergeExample(Flux<Integer> f1, Flux<Integer> f2) {
        return Flux.merge(f1, f2);
    }

    public Flux<String> intervalExample(int count, Duration delay) {
        return Flux.interval(delay)
            .take(count)
            .map(i -> "Tick " + i);
    }

    public Flux<Integer> fibonacci(int count) {
        return Flux.generate(
            () -> new int[]{0, 1},
            (state, sink) -> {
                sink.next(state[0]);
                int next = state[0] + state[1];
                state[0] = state[1];
                state[1] = next;
                if (state[0] > count) sink.complete();
                return state;
            }
        );
    }

    public Flux<Integer> parallelMap(Flux<Integer> source) {
        return source
            .parallel()
            .runOn(Schedulers.parallel())
            .map(x -> x * x)
            .sequential();
    }
}
