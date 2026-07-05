package com.security10;

import java.security.PublicKey;
import java.util.*;
import java.util.function.Function;

/**
 * Demonstrates OAuth2 Resource Server configuration.
 * 
 * SECURITY CONCEPT: An OAuth2 Resource Server protects APIs by
 * validating Bearer tokens (JWTs) presented by clients.
 * 
 * Key responsibilities:
 * 1. Extract Bearer token from Authorization header
 * 2. Validate JWT signature using JWKS endpoint or pre-configured key
 * 3. Validate JWT claims (exp, iss, aud)
 * 4. Extract authorities from token claims (scope, roles)
 * 5. Create Spring Security Authentication object
 * 
 * Configuration types:
 * - JWK Set URI: nimbus-jose-jwt fetches keys dynamically
 * - Pre-configured public key: for offline verification
 * - Introspection URI: for opaque tokens (calls auth server)
 */
public class OAuth2ResourceServerConfig {

    // Simulated JWK Set (in production, fetched from auth server)
    private final Map<String, PublicKey> trustedIssuers = new LinkedHashMap<>();
    private final Set<String> allowedAudiences = new LinkedHashSet<>();
    private final String jwkSetUri;
    private final boolean requireHttps;

    public OAuth2ResourceServerConfig(String jwkSetUri, boolean requireHttps) {
        this.jwkSetUri = jwkSetUri;
        this.requireHttps = requireHttps;
        this.allowedAudiences.add("my-api");
    }

    /**
     * Validates the resource server configuration.
     * SECURITY: Refuse to start if security requirements aren't met.
     */
    public boolean validate() {
        if (jwkSetUri == null || jwkSetUri.isEmpty()) {
            System.err.println("ERROR: jwk-set-uri is required");
            return false;
        }
        if (requireHttps && !jwkSetUri.startsWith("https://")) {
            System.err.println("SECURITY: JWKS endpoint must use HTTPS");
            return false;
        }
        if (allowedAudiences.isEmpty()) {
            System.err.println("SECURITY: At least one allowed audience must be configured");
            return false;
        }
        return true;
    }

    /**
     * Builds a token validation strategy.
     * In Spring Security, this configures NimbusJwtDecoder.
     * 
     * Validation steps:
     * 1. Verify JWT signature with JWKS
     * 2. Check token hasn't expired (exp claim)
     * 3. Verify issuer (iss claim)
     * 4. Verify audience (aud claim)
     * 5. Check not-before time (nbf claim)
     */
    public Function<String, Map<String, Object>> buildTokenValidator() {
        return bearerToken -> {
            Map<String, Object> result = new LinkedHashMap<>();

            // Step 1: Parse JWT
            String[] parts = bearerToken.split("\\.");
            if (parts.length != 3) {
                result.put("valid", false);
                result.put("error", "Malformed JWT");
                return result;
            }

            // Step 2: Decode header and payload (signature verification skipped for demo)
            try {
                String headerJson = new String(
                        Base64.getUrlDecoder().decode(parts[0]));
                String payloadJson = new String(
                        Base64.getUrlDecoder().decode(parts[1]));

                result.put("header", headerJson);
                result.put("payload", payloadJson);

                // Step 3: Extract and validate claims
                Map<String, Object> claims = parseClaims(payloadJson);

                // Validate expiration
                long exp = ((Number) claims.getOrDefault("exp", 0L)).longValue();
                long now = System.currentTimeMillis() / 1000;
                if (now > exp) {
                    result.put("valid", false);
                    result.put("error", "Token expired");
                    return result;
                }

                // Validate issuer
                String iss = (String) claims.get("iss");
                if (iss == null || !trustedIssuers.containsKey(iss)) {
                    result.put("valid", false);
                    result.put("error", "Untrusted issuer: " + iss);
                    return result;
                }

                // Validate audience
                Object audObj = claims.get("aud");
                Set<String> audiences = new HashSet<>();
                if (audObj instanceof String) {
                    audiences.add((String) audObj);
                } else if (audObj instanceof List) {
                    audiences.addAll((List<String>) audObj);
                }
                if (audiences.stream().noneMatch(allowedAudiences::contains)) {
                    result.put("valid", false);
                    result.put("error", "Invalid audience: " + audiences);
                    return result;
                }

                result.put("valid", true);
                result.put("claims", claims);
                result.put("authorities", extractAuthorities(claims));
                return result;

            } catch (Exception e) {
                result.put("valid", false);
                result.put("error", "Token validation error: " + e.getMessage());
                return result;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseClaims(String payloadJson) {
        // Simplified JSON parsing
        Map<String, Object> claims = new LinkedHashMap<>();
        String cleaned = payloadJson.replaceAll("[{}\"]", "");
        for (String pair : cleaned.split(",")) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String key = kv[0].trim();
                String value = kv[1].trim();
                if (key.equals("exp") || key.equals("iat") || key.equals("nbf")) {
                    claims.put(key, Long.parseLong(value));
                } else {
                    claims.put(key, value);
                }
            }
        }
        return claims;
    }

    /**
     * Extracts Spring Security GrantedAuthorities from JWT claims.
     * SECURITY: Map scopes/roles from the JWT to authorities.
     * Common claim mappings:
     * - "scope" or "scp" → SCOPE_read, SCOPE_write
     * - "roles" → ROLE_ADMIN, ROLE_USER
     * - "groups" → GROUP_engineering
     */
    private List<String> extractAuthorities(Map<String, Object> claims) {
        List<String> authorities = new ArrayList<>();

        // Extract scopes
        Object scope = claims.get("scope");
        if (scope != null) {
            Arrays.stream(scope.toString().split(" "))
                    .map(s -> "SCOPE_" + s)
                    .forEach(authorities::add);
        }

        // Extract roles
        Object roles = claims.get("roles");
        if (roles instanceof List) {
            ((List<String>) roles).stream()
                    .map(r -> "ROLE_" + r)
                    .forEach(authorities::add);
        }

        return authorities;
    }

    /**
     * Registers a trusted issuer with its JWKS URI.
     */
    public void registerIssuer(String issuer, PublicKey publicKey) {
        this.trustedIssuers.put(issuer, publicKey);
    }

    public static void main(String[] args) {
        System.out.println("=== OAuth2 Resource Server Configuration ===\n");

        // Production resource server config
        OAuth2ResourceServerConfig config = new OAuth2ResourceServerConfig(
                "https://auth.example.com/realms/academy/protocol/openid-connect/certs",
                true);

        config.registerIssuer("https://auth.example.com/realms/academy", null);
        config.allowedAudiences.add("my-api");

        System.out.println("Configuration valid: " + config.validate());

        // Simulate token validation
        Function<String, Map<String, Object>> validator = config.buildTokenValidator();

        // Valid token simulation
        String validToken = "eyJhbGciOiJSUzI1NiJ9."
                + "eyJzdWIiOiJ1c2VyMTIzIiwiZXhwIjo0NzI4MzE1NjAwLCJpc3MiOiJodHRwczovL2F1dG"
                + "guZXhhbXBsZS5jb20vcmVhbG1zL2FjYWRlbXkiLCJhdWQiOiJteS1hcGkiLCJzY29wZSI6"
                + "InJlYWQgd3JpdGUiLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXX0.fakesig";
        System.out.println("\nValid token validation:");
        Map<String, Object> result = validator.apply(validToken);
        System.out.println("  Valid: " + result.get("valid"));
        System.out.println("  Authorities: " + result.get("authorities"));

        // Expired token
        String expiredToken = "eyJhbGciOiJSUzI1NiJ9."
                + "eyJleHAiOjEsImlzcyI6Imh0dHBzOi8vYXV0aC5leGFtcGxlLmNvbS9yZWFsbXMvYWNhZGV"
                + "teSIsImF1ZCI6Im15LWFwaSJ9.fakesig";
        System.out.println("\nExpired token validation:");
        result = validator.apply(expiredToken);
        System.out.println("  Valid: " + result.get("valid"));
        System.out.println("  Error: " + result.get("error"));
    }
}
