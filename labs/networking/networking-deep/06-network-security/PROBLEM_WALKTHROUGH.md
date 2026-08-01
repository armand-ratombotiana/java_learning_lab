# Lab 06: Problem Walkthrough — WAF Rule Engine (SQL Injection & XSS)

## Problem Statement

Design and implement the core of a Web Application Firewall: a request-screening pipeline that detects **SQL injection** and **cross-site scripting** payloads in HTTP requests, and enforces **per-client rate limiting**, escalating actions from `MONITOR` through `CHALLENGE` to `BLOCK`.

The engine must:

1. **Normalize** every request to a canonical form: percent-decode to a *fixpoint* (defeating double-encoding evasion), strip SQL comments, collapse whitespace, and case-fold.
2. **Detect SQLi** with two complementary strategies:
   - *Structural*: token-level pattern for boolean tautologies (`OR '1'='1`) that survives minor obfuscation.
   - *Signature*: classic markers — `UNION SELECT`, hex string literals, comment markers, time-delay primitives (`WAITFOR DELAY`, `pg_sleep`, `BENCHMARK`).
3. **Detect XSS** reflected payloads: `<script>` tags, event handlers (`onerror=...`), `javascript:` URLs.
4. **Aggregate** per-rule confidence into a request score; map score to an action with a calibrated escalation ladder.
5. **Rate limit** per client with a token bucket, using an injectable clock so the demo is deterministic; rate violations escalate the action.
6. Report **which rules fired** for every verdict — the operational feedback loop that makes a WAF tunable.

**Constraints**

- The pipeline must run in the request path: every stage is a single linear pass; no heavy parsers.
- Normalization must be a fixpoint (decode until no further decode is possible) — a single decode pass is the classic evasion hole.
- The decision must be deterministic given the same input, clock, and rules.
- All code compiles under Java 21+.

---

## Walkthrough

### Step 1: Request model and rule abstraction

```java
package com.networking.deep.lab06;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class WebApplicationFirewall {

    public record Request(String method, String path, String query, String body,
                          String clientIp) {
        public String target() {
            return path + (query == null || query.isEmpty() ? "" : "?" + query);
        }
    }

    public enum Action { MONITOR, CHALLENGE, BLOCK }

    public record Verdict(Action action, double score, List<String> firedRules) {}

    /** A rule contributes a confidence in [0, 1] when it matches normalized text. */
    public interface Rule {
        String name();
        double match(String normalized);
    }

    /** The generic rule: a named regex with a fixed confidence. */
    public record NamedPattern(String name, double confidence, Pattern pattern)
            implements Rule {
        @Override
        public double match(String normalized) {
            return pattern.matcher(normalized).find() ? confidence : 0.0;
        }
    }
```

### Step 2: The normalization pipeline

Normalization runs *before* any rule. The decode loop is the critical part: `%2527` must become `'` after two passes, so we decode until a fixpoint (bounded: after `%` is gone, no further decode can happen, so the loop terminates).

```java
    static final class Normalizer {

        static String normalize(String input) {
            String s = input == null ? "" : input;
            String prev;
            do {
                prev = s;
                s = percentDecode(s);
            } while (!s.equals(prev));          // fixpoint: defeats double encoding

            s = s.replaceAll("(?s)/\\*.*?\\*/", " ");   // SQL block comments
            s = s.replaceAll("(?i)--(?:\\s|$).*", " "); // SQL line comments
            s = s.replaceAll("\\s+", " ");
            return s.toLowerCase();                      // case folding
        }

        static String percentDecode(String s) {
            StringBuilder out = new StringBuilder(s.length());
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '%' && i + 2 < s.length()) {
                    try {
                        int v = Integer.parseInt(s.substring(i + 1, i + 3), 16);
                        out.append((char) v);
                        i += 2;
                        continue;
                    } catch (NumberFormatException ignored) {
                        // not a valid escape; keep as-is
                    }
                }
                out.append(c);
            }
            return out.toString();
        }
    }
```

Why the fixpoint matters: `%2527 OR 1=1` decodes once to `%27 OR 1=1` — which looks harmless to a single-pass detector — and only a second pass reveals the quote that turns it into a working payload. Any real WAF rule pipeline must hit a fixpoint or it is trivially bypassed.

### Step 3: SQL injection rules — structural and signature

The structural rule is the interesting one. Instead of listing every tautology (`1=1`, `'a'='a'`, `1 OR 1`), it matches the *shape*: the keyword `or` followed by two literal operands joined by `=`. The operands are just runs of non-space, non-quote characters (optionally quoted), so `'1'='1'`, `1=1`, and `'x'='x'` all collapse to the same pattern.

```java
    static final class OrEqualityRule implements Rule {
        private static final Pattern PATTERN = Pattern.compile(
                "\\bor\\s+['\"]?[^'\"\\s]+['\"]?\\s*=\\s*['\"]?[^'\"\\s]+['\"]?");

        @Override public String name() { return "or-equality"; }

        @Override public double match(String normalized) {
            return PATTERN.matcher(normalized).find() ? 0.95 : 0.0;
        }
    }
```

The signature rules are ordinary named patterns, each weighted by how diagnostic it is:

```java
    private static final List<Rule> DEFAULT_RULES = List.of(
            new OrEqualityRule(),
            new NamedPattern("union-select", 0.95,
                    Pattern.compile("\\bunion\\s+(?:all\\s+)?select\\b")),
            new NamedPattern("comment-marker", 0.50,
                    Pattern.compile("(?i)(?:--|#|/\\*)")),
            new NamedPattern("hex-string", 0.60,
                    Pattern.compile("(?i)\\b0x[0-9a-f]{4,}")),
            new NamedPattern("time-delay", 0.90,
                    Pattern.compile("(?i)\\b(?:waitfor\\s+delay|benchmark|pg_sleep)\\b")),
            new NamedPattern("script-tag", 0.95,
                    Pattern.compile("<\\s*script\\b[^>]*>")),
            new NamedPattern("event-handler", 0.80,
                    Pattern.compile("\\bon[a-z]+\\s*=")),
            new NamedPattern("javascript-url", 0.80,
                    Pattern.compile("\\bjavascript\\s*:"))
    );
```

Weights encode a priority: a lone comment marker (`--` appears in legit text, e.g. dates like `2026--07`) is weak; a `UNION SELECT` or a script tag is strong. The comment-marker rule is intentionally weak so it stacks with others instead of firing alone on innocent traffic.

### Step 4: The token bucket with an injectable clock

The rate limiter is a per-client token bucket. The clock is injected so the demo advances time deterministically instead of sleeping.

```java
    public interface NanoClock { long nowNanos(); }

    static final class SimClock implements NanoClock {
        private long now;
        SimClock(long startNanos) { this.now = startNanos; }
        @Override public long nowNanos() { return now; }
        void advanceSeconds(double s) { now += (long) (s * 1_000_000_000L); }
    }

    static final class TokenBucket {
        private final double capacity;
        private final double refillPerSecond;
        private final NanoClock clock;
        private double tokens;
        private long lastRefill;

        TokenBucket(double capacity, double refillPerSecond, NanoClock clock) {
            this.capacity = capacity;
            this.refillPerSecond = refillPerSecond;
            this.clock = clock;
            this.tokens = capacity;
            this.lastRefill = clock.nowNanos();
        }

        boolean tryConsume() {
            long now = clock.nowNanos();
            double elapsed = (now - lastRefill) / 1e9;
            lastRefill = now;
            tokens = Math.min(capacity, tokens + elapsed * refillPerSecond);
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }
    }
```

### Step 5: The engine — normalize, evaluate, aggregate, act

```java
    public static final class Engine {
        private final List<Rule> rules;
        private final Map<String, TokenBucket> buckets = new HashMap<>();
        private final NanoClock clock;
        private final double bucketCapacity;
        private final double bucketRefillPerSecond;

        public Engine(NanoClock clock, double bucketCapacity, double bucketRefillPerSecond) {
            this(clock, bucketCapacity, bucketRefillPerSecond, DEFAULT_RULES);
        }

        public Engine(NanoClock clock, double bucketCapacity, double bucketRefillPerSecond,
                      List<Rule> rules) {
            this.clock = clock;
            this.bucketCapacity = bucketCapacity;
            this.bucketRefillPerSecond = bucketRefillPerSecond;
            this.rules = List.copyOf(rules);
        }

        public Verdict evaluate(Request request) {
            String target = Normalizer.normalize(request.target());
            String body = Normalizer.normalize(request.body());

            double score = 0.0;
            List<String> fired = new ArrayList<>();
            for (Rule rule : rules) {
                double c = Math.max(rule.match(target), rule.match(body));
                if (c > 0) {
                    score = Math.min(1.0, score + c);
                    fired.add(rule.name());
                }
            }

            Action action = score >= 0.75 ? Action.BLOCK
                    : score >= 0.40 ? Action.CHALLENGE : Action.MONITOR;

            if (!bucket(request.clientIp()).tryConsume() && action == Action.MONITOR) {
                action = Action.CHALLENGE;   // rate violation escalates, never overrides
            }

            return new Verdict(action, score, fired);
        }

        private TokenBucket bucket(String clientIp) {
            return buckets.computeIfAbsent(clientIp,
                    ip -> new TokenBucket(bucketCapacity, bucketRefillPerSecond, clock));
        }
    }
```

Design points in the aggregation step:

- **Additive scoring**: weak signals (comment marker + hex string) stack into a challenge; strong signals block alone. A single weak rule never blocks by itself.
- **Escalation ladder**: `MONITOR → CHALLENGE → BLOCK`. A challenge (CAPTCHA) is the right answer for ambiguous automation; a hard block is reserved for high-confidence matches. Operational reality: a wrong challenge costs a conversion; a wrong block costs a customer.
- **Rate limiting escalates but never overrides**: an attack burst that also trips the limiter stays `BLOCK`; a slow attacker who happens to exceed the limit gets challenged, not blocked — the action reflects the strongest independent signal.
- **Evidence**: every verdict carries `firedRules` — the review loop that tunes weights against real traffic.

### Step 6: Demo — the verdict matrix

The demo replays a corpus of requests: legitimate traffic, classic payloads, and evasive variants. All attacks must be caught *after normalization*, and legit traffic must pass.

```java
    public static void main(String[] args) {
        SimClock clock = new SimClock(1_700_000_000_000_000_000L);
        Engine engine = new Engine(clock, /*capacity*/ 5.0, /*refill/s*/ 0.1);

        List<Request> corpus = List.of(
                new Request("GET", "/products", "category=books", "", "10.0.0.1"),
                new Request("GET", "/login", "user=admin' OR '1'='1 --", "", "10.0.0.2"),
                new Request("GET", "/search", "q=1 UNION SELECT username,password FROM users",
                        "", "10.0.0.3"),
                new Request("GET", "/search", "q=0x41424344", "", "10.0.0.4"),
                new Request("GET", "/search", "q=<script>alert(1)</script>", "", "10.0.0.5"),
                new Request("POST", "/comment", "", "<img src=x onerror=alert(1)>", "10.0.0.6"),
                new Request("GET", "/login", "user=%2527 OR 1=1 --", "", "10.0.0.7"),
                new Request("GET", "/search", "q=it's a fine day", "", "10.0.0.8"),
                new Request("GET", "/search", "q=1;WAITFOR DELAY '0:0:5'", "", "10.0.0.9"));

        System.out.println("=== WAF Rule Engine — Corpus Verdicts ===");
        for (Request request : corpus) {
            Verdict v = engine.evaluate(request);
            System.out.printf("%-6s %-16s action=%-9s score=%.2f rules=%s%n",
                    request.method(), request.path(), v.action(), v.score(), v.firedRules());
        }

        System.out.println();
        System.out.println("=== Rate Limiting (capacity 5, refill 1 per 10s) ===");
        String ip = "192.168.1.50";
        for (int i = 1; i <= 6; i++) {
            Request r = new Request("GET", "/", "page=" + i, "", ip);
            Verdict v = engine.evaluate(r);
            System.out.printf("request #%d -> %s%n", i, v.action());
        }
        clock.advanceSeconds(10.0);   // one token refilled
        Verdict afterRefill = engine.evaluate(new Request("GET", "/", "page=7", "", ip));
        System.out.printf("after 10s pause -> %s%n", afterRefill.action());
    }
}
```

### Step 7: Verify the expected outputs

| # | Request | Normalized signal | Action | Fired rules |
|---|---------|-------------------|--------|-------------|
| 1 | `category=books` | nothing | MONITOR | — |
| 2 | `admin' OR '1'='1 --` | comment stripped, then `or '1'='1` | BLOCK | or-equality |
| 3 | `1 UNION SELECT ...` | `union select` | BLOCK | union-select |
| 4 | `0x41424344` | hex literal | CHALLENGE | hex-string |
| 5 | `<script>alert(1)</script>` | script tag | BLOCK | script-tag |
| 6 | `<img ... onerror=...>` | event handler | BLOCK | event-handler |
| 7 | `%2527 OR 1=1 --` | double-decode → tautology | BLOCK | or-equality |
| 8 | `it's a fine day` | no pattern | MONITOR | — |
| 9 | `;WAITFOR DELAY '0:0:5'` | time-delay primitive | BLOCK | time-delay |
| 10 | 6th burst request from one IP | — | CHALLENGE | — (rate) |
| 11 | after 10s pause | — | MONITOR | — |

Row 7 is the demonstration of the whole point of the fixpoint loop: a single-decode pipeline sees `%27 OR 1=1` (quote encoded), never matches `or '1'='1'` as a unit... and the request is caught only because the second decode pass reveals the quote. Rows 4 and 8 show the calibration: a weak signal alone only challenges; a legit sentence with an apostrophe sails through.

---

## Complexity Analysis

- **Normalization**: O(k · L) where L is the input length and k ≤ 3 is the number of decode passes until the fixpoint (each pass strictly reduces `%` count, so k is bounded by the number of encoding levels — never the input length).
- **Rule evaluation**: O(R · L) over R rules; every rule is a single linear regex scan, no backtracking on pathological input, so per-request cost stays in the request path.
- **Aggregation**: O(R) additions, constant.
- **Rate limiting**: O(1) amortized per request via `computeIfAbsent`; total state O(C) for C distinct clients, each bucket two doubles and a timestamp.
- **Determinism**: identical input + clock ⇒ identical verdict; the demo can be replayed byte-for-byte.

---

## Follow-Up Questions

1. **How would you fuzz the normalizer?** Property: `normalize(s)` must equal `normalize(normalize(s))` (idempotence) and, for any encode-chain of `s`, `normalize(encoded(s)) == normalize(s)`. A fuzzer generating random encoding nests is the test that finds bypasses before attackers do.

2. **Where does a CSRF token fit in this engine?** CSRF is not an injection class — it is a *session* attack. The WAF can add a protocol rule (e.g., state-changing methods require the token), but the authoritative check belongs to the application; the WAF rule is a cheap second gate.

3. **How do you calibrate the weights?** Replay production traffic (sanitized) through the engine and measure false positives per rule; promote/demote weights so the aggregate score separates the human traffic distribution from the attack corpus with margin. This is why every verdict carries `firedRules`.

4. **How does this scale to a fleet of WAF instances?** The rules and normalization are stateless — replicated by config. The token buckets are the only stateful piece: move them to a shared store (Redis with sliding-window counters) or accept per-instance limits under consistent hashing of client IPs, trading accuracy for latency.

5. **What about HTTP parameter pollution?** Two `q=` values may concatenate differently at the application; the WAF should evaluate the joined value as well as each individual one — and this is exactly the class of bypass that a deterministic normalization spec prevents.

6. **Should the engine inspect responses too?** For stored XSS, response-side detection is stronger: look for *reflected* parameter values appearing in executable contexts (script, attribute). It pairs the taint source with the sink and has far fewer false positives than request-side XSS rules alone.
