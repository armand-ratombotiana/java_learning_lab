# Lab 02: Mock Interview — Service Mesh Deep Dive

**Role**: Platform Engineer / DevOps Lead
**Duration**: 60 minutes
**Focus**: Istio architecture, Envoy sidecars, mTLS, traffic management, resilience patterns

---

**Interviewer**: "You're joining a platform team that runs microservices on Kubernetes without any
service mesh. Why would I want one?"

**Candidate**: "Four reasons, in order of how fast they pay off. Traffic management: weighted
splits, canaries, and blue/green without touching application code — the mesh routes at
the proxy
layer. Security: mTLS between every service with automatic certificate rotation, so
east-west
traffic is encrypted and authenticated by default. Resiliency: circuit breakers,
retries,
timeouts, and fault injection configured declaratively instead of being coded into each
service.
And observability: uniform metrics, access logs, and distributed tracing for every hop,
again
without instrumenting the apps. If a platform team can only justify one of these, I'd
start with
mTLS or traffic management — they're the ones that change the security and deployment
story."

**Interviewer**: "Walk me through the control plane / data plane split."

**Candidate**: "The control plane is the brain — in Istio it's istiod. It holds service discovery,
watches the cluster for services and endpoints, translates the high-level config —
VirtualService,
DestinationRule, Gateway — into Envoy configs, and runs the certificate authority that
issues
identities to every workload. The data plane is the muscle — one Envoy sidecar per pod,
injected
at deploy time. Every packet in and out of the pod flows through that sidecar, which
enforces the
routing rules, checks mTLS, runs the circuit breaker, and emits telemetry. The key
design property:
applications never know the mesh exists; the sidecar intercepts traffic transparently
via iptables
or the CNI. Control plane does config, data plane does enforcement — if istiod dies, the
data
plane keeps routing with the last config it got."

**Interviewer**: "How does mTLS actually work in the mesh?"

**Candidate**: "Each workload gets a SPIFFE-style identity — like spiffe://cluster.local/ns/payments/
sa/payment-service — and istiod's Citadel signs a short-lived certificate for it,
rotated every
few hours. When service A calls B, the sidecars do a mutual TLS handshake: A presents
its cert,
B verifies it against the mesh root CA, and vice versa, then traffic is encrypted
between
sidecars. Enforcement modes: PERMISSIVE accepts both mTLS and plaintext during
migration;
STRICT rejects plaintext entirely — that's the end state you want. The gotchas that bite
people:
unexpected plaintext calls from outside the mesh, certificate expiry being the top
incident, and
mTLS checking the identity, not just the cert — an attacker in a different namespace
with a valid
mesh cert still shouldn't reach your service unless the authorization policy allows it."

**Interviewer**: "How would you run a canary with the mesh?"

**Candidate**: "The mesh makes it a config change instead of a deployment dance. I'd deploy v2 as a
separate Deployment behind a shared Service selector, then a VirtualService with a
weighted
DestinationRule: start at 5% of traffic to v2, watch the golden signals for v2 — error
rate,
p99 latency — compared to v1, then step 25%, 50%, 100%, and finally scale down v1. The
useful
trick: per-version metrics come free because Envoy tags telemetry by destination
version. For
internal testing you can also route by header — traffic with x-canary: v2 goes to the
new
version — which gives you canary-in-request-header without percentage risk."

**Interviewer**: "What's a circuit breaker in Envoy terms, and how is it different from what you'd
build in code?"

**Candidate**: "Envoy's breaker is connection- and request-pool based: maximum connections,
maximum pending requests, maximum requests, and retries per upstream. When a pool limit
is hit,
Envoy responds 503 without opening another connection to the overloaded service — it's a
fail-fast shield. What it is not: it's not the circuit-open/half-open state machine
you'd build
with Resilience4j. In Envoy that behavior lives in outlier detection — consecutive 5xx
threshold, ejection duration, half-open probing after ejection — which is closer to what
people
mean by 'circuit breaker'. In code, the breaker is per-call and needs the caller to
cooperate; in
the mesh it's enforced by every sidecar for every caller without them knowing."

**Interviewer**: "How do you inject faults for resilience testing?"

**Candidate**: "Declaratively, with a VirtualService fault block — abort with a percentage and
HTTP status, or fixed delay with a percentage. For example: abort 2% of calls to
payments with
503 to verify the client retries and the retry budget isn't exhausted, or inject a
2-second delay
on 50% of calls to prove your timeout and circuit-breaker settings respond. That's
static fault
injection. Production chaos usually goes further — chaos-mesh or Litmus can kill pods,
saturate
CPU, or expire certificates early to test rotation. The discipline matters more than the
tool:
run fault injection in staging first, against the real timeout and retry config, and
always have
a blast-radius knob — the percentage is your blast radius."

**Interviewer**: "Sidecar injection is automatic — how do you make sure it's reliable?"

**Candidate**: "Namespace labels with the injection label turned on, and the mutating webhook
rewrites the pod spec at creation. The reliability concerns: the webhook has to be
available
when pods are created, or injection silently fails — so you monitor webhook failures;
sidecar
overhead — each sidecar uses about 50-100MB RAM and adds latency, so you set resource
requests
and watch saturation; and upgrade coordination — updating the data plane requires pod
restarts,
so you use staged rollout. The deeper question is whether you need sidecars at all: for
very
high-throughput workloads, ambient mesh with ztunnel, the per-node L4 proxy, avoids the
per-pod overhead. My default is sidecars for most services, ambient or none for the hot
paths."

**Interviewer**: "How do you debug 'requests fail randomly' when a mesh is involved?"

**Candidate**: "Systematically, because the mesh adds several layers. First, check the golden
signals on both sides of the hop — the source sidecar and the destination sidecar both
record
metrics, so I can see whether the request left the source, arrived at the destination,
or was
dropped in between. Then check the usual mesh suspects: mTLS failures — look for
handshake
errors; circuit breaker or outlier ejection — 503s from the source sidecar; fault
injection
rules left on; header or route mismatches — the VirtualService not matching the actual
host
header; and config propagation lag — istiod pushed old config. The access logs are the
ground
truth: one line per request on each sidecar, with response code, duration, and the route
that
matched. If both sidecars saw the request and the app returned 5xx, the mesh is innocent
— if
only one side saw it, the mesh is implicated."

**Interviewer**: "Where does the mesh stop and your own platform work begin?"

**Candidate**: "The mesh solves the wire: routing, mTLS, resilience, and telemetry between
services. It does not solve: service discovery semantics — that's Kubernetes and your
DNS;
authorization policy — the mesh enforces it, but you design the AuthorizationPolicy
model;
secrets — mTLS certs are mesh-managed, but app secrets belong in Vault; and
application-level
retries and idempotency — the mesh can retry, but only the app knows which calls are
safe to
retry. My rule: mesh for transport-level guarantees, app for business-level guarantees,
and
platform code only where the mesh's config model leaks into app behavior."

**Interviewer**: "Is a service mesh worth it for a team of five?"

**Candidate**: "Honestly? Often not yet. With five engineers and a handful of services, plain
Kubernetes with Ingress for east-west needs, network policies for segmentation, and an
observability stack covers most needs at a fraction of the operational cost. The mesh
pays off
when you cross the thresholds that make hand-rolled answers brittle: many services with
cross-cutting retry and timeout bugs, a real east-west security requirement, or teams
deploying
independent canaries every day. Start with the mesh when the platform team has the
capacity to
operate istiod and version upgrades — an unmanaged mesh is another incident generator,
not a
solution."
