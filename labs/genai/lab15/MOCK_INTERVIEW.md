# Lab 15: Mock Interview — Building a GenAI Platform

**Role**: Platform Engineer / Backend Lead
**Duration**: 60 minutes
**Focus**: model registry & versioning, API gateway routing, A/B testing, orchestrator fallbacks, rate limiting, tiered models

---

**Interviewer**: "Walk me through the lab's platform architecture."

**Candidate**: "Five components on top of the model layer. `Model` is a record — id,
name, version, costUnits — and `ModelRegistry` stores them by name, with
`getLatest` and `getVersion` lookups. `APIGateway.route` picks a model per
request: an A/B variant split when `useVariant` is on, the latest version
otherwise. `Orchestrator` walks a model chain — 'gpt-large, gpt-small' — with
explicit fallbacks, so a downed large model degrades to the small one.
`RateLimiter` is a token bucket: `maxTokens` capacity, `refillRate` tokens per
second, `tryAcquire(cost)` draining per request. `ABTestTracker` counts requests
and errors per variant and reports error rates. The demo wires them together:
two routes to gpt-small, a fallback hit, 10/15 rate-limited grants, and a 100-request
A/B report."

**Interviewer**: "What did your walkthrough reveal about the A/B routing in
`APIGateway`?"

**Candidate**: "Two bugs worth knowing. First, the gate is `requestId.hashCode() % 100
< abVariantRatio * 100`, and `String.hashCode()` returns negative values roughly
half the time — Java's `%` keeps the sign, so negative hashes are always 'less
than 50' and always route to the variant. With the lab's registered versions,
`getLatest('gpt-small')` returns v2 — the last registered — which is also the
variant, so *every* request hit v2 and the 50/50 split was 0/100 in disguise.
My walkthrough fixes both: register v2 before v1 so latest = v1 (control), and
curate request ids (usr-1000...) whose modulo is non-negative — the split then
comes out exactly 10 v1 / 10 v2. The lesson is production-grade: hash-based
splitting must use `Math.floorMod`, and registration order silently defines
'latest'."

**Interviewer**: "Walk through the `route` line: `'Routed to ' + model.name + ' v' +
model.version`. The demo prints 'gpt-small vv2'. What happened?"

**Candidate**: "A string-concatenation quirk: the version field already contains the
'v' — 'v2' — and the format prepends another 'v', producing 'vv2'. It's cosmetic,
but it's exactly the kind of thing that costs a debugging hour: the `Model` record
stores version as 'v2' while the route message assumes '2'. In the walkthrough I
keep the lab's formatting verbatim so the output matches the lab demo, and flag it
as a data-model smell — version should be stored as '2' and formatted once at the
boundary. Interview-wise, noticing it and explaining the cause is the answer
they want."

**Interviewer**: "The orchestrator: walk through the fallback execution."

**Candidate**: "`execute(request, modelEndpoints)` iterates the chain. For 'gpt-large':
its endpoint is 'ERROR', so it tries the fallback map — 'gpt-large' falls back to
'gpt-small' — whose endpoint is 'OK', so it returns '[gpt-small (fallback)]
Processed: Hello world'. If everything is down, the loop exhausts and returns 'All
models failed'. The design mirrors the INTERVIEW guide's reliability patterns:
circuit-breaker logic expressed as endpoint health, graceful degradation to a
cheaper model, and an explicit failure terminal state. The walkthrough exercises
both branches — fallback success and total failure — which is the behavior
contract any caller should test."

**Interviewer**: "Rate limiter: burst of 15 with 10 tokens grants 10, then after 2.1s
of refill at 5/s grants 10 more. Walk through the bucket."

**Candidate**: "The bucket starts full at 10. The burst of 15: `tryAcquire(1)` refills
first — negligible elapsed time, so tokens stay ~10 — and drains one per granted
request; requests 11-15 find `tokens < 1` and are denied: 10/15. Then
`Thread.sleep(2100)` lets `refill()` add `elapsed * refillRate` ≈ 10.5 seconds'
worth, capped at maxTokens 10 — so the next burst of 15 grants 10 again. The
`lastRefill` timestamp makes refill continuous rather than tick-based, which is
the difference between a toy counter and a real token bucket: it's rate-accurate
under bursty traffic. Cost-based limiting — `tryAcquire(cost)` with the model's
`costUnits` — is the natural extension: expensive models drain more tokens, which
is how the gateway enforces budget per tenant."

**Interviewer**: "The A/B report shows 6.67% vs 5.00% error rates. How do you decide
the variant wins?"

**Candidate**: "You don't, from counts alone — that's the trap. 60 vs 40 requests is
a tiny sample; the difference between 4/60 and 2/40 is within noise. Real A/B for
LLMs needs: consistent assignment (the gateway's hash split), quality metrics
beyond errors (human evals or LLM-as-judge scores, latency, token cost), a
statistical test (chi-square or a Bayesian comparison) with a pre-registered
significance level, and a rollout plan — promote if the variant clears the gate,
keep 50/50 or roll back otherwise. The lab's `ABTestTracker` is the *counter*;
the decision framework around it is the product. My walkthrough uses fixed inputs
(60 A/40 B with 4/2 errors) so the report is deterministic and the discussion
can focus on what the numbers do and don't mean."

**Interviewer**: "How does the registry support multi-model serving?"

**Candidate**: "It's the source of truth for what's deployable: register a new version,
route by name, and `getLatest` keeps old callers on the newest stable while
`getVersion` pins specific tests. Real registries add what the lab's `Model`
record hints at: the `costUnits` field is the seed of cost-aware routing (pick
the cheapest model that meets the quality bar), plus capability tags, hardware
placement, and deployment state (draining, canary — lab 14's `CanaryDeployer`
fits right here). The gateway's `route` is where it comes together: hash the
request for stable assignment, consult the registry for version, and apply cost
policy — which is exactly the walkthrough's A/B scenario at 10x scale."

**Interviewer**: "What's the platform's reliability story if the registry or gateway
itself fails?"

**Candidate**: "The platform layers are themselves services, so they need the same
patterns: the registry should be replicated with a warm standby; the gateway needs
a circuit breaker around model calls (the orchestrator's endpoint health check is
the pattern), retry with exponential backoff for transient errors, and bulkheads
per tenant so one noisy customer doesn't exhaust the token bucket for everyone.
Caching at the gateway — exact + semantic, from lab 12 — absorbs repeats before
they hit models at all, and graceful degradation returns a degraded-but-honest
response under load. The INTERVIEW guide lists exactly these: circuit breaker,
retry, bulkhead, caching, graceful degradation. The lab's `Orchestrator` fallback
chain and `RateLimiter` are two of the five in executable form."

**Interviewer**: "How does cost-quality tiering work — when does gpt-large earn its
10 costUnits?"

**Candidate**: "Route by query complexity: simple intents (summaries, extraction,
classification) go to the cheap high-volume model; complex reasoning (multi-step,
code, math) goes to the large one; ambiguous queries ride a fallback chain so a
cheap mistake escalates. The walkthrough's `costUnits` — 1, 2, 10 — make the
arithmetic concrete: 100 simple queries on gpt-small cost 100 units vs 1000 on
gpt-large, a 10x spread. Production adds the guardrails: a complexity classifier,
a budget per tenant (the rate limiter scaled by cost), and quality sampling so
cost savings don't silently degrade the experience. This lab's registry-plus-
gateway is the skeleton of that tiering."

**Interviewer**: "Final question: what's the one design decision you'd change if you
were shipping this today?"

**Candidate**: "Stable request bucketing. The hash-split routing is deterministic per
request id — which is good for A/B assignment consistency — but it's silently
broken by negative hashcodes (my walkthrough shows the 10x overshoot in lab 14's
canary and the 0/100 split here). I'd swap `%` for `Math.floorMod`, make the
bucket function a single auditable utility used by both gateway and canary, and
add a distribution test to CI — every deploy re-verifies that the split is
actually 50/50. The rest of the lab is sound architecture; that one arithmetic
detail is where correctness hides."
