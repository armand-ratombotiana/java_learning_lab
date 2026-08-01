# Mock Interview: OAuth2 Authorization Code Flow

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Identity Platform Engineer (Auth Team)
**Candidate Level**: Senior Engineer
**Focus Area**: OAuth2/OIDC protocol mechanics, token lifecycle, validation, threat model
**Problem**: Implement the OAuth2 authorization code flow — authorization endpoint, token endpoint, and a token validator — with correct validation of expiry, signature, audience, and single-use codes.
**Language**: Java 21+ (records, HMAC via javax.crypto, Base64)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. Walk through the authorization code flow: who talks to whom, in what order?
2. Why an authorization code instead of returning the token directly from the browser?
3. What is the code-exchange attack surface: code interception, CSRF on the redirect, token leakage via Referer?
4. What must a token validator check — and in what order?
5. Why must codes be single-use, short-lived, and bound to redirect_uri?
6. Follow-up: PKCE, refresh tokens, OIDC ID tokens, and the token endpoint's client authentication.

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "We're building the auth platform for a SaaS product. The API needs OAuth2 with the authorization code flow — implement the server side: code issuance, token issuance, and token validation. Clarify the scope."

**Candidate**: "Three questions. First, client authentication at the token endpoint: confidential clients with a client secret (server-side web apps) — I'll assume that, and mention public clients with PKCE as the extension. Second, token format: I'll make access tokens self-contained — HMAC-signed and compact (JWT-like, but hand-rolled claims so there's no JSON dependency) — so any resource server can validate offline with the shared secret. Third, what's in scope for validation: signature, expiry, issuer, audience, and single-use codes with expiry — the full contract."

**Interviewer**: "Correct on all three. Build it."

**Candidate**: "Then the modules: a client registry, an authorization endpoint that mints short-lived single-use codes bound to the redirect URI and client, a token endpoint that authenticates the client, validates the code, and mints the signed token, and a validator that parses and checks everything with clear error types."

### Part 2: Flow Mechanics (10 minutes)

**Interviewer**: "Walk the full flow, and tell me where each security property lives."

**Candidate**: "Step 1: the client redirects the user's browser to the authorization endpoint with client_id, redirect_uri, scope, and state. Step 2: the user authenticates (out of scope today — but the session is bound to the authorization request) and grants; the server issues a random, high-entropy authorization code and redirects the browser to redirect_uri with code + state. Step 3: the client, *server-side*, exchanges the code at the token endpoint, authenticating itself with its client_secret, and including the redirect_uri. Step 4: the token endpoint validates everything and returns the access token. The security properties: the code is single-use, short-lived (5–10 minutes), bound to exactly one client and one redirect_uri — so a stolen code can't be replayed by another client or a different redirect target. The client_secret proves the code exchange comes from the registered client, not a browser-based attacker. The state parameter is the CSRF defense: it must be echoed back unchanged, proving the redirect belongs to the session that started the flow."

**Interviewer**: "Why not just hand the token to the browser and skip the code?"

**Candidate**: "The browser is the least trusted component in the flow — it sees every URL, Referer headers leak the URL, browser history retains it, and browser extensions read everything. A token in the redirect URL can leak via Referer to the next site the user visits. The code is a one-shot capability with a 5-minute lifetime: even if it leaks, it expires quickly and is bound to client_id + redirect_uri, so it can't be redeemed by the attacker's client. The actual token then travels only over the client-server channel — a TLS-protected server-to-server call."

**Interviewer**: "What are the concrete attack scenarios on this design?"

**Candidate**: "Four canonical ones. (1) Code interception: attacker gets the code from the redirect — mitigated by single-use + short lifetime + client binding. (2) CSRF login injection: attacker initiates a flow and tricks the victim into completing it, then uses the code — mitigated by `state` (attacker's state doesn't match the victim's session). (3) Malicious redirect_uri: if the endpoint doesn't validate the redirect_uri against the registered one, an attacker registers a redirect to their own site — the code goes straight to them. (4) Token endpoint brute force of client_secret — mitigated by rate limiting and high-entropy secrets. Also worth naming: the token must never go into the browser's URL bar, and the redirect must be a 302 with the code in the query (or better, a fragment for the implicit flow — which we're not doing)."

### Part 3: Token Validation (8 minutes)

**Interviewer**: "What does the validator check, and in what order?"

**Candidate**: "In this order, because each check is cheaper than the last: (1) parse — three dot-separated base64url parts; (2) signature — HMAC-SHA256 over header.payload with the shared secret, compared in constant time; (3) expiry — exp > now (with a small clock-skew allowance, e.g. 30 seconds); (4) not-before — iat/not-before ≤ now; (5) issuer — must equal the registered issuer; (6) audience — must contain the resource server's identifier; (7) scopes — the requested scope set must be a subset of the token's. The result is a typed outcome — `Valid(token)` or `Invalid(reason)` — never an exception storm. The order matters operationally: signature failures short-circuit before any claim parsing is trusted."

**Interviewer**: "What makes the signature comparison constant-time, and why bother?"

**Candidate**: "Comparing byte arrays with `==` or `Arrays.equals` short-circuits on the first mismatch — a timing side channel that can leak the HMAC byte-by-byte in a local or network-timing attack. The constant-time compare XORs all bytes and checks the aggregate — the same total work regardless of where the difference is. `MessageDigest.isEqual` does exactly this; I'll use it."

### Part 4: Implementation (15 minutes)

**Interviewer**: "Code the token mint and the validator core."

**Candidate**:

```java
public record Token(String value, String header, String payload, String signature,
                    Map<String, String> claims) {}

public static Token mint(String issuer, String subject, String audience,
                         String scope, long ttlSeconds, long issuedAt,
                         SecretKey hmacKey) {
    Map<String, String> claims = new LinkedHashMap<>();
    claims.put("iss", issuer);
    claims.put("sub", subject);
    claims.put("aud", audience);
    claims.put("iat", Long.toString(issuedAt));
    claims.put("exp", Long.toString(issuedAt + ttlSeconds));
    claims.put("scope", scope);
    claims.put("jti", randomId(16));
    String header = b64url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(UTF_8));
    String payload = b64url(serialize(claims).getBytes(UTF_8));
    String sig = b64url(hmac(header + "." + payload, hmacKey));
    return new Token(header + "." + payload + "." + sig, header, payload, sig, claims);
}

public static ValidationResult validate(String tokenValue, String expectedIssuer,
                                        String expectedAudience, long nowSeconds,
                                        SecretKey hmacKey) {
    String[] parts = tokenValue.split("\\.", -1);
    if (parts.length != 3) return invalid("malformed token");
    if (!MessageDigest.isEqual(hmac(parts[0] + "." + parts[1], hmacKey),
                               b64decode(parts[2])))
        return invalid("bad signature");
    Map<String, String> claims = parse(parts[1]);
    if (!claims.containsKey("exp") || !inFuture(claims.get("exp"), nowSeconds))
        return invalid("token expired");
    if (!claims.getOrDefault("iss", "").equals(expectedIssuer))
        return invalid("wrong issuer");
    if (!claims.getOrDefault("aud", "").equals(expectedAudience))
        return invalid("wrong audience");
    return valid(claims);
}
```

**Interviewer**: "Good — note the validation order. Now the code lifecycle."

**Candidate**:

```java
public static String issueCode(String clientId, String redirectUri, String userId,
                               String scope, long ttlSeconds) {
    String code = randomId(32);
    codes.put(code, new CodeEntry(clientId, redirectUri, userId, scope,
                                  nowSeconds() + ttlSeconds));
    return code;
}

public static TokenResult exchangeCode(String code, String clientId, String clientSecret,
                                       String redirectUri, ...) {
    CodeEntry entry = codes.remove(code);          // single-use: remove FIRST
    if (entry == null) return failure("unknown or already-used code");
    if (!entry.clientId().equals(clientId)) return failure("client mismatch");
    if (!entry.redirectUri().equals(redirectUri)) return failure("redirect_uri mismatch");
    if (entry.expiresAt() < nowSeconds()) return failure("code expired");
    if (!authenticateClient(clientId, clientSecret)) return failure("bad client secret");
    return success(mint(...));                     // token bound to entry.userId()/scope
}
```

**Interviewer**: "Why remove the code before validating?"

**Candidate**: "Because 'single-use' means the first redeem attempt consumes it, *even if the attempt fails*. If I validated first and only removed on success, an attacker who guesses or steals a code could race the legitimate client, and replay-with-wrong-params probing would reveal which codes are live. Remove-then-validate makes replay impossible and turns probe attempts into plain failures. This is a classic implementation detail interviewers and auditors both look for."

### Part 5: Testing (5 minutes)

**Interviewer**: "Test plan?"

**Candidate**: "Ten cases: (1) happy path — full flow: authorize → code → exchange → validate → resource call; (2) code replay — second exchange fails; (3) expired code — fails; (4) wrong client exchanging the code — fails; (5) wrong redirect_uri — fails; (6) wrong client_secret — fails; (7) tampered token (flip one payload byte) — signature failure; (8) expired token — fails with the exp check; (9) wrong audience — fails; (10) scope enforcement — a token without the 'admin' scope is rejected for admin endpoints."

### Part 6: Follow-ups (5 minutes)

**Interviewer**: "Where does PKCE change this design?"

**Candidate**: "For public clients — SPAs and native apps that can't keep a client_secret — the client generates a random code_verifier and sends a transformed version (code_challenge = SHA-256(verifier), optionally S256) in the authorization request; at the token endpoint it must present the raw verifier, and the server checks the transform. An attacker who intercepts the code but not the verifier can't exchange it. PKCE is now recommended even for confidential clients as defense-in-depth against code interception."

**Interviewer**: "And refresh tokens?"

**Candidate**: "The access token gets a short TTL (minutes to an hour); the token endpoint also returns a refresh token — a long-lived, server-side-validated capability (rotated on every use). The refresh token is stored server-side (or as a signed but revocable token with a revocation list) and can be revoked per-session. The trade-off: a leaked refresh token is a big deal, so it's bound to the client, rotated, and its use is logged. I'd also bind access-token *metadata* — the token points at the session, so revoking the session kills all its tokens without waiting for expiry."

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Flow knowledge | Full walkthrough with the *why* of each hop | Correct sequence | Confuses code/token |
| Threat model | state/CSRF, redirect_uri binding, Referer leak, code racing | Mentions state | No attack awareness |
| Validation | Ordered checks incl. constant-time signature compare | Basic expiry check | Signature only |
| Code lifecycle | Remove-before-validate single-use semantics | Single-use on success only | Reusable codes |
| Tests | Full negative suite | Happy path | None |

## Red Flags
- Token in the browser URL or Referer-visible.
- Validating the code before consuming it.
- `Arrays.equals` for HMAC comparison.
- Not binding the code to redirect_uri.
- Not checking audience/issuer (token confusion between services).

## Key Takeaways
- Code is a short-lived, single-use, client- and redirect-bound capability.
- Validate in order: parse → signature (constant-time) → exp → iss/aud → scope.
- state defeats CSRF; PKCE protects public clients; remove-before-validate prevents code racing.
- Self-contained signed tokens validate offline with a shared secret.
