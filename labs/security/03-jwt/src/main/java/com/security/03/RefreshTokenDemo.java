package com.security03;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demonstrates JWT refresh token rotation.
 * 
 * SECURITY CONCEPT: Access tokens have short lifetimes (15-60 minutes)
 * to limit damage if leaked. Refresh tokens have longer lifetimes
 * (days/weeks) and are used to obtain new access tokens without
 * re-authentication.
 * 
 * Security best practices:
 * - Refresh tokens are stored server-side (opaque, not JWT)
 * - Access tokens are short-lived (15 min default)
 * - Refresh token rotation: issue a new refresh token with each use
 * - Refresh token reuse detection: if a used refresh token is presented,
 *   revoke all tokens for that user (indicates token theft)
 * - Refresh tokens must be stored securely (HttpOnly, Secure cookies)
 */
public class RefreshTokenDemo {

    static class TokenPair {
        final String accessToken;
        final String refreshToken;
        final Instant accessTokenExpiresAt;
        final Instant refreshTokenExpiresAt;
        final String userId;

        TokenPair(String accessToken, String refreshToken, String userId,
                  long accessTokenTtlSeconds, long refreshTokenTtlSeconds) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.userId = userId;
            this.accessTokenExpiresAt = Instant.now().plusSeconds(accessTokenTtlSeconds);
            this.refreshTokenExpiresAt = Instant.now().plusSeconds(refreshTokenTtlSeconds);
        }
    }

    // Server-side refresh token store
    private static final ConcurrentHashMap<String, TokenPair> tokenStore = new ConcurrentHashMap<>();
    private static final long ACCESS_TOKEN_TTL = 900; // 15 minutes
    private static final long REFRESH_TOKEN_TTL = 604800; // 7 days

    private final KeyPair rsaKeyPair;

    public RefreshTokenDemo() throws Exception {
        this.rsaKeyPair = JwtCreator.generateRsaKeyPair();
    }

    /**
     * Issues an access token (short-lived) and refresh token (long-lived).
     * SECURITY: Access tokens contain claims that can be verified
     * without hitting the database. Refresh tokens are opaque references
     * stored server-side.
     */
    public TokenPair issueTokens(String userId) throws Exception {
        String accessToken = JwtCreator.createRsaJwt(Map.of(
                "sub", userId,
                "type", "access",
                "scope", "read write"
        ), rsaKeyPair.getPrivate());

        String refreshToken = UUID.randomUUID() + ":" + userId;
        TokenPair pair = new TokenPair(accessToken, refreshToken, userId,
                ACCESS_TOKEN_TTL, REFRESH_TOKEN_TTL);
        tokenStore.put(refreshToken, pair);
        return pair;
    }

    /**
     * Refreshes an access token using a refresh token.
     * SECURITY: Implements refresh token rotation:
     * 1. Validate the old refresh token
     * 2. Issue a new access + refresh token pair
     * 3. Invalidate the old refresh token
     * 
     * If a revoked refresh token is reused, it indicates token theft.
     */
    public TokenPair refreshAccessToken(String oldRefreshToken) throws Exception {
        TokenPair oldPair = tokenStore.get(oldRefreshToken);
        if (oldPair == null) {
            throw new SecurityException("Refresh token not found — possible theft");
        }
        if (Instant.now().isAfter(oldPair.refreshTokenExpiresAt)) {
            tokenStore.remove(oldRefreshToken);
            throw new SecurityException("Refresh token expired");
        }

        // Rotate: remove old, issue new
        tokenStore.remove(oldRefreshToken);
        TokenPair newPair = issueTokens(oldPair.userId);
        System.out.println("Refresh token rotated: old revoked, new issued");
        return newPair;
    }

    /**
     * Revokes all tokens for a user (used on logout or breach).
     */
    public void revokeAllTokens(String userId) {
        tokenStore.values().removeIf(p -> p.userId.equals(userId));
        System.out.println("All tokens revoked for user: " + userId);
    }

    /**
     * Checks if an access token is expired.
     */
    public static boolean isAccessTokenExpired(TokenPair pair) {
        return Instant.now().isAfter(pair.accessTokenExpiresAt);
    }

    /**
     * Verifies a JWT access token using the RSA public key.
     */
    public boolean verifyAccessToken(String accessToken) throws Exception {
        return JwtVerifier.verifyRsaJwt(accessToken, rsaKeyPair.getPublic());
    }

    public static void main(String[] args) throws Exception {
        RefreshTokenDemo demo = new RefreshTokenDemo();

        System.out.println("=== JWT Refresh Token Demo ===\n");

        // Issue tokens
        TokenPair pair1 = demo.issueTokens("user123");
        System.out.println("1. Tokens issued:");
        System.out.println("   Access Token:  " + pair1.accessToken.substring(0, 60) + "...");
        System.out.println("   Refresh Token: " + pair1.refreshToken);
        System.out.println("   Access expires: " + pair1.accessTokenExpiresAt);
        System.out.println("   Refresh expires: " + pair1.refreshTokenExpiresAt + "\n");

        // Verify access token
        boolean valid = demo.verifyAccessToken(pair1.accessToken);
        System.out.println("2. Access token verification: " + valid + "\n");

        // Refresh the token
        TokenPair pair2 = demo.refreshAccessToken(pair1.refreshToken);
        System.out.println("3. Refreshed tokens:");
        System.out.println("   New Access Token:  " + pair2.accessToken.substring(0, 60) + "...");
        System.out.println("   New Refresh Token: " + pair2.refreshToken + "\n");

        // Old refresh token should now be invalid
        try {
            demo.refreshAccessToken(pair1.refreshToken);
        } catch (SecurityException e) {
            System.out.println("4. Old refresh token reuse detected: " + e.getMessage() + "\n");
        }

        // Revoke all
        demo.revokeAllTokens("user123");
        System.out.println("5. All tokens revoked for user123");
    }
}
