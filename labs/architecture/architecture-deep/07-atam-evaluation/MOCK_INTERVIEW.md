# Lab 07: Mock Interview — ATAM Evaluation

**Role**: Software Architect
**Duration**: 60 minutes
**Focus**: Quality attributes, utility trees, scenario analysis, tradeoff identification

---

**Interviewer**: "We're about to commit to a microservices architecture for our insurance platform.
The CTO wants to 'evaluate the architecture' before we invest. What process do you run?"

**Candidate**: "ATAM — the Architecture Tradeoff Analysis Method. It's the most battle-tested
evaluation method for this: it forces us to state what we care about, turns it into measurable
scenarios, scores the candidate architecture against those scenarios, and — the part most people
miss — explicitly surfaces the tradeoff points where one quality attribute is traded against
another. The output isn't a yes/no; it's a risk sheet with sensitivity and tradeoff points the CTO
can act on."

**Interviewer**: "Walk me through the ATAM steps."

**Candidate**: "Nine steps in four phases. Phase one, presentation: business drivers and
architecture presentation. Phase two, investigation: identify architectural approaches, generate
quality attribute utility tree, analyze architectural approaches against scenarios. Phase three,
testing: brainstorm and prioritize scenarios, re-analyze architecture. Phase four, reporting:
present results — the risk sheet. The utility tree is the heart: we decompose quality attributes
into scenarios and weight them, so evaluation is anchored to what the business actually values."

**Interviewer**: "Let's build a utility tree for insurance claims processing. What's at the top?"

**Candidate**: "The quality attributes that drive the business: availability — claims systems must
be up during business hours; performance — adjusters expect sub-second screens; security — PII and
PHI; modifiability — regulatory changes come quarterly; and cost of operation, which ATAM folds in
as a business driver. Each attribute branches down. Availability branches into failure handling and
recovery; recovery branches into concrete scenarios. Weights come from stakeholders: for an insurer,
availability and security typically outrank performance."

**Interviewer**: "What's a well-formed scenario? Give me one for claims."

**Candidate**: "ATAM scenarios have six parts. Example: *Source* — a claims adjuster; *stimulus* —
opens a claim with 200 attached documents; *artifact* — the claims web application; *environment* —
at normal load, Monday 9am; *response* — the claim renders; *response measure* — within 2 seconds at
p95. That's measurable and testable. Compare that to 'the system should be fast' — which is a wish,
not a scenario. The discipline of the six-part anatomy is what separates ATAM from opinion."

**Interviewer**: "How do you score the architecture against scenarios?"

**Candidate**: "Each leaf scenario gets scored against the candidate architecture, typically 0-1 or
red/amber/green. The scores roll up through the tree using the weights — each level's weights sum to
1.0, so leaf contribution = product of weights along the path times the score. The total is the
architecture's utility. But I want to be clear: the number matters less than *where* the scores
break. A scenario scoring 0.2 with a weight of 0.3 is a risk; the same score with weight 0.02 is
noise."

**Interviewer**: "Give me a concrete example of a tradeoff point in our insurance context."

**Candidate**: "Consider the decision to process claim payments through an asynchronous saga —
payment approval, ledger entry, notification across three services. That improves *availability* —
the claims service isn't blocked on the payment gateway — but it hurts *modifiability* — every
change to the flow touches three services and a compensation chain — and possibly *performance* for
the synchronous path. One decision, three attributes, mixed deltas. ATAM's job is to write that down
as an explicit tradeoff point and make the team choose consciously instead of accidentally."

**Interviewer**: "How is ATAM different from just doing a scoring workshop with sticky notes?"

**Candidate**: "Three ways. One: scenarios must be measurable — the six-part anatomy prevents vague
criteria. Two: the utility tree forces *weights* — stakeholders have to say what they'd trade, not
just what they'd like. Three: the output is the risk sheet — risks, non-risks, sensitivity points,
tradeoff points — which is decision material, not a scorecard. A sticky-note workshop produces
enthusiasm; ATAM produces a document you can argue with and revisit."

**Interviewer**: "What's a sensitivity point vs a tradeoff point?"

**Candidate**: "A sensitivity point is an attribute whose score changes a lot when the architecture
changes a little. Example: 'latency' is highly sensitive to adding a network hop — adding a cache
layer swings p95 dramatically. A tradeoff point is a single decision that moves two or more
attributes in opposite directions: introducing a message queue improves availability but degrades
latency and adds operational complexity. Sensitivity points tell you where to focus; tradeoff points
tell you what you're paying."

**Interviewer**: "How long does a full ATAM evaluation take?"

**Candidate**: "A proper one is three to five days with the stakeholders in the room — the CTO, a
lead from each domain, the architects. That's the honest answer. Teams often run a *lightweight*
variant — half a day for the utility tree, then the architects score against it and the stakeholders
validate — which captures 80% of the value. But if the decision is big — like committing to
microservices for a core system — spend the five days. The cost of the wrong architecture dwarfs the
evaluation cost."

**Interviewer**: "Who should be in the room?"

**Candidate**: "The decision-makers, the people who know the business drivers, and the people who
know the architecture — and they should be different people. The classic failure: only architects
attend, the tree weights reflect what the architects like, and the business drivers — cost,
regulatory deadlines — never surface. I also want one skeptical operator from each team — they're
the ones who know the scenarios that actually bite in production."

**Interviewer**: "Your team built the utility tree and every scenario scores 1.0. What do you
conclude?"

**Candidate**: "Suspicious. Either the scenarios are too weak — measuring things that are already
fine, not the risky ones — or the scoring was done by the architects evaluating their own
architecture, which is the fox guarding the henhouse. I'd push for adversarial scenarios: the ones
from the incident postmortems, the ones from the worst outages. ATAM is most valuable exactly where
it hurts — scoring honestly against the scenarios the architecture is likely to fail."

**Interviewer**: "How do you connect ATAM to the actual implementation?"

**Candidate**: "Scenario scores should be executable. The response measures — p95 latency, RTO,
breach containment time — become SLOs and load tests. We write a chaos test per availability
scenario and a benchmark per performance scenario, and the evaluation gets re-run quarterly with
*measured* numbers instead of estimates. Architecture evaluation stops being a one-off ceremony and
becomes a living review cycle."

**Interviewer**: "Microservices vs monolith for this insurance platform — what does ATAM usually
reveal?"

**Candidate**: "In my experience: microservices win on availability and team-scalability scenarios,
lose on modifiability for regulatory changes that cross services, and usually lose on cost and
operational simplicity. The killer scenarios are the data-consistency ones — claims have strict
audit requirements, and distributed transactions across services make 'everything committed or
nothing' genuinely hard. ATAM rarely says 'monolith wins' — it says 'these five scenarios are where
microservices will cost you, and here's what you must build to survive them'."

**Interviewer**: "What if the CTO doesn't like the result?"

**Candidate**: "Then the process worked — because ATAM is a decision-support tool, and the
decision-maker gets a risk sheet they can override with eyes open. What I will not do is massage the
weights to produce the desired ranking; that's evaluation theater. If the CTO chooses microservices
despite the tradeoff sheet, that's a legitimate business decision — and the risk sheet becomes the
backlog of mitigations they've accepted."

**Interviewer**: "Where does ATAM fall short?"

**Candidate**: "It's qualitative and subjective — different facilitators get different trees — and
it's a point-in-time snapshot: architecture drifts after the evaluation. It also doesn't score cost
well; cost enters as business drivers, not as a first-class attribute. I pair ATAM with quantitative
techniques — load tests, chaos experiments, cost models — and with ADRs so decisions made during
evaluation are recorded. ATAM frames the conversation; it doesn't replace measurement."

**Interviewer**: "Final: what's the single most valuable output of the evaluation for the CTO?"

**Candidate**: "The tradeoff sheet, hands down. Not the utility score, not the rankings — the
explicit list of decisions that trade one quality for another, with the deltas quantified. That's
the document that turns architecture from personal taste into engineering trade-offs, and it's the
one artifact the company will still cite a year later when someone asks 'why did we do it this
way?'"

---

## Interviewer Feedback

**Strengths**:
- Correctly walked the nine-step ATAM process and the four phases.
- Scenario anatomy applied concretely to the insurance domain.
- Strong framing of sensitivity vs tradeoff points and of evaluation-as-living-review.

**Improvements**:
- Could have sketched a sample utility tree with computed weights on the board.
- Could have discussed stakeholder conflict resolution (how weights are negotiated).
- Could have mentioned ATAM's relation to other methods (CBAM for cost-benefit extension, SAAM as predecessor).

**Score**: Strong Hire
