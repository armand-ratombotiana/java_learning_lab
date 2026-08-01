# Problem Walkthrough: OAuth2 Authorization Code Flow with Token Validation

## Problem Statement

Implement the server side of the **OAuth2 authorization code flow**:

1. **Client registry**: registered clients with id, redirect URIs, and client secrets.
2. **Authorization endpoint**: issues high-entropy, short-lived, **single-use** authorization codes bound to (client_id, redirect_uri, user, scope).
3. **Token endpoint**: authenticates the client, validates and **consumes** the code, and mints a **self-contained HMAC-signed access token** (JWT-shaped, hand-rolled claims — no JSON dependency).
4. **Token validator**: parse → constant-time signature check → expiry → issuer → audience → scope, with typed outcomes.
5. **Full-flow demo**: happy path plus replay, expiry, tampering, wrong-client, wrong-secret, wrong-audience, and scope-denial cases.

**Deliverable**: `com.security.deep.lab02.OAuth2CodeFlow` — complete Java 21+ class with `Token`, `ValidationResult`, `CodeEntry`, mint/validate/exchange machinery, and the `main` verification driver.

---

## Constraints & Requirements

| Item | Requirement |
|------|-------------|
| Language | Java 21+ (javax.crypto HMAC-SHA256, Base64; no external libs) |
| Token format | header.payload.signature (base64url), claims: iss, sub, aud, iat, exp, scope, jti |
| Signature | HMAC-SHA256 with a shared secret; constant-time comparison |
| Codes | Random 256-bit, single-use (consume-before-validate), TTL, bound to client + redirect_uri |
| Validation order | parse → signature → exp → iss → aud → scope |
| Errors | Typed `ValidationResult` (Valid / Invalid(reason)), not exceptions |

---

## Step 1: Protocol Foundation

### 1.1 The flow (RFC 6749 §4.1)

```
Browser               Authorization Server            Client (confidential)
   | 1. GET /authorize?client_id&redirect_uri&scope&state   |
   |-------------------------------------------------------->|
   | 2. user authenticates & consents                        |
   | 3. 302 Location: redirect_uri?code=<C>&state=<S>        |
   |<--------------------------------------------------------|
   |                                                         |
   | 4. client: POST /token {code, client_id, client_secret, redirect_uri}
   |-------------------------------------------------------->|
   |                                                         | 5. validate & consume code
   |                                                         |    mint access token
   | 6. 200 {access_token, token_type, expires_in}           |
   |<--------------------------------------------------------|
```

### 1.2 Why each hop is secured the way it is

| Mechanism | Threat mitigated |
|-----------|------------------|
| Code, not token, in the browser redirect | Token leakage via Referer/history/extensions |
| Code bound to client_id + redirect_uri | Code interception/reuse by a different client |
| Single-use, short TTL | Code replay after interception |
| state parameter echoed back | Login CSRF (session injection) |
| Client secret at the token endpoint | Impersonation of a confidential client |
| Constant-time signature compare | Timing side channel on the HMAC |
| aud claim | Token confusion between services sharing a secret |
| Scope check on resource access | Privilege escalation via scope confusion |

### 1.3 The self-contained token

A JWT-shaped token `header.payload.signature` with:

- header: `{"alg":"HS256","typ":"JWT"}` (base64url).
- payload: serialized claims (key=value&...), signed content.
- signature: HMAC-SHA256(key = shared secret, message = header + "." + payload).

Because the token is self-contained, the resource server validates offline — no token-endpoint round trip per request — which is the performance model of modern microservice auth.

---

## Step 2: Design

### 2.1 Types

```java
public record Token(String value, Map<String, String> claims) {}

public sealed interface ValidationResult permits Valid, Invalid {
    record Valid(Map<String, String> claims) implements ValidationResult {}
    record Invalid(String reason) implements ValidationResult {}
}

public record CodeEntry(String clientId, String redirectUri, String userId,
                        String scope, long expiresAt) {}

public record TokenResponse(String accessToken, String error) {}
```

### 2.2 Code lifecycle — the critical invariant

`exchangeCode` performs `codes.remove(code)` **first**. Single-use means the first redemption attempt — successful or not — invalidates the code. This prevents:

- Replay by a racing attacker;
- Probing (an attacker learns nothing about whether a code is live, because a failed attempt still consumes it).

### 2.3 Claims serialization

Claims are `Map<String,String>` (LinkedHashMap preserves order). Serialized as `k1=v1&k2=v2...` — URL-encoding is unnecessary for our fixed claim set (iss/sub/aud are tokens without '&' or '='; the scope string uses spaces which are preserved). Parsing splits on '&' then first '='.

### 2.4 Validation order

1. Three dot-separated base64url parts.
2. Signature over `header + "." + payload` — `MessageDigest.isEqual` (constant time).
3. exp > now (+ 30 s clock-skew allowance).
4. iss == expectedIssuer.
5. aud == expectedAudience.
6. scope containment — checked by the resource layer via `hasScope(claims, required)`.

---

## Step 3: Complete Solution (Java 21+)

```java
package com.security.deep.lab02;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class OAuth2CodeFlow {

    public static final String ISSUER = "https://auth.example.com";
    public static final String AUDIENCE = "https://api.example.com";

    public record Token(String value, Map<String, String> claims) {}

    public sealed interface ValidationResult permits Valid, Invalid {
        record Valid(Map<String, String> claims) implements ValidationResult {}
        record Invalid(String reason) implements ValidationResult {}
    }

    public record CodeEntry(String clientId, String redirectUri, String userId,
                            String scope, long expiresAt) {}

    public record TokenResponse(String accessToken, String error) {}

    private static final SecureRandom RNG = new SecureRandom();
    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder B64D = Base64.getUrlDecoder();

    private final Map<String, CodeEntry> codes = new ConcurrentHashMap<>();
    private final Map<String, String> clientSecrets = new ConcurrentHashMap<>();
    private final Map<String, String> clientRedirects = new ConcurrentHashMap<>();
    private final byte[] hmacKey;
    private final long codeTtlSeconds;

    public OAuth2CodeFlow(byte[] hmacKey, long codeTtlSeconds) {
        this.hmacKey = hmacKey.clone();
        this.codeTtlSeconds = codeTtlSeconds;
    }

    public void registerClient(String clientId, String clientSecret, String redirectUri) {
        clientSecrets.put(clientId, clientSecret);
        clientRedirects.put(clientId, redirectUri);
    }

    private static String randomId(int bytes) {
        byte[] b = new byte[bytes];
        RNG.nextBytes(b);
        return B64.encodeToString(b);
    }

    public String issueCode(String clientId, String redirectUri, String userId, String scope) {
        if (!clientRedirects.containsKey(clientId)) {
            throw new IllegalArgumentException("unknown client " + clientId);
        }
        if (!clientRedirects.get(clientId).equals(redirectUri)) {
            throw new IllegalArgumentException("redirect_uri not registered for client");
        }
        String code = randomId(32);
        codes.put(code, new CodeEntry(clientId, redirectUri, userId, scope,
                                      nowSeconds() + codeTtlSeconds));
        return code;
    }

    private static byte[] hmac(byte[] key, String message) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
    }

    private static String serialize(Map<String, String> claims) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : claims.entrySet()) {
            if (sb.length() > 0) sb.append('&');
            sb.append(e.getKey()).append('=').append(e.getValue());
        }
        return sb.toString();
    }

    private static Map<String, String> parse(String payload) {
        Map<String, String> claims = new HashMap<>();
        String[] pairs = payload.split("&");
        for (String pair : pairs) {
            int eq = pair.indexOf('=');
            if (eq > 0) claims.put(pair.substring(0, eq), pair.substring(eq + 1));
        }
        return claims;
    }

    public Token mintToken(String issuer, String subject, String audience,
                           String scope, long ttlSeconds, long issuedAt) {
        Map<String, String> claims = new LinkedHashMap<>();
        claims.put("iss", issuer);
        claims.put("sub", subject);
        claims.put("aud", audience);
        claims.put("iat", Long.toString(issuedAt));
        claims.put("exp", Long.toString(issuedAt + ttlSeconds));
        claims.put("scope", scope);
        claims.put("jti", randomId(16));
        String header = B64.encodeToString(
            "{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = B64.encodeToString(serialize(claims).getBytes(StandardCharsets.UTF_8));
        try {
            String sig = B64.encodeToString(hmac(hmacKey, header + "." + payload));
            return new Token(header + "." + payload + "." + sig, claims);
        } catch (Exception e) {
            throw new IllegalStateException("HMAC failure", e);
        }
    }

    public ValidationResult validate(String tokenValue, String expectedIssuer,
                                     String expectedAudience, long nowSeconds) {
        String[] parts = tokenValue.split("\\.", -1);
        if (parts.length != 3) return new ValidationResult.Invalid("malformed token");
        try {
            byte[] expectedSig = hmac(hmacKey, parts[0] + "." + parts[1]);
            if (!MessageDigest.isEqual(expectedSig, B64D.decode(parts[2]))) {
                return new ValidationResult.Invalid("signature mismatch");
            }
            Map<String, String> claims = parse(
                new String(B64D.decode(parts[1]), StandardCharsets.UTF_8));
            long exp = Long.parseLong(claims.getOrDefault("exp", "0"));
            if (exp < nowSeconds - 30) return new ValidationResult.Invalid("token expired");
            if (!expectedIssuer.equals(claims.get("iss"))) {
                return new ValidationResult.Invalid("issuer mismatch");
            }
            if (!expectedAudience.equals(claims.get("aud"))) {
                return new ValidationResult.Invalid("audience mismatch");
            }
            return new ValidationResult.Valid(claims);
        } catch (Exception e) {
            return new ValidationResult.Invalid("parse failure: " + e.getMessage());
        }
    }

    public boolean hasScope(Map<String, String> claims, String requiredScope) {
        String[] granted = claims.getOrDefault("scope", "").split(" ");
        return Arrays.asList(granted).contains(requiredScope);
    }

    public TokenResponse exchangeCode(String code, String clientId, String clientSecret,
                                      String redirectUri) {
        CodeEntry entry = codes.remove(code);
        if (entry == null) return new TokenResponse(null, "invalid_grant: unknown or "
                + "already-used code");
        if (!entry.clientId().equals(clientId)) {
            return new TokenResponse(null, "invalid_grant: code not bound to this client");
        }
        if (!entry.redirectUri().equals(redirectUri)) {
            return new TokenResponse(null, "invalid_grant: redirect_uri mismatch");
        }
        if (entry.expiresAt() < nowSeconds()) {
            return new TokenResponse(null, "invalid_grant: code expired");
        }
        String secret = clientSecrets.get(clientId);
        if (secret == null || !MessageDigest.isEqual(
                secret.getBytes(StandardCharsets.UTF_8),
                clientSecret.getBytes(StandardCharsets.UTF_8))) {
            return new TokenResponse(null, "invalid_client: bad client secret");
        }
        Token token = mintToken(ISSUER, entry.userId(), AUDIENCE, entry.scope(),
                                3600L, nowSeconds());
        return new TokenResponse(token.value(), null);
    }

    private static long nowSeconds() {
        return System.currentTimeMillis() / 1000L;
    }

    private static void check(String label, boolean ok) {
        System.out.printf("[%s] %s%n", ok ? "PASS" : "FAIL", label);
    }

    public static void main(String[] args) throws Exception {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        OAuth2CodeFlow server = new OAuth2CodeFlow(key, 300L);
        server.registerClient("web-app", "s3cr3t!", "https://app.example.com/callback");

        System.out.println("=== OAuth2 Authorization Code Flow ===");

        System.out.println("--- 1. Happy path ---");
        String code = server.issueCode("web-app", "https://app.example.com/callback",
                                       "alice", "read write");
        TokenResponse resp = server.exchangeCode(code, "web-app", "s3cr3t!",
                                                 "https://app.example.com/callback");
        check("token issued without error", resp.error() == null);
        ValidationResult v = server.validate(resp.accessToken(), ISSUER, AUDIENCE,
                                             System.currentTimeMillis() / 1000L);
        check("token validates", v instanceof ValidationResult.Valid);
        boolean canRead = v instanceof ValidationResult.Valid valid
                && server.hasScope(valid.claims(), "read");
        check("scope 'read' granted", canRead);

        System.out.println("--- 2. Code replay rejected ---");
        TokenResponse replay = server.exchangeCode(code, "web-app", "s3cr3t!",
                                                   "https://app.example.com/callback");
        check("replay rejected", replay.error() != null && replay.error().contains("already-used"));

        System.out.println("--- 3. Wrong client rejects ---");
        server.registerClient("other-app", "other-secret", "https://other.example.com/cb");
        String code2 = server.issueCode("web-app", "https://app.example.com/callback",
                                        "alice", "read");
        TokenResponse wrongClient = server.exchangeCode(code2, "other-app", "other-secret",
                                                        "https://app.example.com/callback");
        check("wrong client rejected", wrongClient.error() != null
                && wrongClient.error().contains("not bound"));

        System.out.println("--- 4. Wrong redirect_uri rejects ---");
        String code3 = server.issueCode("web-app", "https://app.example.com/callback",
                                        "alice", "read");
        TokenResponse wrongRedirect = server.exchangeCode(code3, "web-app", "s3cr3t!",
                                                          "https://evil.example.com/cb");
        check("wrong redirect_uri rejected", wrongRedirect.error() != null
                && wrongRedirect.error().contains("redirect_uri"));

        System.out.println("--- 5. Wrong client_secret rejects ---");
        String code4 = server.issueCode("web-app", "https://app.example.com/callback",
                                        "alice", "read");
        TokenResponse wrongSecret = server.exchangeCode(code4, "web-app", "wrong!",
                                                        "https://app.example.com/callback");
        check("wrong secret rejected", wrongSecret.error() != null
                && wrongSecret.error().contains("invalid_client"));

        System.out.println("--- 6. Tampered token rejected ---");
        Token token = server.mintToken(ISSUER, "alice", AUDIENCE, "read", 3600L,
                                       System.currentTimeMillis() / 1000L);
        String value = token.value();
        byte[] payload = B64D.decode(value.split("\\.")[1]);
        payload[3] ^= 0x01;
        String tampered = value.split("\\.")[0] + "."
                + B64.encodeToString(payload) + "." + value.split("\\.")[2];
        ValidationResult tv = server.validate(tampered, ISSUER, AUDIENCE,
                                              System.currentTimeMillis() / 1000L);
        check("tampered payload rejected", tv instanceof ValidationResult.Invalid
                && ((ValidationResult.Invalid) tv).reason().contains("signature"));

        System.out.println("--- 7. Expired token rejected ---");
        Token old = server.mintToken(ISSUER, "alice", AUDIENCE, "read", 60L,
                                     System.currentTimeMillis() / 1000L - 120L);
        ValidationResult ev = server.validate(old.value(), ISSUER, AUDIENCE,
                                              System.currentTimeMillis() / 1000L);
        check("expired token rejected", ev instanceof ValidationResult.Invalid
                && ((ValidationResult.Invalid) ev).reason().contains("expired"));

        System.out.println("--- 8. Wrong audience rejected ---");
        Token wrongAud = server.mintToken(ISSUER, "alice", "https://other-api.example.com",
                                          "read", 3600L, System.currentTimeMillis() / 1000L);
        ValidationResult av = server.validate(wrongAud.value(), ISSUER, AUDIENCE,
                                              System.currentTimeMillis() / 1000L);
        check("wrong audience rejected", av instanceof ValidationResult.Invalid
                && ((ValidationResult.Invalid) av).reason().contains("audience"));

        System.out.println("--- 9. Scope enforcement ---");
        ValidationResult sv = server.validate(resp.accessToken(), ISSUER, AUDIENCE,
                                              System.currentTimeMillis() / 1000L);
        boolean adminDenied = sv instanceof ValidationResult.Valid valid2
                && !server.hasScope(valid2.claims(), "admin");
        check("admin scope denied to read/write token", adminDenied);

        System.out.println("--- 10. Unregistered redirect_uri at authorize ---");
        try {
            server.issueCode("web-app", "https://evil.example.com/cb", "alice", "read");
            check("unregistered redirect_uri rejected", false);
        } catch (IllegalArgumentException e) {
            check("unregistered redirect_uri rejected", true);
        }

        System.out.println("--- 11. Malformed token ---");
        ValidationResult mv = server.validate("not.a.token", ISSUER, AUDIENCE, 0L);
        check("malformed token rejected", mv instanceof ValidationResult.Invalid);
    }
}
```

---

## Step 4: Walkthrough of a Concrete Run

### 4.1 Happy path, byte by byte

1. `issueCode` validates the redirect_uri against the registry, then stores a `CodeEntry` keyed by a 256-bit random value. The entry binds: client "web-app", the exact callback URL, user "alice", scope "read write", expiry now + 300 s.
2. `exchangeCode` **removes** the entry from the map first. All checks then run against the removed entry: client match → redirect match → expiry → secret (constant-time compare).
3. `mintToken` builds claims (iss, sub, aud, iat, exp, scope, jti), serializes, signs header.payload with HMAC-SHA256, and returns the compact token.
4. `validate` splits, verifies the signature first (constant-time), then parses claims only after the signature is proven, then checks exp (30 s skew), iss, aud. A resource call additionally enforces scope via `hasScope`.

### 4.2 The single-use property, demonstrated

Step 2 of main() exchanges the same code twice: the second exchange returns `invalid_grant: unknown or already-used code` — because the first exchange already removed it. Note the *failed* exchanges in steps 3–5 also consumed their codes: the wrong-client attempt destroyed the code before the client check — the remove-before-validate design — so the legitimate client cannot race the attacker.

### 4.3 The tampering case

The harness flips one bit in the *payload* of a valid token and keeps the signature. Validation recomputes the HMAC over the modified header.payload and compares against the old signature — mismatch, "signature mismatch". This is the guarantee that any modification of the signed content (claims, expiry, scope, subject) is detected before the claims are trusted.

### 4.4 The audience trap

The same HMAC key signs tokens for both api.example.com and (hypothetically) other-api.example.com. Without the aud check, a token minted for the other API would be accepted by ours (token confusion). Step 8 shows the rejection.

---

## Step 5: Testing & Verification

| # | Test | Input | Expected | Verified |
|---|------|-------|----------|----------|
| 1 | Happy path | full flow | token issued, validates, scope ok | main() §1 |
| 2 | Code replay | second exchange | invalid_grant (consumed) | main() §2 |
| 3 | Wrong client | other client exchanges | invalid_grant (not bound) | main() §3 |
| 4 | Wrong redirect | attacker callback URL | invalid_grant (redirect_uri) | main() §4 |
| 5 | Wrong secret | bad client_secret | invalid_client | main() §5 |
| 6 | Tampered token | 1-bit payload flip | signature mismatch | main() §6 |
| 7 | Expired token | iat 120 s ago, ttl 60 s | token expired | main() §7 |
| 8 | Wrong audience | other API audience | audience mismatch | main() §8 |
| 9 | Scope denial | read/write token, ask admin | denied | main() §9 |
| 10 | Unregistered redirect | authorize-time check | rejected | main() §10 |
| 11 | Malformed token | "not.a.token" | rejected | main() §11 |
| 12 | Issuer mismatch | wrong iss claim | rejected | code (§validate) |
| 13 | Expired code | code TTL elapsed | invalid_grant: expired | code (§exchangeCode) |
| 14 | Unknown client | authorize for unknown client | rejected | code (§issueCode) |

---

## Complexity Analysis

**Time**:
- issueCode: O(1) map put + 32-byte RNG draw.
- exchangeCode: O(1) map remove + constant checks + one HMAC (mint).
- validate: one HMAC over ~200 bytes + claim parse — O(1), sub-microsecond-to-microsecond; **no network round trip**, which is the point of self-contained tokens.
- `hasScope`: O(scopes).

**Space**: O(1) per code entry (TTL'd by the map remove on exchange; expired codes are never explicitly swept — see pitfall #8). Token objects are O(claims).

**Scaling notes**: the in-memory code store is single-node; a production server would use a distributed store (Redis) with the same remove-atomicity semantics (Redis `GETDEL` or a Lua script), and HMAC keys would be rotated with a key-id in the token header. The constant-time compare and remove-before-validate invariants carry over unchanged.

---

## Edge Cases & Pitfalls

1. **Validate-before-consume ordering**: consuming the code *after* validation allows replay races and lets attackers probe liveness. The remove-first pattern is the correctness core.
2. **Constant-time signature comparison**: `Arrays.equals` leaks the mismatch position over timing. `MessageDigest.isEqual` is the fix; never compare MACs with `==`.
3. **Signature before claim parsing**: never trust claims from an unverified payload — an attacker who forges the payload could set exp far in the future; the parse must come after the signature check.
4. **Clock skew**: a strict `exp < now` check rejects healthy tokens when servers' clocks differ by seconds. The 30 s skew window is the standard tolerance; be explicit about it.
5. **Redirect URI matching**: exact string comparison. Prefix matching (`startsWith`) is a classic open-redirect injection; exact match is mandatory.
6. **Code expiry without exchange**: consumed codes vanish on exchange; unconsumed expired codes linger in the map. For a lab it's acceptable; production needs TTL-based eviction (Redis TTL) or a periodic sweep.
7. **Scope as space-separated string**: `split(" ")` on an empty scope yields `[""]` — `hasScope` must not grant anything for an empty string; the token mint for an empty scope is a caller decision, documented.
8. **Secret comparison**: client secrets compared with `MessageDigest.isEqual` too — they are sensitive shared values, and the same timing argument applies.
9. **Malformed base64url**: `Base64.getUrlDecoder()` throws on bad input — caught and mapped to a typed Invalid outcome, so callers never see raw exceptions.

---

## Follow-up Questions

1. **PKCE (RFC 7636)**: derive the S256 transform — code_challenge = BASE64URL(SHA256(code_verifier)). Where in the flow does the server store the challenge, and what does it compare at the token endpoint? Why is PKCE recommended even for confidential clients today?

2. **Refresh token rotation**: design the refresh lifecycle: opaque vs signed, rotation on use, reuse detection (if a rotated token is presented again, revoke the whole family). Why does rotation convert theft into detection?

3. **OIDC ID token**: the ID token is a signed JWT with `sub`, `aud`, `nonce`, `at_hash` — what does the nonce bind, and what does at_hash prove? Where does the access token *not* carry identity claims?

4. **JWT alg confusion**: the classic attack — `{"alg":"none"}` and the RS256→HS256 confusion. Why does this implementation's fixed algorithm string avoid it, and what is the hardening rule for JWT libraries (whitelist algs, bind key types to algs)?

5. **Token revocation**: self-contained tokens can't be revoked by the resource server alone — what are the options (short TTL + refresh, revocation list per audience, token-status endpoint, jti-based denylists)? Pick one and justify it for a 10M-user API.

6. **Distributed code store**: the remove-before-validate semantics over Redis — write the Lua script or GETDEL usage that preserves atomicity, and explain why check-then-delete in two round trips breaks the invariant.

---

## Extension Ideas

- **PKCE support**: add `code_challenge`/`code_challenge_method` to the authorization request and verifier validation at the token endpoint — the public-client path.
- **Refresh tokens**: add mint/rotate/revoke with reuse detection and a family revocation policy.
- **Key rotation**: add a `kid` claim and a key registry; validate with the key whose id is in the header; demonstrate overlapping-validity rotation windows.
- **Token introspection**: implement the RFC 7662-style endpoint returning active/exp/scope/sub for opaque-token compatibility.
- **Concurrency harness**: fire 100 simultaneous exchanges of the same code from a thread pool and assert exactly one succeeds — the live proof of the single-use invariant.
