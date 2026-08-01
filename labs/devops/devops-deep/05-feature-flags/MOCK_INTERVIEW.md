# Lab 05: Mock Interview — Feature Flags Deep Dive

**Role**: Platform / DevOps Engineer
**Duration**: 60 minutes
**Focus**: Flag architecture, targeting and rollout strategies, lifecycle, kill switches, testing

---

**Interviewer**: "What problem do feature flags actually solve — isn't it just a fancy if
statement?"

**Candidate**: "An if statement is the end state; flags solve the problems around it. Deploy
independence: shipping code that isn't active yet, so deploys don't equal releases — you
deploy
the code on Monday and turn it on for 1% on Wednesday. Safe rollouts: gradual ramp with
instant
rollback — flip a kill switch instead of rolling back a deploy, which is minutes vs. a
redeploy.
Targeted exposure: internal users first, then beta, then region-by-region. And
experimentation:
flags are how you run A/B tests cleanly. The if statement is the mechanical part; the
value is
the operational layer — evaluation, targeting, lifecycle, and the team discipline around
it."

**Interviewer**: "Walk me through the evaluation pipeline for a single flag."

**Candidate**: "First, the kill switch check — if the operator hard-disabled the flag, return the
default and stop; that's the emergency path and it must be first. Then load the flag
definition
and its targeting rules. If there are no rules, return the default variation. If there
are
rules, evaluate them against the request context — user id, environment, attributes —
and
combine them: AND semantics means every rule must pass, first-match means the first
passing
rule wins. The result is a boolean variation. Two properties matter more than the
mechanics:
evaluation is deterministic per user — same user, same result, no randomness — and
evaluation
is local, a library call with a cached config, not a network round trip to a flag
service."

**Interviewer**: "How do you do a percentage rollout that stays sticky for a user?"

**Candidate**: "Consistent bucketing: hash a stable identifier — the user id, or tenant id for
multi-tenant systems — into a bucket from 0 to 99, using a salt per flag so different
flags
don't correlate, then the user is in the rollout if the bucket is below the target
percentage.
The gotchas: never use Java's hashCode across versions — it isn't stable, so users would
jump
buckets on a JVM upgrade; use a stable hash like SHA-256 truncated, or a proper
consistent
hashing scheme; and for gradual ramps, only ever increase the percentage, because
lowering it
evicts users who already saw the feature. When the rollout hits 100, the sticky logic
should
retire — keep the bucket logic only for rollbacks."

**Interviewer**: "When should a flag be removed?"

**Candidate**: "As soon as the code path it guards is proven and stable — the industry standard
is 'flags are temporary; permanent flags are the exception'. The lifecycle: create,
rollout,
verify, then remove the flag and the dead branch in the same change, before the flag
surface
becomes unmanageable. Real teams schedule flag cleanup — a review every sprint or a
flag-aging report showing flags older than 90 days. The cost of not cleaning: dead code
branches that nobody can reason about, flags whose semantics nobody remembers, and an
evaluation surface that's an audit and security liability. Operational toggles that must
stay —
like a global rate-limit switch — are the rare permanent exception, and they should be a
separate category from feature rollouts."

**Interviewer**: "What's the difference between a flag and a configuration value?"

**Candidate**: "It's a spectrum, but the operational distinction matters. Config is a static
setting the app reads at startup or on a long interval — connection pools, log levels. A
flag is
a *runtime switch with rollout semantics* — per-user targeting, percentage ramps, kill
switch,
experimentation — and it's meant to change frequently and reactively. The practical
rule: if
you need per-user or percentage behavior, or instant rollback, it's a flag; if it's the
same
for everyone and rarely changes, it's config. The anti-pattern is making everything a
flag —
flag sprawl — or making flags out of config with no targeting, which buys you the
operational
complexity without the operational power."

**Interviewer**: "How do you test code behind a flag?"

**Candidate**: "Three layers. Unit tests: test both states — flag on and off — plus the kill
switch path, because the bug class is 'works when on, breaks when off' and vice versa.
Integration tests: test the targeting rules with golden users — fixed user ids whose
buckets
are known and asserted, so the rollout math is verified, not sampled. And environment
tests:
exercise the environment-override path in staging, and have CI run the matrix — default
on,
default off, kill-switched — against a representative set. The common failure is testing
only
the default state, so the first production toggle turns into a surprise. Also: tests
should
assert the flag configuration itself — that the rollout percentages and segments are
what the
product team declared."

**Interviewer**: "The product team wants to turn a flag on for 100% on Friday. What do you
ask?"

**Candidate**: "The operational checklist: have we verified both flag states in production
already — did the canary traffic and the internal-beta traffic exercise the code path?
What's
the metric we're watching for the next hour — error rate and latency by version, not
just the
feature's own signal. Is the kill switch tested — someone should have flipped it at
least once
in staging. And the timing: Friday, before a weekend, is the classic regrettable window;
if it
can wait until Monday with a full incident response team awake, it should. Not to be the
process police — the point is that a 0-to-100 flag flip is a release, and it deserves
the same
checklist as any release: metrics, rollback, people."

**Interviewer**: "How do feature flags interact with your GitOps and CI/CD flow?"

**Candidate**: "They complement each other. CI/CD moves code through environments; flags decouple
the code from its activation. The GitOps angle: the flag configuration itself should be
versioned — as code or YAML in the repo — so flag changes are reviewed, auditable, and
rollback-able like any other change, not click-ops in a UI. The pipeline angle: flags
let CI
merge code behind a flag, deploy it, and let the rollout happen at runtime with instant
rollback — which is why teams adopting flags often relax their 'no merge before
production
ready' policies. The caution: flag changes and code changes must both be in the same
review
loop, or you get flags whose definitions drift from the code that guards them."

**Interviewer**: "What breaks in a distributed system when flags are evaluated locally?"

**Candidate**: "Consistency and freshness. Each instance evaluates with its own copy of the flag
config, so during a config propagation you can get split behavior — some instances
serving the
new variation, some the old — which surfaces as weird flakiness in A/B results and can
leak
experiments across users hitting different instances. The mitigations: versioned config
with
atomic swaps, warm caches that fetch before serving, and short TTLs; and accept that
eventual consistency is the contract — flags are eventually consistent by nature, and
your
experimentation tooling must handle it. The second failure class is stale config during
a kill
switch — a slow-polling instance keeps serving the bad feature for minutes after the
switch —
so kill-switch config usually gets a fast path."
