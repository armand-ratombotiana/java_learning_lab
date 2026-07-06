package com.javaacademy.lab36.reactive;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.util.concurrent.Queues;
import java.time.Duration;

public class BackpressureExample {

    public Flux<Integer> bufferStrategy(Flux<Integer> source, int bufferSize) {
        return source
            .onBackpressureBuffer(bufferSize, v -> System.out.println("Dropped: " + v));
    }

    public Flux<Integer> dropStrategy(Flux<Integer> source) {
        return source
            .onBackpressureDrop(v -> System.out.println("Dropped due to backpressure: " + v));
    }

    public Flux<Integer> latestStrategy(Flux<Integer> source) {
        return source
            .onBackpressureLatest();
    }

    public Flux<Integer> errorStrategy(Flux<Integer> source) {
        return source
            .onBackpressureError();
    }

    public Flux<Integer> slowConsumer(Flux<Integer> source) {
        return source
            .doOnRequest(n -> System.out.println("Requested: " + n))
            .concatMap(x -> Mono.just(x).delayElement(Duration.ofMillis(10)));
    }

    public Flux<Integer> fastProducer(int items) {
        return Flux.range(1, items)
            .doOnRequest(n -> System.out.println("Producer received request: " + n))
            .doOnNext(i -> {
                if (i % 100 == 0) {
                    System.out.println("Produced: " + i + " items");
                }
            });
    }

    public Flux<Integer> limitRate(Flux<Integer> source, int rate) {
        return source.limitRate(rate);
    }

    public Flux<Integer> overflowExample() {
        return Flux.range(1, 1000000)
            .log()
            .onBackpressureBuffer(256);
    }
}
