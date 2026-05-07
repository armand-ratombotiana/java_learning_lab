package com.learning.mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.annotation.Id;

import java.util.List;

@SpringBootApplication
public class MongoRepositoryLab {
    public static void main(String[] args) {
        SpringApplication.run(MongoRepositoryLab.class, args);
    }
}

@Document(collection = "products")
class Product {
    @Id private String id;
    private String name;
    private double price;
    private List<String> tags;

    public Product() {}
    public Product(String name, double price) {
        this.name = name; this.price = price;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}

interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByNameContaining(String name);
    List<Product> findByPriceLessThan(double price);
}

@RestController
class ProductController {
    private final ProductRepository repository;

    ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/products")
    List<Product> all() { return repository.findAll(); }

    @PostMapping("/products")
    Product create(Product product) {
        return repository.save(product);
    }
}