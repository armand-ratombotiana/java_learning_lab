# Mock Interview Transcript: Distributed Cache with Cache-Aside and Invalidation

| Field | Detail |
|-------|--------|
| **Level** | Senior Backend Engineer (L5) |
| **Duration** | 45 minutes |
| **Format** | System design whiteboard + implementation |
| **Problem** | "Implement a distributed cache with cache-aside semantics and a write-through invalidation path for a read-heavy product service." |

---

## Part 0: Scene Setting (2 minutes)

**Interviewer (I):** We have a product catalog service. Reads dominate: 95% reads,
5% writes, and reads must be fast — P99 under 10 ms, served from cache. The data
lives in a relational store of record. Your job: design and implement the caching
layer — the read path and the write path — and tell me exactly what can go stale,
when, and why. Start with clarifying questions.

**Candidate (C):** Understood. Let me pin down the semantics before designing.

---

## Part 1: Clarifying Questions (5 minutes)

**C:** Three things. One: is a read that returns stale data ever acceptable, and for
how long? Two: do writes go through this service, or do other services write
directly to the database? Three: can a single read miss cost a database query, or
are some keys so hot that a miss is catastrophic?

**I:** One: a bounded staleness of seconds is acceptable; minutes is not. Two:
writes go through this service today, but other services may write later. Three:
a single miss costs a DB round trip; hot keys are a concern you should design for.

**C:** Good — "bounded staleness, writes via this service, hot keys matter" is
exactly the shape of a cache-aside design. The write path is where correctness
lives; the read path is where performance lives. Let me lay out both.

---

## Part 2: The Read Path — Cache-Aside (7 minutes)

**C:** The read path is cache-aside:

```
read(key):
  1. value = cache.get(key)          // ~1ms, Redis
  2. if hit: return value
  3. miss -> value = db.load(key)    // ~5-20ms
  4. cache.put(key, value, ttl)
  5. return value
```

The name is literal: the cache sits *aside*; the application orchestrates it and
owns the miss-handling. The alternatives — read-through, write-through,
write-behind — invert responsibility and, for this workload, buy us nothing the
write path doesn't already give us.

**I:** Why not read-through with a cache like Redis Enterprise that supports it?

**C:** Read-through pushes the load-on-miss into the cache tier, which means the
cache now needs DB credentials, backpressure logic, and your data model — it stops
being a dumb tier. Cache-aside keeps the policy in the service where it's
testable, and the failure mode is simple: if the cache is down, we bypass it and
hit the DB directly. That's the first rule of cache-aside — **the cache must never
be in the critical path of availability**.

**I:** What about the stampede problem?

**C:** On a miss, every concurrent request reloads the DB — a thundering herd.
Three mitigations: (1) a single-flight in-process lock so only one request per key
loads and the rest wait on the same future; (2) a hot-key or "stale-while-revalidate"
variant where we serve the last known value while refreshing in the background;
(3) probabilistic early-expiration (jittered TTLs) so a hot key doesn't expire
synchronously for everyone at the same second. In the implementation I'll do
single-flight with `computeIfAbsent`-style memoization — deterministic and easy to
test.

---

## Part 3: The Write Path — Invalidation (8 minutes)

**C:** Now the write path, which is the part that's actually hard. Two options:
update the cache on write, or invalidate it on write. I choose **invalidation**,
and here's the reasoning.

**I:** Go on.

**C:** Update-on-write has a classic race. Thread A reads the value, thread B writes
the DB and updates the cache with the *new* value, thread A's read returns and then
overwrites the cache with the *old* value it read. You now have a permanently stale
cache until TTL. This is the "read-then-write race" or lost-update. Invalidation
avoids the race entirely: after the DB commit we *delete* the key, and the next
read does a clean reload from the DB. The cost is one extra miss per write, which
is trivial at a 95/5 read/write ratio.

**C:** There's one more ordering subtlety: we must **invalidate after the DB
commit**, never before. If we invalidate first and the write then fails, the cache
is cold — safe, just a miss. If we invalidate *before* commit and the cache is
repopulated from a concurrent read *before* the commit lands, that read can load
the old value and we're stale until TTL. Invalidate-after-commit closes the
window: any read that repopulates after invalidation reads the new committed value.

**I:** But the read between commit and invalidate returns the old cached value.

**C:** Yes — that's the bounded staleness you accepted. It's a few milliseconds,
and it's the *price of not putting the DB and cache in one transaction*. The
alternative is a write-through double-write in a transaction, which either drags
the DB transaction into the cache (a failure to invalidate then fails the business
write — wrong coupling) or needs a compensating mechanism anyway. Invalidation is
the pattern that fails *open*.

---

## Part 4: Multi-Service Writes and the Invalidation Bus (6 minutes)

**I:** You said other services may write later. What breaks?

**C:** If service B writes directly to the DB, service A's cache doesn't know to
invalidate — the pattern silently degrades to "stale until TTL," and with long
TTLs that's how you get support tickets about phantom data. The standard fix is an
**invalidation bus**: every writer publishes a `ProductUpdated {id}` event after
its commit (ideally via the transactional outbox from the previous question), and
every cache owner subscribes and deletes the key. This decouples "who wrote" from
"who invalidates" and it composes with TTL as a backstop. In this implementation
the invalidation bus is the in-process listener list — the demo of the same shape.

**I:** What if invalidation events are delayed or lost?

**C:** That's what TTL is for — the backstop. TTL sets the *maximum* staleness
under event loss; the bus reduces the *typical* staleness to milliseconds. The
design is only correct because both layers exist. If an event is permanently lost,
we still converge when the TTL expires.

---

## Part 5: Implementation Walkthrough (10 minutes)

**C:** The implementation has four pieces: a TTL-aware entry, the cache with hit
and miss counters, the store of record, and the service wiring read/write paths.
Let me write it.

**C:** (writing) The entry stores value plus absolute expiry — `Instant.now().plus(ttl)`
at write time, so expiry is monotonic and doesn't depend on when the reader checks.
Expired entries are treated as misses and removed lazily on read; that keeps
`get` a single map operation.

**I:** Why lazy expiry instead of a sweeper?

**C:** A sweeper is a second moving part — a timer that touches the whole map. Lazy
expiry on read is O(1), correct for a bounded-staleness contract, and leaves the
hot path single-lookup. If keys are numerous and rarely read we'd add a
probabilistic sweeper later; for a catalog it's wasted machinery.

**C:** (writing) The cache-aside service implements the read path — check cache,
on miss load from store, populate with TTL; the write path — store of record
commits first, then invalidation; and single-flight so concurrent misses share one
DB load.

**I:** Walk me through your demonstration.

**C:** Four scenarios. (1) A read warms the cache — miss, then hit. (2) A write
invalidates — the next read reloads and sees the new value. (3) TTL expiry — after
the TTL elapses, the value is evicted on read. (4) Single-flight — N concurrent
readers of a cold key produce exactly one store load. The counters make each
scenario observable: hits, misses, invalidations, store writes, store loads.

---

## Part 6: Consistency, Hot Keys, and Failure Modes (6 minutes)

**I:** Where does this system disagree with the database, and for how long?

**C:** Three windows. Write-commit to invalidation: milliseconds of stale reads.
Event lost or delayed: stale up to TTL. Replica lag, if the store is a replica:
bounded by replication lag. All three are bounded and all three shrink with the
same tool — shorter TTL. That's the tension: short TTL bounds staleness but raises
miss rate and DB load; long TTL cuts DB load but widens staleness. The TTL is the
slider between them, and the invalidation bus moves the whole system to the cheap
side of that trade.

**I:** Hot keys?

**C:** A single celebrity key can saturate a shard. Mitigations: local in-process
caches in front of the distributed cache for the hottest keys (bounded, with
replicated invalidation); jittered TTLs to desynchronize expiry; and for the
absolute worst cases, serve a versioned snapshot and accept sub-second staleness
by design. The single-flight lock also caps the DB load a hot key can generate.

**I:** Cache outage?

**C:** Cache-aside fails open — reads bypass to the DB, latency degrades to DB
speed but the service stays up. We must never treat a cache exception as a request
failure; we log, increment an error counter, and serve the read from the store.
That's a non-negotiable rule of this pattern.

---

## Part 7: Closing and Feedback (3 minutes)

**I:** Summarize in three lines.

**C:** Cache-aside keeps the cache out of the availability path and makes misses
cheap and deterministic. Invalidation-after-commit eliminates the lost-update
race that update-on-write can't avoid. TTL is the staleness bound, the bus is the
staleness reducer, and single-flight protects the DB from stampedes.

**I:** Very strong. You immediately separated performance from correctness,
identified the read-then-write race without prompting, and were precise about the
staleness windows. Minor notes: you could have quantified the TTL selection
(nominal vs peak request rate, expected stale window) and spent a moment on memory
sizing — eviction policy was implied but not stated. Say "LRU" or "LFU" out loud
next time.

---

## Evaluation Scorecard

| Dimension | Observation | Score (1-5) |
|-----------|-------------|-------------|
| Read path design | Correct cache-aside shape; cache never in availability path | 5 |
| Write path | Chose invalidation over update; identified read-then-write race | 5 |
| Ordering | Invalidate-after-commit rationale was precise and correct | 5 |
| Multi-service | Invalidation bus + TTL backstop layering | 5 |
| Failure modes | Fail-open on cache outage; bounded staleness enumeration | 5 |
| Hot keys | Single-flight, jittered TTL, local caches, stampede mitigations | 4 |
| Depth beyond prompt | Memory sizing / eviction policy not proactively discussed | 3 |

**Overall: Strong Hire** — production-grade reasoning with clean separation of
correctness vs performance concerns.

## Common Pitfalls Candidates Hit

- Choosing update-on-write and defending it past the lost-update race.
- Invalidating *before* the DB commit and claiming the system is correct.
- Treating cache exceptions as request failures instead of failing open.
- No single-flight, then hand-waving "a lock" when asked about stampedes.
- Claiming the cache is "always consistent" — the honest answer is bounded
  staleness with a backstop.
- Forgetting that other writers invalidate the pattern; the bus is a requirement,
  not an upgrade.
