# Lab 03: Mock Interview — Hexagonal Architecture

**Role**: Senior Software Architect
**Duration**: 45 minutes
**Focus**: Ports and adapters, dependency inversion, framework-free core

---

**Interviewer**: "We're starting a new lending platform. The CTO wants 'hexagonal architecture' — in
your words, what does that actually buy us?"

**Candidate**: "Three concrete things. First, the business logic becomes testable without any
infrastructure — no database, no HTTP server, no framework context. Second, infrastructure is
swappable: a Postgres repository, a Redis cache, or an in-memory fake all plug into the same port,
so the core never changes when infrastructure changes. Third, the core stays stable while the
outside world evolves — frameworks and databases have shorter lifespans than business rules."

**Interviewer**: "Explain ports and adapters with an example from lending."

**Candidate**: "Take 'disburse a loan'. The core defines an inbound port — the use case interface —
like `DisburseLoanUseCase.disburse(loanId)`. A REST controller is a driving adapter that calls that
port. The core also needs things: it defines outbound ports — `LoanRepository`, `PaymentProvider`,
`LoanNotification`. A JDBC adapter implements `LoanRepository`, a Stripe adapter implements
`PaymentProvider`. The core only knows the interfaces; the composition root wires concrete adapters
at startup."

**Interviewer**: "What's the difference between a port and an adapter?"

**Candidate**: "A port is an interface — the contract, expressed in the core's language. An adapter
is a concrete implementation that translates between the outside world's protocol and that contract.
The REST controller translates HTTP into a use-case call; the JDBC repository translates method
calls into SQL. Ports point inward, adapters live outside. The rule: adapters implement ports; the
core never imports an adapter."

**Interviewer**: "How is this different from Clean Architecture?"

**Candidate**: "Same dependency rule, different vocabulary and emphasis. Hexagonal emphasizes the
shape — inside/outside, ports on all sides, any number of driving and driven adapters. Clean
Architecture adds explicit layer names — entities, use cases, interface adapters, frameworks — and
the idea that you can swap the whole outer ring (web for CLI, for example) without touching the
core. They're siblings; a hexagonal app is usually also clean."

**Interviewer**: "Where does your domain model live, physically?"

**Candidate**: "In the core module, along with the use cases and ports. The domain is the innermost
thing — entities with business rules, no framework annotations, no infrastructure imports. In a
Maven multi-module build, the core module must have a dependency list that's basically empty: just
the JDK. That's the architectural test — if the core module's `pom.xml` lists Spring Data, we've
violated the rule."

**Interviewer**: "How do you enforce the dependency rule mechanically?"

**Candidate**: "Three tools: module boundaries in the build (core module can't import adapter
packages — compile error if it does), ArchUnit rules in tests (no dependencies from core to
infrastructure packages, no annotations from frameworks in domain classes), and code review. The
build-level enforcement is strongest because it's structural, not behavioral. I've seen teams ship
hexagonal-shaped code that secretly imports the framework everywhere — the architecture only works
if it's enforced."

**Interviewer**: "Show me what happens when the core needs to call an external payment API."

**Candidate**: "The core declares an outbound port, `PaymentProvider`:

```java
public interface PaymentProvider {
    PaymentResult charge(PaymentRequest request);
}
```

A `StripePaymentAdapter` implements it, translating the domain's `PaymentRequest` into Stripe's API.
The core tests use a fake `PaymentProvider` that returns success or failure on demand. If we switch
to Adyen, we write one adapter class and change one line in the composition root. The use case code
never changes."

**Interviewer**: "What goes in the composition root?"

**Candidate**: "Everything concrete: which repository implementation, which notification adapter,
which clock. The composition root is the only place allowed to `new` concrete adapters. In Spring
that's the `@Configuration` classes — and the core doesn't know Spring exists. A good signal: if you
delete the framework, the composition root shrinks to a `main` method that wires the same objects by
hand."

**Interviewer**: "Your team is on a deadline. Is hexagonal architecture worth it for a small CRUD
service?"

**Candidate**: "For a trivial CRUD service, the full ceremony — ports for every use case, adapters
for everything — is overkill. I'd apply the pattern pragmatically: a simple service can have a thin
service layer and direct repository access; the moment you have two data stores, two UIs, or a
testability problem, add the ports. The pattern pays for itself at the boundaries that actually
vary. That said, the lending domain we're building is not trivial — there I'd do it properly from
day one, because rewriting domain code later is expensive."

**Interviewer**: "How do transactions fit? The core calls `save` on the repository — who controls
the transaction?"

**Candidate**: "The use case owns the transaction boundary: one use case, one transaction. Options:
the core calls a `UnitOfWork` port with `begin/commit/rollback`, or each repository method is
transactional and the adapter manages it. In practice I prefer an explicit port or a decorator on
the use case that wraps it in a transaction. The important thing is the transaction decision is an
architectural concern, not scattered in adapters."

**Interviewer**: "Does the core ever see a database entity or a DTO?"

**Candidate**: "No. The repository port deals in domain objects — `Loan`, `Customer`. The adapter
maps between those and its persistence model (JPA entities, JDBC rows). Same for inbound:
controllers translate requests into domain commands. Mapping is the adapter's job, not the core's.
It's a few extra lines but it's what keeps the core pure."

**Interviewer**: "How do you test the whole thing end to end?"

**Candidate**: "Four levels. Unit tests on the domain — pure business rules. Use-case tests with
fake ports — every port interaction asserted. Adapter tests — the JDBC repository against a real
(containerized) database, the controller against a test HTTP client. And one or two full-stack tests
through the real composition root. The architecture's payoff: level two runs in milliseconds with no
infrastructure, so the test pyramid is actually cheap."

**Interviewer**: "What are the common failure modes in teams adopting hexagonal?"

**Candidate**: "Four. One: leaky ports — methods returning framework types like `Page` or
`ResponseEntity`. Two: adapter logic creeping into the core — business rules in the repository
class. Three: port proliferation — five interfaces where one role suffices. Four: the composition
root becoming a god class with wiring for everything. And the meta-failure: following the pattern
but never enforcing it, so it decays silently."

**Interviewer**: "Your core needs a clock — for loan due dates. How do you handle time?"

**Candidate**: "A `Clock` port. The core never calls `Instant.now()` directly; it depends on a
`Clock` interface that a real adapter implements with `Instant.now()` and tests implement with a
fixed instant. This makes time-dependent logic — due dates, grace periods, interest accrual —
perfectly deterministic in tests. It's a tiny port that pays off disproportionately."

**Interviewer**: "How does hexagonal architecture interact with microservices?"

**Candidate**: "They compose beautifully. Each microservice is itself a hexagonal app: the API
controller is a driving adapter, the database and message producers are driven adapters. A Kafka
listener is just another driving adapter calling inbound ports. The message payloads become the
contracts between services. It keeps each service's core clean regardless of the orchestration
around it."

**Interviewer**: "The CTO asks you to 'prove' the architecture works before the team commits. What
do you do?"

**Candidate**: "Build the skeleton: one real use case — disburse a loan — with one driving adapter
(REST), two driven adapters (JDBC repository, fake payment provider), and the composition root. Then
demo: run the use case tests without any infrastructure; run the app against a real database; swap
the in-memory repository for the JDBC one by changing one line; show the core module's dependency
list is empty. That demo settles more architecture debates than any document."

**Interviewer**: "Anything you'd add?"

**Candidate**: "Document the boundary rules in the repository README: what may live in the core,
what may not, where ports go, how to add an adapter. Architecture that can't be explained to the
next hire gets eroded within a quarter."

---

## Interviewer Feedback

**Strengths**:
- Crisp definitions of ports, adapters, and the dependency direction.
- Practical enforcement story: module boundaries, ArchUnit, empty core dependencies.
- Honest about when the pattern is overkill.

**Improvements**:
- Could have sketched a concrete ArchUnit rule snippet.
- Could have discussed how to handle mapping boilerplate (e.g., record mappers) in the adapter layer.
- Could have mentioned event-driven inbound adapters (Kafka listeners) as driving adapters explicitly — touched but brief.

**Score**: Hire
