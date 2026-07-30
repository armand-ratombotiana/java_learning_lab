package com.security.deep.lab02;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

public class OAuth2OIDC {

    public static String generateCodeVerifier() {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(code);
    }

    public static String generateCodeChallenge(String codeVerifier) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }

    public static String createJwtHs256(Map<String, Object> payload, String secret) throws Exception {
        String header = Base64.getUrlEncoder().withoutPadding()
            .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        String payloadJson = mapToJson(payload);
        String payloadEncoded = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(payloadJson.getBytes());
        String signingInput = header + "." + payloadEncoded;
        String signature = hmacSha256(signingInput, secret);
        return signingInput + "." + signature;
    }

    public static Map<String, Object> verifyJwtHs256(String jwt, String secret) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) throw new IllegalArgumentException("Invalid JWT format");
        String signingInput = parts[0] + "." + parts[1];
        String expectedSig = hmacSha256(signingInput, secret);
        if (!expectedSig.equals(parts[2])) throw new SecurityException("Invalid JWT signature");
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        return parseJsonToMap(payloadJson);
    }

    public static boolean validateJwtClaims(Map<String, Object> claims, String expectedIssuer, String expectedAudience) {
        long now = System.currentTimeMillis() / 1000;
        if (claims.containsKey("exp") && ((Number) claims.get("exp")).longValue() < now) return false;
        if (claims.containsKey("nbf") && ((Number) claims.get("nbf")).longValue() > now) return false;
        if (expectedIssuer != null && !expectedIssuer.equals(claims.get("iss"))) return false;
        if (expectedAudience != null && !claims.get("aud").equals(expectedAudience)) return false;
        return true;
    }

    public static Map<String, Object> simulateAuthCodeFlow(String clientId, String redirectUri,
                                                            String codeChallenge, boolean valid) {
        String authCode = UUID.randomUUID().toString();
        Map<String, Object> tokenResponse = new LinkedHashMap<>();
        if (!valid) {
            tokenResponse.put("error", "access_denied");
            return tokenResponse;
        }
        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        long expiresIn = 3600;
        tokenResponse.put("access_token", accessToken);
        tokenResponse.put("token_type", "Bearer");
        tokenResponse.put("expires_in", expiresIn);
        tokenResponse.put("refresh_token", refreshToken);
        tokenResponse.put("scope", "openid profile email");
        return tokenResponse;
    }

    public static Map<String, Object> createIdToken(String subject, String issuer,
                                                     String audience, long expirationSec) throws Exception {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", subject);
        payload.put("iss", issuer);
        payload.put("aud", audience);
        payload.put("exp", System.currentTimeMillis() / 1000 + expirationSec);
        payload.put("iat", System.currentTimeMillis() / 1000);
        payload.put("name", "John Doe");
        payload.put("email", "john@example.com");
        payload.put("email_verified", true);
        String token = createJwtHs256(payload, "oidc-secret-key-256bit!");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id_token", token);
        result.put("claims", payload);
        return result;
    }

    private static String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getUrlEncoder().withoutPadding()
            .encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    private static String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (var entry : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(entry.getKey()).append("\":");
            Object v = entry.getValue();
            if (v instanceof String) sb.append("\"").append(v).append("\"");
            else if (v instanceof Boolean) sb.append(v);
            else if (v instanceof Number) sb.append(v);
            else sb.append("\"").append(v).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    private static Map<String, Object> parseJsonToMap(String json) {
        Map<String, Object> map = new LinkedHashMap<>();
        json = json.replaceAll("[{}]", "").trim();
        if (json.isEmpty()) return map;
        for (String pair : json.split(",")) {
            String[] kv = pair.split(":", 2);
            String key = kv[0].trim().replaceAll("^\"|\"$", "");
            String val = kv[1].trim().replaceAll("^\"|\"$", "");
            if (val.equals("true") || val.equals("false")) map.put(key, Boolean.parseBoolean(val));
            else if (val.matches("\\d+")) map.put(key, Long.parseLong(val));
            else map.put(key, val);
        }
        return map;
    }
}
