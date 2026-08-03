# Lab 06: Mock Interview — AI Pipeline Orchestration

**Role**: AI Engineer / Data Engineer
**Duration**: 60 minutes
**Focus**: Stage-based pipelines, orchestration, retries, idempotency, observability, DAGs

---

**Interviewer**: "Walk me through the pipeline architecture in this lab."

**Candidate**: "A `Pipeline` is an ordered sequence of `Stage` objects, each with a name
and an execute step, and the demo composes it from the lab's building blocks:
`TextPreprocessor`, `Tokenizer`, `FeatureExtractor`, `ModelInference`, and
`ResultFormatter`. Each stage receives the payload, transforms it, and passes the
result downstream — the pipeline is dataflow with explicit stages. `TimedStage`
wraps any stage to measure execution time, so per-stage latency is a first-class
concern, and the demo prints the stage-by-stage timing to show where time actually
goes."

**Interviewer**: "Why model a pipeline as explicit stages instead of one big function?"

**Candidate**: "Because each stage becomes independently testable, instrumentable, and
replaceable. A big function hides the structure: you cannot time a single step, you
cannot retry a single step, and you cannot tell where a failure came from. With
explicit stages you can wrap any stage in timing, log per-stage results, add a stage
without touching the others, and fail with precision — the error names the stage.
The lab's `TimedStage` is the payoff: per-stage metrics fall out of the architecture
instead of requiring a profiler."

**Interviewer**: "How does the pipeline handle a failure in the middle of a run?"

**Candidate**: "The failure is attributed to the stage that threw — the pipeline stops
at that stage rather than continuing with a corrupt payload — and the stage boundary
is where retry policy lives: transient stages can be retried with backoff, while
non-idempotent stages must not be. The deeper design point is the payload contract:
stages should pass immutable, versioned payloads so a failed run can be replayed
from a checkpoint. The lab shows the pipeline executing end to end and timing each
stage, and the production extension is exactly that — failure attribution and
resume-from-stage."

**Interviewer**: "Why does idempotency matter in an ML pipeline?"

**Candidate**: "Because production pipelines run more than once: retries, backfills, and
scheduled reruns are normal. If a stage appends results to a store, a rerun doubles
them; if it writes a derived artifact, a retry can corrupt it. Idempotent stages —
write-by-key, upsert, or produce-then-rename — make reruns safe by construction. The
lab's stage contract supports this by keeping execution deterministic given the same
input, which is the property you rely on when you replay a failed run. When someone
says 'just rerun the pipeline', the answer is only safe if every stage is
idempotent."

**Interviewer**: "What is the role of TimedStage, and what can it teach you?"

**Candidate**: "`TimedStage` decorates a stage and records its duration, and the demo
aggregates those durations across the run. It teaches you where the pipeline's time
goes — in a typical AI pipeline, model inference dominates while preprocessing is
cheap, and the timing output makes that visible instead of assumed. In production
this becomes per-stage metrics over many runs: p50/p99 per stage, drift in stage
cost over time, and alerts when a stage that used to be fast slows down. You cannot
optimize what you have not measured, and stage timing is the first measurement."

**Interviewer**: "How do you orchestrate stages with dependencies, not just a linear
chain?"

**Candidate**: "A linear chain is the simple case; real systems are DAGs — enrichment
stages that run in parallel, a merge stage that waits for both. The lab's pipeline is
linear, which is the right teaching shape, and the production upgrade is a DAG
runner: stages declare dependencies, the scheduler runs independent stages in
parallel, and the critical path defines the end-to-end latency. The discipline that
carries over unchanged: every stage still has a name, an input contract, and an
output contract, and the orchestration layer still needs per-stage metrics and
retry policy — parallelism changes the runner, not the stage model."

**Interviewer**: "How would you make this pipeline observable?"

**Candidate**: "Per-stage events: stage name, start, end, duration, input and output
summaries, and the failure mode if it fails. The lab's demo output already shows the
timing and the stage sequence; production adds a run id so every stage event for one
execution is correlated, and structured logging so events are queryable. The two
questions every observability setup must answer: which run failed, and which stage
in it — everything else is decoration. Once you have run-level correlation, you can
alert on stage duration regressions and failed-run rates instead of guessing."

**Interviewer**: "How do you handle non-determinism in a pipeline?"

**Candidate**: "Isolate it. Non-deterministic stages — model calls with sampling, time
stamps, random IDs — should be explicit and bounded, because they make runs
unreproducible: the same input can produce different outputs, so a failed run cannot
be replayed with confidence and a test cannot be repeated. The lab's pipeline is
deterministic by design — same input, same output — which is what makes its
demonstration repeatable. In production you pin seeds for testability, record the
model version in the payload, and treat 'unexpectedly different output for the same
input' as a bug until proven otherwise."

**Interviewer**: "How do you evolve a pipeline without breaking in-flight runs?"

**Candidate**: "Version the contracts, not just the code: payloads carry a schema
version, stages advertise the input version they consume, and the orchestrator runs
new and old stage versions side by side during transition. The lab's payload-passing
design is the foundation — a stage's input and output contracts are implicit in its
signature, so versioning them makes compatibility explicit. The failure mode is the
silent contract break: a stage changes what it emits, downstream stages consume it
without noticing, and results corrupt quietly. Contract versions make the break loud
instead of silent."

**Interviewer**: "What is the difference between orchestration and dataflow?"

**Candidate**: "Orchestration is control flow: which stages run, in what order, with
what retries and dependencies. Dataflow is the payload movement: what data each stage
receives and emits. The lab's pipeline shows both — the stage sequence is the
orchestration, the payload transformations are the dataflow — and keeping them
separate is what makes the system maintainable. When a pipeline becomes hard to
change, it is usually because the two are tangled: stages that know about the
scheduler, or control logic embedded in data transformations. The rule: stages do
data work, the orchestrator does control work."

**Interviewer**: "How would you scale this pipeline for high throughput?"

**Candidate**: "Parallelize at the stage level and the run level. Stage level: a stage
that is CPU-bound can process records in parallel, which the lab's stage model
supports since each stage is independent. Run level: independent pipelines run
concurrently; dependent runs queue — and this is where stage timing matters, because
the bottleneck stage determines the throughput ceiling. Then add backpressure: the
producer stage must not flood a slow consumer, or the queue grows unboundedly and
latency dies. The lab teaches the decomposition — bounded, instrumented, composable
stages — and scaling is composition plus a scheduler."

**Interviewer**: "How do you handle backpressure between stages?"

**Candidate**: "When a producer stage is faster than its consumer, the queue between
them grows without limit and the pipeline dies of latency before it dies of
anything else. Backpressure means the producer blocks or throttles when the
downstream queue is full — the pipeline is only as fast as its slowest stage, and
pretending otherwise just buffers the problem. The lab's linear pipeline hides this
because stages run in sequence, but the stage model is exactly where the fix lives:
each stage boundary is a potential queue, and each queue needs a bound and a
policy — drop, block, or spill. The lesson: measure the queue depths alongside
stage timing, because an unbounded queue is a silent memory leak with a
performance name."

**Interviewer**: "What is the most common failure you have seen in AI pipelines?"

**Candidate**: "The silent data drift between stages: each stage looks correct in
isolation, but a transformation subtly changes the payload — a field renamed, a type
coerced, a default silently applied — and downstream stages consume it, so results
degrade without any error. The fix is the contract discipline the lab models:
explicit stage interfaces, deterministic behavior, per-stage timing and logging, and
payloads that can be inspected at every boundary. The other common failure is the
opposite: a fragile pipeline that errors on any variation and dies in production on
the first unexpected input. Both are failures of stage design, and both are visible
when stages are first-class."
