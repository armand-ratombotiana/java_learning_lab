package com.security07;

import java.util.*;

/**
 * Demonstrates Content Security Policy (CSP) header configuration.
 * 
 * SECURITY CONCEPT: CSP is a browser security mechanism that prevents
 * XSS, data injection, and clickjacking by specifying which content
 * sources are trusted. CSP is implemented via the
 * Content-Security-Policy HTTP response header.
 * 
 * CSP directives control what resources can load:
 * - default-src: fallback for all resource types
 * - script-src: JavaScript sources
 * - style-src: CSS sources
 * - img-src: image sources
 * - connect-src: XMLHttpRequest, WebSocket, EventSource
 * - font-src: font sources
 * - frame-src: iframe sources (clickjacking protection)
 * - object-src: <object>, <embed>, <applet> (disable if unused)
 * - base-uri: <base> element (prevents base URI injection)
 * - report-uri / report-to: violation reporting endpoint
 * 
 * CSP levels:
 * - CSP Level 1: Basic directives, no inline scripts
 * - CSP Level 2: Nonces and hashes for inline scripts/styles
 * - CSP Level 3: Strict CSP using 'strict-dynamic'
 */
public class CspHeaderConfig {

    private final Map<String, List<String>> directives = new LinkedHashMap<>();

    /**
     * Builds a strict CSP policy.
     * SECURITY: Start restrictive and relax as needed.
     * Avoid 'unsafe-inline' and 'unsafe-eval' — they defeat CSP's purpose.
     */
    public CspHeaderConfig buildStrictPolicy() {
        // default-src: fallback — deny everything by default
        directives.put("default-src", List.of("'none'"));

        // script-src: only from same origin + nonce
        directives.put("script-src", List.of(
                "'self'",
                "'strict-dynamic'",   // CSP Level 3 — allows scripts loaded by trusted scripts
                "'unsafe-inline'",    // Remove this in production — needed only for backward compat
                "'unsafe-eval'"       // Remove in production — blocks eval(), setTimeout(string)
        ));
        // In production, use a nonce: script-src 'nonce-{random}' 'strict-dynamic'

        // style-src: only same origin
        directives.put("style-src", List.of("'self'"));

        // img-src: same origin + data URIs (for small inline images)
        directives.put("img-src", List.of("'self'", "data:"));

        // connect-src: same origin only
        directives.put("connect-src", List.of("'self'"));

        // font-src: same origin
        directives.put("font-src", List.of("'self'"));

        // frame-src: deny all frames (clickjacking protection)
        directives.put("frame-src", List.of("'none'"));

        // object-src: deny all plugins
        directives.put("object-src", List.of("'none'"));

        // base-uri: only same origin
        directives.put("base-uri", List.of("'self'"));

        // form-action: only same origin
        directives.put("form-action", List.of("'self'"));

        // Upgrade insecure requests: automatically upgrade HTTP to HTTPS
        directives.put("upgrade-insecure-requests", List.of());

        // Report violations
        directives.put("report-uri", List.of("/csp-violation-report"));

        return this;
    }

    /**
     * Builds a permissive CSP for development.
     * SECURITY: Never use this in production.
     */
    public CspHeaderConfig buildDevPolicy() {
        directives.put("default-src", List.of("'self'"));
        directives.put("script-src", List.of("'self'", "'unsafe-inline'"));
        directives.put("style-src", List.of("'self'", "'unsafe-inline'"));
        directives.put("img-src", List.of("'self'", "data:", "https:"));
        directives.put("connect-src", List.of("'self'", "https:"));
        directives.put("font-src", List.of("'self'", "https:"));
        directives.put("frame-src", List.of("'none'"));
        directives.put("object-src", List.of("'none'"));
        return this;
    }

    /**
     * Serializes the policy to a CSP header string.
     */
    public String toHeaderString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : directives.entrySet()) {
            if (sb.length() > 0) sb.append("; ");
            sb.append(entry.getKey());
            if (!entry.getValue().isEmpty()) {
                sb.append(" ").append(String.join(" ", entry.getValue()));
            }
        }
        return sb.toString();
    }

    /**
     * Simulates nonce generation for inline scripts.
     * SECURITY: Nonces must be:
     * - Cryptographically random per request
     * - At least 128 bits (16 bytes)
     * - Never reused
     * - Generated server-side
     */
    public static String generateNonce() {
        java.security.SecureRandom random = new java.security.SecureRandom();
        byte[] nonceBytes = new byte[16];
        random.nextBytes(nonceBytes);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(nonceBytes);
    }

    /**
     * Generates a hash for a specific inline script (SHA-256).
     * CSP Level 2 allows whitelisting specific inline scripts by hash.
     */
    public static String generateScriptHash(String scriptContent) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(scriptContent.getBytes());
        return "sha256-" + java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Content Security Policy (CSP) Configuration ===\n");

        // Strict policy
        CspHeaderConfig strictCsp = new CspHeaderConfig().buildStrictPolicy();
        System.out.println("Strict CSP Header:");
        System.out.println("Content-Security-Policy: " + strictCsp.toHeaderString());

        // Nonce generation
        System.out.println("\n--- Nonce-Based Script Loading ---");
        String nonce = generateNonce();
        System.out.println("Nonce for this request: " + nonce);
        System.out.println("Script tag: <script nonce=\"" + nonce + "\">");
        System.out.println("CSP: script-src 'nonce-" + nonce + "' 'strict-dynamic'");

        // Script hash
        System.out.println("\n--- Hash-Based Script Allowlisting ---");
        String script = "alert('hello');";
        String hash = generateScriptHash(script);
        System.out.println("Script: " + script);
        System.out.println("Hash:   " + hash);
        System.out.println("CSP:    script-src '" + hash + "'");

        System.out.println("\n--- CSP Best Practices ---");
        System.out.println("1. Start with 'default-src none' and relax as needed");
        System.out.println("2. Use nonce or hash for inline scripts, not 'unsafe-inline'");
        System.out.println("3. Avoid 'unsafe-eval' — use JSON.parse instead of eval()");
        System.out.println("4. Use 'strict-dynamic' with nonce (CSP Level 3)");
        System.out.println("5. Use 'upgrade-insecure-requests' for HTTPS migration");
        System.out.println("6. Implement reporting (report-uri / report-to)");
        System.out.println("7. Test with Content-Security-Policy-Report-Only first");
    }
}
