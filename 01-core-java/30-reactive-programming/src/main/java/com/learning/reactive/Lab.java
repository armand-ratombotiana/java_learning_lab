package com.learning.reactive;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Reactive Programming Lab ===\n");

        reactiveStreamsApi();
        customPublisher();
        backpressure();
        operators();
        schedulers();
    }

    static void reactiveStreamsApi() {
        System.out.println("--- Reactive Streams (java.util.concurrent.Flow) ---");

        System.out.println("  Flow.Publisher<T>   - emits items");
        System.out.println("  Flow.Subscriber<T>  - consumes items");
        System.out.println("  Flow.Subscription   - controls demand (request(n)/cancel)");
        System.out.println("  Flow.Processor<T,R> - both Publisher and Subscriber");

        System.out.println("\n  Subscriber lifecycle:");
        System.out.println("    onSubscribe(Subscription) -> request(N)");
        System.out.println("    onNext(T)  (×N) -> onComplete() | onError(Throwable)");

        System.out.println("\n  Libraries comparison:");
        for (var c : List.of(
            "Project Reactor: Mono<T>, Flux<T>",
            "RxJava 3: Single<T>, Observable<T>",
            "JDK Flow: Publisher<T>"))
            System.out.println("  " + c);
    }

    static void customPublisher() {
        System.out.println("\n--- Custom Publisher & SubmissionPublisher ---");

        class RangePublisher implements Flow.Publisher<Integer> {
            final int max;
            RangePublisher(int m) { max = m; }

            public void subscribe(Flow.Subscriber<? super Integer> sub) {
                sub.onSubscribe(new Flow.Subscription() {
                    int emitted = 0;
                    long requested = 0;
                    volatile boolean cancelled = false;

                    public void request(long n) {
                        requested += n;
                        while (emitted < max && requested > 0 && !cancelled) {
                            sub.onNext(++emitted);
                            requested--;
                        }
                        if (emitted >= max && !cancelled) sub.onComplete();
                    }

                    public void cancel() { cancelled = true; }
                });
            }
        }

        System.out.println("  Custom publisher emitting 1-5:");
        new RangePublisher(5).subscribe(new Flow.Subscriber<>() {
            public void onSubscribe(Flow.Subscription s) { s.request(3); }
            public void onNext(Integer i) { System.out.println("    " + i); }
            public void onError(Throwable t) {}
            public void onComplete() { System.out.println("    Done!"); }
        });

        System.out.println("\n  SubmissionPublisher (JDK built-in):");
        var sp = new SubmissionPublisher<String>();
        sp.subscribe(new Flow.Subscriber<>() {
            Flow.Subscription s;
            public void onSubscribe(Flow.Subscription s) { this.s = s; s.request(Long.MAX_VALUE); }
            public void onNext(String i) { System.out.println("    " + i); }
            public void onError(Throwable t) {}
            public void onComplete() { System.out.println("    SP done"); }
        });
        sp.submit("A"); sp.submit("B"); sp.submit("C"); sp.close();
        try { Thread.sleep(50); } catch (InterruptedException e) {}
    }

    static void backpressure() {
        System.out.println("\n--- Backpressure ---");

        class SlowPublisher implements Flow.Publisher<Integer> {
            public void subscribe(Flow.Subscriber<? super Integer> sub) {
                sub.onSubscribe(new Flow.Subscription() {
                    int n = 0;
                    public void request(long r) {
                        System.out.println("    Demand: " + r);
                        for (int i = 0; i < r && n < 10; i++) sub.onNext(++n);
                        if (n >= 10) sub.onComplete();
                    }
                    public void cancel() {}
                });
            }
        }

        System.out.println("  Slow consumer requesting one at a time:");
        new SlowPublisher().subscribe(new Flow.Subscriber<>() {
            Flow.Subscription s;
            public void onSubscribe(Flow.Subscription s) { this.s = s; s.request(1); }
            public void onNext(Integer i) {
                System.out.println("    Processed: " + i + " [requesting next]");
                s.request(1);
            }
            public void onError(Throwable t) {}
            public void onComplete() { System.out.println("    All consumed"); }
        });

        System.out.println("\n  Backpressure strategies:");
        for (var s : List.of("BUFFER - overflow to buffer", "DROP - drop overflow",
                "LATEST - keep latest", "ERROR - throw on overflow"))
            System.out.println("  " + s);
    }

    static void operators() {
        System.out.println("\n--- Reactive Operators ---");

        class SimFlux<T> {
            final List<T> items;
            SimFlux(List<T> i) { items = i; }
            SimFlux<T> filter(Predicate<T> p) { return new SimFlux<>(items.stream().filter(p).toList()); }
            <R> SimFlux<R> map(Function<T, R> f) { return new SimFlux<>(items.stream().map(f).toList()); }
            SimFlux<T> skip(int n) { return new SimFlux<>(items.subList(Math.min(n, items.size()), items.size())); }
            SimFlux<T> take(int n) { return new SimFlux<>(items.subList(0, Math.min(n, items.size()))); }
            List<T> collect() { return items; }
        }

        var flux = new SimFlux<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        System.out.println("  Original: " + flux.collect());
        System.out.println("  filter(even): " + flux.filter(n -> n % 2 == 0).collect());
        System.out.println("  map(sq): " + flux.map(n -> n * n).collect());
        System.out.println("  skip(3).take(4): " + flux.skip(3).take(4).collect());

        System.out.println("\n  Operators by category:");
        for (var c : List.of(
            "Creation: just, fromIterable, range, interval, empty, error",
            "Transform: map, flatMap, concatMap, switchMap",
            "Filter: filter, distinct, take, skip, first",
            "Combine: merge, concat, zip, combineLatest",
            "Error: onErrorReturn, onErrorResume, retry, timeout",
            "Side effect: doOnNext, doOnError, doOnComplete"))
            System.out.println("  " + c);
    }

    static void schedulers() {
        System.out.println("\n--- Schedulers & Threading ---");

        var executors = List.of(
            "parallel - Fixed pool (CPU-bound, size = cores)",
            "boundedElastic - Bounded pool (I/O-bound)",
            "single - Single thread (ordered execution)",
            "immediate - Current thread (no switch)"
        );
        System.out.println("  Reactor schedulers:");
        executors.forEach(s -> System.out.println("  " + s));

        var pool = Executors.newFixedThreadPool(2);
        pool.submit(() -> System.out.println("  [" + Thread.currentThread().getName() + "] Task 1"));
        pool.submit(() -> System.out.println("  [" + Thread.currentThread().getName() + "] Task 2"));
        pool.shutdown();

        System.out.println("\n  subscribeOn vs publishOn:");
        System.out.println("  subscribeOn - affects upstream (source) thread");
        System.out.println("  publishOn   - affects downstream operators thread");

        System.out.println("\n  Hot vs Cold:");
        System.out.println("  Cold: each subscriber gets own stream (Flux.just)");
        System.out.println("  Hot:  subscribers share stream (Sinks.many, interval)");
    }
}
