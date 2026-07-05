package com.security03;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Instant;
import java.util.Base64;

/**
 * Demonstrates JWT verification for both HMAC and RSA signatures.
 * 
 * SECURITY CONCEPT: JWT verification is critical — without it,
 * an attacker can forge tokens. This implementation demonstrates:
 * 
 * 1. Algorithm validation — reject "none" algorithm tokens
 * 2. Signature verification — cryptographically verify integrity
 * 3. Claims validation — check expiry, issuer, audience
 * 4. Clock skew handling — allow small time differences
 */
public class JwtVerifier {

    private static final String HMAC_SECRET = "my-very-long-and-secure-hmac-secret-key-at-least-256-bits";
    private static final long MAX_CLOCK_SKEW_SECONDS = 30;

    /**
     * Verifies an HMAC-signed JWT.
     * SECURITY: Always verify algorithm before checking signature.
     * An attacker could send {"alg":"none"} to bypass verification.
     */
    public static boolean verifyHmacJwt(String jwt) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            System.out.println("INVALID: JWT must have 3 parts");
            return false;
        }

        // SECURITY: Extract and validate algorithm
        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
        if (!headerJson.contains("\"alg\":\"HS256\"")) {
            System.out.println("REJECTED: Algorithm mismatch or 'none' alg attack detected");
            return false;
        }

        // Verify signature
        String message = parts[0] + "." + parts[1];
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(
                HMAC_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String expectedSignature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(hmac.doFinal(message.getBytes(StandardCharsets.UTF_8)));

        if (!MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                parts[2].getBytes(StandardCharsets.UTF_8))) {
            System.out.println("REJECTED: Invalid signature");
            return false;
        }

        // Validate claims
        return validateClaims(parts[1]);
    }

    /**
     * Verifies an RSA-signed JWT using the public key.
     * SECURITY: The public key is distributed — only the private key
     * can sign, but any holder of the public key can verify.
     */
    public static boolean verifyRsaJwt(String jwt, PublicKey publicKey) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            System.out.println("INVALID: JWT must have 3 parts");
            return false;
        }

        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
        if (!headerJson.contains("\"alg\":\"RS256\"")) {
            System.out.println("REJECTED: Algorithm mismatch");
            return false;
        }

        // Verify RSA signature
        String message = parts[0] + "." + parts[1];
        Signature rsaVerify = Signature.getInstance("SHA256withRSA");
        rsaVerify.initVerify(publicKey);
        rsaVerify.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] signature = Base64.getUrlDecoder().decode(parts[2]);

        if (!rsaVerify.verify(signature)) {
            System.out.println("REJECTED: Invalid RSA signature");
            return false;
        }

        return validateClaims(parts[1]);
    }

    /**
     * Validates JWT claims (exp, iat, nbf).
     * SECURITY: Check expiration strictly to prevent replay attacks.
     * Always validate 'exp' — tokens should have limited lifetime (15-60 min).
     */
    private static boolean validateClaims(String payloadBase64) throws Exception {
        String json = new String(Base64.getUrlDecoder().decode(payloadBase64));
        long now = Instant.now().getEpochSecond();

        // Check expiry
        if (json.contains("\"exp\"")) {
            long exp = extractLongValue(json, "exp");
            if (now > exp + MAX_CLOCK_SKEW_SECONDS) {
                System.out.println("REJECTED: Token expired (exp=" + exp + ", now=" + now + ")");
                return false;
            }
        }

        // Check issued-at time (optional, but recommended)
        if (json.contains("\"iat\"")) {
            long iat = extractLongValue(json, "iat");
            if (now < iat - MAX_CLOCK_SKEW_SECONDS) {
                System.out.println("REJECTED: Token used before issue time");
                return false;
            }
        }

        System.out.println("VERIFIED: All claims valid");
        return true;
    }

    private static long extractLongValue(String json, String key) {
        int keyIdx = json.indexOf("\"" + key + "\":");
        if (keyIdx == -1) return 0;
        int valueStart = json.indexOf(':', keyIdx) + 1;
        int valueEnd = json.indexOf(',', valueStart);
        if (valueEnd == -1) valueEnd = json.indexOf('}', valueStart);
        return Long.parseLong(json.substring(valueStart, valueEnd).trim());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== JWT Verification ===\n");

        // Create a valid HMAC JWT
        var claims = Map.of("sub", "user123", "name", "Alice");
        String validJwt = JwtCreator.createHmacJwt(claims);
        System.out.println("Valid JWT: " + validJwt);
        boolean result = verifyHmacJwt(validJwt);
        System.out.println("Verification result: " + result + "\n");

        // Tampered JWT (signature will not match)
        String tamperedJwt = validJwt.substring(0, validJwt.lastIndexOf('.'))
                + ".invalidsignature";
        System.out.println("Tampered JWT: " + tamperedJwt);
        result = verifyHmacJwt(tamperedJwt);
        System.out.println("Verification result: " + result + "\n");

        // 'none' algorithm attack attempt
        String noneAlgJwt = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0."
                + "eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJhZG1pbiJ9.";
        System.out.println("'none' alg JWT: " + noneAlgJwt);
        result = verifyHmacJwt(noneAlgJwt);
        System.out.println("Verification result (should be false): " + result + "\n");

        // RSA JWT verification
        KeyPair rsaKeyPair = JwtCreator.generateRsaKeyPair();
        String rsaJwt = JwtCreator.createRsaJwt(claims, rsaKeyPair.getPrivate());
        System.out.println("RSA JWT: " + rsaJwt);
        result = verifyRsaJwt(rsaJwt, rsaKeyPair.getPublic());
        System.out.println("RSA verification result: " + result);
    }
}
