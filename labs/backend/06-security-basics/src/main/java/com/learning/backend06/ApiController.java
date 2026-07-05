package com.learning.backend06;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller demonstrating secured and public endpoints.
 *
 * Access rules (defined in SecurityConfig):
 * - /api/public/** — anyone (no authentication)
 * - /api/admin/** — needs ADMIN role
 * - all others — needs authentication
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);

    @GetMapping("/public/hello")
    public Map<String, String> publicHello() {
        log.info("Public endpoint accessed");
        return Map.of("message", "This is a public endpoint, no auth required");
    }

    @GetMapping("/user/profile")
    public Map<String, Object> userProfile(Authentication authentication) {
        log.info("User profile accessed by: {}", authentication.getName());
        return Map.of(
            "username", authentication.getName(),
            "roles", authentication.getAuthorities(),
            "message", "Authenticated user endpoint"
        );
    }

    @GetMapping("/admin/dashboard")
    public Map<String, String> adminDashboard(Authentication authentication) {
        log.info("Admin dashboard accessed by: {}", authentication.getName());
        return Map.of(
            "message", "Welcome to the admin dashboard",
            "username", authentication.getName()
        );
    }
}
