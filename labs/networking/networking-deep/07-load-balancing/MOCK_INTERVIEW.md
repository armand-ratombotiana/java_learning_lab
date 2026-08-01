# Mock Interview — Load Balancing

*Transcript of a senior-level interview covering L4/L7 load balancing, algorithms, health checking, session persistence, and a design exercise.*

---

## Opening

**Interviewer:** Tell me about the load-balancing work you've done in production.

**Candidate:** I've owned the ingress layer for a platform doing roughly 30k requests per second across about 300 backends. The stack was a cloud L4 load balancer in front of a fleet of L7 reverse proxies, and most of my time went to three things: getting the health-check semantics right so a sick backend never served traffic but a slow backend wasn't hammered by probes, tuning consistent hashing for our cache tier so node changes didn't melt the caches, and the session-persistence story for stateful services during deployments.

**Interviewer:** Start at the top. Layer 4 versus Layer 7 — what's the real difference, not the textbook one?

**Candidate:** The textbook answer is TCP/UDP versus HTTP — but the real difference is *where the load balancer stops participating in the connection*. An L4 balancer forwards packets or establishes TCP passthrough; it never sees application payloads, so it can't route on URL, headers, or cookies, and it can't terminate TLS. An L7 balancer terminates the client connection, reads the HTTP request, and opens a *new* connection to a backend. That has three consequences people forget: L7 gives you routing and rewriting power, but it makes the balancer a full participant — it can buffer, it can retry, and it becomes responsible for connection hygiene; it also means TLS can be terminated there, which changes the security model; and it costs more in state because the balancer holds client connections while backends hold their own.

**Interviewer:** When would you deliberately choose L4?

**Candidate:** Three cases. When you need raw throughput and can't afford the L7 proxy layer's memory per connection. When the protocol isn't HTTP — databases, gRPC over raw TCP, WebSockets where you want pure passthrough. And when you want TLS end-to-end without the balancer being a man-in-the-middle — although then you give up routing on encrypted fields. The classic pattern is actually both: L4 in front, L7 behind it, so the L4 tier absorbs the flood and the L7 tier does the intelligent routing at smaller scale.

---

## Algorithms

**Interviewer:** Walk me through the selection space — round-robin, least connections, weighted variants. When do you pick each?

**Candidate:** Round-robin is the baseline: zero state, perfectly fair under uniform request costs and equal backend capacity. The moment costs are not uniform, it falls apart — one slow endpoint backs up because everyone keeps getting assigned to it. Least connections fixes exactly that: it's the right default for *heterogeneous* request costs and variable processing times, because it reacts to actual concurrency rather than the arrival sequence. The catch is that it's only as good as the accuracy of "connections" as a proxy for load — a connection serving a 10ms request and one streaming a file for minutes are not equivalent, so modern L7 balancers use *in-flight request estimates* rather than raw connections. Weighted variants exist for heterogeneous capacity: you're weighting by machine size or by measured throughput.

**Interviewer:** So why does almost everyone still use round-robin?

**Candidate:** Because it's stateless, predictable, and its behavior is trivial to reason about — no convergence surprises, no oscillation. In most systems request costs average out over enough volume, and the *load balancer's* job is distribution, not load shaping. I'd add: round-robin makes capacity planning linear, and it degrades gracefully. Least connections is my choice when backends differ in speed or requests are heavy-tailed.

**Interviewer:** When do you reach for consistent hashing?

**Candidate:** When the *destination* matters — cache affinity, sticky sessions without a cookie, sharded backends. The point isn't distribution quality; it's stability: adding or removing a node should move only the keys that node owned, not everything. Classic hashing modulo N is catastrophic — a node change remaps nearly every key and the cache tier takes a miss storm. Consistent hashing bounds the disruption to about 1/N of keys for a ring of N nodes — and with virtual nodes you also fix the skew problem, because real machines have wildly different hash ranges on a ring.

**Interviewer:** What's the hidden cost of consistent hashing you'd warn a team about?

**Candidate:** Three things. First, *distribution isn't the algorithm's strength*: even with virtual nodes, a small ring can deviate from uniform; you need replication of hash ranges for hot-key tolerance. Second, *load imbalance on capacity change*: when a node joins, it takes 1/N of keys — but if the new node is half the size of the others, you've just overloaded it. You need *weighted* consistent hashing where each node owns range proportional to its capacity. Third, *removal semantics*: when a node dies, its keys are rehashed to neighbors — which then take a load spike; the ring must be sized so a single node failure is absorbable.

---

## Health Checks

**Interviewer:** Now the part everyone gets wrong: health checking. What does a good health-check design look like?

**Candidate:** Three layers, in order of increasing cost: **passive** detection (connection failures, protocol errors, error ratios — the balancer is *already* in the data path, so these are free), **active probes** (synthetic requests to a dedicated health endpoint on an interval), and **circuit breakers** (the endpoint's own view — if the backend's internal error rate exceeds a threshold, it trips its own breaker and tells the balancer). The key design rule: active probes must hit a *dedicated* health endpoint that checks the backend's dependencies (DB, cache), because a probe that only checks "process is up" lulls the balancer into sending traffic to a backend that's alive but useless. The second rule: probe *interval and failure thresholds* must be tuned per tier — a 1-second probe on a 5-second GC pause backend creates a flap.

**Interviewer:** Talk about the flap problem.

**Candidate:** A backend that flips between healthy and unhealthy is worse than one that's simply down — it creates connection churn, breaks in-flight requests at re-probe boundaries, and causes thundering herd on the other nodes. Mitigations: hysteresis — require K consecutive successes to mark healthy and M consecutive failures to mark unhealthy, so a single blip doesn't flip state; exponential backoff on probes of confirmed-down nodes; and in-flight request draining on unhealthiness — stop *new* traffic immediately but let existing connections finish instead of resetting them.

**Interviewer:** What does "draining" mean precisely, and when does it happen?

**Candidate:** Draining is the state where the balancer stops sending new connections to a node but keeps the existing ones alive long enough for them to finish — used during rolling deploys and on graceful shutdown. Two parameters matter: the drain window (typically seconds to minutes, bounded by the longest legitimate request) and the *wait* semantics: after the window, remaining connections are force-closed. The common failure mode is draining with no timeout — deployments stall forever because a few long-lived connections never end, and the cluster hangs at zero-drain progress.

---

## The Design Exercise

**Interviewer:** Design a session-persistence strategy for a service behind an L7 balancer, three replicas, rolling deploys every two hours.

**Candidate:**

1. **Prefer statelessness**: the real fix is to make sessions shareable — sticky sessions are the last resort, not the first. If the session state can live in a shared store, the balancer needs no persistence at all.
2. **If stickiness is required**, cookie-based affinity beats IP hash: the balancer inserts a cookie holding an encoded backend id (with HMAC so clients can't forge a specific backend). IP hash breaks on NAT, mobile carrier proxies, and VPNs — it glues a *household* to one node.
3. **Rolling-deploy integration**: the balancer drains the node being replaced before it leaves the pool; new sessions land on the new node while old sessions finish on the old one — that's why the drain window must exceed the session lifetime.
4. **Backend failure**: a sticky cookie pointing at a dead node must fall back — hash the cookie value again and pick a replacement, and *rewrite* the cookie on the response so subsequent requests stick to the new node.

**Interviewer:** How do you prevent one overloaded node from becoming the bottleneck of an entire fleet?

**Candidate:** Two mechanisms, used together. An **overload indicator**: backends advertise load (queue depth, latency) and the balancer weights them down — this is the L7-proxy's advantage, because it has real signal instead of connection counts. And a **capacity headroom policy**: the pool's target utilization is capped at, say, 70-80%, and the *autoscaler* is the load balancer's partner — the balancer reports per-node utilization so scaling decisions react to the same signal. A fleet that's allowed to reach 100% utilization is one node-failure away from a cascading meltdown.

**Interviewer:** How do you validate the whole design before production?

**Candidate:** A load test with three failure-injection scenarios: kill a node at peak load and watch error rate and the neighbor nodes' utilization; slow a node to 3x latency (not down — *slow*, which is the case health checks handle worst) and watch whether traffic shifts away; and run a rolling deploy at peak and watch the drain window against the session lifetime. And the chaos part: verify the *balancer itself* is not a single point of failure — two active-active balancers with healthy BGP/anycast failover between them.

---

## Wrap-Up

**Interviewer:** What's the one sentence you'd leave an engineer implementing load balancing?

**Candidate:** The load balancer's job is to make *backend failure invisible* to clients — every decision, from algorithm to health checks to drain windows, should be evaluated by that standard.

**Interviewer:** And the most common mistake you see in interviews?

**Candidate:** Treating the algorithm as the interesting part. The algorithm is a one-liner — the interesting part is the *state machine around it*: health, draining, fallback, overload signals. That's where production load balancers live and die.

---

## What the Interviewer Was Looking For

- Understanding L4 vs L7 as *where the balancer participates*, not just protocol names.
- Choosing algorithms by property (stability for caching, reactivity for heterogeneous load).
- Health checks as a layered system with hysteresis and backoff, not a ping.
- Drain semantics and the rolling-deploy interaction.
- Cookie-based stickiness over IP hash, with fallback and HMAC.

## Common Mistakes Candidates Make

- Calling least connections "the best algorithm" without specifying *what signal* it uses.
- Treating health checks as a single ping with a fixed interval.
- Forgetting that consistent hashing's strength is stability, not distribution quality.
- Missing the overload-indicator feedback loop.
- Designing stickiness without a fallback path for backend death.
