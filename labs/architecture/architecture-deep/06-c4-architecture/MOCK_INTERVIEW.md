# Lab 06: Mock Interview — C4 Architecture Model

**Role**: Software Architect
**Duration**: 45 minutes
**Focus**: C4 levels, diagram-driven documentation, architecture as code

---

**Interviewer**: "Your new team inherited a system with zero architecture documentation. The last
architect left one PowerPoint from 2019. What do you do?"

**Candidate**: "First, interview — but the deliverable isn't a document, it's a model. I'd use the
C4 model: four zoom levels — context, containers, components, code. I start by walking the codebase
with the team to build the container view: what applications exist, what databases, what message
brokers, how they talk. From there I derive context — the systems around us — and component views
for the important containers. The critical decision: keep the model as code (PlantUML/Structurizr
DSL) so it regenerates instead of rotting."

**Interviewer**: "Why C4 rather than UML?"

**Candidate**: "UML is a notation; C4 is a *zoom discipline*. The four levels answer the four
questions stakeholders actually ask: who is involved and what external systems exist (context)? What
apps and data stores make up the system (containers)? What parts does each app have (components)?
What does the code look like (code)? C4 doesn't add new notation — it uses boxes and arrows with a
strict leveling rule — so it survives contact with business people, which UML diagrams usually
don't."

**Interviewer**: "Walk me through the four levels with our payments system as the example."

**Candidate**: "Level 1, context: the actors — customer, merchant — and the systems — our payments
platform, the banks we integrate with, the card networks. One diagram, ten boxes, readable by the
CEO. Level 2, containers: our payments platform decomposes into the API application, the transaction
worker, the ledger database, the event bus. Level 3, components: inside the API application — the
`ChargeController`, `ChargeService`, `LedgerClient`. Level 4, code: class-level diagrams for the
interesting parts — and that's where C4 stops being a diagram and becomes the source code itself."

**Interviewer**: "Who reads which level?"

**Candidate**: "Context for anyone — new hires, sales, architects from other teams. Containers for
engineers and ops — deployment and ownership decisions. Components for engineers working inside a
container, and code for the few classes that carry real complexity. The rule that keeps C4 honest:
never mix levels in one diagram. A diagram with a customer, a database, and a controller is a level
salad, and it's the most common C4 violation."

**Interviewer**: "What makes a good container diagram?"

**Candidate**: "Completeness and the right annotation. Every runtime — app, database, queue — is a
container, annotated with its technology: 'Spring Boot', 'PostgreSQL 16'. Relationships between
containers are labeled with the protocol: 'HTTPS/JSON', 'SQL', 'Kafka events'. Deployment boundaries
— AWS account, Kubernetes namespace — are drawn as boundaries around the containers. If a
relationship can't be labeled, you probably don't understand the system yet — and that's the diagram
doing its job."

**Interviewer**: "How do you decide what's a container vs a component?"

**Candidate**: "A container is separately deployable — it has its own runtime or its own database. A
component is a logical grouping inside a container — controller, service, repository. A monolith is
*one* container with many components; microservices are *many* containers, each with a few
components. Teams confuse the two when they draw microservice internals at container level, or when
they draw a monolith as one giant box and stop — the interesting detail lives at component level."

**Interviewer**: "The previous architect's diagram was beautiful and useless. How do you keep C4
diagrams truthful?"

**Candidate**: "Make them code. I use Structurizr DSL or PlantUML C4 files checked into the repo,
and a CI job regenerates the diagrams and fails the build on drift. When a developer adds an
endpoint and a database table, the architecture diff shows up in the same pull request as the code.
The alternative — a wiki page someone maintains by hand — decays within weeks. Truthfulness is a
process problem, and the process is: diagrams are generated artifacts."

**Interviewer**: "How much detail goes in a component diagram?"

**Candidate**: "One screen, roughly. If a container needs more than fifteen components, it's either
a level-4 view or the container is doing too much. The key is *scope*: a component diagram is for
one container, not all of them. For our payments API: `ChargeController` -> `ChargeService` ->
`LedgerClient` and `TransactionRepository`, plus external `PaymentGateway` — that's the whole
diagram. Diagrams that need a legend larger than the content have failed."

**Interviewer**: "Your CEO wants to see the architecture. Which level do you show, and why does it
matter?"

**Candidate**: "Context. The CEO needs to see the system's place in the world: customers, merchants,
banks, regulators. It's a business conversation — costs, dependencies, risks — that happens to use
boxes and arrows. This is why C4's leveling is its superpower: the same model produces the CEO's
view and the engineer's view; we never maintain two different diagrams of 'the same' system that
quietly disagree."

**Interviewer**: "How do you handle the dynamic behavior — workflows, state machines — that static
diagrams miss?"

**Candidate**: "C4 explicitly defers behavior to UML sequence/state diagrams. I use C4 for structure
and a small set of sequence diagrams for the flows that matter: a charge flow, a refund flow, a
reconciliation flow. The two are linked from the C4 components — the component box is clickable in
Structurizr. Structure without behavior is a skeleton; behavior without structure is gossip."

**Interviewer**: "How do you relate the C4 model to the actual Java code structure?"

**Candidate**: "The mapping is direct: system = one codebase or repo group; container = a deployable
artifact (one Spring Boot app); component = a package or module inside it. I keep the mapping
explicit — the component id is often the package name. When the code layout disagrees with the
model, one of them moves. In the healthiest setups, the model is generated from package scanning, so
disagreement is a compile-time concept."

**Interviewer**: "Is C4 worth it for a team of four shipping fast?"

**Candidate**: "Yes, but scoped: a context diagram and container diagram only, maintained as code,
regenerated in CI. That's a few hours of work that compounds — onboarding, incident response,
architecture reviews all get a shared map. Component diagrams only for the containers with actual
complexity. The failure mode to avoid is diagram tourism: producing all four levels in week one and
never updating them."

**Interviewer**: "What tools would you pick?"

**Candidate**: "Structurizr DSL if we want the model to be queryable and render multiple diagram
types; PlantUML C4 if the team already uses PlantUML; Mermaid if we want zero-install GitHub
rendering. My real requirement is the same regardless of tool: the source of truth is text in the
repo, and rendering is automated. I also like ASCII fallbacks rendered from the same model — they
survive in code review comments."

**Interviewer**: "Your new hire asks what the difference between C4 and ArchiMate is."

**Candidate**: "ArchiMate is an enterprise architecture language with formal concepts —
capabilities, value streams, motivation — aimed at architects and business. C4 is a lightweight
structural modeling convention aimed at software systems and engineers. C4 fits the 'which boxes are
our apps' question; ArchiMate answers 'how does this capability deliver value to the strategy'. You
can even combine them: C4 for the system level, ArchiMate for the enterprise level above it."

**Interviewer**: "Biggest C4 mistake you've seen in industry?"

**Candidate**: "Using C4 diagrams as a substitute for design thinking — drawing boxes instead of
making decisions. C4 documents decisions; it doesn't make them. The second-biggest: hand-maintained
diagrams that drifted so far from reality that engineers started ignoring them, which is worse than
no diagram, because stale documentation actively misleads. Both mistakes trace to the same root: the
diagram was a deliverable instead of an artifact of a living model."

**Interviewer**: "How do you start with a 10-year-old monolith that predates C4?"

**Candidate**: "Don't model the dream — model reality, warts and all. Generate the container diagram
from the actual deployment manifests and database connections; label the awkward truth — 'Oracle
shared schema: 3 apps'. The model becomes the migration's baseline: as strangler-fig extraction
proceeds, the container diagram is the before/after picture, regenerated each sprint. Architecture
that can't be drawn as-is can't be changed safely."

**Interviewer**: "Anything you'd add?"

**Candidate**: "One rule that keeps C4 useful forever: diagrams answer questions; if nobody is
asking the question a diagram would answer, don't draw it. C4 is a communication tool, not a
compliance artifact. When the model is code and the questions are real, the diagrams earn their
place in every review."

---

## Interviewer Feedback

**Strengths**:
- Clear four-level narrative anchored in the interviewer's payments domain.
- Strong stance on diagrams-as-code with drift detection in CI.
- Correctly handled level-mixing, scoping, and the monolith baseline problem.

**Improvements**:
- Could have given a concrete Structurizr DSL snippet.
- Could have discussed relationship cardinality/direction conventions (arrows from actor to system).
- Could have mentioned that level-4 code diagrams are best auto-generated by IDE/architectural tools rather than hand-drawn.

**Score**: Hire
