# Lab 05: Mock Interview — Domain-Driven Design

**Role**: Senior Software Architect
**Duration**: 60 minutes
**Focus**: Aggregates, invariants, bounded contexts, ubiquitous language, domain events

---

**Interviewer**: "Our company processes insurance claims with a 15-year-old codebase. The domain
logic is spread across services, controllers, and stored procedures. We want to adopt DDD. Where do
you start?"

**Candidate**: "With the domain, not the code: map the business. Run event-storming workshops with
underwriters, claims adjusters, and actuaries. Discover the core domain — for insurers that's
underwriting and claims adjudication — find the subdomains, draw the bounded contexts, and build the
ubiquitous language together. Only then do we touch code. DDD fails when teams rename classes and
keep their old mental model."

**Interviewer**: "What's a bounded context, concretely?"

**Candidate**: "A boundary where a model has a single, consistent meaning. 'Policy' means something
different to sales, underwriting, and claims. Each of those is a bounded context with its own model
of Policy. Contexts communicate through translation — anticorruption layers, shared kernels, or
events — but the models never merge. The classic failure is one global model used by everyone:
that's how 'Policy' ends up with forty boolean flags and zero meaning."

**Interviewer**: "We have one database for everything. What does DDD say about that?"

**Candidate**: "Each bounded context should own its data — a database per context, or at minimum a
schema per context with no cross-context foreign keys. Two contexts sharing a table are one context
pretending to be two. For a legacy system, start by physically separating schemas and building
translation services between them, then extract contexts one at a time."

**Interviewer**: "Define aggregate for me. Give me a claims example."

**Candidate**: "An aggregate is a cluster of domain objects treated as one unit for changes — the
consistency boundary. Every change to the aggregate goes through its root. Example: a `Claim`
aggregate — the root is `Claim`, containing `ClaimItem`s, `Claimant`, and a `ClaimStatus`. When you
approve a claim, you must validate items, compute the settlement, and change status atomically — so
those objects are one aggregate. The repository loads and saves the whole aggregate."

**Interviewer**: "How do you decide what's inside the aggregate and what's outside?"

**Candidate**: "Ask: 'can these objects change in the same transaction, and is the invariant across
them?' If yes, same aggregate. If they only need eventual consistency, different aggregates
communicating via events. The rule of thumb I teach: an aggregate is as small as possible while
still protecting its invariants. A `Claim` and a `Customer` are separate aggregates — changes to
each are independent, and cross-aggregate notifications are events."

**Interviewer**: "Walk me through invariants in a claims aggregate."

**Candidate**: "Every command method checks invariants before mutating. For `Claim.approve(amount)`:

```java
public void approve(Money settlement) {
    if (status != ClaimStatus.UNDER_REVIEW) {
        throw new DomainException("Only claims under review can be approved");
    }
    if (settlement.isGreaterThan(policyCoverage)) {
        throw new DomainException("Settlement exceeds policy coverage");
    }
    if (!allItemsResolved()) {
        throw new DomainException("All claim items must be resolved first");
    }
    this.settlement = settlement;
    this.status = ClaimStatus.APPROVED;
    registerEvent(new ClaimApproved(id, settlement));
}
```

Check-before-change, throw `DomainException` on violation, and record a domain event only after a
successful change. The invariants live in the aggregate — no service can bypass them, because state
is only reachable through the root."

**Interviewer**: "Entity vs value object — how do you tell them apart?"

**Candidate**: "Identity. An entity has an identity that persists across changes — a `Claim` keeps
its claim number even when its status changes. A value object is defined entirely by its attributes
— `Money(amount, currency)` — and is immutable; two Money objects with the same attributes are the
same value, and one can be replaced by another. Rule of thumb: if you'd write an `equals` based on
all fields and never mutate it, it's a value object. `ClaimId` and `Money` are value objects;
`Claim` and `Customer` are entities."

**Interviewer**: "Why does immutability of value objects matter in this domain?"

**Candidate**: "Insurance math depends on it. `Money` and `Coverage` are shared freely across
aggregates — percentages, deductibles, settlement amounts. If they were mutable, a handler could
accidentally mutate a `Coverage` object that's also referenced elsewhere, corrupting two claims
silently. Immutable value objects make aliasing harmless, and the compiler enforces it."

**Interviewer**: "Domain events — what role do they play?"

**Candidate**: "They record what happened in the domain: `ClaimApproved`, `ClaimantInjured`,
`PremiumChanged`. Two uses. Within the context: separate aggregates synchronize — the approval of a
claim emits an event that the payment context consumes to issue the payout. Across contexts:
contexts communicate through events without sharing models. And events give us an audit trail — for
insurers, regulators love 'everything that happened to this claim, in order'."

**Interviewer**: "When would you choose event sourcing with DDD?"

**Candidate**: "When the audit history is a first-class requirement or you need temporal queries —
'what did this claim look like on May 1?' — or when the current state is just a projection of what
happened. Insurance is a classic fit: the event log is literally the business record. But it adds
serious operational complexity — event store, projection rebuilding, versioning of events. I'd only
take it on for the contexts that need it, typically claims, not the whole system."

**Interviewer**: "How does DDD handle the legacy stored procedures you mentioned?"

**Candidate**: "Carefully — the stored procedures encode business rules the business may have
forgotten. I'd inventory them, map them onto the domain model, and use them as a conformance test:
new aggregate behavior must produce the same business outcomes the procedures did, for a golden set
of historical claims. Then migrate context by context, strangler-fig style. The procedures are
documentation until proven redundant."

**Interviewer**: "What does the repository pattern give you?"

**Candidate**: "A collection-like interface for aggregates: `findByClaimId`, `save`. The domain
model uses it, so the persistence strategy — SQL, NoSQL, event store — is an implementation detail.
The repository is also the place where aggregate boundaries are enforced: you can't accidentally
save half a claim. For insurers with heavy reporting, the read side often becomes a CQRS projection
instead of hitting the same store."

**Interviewer**: "Talk to me about the ubiquitous language — how do you keep it alive?"

**Candidate**: "It's enforced by naming discipline. The class names, method names, and database
column names should read like the business speaks: `Claim.approve(settlement)`, not
`Claim.statusManager.updateClaim(claimDTO)`. We maintain a glossary from the event-storming
sessions, use the business terms in code reviews, and — the hard part — when a new term emerges from
the business, we rename the code. Teams that let code names drift from business names are rebuilding
the Tower of Babel."

**Interviewer**: "A junior developer asks you: 'why is my code slow to change? Every fix touches ten
files.'"

**Candidate**: "That's the symptom of an anemic domain — data classes with no behavior, all logic in
services. The claim object is a bag of getters, and 'business logic' is a 2000-line `ClaimService`.
The fix is the one we're building: move the rules into the aggregate, make the service a thin
coordinator, and let the domain speak. It's a rewrite of the structure but the behavior stays —
that's why we start with the ubiquitous language and invariants, not the code."

**Interviewer**: "How do you test DDD code — especially invariants?"

**Candidate**: "Unit tests against the aggregate root, no infrastructure: every command method gets
a table of scenarios — valid transitions, invalid states, boundary amounts. Domain exceptions
asserted by message. The event list asserted per operation. Integration tests verify the repository
round-trips the whole aggregate. Because the aggregate is a plain object, these tests run in
milliseconds, which is exactly the feedback loop you want when the domain is the risk."

**Interviewer**: "Final question: what's the most common way DDD initiatives fail?"

**Candidate**: "Treating it as a coding style instead of a discovery process. Teams draw aggregates
from the database schema instead of the business; they skip event-storming; they adopt the tactical
patterns — entities, value objects, repositories — without the strategic ones — contexts, language,
core domain. You get beautifully structured code that models the wrong thing. DDD is a conversation
with the business first, and a refactoring discipline second."

---

## Interviewer Feedback

**Strengths**:
- Strategic-first framing: event-storming, bounded contexts, ubiquitous language before tactical patterns.
- Concrete invariant example with domain exception + event registration.
- Honest about legacy: stored procedures as conformance tests, context-by-context migration.

**Improvements**:
- Could have sketched aggregate sizing for a specific claims flow (what is inside `Claim` vs separate aggregates).
- Could have mentioned tactical pattern 'factory' for aggregate creation (static factory enforcing creation invariants).
- Could have discussed eventual consistency trade-offs between contexts more concretely.

**Score**: Strong Hire
