package com.learning.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
public class RestApiLab {
    public static void main(String[] args) {
        SpringApplication.run(RestApiLab.class, args);
    }
}

@RestController
class ProductRestController {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @GetMapping("/api/products")
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Map.of(
            "content", products.values().stream().toList(),
            "page", page, "size", size, "total", products.size()
        ));
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        Product product = products.get(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping("/api/products")
    public ResponseEntity<Product> create(@RequestBody Product product) {
        product.setId(idGenerator.getAndIncrement());
        products.put(product.getId(), product);
        return ResponseEntity.status(201).body(product);
    }

    @PutMapping("/api/products/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        if (!products.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        product.setId(id);
        products.put(id, product);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/api/products/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!products.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        products.remove(id);
        return ResponseEntity.noContent().build();
    }
}

class Product {
    private Long id;
    private String name;
    private String description;
    private double price;

    public Product() {}
    public Product(Long id, String name, String description, double price) {
        this.id = id; this.name = name; this.description = description; this.price = price;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}