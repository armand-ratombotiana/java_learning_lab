package com.learning.backend15;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Service demonstrating WebClient and reactive operators.
 *
 * WebClient is the reactive equivalent of RestTemplate.
 * It is non-blocking and supports both synchronous and asynchronous calls.
 *
 * This class also demonstrates standard Reactor operators:
 * - map / flatMap: transform items
 * - filter: select items
 * - zip: combine multiple publishers
 * - timeout: limit wait time
 * - retry: recover from errors
 */
@Service
public class ReactiveService {

    private static final Logger log = LoggerFactory.getLogger(ReactiveService.class);
    private final ProductRepository repository;
    private final WebClient webClient;

    public ReactiveService(ProductRepository repository) {
        this.repository = repository;
        this.webClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .build();
    }

    /**
     * Returns all products with a simulated delay and a timeout.
     * Uses flux operators: delayElements, timeout, onErrorReturn.
     */
    public Flux<Product> getAllProductsWithTimeout() {
        return repository.findAll()
            .delayElements(Duration.ofMillis(200))
            .timeout(Duration.ofSeconds(5))
            .onErrorResume(e -> {
                log.error("Timeout or error fetching products", e);
                return Flux.empty();
            });
    }

    /**
     * Finds a product by ID and returns it wrapped in Mono.
     * If not found, returns a default product.
     */
    public Mono<Product> findProductOrDefault(String id) {
        return repository.findById(id)
            .defaultIfEmpty(new Product("DEFAULT", "Default Product", 0.0));
    }

    /**
     * Uses WebClient to call an external API reactively.
     * The call is non-blocking — the thread is freed during the HTTP request.
     */
    public Mono<String> callExternalApi() {
        log.info("Calling external API via WebClient");
        return webClient.get()
            .uri("/api/products")
            .retrieve()
            .bodyToFlux(Product.class)
            .map(Product::getName)
            .reduce((a, b) -> a + ", " + b)
            .map(names -> "Products: [" + names + "]");
    }

    /**
     * Combines multiple reactive sources using zip operator.
     */
    public Mono<String> getInventorySummary() {
        Mono<Long> countMono = repository.findAll().count();
        Mono<Double> totalValueMono = repository.findAll()
            .map(p -> p.getPrice())
            .reduce(0.0, Double::sum);

        return Mono.zip(countMono, totalValueMono)
            .map(tuple -> {
                long count = tuple.getT1();
                double total = tuple.getT2();
                return String.format("Inventory: %d products, total value: $%.2f", count, total);
            });
    }
}
