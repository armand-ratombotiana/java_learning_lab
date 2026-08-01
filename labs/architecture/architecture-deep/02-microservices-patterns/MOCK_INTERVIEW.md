# Lab 02: Mock Interview — Microservices Patterns

**Role**: Senior Software Architect
**Duration**: 60 minutes
**Focus**: Service decomposition, saga patterns, circuit breakers, API gateway

---

**Interviewer**: "Our company has a 10-year-old monolith handling orders, payments, inventory, and
shipping. We want to move to microservices. Where do you start?"

**Candidate**: "Not with the technology — with the seams. I'd start by decomposing by business
capability using domain-driven design: identify subdomains, draw bounded contexts, and find the
existing seams in the monolith — module boundaries, database schemas, team ownership. The first cut
is usually orders, payments, inventory, and shipping. Then I'd pick the highest-value, lowest-risk
capability to extract first and use the strangler fig pattern to migrate incrementally."

**Interviewer**: "Why strangler fig?"

**Candidate**: "Because it lets us keep shipping features while decomposing. We route a slice of
traffic to the new service through a gateway or a routing layer in front of the monolith. As
services prove out, we move more capabilities. The monolith shrinks until it's gone — or until we
decide it's not worth killing. It's the only pattern that gives continuous delivery during a
migration of this size."

**Interviewer**: "Our team is two senior engineers and four juniors. Is microservices the right
call?"

**Candidate**: "Honestly? Probably not yet. Conway's law cuts both ways: microservices need teams to
own them, and two seniors can't own ten services. The pragmatic answer is a modular monolith first —
strict module boundaries with the same interfaces you'd have as services — then extract services
when team size or scaling needs justify it. Microservices are a cost; you only take it on when the
benefit clears the bar."

**Interviewer**: "Let's say we do it. Database per service — walk me through the implications."

**Candidate**: "Each service owns its database, and no other service touches it — all access goes
through the owning service's API. That kills the classic failure where ten services share one
schema. The tradeoffs: you lose cross-database joins and transactions. Joins become API calls or
eventually-consistent projections; transactions become sagas."

**Interviewer**: "So how do you handle a transaction that spans order creation, payment, and
inventory reservation?"

**Candidate**: "A saga. Two styles: choreographed — each service publishes a domain event and the
next service reacts; or orchestrated — a central orchestrator calls each service in order and
invokes compensations in reverse on failure. For an order flow I prefer orchestration: the flow is
explicit, debuggable, and the compensation order is defined in one place."

**Interviewer**: "Walk me through the orchestrated saga for placing an order."

**Candidate**: "The orchestrator calls: (1) reserve inventory, (2) charge payment, (3) ship order.
Each step is a service call. If payment fails, the orchestrator runs the compensation for the steps
already done — release the inventory reservation. Every step must implement its compensation, and
compensations must be idempotent because they may retry. The saga state is persisted so a crash
mid-flow can resume compensation."

**Interviewer**: "What happens if the payment step times out — did it charge or not?"

**Candidate**: "That's the uncertainty window. We record the saga as 'payment unknown' and run a
resolution procedure: query the payment service for the transaction status before deciding to
compensate or proceed. Never guess. Compensating a charge that actually succeeded creates a refund
nobody asked for; proceeding without a charge creates a free order. Resolution by query, then
decide."

**Interviewer**: "Now resilience. Your inventory service is slow. How do you protect the rest of the
system?"

**Candidate**: "A circuit breaker. The client-side call to inventory is wrapped: when failures cross
a threshold — say 50% over 10 seconds — the breaker opens and calls fail fast without hitting the
service. After a cooldown, half-open probes allow a single test request; success closes the circuit,
failure reopens it. This gives the inventory service time to recover and stops cascading timeouts."

**Interviewer**: "Show me the circuit breaker states and transitions."

**Candidate**: "Closed -> Open -> HalfOpen -> Closed, plus timeouts. Closed: normal calls, counting
failures. When failure rate exceeds threshold: open — all calls rejected immediately. After reset
timeout: half-open — one probe call allowed. Probe success: closed again, counters reset. Probe
failure: open again. Fallbacks sit behind the breaker: a cached response or a degraded message like
'inventory unavailable, we'll notify you'."

**Interviewer**: "How do all these services talk to the outside world?"

**Candidate**: "Through an API gateway — the single entry point. It handles routing, authentication,
rate limiting, and request aggregation. The client never knows which service owns what; it talks to
`/orders`, `/payments`, `/inventory` on the gateway. The gateway also lets us version APIs and do
the strangler-fig traffic splitting during migration."

**Interviewer**: "Is the gateway a single point of failure?"

**Candidate**: "It's a potential one, so it must be stateless and horizontally scaled behind a load
balancer — no session state inside the gateway itself. Many teams also split it: an edge gateway for
auth/TLS and a service gateway for routing. The bigger risk is teams turning the gateway into a god
object with business logic — it should do routing and cross-cutting concerns only."

**Interviewer**: "How do you handle service discovery?"

**Candidate**: "The gateway or client-side load balancer queries a service registry — services
register on startup, deregister on shutdown, and health checks prune dead instances. With
Kubernetes, the registry is often built in: services get DNS names and endpoint slices. The key
point is that service addresses are never hardcoded; they're discovered."

**Interviewer**: "What about shared libraries — we have a common model jar used everywhere."

**Candidate**: "Shared *domain* code is the classic trap. If ten services compile against the same
jar, every change forces a coordinated release — that's a distributed monolith. Share only stable
contracts: DTOs or better, API schemas via OpenAPI or a protobuf/gRPC contract. Each service owns
its domain model and maps to the contract. If a team shares genuinely stable utilities — logging,
tracing, error types — those are fine."

**Interviewer**: "How do you test a saga?"

**Candidate**: "Unit-test the orchestrator with stubbed steps — verify execution order and reverse
compensation order. Contract-test each step's compensation against the real service API.
Integration-test the full saga against containerized services with fault injection: make payment
fail at the second retry, kill the orchestrator mid-flow, verify state recovery. Property: any step
failure at any position must end in a consistent compensated state."

**Interviewer**: "Observability — what do you need on day one?"

**Candidate**: "Three pillars: logs with correlation IDs that thread through gateway to service
calls; metrics — error rate, latency percentiles, saturation per service; distributed tracing with
span propagation so a saga's five calls show up as one trace. Plus saga-specific telemetry: current
saga state per order, compensation counts, and step durations. You can't debug a saga without seeing
the whole journey."

**Interviewer**: "Your inventory service owns the inventory database. The reporting team needs
inventory data. What do you do?"

**Candidate**: "Event-based data sharing. The inventory service publishes `InventoryReserved`,
`StockLevelChanged` events to a topic. Reporting consumes and builds its own projection — its own
copy of inventory data shaped for reports. This avoids the two things I said no to: cross-service DB
access and a shared database. Event-driven projections are the standard answer for analytics in a
microservices world."

**Interviewer**: "Biggest failure mode you've seen with microservices teams?"

**Candidate**: "Decomposing services but keeping a monolith mindset — shared databases, shared code,
one giant deployment pipeline that deploys everything together. At that point you have a distributed
monolith: all the pain of distribution, none of the independence. The second is starting with
microservices before product-market fit; the third is breaking things that shouldn't be split, like
a tiny CRUD service per table."

**Interviewer**: "How would you split a service later if you got it wrong?"

**Candidate**: "Same strangler fig discipline at the service level: put a routing layer in front,
carve out a sub-capability, route its traffic to the new service, verify, then delete the old code.
The pattern is fractal — it works at monolith scale and service scale."

**Interviewer**: "What's the one thing you'd never do in a microservices migration?"

**Candidate**: "The big-bang rewrite. Never rewrite the monolith from scratch and cut over. You lose
business knowledge, you stall features for a year, and the 'clean' rewrite reproduces the monolith's
mistakes. Incremental strangler-fig extraction is slower but it de-risks every step and keeps the
business moving."

**Interviewer**: "Last one: when is microservices genuinely the right architecture for you?"

**Candidate**: "When you have multiple teams that can own services end-to-end, when parts of the
system have genuinely different scaling or reliability requirements, when you need independent
deployability for velocity, or when the team is organized to support it. It's a business and
organizational decision as much as a technical one — Conway's law is the real architecture."

---

## Interviewer Feedback

**Strengths**:
- Correctly resisted microservices-for-its-own-sake; recommended modular monolith given team size.
- Deep saga knowledge: orchestration vs choreography, compensation idempotency, the timeout uncertainty window, resolution-by-query.
- Strangler fig applied at two levels — monolith and service.

**Improvements**:
- Could have sketched the saga state journal schema (what to persist on each step transition).
- Could have given concrete circuit breaker numbers (threshold, cooldown) earlier.
- Could have mentioned saga state machines with explicit states (STARTED, WAITING, COMPENSATING, ENDED) as a production pattern.

**Score**: Strong Hire
