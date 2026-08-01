# Mock Interview: API Key Rotation with Rate Limiting

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Security Engineer (API Platform Team)
**Candidate Level**: Senior Engineer
**Focus Area**: Credential lifecycle, hashing, constant-time comparison, rate-limit math
**Problem**: Implement an API key service that supports rotation with a grace period, plus a rate limiter that protects endpoints — and a demo that shows both working together.
**Language**: Java 21+ (records, no external libs)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. Why must keys never be stored in plaintext? What's the right hash?
2. What is the rotation lifecycle: issue → valid → grace → revoked?
3. Why a grace period for rotated keys, and what does it solve?
4. How does the rate limiter work (window, bucket, counter) and what are the math trade-offs?
5. What does a 429 look like on the wire — headers, retry-after semantics?
6. Follow-up: key prefixes, scope limits, audit trails, and the "leaked key" playbook.

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "We hand API keys to partner integrations and they leak — someone pastes one into a GitHub issue. We need rotation so keys can be replaced, and rate limiting so a leaked key can't hammer us. Scope it."

**Candidate**: "I'll build three pieces. First, the **key service**: `createKey(owner, scopes)` returns a random key but stores only its SHA-256 hash; `authenticate(key)` looks up by hash with constant-time comparison. Second, **rotation**: a rotated key keeps a *grace window* (say 7 days) during which both old and new keys work, then the old one is revoked — that's how you swap keys without breaking the partner's deploy cycle. Third, the **rate limiter**: a sliding window over per-key+endpoint counters that answers `allow(now, key, endpoint) -> boolean` and tracks remaining/retry-after for the response headers. Then a demo main that rotates a key mid-stream and shows the old key failing only after the grace period."

**Interviewer**: "What does the rate limiter need from the key service?"

**Candidate**: "The authenticated key identity. Rate limiting anonymous requests is meaningless — attackers rotate IPs; the key is the stable identity. So the pipeline is: authenticate → authorize (scopes) → rate-limit → dispatch."

### Part 2: Theory — Hashing and Comparison (10 minutes)

**Interviewer**: "Walk me through the key storage design."

**Candidate**: "The key is 32 random bytes base64-encoded — ~256 bits of entropy, no need for anything password-like (no KDF cost, no salt; a salt is for low-entropy secrets, our key has none of that problem). We store `SHA-256(key)`. Compare by hashing the presented key and using `MessageDigest.isEqual` — the constant-time comparison — because a plain `Arrays.equals` short-circuits on the first differing byte, and with timing measurements an attacker can recover the stored digest byte-by-byte... well, recovering a *hash* is useless, but the discipline costs nothing and it's the correct pattern. The larger point: the database leak is then worthless — you can't reverse SHA-256 to get the key."

**Interviewer**: "And the key format on the wire?"

**Candidate**: "`sk_live_` prefix (identifies the scheme instantly), then the base64url secret. We *store the prefix separately* so support can say 'this is a live key, format v2' without ever seeing the secret, and log `sk_live_…redacted` — never the full key — in audit trails. The hash lookup uses the raw secret; the prefix routes to the right key table."

### Part 3: Theory — Rate Limiting Math (8 minutes)

**Interviewer**: "Fixed window, sliding window, token bucket — trade-offs?"

**Candidate**: "Fixed window (e.g. 100/min per minute bucket) is the cheapest: one counter per key per window, O(1), but has the classic **boundary burst**: requests at 59.9s and 60.1s both count against their own windows → 2× the limit in a single second. Sliding window log (store every request timestamp) is exact — never exceeds the limit — but memory grows with request volume: 100/min × 100k keys is a lot of timestamps. The middle path is a **sliding window counter** — two counters, current and previous partial window, weighted by elapsed fraction: approximate but tight, O(1) memory, and the error is bounded and well-understood. Token bucket (fixed rate + burst size) is the other standard: it smooths *sustained* rate while allowing bursts — `capacity` and `refillPerSecond`. For this exercise I'll implement the sliding window counter: exact-enough, constant memory, easy to reason about in an interview."

**Interviewer**: "What belongs in the 429 response?"

**Candidate**: "`429 Too Many Requests`, `Retry-After` in seconds (or HTTP-date), `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset` (Unix epoch of window end) — the standard trio. And an idempotency consideration: the *check* and the *record* must be atomic — check-then-record with a race is how limits get bypassed. Here I'll do it single-threaded (in-memory), and note that production would use a Redis Lua script to make record atomic against concurrent requests."

### Part 4: Implementation (18 minutes)

**Interviewer**: "Code it."

**Candidate**: "Four records — `ApiKey(id, owner, prefix, scopes, status, createdAt, expiresAt)`, `KeyRecord` being the stored form with the hash, `AuthResult` carrying the key plus a reason for rejection, and the limiter's `WindowCounters`. The key service holds `Map<String, KeyRecord>` by hash, `Map<String, String>` id → owner for lookups... let me write the core pieces."

```java
public record ApiKey(String id, String owner, Set<String> scopes,
                     KeyStatus status, Instant createdAt, Instant expiresAt) {}

public enum KeyStatus { ACTIVE, GRACE, REVOKED }

public record AuthResult(ApiKey key, String reason) {
    static AuthResult ok(ApiKey k) { return new AuthResult(k, null); }
    static AuthResult fail(String r) { return new AuthResult(null, r); }
}
```

**Candidate**: "Rotation: `rotate(owner)` creates a new key and demotes the old to GRACE with `graceUntil = now + 7 days`; the authorizer accepts GRACE keys while `now < graceUntil`, then a lazy sweep flips them to REVOKED. The demo shows the old key authenticating during grace and failing after — that's the correctness story."

**Interviewer**: "Now the limiter."

**Candidate**: "Sliding window counter per (keyId, endpoint): keep `currentWindowStart`, `currentCount`, `previousCount`. On request: if `now >= windowStart + window`, shift previous=current, current=0. Weighted estimate = `previous * (1 - elapsedFraction) + current`; allow iff estimate < limit, increment current. The weights make the boundary burst a non-event — at 60.0s the previous window is fully weighted out."

### Part 5: Testing (5 minutes)

**Interviewer**: "Test plan?"

**Candidate**: "Key lifecycle: create → authenticate succeeds; wrong key fails; rotation → both keys valid during grace, old fails after 7 days; revoke → immediate fail; expired key fails. Hashing: stored record contains no plaintext (test asserts the raw key is absent from the store). Rate limit: 5/min limit, 6th request in the same window → 429 with Retry-After; a burst straddling the boundary — 4 requests at t=59.9, 2 at t=60.1 → allowed count matches the weighted estimate (either 5 or 6 by the exact formula — the assertion checks the estimate never exceeds limit + 1). And scope checks: a `read-only` key hitting a write endpoint → 403."

### Part 6: Follow-ups (5 minutes)

**Interviewer**: "A partner's key got pasted into a public repo at 3am. Playbook?"

**Candidate**: "Revoke *now* — don't rotate, revoke (rotation is for planned swaps; a public leak is a burn). Block the key at the CDN edge immediately, then check the audit log: which scopes it touched, what endpoints, what data. If scopes were wide, assume data exposure and start the breach-notification clock. Then the org-level fixes: short-lived keys, per-environment scopes, and a secret-scanning gate in CI so the paste never reaches GitHub in the first place."

---

## Extended Q&A: Follow-up Round

**Q: Why is bare SHA-256 (no salt, no KDF) acceptable for API keys but not passwords?**

**A**: KDF cost and salt exist to defend low-entropy secrets. Passwords carry roughly 30–40 bits of entropy, so an attacker with a DB dump can brute-force offline at billions of hashes per second — salt defeats rainbow tables and work factors slow the attack. An API key is 256 random bits; reversing SHA-256 on that space is impossible regardless of iteration count. Hashing is defense-in-depth for DB leaks; the entropy is the real defense.

**Q: Bound the error of the sliding-window estimate.**

**A**: At time t the true window is (t − w, t]. The current fixed window lies entirely inside it and contributes `cur` requests; the previous window contributes at most `prev` and at least 0. So the true count is in [cur, cur + prev], and the weighted estimate prev·(1 − f) + cur is the linear interpolation between the two extremes as the previous window ages out — bounded error, exact at the window boundaries, O(1) memory. That is the whole point over a fixed window: the boundary burst at 59.9s/60.1s is weighted by f instead of doubling.

**Q: What breaks if check-and-record is not atomic?**

**A**: Two concurrent requests both read count = limit − 1, both pass the test, both record — the effective limit is exceeded by the race window. In this lab's single-threaded model it is atomic by construction; in production the standard fix is a Redis Lua script (`INCR` + `EXPIRE` in one `eval`) so the check and the increment share one atomic execution.

**Q: Why rate-limit after authentication, not before?**

**A**: Limiting before auth would let anyone exhaust a victim's key budget with garbage requests — a free denial-of-service. Unauthenticated failures are limited by IP (and bot detection); the key-based limiter runs on authenticated identity only.

**Q: What does the revoke audit log record?**

**A**: Key ID (never the secret), owner, revoke time, operator, reason (rotation vs leak), last-seen IPs, and the scopes used in the trailing 24 hours. The leaked-key playbook reads exactly that log to scope the damage assessment — which data could have been reached with which capabilities.

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Key storage | SHA-256 + constant-time compare, no plaintext | Hashed | Plaintext |
| Rotation | Grace period lifecycle, status transitions | Immediate swap | None |
| Rate limiting | Sliding window counter, boundary math | Fixed window | None |
| Responses | 429 + Retry-After + limit headers | 429 only | None |
| Tests | Lifecycle + boundary + scope corpus | Happy path | None |

## Red Flags
- Storing keys in plaintext, or comparing with `Arrays.equals`.
- No grace period — rotation is a breaking event for the partner.
- Fixed-window limiter presented without acknowledging the boundary burst.
- Rate limiting that runs before authentication.

## Key Takeaways
- Hash the key (SHA-256 is fine for 256-bit entropy); compare constant-time.
- Rotation with grace: ACTIVE → GRACE → REVOKED; grace = zero-downtime swap.
- Sliding window counter: O(1) memory, bounded error, kills the boundary burst.
- 429 must carry Retry-After; leaked-key playbook is revoke, audit, assume exposure.

## Glossary

- **Entropy** — randomness measured in bits; a 32-byte key carries 256 bits.
- **SHA-256** — the one-way hash used to store keys; preimage-reversal is infeasible.
- **Constant-time comparison** — a comparison whose runtime doesn't depend on the data, preventing timing leaks.
- **KDF / salt** — key-derivation cost and per-secret randomness; needed for low-entropy passwords, unnecessary for random keys.
- **Sliding window counter** — the two-bucket rate-limit estimator: `prev·(1−f) + cur`.
- **Boundary burst** — the fixed-window flaw where requests at 59.9s and 60.1s both count fresh.
- **Grace period** — the window during which a rotated key still authenticates.
- **Retry-After** — the seconds a 429 tells the client to wait.
- **Scope** — a capability bound to a key (read, write, admin).
- **Audit log** — the tamper-evident record of key lifecycle events, never containing the secret.
- **Base64url** — the URL-safe encoding used for key material.
- **Revocation** — immediate burn of a key (the leaked-key playbook's first step).
