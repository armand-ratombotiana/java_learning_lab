# Mock Interview — Web Application Firewall (WAF) & Network Security

*Transcript of a senior-level interview. The interviewer drives from breadth into depth, ending with a design exercise.*

---

## Opening

**Interviewer:** Walk me through your experience with network security. Where did you spend the most time?

**Candidate:** Most recently I owned the security posture for a customer-facing web platform — about 40 services behind a CDN. The areas I personally worked on were the WAF rules (SQL injection and XSS detection), the rate-limiting layer, and the DDoS posture: what gets scrubbed at the edge versus what the application must absorb. I also ran the tabletop exercises for attack response, which is where the detection-quality questions get real.

**Interviewer:** Good. Let's start with fundamentals. What does a WAF actually protect, and what does it *not* protect?

**Candidate:** A WAF protects the HTTP layer: it inspects requests for application-layer attacks — SQL injection, cross-site scripting, path traversal, command injection, CSRF abuse, and bot traffic. What it does not do is fix the underlying application. If you have a vulnerable endpoint, the WAF is a compensating control in front of it, not a cure. It also does nothing for attacks that never reach HTTP — DNS, BGP, volumetric layer-3 floods. And it cannot see inside encrypted traffic unless you terminate TLS at the WAF or use a private-key integration, which many teams won't do.

---

## SQL Injection Deep Dive

**Interviewer:** Design a SQL injection detector. What makes one "good"?

**Candidate:** Three properties: coverage, precision, and cost. Coverage means catching the real payloads — the tautologies like `OR 1=1`, stacked queries, comment tricks, and the modern polymorphic variants. Precision means not flagging legitimate traffic — this is the hard part, and the reason I prefer layered detection. Cost means the inspection has to sit in the request path, so every rule must be cheap: no expensive regex on the full body if a fast prefix scan can reject most traffic first.

My default structure is a rule engine where each rule has three phases: a cheap prefilter (token or character-class scan), a deterministic pattern check, and an optional context check (is the payload actually in a place where it can reach the database?).

**Interviewer:** Let's pin down a concrete payload class. How do you detect `1' OR '1'='1` without tripping on normal text?

**Candidate:** The key insight is that SQLi detection is about *structure*, not keywords. `OR '1'='1` is a comparison whose operands are equal *by construction* — the string is self-consistent. So my detector tokenizes the decoded value and looks for a boolean-equality pattern: `OR <lit> = <lit>` or `OR <lit> = <lit>` variants with quotes, where the two literals compare equal. That structural pattern is what survives obfuscation.

That said, I keep a fallback rule list for the classic signatures, because structural parsing misses payloads that rely on database-specific behavior — comment markers like `--` and `#`, `UNION SELECT`, `WAITFOR DELAY` for timing, hex-encoded strings like `0x414141`.

**Interviewer:** What about encoding? `OR 1=1--` URL-encoded, double-encoded, Unicode variants?

**Candidate:** That's the evasion problem, and it's why normalization is phase one of the pipeline. I normalize in a fixed order: URL-decode, then normalize whitespace, collapse comments (`/*...*/` and `--`), lowercase for matching, and unify quote characters. Then — this is critical — I *re-run* the normalization once, because attackers double-encode: `%2527` decodes to `%27` which decodes to `'`. You have to reach a fixpoint.

The bigger point: normalization and detection are two stages, and both have to be deterministic or you get false negatives that are impossible to debug. We test every encoding variant against a corpus of real attacks.

---

## XSS and the False-Positive Problem

**Interviewer:** Now XSS. How is detecting stored XSS in a request different from detecting it in a response?

**Candidate:** Completely different problems. Request-side XSS detection looks for `<script>` tags, event handlers, `javascript:` URLs — but those same strings appear in perfectly legitimate posts from developers pasting code snippets, and in CSS, and in markdown. The precision problem is brutal.

Response-side detection (what I prefer for stored XSS) looks for *reflected input in an executable context*: the request parameter value appears in the response HTML inside a script context or an attribute that can break out. That's the taint-tracking view: source (parameter) → sink (executable context). It has far fewer false positives because it's conditional on both sides.

**Interviewer:** How do you handle the case where the application is *supposed* to allow rich HTML — a comment system with bold and images?

**Candidate:** That's an allowlist-vs-blocklist question. The right answer for rich content is a sanitizer allowlist, not a WAF blocklist: a parser that removes everything not in the allowed set — tags like `b`, `i`, `a` with href limited to http/https, plus attribute stripping. The WAF can't do that reliably because it doesn't understand the app's escaping context. So my answer in the design would be: WAF handles the generic attack classes and the volume problem; the app's sanitizer handles context-sensitive allowlisting; and the WAF rule for script tags is still valuable as defense in depth at the perimeter.

---

## The Design Exercise

**Interviewer:** Let's design a WAF rule engine for a service that gets 50k req/s at peak, behind a CDN. Sketch the pipeline.

**Candidate:**

1. **Pre-filter stage**: cheapest checks first — URI length, header anomalies, known-bad request methods, obvious patterns. 90% of traffic exits here with one or two character scans.
2. **Normalization stage**: decode + canonicalize to a fixpoint, as I described.
3. **Rule evaluation**: rules organized by phase — injection rules, XSS rules, traversal rules, protocol rules. Each rule returns match + confidence.
4. **Aggregation and action**: scores accumulate per request and per client; actions escalate: `monitor → rate-limit → block → challenge (CAPTCHA)`. Never jump straight to block on a single low-confidence match.
5. **Feedback**: every block writes a sample to a review queue; humans review; rules are promoted or retired. This loop is what keeps false-positive rates low over time.

**Interviewer:** How do you decide between block and challenge?

**Candidate:** The distinction is between malicious intent and automation. A challenge (CAPTCHA or JS proof-of-work) stops bots but lets a confused human through — that's the right action for ambiguous cases. Blocking with a hard 403 is for high-confidence matches or repeat offenders. There's also the risk-based tier: if the same client has accumulated three medium-confidence matches in a short window, escalate to challenge even if no single rule fired with high confidence.

**Interviewer:** And how do you protect the WAF itself?

**Candidate:** The WAF must never be the bottleneck or the single point of failure. Operationally: it runs in active-active pairs with a dedicated health-check path that bypasses rules; there's a kill-switch — a config flag that makes it pass-through — for the incident where a false-positive rule is blocking legitimate traffic at scale; and the rule store is versioned and canary-deployed. Rate-limit state is shared across instances via a backing store so a distributed attacker can't just spray across replicas.

**Interviewer:** Rate limiting looks easy and is usually done badly. What's the hard part?

**Candidate:** The hard parts are identity and accuracy. Identity: what is a "client" — IP, device fingerprint, session? Attackers rotate IPs, so per-IP limits alone are weak; I key on a composite (IP + fingerprint + TLS fingerprint) and aggregate at multiple scopes: per-client, per-endpoint, per-cidr. Accuracy: the limit has to be calibrated to the *legitimate* traffic distribution, which is bursty. A fixed window of 100 req/min either blocks the legit burst or lets the attacker through. Token bucket with a burst parameter tuned per endpoint, plus adaptive limits that shrink under sustained load, is my standard answer.

---

## Wrap-Up

**Interviewer:** What's the most common mistake teams make with WAFs, and what's the most common interview answer you'd correct?

**Candidate:** The most common operational mistake is treating the WAF as a set-and-forget: rules deployed once, never tuned, false positives discovered by angry customers. A WAF without a review loop decays into either a noise generator or a disabled control. The most common technical mistake in interviews is answering detection with a single giant regex — regex is one tool in the pipeline, and the pipeline is: normalize → tokenize → detect → aggregate → act.

**Interviewer:** Final question — how would you measure whether your WAF is actually working?

**Candidate:** Three metrics, in order: **precision** (false-positive rate — a blocked legitimate request costs more than a missed attack in most businesses), **coverage** (detection rate against a replayed attack corpus — we maintain one and re-run it on every rule change), and **latency overhead** (the 50th and 99th percentile added by inspection — this gates everything else, because a slow WAF gets turned off). Plus one operational metric: mean time to tune a rule after a review-cycle report.

---

## What the Interviewer Was Looking For

- **Pipeline thinking**: normalization, multi-stage evaluation, escalation — not a single regex.
- **Precision awareness**: knowing where false positives come from and how to contain them (challenge vs block, review loop).
- **Evasion knowledge**: encoding fixpoints, structural detection, normalization order.
- **Operational maturity**: kill-switch, canary rules, shared rate-limit state, calibration against real traffic.

## Common Mistakes Candidates Make

- Jumping straight to "block everything that matches" — no escalation ladder.
- Ignoring decoding normalization until asked about evasion.
- Confusing the WAF's job with the application sanitizer's job.
- Describing per-IP rate limiting as a complete answer.
