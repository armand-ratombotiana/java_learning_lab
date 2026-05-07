package com.learning.reactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class ReactorLab {

    public static void main(String[] args) {
        System.out.println("=== Reactor (Project Reactor) Lab ===\n");

        demonstrateMono();
        demonstrateFlux();
        demonstrateTransformations();
    }

    private static void demonstrateMono() {
        System.out.println("--- Mono (Single Value) ---");
        Mono.just("Hello Reactor")
            .map(String::toUpperCase)
            .subscribe(System.out::println);

        Mono.error(new RuntimeException("Error occurred"))
            .onErrorResume(e -> Mono.just("Recovered"))
            .subscribe(System.out::println, Throwable::printStackTrace);
    }

    private static void demonstrateFlux() {
        System.out.println("\n--- Flux (Multiple Values) ---");
        Flux.just("A", "B", "C", "D")
            .subscribe(System.out::print);
        System.out.println();

        Flux.range(1, 5)
            .filter(n -> n % 2 == 0)
            .subscribe(n -> System.out.println("Even: " + n));
    }

    private static void demonstrateTransformations() {
        System.out.println("\n--- Transformations ---");
        Flux.just(1, 2, 3, 4, 5)
            .map(n -> n * 2)
            .flatMap(n -> Flux.just(n, n * 10))
            .subscribe(System.out::println);

        Flux.just("a", "b", "c")
            .collectList()
            .subscribe(list -> System.out.println("Collected: " + list));

        Flux.interval(Duration.ofSeconds(1))
            .take(3)
            .subscribe(n -> System.out.println("Tick: " + n));
    }
}