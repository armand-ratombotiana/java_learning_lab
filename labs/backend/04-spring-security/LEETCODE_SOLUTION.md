# Implement JWT Token with Refresh Token Rotation

## Problem Statement
Implement a JWT-based authentication system with:
- Access token (short-lived, 15 min) and refresh token (long-lived, 7 days)
- Refresh token rotation: each refresh returns a new refresh token + invalidates the old one
- Reuse detection: if a compromised refresh token is reused, revoke all tokens for that user
- Token signing with HMAC-SHA256
- Token revocation (blacklist)
- Clock skew tolerance

## Solution

```java
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.*;
import java.security.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.Base64;
import java.util.concurrent.atomic.*;

/**
 * JWT token service with refresh token rotation and reuse detection.
 * <p>
 * Time complexity:
 * - createAccessToken: O(1)
 * - createRefreshToken: O(1)
 * - verify: O(1)
 * - refresh: O(1) average
 */
public class JwtTokenService {

    private static final String HMAC_ALGO = "HmacSHA256";
    private static final String ISSUER = "backend-academy";
    private static final Base64.Encoder B64ENC = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder B64DEC = Base64.getUrlDecoder();

    private final SecretKeySpec signingKey;
    private final long accessTokenTtlMs;
    private final long refreshTokenTtlMs;
    private final long clockSkewMs;

    // token blacklist (revoked tokens)
    private final ConcurrentHashMap<String, Long> blacklist;
    // active refresh tokens: refreshTokenId -> token metadata
    private final ConcurrentHashMap<String, RefreshTokenInfo> activeRefreshTokens;
    // user -> list of refresh token family IDs (for reuse detection)
    private final ConcurrentHashMap<String, String> userTokenFamily;
    // reuse detection: seen refresh token hashes
    private final ConcurrentHashMap<String, Boolean> reusedTokens;

    public JwtTokenService(String secret, long accessTokenTtlMs,
                           long refreshTokenTtlMs, long clockSkewMs) {
        this.signingKey = new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGO);
        this.accessTokenTtlMs = accessTokenTtlMs;
        this.refreshTokenTtlMs = refreshTokenTtlMs;
        this.clockSkewMs = clockSkewMs;
        this.blacklist = new ConcurrentHashMap<>();
        this.activeRefreshTokens = new ConcurrentHashMap<>();
        this.userTokenFamily = new ConcurrentHashMap<>();
        this.reusedTokens = new ConcurrentHashMap<>();
    }

    // ── Token creation ──────────────────────────────────────────────────────

    public TokenPair createTokens(String userId, String... roles) {
        long now = System.currentTimeMillis();
        String accessToken = createAccessToken(userId, now, roles);
        String refreshToken = createRefreshToken(userId, now);
        return new TokenPair(accessToken, refreshToken);
    }

    private String createAccessToken(String userId, long now, String... roles) {
        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", userId);
        payload.put("iss", ISSUER);
        payload.put("iat", now / 1000);
        payload.put("exp", (now + accessTokenTtlMs) / 1000);
        payload.put("roles", List.of(roles));
        payload.put("type", "access");
        return encodeJwt(header, payload);
    }

    private String createRefreshToken(String userId, long now) {
        String tokenId = UUID.randomUUID().toString();
        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", userId);
        payload.put("iss", ISSUER);
        payload.put("iat", now / 1000);
        payload.put("exp", (now + refreshTokenTtlMs) / 1000);
        payload.put("jti", tokenId);
        payload.put("type", "refresh");

        String token = encodeJwt(header, payload);
        String familyId = userTokenFamily.getOrDefault(userId, UUID.randomUUID().toString());
        userTokenFamily.put(userId, familyId);

        activeRefreshTokens.put(tokenId, new RefreshTokenInfo(
            userId, familyId, now + refreshTokenTtlMs, false));
        return token;
    }

    // ── Token verification ──────────────────────────────────────────────────

    public VerificationResult verifyAccessToken(String token) {
        try {
            JwtParts parts = decodeJwt(token);
            Map<String, Object> payload = parts.payload();

            if (!"access".equals(payload.get("type"))) {
                return VerificationResult.invalid("Not an access token");
            }
            if (isBlacklisted(token)) {
                return VerificationResult.invalid("Token is revoked");
            }
            if (isExpired(payload)) {
                return VerificationResult.invalid("Token expired");
            }
            if (!verifySignature(parts)) {
                return VerificationResult.invalid("Invalid signature");
            }
            return VerificationResult.valid(
                (String) payload.get("sub"),
                castRoles(payload.get("roles")));
        } catch (Exception e) {
            return VerificationResult.invalid("Malformed token: " + e.getMessage());
        }
    }

    // ── Token refresh with rotation ─────────────────────────────────────────

    public RefreshResult refreshAccessToken(String refreshToken) {
        try {
            JwtParts parts = decodeJwt(refreshToken);
            Map<String, Object> payload = parts.payload();

            if (!"refresh".equals(payload.get("type"))) {
                return RefreshResult.failure("Not a refresh token");
            }
            if (isBlacklisted(refreshToken)) {
                return RefreshResult.failure("Token is revoked");
            }
            if (isExpired(payload)) {
                return RefreshResult.failure("Refresh token expired");
            }
            if (!verifySignature(parts)) {
                return RefreshResult.failure("Invalid signature");
            }

            String jti = (String) payload.get("jti");
            RefreshTokenInfo stored = activeRefreshTokens.get(jti);

            if (stored == null) {
                // Token not in active store — possible reuse or already rotated
                return RefreshResult.failure("Refresh token not found or already used");
            }

            // Check if this token has already been used for rotation
            String tokenHash = hashToken(refreshToken);
            if (stored.reused || reusedTokens.containsKey(tokenHash)) {
                // Reuse detected! Revoke all tokens for this user
                revokeUserTokens(stored.userId);
                return RefreshResult.failure("Reuse detected — all tokens revoked");
            }

            // Mark as used (rotation)
            activeRefreshTokens.remove(jti);
            reusedTokens.put(tokenHash, Boolean.TRUE);

            // Issue new token pair
            String userId = (String) payload.get("sub");
            TokenPair newTokens = createTokens(userId, "user");

            return RefreshResult.success(newTokens);
        } catch (Exception e) {
            return RefreshResult.failure("Malformed token: " + e.getMessage());
        }
    }

    public void revokeAccessToken(String token) {
        blacklist.put(hashToken(token), System.currentTimeMillis());
    }

    public void revokeUserTokens(String userId) {
        // Remove all refresh tokens for this user
        activeRefreshTokens.entrySet()
            .removeIf(e -> e.getValue().userId.equals(userId));
        userTokenFamily.remove(userId);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(hashToken(token));
    }

    public long getActiveRefreshTokenCount() {
        return activeRefreshTokens.size();
    }

    public void cleanupExpired() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(e -> e.getValue() < now - refreshTokenTtlMs);
        activeRefreshTokens.entrySet().removeIf(e -> e.getValue().expiryMs < now);
    }

    // ── JWT encoding / decoding ─────────────────────────────────────────────

    private String encodeJwt(Map<String, Object> header, Map<String, Object> payload) {
        String headerB64 = B64ENC.encodeToString(toJson(header).getBytes(StandardCharsets.UTF_8));
        String payloadB64 = B64ENC.encodeToString(toJson(payload).getBytes(StandardCharsets.UTF_8));
        String signature = sign(headerB64 + "." + payloadB64);
        return headerB64 + "." + payloadB64 + "." + signature;
    }

    private JwtParts decodeJwt(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) throw new IllegalArgumentException("Invalid JWT format");
        Map<String, Object> header = parseJson(new String(B64DEC.decode(parts[0]), StandardCharsets.UTF_8));
        Map<String, Object> payload = parseJson(new String(B64DEC.decode(parts[1]), StandardCharsets.UTF_8));
        return new JwtParts(parts[0], parts[1], parts[2], header, payload);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(signingKey);
            return B64ENC.encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Signing failed", e);
        }
    }

    private boolean verifySignature(JwtParts parts) {
        String expectedSig = sign(parts.headerB64() + "." + parts.payloadB64());
        return MessageDigest.isEqual(
            expectedSig.getBytes(StandardCharsets.UTF_8),
            parts.signature().getBytes(StandardCharsets.UTF_8));
    }

    private boolean isExpired(Map<String, Object> payload) {
        long exp = ((Number) payload.get("exp")).longValue() * 1000;
        return System.currentTimeMillis() > (exp + clockSkewMs);
    }

    private String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return B64ENC.encodeToString(md.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String[] castRoles(Object roles) {
        if (roles instanceof List<?> list) {
            return list.stream().map(Object::toString).toArray(String[]::new);
        }
        return new String[0];
    }

    // Minimal JSON (avoiding dependency) — in production use Jackson/Gson
    private String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        var it = map.entrySet().iterator();
        while (it.hasNext()) {
            var e = it.next();
            sb.append('"').append(escapeJson(e.getKey())).append('"').append(':');
            Object v = e.getValue();
            if (v instanceof String s) sb.append('"').append(escapeJson(s)).append('"');
            else if (v instanceof List<?> list) {
                sb.append('[');
                for (var li = list.iterator(); li.hasNext();) {
                    sb.append('"').append(escapeJson(li.next().toString())).append('"');
                    if (li.hasNext()) sb.append(',');
                }
                sb.append(']');
            }
            else sb.append(v);
            if (it.hasNext()) sb.append(',');
        }
        sb.append('}');
        return sb.toString();
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private Map<String, Object> parseJson(String json) {
        // Minimal — in production use Jackson
        Map<String, Object> map = new LinkedHashMap<>();
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) return map;
        json = json.substring(1, json.length() - 1);
        int i = 0;
        while (i < json.length()) {
            // find key
            int keyStart = json.indexOf('"', i);
            if (keyStart < 0) break;
            int keyEnd = json.indexOf('"', keyStart + 1);
            String key = json.substring(keyStart + 1, keyEnd);
            int colon = json.indexOf(':', keyEnd + 1);
            i = colon + 1;
            while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
            if (i >= json.length()) break;
            if (json.charAt(i) == '"') {
                int valEnd = json.indexOf('"', i + 1);
                map.put(key, json.substring(i + 1, valEnd));
                i = valEnd + 1;
            } else if (json.charAt(i) == '[') {
                int arrEnd = json.indexOf(']', i);
                String arrContent = json.substring(i + 1, arrEnd);
                List<String> list = new ArrayList<>();
                for (var s : arrContent.split(",")) {
                    s = s.trim();
                    if (s.startsWith("\"") && s.endsWith("\"")) s = s.substring(1, s.length() - 1);
                    list.add(s);
                }
                map.put(key, list);
                i = arrEnd + 1;
            } else {
                int comma = json.indexOf(',', i);
                if (comma < 0) comma = json.length();
                map.put(key, json.substring(i, comma).trim());
                i = comma;
            }
            if (i < json.length() && json.charAt(i) == ',') i++;
        }
        return map;
    }

    // ── Record types ────────────────────────────────────────────────────────

    public record TokenPair(String accessToken, String refreshToken) {}
    public record VerificationResult(boolean valid, String userId, String[] roles, String error) {
        public static VerificationResult valid(String userId, String[] roles) {
            return new VerificationResult(true, userId, roles, null);
        }
        public static VerificationResult invalid(String error) {
            return new VerificationResult(false, null, new String[0], error);
        }
    }
    public record RefreshResult(boolean success, TokenPair tokens, String error) {
        public static RefreshResult success(TokenPair tokens) {
            return new RefreshResult(true, tokens, null);
        }
        public static RefreshResult failure(String error) {
            return new RefreshResult(false, null, error);
        }
    }
    private record JwtParts(String headerB64, String payloadB64, String signature,
                            Map<String, Object> header, Map<String, Object> payload) {}
    private record RefreshTokenInfo(String userId, String familyId, long expiryMs, boolean reused) {}
}
```

## Complexity Analysis

| Operation            | Time Complexity | Space Complexity |
|----------------------|----------------|-----------------|
| createTokens         | O(1)           | O(1)            |
| verifyAccessToken    | O(1)           | O(1)            |
| refreshAccessToken   | O(1)           | O(1)            |
| revokeAccessToken    | O(1)           | O(1)            |
| revokeUserTokens     | O(k)           | O(1)            |

Overall storage: O(n) for n active refresh tokens + O(m) for m blacklisted token hashes.

## Test Cases

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTest {

    private JwtTokenService service;
    private static final String SECRET = "my-super-secret-key-2024!";

    @BeforeEach
    void setUp() {
        service = new JwtTokenService(SECRET, 900_000, 7 * 24 * 3600_000L, 5000);
    }

    @Test
    void testCreateAndVerifyTokens() {
        var pair = service.createTokens("user1", "admin", "user");
        assertNotNull(pair.accessToken());
        assertNotNull(pair.refreshToken());

        var result = service.verifyAccessToken(pair.accessToken());
        assertTrue(result.valid());
        assertEquals("user1", result.userId());
    }

    @Test
    void testRefreshTokenRotation() {
        var pair = service.createTokens("user2");
        var refreshResult = service.refreshAccessToken(pair.refreshToken());
        assertTrue(refreshResult.success());

        // Old refresh token should now be invalid
        var secondResult = service.refreshAccessToken(pair.refreshToken());
        assertFalse(secondResult.success());
    }

    @Test
    void testReuseDetection() {
        var pair = service.createTokens("user3");
        // First refresh succeeds
        service.refreshAccessToken(pair.refreshToken());
        // Second refresh with same token should detect reuse and revoke all
        var result = service.refreshAccessToken(pair.refreshToken());
        assertFalse(result.success());
        assertEquals("Reuse detected", result.error().substring(0, 14));
    }

    @Test
    void testAccessTokenRevocation() {
        var pair = service.createTokens("user4");
        service.revokeAccessToken(pair.accessToken());
        var result = service.verifyAccessToken(pair.accessToken());
        assertFalse(result.valid());
    }

    @Test
    void testExpiredAccessToken() throws Exception {
        var shortLived = new JwtTokenService(SECRET, 1, 7 * 24 * 3600_000L, 0);
        var pair = shortLived.createTokens("user5");
        Thread.sleep(10);
        var result = shortLived.verifyAccessToken(pair.accessToken());
        assertFalse(result.valid());
    }

    @Test
    void testInvalidSignature() {
        var service2 = new JwtTokenService("different-secret", 900_000, 7 * 24 * 3600_000L, 5000);
        var pair = service2.createTokens("user6");
        var result = service.verifyAccessToken(pair.accessToken());
        assertFalse(result.valid());
    }

    @Test
    void testUserTokenRevocation() {
        var pair = service.createTokens("user7");
        service.revokeUserTokens("user7");
        var result = service.refreshAccessToken(pair.refreshToken());
        assertFalse(result.success());
    }

    @Test
    void testMalformedToken() {
        var result = service.verifyAccessToken("invalid.token.here");
        assertFalse(result.valid());
    }

    @Test
    void testMultipleConsecutiveRefreshes() {
        var pair = service.createTokens("user8");
        for (int i = 0; i < 5; i++) {
            var result = service.refreshAccessToken(pair.refreshToken());
            assertTrue(result.success());
            pair = result.tokens();
        }
        assertEquals(1, service.getActiveRefreshTokenCount());
    }
}
```
