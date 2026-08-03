# Lab 10: Mock Interview — AI Deployment & CI/CD

**Role**: MLOps Engineer / Platform Engineer
**Duration**: 60 minutes
**Focus**: Deployment strategies, traffic routing, canaries, rollback, model registry, CI/CD

---

**Interviewer**: "Walk me through the deployment architecture in this lab."

**Candidate**: "Four components. `ModelRegistry` is the source of truth: every `ModelVersion`
— an id, a version tag, a model or artifact location, a status — is registered and
immutable, so deployment always references a known artifact. `TrafficRouter` owns
the live weights: it maps each active model version to a percentage of traffic, and
the demo shows weighted routing — ninety-five percent to the incumbent, five
percent to the candidate. `DeploymentManager` orchestrates the lifecycle: promote,
rollback, canary — it mutates the router's weights and the registry's statuses.
`CiCdPipeline` runs the gate: it evaluates a candidate model against the incumbent
and passes or fails the build."

**Interviewer**: "Why is the model registry the foundation of safe deployment?"

**Candidate**: "Because every deployment decision — promote, rollback, canary — must
reference an artifact that is exactly known. If versions are implicit — a model
path in some config, a weight somewhere — then 'rollback' is an act of memory and
you cannot guarantee what you are rolling back to. The registry makes the
operation trivial: a version has an id, a tag, a status, and a location, and the
history is a list you can inspect. The lab shows this concretely: the registry
accumulates versions across the demo — the incumbent, the candidate, the canary,
the failed build — and every routing or rollback decision names a registry entry
instead of a guess."

**Interviewer**: "How does traffic routing work, and why weights instead of binary
switches?"

**Candidate**: "The `TrafficRouter` maps each active version to a weight — the lab's
demo routes a stream of requests and the percentages come out exactly: ninety-five
percent to one version, five percent to another. Weights are the tool because
deployment is gradual: a canary at five percent is a smaller risk than an all-or-
nothing switch, and a rollback from ninety-five/five to one hundred/zero is a
weight change, not a new deployment. The implementation detail that matters is
determinism: routing must be a pure function of the request — a stable hash — so a
given request always lands on the same version during a test window, which is what
makes consistency checks meaningful."

**Interviewer**: "What is a canary deployment, and when do you use it?"

**Candidate**: "A canary is a small, controlled slice of traffic on the new version —
the lab runs a canary at five percent against the incumbent at ninety-five — with
the goal of observing the candidate under real traffic before it takes over. You
use it whenever the evaluation can only be trusted on live data: latency, cost,
user-visible quality, and behavior under production load. The canary only works
with an automatic exit: the lab's canary monitor watches the candidate's metrics,
and after a run of consecutive breaches — the walkthrough shows a canary failing
three checks in a row — it rolls back automatically, restoring one hundred percent
to the incumbent. A canary that requires a human to notice is an alerting system,
not a canary."

**Interviewer**: "How does the lab decide to promote or roll back a canary?"

**Candidate**: "On metrics, with thresholds set in advance. The canary monitor compares
the candidate's live score against the quality bar: the walkthrough shows a canary
at 0.90 passing, then scores of 0.64, 0.61, and 0.58 failing — three consecutive
breaches trigger the automatic rollback with 'Canary rolled back; v2 serves 100%
traffic'. Promotion happens when the candidate clears the bar across its whole
window. The discipline is that the decision is encoded, not felt: a promotion or
rollback triggered by a metric breach is a rule; one triggered by a hunch is
gambling. The walkthrough's auto-rollback is exactly this — the worst thing a
canary can do is silently fail forward."

**Interviewer**: "How do you choose the canary percentage and the breach rule?"

**Candidate**: "The percentage is a risk dial: five percent means one in twenty requests
sees the candidate, which is enough to expose systematic failures — the walkthrough
uses exactly this split — and small enough that even a catastrophic candidate is
contained. The breach rule needs both a threshold and a patience: a single bad
metric can be noise, so the lab's monitor requires consecutive breaches — the
walkthrough shows three in a row at 0.64, 0.61, 0.58 — before rolling back, which
filters out jitter while still exiting fast on real failure. The tuning rule: the
window must be short enough to catch a bad model before it sees significant
traffic, and the threshold strict enough that 'passing' means something."

**Interviewer**: "Why is rollback as important as deployment?"

**Candidate**: "Because every deploy is a bet, and the safety net is the exit. The lab
models rollback as a first-class operation: reverting the weights so the incumbent
serves all traffic, or restoring the previously active version from the registry —
the walkthrough shows both: a rollback after promotion when the new version fails
its gate, and the canary auto-rollback. The design rules: rollback must be a
single atomic operation — change the weights back, update the statuses — and it
must be tested like any other path, because the first time you need it is not the
time to discover it is broken. Deployments fail; teams that roll back cleanly are
teams that deploy fearlessly."

**Interviewer**: "What does the CI/CD pipeline evaluate, and how?"

**Candidate**: "The pipeline runs the candidate through the evaluation and compares it
to the incumbent on the same metrics with the same gate. The lab's walkthrough
shows two builds: build number one passes — the candidate scores 0.93, above the
quality bar — and is deployed; build number two fails — the candidate scores 0.65
against the bar and the build is rejected before it can ship. The essential
properties: the gate is deterministic — same candidate, same data, same result —
and it runs before the deploy, so bad models never reach routing. The CI gate and
the canary are complements: CI catches what the evaluation set can see, the canary
catches what only live traffic can reveal."

**Interviewer**: "How does the deployment manager coordinate registry and routing?"

**Candidate**: "It is the transaction layer: when promoting a candidate, it updates the
registry statuses and adjusts the router weights together, so the system never
serves a half-registered version or routes to an unknown artifact. The walkthrough
shows the sequence of states this produces: the candidate promoted, then a
rollback restoring the previous version, then a canary added and auto-rolled-back,
and the registry recording all six versions with their final statuses. The
coordinated update is the safety property: a deployment system that can route
traffic to a version that is not registered, or register a version that never
served, has already lost its source of truth."

**Interviewer**: "How do you test the deployment machinery itself?"

**Candidate**: "With deterministic walkthroughs like the lab's: route a known request
sequence and assert the exact percentages, run a canary with scripted scores and
assert the auto-rollback fires on the third breach, run a gate with a known score
and assert pass or fail. Because the components are pure and the registry is
explicit, the entire lifecycle is replayable — you can run the same scenario twice
and get the same trace. The production version of this is the same idea at scale:
a staging environment where promotion, rollback, and canary are exercised with
recorded traffic before anyone trusts them with real requests. Untested deployment
code is the riskiest code in the system."

**Interviewer**: "How do you monitor a deployment after the traffic shift completes?"

**Candidate**: "You keep watching the numbers that motivated the change — latency,
error rate, cost per request, quality scores — because models drift and traffic
changes even after a clean deploy. The lab's monitors are the model: the canary
monitor that watched the candidate becomes the steady-state monitor that watches
the winner, with the same thresholds, on live traffic. And you keep the exit open:
the previous version stays registered and routable, so a regression discovered at
any point — not just during the canary window — is still one weight change away
from being reverted. Deployment does not end at the traffic shift; it ends when
the new version has proven itself in the wild."

**Interviewer**: "What is the most common failure you have seen in model deployment?"

**Candidate**: "The silent failed deploy: traffic shifts to the new version, nothing
crashes, and the metrics degrade slowly — quality, cost, or latency — while nobody
notices, because there was no canary, no gate, or no post-deploy monitoring.
The second most common is the rollback that cannot roll back: the old version was
overwritten, or the registry was never kept, so when the new model fails there is
no clean exit. The lab's design is the answer to both: a registered, immutable
history of versions, a gated pipeline that rejects failures before they ship, a
canary with automatic exit, and a rollback that is one operation. Deployment
without these is not deployment; it is an incident waiting for a name."
