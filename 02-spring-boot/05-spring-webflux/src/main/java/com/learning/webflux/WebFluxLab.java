package com.learning.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class WebFluxLab {
    public static void main(String[] args) {
        SpringApplication.run(WebFluxLab.class, args);
    }
}

@RestController
class ReactiveProductController {

    private final AtomicInteger idCounter = new AtomicInteger(1);

    @GetMapping("/products/stream")
    public Flux<Product> streamProducts() {
        return Flux.interval(Duration.ofSeconds(1))
            .map(i -> new Product((long) i, "Product " + i, 9.99 * i))
            .take(10);
    }

    @GetMapping("/product/{id}")
    public Mono<Product> getProduct(@PathVariable Long id) {
        return Mono.just(new Product(id, "Product " + id, 19.99));
    }

    @PostMapping("/product")
    public Mono<Product> createProduct(@RequestBody Product product) {
        product.setId((long) idCounter.getAndIncrement());
        return Mono.just(product);
    }
}

class Product {
    private Long id;
    private String name;
    private double price;

    public Product() {}
    public Product(Long id, String name, double price) {
        this.id = id; this.name = name; this.price = price;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}