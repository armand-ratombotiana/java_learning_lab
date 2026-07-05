package com.learning.backend15;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reactive repository using Project Reactor (Flux / Mono).
 *
 * Mono<T>:  a reactive type that emits 0 or 1 item.
 * Flux<T>:  a reactive type that emits 0..N items.
 *
 * These are the core publishers in Spring WebFlux / Reactor.
 * They are asynchronous, non-blocking, and composeable via operators.
 */
public class ProductRepository {

    private final Map<String, Product> products = new ConcurrentHashMap<>();

    public ProductRepository() {
        products.put("P001", new Product("P001", "Laptop", 999.99));
        products.put("P002", new Product("P002", "Mouse", 29.99));
        products.put("P003", new Product("P003", "Keyboard", 89.99));
    }

    public Flux<Product> findAll() {
        // Flux.fromIterable creates a Flux from a collection.
        // .delayElements simulates non-blocking latency.
        return Flux.fromIterable(products.values())
            .delayElements(Duration.ofMillis(100));
    }

    public Mono<Product> findById(String id) {
        // Mono.justOrEmpty returns Mono.empty() when null, allowing
        // reactive null-safe handling without NullPointerException.
        return Mono.justOrEmpty(products.get(id));
    }

    public Mono<Product> save(Product product) {
        products.put(product.getId(), product);
        return Mono.just(product);
    }

    public Mono<Void> deleteById(String id) {
        products.remove(id);
        return Mono.empty();
    }
}
