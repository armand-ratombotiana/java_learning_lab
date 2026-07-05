package com.security10;

import java.util.*;

/**
 * Demonstrates a custom JWT Authentication Converter.
 * 
 * SECURITY CONCEPT: A JwtAuthenticationConverter extracts
 * authentication information from a validated JWT and creates
 * a Spring Security Authentication object.
 * 
 * Why customize the converter?
 * - Map custom JWT claims to GrantedAuthorities
 * - Support different token formats from different providers
 * - Extract user attributes beyond basic claims
 * - Implement custom claim validation logic
 * 
 * Spring Security default behavior:
 * - Extracts "scope" claim → SCOPE_ authorities
 * - Creates JwtAuthenticationToken with principal = Jwt object
 * 
 * Custom behavior (this example):
 * - Extracts "roles" claim → ROLE_ authorities
 * - Extracts "groups" claim → GROUP_ authorities
 * - Creates a custom authentication with user profile data
 */
public class JwtAuthConverter {

    // Simulated user profile
    public static class UserProfile {
        public final String userId;
        public final String username;
        public final String email;
        public final Set<String> authorities;

        public UserProfile(String userId, String username, String email,
                          Set<String> authorities) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.authorities = Collections.unmodifiableSet(authorities);
        }

        @Override
        public String toString() {
            return "UserProfile{userId='" + userId + "', username='" + username
                    + "', email='" + email + "', authorities=" + authorities + "}";
        }
    }

    // Configuration for claim mapping
    private final Map<String, String> claimToAuthorityPrefix = new LinkedHashMap<>();
    private final Set<String> requiredClaims = new LinkedHashSet<>();

    public JwtAuthConverter() {
        // Map JWT claims to authority prefixes
        claimToAuthorityPrefix.put("roles", "ROLE_");
        claimToAuthorityPrefix.put("groups", "GROUP_");
        claimToAuthorityPrefix.put("scp", "SCOPE_");

        // Claims that must be present
        requiredClaims.addAll(List.of("sub", "exp"));
    }

    /**
     * Converts a JWT (parsed into claims) to a UserProfile.
     * This simulates JwtAuthenticationConverter.convert().
     * 
     * SECURITY: Always validate claims during conversion:
     * - Reject if required claims are missing
     * - Sanitize authority strings (prevent privilege escalation)
     * - Validate claim formats
     */
    public UserProfile convert(Map<String, Object> jwtClaims) {
        // Validate required claims
        for (String required : requiredClaims) {
            if (!jwtClaims.containsKey(required)) {
                throw new SecurityException(
                        "Missing required claim: " + required);
            }
        }

        // Extract user identity
        String subject = (String) jwtClaims.get("sub");
        String preferredUsername = (String) jwtClaims.getOrDefault(
                "preferred_username", subject);
        String email = (String) jwtClaims.getOrDefault("email", "");

        // Extract authorities from configured claims
        Set<String> authorities = new LinkedHashSet<>();

        for (Map.Entry<String, String> entry : claimToAuthorityPrefix.entrySet()) {
            String claimName = entry.getKey();
            String prefix = entry.getValue();
            Object claimValue = jwtClaims.get(claimName);

            if (claimValue instanceof String) {
                // Single string value (space-separated scopes)
                Arrays.stream(((String) claimValue).split(" "))
                        .filter(s -> !s.isEmpty())
                        .forEach(s -> authorities.add(prefix + s.toUpperCase()));

            } else if (claimValue instanceof List) {
                // Array of values
                @SuppressWarnings("unchecked")
                List<String> values = (List<String>) claimValue;
                values.stream()
                        .filter(Objects::nonNull)
                        .forEach(s -> {
                            // SECURITY: Sanitize authority strings
                            String sanitized = s.trim().toUpperCase()
                                    .replaceAll("[^A-Z0-9_]", "_");
                            authorities.add(prefix + sanitized);
                        });
            }
        }

        return new UserProfile(subject, preferredUsername, email, authorities);
    }

    /**
     * Simulates JWT decoding with a mock token for development.
     * In production, this uses NimbusJwtDecoder.
     */
    public static Map<String, Object> decodeMockToken(String tokenType) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", "user-abc-123");
        claims.put("exp", System.currentTimeMillis() / 1000 + 3600);
        claims.put("iat", System.currentTimeMillis() / 1000);
        claims.put("preferred_username", "alice.johnson");
        claims.put("email", "alice@example.com");

        switch (tokenType) {
            case "admin" -> {
                claims.put("roles", List.of("admin", "user"));
                claims.put("groups", List.of("engineering", "security"));
                claims.put("scp", "read write delete");
            }
            case "user" -> {
                claims.put("roles", List.of("user"));
                claims.put("groups", List.of("engineering"));
                claims.put("scp", "read");
            }
            case "legacy" -> {
                // Legacy token uses space-separated scopes
                claims.put("scp", "openid profile email");
            }
            default -> throw new IllegalArgumentException("Unknown token type");
        }
        return claims;
    }

    public static void main(String[] args) {
        JwtAuthConverter converter = new JwtAuthConverter();

        System.out.println("=== JWT Authentication Converter ===\n");

        // Convert different types of tokens
        for (String tokenType : List.of("admin", "user", "legacy")) {
            System.out.println("Token type: " + tokenType);
            Map<String, Object> claims = decodeMockToken(tokenType);
            System.out.println("  JWT claims: " + claims);

            try {
                UserProfile profile = converter.convert(claims);
                System.out.println("  Profile: " + profile);
            } catch (SecurityException e) {
                System.out.println("  ERROR: " + e.getMessage());
            }
            System.out.println();
        }

        // Test missing required claim
        System.out.println("--- Missing Required Claim ---");
        Map<String, Object> badClaims = new LinkedHashMap<>();
        badClaims.put("scope", "read");
        // Missing "sub" claim
        try {
            converter.convert(badClaims);
        } catch (SecurityException e) {
            System.out.println("  Rejected: " + e.getMessage());
        }
    }
}
