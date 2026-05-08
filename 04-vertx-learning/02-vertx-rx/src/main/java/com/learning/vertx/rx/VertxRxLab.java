package com.learning.vertx.rx;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VertxRxLab {

    static class Observable<T> {
        private final List<Consumer<T>> subscribers = new ArrayList<>();
        private final List<Consumer<Throwable>> errorHandlers = new ArrayList<>();
        private Runnable completionHandler;

        public void subscribe(Consumer<T> onNext) {
            subscribers.add(onNext);
        }

        public void subscribe(Consumer<T> onNext, Consumer<Throwable> onError) {
            subscribers.add(onNext);
            errorHandlers.add(onError);
        }

        public void onNext(T item) {
            for (Consumer<T> sub : subscribers) {
                sub.accept(item);
            }
        }

        public void onError(Throwable t) {
            for (Consumer<Throwable> h : errorHandlers) {
                h.accept(t);
            }
        }

        public void onComplete() {
            if (completionHandler != null) completionHandler.run();
        }

        public Observable<T> doOnComplete(Runnable action) {
            this.completionHandler = action;
            return this;
        }

        public <R> Observable<R> map(Function<T, R> mapper) {
            Observable<R> result = new Observable<>();
            subscribe(item -> result.onNext(mapper.apply(item)),
                      err -> result.onError(err));
            return result;
        }

        public Observable<T> filter(java.util.function.Predicate<T> predicate) {
            Observable<T> result = new Observable<>();
            subscribe(item -> { if (predicate.test(item)) result.onNext(item); },
                      err -> result.onError(err));
            return result;
        }
    }

    static class Flowable<T> {
        private final List<Consumer<T>> subscribers = new ArrayList<>();
        private long bufferSize = 128;
        private long requested = 0;

        public void subscribe(Consumer<T> onNext) {
            subscribers.add(onNext);
        }

        public void onNext(T item) {
            if (requested > 0 || requested == Long.MAX_VALUE) {
                for (Consumer<T> sub : subscribers) {
                    sub.accept(item);
                }
                if (requested != Long.MAX_VALUE) requested--;
            }
        }

        public void request(long n) {
            requested += n;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Vert.x RxJava Concepts Lab ===");
        System.out.println("Demonstrating reactive streams and RxJava patterns\n");

        observableDemo();
        flatMapAndComposition();
        backpressureDemo();
        errorHandling();
        asyncFlowDemo();
    }

    static void observableDemo() {
        System.out.println("--- Observable Pattern ---");
        Observable<String> source = new Observable<>();
        source.subscribe(item -> System.out.println("Received: " + item));
        source.onNext("Hello");
        source.onNext("RxWorld");
        source.onComplete();

        Observable<Integer> numbers = new Observable<>();
        Observable<Integer> doubled = numbers.map(x -> x * 2);
        Observable<Integer> even = numbers.filter(x -> x % 2 == 0);

        doubled.subscribe(d -> System.out.println("Doubled: " + d));
        even.subscribe(e -> System.out.println("Even: " + e));

        for (int i = 1; i <= 5; i++) {
            numbers.onNext(i);
        }
    }

    static void flatMapAndComposition() {
        System.out.println("\n--- FlatMap & Composition ---");
        Observable<String> words = new Observable<>();
        Observable<String> letters = new Observable<>();

        words.subscribe(word -> {
            for (char c : word.toCharArray()) {
                letters.onNext(String.valueOf(c));
            }
        });

        letters.subscribe(l -> System.out.print(l + " "));
        words.onNext("Rx");
        words.onNext("Java");
        System.out.println();
    }

    static void backpressureDemo() {
        System.out.println("\n--- Backpressure Concept ---");
        Flowable<String> fastProducer = new Flowable<>();
        fastProducer.subscribe(item -> {
            System.out.println("Consumer processing: " + item);
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        fastProducer.request(3);
        for (int i = 0; i < 10; i++) {
            fastProducer.onNext("Event-" + i);
        }
        System.out.println("Backpressure: only 3 events consumed, rest buffered");
    }

    static void errorHandling() {
        System.out.println("\n--- Reactive Error Handling ---");
        Observable<String> risky = new Observable<>();
        risky.subscribe(
            item -> {
                if (item.equals("fail")) throw new RuntimeException("Simulated failure");
                System.out.println("OK: " + item);
            },
            err -> System.out.println("Error caught: " + err.getMessage())
        );
        risky.onNext("ok");
        risky.onNext("fail");
        risky.onNext("after fail");
    }

    static void asyncFlowDemo() throws Exception {
        System.out.println("\n--- Async Reactive Flow (CompletableFuture) ---");
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Random rand = new Random();

        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(rand.nextInt(300));
            return 10;
        }, executor);

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(rand.nextInt(300));
            return 20;
        }, executor);

        CompletableFuture<Integer> combined = future1.thenCombine(future2, Integer::sum);
        System.out.println("Combined async result: " + combined.get());

        CompletableFuture<Integer> chained = CompletableFuture.supplyAsync(() -> 5, executor)
            .thenApplyAsync(x -> x * 3, executor)
            .thenApplyAsync(x -> x + 2, executor);
        System.out.println("Chained async result: " + chained.get());

        executor.shutdown();
    }

    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
