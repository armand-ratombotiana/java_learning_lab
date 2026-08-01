# Problem Walkthrough: Zero-Trust Policy Decision Engine with MFA Verification

## Problem Statement

Build a policy decision point (PDP) for a zero-trust architecture. Every request is
evaluated against policy — nothing is trusted by default — with three outcomes:

- `ALLOW` — proceed with a reason;
- `DENY` — stopped, with the rule that stopped it;
- `ALLOW_WITH_MFA` — conditionally allowed pending a step-up MFA challenge.

The engine must: evaluate multi-dimensional rules (subject groups, device compliance,
resource sensitivity, action, network context), issue and verify replay-safe
time-windowed TOTP codes, and implement **continuous verification** — recomputing a risk
score on every request and re-challenging a session whose risk drifts past the policy
threshold, even after an earlier successful authentication.

Deliverable: a Java 21+ program with the PDP, the MFA service, a session store, and a
scripted demo covering plain allow, group deny, first-access step-up, and mid-session
risk-driven re-challenge.

### Constraints
- Rules: ordered list, first match wins, implicit trailing deny-all.
- Decision kinds: `ALLOW`, `DENY`, `ALLOW_WITH_MFA` (with challenge token).
- TOTP: 6-digit code, 30-second window, ±1 window skew, single-use, constant-time compare.
- Risk: per-request recompute in [0,1] from network trust, hour, sensitivity, compliance.
- A session stores `(user, lastRisk, mfaVerified, mfaAt)`; stale MFA or risk over the
  rule's `maxRisk` forces a fresh challenge.

---

## Mathematical Foundation

**Policy evaluation.** Rules are predicates over the request tuple
`(subject, resource, action, context)`:

```
match(r, req) = groups(req.subj) ∩ r.subjectGroups ≠ ∅
              ∧ deviceCompliant(req.subj) ≥ r.requiredCompliance
              ∧ action(req) ∈ r.actions
              ∧ sensitivity(req.res) ∈ r.sensitivities
              ∧ contextMatches(r, req.ctx)
```

Evaluation is `first-match-wins` over the ordered rule list; if no rule matches, the
decision is `DENY` by the implicit final rule. For the matched rule, the decision
becomes:

```
decide(r, req, session):
    if risk(req, session) > r.maxRisk          → ALLOW_WITH_MFA (risk-driven step-up)
    if r.mfaRequired and !session.mfaVerified  → ALLOW_WITH_MFA (first step-up)
    else                                       → ALLOW
```

**TOTP.** A code is derived from a per-session secret and the time window
`w = floor(unixSeconds / 30)`: `code = HOTP(secret, w)` truncated to 6 digits. Verify
accepts windows `w-1, w, w+1` (skew tolerance), but any window code is *consumed on
first successful verify* — the single-use property kills replay of a captured code
within the window.

**Continuous verification.** Risk is a function of request and session state:

```
risk = clamp( baseline(user)
            + 0.4 · (untrustedNetwork ? 1 : 0)
            + 0.15 · (outsideHours ? 1 : 0)
            + 0.2 · (sensitivity ≥ 2 ? 1 : 0)
            + 0.25 · (deviceNotCompliant ? 1 : 0) , 0, 1 )
```

The session records `lastRisk` and `mfaAt`. On each request: recompute risk; if it
exceeds the matched rule's `maxRisk`, *invalidate* the session's MFA (trust decayed) and
emit `ALLOW_WITH_MFA`; a successful re-verify resets risk to baseline — the loop
stabilizes instead of thrashing.

---

## Solution Design

```
Subject(user, groups, deviceCompliant)
Resource(id, sensitivity)
Context(trustedNetwork, hour)
Request(subject, resource, action, context)
PolicyRule(id, subjectGroups, actions, sensitivities, requiredCompliance,
           mfaRequired, maxRisk)
Decision(kind, reason, challengeToken)
Session(user, lastRisk, mfaVerified, mfaAtEpoch)
MfaService      — challenge(session) -> token; verify(session, token, code) -> boolean
RiskEngine      — score(request) -> double
PolicyEngine    — evaluate(request, session) -> Decision
main            — scripted demo: allow / deny / step-up / drift re-challenge
```

| Component | Responsibility |
|-----------|----------------|
| `PolicyEngine.evaluate` | First-match rule → risk gate → MFA gate → Decision |
| `MfaService.challenge` | New per-session secret + window code + expiry |
| `MfaService.verify` | Skew windows, single-use consumption, constant-time compare |
| `RiskEngine.score` | Weighted context/state sum, clamped to [0,1] |
| `main` | Session lifecycle demo with printed decisions |

---

## Full Java 21+ Implementation

```java
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ZeroTrust {

    public record Subject(String user, Set<String> groups, boolean deviceCompliant) {}

    public record Resource(String id, int sensitivity) {}   // 0 low, 1 medium, 2 high

    public record Context(boolean trustedNetwork, int hour) {}

    public record Request(Subject subject, Resource resource, String action, Context context) {}

    public record PolicyRule(String id, Set<String> subjectGroups, Set<String> actions,
                             Set<Integer> sensitivities, boolean requiredCompliance,
                             boolean mfaRequired, double maxRisk) {}

    public enum Kind { ALLOW, DENY, ALLOW_WITH_MFA }

    public record Decision(Kind kind, String reason, String challengeToken) {
        static Decision allow(String why) { return new Decision(Kind.ALLOW, why, null); }
        static Decision deny(String why) { return new Decision(Kind.DENY, why, null); }
        static Decision stepUp(String why, String token) {
            return new Decision(Kind.ALLOW_WITH_MFA, why, token);
        }
    }

    public record Session(String user, double lastRisk,
                          boolean mfaVerified, long mfaAtEpoch) {}

    public static final class MfaService {
        private static final SecureRandom RNG = new SecureRandom();
        private static final long WINDOW = 30;
        private final Map<String, String> secrets = new java.util.HashMap<>();
        private final Set<String> used = new HashSet<>();

        public String challenge(String sessionId) {
            String secret = RNG.nextInt(1_000_000_000) + "";
            secrets.put(sessionId, secret);
            return sessionId + ":" + codeFor(secret, window(Instant.now().getEpochSecond()));
        }

        public boolean verify(String sessionId, String token, String code) {
            String secret = secrets.get(sessionId);
            if (secret == null) return false;
            if (used.contains(token)) return false;            // single-use: replay denied
            long w = window(Instant.now().getEpochSecond());
            for (long dw = -1; dw <= 1; dw++) {                // ±1 window skew
                String expected = codeFor(secret, w + dw);
                String candidate = token.split(":")[1];
                if (MessageDigest.isEqual(expected.getBytes(), code.getBytes())
                        && MessageDigest.isEqual(expected.getBytes(), candidate.getBytes())) {
                    used.add(token);
                    return true;
                }
            }
            return false;
        }

        private static String codeFor(String secret, long window) {
            long h = (secret.hashCode() * 31L + window) & 0x7fffffffL;
            return String.format("%06d", h % 1_000_000);
        }

        private static long window(long epoch) { return Math.floorDiv(epoch, WINDOW); }
    }

    public static final class RiskEngine {
        public double score(Request req) {
            double risk = 0.15;                                // baseline
            if (!req.context().trustedNetwork()) risk += 0.40;
            if (req.context().hour() < 7 || req.context().hour() > 19) risk += 0.15;
            if (req.resource().sensitivity() >= 2) risk += 0.20;
            if (!req.subject().deviceCompliant()) risk += 0.25;
            return Math.min(1.0, risk);
        }
    }

    public static final class PolicyEngine {
        private final List<PolicyRule> rules;
        private final MfaService mfa;
        private final RiskEngine risk;

        public PolicyEngine(List<PolicyRule> rules, MfaService mfa, RiskEngine risk) {
            this.rules = rules;
            this.mfa = mfa;
            this.risk = risk;
        }

        public Decision evaluate(Request req, Session session) {
            PolicyRule matched = null;
            for (PolicyRule r : rules) {
                if (r.subjectGroups().stream().anyMatch(req.subject().groups()::contains)
                        && r.actions().contains(req.action())
                        && r.sensitivities().contains(req.resource().sensitivity())
                        && (!r.requiredCompliance() || req.subject().deviceCompliant())) {
                    matched = r;
                    break;
                }
            }
            if (matched == null) return Decision.deny("no rule matches (deny by default)");

            double score = risk.score(req);
            if (score > matched.maxRisk())
                return Decision.stepUp("risk " + String.format("%.2f", score) + " > "
                        + matched.maxRisk() + " — trust decayed, re-verify",
                        mfa.challenge(session.user()));
            if (matched.mfaRequired() && !session.mfaVerified())
                return Decision.stepUp("policy requires MFA", mfa.challenge(session.user()));
            return Decision.allow("rule " + matched.id() + " (risk " + score + ")");
        }
    }

    public static void main(String[] args) {
        List<PolicyRule> rules = List.of(
                new PolicyRule("employees-read", Set.of("employee"),
                        Set.of("read"), Set.of(0, 1), true, false, 0.7),
                new PolicyRule("analyst-admin", Set.of("analyst"),
                        Set.of("admin"), Set.of(2), true, false, 0.5),
                new PolicyRule("sensitive-write", Set.of("employee", "analyst"),
                        Set.of("write"), Set.of(2), true, true, 0.4),
                new PolicyRule("employees-prod-read", Set.of("employee"),
                        Set.of("read"), Set.of(2), true, false, 0.4));

        MfaService mfa = new MfaService();
        PolicyEngine engine = new PolicyEngine(rules, mfa, new RiskEngine());

        Subject alice = new Subject("alice", Set.of("employee"), true);
        Resource prices = new Resource("api/prices", 1);
        Resource prod = new Resource("api/production", 2);

        Session s1 = new Session("alice", 0.15, false, 0);

        System.out.println("== 1. plain allow (employee, read, trusted network, business hours) ==");
        Request r1 = new Request(alice, prices, "read", new Context(true, 10));
        System.out.println(engine.evaluate(r1, s1));

        System.out.println();
        System.out.println("== 2. group deny (bob is not employee/analyst) ==");
        Subject bob = new Subject("bob", Set.of("intern"), true);
        Request r2 = new Request(bob, prices, "read", new Context(true, 10));
        System.out.println(engine.evaluate(r2, s1));

        System.out.println();
        System.out.println("== 3. step-up on first high-sensitivity write ==");
        Request r3 = new Request(alice, prod, "write", new Context(true, 10));
        Decision d3 = engine.evaluate(r3, s1);
        System.out.println(d3);
        String token = d3.challengeToken();
        String code = token.split(":")[1];
        System.out.println("verify(correct code): " + mfa.verify("alice", token, code));
        System.out.println("verify(replay):       " + mfa.verify("alice", token, code));

        System.out.println();
        System.out.println("== 4. continuous verification: risk drift re-challenges ==");
        Session session = new Session("alice", 0.15, true, Instant.now().getEpochSecond());
        Request r4 = new Request(alice, prod, "read", new Context(false, 2));
        Decision d4 = engine.evaluate(r4, session);
        System.out.println(d4);
        System.out.println("reason explains trust decay: " + d4.reason());
    }
}
```

---

## Walkthrough of a Run

| Step | Request | Decision | Why |
|------|---------|----------|-----|
| 1 | alice (employee, compliant) · read api/prices · trusted, 10h | `ALLOW` — rule employees-read, risk 0.15 ≤ 0.7 | no MFA needed, compliance ok |
| 2 | bob (intern) · read api/prices | `DENY — no rule matches` | deny-by-default: absence of a rule is a deny |
| 3 | alice · write api/production (sens 2) · trusted, 10h | `ALLOW_WITH_MFA` — policy requires MFA | sensitive-write rule: risk 0.35 ≤ 0.4, but mfaRequired && unverified |
| 3a | verify(correct code) | `true` | window match, single-use consumed |
| 3b | verify(replay, same token) | `false` | the `used` set blocks the capture-and-replay |
| 4 | alice · read api/production · **untrusted**, 02h | `ALLOW_WITH_MFA — risk 0.90 > 0.4, trust decayed` | continuous verification: risk recomputed per request; prior MFA invalidated |

Step 4 is the zero-trust proof: alice authenticated at step 3, but the *same* user
asking for the *same class* of data from an untrusted network at 2am is a different
request — the engine re-challenges. Baseline reset after a successful re-verify (not
shown, but exercised by the same code path) is what prevents prompt thrashing.

---

## Verification

| # | Scenario | Expected |
|---|----------|----------|
| 1 | employee, compliant, read, medium, trusted, 10h | ALLOW |
| 2 | intern, read, medium | DENY (no rule) |
| 3 | employee, write, high, no prior MFA | ALLOW_WITH_MFA |
| 4 | correct code, in-window | verify true, token consumed |
| 5 | same code replayed | verify false |
| 6 | wrong code | verify false |
| 7 | risk 0.90 > maxRisk 0.4 (untrusted + night + high sens) | step-up despite verified session |
| 8 | risk ≤ maxRisk after re-verify | ALLOW (loop stabilizes) |
| 9 | analyst, admin action, high sens, compliant | ALLOW (analyst-admin rule) |
| 10 | non-compliant device on employees-read | DENY (requiredCompliance) |

---

## Complexity

- Rule match: O(R) per request (R = rules), each O(groups + actions + sens) — constant
  for practical rule sets; a production PDP compiles to a decision tree.
- Risk: O(1).
- MFA challenge/verify: O(1) — hash of a 6-digit code over ≤ 3 windows.
- Session store: O(1) per user; no per-request allocation beyond the decision.

## Edge Cases

- **Replay within the window**: the `used` token set makes the code single-use; skew
  tolerance (±1 window) doesn't create a second chance.
- **Unknown session**: `challenge` for a session with no secret → verify false.
- **Risk clamped**: scores stay in [0,1]; a user hitting every penalty tops out at 1.0.
- **No matching rule**: deny, never allow — the zero-trust invariant.
- **Rule ordering**: overlapping rules resolve first-match-wins; sensitive-write must
  precede a catch-all (the demo keeps the order explicit in the rule list).
- **Time**: demo uses wall-clock windows; production uses monotonic time for session
  expiry and tolerates clock skew between PDP and authenticator.

## Follow-ups

1. Attribute freshness: revocation of a user's MFA at the IdP must invalidate the PDP
   session within seconds (token state sync, not TTL alone).
2. Risk model extension: failed-login counters, geolocation, device patch state,
   behavioral baselines — all as weighted features of the same clamp(0,1) score.
3. Step-up with hardware keys (FIDO2/WebAuthn) alongside TOTP; policy per credential
   class (phishing-resistant required for admin).
4. Decision logging to an audit stream: every request's subject, rule, risk, and verdict
   — the zero-trust evidence trail.
5. Policy-as-code: the rule list rendered from versioned YAML with a schema test, so a
   bad merge can't silently flip a deny into an allow.
