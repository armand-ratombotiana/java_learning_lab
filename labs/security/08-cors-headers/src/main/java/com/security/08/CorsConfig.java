package com.security08;

import java.util.*;

/**
 * Demonstrates CORS (Cross-Origin Resource Sharing) configuration.
 * 
 * SECURITY CONCEPT: CORS is a browser security mechanism that controls
 * which origins (domains) can access resources from your server.
 * The browser enforces the Same-Origin Policy by default and uses
 * CORS headers to relax it selectively.
 * 
 * Key CORS headers:
 * - Access-Control-Allow-Origin: which origins are allowed (* is dangerous)
 * - Access-Control-Allow-Methods: which HTTP methods are allowed
 * - Access-Control-Allow-Headers: which headers the client can send
 * - Access-Control-Expose-Headers: which headers the client can read
 * - Access-Control-Allow-Credentials: whether cookies/auth headers are sent
 * - Access-Control-Max-Age: how long preflight results are cached
 * 
 * Security risks:
 * - Using `Access-Control-Allow-Origin: *` with credentials is NOT ALLOWED
 * - Origin reflection (echoing Origin header) can bypass CORS protections
 * - Overly permissive allowed methods or headers
 * - Missing Vary: Origin header breaks caching
 */
public class CorsConfig {

    private final Set<String> allowedOrigins;
    private final Set<String> allowedMethods;
    private final Set<String> allowedHeaders;
    private final Set<String> exposedHeaders;
    private final boolean allowCredentials;
    private final long maxAgeSeconds;

    private CorsConfig(Builder builder) {
        this.allowedOrigins = Collections.unmodifiableSet(builder.allowedOrigins);
        this.allowedMethods = Collections.unmodifiableSet(builder.allowedMethods);
        this.allowedHeaders = Collections.unmodifiableSet(builder.allowedHeaders);
        this.exposedHeaders = Collections.unmodifiableSet(builder.exposedHeaders);
        this.allowCredentials = builder.allowCredentials;
        this.maxAgeSeconds = builder.maxAgeSeconds;
    }

    /**
     * Builds CORS response headers for a given origin.
     * SECURITY: Validate origin against the allowed list.
     * Never use origin reflection — that allows any site to access your API.
     */
    public Map<String, String> buildCorsHeaders(String requestOrigin, String requestMethod) {
        Map<String, String> headers = new LinkedHashMap<>();

        // Validate origin (never echo back arbitrary origins)
        if (!isOriginAllowed(requestOrigin)) {
            System.out.println("  CORS: Origin NOT allowed: " + requestOrigin);
            return headers; // No CORS headers → browser blocks the request
        }

        headers.put("Access-Control-Allow-Origin", requestOrigin);
        headers.put("Vary", "Origin");

        if (allowCredentials) {
            headers.put("Access-Control-Allow-Credentials", "true");
        }

        // Handle preflight OPTIONS request
        if ("OPTIONS".equalsIgnoreCase(requestMethod)) {
            headers.put("Access-Control-Allow-Methods",
                    String.join(", ", allowedMethods));
            headers.put("Access-Control-Allow-Headers",
                    String.join(", ", allowedHeaders));
            headers.put("Access-Control-Max-Age", String.valueOf(maxAgeSeconds));
        }

        if (!exposedHeaders.isEmpty()) {
            headers.put("Access-Control-Expose-Headers",
                    String.join(", ", exposedHeaders));
        }

        return headers;
    }

    /**
     * Validates whether an origin is allowed.
     * SECURITY: Use exact matching, not prefix/suffix matching
     * (which can be bypassed: "evil-attacker.com/trustedsite.com").
     * Never allow null origins with credentials.
     */
    private boolean isOriginAllowed(String origin) {
        if (origin == null || origin.isEmpty()) {
            return false;
        }
        if (allowedOrigins.contains("*")) {
            // SECURITY: Wildcard allowed only without credentials
            return !allowCredentials;
        }
        return allowedOrigins.contains(origin);
    }

    /**
     * Recommended production CORS configuration.
     */
    public static CorsConfig productionConfig() {
        return new CorsConfig.Builder()
                .allowedOrigins("https://app.example.com", "https://admin.example.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With")
                .exposedHeaders("X-Total-Count", "X-RateLimit-Remaining")
                .allowCredentials(true)
                .maxAge(3600)
                .build();
    }

    /**
     * Insecure CORS configuration for demonstration.
     * SECURITY WARNING: Never use in production.
     */
    public static CorsConfig insecureConfig() {
        return new CorsConfig.Builder()
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(false)
                .build();
    }

    public static class Builder {
        private Set<String> allowedOrigins = new LinkedHashSet<>();
        private Set<String> allowedMethods = new LinkedHashSet<>(Set.of("GET", "POST"));
        private Set<String> allowedHeaders = new LinkedHashSet<>();
        private Set<String> exposedHeaders = new LinkedHashSet<>();
        private boolean allowCredentials;
        private long maxAgeSeconds = 1800;

        public Builder allowedOrigins(String... origins) {
            this.allowedOrigins.addAll(Arrays.asList(origins));
            return this;
        }
        public Builder allowedMethods(String... methods) {
            this.allowedMethods.addAll(Arrays.asList(methods));
            return this;
        }
        public Builder allowedHeaders(String... headers) {
            this.allowedHeaders.addAll(Arrays.asList(headers));
            return this;
        }
        public Builder exposedHeaders(String... headers) {
            this.exposedHeaders.addAll(Arrays.asList(headers));
            return this;
        }
        public Builder allowCredentials(boolean ac) { this.allowCredentials = ac; return this; }
        public Builder maxAge(long seconds) { this.maxAgeSeconds = seconds; return this; }
        public CorsConfig build() { return new CorsConfig(this); }
    }

    public static void main(String[] args) {
        System.out.println("=== CORS Configuration ===\n");

        CorsConfig production = CorsConfig.productionConfig();

        // Test requests from various origins
        String[][] testOrigins = {
            {"https://app.example.com", "GET"},
            {"https://evil.com", "POST"},
            {"http://app.example.com", "GET"}, // HTTP instead of HTTPS
            {null, "GET"}
        };

        System.out.println("Production CORS Policy:");
        System.out.println("  Allowed origins: " + production.allowedOrigins);
        System.out.println("  Allowed methods: " + production.allowedMethods);
        System.out.println("  Credentials: " + production.allowCredentials + "\n");

        for (String[] test : testOrigins) {
            String origin = test[0];
            String method = test[1];
            System.out.println("Request: " + method + " " + origin);
            Map<String, String> headers = production.buildCorsHeaders(origin, method);
            if (headers.isEmpty()) {
                System.out.println("  → BLOCKED by browser (no CORS headers)\n");
            } else {
                headers.forEach((k, v) -> System.out.println("  " + k + ": " + v));
                System.out.println();
            }
        }

        System.out.println("--- Insecure Config vs Production ---");
        System.out.println("Insecure:  Allow-Origin: *, Credentials: false");
        System.out.println("Production: Allow-Origin: [explicit list], Credentials: true");
    }
}
