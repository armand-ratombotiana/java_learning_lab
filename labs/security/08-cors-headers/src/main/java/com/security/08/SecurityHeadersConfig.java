package com.security08;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Demonstrates HTTP security headers for application defense-in-depth.
 * 
 * SECURITY CONCEPT: Security headers tell browsers how to behave
 * when handling your application's responses. They provide critical
 * protections against XSS, clickjacking, MIME sniffing, and more.
 * 
 * Essential security headers:
 * 
 * 1. Strict-Transport-Security (HSTS)
 *    Forces HTTPS — prevents SSL stripping and downgrade attacks
 * 
 * 2. X-Content-Type-Options: nosniff
 *    Prevents MIME type sniffing (browser guessing content type)
 * 
 * 3. X-Frame-Options: DENY
 *    Prevents clickjacking — blocks rendering in <frame>/<iframe>
 * 
 * 4. X-XSS-Protection: 0
 *    Disables legacy XSS filter (was buggy, now deprecated)
 * 
 * 5. Referrer-Policy: strict-origin-when-cross-origin
 *    Controls what referrer info is sent with requests
 * 
 * 6. Permissions-Policy
 *    Controls browser features (camera, microphone, geolocation)
 * 
 * 7. Cache-Control: no-store
 *    Prevents sensitive data from being cached
 */
public class SecurityHeadersConfig {

    private final Map<String, String> headers = new LinkedHashMap<>();

    /**
     * Builds a comprehensive set of security headers.
     * In Spring Security: http.headers(headers -> headers...)
     */
    public SecurityHeadersConfig buildProductionHeaders() {
        // HSTS: enforce HTTPS for 1 year, include subdomains, preload
        headers.put("Strict-Transport-Security",
                "max-age=31536000; includeSubDomains; preload");

        // Prevent MIME sniffing
        headers.put("X-Content-Type-Options", "nosniff");

        // Prevent clickjacking
        headers.put("X-Frame-Options", "DENY");

        // Disable legacy XSS filter (modern browsers use CSP instead)
        headers.put("X-XSS-Protection", "0");

        // Control referrer information
        headers.put("Referrer-Policy", "strict-origin-when-cross-origin");

        // Restrict browser features
        headers.put("Permissions-Policy",
                "camera=(), microphone=(), geolocation=(), payment=()");

        // Prevent caching of sensitive responses
        headers.put("Cache-Control", "no-store, max-age=0");

        // Remove server header to avoid information disclosure
        headers.put("Server", ""); // In production: remove entirely

        return this;
    }

    /**
     * Builds minimal security headers for simple APIs.
     */
    public SecurityHeadersConfig buildApiHeaders() {
        headers.put("Strict-Transport-Security",
                "max-age=31536000; includeSubDomains");
        headers.put("X-Content-Type-Options", "nosniff");
        headers.put("Cache-Control", "no-store");
        return this;
    }

    /**
     * Adds a custom header.
     */
    public SecurityHeadersConfig addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    /**
     * Returns the headers as a map.
     */
    public Map<String, String> getHeaders() {
        return Map.copyOf(headers);
    }

    /**
     * Prints all configured headers.
     */
    public void printHeaders() {
        headers.forEach((name, value) ->
                System.out.println("  " + name + ": " + value));
    }

    /**
     * Analyzes existing headers for security gaps.
     */
    public static void auditHeaders(Map<String, String> existingHeaders) {
        Map<String, String> required = new SecurityHeadersConfig()
                .buildProductionHeaders().headers;

        System.out.println("=== Security Header Audit ===");
        for (Map.Entry<String, String> entry : required.entrySet()) {
            String actual = existingHeaders.get(entry.getKey());
            if (actual == null) {
                System.out.println("  MISSING: " + entry.getKey());
            } else if (!actual.equals(entry.getValue())) {
                System.out.println("  WEAK:    " + entry.getKey()
                        + " (expected: " + entry.getValue() + ")");
                System.out.println("           actual:   " + actual);
            } else {
                System.out.println("  OK:      " + entry.getKey());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== HTTP Security Headers ===\n");

        // Production configuration
        System.out.println("Production Security Headers:");
        SecurityHeadersConfig config = new SecurityHeadersConfig()
                .buildProductionHeaders();
        config.printHeaders();

        // Simulate a security audit
        System.out.println("\n--- Security Header Audit ---");
        Map<String, String> currentHeaders = new LinkedHashMap<>();
        currentHeaders.put("Strict-Transport-Security",
                "max-age=31536000; includeSubDomains");
        currentHeaders.put("X-Content-Type-Options", "nosniff");
        // Missing: X-Frame-Options, Referrer-Policy, Permissions-Policy, Cache-Control
        // Weak: HSTS missing 'preload'

        auditHeaders(currentHeaders);

        System.out.println("\n--- Header Protection Summary ---");
        System.out.println("HSTS:          Prevents SSL stripping");
        System.out.println("X-Frame-Options: Prevents clickjacking");
        System.out.println("X-Content-Type-Options: Prevents MIME sniffing");
        System.out.println("Referrer-Policy: Controls information leakage");
        System.out.println("Permissions-Policy: Restricts browser features");
        System.out.println("Cache-Control:  Prevents sensitive data caching");
    }
}
