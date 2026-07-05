package com.security08;

import java.util.Map;

/**
 * Demonstrates CORS preflight request handling (OPTIONS).
 * 
 * SECURITY CONCEPT: Browsers send a preflight OPTIONS request before
 * actual cross-origin requests that:
 * - Use methods other than GET, HEAD, POST (e.g., PUT, DELETE, PATCH)
 * - Include custom headers (e.g., Authorization, X-Requested-With)
 * - Send credentials (cookies, HTTP auth)
 * 
 * The preflight asks the server if the cross-origin request is allowed.
 * The server must respond with appropriate CORS headers.
 * 
 * Without proper preflight handling, complex cross-origin requests fail.
 * With overly permissive preflight handling, security is compromised.
 */
public class PreflightHandler {

    /**
     * Determines if a request is a CORS preflight.
     * A preflight request has:
     * - Method: OPTIONS
     * - Headers: Origin + Access-Control-Request-Method
     */
    public static boolean isPreflightRequest(String method, Map<String, String> headers) {
        boolean isOptions = "OPTIONS".equalsIgnoreCase(method);
        boolean hasOrigin = headers.containsKey("Origin");
        boolean hasRequestMethod = headers.containsKey("Access-Control-Request-Method");
        return isOptions && hasOrigin && hasRequestMethod;
    }

    /**
     * Handles a preflight request and returns CORS response headers.
     * SECURITY: Validate that the requested method and headers
     * are within the server's allowed policy.
     */
    public static Map<String, String> handlePreflight(
            String origin,
            String requestMethod,
            String requestHeaders,
            CorsConfig corsConfig) {

        System.out.println("Handling preflight request:");
        System.out.println("  Origin: " + origin);
        System.out.println("  Request-Method: " + requestMethod);
        System.out.println("  Request-Headers: " + requestHeaders);

        // SECURITY: Validate the requested method is allowed
        if (requestMethod != null) {
            Map<String, String> headers = corsConfig.buildCorsHeaders(origin, "OPTIONS");
            // Additional validation could go here
        }

        return corsConfig.buildCorsHeaders(origin, "OPTIONS");
    }

    /**
     * Simulates a complete preflight → actual request flow.
     */
    public static void simulateRequestFlow(String origin, String method,
                                           Map<String, String> headers,
                                           CorsConfig config) {
        // Step 1: Preflight detection
        if (isPreflightRequest(method, headers)) {
            System.out.println("1. PREFLIGHT DETECTED");
            Map<String, String> preflightHeaders = handlePreflight(
                    origin,
                    headers.get("Access-Control-Request-Method"),
                    headers.get("Access-Control-Request-Headers"),
                    config);

            if (preflightHeaders.containsKey("Access-Control-Allow-Origin")) {
                System.out.println("2. PREFLIGHT APPROVED → browser sends actual request");
                boolean actualAllowed = !config.buildCorsHeaders(origin, method).isEmpty();
                System.out.println("3. ACTUAL REQUEST: "
                        + (actualAllowed ? "ALLOWED" : "BLOCKED"));
            } else {
                System.out.println("2. PREFLIGHT REJECTED → browser blocks request");
            }
        } else {
            // Simple request (GET, HEAD, POST with standard headers)
            System.out.println("1. SIMPLE REQUEST (no preflight needed)");
            boolean allowed = !config.buildCorsHeaders(origin, method).isEmpty();
            System.out.println("2. REQUEST: " + (allowed ? "ALLOWED" : "BLOCKED"));
        }
    }

    public static void main(String[] args) {
        CorsConfig config = CorsConfig.productionConfig();

        System.out.println("=== CORS Preflight Handling ===\n");

        // Scenario 1: Complex request (DELETE) triggers preflight
        System.out.println("Scenario 1: DELETE with Authorization header");
        Map<String, String> headers1 = Map.of(
            "Origin", "https://app.example.com",
            "Access-Control-Request-Method", "DELETE",
            "Access-Control-Request-Headers", "Authorization"
        );
        simulateRequestFlow("https://app.example.com", "OPTIONS", headers1, config);
        System.out.println();

        // Scenario 2: Simple GET request (no preflight)
        System.out.println("Scenario 2: Simple GET request");
        Map<String, String> headers2 = Map.of("Origin", "https://app.example.com");
        simulateRequestFlow("https://app.example.com", "GET", headers2, config);
        System.out.println();

        // Scenario 3: Cross-origin request from disallowed origin
        System.out.println("Scenario 3: Disallowed origin");
        Map<String, String> headers3 = Map.of(
            "Origin", "https://evil.com",
            "Access-Control-Request-Method", "POST"
        );
        simulateRequestFlow("https://evil.com", "OPTIONS", headers3, config);
    }
}
