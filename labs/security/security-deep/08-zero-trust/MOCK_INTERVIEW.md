# Mock Interview: Zero-Trust Policy Decision Engine with MFA Verification

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Security Engineer (Zero Trust Architecture Team)
**Candidate Level**: Senior Engineer
**Focus Area**: Policy engines, context-aware authorization, step-up MFA, continuous verification
**Problem**: Implement a policy decision point (PDP) that evaluates access requests against policies, issues MFA challenges when policy demands, verifies TOTP codes, and re-evaluates trust on every request (continuous verification).
**Language**: Java 21+ (records, no external libs)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. What is a PDP/PEP/PA and where does the decision happen?
2. How do you express "allow, but only with MFA" as a policy rule?
3. Why is trust a continuous signal, not a login-time boolean?
4. What is step-up authentication and when is it triggered?
5. How do you stop a replay of the MFA code?
6. Follow-up: attribute freshness, risk scoring, policy-as-code.

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "Trust no one by default: every request is verified. We need a policy engine that decides allow/deny per request, forces MFA when the policy says so, verifies the code, and re-verifies continuously. Scope it."

**Candidate**: "I'll build the **PDP** — policy decision point — as the heart, with three contracts. (1) `evaluate(request) -> Decision`: subject attributes (user, groups, device compliance, risk score) + resource + action + context (network trust, hour) → allow or deny, possibly *with* a required MFA step-up. (2) **MFA verification**: the PDP issues a challenge (a TOTP-style 6-digit code with a time window), and `verify(subject, code)` must be replay-safe — a code validates at most once, and it expires. (3) **Continuous verification**: risk isn't computed at login and forgotten; each request recomputes a risk score from context — untrusted network, unusual hour, high-sensitivity resource — and a session that drifts past the policy's risk threshold gets a *fresh* MFA challenge even though it already authenticated."

**Interviewer**: "What stays out of scope?"

**Candidate**: "Identity providers, device agents, and the crypto of TLS mutual auth — the engine consumes their verdicts as attributes. And the PEP (the enforcement point that carries the decision into the data plane) I'll model only as a demo that consumes PDP output."

### Part 2: Theory — Policy Model (8 minutes)

**Interviewer**: "Define the policy rule shape."

**Candidate**: "A rule binds five dimensions: which **subjects** (groups, device-compliance requirement), which **resources** (pattern or exact ID), which **actions** (read/write/admin), **what context** must hold (trusted network, working hours), and **what verification** it demands — an `mfaRequired` flag plus a `maxRisk` threshold. A request matches a rule when all its attribute constraints hold; evaluation is *first-match-wins over an ordered rule list* with an implicit final **deny-all** — zero trust means the absence of a rule is a deny, never an allow."

**Interviewer**: "Allow-with-MFA — how does that surface in a decision?"

**Candidate**: "Three decision kinds, not two: `ALLOW`, `DENY`, and `ALLOW_WITH_MFA` — a *conditional allow* that the PEP must convert into a step-up before the data plane opens. The policy engine doesn't check MFA itself; it returns a challenge and the session state tracks whether it was satisfied. That split is what makes step-up testable: the same rule set governs first login and the session's later risk-driven re-challenge."

### Part 3: Theory — MFA and Replay (8 minutes)

**Interviewer**: "The challenge/verify contract."

**Candidate**: "Challenge: a 6-digit code derived from a per-session secret and a 30-second time window — TOTP in essence — plus an expiry and a `used` flag. Verify: constant-time code comparison, and the code must be **single-use**: the first successful verify consumes it, so a captured code can't be replayed by the attacker five seconds later. Time-window skew tolerance: I'll accept `t-1, t, t+1` windows — the standard TOTP margin — but each window's code is still consumed once. The demo shows: correct code → session upgraded; same code replayed → denied; stale code (past expiry) → denied."

**Interviewer**: "Why does one-time-ness matter more than secrecy here?"

**Candidate**: "Because codes are short and screenshots happen. Secrecy is amortized by the 30-second window; the *one-time* property is what turns a 6-digit secret into a capability you can't borrow after first use. If a code validates forever, the attacker who saw the partner's authenticator screen once is authenticated for the whole session."

### Part 4: Theory — Continuous Verification (8 minutes)

**Interviewer**: "Explain risk scoring and the re-auth loop."

**Candidate**: "Each request recomputes `risk(subject, context, resource)`: baseline per user (trusted group, compliant device) minus penalties — untrusted network +40, outside working hours +15, high-sensitivity resource +20, first request from a new device +25 — clamped to [0,1]. A session records `(lastMfaAt, lastRisk)`. The decision loop: evaluate policy; if the matched rule demands MFA and the session's MFA is stale or absent → step-up challenge; if the session's *current* risk exceeds the rule's `maxRisk` → **re-challenge**: the previously verified MFA stops counting, because trust decayed. That's continuous verification: *authentication is a claim about a moment, not a property of the day.*"

**Interviewer**: "What prevents a storm of step-up prompts?"

**Candidate**: "A grace period: after a successful re-auth the risk is reset to baseline and the policy's `maxRisk` won't trip again for a comparable request — and the demo includes a session whose risk returns to baseline to show the loop stabilizes rather than thrashing."

### Part 5: Implementation (15 minutes)

**Interviewer**: "Code the decision core."

**Candidate**: "Records: `Subject(user, groups, deviceCompliant)`, `Resource(id, sensitivity)`, `Request(subject, resource, action, context)`, `Context(trustedNetwork, hour)`, `PolicyRule(...)`, `Decision(allow, reason, challengeToken)`, `Session(user, lastRisk, mfaVerified, mfaExpiry)`. The engine: rule match → risk compute → MFA gate → decision; the MFA service: `challenge(session)` / `verify(session, code)` with the used-set. The demo main scripts the four stories: plain allow; deny by group; step-up on first high-sensitivity access; risk-drift re-challenge mid-session."

### Part 6: Testing (5 minutes)

**Interviewer**: "Test plan?"

**Candidate**: "Policy: employee+compliant on `read` of medium sensitivity → ALLOW without MFA; analyst on `admin` → DENY (group rule); high-sensitivity `write` → ALLOW_WITH_MFA, challenge issued. MFA: correct code within window → verified; replay → denied; wrong code → denied; expired code → denied. Risk: trusted network + business hours keeps risk under threshold; untrusted network spikes it → the second request gets a step-up despite prior verification; after re-auth, risk reset → third request allows again."

---

## Extended Q&A: Follow-up Round

**Q: Why is zero trust "verify every request" rather than "verify once"?**

**A**: Because trust attributes decay: a device can be compromised after login, a session token can be stolen, a network can turn hostile. The PDP's per-request risk recompute makes decay visible — step 4 of the demo (same user, hostile network, 2am) is exactly that story. Sessions with unbounded trust are precisely what zero trust abolishes.

**Q: What is the PDP/PEP/PA split, and why does it matter?**

**A**: PDP = policy decision point (this lab: pure logic, no enforcement); PEP = policy enforcement point (the gateway or service that carries the verdict into the data plane and enforces step-up); PA = policy administration point (the UI/API that manages rules). Separation lets the PDP be unit-tested and audited without touching enforcement, and lets enforcement be uniform across every service — one decision engine, many enforcement points.

**Q: How do you avoid re-prompting the user on every request?**

**A**: Session trust with a freshness bound: the decision is per-request, but the MFA requirement is per-verification. A verified session at low risk passes for hours; a risk spike invalidates instantly. The risk-reset after a successful re-verify is the mechanism that stops prompt thrashing — the demo's loop stabilizes rather than locking the user out.

**Q: What does device attestation add beyond a compliance boolean?**

**A**: Evidence — a signed report (TEE quote, platform attestation) proving device state at a moment, instead of a self-reported flag. The PDP treats attestation freshness as an attribute: stale attestation raises risk. That is the difference between "my device asserts it is patched" and "my device proved it".

**Q: Where do decisions get cached, and what is the danger?**

**A**: PEP-side decision caching with TTL is standard for latency, but a cached ALLOW can outlive a revocation or a risk spike. The trade-off is TTL against trust: short TTLs for sensitive actions, plus a revocation event channel so the cache cannot resurrect a dead session.

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Policy model | Multi-dimensional rules, ordered, deny-by-default | Allow/deny flags | Hardcoded checks |
| Decision kinds | ALLOW / DENY / ALLOW_WITH_MFA | Two-way | None |
| MFA | TOTP window, single-use, constant-time | Static password | None |
| Continuous verification | Per-request risk recompute + re-challenge | Login-time only | None |
| Tests | Replay, drift, grace corpus | Happy path | None |

## Red Flags
- Trust decided once at login and never revisited.
- MFA codes that verify more than once (replay wins).
- Rule list with an implicit allow-all tail.
- Risk scoring that can't change per request (the whole point).

## Key Takeaways
- PDP evaluates; PEP enforces; the decision has three states, not two.
- MFA: time-windowed, single-use, constant-time — replay is the threat.
- Continuous verification: risk recomputed per request; trust decays; step-up follows.
- Zero trust = deny-by-default, and a rule's *absence* is a deny.

## Glossary

- **PDP / PEP / PA** — policy decision point (this lab), policy enforcement point (the data plane), policy administration point (rule management).
- **Step-up** — a conditional allow converted into a fresh authentication requirement.
- **TOTP** — time-based one-time password: a 6-digit code derived from a secret and a 30-second window.
- **Single-use** — a code validates at most once; replay of a captured code fails.
- **Risk score** — the per-request recomputation of trust in [0,1] from context and state.
- **Continuous verification** — re-evaluating trust on every request, not at login only.
- **Trust decay** — the fact that risk rises (and MFA expires) as a session ages or context degrades.
- **Attestation** — signed device evidence (TEE quote, platform proof) instead of a self-reported flag.
- **Deny-by-default** — the invariant that a request with no matching rule is denied.
- **Session TTL** — the freshness bound on an MFA verification; after it, step-up is required again.
- **Revocation channel** — the event stream that invalidates cached decisions after a burn.
- **Baseline risk** — the user's steady-state score from group and compliance attributes.
- **Freshness** — how recent an attestation or verification is; stale means re-verify.
- **Risk threshold** — the rule's maxRisk bound; crossing it invalidates session MFA.
- **Phishing-resistant credential** — a hardware-backed factor (FIDO2/WebAuthn) that can't be replayed by a fake site.
- **Decision cache** — PEP-side caching of PDP verdicts, bounded by TTL and revocation events.
- **Attribute provider** — the feed (IdP, MDM, SIEM) that supplies the PDP's subject and context data.
- **HOTP** — HMAC-based one-time password; TOTP is HOTP with the counter replaced by the time window.
- **Risk reset** — the return to baseline after a successful step-up, which stops prompt thrashing.
