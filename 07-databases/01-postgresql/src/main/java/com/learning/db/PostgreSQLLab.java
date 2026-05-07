package com.learning.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.*;

import java.util.List;

@SpringBootApplication
public class PostgreSQLLab {
    public static void main(String[] args) {
        SpringApplication.run(PostgreSQLLab.class, args);
    }
}

@Entity
@Table(name = "products")
class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 2)
    private double price;

    public Product() {}
    public Product(String name, String description, double price) {
        this.name = name; this.description = description; this.price = price;
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

interface ProductRepository extends JpaRepository<Product, Long> {
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
    Product create(@RequestBody Product product) {
        return repository.save(product);
    }

    @GetMapping("/products/search")
    List<Product> search(@RequestParam String name) {
        return repository.findByNameContaining(name);
    }
}