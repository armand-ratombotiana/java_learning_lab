package com.learning.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
public class Lab {
    public static void main(String[] args) {
        SpringApplication.run(Lab.class, args);
    }
}

@RestController
@RequestMapping("/api/orders")
class OrderController {

    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @GetMapping
    public Map<String, Object> getAll() {
        return Map.of("orders", orders.values(), "count", orders.size());
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order order) {
        order.setId(idGen.getAndIncrement());
        order.setStatus("CREATED");
        orders.put(order.getId(), order);
        return ResponseEntity.status(201).body(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        Order o = orders.get(id);
        return o != null ? ResponseEntity.ok(o) : ResponseEntity.notFound().build();
    }

    @GetMapping("/patterns")
    public Map<String, String> patterns() {
        return Map.of(
            "service-discovery", "Eureka / Consul",
            "api-gateway", "Spring Cloud Gateway",
            "circuit-breaker", "Resilience4j",
            "config-server", "Spring Cloud Config",
            "tracing", "Micrometer Tracing + Zipkin"
        );
    }
}

class Order {
    private Long id;
    private String customerId;
    private String product;
    private int quantity;
    private double total;
    private String status;

    public Order() {}
    public Order(Long id, String customerId, String product, int quantity, double total, String status) {
        this.id = id; this.customerId = customerId; this.product = product;
        this.quantity = quantity; this.total = total; this.status = status;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String c) { this.customerId = c; }
    public String getProduct() { return product; }
    public void setProduct(String p) { this.product = p; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int q) { this.quantity = q; }
    public double getTotal() { return total; }
    public void setTotal(double t) { this.total = t; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
}
