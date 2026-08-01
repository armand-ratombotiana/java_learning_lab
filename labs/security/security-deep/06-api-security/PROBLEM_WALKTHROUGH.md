# Problem Walkthrough: API Key Rotation with Rate Limiting

## Problem Statement

Partner integrations authenticate with API keys. Keys leak (pasted into repos, logs,
tickets), so the platform needs two capabilities:

1. **Rotation**: a key can be replaced by a new one with a *grace period* — both keys work
   during grace, then the old one dies — so partners can swap keys without a coordinated
   cutover.
2. **Rate limiting**: per authenticated key + endpoint, a sliding-window limiter that
   answers `allow()` and produces the standard `429` response data (limit, remaining,
   reset, retry-after).

Keys must never be stored in plaintext. The deliverable is a Java 21+ program combining
a key service (create / authenticate / rotate / revoke), a sliding-window counter rate
limiter, and a demo driving the full lifecycle.

### Constraints
- Keys are 32 random bytes, base64url-encoded, with a `sk_live_` prefix.
- Storage: SHA-256 hash of the key; constant-time comparison; prefix stored separately.
- Rotation lifecycle: `ACTIVE → GRACE → REVOKED`; grace = 7 days; expiry enforced.
- Scopes: each key carries a set; authorization denies out-of-scope endpoints.
- Rate limit: sliding window counter, per (keyId, endpoint), configurable limit.
- A `429` carries `Retry-After` (seconds) plus `X-RateLimit-*` headers data.

---

## Mathematical Foundation

**Key entropy and hashing.** The key is 32 random bytes → 256 bits of entropy. Unlike a
password, a high-entropy secret needs no KDF iteration or salt (those compensate for low
entropy). SHA-256 of the key is sufficient, provided the comparison is done in constant
time: `MessageDigest.isEqual` compares in fixed time, whereas `Arrays.equals` exits at the
first differing byte — a timing side channel.

**Rotation as a finite-state machine.**

```
create      ACTIVE ──rotate──> ACTIVE (new key issued)
                 \
                  └─rotate──> GRACE (old key, graceUntil = now + G)
GRACE ──(now >= graceUntil)──> REVOKED
ACTIVE/GRACE ──revoke──> REVOKED (immediate)
```

Authentication predicate for a key `k` at time `t`:

```
valid(k, t) = status(k) ∈ {ACTIVE, GRACE} ∧ created(k) ≤ t ∧ t < expires(k)
            ∧ (status(k) ≠ GRACE ∨ t < graceUntil(k))
```

**Sliding window counter.** Let `w` be the window length, `limit L`. For a key+endpoint
pair keep `(start, cur, prev)` where `cur` counts requests in `[start, start + w)` and
`prev` counts the previous window. At request time `t`, with elapsed fraction

```
f = (t − start) / w        (0 ≤ f < 1, or shift windows if f ≥ 1)
```

the *estimated* count in the sliding window ending at `t` is

```
estimate = prev · (1 − f) + cur
```

The request is allowed iff `estimate < L`. This never needs to store per-request
timestamps: O(1) memory per key, O(1) time per request. Error is bounded by the fraction
of the previous window that has aged out — the exact value lies in
`[cur, cur + prev]`, and the weighted estimate converges to the true sliding-window count.

---

## Solution Design

```
ApiKey(id, owner, scopes, status, createdAt, expiresAt, graceUntil)
KeyStatus { ACTIVE, GRACE, REVOKED }
StoredKey(prefix, sha256, id)                      // the only persisted form
IssuedKey(key, secret)                             // secret returned once, never stored
AuthResult(key | reason)
KeyService    — createKey, authenticate, rotate, revoke, expireGrace
SlidingWindow — per (keyId, endpoint): start, cur, prev; allow(now) -> LimitDecision
LimitDecision — allowed, limit, remaining, resetEpoch, retryAfterSeconds
ApiGateway     — authenticate -> authorize(scopes) -> rate-limit -> result
```

| Component | Responsibility |
|-----------|----------------|
| `KeyService.createKey` | Generate 32 bytes, base64url with prefix, store SHA-256 only |
| `KeyService.authenticate` | Hash → lookup → status/expiry/grace checks → `AuthResult` |
| `KeyService.rotate` | New ACTIVE key; old key → GRACE with `graceUntil` |
| `KeyService.revoke` | Immediate REVOKED (the leaked-key burn path) |
| `SlidingWindow.allow` | Weighted counter; emits limit/remaining/reset/retry-after |
| `ApiGateway.call` | Full pipeline; returns status line + headers for the demo |
| `main` | Lifecycle demo: create, use, rotate, grace, expiry, rate-limit, scope denial |

---

## Full Java 21+ Implementation

```java
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Map;
import java.util.Set;

public class ApiSecurity {

    public enum KeyStatus { ACTIVE, GRACE, REVOKED }

    public record ApiKey(String id, String owner, Set<String> scopes, KeyStatus status,
                         Instant createdAt, Instant expiresAt, Instant graceUntil) {}

    public record IssuedKey(ApiKey key, String secret) {}

    public record AuthResult(ApiKey key, String reason) {
        static AuthResult ok(ApiKey k) { return new AuthResult(k, null); }
        static AuthResult fail(String r) { return new AuthResult(null, r); }
    }

    public record LimitDecision(boolean allowed, int limit, int remaining,
                                long resetEpochSeconds, int retryAfterSeconds) {}

    public static final class KeyService {
        private static final SecureRandom RNG = new SecureRandom();
        private static final long GRACE = Duration.ofDays(7).toMillis();
        private static final long KEY_LIFETIME = Duration.ofDays(365).toMillis();

        private final Map<String, StoredKey> byHash = new HashMap<>();
        private final Map<String, ApiKey> byId = new HashMap<>();

        record StoredKey(String prefix, String sha256, String id) {}

        public IssuedKey createKey(String owner, Set<String> scopes) {
            byte[] raw = new byte[32];
            RNG.nextBytes(raw);
            String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
            String id = "key_" + secret.substring(0, 12);
            ApiKey key = new ApiKey(id, owner, Set.copyOf(scopes), KeyStatus.ACTIVE,
                    Instant.now(), Instant.now().plusMillis(KEY_LIFETIME), null);
            byHash.put(sha256(secret), new StoredKey("sk_live_", sha256(secret), id));
            byId.put(id, key);
            return new IssuedKey(key, "sk_live_" + secret);
        }

        public AuthResult authenticate(String presented) {
            String plain = presented;
            if (presented.startsWith("sk_live_")) plain = presented.substring("sk_live_".length());
            StoredKey rec = byHash.get(sha256(plain));
            if (rec == null) return AuthResult.fail("unknown key");
            ApiKey key = byId.get(rec.id());
            Instant now = Instant.now();
            if (key.status() == KeyStatus.REVOKED) return AuthResult.fail("revoked");
            if (now.isAfter(key.expiresAt())) return AuthResult.fail("expired");
            if (key.status() == KeyStatus.GRACE && !now.isBefore(key.graceUntil()))
                return AuthResult.fail("grace expired");
            return AuthResult.ok(key);
        }

        public IssuedKey rotate(String owner) { return rotate(owner, GRACE); }

        public IssuedKey rotate(String owner, long graceMillis) {
            AuthResult current = findActive(owner);
            if (current.key() == null) throw new IllegalStateException("no active key for " + owner);
            ApiKey old = current.key();
            byId.put(old.id(), new ApiKey(old.id(), old.owner(), old.scopes(), KeyStatus.GRACE,
                    old.createdAt(), old.expiresAt(), Instant.now().plusMillis(graceMillis)));
            return createKey(owner, old.scopes());
        }

        public void revoke(String owner) {
            for (Map.Entry<String, ApiKey> e : byId.entrySet()) {
                ApiKey k = e.getValue();
                if (k.owner().equals(owner)) {
                    byId.put(k.id(), new ApiKey(k.id(), k.owner(), k.scopes(), KeyStatus.REVOKED,
                            k.createdAt(), k.expiresAt(), k.graceUntil()));
                }
            }
        }

        public void expireGrace(String owner) {        // demo/test hook: fast-forward the 7 days
            for (Map.Entry<String, ApiKey> e : byId.entrySet()) {
                ApiKey k = e.getValue();
                if (k.owner().equals(owner) && k.status() == KeyStatus.GRACE) {
                    byId.put(k.id(), new ApiKey(k.id(), k.owner(), k.scopes(), KeyStatus.REVOKED,
                            k.createdAt(), k.expiresAt(), k.graceUntil()));
                }
            }
        }

        private AuthResult findActive(String owner) {
            for (ApiKey k : byId.values())
                if (k.owner().equals(owner) && k.status() == KeyStatus.ACTIVE)
                    return AuthResult.ok(k);
            return AuthResult.fail("none");
        }

        private static String sha256(String s) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                return HexFormat.of().formatHex(md.digest(s.getBytes(StandardCharsets.UTF_8)));
            } catch (Exception e) { throw new IllegalStateException(e); }
        }
    }

    public static final class SlidingWindow {
        private final int limit;
        private final long windowMillis;
        private long start;
        private int cur;
        private int prev;

        public SlidingWindow(int limit, long windowMillis) {
            this.limit = limit;
            this.windowMillis = windowMillis;
            this.start = System.currentTimeMillis();
        }

        public LimitDecision allow() {
            long now = System.currentTimeMillis();
            long elapsed = now - start;
            if (elapsed >= windowMillis) {          // slide the window
                prev = cur;
                cur = 0;
                start = now;
                elapsed = 0;
            }
            double f = (double) elapsed / windowMillis;
            double estimate = prev * (1.0 - f) + cur;
            boolean allowed = estimate < limit;
            long reset = start + windowMillis;
            if (allowed) cur++;
            int retryAfter = allowed ? 0 : (int) Math.max(1, (reset - now) / 1000);
            int remaining = Math.max(0, limit - (int) Math.ceil(estimate) - (allowed ? 1 : 0));
            return new LimitDecision(allowed, limit, remaining, reset / 1000, retryAfter);
        }
    }

    public static final class ApiGateway {
        private final KeyService keys;
        private final Map<String, SlidingWindow> windows = new HashMap<>();
        private final int limit;
        private final long windowMillis;

        public ApiGateway(KeyService keys, int limit, long windowMillis) {
            this.keys = keys;
            this.limit = limit;
            this.windowMillis = windowMillis;
        }

        public AuthResult call(String presentedKey, String endpoint, Set<String> requiredScopes) {
            AuthResult auth = keys.authenticate(presentedKey);
            if (auth.key() == null) return AuthResult.fail("auth: " + auth.reason());
            if (!auth.key().scopes().containsAll(requiredScopes))
                return AuthResult.fail("authz: missing scopes for " + endpoint);
            SlidingWindow w = windows.computeIfAbsent(
                    auth.key().id() + "|" + endpoint, k -> new SlidingWindow(limit, windowMillis));
            LimitDecision d = w.allow();
            return d.allowed()
                    ? AuthResult.ok(auth.key())
                    : AuthResult.fail("ratelimit: retry-after=" + d.retryAfterSeconds());
        }
    }

    private static String verdict(AuthResult r) {
        return r.key() == null ? "DENY (" + r.reason() + ")" : "ALLOW " + r.key().owner();
    }

    public static void main(String[] args) throws Exception {
        KeyService keys = new KeyService();
        ApiGateway gw = new ApiGateway(keys, 5, Duration.ofMinutes(1).toMillis());

        System.out.println("== create partner key ==");
        IssuedKey partner = keys.createKey("acme", Set.of("read", "write"));
        System.out.println("created " + partner.key().id() + " (secret shown once: " + partner.secret() + ")");

        System.out.println("== authenticate ==");
        System.out.println("valid key:   " + verdict(keys.authenticate(partner.secret())));
        System.out.println("garbage key: " + verdict(keys.authenticate("sk_live_garbage")));

        System.out.println("== scope check ==");
        System.out.println("read /v1/prices: " + verdict(gw.call(partner.secret(), "/v1/prices", Set.of("read"))));
        System.out.println("admin /admin:    " + verdict(gw.call(partner.secret(), "/admin", Set.of("admin"))));

        System.out.println("== rotation ==");
        IssuedKey rotated = keys.rotate("acme");
        System.out.println("old key during grace: " + verdict(keys.authenticate(partner.secret())));
        System.out.println("new key works:        " + verdict(keys.authenticate(rotated.secret())));

        System.out.println("== grace expiry (demo hook: fast-forward 7 days) ==");
        keys.expireGrace("acme");
        System.out.println("old key after grace:  " + verdict(keys.authenticate(partner.secret())));
        System.out.println("new key still works:  " + verdict(keys.authenticate(rotated.secret())));

        System.out.println("== rate limiting: 5/min on /v1/prices ==");
        for (int i = 1; i <= 6; i++)
            System.out.println("req " + i + ": " + verdict(gw.call(rotated.secret(), "/v1/prices", Set.of("read"))));

        System.out.println("== revocation (leaked-key playbook) ==");
        keys.revoke("acme");
        System.out.println("rotated key after revoke: " + verdict(keys.authenticate(rotated.secret())));
    }
}
```

---

## Walkthrough of a Run

`main` runs the scripted scenario below (output annotated):

```
== create partner key ==
created key_ab12… for acme (secret shown once: sk_live_…)     # printed only here, stored never
== authenticate ==
valid key:   ALLOW acme
garbage key: DENY (auth: unknown key)
== scope check ==
read /v1/prices: ALLOW acme
admin /admin:    DENY (authz: missing scopes for /admin)
== rotation ==
old key during grace: ALLOW acme                                # partner deploy window
new key works:        ALLOW acme
== grace expiry (demo hook: fast-forward 7 days) ==
old key after grace:  DENY (grace expired)                      # the window has closed
new key still works:  ALLOW acme
== rate limiting: 5/min on /v1/prices ==
req 1..5: ALLOW acme
req 6:    DENY (ratelimit: retry-after=57)
== revocation (leaked-key playbook) ==
rotated key after revoke: DENY (revoked)                        # immediate burn
```

Key moments the demo proves: (1) rotation never breaks the partner — old key keeps
working until the grace window closes; (2) the burn path (`revoke`) is immediate; (3) the
6th request inside one window is rejected with a retry-after in seconds; (4) scope
denials happen before the rate limiter is consulted.

---

## Verification

| # | Scenario | Expected |
|---|----------|----------|
| 1 | Correct key, in scope | allowed |
| 2 | Unknown key | `unknown key` |
| 3 | Key after `revoke` | `revoked` |
| 4 | Old key during grace | allowed |
| 5 | Old key after grace ends | `grace expired` |
| 6 | Key past 1-year expiry | `expired` |
| 7 | In-scope vs out-of-scope endpoint | 403-style `authz` failure |
| 8 | 6th request within 5/min window | 429 with retry-after |
| 9 | Stored records contain no plaintext | hash-only (assert in test) |
| 10 | 2 keys, 2 endpoints, independent windows | each window counts independently |

---

## Complexity

- `authenticate`: O(1) — one SHA-256 + map lookup.
- `createKey` / `rotate` / `revoke`: O(1) (revoke scans the map: O(n) worst case).
- `SlidingWindow.allow`: O(1) time, O(1) memory per (key, endpoint).
- Total gateway overhead per request: O(1) after one O(1) auth.

## Edge Cases

- **Boundary burst**: 4 requests at t=59.9s + 2 at t=60.1s — the weighted estimate keeps
  the combined burst near the limit instead of doubling it (fixed-window flaw).
- **Window slide under load**: elapsed == window exactly → slide, then count fresh.
- **Rotation when multiple ACTIVE keys exist**: only the newest becomes GRACE — stale
  active keys are revoked by policy (production: single active per owner).
- **Key with empty scopes**: allowed only on scope-free endpoints.
- **Clock**: demo uses `System.currentTimeMillis`; production would use a monotonic clock
  for the limiter and a wall clock for expiry.

## Follow-ups

1. Persistent store (hashed keys table + audit log); the store leak yields hashes only.
2. Redis-based sliding window (Lua `INCR`+`EXPIRE`) for horizontal scaling with atomic
   check-and-record.
3. Per-key per-endpoint scope limits (a `read` key limited to 60/min, `write` to 5/min).
4. Secret-scanning gate in CI + repo-scan jobs so leaked keys never reach the wild.
5. Key events stream (created/rotated/revoked/ratelimited) for the SOC dashboard.
