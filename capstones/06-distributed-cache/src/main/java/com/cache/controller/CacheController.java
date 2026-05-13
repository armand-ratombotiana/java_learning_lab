package com.cache.controller;

import com.cache.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @PostMapping("/nodes/{nodeId}")
    public ResponseEntity<Map<String, String>> registerNode(@PathVariable String nodeId) {
        cacheService.registerNode(nodeId);
        return ResponseEntity.ok(Map.of("status", "registered", "nodeId", nodeId));
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> set(@RequestBody Map<String, Object> request) {
        String key = (String) request.get("key");
        String value = (String) request.get("value");
        Long ttl = request.get("ttl") != null ? ((Number) request.get("ttl")).longValue() : null;

        if (ttl != null) {
            cacheService.set(key, value, ttl);
        } else {
            cacheService.set(key, value);
        }

        return ResponseEntity.ok(Map.of("status", "set", "key", key));
    }

    @GetMapping("/{key}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String key) {
        Optional<String> value = cacheService.get(key);
        if (value.isPresent()) {
            return ResponseEntity.ok(Map.of("key", key, "value", value.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String key) {
        boolean deleted = cacheService.delete(key);
        return ResponseEntity.ok(Map.of("status", deleted ? "deleted" : "not_found", "key", key));
    }

    @PostMapping("/{key}/increment")
    public ResponseEntity<Map<String, Object>> increment(@PathVariable String key) {
        long newValue = cacheService.increment(key);
        return ResponseEntity.ok(Map.of("key", key, "value", newValue));
    }

    @GetMapping("/keys")
    public ResponseEntity<Map<String, Object>> keys(@RequestParam(defaultValue = "*") String pattern) {
        return ResponseEntity.ok(Map.of("pattern", pattern, "keys", cacheService.keys(pattern)));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> stats() {
        return ResponseEntity.ok(cacheService.getClusterStats());
    }
}