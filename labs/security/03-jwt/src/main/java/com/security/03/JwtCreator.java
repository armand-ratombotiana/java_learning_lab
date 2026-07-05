package com.security03;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

/**
 * Demonstrates JWT creation and signing with HMAC (HS256) and RSA (RS256).
 * 
 * SECURITY CONCEPT: A JSON Web Token (JWT) consists of three parts:
 *   Header (algorithm & token type)
 *   Payload (claims — data + metadata)
 *   Signature (cryptographic verification)
 * 
 * JWTs must ALWAYS be signed. Never accept unsigned JWTs (alg: "none") in production.
 * Use RS256 (asymmetric) for inter-service communication where
 * multiple services need to verify tokens without sharing a secret.
 * Use HS256 (symmetric) for single-service applications.
 */
public class JwtCreator {

    // SECURITY: In production, load from secure config/vault, not hardcoded
    private static final String HMAC_SECRET = "my-very-long-and-secure-hmac-secret-key-at-least-256-bits";

    /**
     * Creates a JWT signed with HMAC-SHA256 (symmetric).
     * SECURITY: The secret must be at least 256 bits (32 bytes) for HS256.
     * Only the service that issued the token can verify it.
     */
    public static String createHmacJwt(Map<String, Object> claims) throws Exception {
        // Header
        String header = jsonBase64(Map.of("alg", "HS256", "typ", "JWT"));
        // Payload with standard claims
        Map<String, Object> payload = new TreeMap<>(claims);
        payload.putIfAbsent("iat", Instant.now().getEpochSecond());
        payload.putIfAbsent("exp", Instant.now().plusSeconds(3600).getEpochSecond());
        String payloadEncoded = jsonBase64(payload);

        // Signature
        String message = header + "." + payloadEncoded;
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(
                HMAC_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(hmac.doFinal(message.getBytes(StandardCharsets.UTF_8)));

        return header + "." + payloadEncoded + "." + signature;
    }

    /**
     * Creates a JWT signed with RSA-SHA256 (asymmetric).
     * SECURITY: The private key stays with the issuer (never shared).
     * Public key is distributed to verifiers. This is the recommended
     * approach for microservices architectures.
     */
    public static String createRsaJwt(Map<String, Object> claims, PrivateKey privateKey)
            throws Exception {
        String header = jsonBase64(Map.of("alg", "RS256", "typ", "JWT"));
        Map<String, Object> payload = new TreeMap<>(claims);
        payload.putIfAbsent("iat", Instant.now().getEpochSecond());
        payload.putIfAbsent("exp", Instant.now().plusSeconds(3600).getEpochSecond());
        String payloadEncoded = jsonBase64(payload);

        String message = header + "." + payloadEncoded;
        Signature rsaSign = Signature.getInstance("SHA256withRSA");
        rsaSign.initSign(privateKey);
        rsaSign.update(message.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(rsaSign.sign());

        return header + "." + payloadEncoded + "." + signature;
    }

    /**
     * Generates an RSA key pair for JWT signing (2048-bit recommended).
     */
    public static KeyPair generateRsaKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        return generator.generateKeyPair();
    }

    /**
     * Helper: JSON → Base64URL encoding.
     * This is simplified; use a proper JSON library (Jackson, Gson) in production.
     */
    private static String jsonBase64(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) json.append(",");
            first = false;
            json.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else {
                json.append(entry.getValue());
            }
        }
        json.append("}");
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(json.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) throws Exception {
        var claims = Map.of(
            "sub", "user123",
            "name", "Alice Johnson",
            "roles", "admin,user"
        );

        System.out.println("=== JWT Creation ===\n");

        // HMAC-signed JWT
        String hmacJwt = createHmacJwt(claims);
        System.out.println("HMAC (HS256) JWT:");
        System.out.println(hmacJwt + "\n");

        // RSA-signed JWT
        KeyPair rsaKeyPair = generateRsaKeyPair();
        String rsaJwt = createRsaJwt(claims, rsaKeyPair.getPrivate());
        System.out.println("RSA (RS256) JWT:");
        System.out.println(rsaJwt + "\n");

        // Show JWT parts
        String[] hmacParts = hmacJwt.split("\\.");
        System.out.println("JWT Structure (3 parts):");
        System.out.println("  Header:    " + hmacParts[0]);
        System.out.println("  Payload:   " + hmacParts[1]);
        System.out.println("  Signature: " + hmacParts[2]);
    }
}
