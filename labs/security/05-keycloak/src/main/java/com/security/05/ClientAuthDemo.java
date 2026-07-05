package com.security05;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demonstrates Keycloak client authentication methods.
 * 
 * SECURITY CONCEPT: Client authentication verifies that the
 * application (not just the user) is legitimate. Keycloak supports:
 * 
 * 1. Client Secret (client_secret_basic / client_secret_post)
 *    - Simple, but secret must be stored securely server-side
 *    - NOT suitable for public clients (SPAs, mobile apps)
 * 
 * 2. Signed JWT (private_key_jwt)
 *    - Client signs a JWT with its private key
 *    - More secure — no shared secret transmitted
 *    - Key rotation is easier
 * 
 * 3. Client Secret JWT (client_secret_jwt)
 *    - Client signs a JWT with the shared secret
 *    - More secure than plain secret transmission
 * 
 * 4. Mutual TLS (tls_client_auth)
 *    - Client presents X.509 certificate
 *    - Most secure — requires infrastructure
 * 
 * 5. Public Client (no authentication)
 *    - No secret — relies on PKCE + redirect URI validation
 *    - For SPAs, mobile apps, and CLI tools
 */
public class ClientAuthDemo {

    // Simulates stored client credentials
    private static final Map<String, String> clientSecrets = new ConcurrentHashMap<>();
    private static final Map<String, String> clientAuthMethods = new ConcurrentHashMap<>();

    static {
        clientSecrets.put("confidential-app", "a1b2c3d4-e5f6-7890-abcd-ef1234567890");
        clientAuthMethods.put("confidential-app", "client_secret_basic");

        clientSecrets.put("service-account", "service-account-secret-key-2024");
        clientAuthMethods.put("service-account", "private_key_jwt");
    }

    /**
     * Client Secret Basic: credentials sent in Authorization header.
     * SECURITY: The header is Base64(client_id:client_secret).
     * Requires TLS — without it, the secret is exposed.
     */
    public static boolean authenticateBasic(String clientId, String clientSecret) {
        String expected = clientSecrets.get(clientId);
        if (expected == null) return false;
        boolean match = MessageDigest.isEqual(
                clientSecret.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8));
        System.out.println("  Basic auth [" + clientId + "]: "
                + (match ? "AUTHENTICATED" : "REJECTED"));
        return match;
    }

    /**
     * Client Secret Post: credentials sent in request body.
     * SECURITY: Slightly better than Basic as credentials
     * aren't in headers (less likely to be logged),
     * but still requires TLS.
     */
    public static boolean authenticatePost(String clientId, String clientSecret) {
        return authenticateBasic(clientId, clientSecret);
    }

    /**
     * Public Client: no authentication — relies on PKCE + redirect URI.
     * SECURITY: The redirect URI MUST be pre-registered and validated
     * exactly. Attackers can steal the auth code if redirect URI
     * validation is loose (open redirector vulnerability).
     */
    public static boolean authenticatePublic(String clientId, String redirectUri) {
        // In Keycloak, public clients have no secret
        System.out.println("  Public client [" + clientId + "]: "
                + "no secret — relying on PKCE + redirect URI validation");
        return true;
    }

    /**
     * Simulates signed JWT client authentication (private_key_jwt).
     * SECURITY: The client creates a JWT containing:
     * - iss: client_id
     * - sub: client_id
     * - aud: token endpoint URL
     * - jti: unique identifier (prevent replay)
     * - iat/exp: time window
     * 
     * The JWT is signed with the client's RSA private key.
     * The authorization server verifies with the registered public key.
     */
    public static boolean authenticateSignedJwt(String clientId) {
        // Simulates verification of a client JWT assertion
        System.out.println("  Signed JWT [" + clientId + "]:");
        System.out.println("    iss: " + clientId);
        System.out.println("    aud: https://auth.example.com/realms/academy/protocol/openid-connect/token");
        System.out.println("    jti: " + java.util.UUID.randomUUID());
        System.out.println("    Signature: [verified with registered public key]");
        return true;
    }

    /**
     * Generates a client secret with sufficient entropy.
     */
    public static String generateClientSecret() {
        SecureRandom random = new SecureRandom();
        byte[] secret = new byte[32]; // 256 bits
        random.nextBytes(secret);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(secret);
    }

    public static void main(String[] args) {
        System.out.println("=== Keycloak Client Authentication Methods ===\n");

        // Confidential client with client_secret_basic
        System.out.println("1. Client Secret Basic (confidential-app):");
        authenticateBasic("confidential-app",
                "a1b2c3d4-e5f6-7890-abcd-ef1234567890");
        authenticateBasic("confidential-app", "wrong-secret");

        // Public client (SPA)
        System.out.println("\n2. Public Client (my-spa):");
        authenticatePublic("my-spa", "https://myapp.example.com/callback");

        // Service account with private_key_jwt
        System.out.println("\n3. Service Account (private_key_jwt):");
        authenticateSignedJwt("service-account");

        // Generate a new client secret
        System.out.println("\n4. Generate new client secret:");
        System.out.println("   " + generateClientSecret());
        System.out.println("   (Store securely — cannot be retrieved later)");
    }
}
