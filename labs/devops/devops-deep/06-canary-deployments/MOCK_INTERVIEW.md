# Lab 06: Mock Interview — Canary Deployments Deep Dive

**Role**: Platform / DevOps Engineer
**Duration**: 60 minutes
**Focus**: Rollout strategies, metrics-driven promotion, automatic rollback, Argo Rollouts

---

**Interviewer**: "What is a canary deployment, exactly — and what is it not?"

**Candidate**: "A canary deploys a new version to a subset of traffic, measures its behavior
against a defined budget, and promotes or rolls back based on those measurements. The
defining
features: it's incremental — 10%, 25%, 50% of traffic, not a big bang; it's
metrics-driven —
promotion is a decision made from data about the new version, not a schedule; and it's
reversible at every step — the rollback is a traffic shift, not a redeploy. It is not a
blue/green cutover, not just deploying to one node and crossing your fingers, and not a
scheduled rollout that promotes on a timer regardless of what the metrics say — that
last one
is the most common misuse, and it throws away the safety the strategy exists to
provide."

**Interviewer**: "Walk me through how Argo Rollouts implements a canary."

**Candidate**: "Argo Rollouts replaces the Deployment object with a Rollout CRD. You define
steps — each step is a weight and an optional pause, like 10% then pause 2 minutes — and
optionally an analysis: an AnalysisRun that queries Prometheus, or an analysis template
with
metrics and thresholds. The controller shifts traffic to the canary by the step's
weight,
pauses, runs the analysis against the canary's metrics, and on success advances to the
next
step; on failure it aborts — traffic reverts to the stable version and the canary is
scaled
down. Traffic shifting works through the service mesh, ingress, or the native service
mesh
integrations. The status is written back to the CRD, so every decision is auditable: who
promoted, at which weight, based on which metric run."

**Interviewer**: "How do you choose the steps and their weights?"

**Candidate**: "The steps are a trade between speed and blast radius. A common pattern: 5% for
minutes — enough signal, small blast radius; then 25%, 50%, 75%, 100%, with longer
pauses at
the low weights, where the signal is weakest. The pause duration is the harder decision:
it
must be long enough to collect statistically significant samples at that traffic level —
a
low-traffic service at 5% can need hours, not minutes. My guidance: start slower than
you
think you need, especially for stateful or schema-changing releases; automate the metric
queries so pauses are governed by significance, not vibes; and keep the steps auditable
— the
step list is a reviewable artifact."

**Interviewer**: "What metrics do you gate a canary on?"

**Candidate**: "Start with the ones that page you: error rate — 5xx and request failures per
version; latency — p99, because averages hide the tail that users feel; and saturation —
CPU,
memory, GC, connection pools, because a canary that's 'fast' can still be consuming
resources
unsustainably. Then add business metrics when the team has them: checkout success rate,
session duration — the canary can pass all technical metrics while breaking the product
behavior. The comparison matters as much as the metrics: compare canary vs stable on the
same
window, so a weekend-wide latency spike doesn't falsely fail a healthy canary. And keep
the
thresholds tied to your SLOs — if the SLO says 99.9% availability, the canary gate
should be
aligned with the error budget, not a random 10% number."

**Interviewer**: "What happens when a canary fails — what's the rollback sequence?"

**Candidate**: "Three phases. Decision: the analysis fails — threshold breached, or the
analysis run errors — and the controller marks the rollout degraded. Action: traffic
shifts
back to the stable version instantly, and the canary is scaled down; in Argo, the stable
replica set takes over and the rollout status shows RolledBack with the failure reason.
Then
the human loop: the incident is about the new version, not the platform — the rollout
was the
safety net that contained the failure. The design goal: rollback should be a routing
decision
in seconds, not a deploy pipeline run. What teams get wrong: the stable version must
actually
be healthy — a canary that rolls back to an already-broken stable version produces a
false
recovery, so the analysis should compare against the stable baseline every step, not
just
check the canary in isolation."

**Interviewer**: "How is this different from a progressive rollout done with a service mesh?"

**Candidate**: "They're complementary, not competitors. A service mesh — Istio, Linkerd — gives
you the traffic-shifting primitive: VirtualService weights, per-version metrics, and
request-level routing. Argo Rollouts is a controller that *uses* those primitives: it
defines
the rollout policy — steps, analyses, promotion rules — and drives the mesh config. The
division of labor: mesh is the data plane capability, Argo is the control loop. You can
do
canaries with either alone — mesh manually, Argo with a load balancer — but the
combination is
the standard modern stack: Argo's rollout object as the declarative policy, the mesh as
the
enforcement point. The key insight for interviews: the rollout controller is where the
*policy*
lives; the mesh is where the *mechanics* live."

**Interviewer**: "How do you test a canary rollout without waiting for production?"

**Candidate**: "Three layers. First, dry-run the analysis: point the AnalysisRun at real metrics
from staging and verify the queries and thresholds behave — a bad Prometheus query fails
every canary in production. Second, rehearsal mode: Argo has rollout rehearsal and the
setWeight/verifyFullPromotion commands that simulate the rollout's analysis against a
test
cluster. Third — and most important — the metrics comparison itself must be validated
against
known-good and known-bad versions: deploy an intentionally broken canary in staging and
prove
the analysis catches it. A canary system that has never failed a test canary is a canary
system whose rollback you don't trust."

**Interviewer**: "What are the classic mistakes in canary adoption?"

**Candidate**: "The big ones. Pauses that are too short for the traffic volume — promoting on
noise. Thresholds unrelated to SLOs — either so tight that every canary flips back,
teaching
teams to distrust the gate, or so loose that they're decorative. Rolling back to a
broken
stable version. Forgetting stateful workloads — a canary that's fine for stateless web
traffic
can still corrupt data when it writes to a shared schema; schema migrations need their
own
analysis. And the culture failure: no one watches the rollout, so a promotion happens at
3am
on Friday with no one ready to respond to the analysis. Tooling is 20% of a safe rollout
program; the other 80% is the operating discipline around it."

**Interviewer**: "When should you NOT use canaries?"

**Candidate**: "When the change isn't safely reversible, or the infrastructure can't do partial
exposure. Classic examples: destructive database migrations with no rollback path —
shifting
traffic back doesn't un-apply the migration; security patches where the vulnerability is
active in the canary's exposure window; and capacity-constrained systems where running
two
versions doubles peak load — or a fleet so small that a 10% canary is one replica and
carries
no statistical meaning. In those cases, use feature flags to decouple code from traffic,
or
blue/green with a full rollback environment, or scheduled maintenance windows. Canary is
a
strategy, not a default; the question is always 'can this specific change survive
partial
exposure and fast reversal?'"
