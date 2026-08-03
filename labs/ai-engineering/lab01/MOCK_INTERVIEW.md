# Lab 01: Mock Interview — LLM Serving Infrastructure

**Role**: AI Engineer / MLOps Engineer
**Duration**: 60 minutes
**Focus**: Continuous batching, response caching, load balancing, autoscaling, KV-cache

---

**Interviewer**: "Walk me through serving a burst of concurrent requests."

**Candidate**: "Requests arrive at `InferenceServer` and are handed to the demo as
`LlmRequest` objects — id, prompt, max tokens, priority. The server keeps a bounded
work queue with a token budget: enqueuing is cheap and rejects nothing immediately; a
worker pool pulls the queue in priority order, so the hot path is O(1) enqueue and
the scheduling decision happens once per batch slot, not per request. The burst demo
submits thirty concurrent requests and the server processes them in a handful of
batches — that is the heart of the lab: you cannot serve an LLM gateway with one
request per worker the way you would a REST API, you batch."

**Interviewer**: "What is continuous batching and why does it beat static batching?"

**Candidate**: "Static batching packs N requests into one forward pass and releases them
together, so a short prompt waits on the longest generation in its batch. Continuous
batching adds and removes sequences from the batch as they finish — when one request
reaches its EOS token it is evicted and a queued request takes its slot mid-iteration.
The lab encodes this in the worker loop: after every generation step it drains finished
sequences, refills from the priority queue, and recomputes the batch composition. The
result is what the demo shows: latency no longer tracks the tail of a fixed batch, and
GPU utilization stays high because there are always tokens in flight."

**Interviewer**: "How does the response cache work, and what does it store?"

**Candidate**: "`ResponseCache` is an LRU map keyed by the normalized prompt — exact-match
only, no semantic dedup — holding the `LlmResponse` including the text and the cache
hit bookkeeping. Two details matter. First, capacity is bounded and eviction is LRU, so
a runaway cache cannot leak memory; the demo can print hits versus misses to show the
hit rate. Second, the cache is a single-flight cache: concurrent identical requests
share one upstream call instead of stampeding the model with duplicate work. That
matters for a serving tier because caching is where the cost win lives — every hit is a
request that never touches the GPU."

**Interviewer**: "Why can't you just cache aggressively and ignore the model?"

**Candidate**: "Because cache hits must be correct, and LLM outputs are not always
deterministic. The cache is keyed on exact prompt text, so anything with
temperature-driven sampling or a random seed cannot be blindly replayed, and any
prompt that changed by a single token is a miss. The deeper issue is staleness: if the
model version changes, cached responses from the old version must be invalidated —
serving an old model's output under a new deploy is a correctness bug, not a
performance trick. The lab treats the cache as an optimization layered on top of the
serving path, not as a substitute for hitting the model."

**Interviewer**: "How does the load balancer decide which replica gets a request?"

**Candidate**: "`LoadBalancer` tracks `ModelReplica` entries with a live status and a weight.
The default strategy is weighted round-robin over healthy replicas — health is
determined by the last heartbeat or probe, and a replica that fails its check is
excluded from the rotation until it recovers, rather than failing requests at the
gateway. The lab supports sticky sessions so a conversation thread keeps hitting the
same replica, which preserves KV-cache reuse across turns; a stateless request can go
to any healthy replica. The key property: routing decisions are made from the balancer's
view of replica health, and that view must be kept fresh."

**Interviewer**: "What role does the KV-cache play in serving, and why does it complicate
scaling?"

**Candidate**: "During generation the model keeps the key-value tensors of every prefix
token, and recomputing them per request is the dominant waste — that is what paged
attention and KV reuse exist to avoid. The complication is memory: KV-cache grows with
the number of concurrent sequences, so capacity is a function of how many requests are
in flight, not just model size. That is why the server's batch slot must consider the
tokens each sequence still needs, why evicting finished sequences matters every step,
and why sticky routing helps — the cache is only reusable when the same replica
continues the conversation."

**Interviewer**: "The lab mentions autoscaling. What signal drives it?"

**Candidate**: "The signal is queue depth and batch saturation. When the worker pool is
consistently full and the priority queue keeps growing, the manager scales the replica
set up — `scaleTo(n)` with a new `ModelReplica` joining the balancer's rotation; when
the queue drains below a threshold for sustained time, replicas scale back down. The
design point is that scaling is reactive to demand but with hysteresis, so you do not
thrash: a spike of thirty burst requests should not instantly double the fleet, and a
quiet minute should not halve it. Autoscaling only works when the health and routing
layer can absorb the churn, which is why it sits on top of the balancer."

**Interviewer**: "How do you reason about the latency/throughput tradeoff for this gateway?"

**Candidate**: "Throughput is requests per second and tokens per second at the GPU, and it
is maximized by larger batches and full KV reuse; latency is the p50 to p99 of a single
request, and it suffers when a request waits in the queue or shares a full batch. The
lab measures both: server-side p50/p99 of queued-plus-generate time, and the aggregate
tokens-per-second of each batch. You tune the batch slot and queue budget so the p99
stays under the SLO while the token utilization stays high — and you always report
latency percentiles, never averages, because the average hides the queued tail that
user experience actually feels."

**Interviewer**: "What happens when a replica goes down mid-serve?"

**Candidate**: "The balancer's health check marks the replica unhealthy and stops routing
to it, so new requests go elsewhere. In-flight requests on the dead replica fail, and
the policy is to retry idempotent, cacheable requests once against a healthy replica —
never retry blindly, because retries amplify load when a whole tier is failing. The
demo exercises this by failing a replica and showing the balancer reroute around it
while the server keeps serving. The lesson: failure handling at a gateway is about
containment — mark unhealthy, drain, and let the pool absorb it — not about heroic
per-request recovery."

**Interviewer**: "How do you cost-account for serving, and where does money go?"

**Candidate**: "The largest cost is GPU time, so the lab ties cost to tokens: a cost
multiplier per model and per token class (prompt versus completion) means every
response carries a cost estimate. Caching reduces spend directly, batching improves the
tokens-per-second per GPU, and queue admission control prevents runaway spend from
unbounded retries. You want three numbers visible at all times: total tokens served,
cache hit rate, and cost per successful request — when hit rate drops or cost per
request climbs, you investigate the routing or the cache keying before you buy more
GPUs."

**Interviewer**: "How does the server handle priorities among queued requests?"

**Candidate**: "The queue is priority-ordered: an interactive request with a low latency
tolerance jumps ahead of a batch job that can wait, because the priority is part of
the `LlmRequest` the client submits. The design point is that priority affects
queue position, not correctness — every request is served, and the ordering is a
policy decision made by the server, not by client behavior. The production lesson
is the same one the queue itself teaches: without explicit priority, all requests
are equal and interactive users drown behind batch work; with it, you need the
token budget and the batch slot to keep starvation away — a high-priority flood
must not block everything else indefinitely."

**Interviewer**: "Walk me through serving a conversational thread across multiple turns."

**Candidate**: "First turn: the request is a cache miss, the balancer picks a replica —
with sticky sessions — the model generates, and the response is cached. Second turn:
the same session key must route to the same replica so the KV-cache from the first turn
is still resident; a new session would start cold. The system-level point is that the
conversation needs state at three layers — the cache for identical prompt reuse, the
replica for KV locality, and the balancer's stickiness to keep them aligned. Break
stickiness and every turn is a cold start; keep it and multi-turn latency compounds
down over the conversation, which the demo's turn-by-turn latency output shows."

**Interviewer**: "What is the most common serving failure you have seen, and how does the
lab's design prevent it?"

**Candidate**: "The most common failure is thundering-herd retries: the gateway times out,
every client retries at once, and the replicas that could recover are crushed by the
retry wave. The lab prevents it in three ways: the queue is bounded and refuses new
work with backpressure instead of unbounded buffering; the cache dedups concurrent
identical requests so one upstream call serves many clients; and the balancer excludes
unhealthy replicas instead of letting retries hammer them. The other classic failure —
invisible tail latency — is handled by measuring p99 at the server so the queueing
component is visible. Both fixes are structural, not load-dependent."
