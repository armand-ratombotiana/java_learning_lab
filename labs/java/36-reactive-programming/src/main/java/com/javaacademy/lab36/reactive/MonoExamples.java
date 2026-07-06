package com.javaacademy.lab36.reactive;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.time.Duration;
import java.util.function.Supplier;

public class MonoExamples {

    public Mono<String> justExample() {
        return Mono.just("Hello Reactive World");
    }

    public Mono<String> emptyExample() {
        return Mono.empty();
    }

    public Mono<String> errorExample() {
        return Mono.error(new RuntimeException("Something went wrong"));
    }

    public Mono<String> fromSupplier(Supplier<String> supplier) {
        return Mono.fromSupplier(supplier);
    }

    public Mono<String> fromCallable() {
        return Mono.fromCallable(() -> {
            Thread.sleep(100);
            return "Computed value";
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> mapExample(Mono<String> source) {
        return source.map(String::toUpperCase);
    }

    public Mono<String> flatMapExample(Mono<String> source) {
        return source.flatMap(val -> Mono.just("Processed: " + val));
    }

    public Mono<String> delayExample() {
        return Mono.just("Delayed result")
            .delayElement(Duration.ofMillis(100));
    }

    public Mono<String> defaultIfEmpty(Mono<String> source) {
        return source.defaultIfEmpty("Default Value");
    }

    public Mono<String> switchIfEmpty(Mono<String> source) {
        return source.switchIfEmpty(Mono.just("Fallback Value"));
    }

    public Mono<Integer> toInteger(Mono<String> source) {
        return source.map(Integer::parseInt);
    }

    public Mono<String> blockExample() {
        return Mono.fromSupplier(() -> {
            try { Thread.sleep(50); } catch (Exception ignored) { }
            return "Blocking Result";
        });
    }
}
