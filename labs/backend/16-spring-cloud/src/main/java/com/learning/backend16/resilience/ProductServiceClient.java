package com.learning.backend16.resilience;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class ProductServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceClient.class);
    private final RestTemplate restTemplate;

    public ProductServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "getDefaultInventory")
    @Retry(name = "inventoryService", fallbackMethod = "getDefaultInventory")
    @Bulkhead(name = "inventoryService", type = Bulkhead.Type.THREADPOOL, fallbackMethod = "getDefaultInventory")
    public Map<String, Object> getInventory(String productId) {
        log.info("Fetching inventory for product: {}", productId);
        String url = "http://INVENTORY-SERVICE/api/inventory/{productId}";
        return restTemplate.getForObject(url, Map.class, productId);
    }

    private Map<String, Object> getDefaultInventory(String productId, Throwable t) {
        log.warn("Circuit breaker triggered for inventory service: {}", t.getMessage());
        return Map.of(
            "productId", productId,
            "available", false,
            "quantity", 0,
            "status", "UNAVAILABLE",
            "source", "fallback"
        );
    }
}
