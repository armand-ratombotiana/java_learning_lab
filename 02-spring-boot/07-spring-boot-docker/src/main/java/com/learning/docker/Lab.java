package com.learning.docker;

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
@RequestMapping("/api/items")
class ItemController {

    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public ItemController() {
        Item sample = new Item(1L, "Docker Item", "Sample for containerized app");
        items.put(1L, sample);
        idGen.set(2L);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        return ResponseEntity.ok(Map.of(
            "count", items.size(),
            "items", items.values(),
            "app", "spring-boot-docker:1.0.0"
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getById(@PathVariable Long id) {
        Item item = items.get(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Item> create(@RequestBody Item item) {
        item.setId(idGen.getAndIncrement());
        items.put(item.getId(), item);
        return ResponseEntity.status(201).body(item);
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
            "app", "Spring Boot Docker Lab",
            "dockerfile", "Multi-stage build",
            "ports", "8080:8080",
            "health", "/actuator/health"
        );
    }
}

class Item {
    private Long id;
    private String name;
    private String description;

    public Item() {}
    public Item(Long id, String name, String description) {
        this.id = id; this.name = name; this.description = description;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
}
