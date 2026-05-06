package com.learning.reactive;

import java.util.*;
import java.time.Duration;

public class ReactiveProgrammingTraining {

    public static void main(String[] args) {
        System.out.println("=== Reactive Programming Training ===");

        demonstrateReactiveConcepts();
        demonstrateMono();
        demonstrateFlux();
        demonstrateWebFlux();
    }

    private static void demonstrateReactiveConcepts() {
        System.out.println("\n--- Reactive Programming Concepts ---");

        String[] concepts = {
            "Asynchronous - non-blocking operations",
            "Non-blocking - immediate return, callback-based",
            "Event-driven - react to data events",
            "Backpressure - consumer controls data flow",
            "Immutability - data streams are immutable"
        };

        System.out.println("Reactive Principles:");
        for (String c : concepts) System.out.println("  - " + c);

        System.out.println("\nReactive vs Blocking:");
        String[] comparison = {
            "Blocking: Thread waits for result",
            "Reactive: Thread continues, notified on result",
            "Blocking: High memory with many threads",
            "Reactive: Low memory with async handlers"
        };

        for (String c : comparison) System.out.println("  " + c);

        System.out.println("\nReactive Streams API:");
        String[] api = {
            "Publisher - emits data",
            "Subscriber - receives data",
            "Subscription - controls demand",
            "Processor - both publisher and subscriber"
        };

        for (String a : api) System.out.println("  " + a);
    }

    private static void demonstrateMono() {
        System.out.println("\n--- Mono<T> - Single Item ---");

        System.out.println("Mono creates 0 or 1 element:");
        String[] monoOps = {
            "Mono.just(value) - single value",
            "Mono.empty() - no value",
            "Mono.error(exception) - error",
            "Mono.fromCallable() - async computation",
            "Mono.delay() - time-based emission"
        };

        for (String m : monoOps) System.out.println("  " + m);

        System.out.println("\nMono Operators:");
        String[] operators = {
            "map() - transform value",
            "flatMap() - async transformation",
            "filter() - conditional emission",
            "defaultIfEmpty() - default on empty",
            "switchIfEmpty() - alternative Mono",
            "doOnSuccess() - side effect on success",
            "doOnError() - side effect on error"
        };

        for (String o : operators) System.out.println("  - " + o);

        System.out.println("\nMono Example:");
        String example = """
            Mono<String> mono = Mono.just("Hello")
                .map(String::toUpperCase)
                .filter(s -> s.length() > 5)
                .defaultIfEmpty("DEFAULT");
            
            mono.subscribe(System.out::println);""";
        System.out.println(example);
    }

    private static void demonstrateFlux() {
        System.out.println("\n--- Flux<T> - Multiple Items ---");

        System.out.println("Flux creates 0 to N elements:");
        String[] fluxOps = {
            "Flux.just(1, 2, 3) - finite stream",
            "Flux.range(1, 10) - range of numbers",
            "Flux.fromIterable(list) - from collection",
            "Flux.interval(Duration) - time-based",
            "Flux.generate() - dynamic generation"
        };

        for (String f : fluxOps) System.out.println("  " + f);

        System.out.println("\nTransforming Flux:");
        String[] transform = {
            "map() - transform each element",
            "flatMap() - async transformation",
            "filter() - filter elements",
            "distinct() - remove duplicates",
            "take() - limit elements",
            "skip() - skip elements",
            "reduce() - aggregate to single value",
            "collectList() - gather to list"
        };

        for (String t : transform) System.out.println("  - " + t);

        System.out.println("\nCombining Flux:");
        String[] combine = {
            "merge() - interleave multiple Flux",
            "concat() - sequentially append",
            "zip() - combine pairs",
            "combineLatest() - latest from each"
        };

        for (String c : combine) System.out.println("  " + c);

        System.out.println("\nBackpressure:");
        String[] backpressure = {
            "request() - subscriber requests items",
            "limitRate(n) - windowed requests",
            "onBackpressureBuffer() - buffer overflow",
            "onBackpressureDrop() - drop overflow"
        };

        for (String b : backpressure) System.out.println("  " + b);
    }

    private static void demonstrateWebFlux() {
        System.out.println("\n--- Spring WebFlux ---");

        System.out.println("WebFlux vs Web MVC:");
        String[] comparison = {
            "Web MVC - blocking, servlet-based",
            "WebFlux - reactive, non-blocking",
            "Web MVC - thread-per-request",
            "WebFlux - event-driven, few threads"
        };

        for (String c : comparison) System.out.println("  " + c);

        System.out.println("\nWebFlux Annotations:");
        String[] annotations = {
            "@RestController - reactive REST endpoint",
            "@GetMapping - GET request handler",
            "@PostMapping - POST request handler",
            "@RequestBody - reactive body binding"
        };

        for (String a : annotations) System.out.println("  " + a);

        System.out.println("\nWebFlux Controller:");
        String controller = """
            @RestController
            @RequestMapping("/api/users")
            public class UserController {
                
                @GetMapping
                public Flux<User> getAllUsers() {
                    return userService.findAll();
                }
                
                @GetMapping("/{id}")
                public Mono<User> getUser(@PathVariable Long id) {
                    return userService.findById(id);
                }
                
                @PostMapping
                public Mono<ResponseEntity<User>> create(
                        @RequestBody Mono<User> userMono) {
                    return userMono.flatMap(user -> 
                        userService.save(user)
                            .map(ResponseEntity::ok));
                }
            }""";
        System.out.println(controller);

        System.out.println("\nWebClient:");
        String client = """
            WebClient webClient = WebClient.create();
            
            Mono<User> user = webClient.get()
                .uri("http://api.example.com/users/1")
                .retrieve()
                .bodyToMono(User.class);
            
            Flux<User> users = webClient.get()
                .uri("http://api.example.com/users")
                .retrieve()
                .bodyToFlux(User.class);""";
        System.out.println(client);

        System.out.println("\nReactive Use Cases:");
        String[] useCases = {
            "Real-time streaming APIs",
            "High concurrency microservices",
            "Event-driven architectures",
            "Non-blocking I/O operations"
        };

        for (String u : useCases) System.out.println("  - " + u);
    }
}