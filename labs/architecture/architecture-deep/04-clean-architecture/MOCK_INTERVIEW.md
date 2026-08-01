# Lab 04: Mock Interview — Clean Architecture

**Role**: Senior Software Architect
**Duration**: 45 minutes
**Focus**: Dependency rule, layer boundaries, use-case driven design

---

**Interviewer**: "We're rewriting a claims processing system. The current one is a Spring Boot
monolith where controllers talk directly to JPA repositories, and business rules live in service
classes. The CTO wants Clean Architecture. What's the core idea you'd implement first?"

**Candidate**: "The dependency rule: source code dependencies point strictly inward. Entities and
use cases in the middle know nothing about Spring, JPA, or HTTP. The controllers, repositories, and
framework adapters live in outer layers and depend on interfaces owned by the inner layers. The
immediate practical effect: the business logic becomes compilable and testable without a Spring
context or a database."

**Interviewer**: "Walk me through the layers with a claims example."

**Candidate**: "From the center out: entities — a `Claim` with status transitions and amount rules,
a `ClaimPolicy` with coverage limits. Use cases — `SubmitClaim`, `ApproveClaim`, `PayClaim`, each
orchestrating entities and gateway interfaces. Interface adapters — controllers that translate HTTP
into use-case calls, presenters that translate results into JSON/HTML, and repository
implementations. Frameworks — Spring itself, the database driver, the web server. Dependencies point
inward: use cases know entities; they never import Spring classes."

**Interviewer**: "Wait — you said repositories are in the adapter layer but the use case needs to
persist a claim. How?"

**Candidate**: "Through an interface the use case owns: `ClaimGateway.save(claim)`. The use case
declares it; the adapter implements it. So the direction of control is inverted — the inner layer
dictates the contract, the outer layer fulfills it. That's the whole trick of the dependency rule:
the use case never names the repository class, so swapping JPA for Mongo means writing a new
adapter, not touching business logic."

**Interviewer**: "What's actually different from the classic layered architecture your team has
now?"

**Candidate**: "In classic layering, the service layer depends on concrete repository classes and
typically the framework — you have `ClaimService` calling `ClaimRepositoryImpl` (a JPA class). In
Clean Architecture, `SubmitClaimUseCase` depends on an interface and the direction of the dependency
is inverted; also, frameworks are pushed to the outer ring instead of being woven through every
layer. The visible symptom: your current services probably can't be unit-tested without Spring Boot
running. After the change, they run as plain Java."

**Interviewer**: "Where does the Spring `@Service` or `@Transactional` annotation go?"

**Candidate**: "On the outer ring. If you want Spring's bean management, you put `@Service` on a
thin application service that delegates to the use case, or you configure beans in the composition
root. Transactions: a decorator class in the adapter layer wraps the use case with `@Transactional`.
The use case itself stays annotation-free. This is the controversial part for teams — Spring folk
love annotations — but it's exactly what the dependency rule demands."

**Interviewer**: "Show me the shape of a use case."

**Candidate**: "A class with one public method per use case. Example:

```java
public class ApproveClaimUseCase {
    private final ClaimGateway claims;
    private final ClaimOutputBoundary presenter;

    public void execute(ApproveClaimCommand command) {
        var claim = claims.findById(command.claimId())
            .orElseThrow(() -> new ClaimNotFoundException(command.claimId()));
        claim.approve(command.approvedBy());
        claims.save(claim);
        presenter.present(new ClaimApproved(claim.id(), claim.status()));
    }
}
```

Input is a command record; output goes to a boundary. No Spring types, no return DTO leaking — the
use case is pure coordination of entities and gateways."

**Interviewer**: "Why a presenter instead of returning a DTO?"

**Candidate**: "A presenter decouples the use case from the format and destination of the result.
One use case can serve a REST endpoint, an internal scheduler, and a test spy through different
presenters. If we only ever have one consumer, returning a simple result record is fine — Clean
Architecture allows both; the presenter matters when you have multiple output formats (JSON for web,
events for Kafka, assertions for tests)."

**Interviewer**: "Your team has 200 existing service classes. How do you migrate without stopping
delivery?"

**Candidate**: "Strangler-fig it: pick one capability — say claim submission — and rebuild it with
the new structure behind the same REST endpoints. The old controller delegates to a compatibility
layer; new use cases live in new packages with ArchUnit tests enforcing the dependency rule from day
one. The critical success factor: enforce the rule mechanically from the first commit, because code
review alone will not hold the line under deadline pressure."

**Interviewer**: "What's your ArchUnit enforcement rule set?"

**Candidate**: "Four rules. One: entities and use cases may only import `java.*` and sibling core
packages. Two: nothing in the core may reference framework names — scan for `org.springframework`,
`jakarta.persistence`, `javax`. Three: gateways are interfaces; the core may not import any class
whose name ends in `Impl` or lives in an adapter package. Four: controllers/presenters may not be
imported by use cases. A hundred lines of ArchUnit protect the architecture for the life of the
project."

**Interviewer**: "Doesn't all this indirection slow the team down? Junior devs struggle with where
code goes."

**Candidate**: "Yes, that's the real cost — Clean Architecture has a learning curve, and I've seen
teams drown in interfaces. My answer is a documented package map: `domain`, `usecase`, `gateway`,
`adapter`, `frameworks`, with a README stating what may live where and a golden-path example —
'adding a new endpoint touches exactly these five files'. Clear conventions plus mechanical
enforcement turn the learning curve into a few days, not months."

**Interviewer**: "When would you *not* recommend Clean Architecture?"

**Candidate**: "Small CRUD apps, prototypes, or teams under two developers with tight deadlines. The
ceremony costs more than it saves when there's no complex business logic to protect and no
infrastructure churn. I'd recommend a modular monolith with clear boundaries but skip the full ring
ceremony. Also, if the team won't buy into enforcement, the pattern decays into 'fake clean' —
interfaces everywhere, logic still leaking — which is worse than honest layering."

**Interviewer**: "How does Clean Architecture interact with the database schema?"

**Candidate**: "The schema is an adapter detail. The core defines the domain model — `Claim`,
`Coverage` — and the persistence adapter maps to tables. This is where I push back on teams who let
JPA annotations into entities: the moment `@Entity` lands on a domain class, the dependency rule is
broken and the schema owns the domain. Keep entities plain, map in the adapter."

**Interviewer**: "What about validation — where does it live?"

**Candidate**: "Two kinds. Structural validation — 'amount must be positive', 'status must be OPEN
to approve' — lives in the entity as business rules; that's the enterprise logic. Protocol
validation — 'request JSON malformed', 'missing field' — lives in the adapter. Bean Validation
annotations belong in the controller's DTOs, not on entities."

**Interviewer**: "How do you test the use-case layer?"

**Candidate**: "Fake gateways and a recording presenter. `SubmitClaimUseCaseTest` wires a fake
`ClaimGateway` returning a claim, calls `execute`, asserts the fake got the saved claim and the
presenter received the right response. No Spring, no DB — microseconds per test. The persistence
adapter gets its own integration tests against a real database. The pyramid stays healthy because
the most important tests are the cheapest."

**Interviewer**: "Your CTO asks: 'what will be different in six months because we did this?'"

**Candidate**: "Three things they'll see. One: a migration like Spring Boot 2 to 3 or switching from
JPA to a different ORM becomes an adapter change, not a rewrite. Two: business rules are
unit-testable, so regression bugs drop — the claims logic has a suite that runs in seconds. Three:
new hires can trace a feature through five well-named files instead of spelunking a 2000-line
service class. The investment pays back on the first framework upgrade or the first big refactor."

**Interviewer**: "Anything to add?"

**Candidate**: "Keep the rings honest at the edges: a use case that starts importing `Clock.now()`
from a framework, or an entity with a logging dependency, is a leak. Small leaks become big ones —
I've never seen a 'clean' codebase stay clean without enforcement. Decide the rules, write the
ArchUnit, and treat architecture as code."

---

## Interviewer Feedback

**Strengths**:
- Articulated the dependency rule and its inversion mechanism crisply.
- Handled the Spring tension well (annotations at the edge, decorators for transactions).
- Realistic migration path with mechanical enforcement.

**Improvements**:
- Could have sketched the adapter decorator for `@Transactional` concretely.
- Could have discussed how to handle cross-cutting concerns (logging, metrics) without breaking the rule — AOP in the outer ring.
- Could have given explicit package names for the ring structure.

**Score**: Hire
