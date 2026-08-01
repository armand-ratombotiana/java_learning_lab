# Mock Interview Transcript: Sliding Window Rate Limiter with Redis Semantics

| Field | Detail |
|-------|--------|
| **Level** | Senior Backend Engineer (L5) |
| **Duration** | 45 minutes |
| **Format** | System design whiteboard + implementation |
| **Problem** | "Implement a sliding window rate limiter with Redis semantics: one key per client, per-window request counting, sub-second precision." |

---

## Part 0: Scene Setting (2 minutes)

**Interviewer (I):** We expose a public API. Clients misbehave — bursty traffic,
runaway retries — and the edge needs a rate limiter: per-client, say 100
requests/minute, enforced at the API gateway. Redis is available. I want you to
implement a sliding window limiter with Redis semantics, then tell me what breaks
in a distributed deployment.

**Candidate (C):** Before designing, I need to know the enforcement point, the
allowed failure modes, and whether the limiter must be consistent across nodes.

---

## Part 1: Clarifying Questions (5 minutes)

**C:** Three questions. One: where is the limiter deployed — one gateway node, or
many behind a load balancer? Two: what happens on a Redis outage — fail open or
fail closed? Three: is the 100/min per client a *hard* cap, or is an occasional
breach above the limit tolerable?

**I:** Many nodes behind a load balancer. On Redis outage: fail open is acceptable,
bursts are tolerable, but I expect you to defend the choice. Hard cap.

**C:** Then we have the shape of the design. Many nodes and a hard cap means the
decision must be **shared, atomic, and per-request** — that points at Redis as the
arbiter and rules out local in-memory limiting except as a pre-filter. Fail-open
on outage means we never brick the API because the limiter is down — the cost is
temporary over-admission, which is a pricing/support problem, not an availability
one.

---

## Part 2: Why Sliding Window (6 minutes)

**I:** Why not a fixed window — one counter per minute that resets?

**C:** Fixed window has the boundary burst problem. With 100/min, a client can send
100 requests at 59.9 seconds and 100 more at 60.1 seconds — 200 requests in 200 ms
with two empty windows in between. That's twice the intended rate for a sustained
period, and for a public API that's exactly the attack shape we're defending
against. Sliding window closes that hole by counting over the *last* N seconds,
not the current bucket.

**C:** There are two sliding implementations. The precise one — the **sliding
window log** — keeps a timestamp per request and counts how many fall inside
`now - window .. now`. Exact, but O(requests) memory per client. The efficient
approximation — **sliding window counter** — blends two fixed buckets with a
fraction: `count = prevBucket * (overlap) + currentBucket`. O(1) memory, and the
error is bounded by how far the window boundary cuts the previous bucket. For
Redis semantics, the interviewer usually wants the log form — ZSET-based, exact —
because that's what Redis's own examples implement.

**I:** So which do you implement?

**C:** The exact log — a sorted set per client key. It's the Redis-canonical
implementation: `ZADD`, `ZREMRANGEBYSCORE`, `ZCARD`, all atomic in a Lua script.
Memory is the real cost and that's a production tuning question; the algorithm is
unambiguous.

---

## Part 3: Redis Semantics — The Lua Contract (7 minutes)

**C:** The Redis semantics I'd implement are exactly this Lua script, run with
`EVAL` so the whole check is one atomic unit on one Redis instance:

```lua
-- KEYS[1] = client key      ARGV[1] = now (ms)   ARGV[2] = window (ms)   ARGV[3] = limit
redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, ARGV[1] - ARGV[2])
local count = redis.call('ZCARD', KEYS[1])
if count < tonumber(ARGV[3]) then
    redis.call('ZADD', KEYS[1], ARGV[1], ARGV[1] .. ':' .. ARGV[4])  -- unique member per request
    return 1  -- allowed
end
return 0
```

**I:** Why must it be a Lua script and not client-side commands?

**C:** Because the check-and-act sequence — trim, count, conditionally add — has to
be atomic. Two gateway nodes both reading `count < limit` and both adding would
let the client exceed the cap by the concurrency factor. A Lua script executes
atomically on Redis, so the read-modify-write happens with no interleaving. Doing
it in client code with `WATCH/MULTI` also works but is slower and easier to get
wrong. This is the heart of "Redis semantics": the decision is made by the
database, atomically, with the timestamps stored *in* the sorted set so the window
is computed from data, not from the client's clock.

**I:** Clock? Why does the timestamp come from the caller?

**C:** Nodes behind the LB can have clock skew — if each node stamped its own `now`,
the window boundaries would differ per node and the limit would shift. The gateway
sends a server-side time from Redis itself (`TIME` command) or a single NTP-synced
authority. In the Java implementation I pass a shared clock source, so the test
doesn't depend on wall clock either.

---

## Part 4: Distributed Deployment Concerns (7 minutes)

**I:** Now the hard part — you have 20 gateway nodes. What's the cost profile?

**C:** Every request becomes a Redis round trip — that's the price of a shared
arbiter. At our scale (tens of k rps) a single Redis shard handles the ZSET ops
easily, but the round trip adds roughly a millisecond to the request path, and the
limiter becomes a *dependency of the edge*. Three mitigations: (1) local
pre-filtering with a generous budget — e.g., admit locally up to 90% of the limit
per second and check Redis only beyond that, which cuts Redis traffic by an order
of magnitude; (2) Redis Cluster sharding by client key so no shard is hot; (3)
fail-open on timeout with a metrics spike so operators see over-admission.

**I:** How do you key per client?

**C:** `rl:{clientId}` — the API key hash or the authenticated user id. Two
subtleties: unauthenticated traffic keys on the source IP (with the caveat that
NAT'd offices share an IP — then the limiter becomes a coordination tool and you
need a token bucket per IP with higher limits, or fingerprinting); and the keys
must have a TTL of `window + slack` so Redis doesn't accumulate a ZSET per client
forever.

**I:** Memory math for the ZSET?

**C:** The ZSET holds at most `limit` members *per client per window* (trimmed on
every call). Each member is ~40-80 bytes in Redis (ziplist/skiplist encoding
depends). For 1M clients at limit 100 that's up to 100M members — that's the
number that tells you when to switch to the O(1)-memory sliding-window *counter*
approximation. I'd call that out as a deployment trigger: 1M clients × limit is
the break-even where approximation wins.

---

## Part 5: Implementation Walkthrough (10 minutes)

**C:** For the implementation I'll model Redis semantics faithfully without the
Redis dependency: a per-client sorted structure of timestamps, trim-by-score on
every call, and a hard limit check — all under one lock per client, mirroring the
Lua script's atomicity. The class structure: `allow(clientId)` returns boolean;
timestamps live in a `TreeMap`; trimming removes everything older than
`now - window`.

**I:** Why a TreeMap and not a list?

**C:** The two operations are *trim by cutoff* and *count within range* — both are
log-time range operations on a sorted map: `headMap(cutoff).clear()` trims,
`size()` counts. A list would need O(n) scans per request. And per-client locking
keeps one client's trimming from serializing everyone else's — lock contention is
per key, which matches the Redis design where each key is independent.

**C:** (writing) The window math uses a monotonic clock domain (`System.nanoTime`)
so machine clock jumps — NTP steps, suspend/resume — can't inflate or deflate the
window. I also expose `outstanding(clientId)` so the demo can inspect state.

**I:** Your demo?

**C:** Four checks. (1) Burst within limit: `limit` rapid calls all pass, the next
is rejected. (2) The boundary-hole test from earlier: after `window` elapses, the
sliding window has drained and a request passes again — with timestamps printed so
the sliding behavior is visible. (3) Sliding window *specificity*: a request just
inside the window counts, one just outside is trimmed — I'll print the cutoff.
(4) Per-client isolation: one client's rejection doesn't affect another's budget.

---

## Part 6: Failure Modes, Fallbacks, and Follow-Ups (5 minutes)

**I:** Redis dies mid-request. Walk the failure path.

**C:** The call times out → we fail open: admit the request, increment an
`overAdmission` counter, and alert. Fail open is the right default here because
the limiter protects *economics* (quota abuse), not *correctness* (a request is
still a request); bricking the API to enforce a quota is the worse failure. For
clients that need a hard cap even during outages — e.g., a billing-sensitive tier —
fail closed per-tier is the configuration knob. And the local pre-filter budget
gives us graceful degradation during partial Redis blips.

**I:** How would you test the Lua script itself?

**C:** With a real Redis in CI: fire `limit + 1` calls in one second and assert the
last is rejected; fire `limit` calls spanning a boundary and assert the exact count
inside the window; and a concurrency test — N threads racing the same key must
never admit more than `limit` — that test is exactly what catches a missing
atomicity in the script. Plus a clock-skew test driving the injected clock.

**I:** Last one: how do quotas for *different* clients share Redis, and how do you
isolate tiers?

**C:** Keyspace partitioning — `rl:free:{id}`, `rl:pro:{id}` with different limits
in the script's ARGV — and sharding by key hash in Redis Cluster. The Lua script
stays identical across tiers; the limit parameter does the work. One script,
parameterized, is much easier to reason about than N scripts.

---

## Part 7: Closing and Feedback (3 minutes)

**I:** Three takeaways?

**C:** Sliding window closes the boundary-burst hole that fixed windows can't.
Redis semantics means the decision is atomic and data-driven — a Lua script, not
client-side check-and-act. And the distributed costs are real: per-request round
trips, memory proportional to `clients × limit`, and an explicit fail-open policy
with metrics — otherwise the limiter becomes the outage.

**I:** Strong. You went straight to the concurrency question, knew the memory math
cold, and correctly made fail-open a *policy decision*. One improvement: I'd have
liked you to sketch the Lua/ZSET interaction on the board earlier — the
ZREMRANGEBYSCORE-to-ZCARD loop is the visual that sells the design — and to mention
token-bucket as the alternative for burst-tolerant clients (many APIs prefer it).

---

## Evaluation Scorecard

| Dimension | Observation | Score (1-5) |
|-----------|-------------|-------------|
| Algorithm choice | Correctly rejected fixed-window boundary burst; chose exact log over O(1) approximation with trade-off stated | 5 |
| Redis semantics | Lua atomicity, server-side timestamps, ZSET trim/count loop | 5 |
| Concurrency | Identified check-and-act race across nodes immediately | 5 |
| Distributed costs | Round-trip latency, memory math (clients × limit), sharding, local pre-filter | 5 |
| Failure policy | Fail-open with metrics; per-tier fail-closed option | 5 |
| Testing | Concurrency test for atomicity; clock injection | 4 |
| Alternatives | Token bucket mentioned only after prompting | 4 |

**Overall: Strong Hire** — the single weak spot was proactive breadth (token
bucket, gateways vs origin enforcement), not depth.

## Common Pitfalls Candidates Hit

- Presenting a fixed window as "good enough" and missing the boundary burst hole.
- Client-side check-and-act without atomicity, claiming it's fine "because Redis
  is fast."
- Using per-node wall clocks and ignoring skew across gateway nodes.
- No memory math — "Redis handles it" — when ZSET size is `clients × limit`.
- Fail-closed by default and defending it: the limiter then owns availability.
- Not trimming old timestamps and letting per-client ZSETs grow unboundedly.
