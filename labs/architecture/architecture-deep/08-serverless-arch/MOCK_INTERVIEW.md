# Lab 08: Mock Interview — Serverless Architecture

**Role**: Senior Software Architect
**Duration**: 60 minutes
**Focus**: Function composition, cold starts, event routing, state management, FaaS trade-offs

---

**Interviewer**: "Our team wants to build a document-processing pipeline — upload, OCR, extraction,
indexing — on serverless. Talk me through the architecture."

**Candidate**: "Classic serverless pipeline: S3 triggers an ingestion function on upload, which puts
a message on SQS; an OCR function (or Step Functions state) processes it; extracted text goes to a
queue for the indexing function; results land in DynamoDB. Everything event-driven, everything
asynchronous, each function single-purpose and stateless. The design principles I'd hold to:
functions stay small, state lives in external stores, and failures are handled by queues and DLQs,
not by retry-in-function."

**Interviewer**: "Why SQS between the steps instead of just chaining function calls?"

**Candidate**: "Three reasons. Durability: if the OCR function is mid-deployment when 10,000
messages arrive, SQS holds them until the function is ready — a direct chained call would lose or
reject them. Backpressure: the queue absorbs bursts instead of the function. Retry semantics: a
failing message is retried with backoff, then moved to a dead-letter queue after max attempts.
Chaining synchronous calls between functions is the number one way teams accidentally rebuild a
monolith on Lambda."

**Interviewer**: "Walk me through the invocation lifecycle of a function."

**Candidate**: "An invocation has three phases. Init: the platform provisions or reuses a sandbox,
downloads the code, starts the runtime, runs the handler setup. Invoke: the handler executes with
the event payload. Shutdown: the sandbox is torn down after idle. The trick: sandboxes are *reused*
for subsequent invocations of the same function, and only the handler runs on reuse — that's why
globals initialized outside the handler survive, and why keeping expensive initialization (DB pools,
SDK clients) at module level kills cold-start latency on warm invocations."

**Interviewer**: "What causes a cold start, and how bad is it?"

**Candidate**: "A cold start happens when no warm sandbox is available — first invocation, or after
the sandbox was recycled during idle. The cost: init time added to the request. Java is the worst
case — JVM boot plus framework init can push p99 to seconds. The fixes, in order of impact: keep the
handler slim (no Spring context for trivial functions), initialize expensive resources at module
scope so warm invocations skip them, use snapshot restore (Lambda SnapStart / GraalVM native image)
for Java, and provisioned concurrency for the hot path. And measure with percentiles, not averages —
p99 is the number that matters."

**Interviewer**: "How do you handle state in a function that needs to remember something between
calls?"

**Candidate**: "You don't keep it in the function — the sandbox is ephemeral and can be torn down
any second. State goes to an external store: DynamoDB for structured state, S3 for documents,
ElastiCache/DAX for hot caches. The rule: a function must survive being killed and restarted with
zero data loss. For multi-step workflows with intermediate state, Step Functions stores execution
state natively — that's the idiomatic answer to 'my function needs memory between steps'."

**Interviewer**: "When would you use Step Functions vs composing functions yourself?"

**Candidate**: "Step Functions when the flow has branches, retries, timeouts, or human approvals —
its state machine gives you durable execution, checkpointing, and an audit trail for free, and you
don't pay for idle wait time. Direct function composition for simple pipelines — enrich then index —
where a queue hop is all you need. The boundary: if I need to track 'where is this document in the
flow' as a first-class concept, that's Step Functions territory."

**Interviewer**: "How do you route different event types to different functions?"

**Candidate**: "The trigger layer. S3 event notifications filter by prefix/suffix and route to
specific functions. SNS topics route messages by attribute to different subscribers. An API Gateway
maps HTTP paths/methods to functions. Or one function reads an `eventType` header and dispatches
internally — which is what I'd do when the routing rules live in code and need versioning. Each
route is a binding: `order.created` -> `enrich-order`, `order.enriched` -> `validate-payment`."

**Interviewer**: "What happens to events nobody handles?"

**Candidate**: "Dead-letter: route them to a DLQ with the original payload, the failing function,
and the error. A DLQ is not a trash can — it's a recovery path: someone fixes the cause, then
replays the queue. My rule: every queue has a DLQ, every DLQ has an owner and a replay runbook, and
DLQ depth is a pageable metric. An unrouted event that silently vanishes is how data gets lost."

**Interviewer**: "Cold start mitigation for Java specifically — what would you tell a team?"

**Candidate**: "Four moves, in order: (1) don't do heavy work at startup — lazy-init everything you
can; (2) keep the deployment package small — fat jars with every dependency are the enemy; (3)
SnapStart — the platform snapshots the initialized sandbox and restores it, cutting init from
seconds to hundreds of milliseconds; (4) provisioned concurrency for the p99-sensitive endpoints.
And honestly: for hot paths, sometimes a long-running service is the right answer. Serverless isn't
a religion."

**Interviewer**: "What are the failure modes specific to serverless?"

**Candidate**: "Five that bite people. Cold-start spikes during traffic surges — exactly when you
need the capacity. Timeout ceilings — 15 minutes max for Lambda; long jobs need chunking or a
different compute. Ephemeral storage limits — /tmp is 512MB and not durable. Concurrent-execution
limits — account-level caps throttle you precisely when traffic spikes. And the silent one: *wasted
money* from functions polling or running loops — you pay per millisecond, so sleep() in a function
is literally burning cash. Plus the observability gap — distributed tracing is non-negotiable."

**Interviewer**: "How do you observe a serverless pipeline?"

**Candidate**: "CloudWatch logs with structured JSON, X-Ray traces or OpenTelemetry with one span
per function invocation carrying a correlation ID through the whole pipeline, and metrics:
invocations, error rates, cold-start counts, duration percentiles, throttles, DLQ depth. The tracing
story is the important one — a document that fails in the indexing step needs its full journey: S3
upload time, OCR duration, extraction retries, index write. Without the correlation ID threading
through, debugging is archaeology."

**Interviewer**: "How do you do environment/config and secrets for functions?"

**Candidate**: "Environment variables for non-secret config, versioned with the function — and a
dedicated secrets store (SSM Parameter Store or Secrets Manager) for keys and credentials, injected
at runtime, rotated on schedule. Never bake secrets into the deployment package; they end up in
container images and layer snapshots where they leak. For a Java function, config is read once at
init and cached at module scope."

**Interviewer**: "Your team argues we should move everything to serverless to cut costs. What do you
say?"

**Candidate**: "Cost is workload-shaped. Serverless wins for spiky, event-driven, low-utilization
workloads — an OCR pipeline that runs 200 times a day is nearly free. It loses for steady
high-throughput compute — a constant 8-vCPU load at $0.0000167/GB-second is cheaper as an EC2 or
Fargate instance, and functions have the concurrency, timeout, and memory ceilings to fight. I'd run
the math on the actual traffic profile before committing, and I'd keep the door open for a hybrid:
serverless for the event plane, containers for the sustained compute."

**Interviewer**: "How do you test a serverless pipeline locally?"

**Candidate**: "Unit-test the handler with a fake event — the handler is just a function, so this is
plain Java testing. Contract-test the routing table: every event type has exactly one route, and
every route points at a registered function. Integration-test with the local emulator or the real
cloud against a staging account, including failure injection: kill the OCR function mid-batch and
verify the messages retry and the DLQ catches the poison messages. And test cold starts explicitly —
a test that asserts cold-start p99 under the SLO."

**Interviewer**: "Biggest serverless anti-pattern you see in the wild?"

**Candidate**: "Functions that call each other synchronously — HTTP-calling their siblings instead
of eventing. It couples functions, doubles latency on every hop, and turns the platform's retry
semantics off. The second: putting business logic in the routing layer or in giant functions that do
everything. And the third: assuming statelessness solves itself — teams that store state in instance
memory and discover the truth during a traffic spike. Events between functions, state outside
functions — those two rules prevent most serverless disasters."

**Interviewer**: "What's your take on the 'serverless monolith'? One function with a router that
handles all endpoints."

**Candidate**: "It's a legitimate pattern — one Lambda per service, not per function — that gets you
most of the cost model with less cold-start pain and simpler ops, especially for Java services with
frameworks like Spring Boot. I'd use it for a bounded context with cohesive behavior, and split to
finer functions only where scaling or failure isolation demands it. 'Serverless monolith' is a
pejorative only when it's a monolith by accident; it's a strategy when chosen deliberately."

**Interviewer**: "Final question: how do you decide between serverless and containers for a new
service?"

**Candidate**: "A decision table. Serverless: event-driven, spiky, short-lived tasks, needs zero
idle-cost, fast to ship, variable load. Containers: sustained throughput, long-running processes,
low latency with tight p99 budgets, GPU or specialized compute, stateful workloads, or teams that
need full runtime control. And the tiebreaker: the team's operational maturity and the observability
story they can actually run. I've seen teams adopt serverless for the logo and hit a wall on
tracing; I've seen container shops pay for idle capacity for years. Match the compute to the
workload, not the fashion."

---

## Interviewer Feedback

**Strengths**:
- Solid lifecycle knowledge: sandbox reuse, module-scope init, SnapStart, provisioned concurrency.
- Correctly pushed queues/Step Functions over synchronous chaining.
- Honest cost analysis and hybrid guidance.

**Improvements**:
- Could have sketched the Step Functions state machine JSON for the document pipeline.
- Could have given concrete cold-start latency numbers for Java with/without SnapStart.
- Could have mentioned serverless observability specifics (X-Ray subsegments, metrics filters) in more depth.

**Score**: Hire
