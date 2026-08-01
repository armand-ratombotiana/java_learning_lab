# Lab 01: Mock Interview — Senior Cloud Architect

**Role**: Senior Cloud Architect | **Topic**: Multi-Cloud Failover with Provider Abstraction | **Duration**: 45 minutes

---

## Interview Transcript

**Interviewer**: "Welcome. Today I'd like you to design a multi-cloud failover system. Say we have a workload that must survive a full regional outage of any single public cloud provider. How do you approach this?"

**Candidate**: "Before writing any code or drawing any diagram, I'd want to nail down two things: the recovery objective and the failure model. If the business says RPO of 15 minutes and RTO of 5 minutes, that drives a very different design than RTO of 2 hours with data loss allowed. Second, I want to define what 'failover' means for our specific workload — is it stateless compute, stateful compute, or data plane? The abstraction layer we build is only as good as the primitives we commit to it."

**Interviewer**: "Good. Let's say the workload is stateless — an HTTP API behind a load balancer. RTO 5 minutes, RPO near zero for the database behind it."

**Candidate**: "With those numbers I'd design active-active across two providers with a third as a tie-breaker or cold standby. The traffic layer uses a global load balancer — DNS-based with health probes, or better, anycast L7 routing that can steer traffic in under a minute. The stateless API tier is the easy part: container images in a registry that both clouds can pull, Terraform modules that can materialize the whole tier in either cloud in minutes. The hard part is the database. A single-writer database in one cloud is a SPOF in the 'other cloud' scenario — if that cloud goes down, we're down. So I'd use either a multi-region managed database with automated failover, or better for true multi-cloud, a database that natively replicates across providers — something like a CRDT-based store or a multi-master setup with conflict resolution, accepting that different regions might briefly diverge."

**Interviewer**: "You mentioned provider abstraction. How do you design it without falling into the trap of abstracting away everything until you get the lowest common denominator?"

**Candidate**: "That's exactly the tension. My approach is interface-driven abstraction over the *primitives we actually need*: compute, storage, DNS, and messaging. I define a small set of capability interfaces — `CloudCompute`, `CloudStorage`, `CloudMessaging` — with provider adapters implementing them. But I explicitly design for an escape hatch: a capability discovery mechanism. If the application needs a provider-specific feature, like S3's Object Lock for WORM compliance, it doesn't go into the generic interface. The interface exposes a `hasCapability(String)` / `getCapability(String)` pattern, and the adapter can throw `UnsupportedCapabilityException`. We commit to the shared contract for 90% of the workload and explicitly acknowledge the 10% that's provider-specific."

**Interviewer**: "How do you handle failover detection? What's the failure signal, and how do you avoid a false positive that flaps traffic to a degraded cloud?"

**Candidate**: "This is where most designs get sloppy. I'd use a layered health signal: passive — actual 5xx rates from the API tier; active — synthetic probes hitting a health endpoint with a canary payload every 5 seconds; and infrastructure-level — provider status APIs. The failover decision requires a quorum: two of the three signals must say the cloud is unhealthy for at least 2 consecutive probe windows (20 seconds). Then I use a circuit breaker pattern with half-open state — the breaker trips, traffic flows to the secondary, but we periodically send a small percentage (say 5%) of traffic to the primary to check if it recovered. This prevents flapping between clouds, which is worse than being down in one, because each failover has a cost: DNS TTLs, connection draining, cache warming."

**Interviewer**: "Speaking of cost, what happens when you fail over to the secondary? Are you always running it hot?"

**Candidate**: "It depends on the tier. The stateless API tier in the secondary runs at minimum scale — one or two instances — sized to absorb a percentage of traffic during normal operation (this also gives us production-verified failover since it's constantly serving). On failure detection, we burst scale using pre-warmed capacity reservations — reserved instances or committed-use discounts so the marginal cost of bursting is lower. The database standby is a physical replica or cross-cloud replication that's continuously syncing, which is the expensive piece. So my cost model has three states: steady-state, pre-failover surge, and post-failover steady-state, and I'd do the math with the finance team on how much of the secondary we keep hot versus warm. A common middle ground: keep the secondary API tier hot enough to serve read traffic, and only the database replica is continuously replicated."

**Interviewer**: "What about data synchronization during the failover window? Walk me through the moment of failure."

**Candidate**: "Timeline: T0 the primary's health probes start failing. T0+20s the breaker trips. T0+20s the global load balancer flips traffic to the secondary. Between T0 and T0+20s, writes that were acknowledged by the primary but not yet replicated are at risk — that's the RPO exposure. With a synchronous replication setup, an acknowledged write is already on the replica, so RPO is effectively zero but write latency to the primary is worse and the cost is higher. With asynchronous replication, RPO is bounded by replication lag — measured in seconds normally, but it's a random variable, not a guarantee. My answer to the interviewer question 'which do you pick' is: it depends on the write path. For payments, synchronous with a quorum; for analytics events, asynchronous with an at-least-once replay. After failover, we enter 'split-brain protection' mode: the old primary is fenced — its egress is blocked at the network level so it can't process writes — and once it's confirmed dead, we promote the replica. Fencing is non-negotiable; without it you get two writers and data divergence."

**Interviewer**: "Now let's talk about the abstraction layer in code. What does the failover controller look like?"

**Candidate**: "The controller is a small, deliberately boring piece of code. It holds a list of `CloudProvider` adapters, each exposing `healthCheck()`, `provision()`, and `route()`. A scheduled executor runs health checks every 5 seconds. The controller maintains the routing decision — which provider is primary — and the application asks the controller for the current provider. The important design detail is that the controller must be stateless and crash-safe: if the controller process restarts, it rebuilds routing state from the health data, not from its own memory. And the controller itself must run somewhere that isn't in the blast radius — ideally it's a pair of controllers in different clouds using a shared lock, but honestly for most orgs, running it in two clouds with the state in a replicated store is the pragmatic answer."

**Interviewer**: "How do you test this? How do you prove failover works?"

**Candidate**: "Three layers. First, Chaos Engineering: we run scheduled failover drills on a monthly basis in production, ideally during low traffic. We use a 'failure injection' switch — an API on the provider adapter that can simulate degraded states — to test the detection logic without touching the real cloud. Second, simulation: the controller logic is pure enough to test in a unit test — I can inject fake providers that fail at scripted times and assert routing decisions, failover latency, and no-flap guarantees. Third, we continuously verify the secondary is actually functional: a canary that writes to the secondary's database and reads it back, otherwise we discover at failover time that the standby was silently broken — the 'phantom standby' problem. I'd also demand a runbook with a clear escalation: what's the manual override, who can force failover, and what the rollback procedure is."

**Interviewer**: "You mentioned active-active. With active-active across clouds, how do you handle the conflict between two writes to the same key happening concurrently?"

**Candidate**: "Active-active only works with a data layer that accepts it. Options, in order of preference: partition the namespace by region — writes to a key always go to its home region, so no conflict; last-writer-wins with vector clocks if conflicts are rare and acceptable; or CRDTs for the data structures that support them — counters, sets, maps with well-defined merge semantics. For a general SQL workload, I would not do active-active without serious thought. That's a product decision as much as an engineering one: if the business can tolerate eventual consistency for certain reads, active-active with LWW is fine; if not, active-passive is the honest architecture."

**Interviewer**: "Last question on this: what's your advice on multi-cloud vs. single cloud with multi-region? I hear a lot of debate."

**Candidate**: "I'd say multi-cloud is not a technical requirement, it's a risk-management decision, and the price is real: 20-30% higher engineering cost, slower feature velocity because you can't use the deepest provider features, and operational complexity. If a single cloud with two or three well-separated regions meets the availability target — and for the vast majority of workloads, 99.99% and even 99.999% is achievable — that's the better engineering trade. Multi-cloud earns its keep when: you need to survive an entire provider's control-plane or billing-plane outage (rare but real, and they have happened), you have regulatory data-residency requirements that force different providers per geography, or you're protecting against commercial risk — pricing power and negotiation leverage. My honest recommendation to leadership is usually: architect for multi-cloud portability at the interface level, run on two regions of one cloud for the steady state, and keep a validated failover path to a second provider as the 'insurance policy' that you actually rehearse."

**Interviewer**: "That's a balanced answer. If you had to pick the single most important implementation detail in the whole system, what is it?"

**Candidate**: "The health-check and decision loop, because every other component — the abstraction layer, the routing, the cost model — is static until the moment of failure. The decision loop is where correctness lives: quorum-based detection, debouncing to prevent flapping, fencing the old primary, and a half-open recovery path. If that loop is right, the system degrades gracefully; if it's wrong, you get split brain or a failover that's worse than the outage it was meant to fix. That's why the controller is the part I'd demand to see unit-tested with fault injection before anything else ships."

---

## Wrap-Up

**What the interviewer is looking for**:
- Clear requirement gathering (RTO/RPO, failure model) before architecture
- Honest trade-off analysis: multi-cloud vs multi-region, abstraction vs provider-native features
- Deep understanding of failure detection (quorum, debouncing, circuit breakers) and split-brain prevention
- Cost awareness: hot vs warm vs cold standby economics
- Testability: chaos drills, fault-injection unit tests, canary verification of the standby

**Common mistakes candidates make**:
- Jumping straight to a diagram without asking about RPO/RTO
- Abstracting everything to the lowest common denominator
- Forgetting fencing and split-brain protection in the failover sequence
- Claiming active-active works without addressing write conflicts
- Never answering the cost question: who pays for the always-on standby
