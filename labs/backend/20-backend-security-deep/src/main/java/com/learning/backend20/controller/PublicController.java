package com.learning.backend20.controller;

import com.learning.backend20.config.RateLimitingConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private static final Logger log = LoggerFactory.getLogger(PublicController.class);
    private final RateLimitingConfig rateLimitingConfig;

    public PublicController(RateLimitingConfig rateLimitingConfig) {
        this.rateLimitingConfig = rateLimitingConfig;
    }

    @PostMapping("/contact")
    public ResponseEntity<Map<String, String>> submitContact(
            @Valid @RequestBody ContactRequest request) {
        log.info("Contact form submitted: {}", request.email());
        return ResponseEntity.ok(Map.of("status", "received"));
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam String query,
            @RequestHeader("X-Forwarded-For") String clientIp) {
        var bucket = rateLimitingConfig.resolveBucket(clientIp);
        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(Map.of("query", sanitize(query), "results", "[]"));
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .body(Map.of("error", "Rate limit exceeded", "retryAfter", "60"));
    }

    private String sanitize(String input) {
        return input.replaceAll("[<>\"'&]", "");
    }

    public record ContactRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String message
    ) {}
}
