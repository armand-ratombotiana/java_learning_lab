package com.learning.backend15;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Handler functions for the functional WebFlux routing.
 *
 * Each method receives a ServerRequest and returns Mono<ServerResponse>.
 * Handlers are pure functions — they don't use @Controller or @RestController.
 */
@Component
public class ProductHandler {

    private static final Logger log = LoggerFactory.getLogger(ProductHandler.class);
    private final ProductRepository repository;

    public ProductHandler(ProductRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        log.info("Handling GET all products");
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(repository.findAll(), Product.class);
    }

    public Mono<ServerResponse> getProductById(ServerRequest request) {
        String id = request.pathVariable("id");
        log.info("Handling GET product by id: {}", id);
        return repository.findById(id)
            .flatMap(product -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(product)))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        log.info("Handling POST create product");
        return request.bodyToMono(Product.class)
            .flatMap(product -> {
                log.info("Creating product: {}", product);
                return repository.save(product);
            })
            .flatMap(saved -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(saved)));
    }

    public Mono<ServerResponse> streamProducts(ServerRequest request) {
        log.info("Handling SSE stream of products");
        return ServerResponse.ok()
            .contentType(MediaType.TEXT_EVENT_STREAM)
            .body(repository.findAll(), Product.class);
    }
}
