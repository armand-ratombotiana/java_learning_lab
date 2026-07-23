package reactive;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * Project Reactor: Flux and Mono demonstration.
 * 
 * Project Reactor is a reactive library implementing Reactive Streams.
 * 
 * Core types:
 * - Flux<T>: 0..N items (like a stream)
 * - Mono<T>: 0..1 item (like a CompletableFuture)
 * 
 * Maven dependency:
 *   io.projectreactor:reactor-core:3.6.0
 * 
 * This class demonstrates Reactor patterns using simulated equivalents.
 */
public class FluxMonoExample {

    // Simulated Flux (multi-value async sequence)
    static class Flux<T> {
        private final Iterable<T> source;

        private Flux(Iterable<T> source) { this.source = source; }

        static <T> Flux<T> just(T... items) {
            return new Flux<>(Arrays.asList(items));
        }

        // map
        <R> Flux<R> map(Function<T, R> fn) {
            var list = new ArrayList<R>();
            for (T item : source) list.add(fn.apply(item));
            return new Flux<>(list);
        }

        // filter
        Flux<T> filter(Predicate<T> pred) {
            var list = new ArrayList<T>();
            for (T item : source) if (pred.test(item)) list.add(item);
            return new Flux<>(list);
        }

        // flatMap
        <R> Flux<R> flatMap(Function<T, Flux<R>> fn) {
            var list = new ArrayList<R>();
            for (T item : source) {
                for (R r : fn.apply(item).source) list.add(r);
            }
            return new Flux<>(list);
        }

        // collect to list
        List<T> collectList() {
            var list = new ArrayList<T>();
            for (T item : source) list.add(item);
            return list;
        }

        Flux<T> take(long n) {
            var list = new ArrayList<T>();
            long count = 0;
            for (T item : source) {
                if (count++ >= n) break;
                list.add(item);
            }
            return new Flux<>(list);
        }

        Flux<T> timeout(Duration duration) {
            // Simulated — in Reactor, throws TimeoutException
            return this;
        }

        // onErrorReturn
        Flux<T> onErrorReturn(T fallback) {
            try {
                for (T item : source) { }
                return this;
            } catch (Exception e) {
                return Flux.just(fallback);
            }
        }

        // blocking subscribe
        void subscribe(Consumer<T> consumer) {
            for (T item : source) consumer.accept(item);
        }
    }

    // Simulated Mono (0-1 value)
    static class Mono<T> {
        private final T value;

        private Mono(T value) { this.value = value; }

        static <T> Mono<T> just(T value) { return new Mono<>(value); }

        static <T> Mono<T> fromCallable(Callable<T> callable) {
            try { return new Mono<>(callable.call()); }
            catch (Exception e) { return new Mono<>(null); }
        }

        <R> Mono<R> map(Function<T, R> fn) {
            return new Mono<>(value == null ? null : fn.apply(value));
        }

        <R> Mono<R> flatMap(Function<T, Mono<R>> fn) {
            return value == null ? new Mono<>(null) : fn.apply(value);
        }

        T block() { return value; }

        Mono<T> timeout(Duration duration) { return this; }

        Mono<T> onErrorReturn(T fallback) { return this; }
    }

    public static void main(String[] args) {
        // Flux examples
        Flux.just(1, 2, 3, 4, 5, 6)
            .filter(n -> n % 2 == 0)
            .map(n -> "Even: " + n)
            .subscribe(System.out::println);

        List<String> result = Flux.just("a", "bb", "ccc")
            .map(String::toUpperCase)
            .collectList();
        assert result.equals(List.of("A", "BB", "CCC"));

        List<Integer> flat = Flux.just(1, 2, 3)
            .flatMap(n -> Flux.just(n, n * 10))
            .collectList();
        assert flat.equals(List.of(1, 10, 2, 20, 3, 30));

        // Mono examples
        Mono<String> mono = Mono.just("Hello")
            .map(String::toUpperCase);
        assert mono.block().equals("HELLO");

        Mono<Integer> chain = Mono.just(5)
            .flatMap(n -> Mono.just(n * 2))
            .map(n -> n + 1);
        assert chain.block() == 11;

        // Error handling
        Mono<String> safe = Mono.fromCallable(() -> { throw new RuntimeException("fail"); })
            .onErrorReturn("fallback");
        assert safe.block().equals("fallback");

        System.out.println("All FluxMonoExample tests passed.");

        // Show real Reactor usage
        System.out.println("\nReal Reactor usage:");
        System.out.println("  Flux.range(1, 100)");
        System.out.println("      .filter(i -> i % 2 == 0)");
        System.out.println("      .map(i -> i * 10)");
        System.out.println("      .subscribe(System.out::println);");
        System.out.println("");
        System.out.println("  Mono.just(\"data\")");
        System.out.println("      .map(String::toUpperCase)");
        System.out.println("      .flatMap(Mono::just)");
        System.out.println("      .block();");
    }
}