# Lab 06: Mock Interview — Distributed Caching & Invalidation

**Role**: Senior Distributed Systems Engineer
**Duration**: 45 minutes
**Company style**: CDN / Redis / cache-focused platforms

---

**Interviewer**: "What are the consistency models for a distributed cache, and how do they map to real products?"

**Candidate**: "Three levels. **(1) Cache-aside (lazy) with best-effort invalidation** — the classic: on read, check cache; on miss, load from DB and populate; on write, update DB then delete (or set) the cache key. Consistency is eventual; the window of staleness depends on invalidation latency. **(2) Strongly consistent (read-through with synchronous invalidation)** — writes go to DB and the cache *in the same transaction* (outbox pattern / CDC), or the cache is written first in a write-through topology; reads that hit a stale entry are stalled. This is what you get with 'cache-aside + synchronous invalidation in the write transaction' or with Redis as a *source of truth* for the hot keys (which is what people actually do when they claim Redis is strongly consistent — it's only strong if reads and writes both go to Redis and Redis replication is synchronous). **(3) The pragmatic middle: TTLs + versioned keys** — short TTLs bound staleness absolutely; versioned or generation-based keys invalidate atomically. Most production systems are *not* strongly consistent on the cache — they're 'eventually consistent with a bounded staleness window,' and the SLO is the TTL. The senior framing: a cache is a *performance* structure, and its consistency contract is a latency/consistency tradeoff you choose per key — 'hot but harmless if stale' gets a long TTL; 'monetary but read-mostly' gets synchronous invalidation."

**Interviewer**: "Cache invalidation in a multi-node cache cluster — how do invalidation messages propagate, and what breaks?"

**Candidate**: "Mechanisms: (1) **direct invalidation**: the writer sends a DELETE to every cache node synchronously (or the cache is a single node) — simple, but the write path takes N round trips and a missed node stays stale. (2) **Pub/sub invalidation** (Redis Pub/Sub): the writer publishes a 'key invalidated' event; cache nodes subscribe and evict. Problem: pub/sub is *at-most-once* — if a subscriber is disconnected during the publish, the invalidation is lost *forever* and the key stays stale until its TTL. (3) **CDC + outbox**: the DB's binlog (or a transactional outbox) drives invalidation — reliable, ordered, but adds a pipeline and latency. (4) **Versioned keys with generation numbers**: keys become `entity:v1`, `entity:v2` — invalidation is just *not pointing to v1 anymore*; readers atomically fetch the pointer, so there's no eviction race at all. What breaks, in practice: *lost events* (pub/sub disconnect), *ordering* (invalidation arriving before the write it invalidates — solved with versioning), *split-brain writers* (two app instances writing concurrently, one invalidating while the other repopulates — the classic 'write-through thundering herd' where a stale populate fills the cache after an invalidation), and *partial clusters* (some nodes evict, one doesn't — inconsistent reads until TTL). The engineering answer: invalidation must be *at-least-once and ordered*, which is exactly what CDC/outbox gives you and what raw pub/sub doesn't."

**Interviewer**: "The 'stale populate after invalidation' race — walk me through it and how you fix it."

**Candidate**: "The race: app instance W1 writes new value V2 to the DB, then sends 'invalidate key K'. App instance R1 had a cache miss a moment earlier, was reading the old V1 from the DB, and completes *after* W1's invalidation: R1 populates K with stale V1. Now the cache holds V1 with a fresh TTL and serves stale data for its whole lifetime — invalidation was correct but lost the race. Fixes: **(1) Write-then-invalidate with a timestamp/version check**: the populate writes `(value, dbVersion)`; an invalidation carries the invalidating version; eviction checks 'is my stored version < invalidated version?' — if so, don't repopulate (or delete again). This is the standard '**lease**' approach — Memcached/etcd-style leases: a writer takes a lease per key; readers must obtain the lease before populating; writers bump the lease, invalidating in-flight populates. **(2) Versioned keys**: populate `entity:v1`; W1 writes V2, publishes 'now point at v2'; R1's populate of v1 is harmless — readers stop asking for v1. This is why generation-based keys are the cleanest fix: *you never delete, you just stop referencing*. **(3) DB-timestamp compare-and-set on populate** — populate is conditional on 'still the latest DB version'; the DB (or a coordination service) arbitrates. The interview answer: the race is real and classic; the fix is either a lease/version gate on populate, or versioned keys so invalidation becomes pointer movement instead of deletion."

**Interviewer**: "Thundering herd — same cache miss, many concurrent readers, all populate simultaneously. How do you prevent it?"

**Candidate**: "The herd: K expires; 1000 requests miss simultaneously; all 1000 hit the DB and all 1000 populate the cache — the cache miss *amplifies* load by 1000× on the DB. Fixes, in order of preference: **(1) single-flight / request coalescing**: the first miss acquires the 'loading' state; others wait on it (either a per-key mutex in one node, or a distributed lock — `SETNX` in Redis, or a conditional 'only one populate' via compare-and-set); when the loader finishes, waiters read from cache. **(2) Leases (again)**: a per-key lease prevents stale populates *and* herds — the lease holder is the only legitimate populator. **(3) Early/delayed revalidation with expiry randomization**: add jitter to TTLs and refresh before expiry ('TTL with refresh-at-80%'), so keys don't expire in lockstep — herd-forming expiries are often *correlated* (all keys with the same TTL expiring at the same moment). **(4) Precompute on write**: for expensive computed data, refresh the cache *on write* so misses are rare. The metric to track: *peak DB QPS per cache-miss event* — the herd is exactly the spike, and single-flight + jitter should flatten it."

**Interviewer**: "Where does the cache live? Same-process vs a Redis cluster — design tradeoffs for a latency-sensitive service."

**Candidate**: "**Same-process (local) cache**: nanoseconds, zero network — great for per-node hot data (configs, current-user session state), but each node has its own copy → N copies to invalidate, memory multiplied, and invalidation becomes a broadcast problem. **Distributed (Redis/cluster)**: one shared store, single invalidation point, consistent view, but +1 RTT per access (~0.1-1ms) and the cache itself becomes an availability dependency (its failure cascades to the app — hence circuit breakers: on cache failure, go to DB, don't 503). **Hybrid, the production answer**: L1 (local, tiny, TTL-seconds) + L2 (distributed, TTL-minutes) — reads hit L1 first; L1 invalidations are *lazy* (short TTL + version polling), L2 is the authority. The design rules: (a) anything with per-node locality (config, feature flags) → L1; (b) shared hot data → L2; (c) the L1 TTL bounds the invalidation window of the hybrid; (d) always have a DB fallback path with timeouts on the cache call — a cache that can take down your service is a bug. Also worth naming: **cache stampede protection** needs to exist *per node* in the hybrid (each L1 is a potential herd point) — the local mutex + jitter applies at L1 too."

**Interviewer**: "Design question: a service reads a user's profile on every request; profiles change rarely (a few % of users per day); the service runs 100 instances; reads are 10k RPS. Design the caching."

**Candidate**: "Given rarity of writes and read dominance: **cache-aside with generation-based keys + L1/L2 hybrid + async invalidation via CDC.** Details: L2 = Redis cluster (sharded by user id, replica factor 2, cluster mode), keys `profile:{userId}:{generation}` with a small pointer key `profile:{userId}` → generation; reads: L1 (local, TTL 10s, size-bounded LRU per instance) → miss → L2 pointer+profile (TTL 10 min) → miss → DB. Writes: DB update → transactional outbox → CDC/worker publishes 'bump generation for userId' → Redis pointer updates to g+1; L1s don't get invalidation messages at all — they just age out in ≤10s (bounded staleness, zero broadcast). Old generations' profiles stay in Redis until TTL — no eviction race, no herd on the DB for cold generations... wait — one risk: the DB read on miss is still possible for a user never seen before — 10k RPS with a few % cold misses is fine with single-flight per key. Why not synchronous invalidation: writes are rare (a few % of users/day) — a CDC pipeline with ≤1s latency gives 1-second staleness for a value that changes daily; synchronous invalidation would add a dependency on every write path for zero user-visible gain. The senior point: *match the staleness budget to the data's change rate* — profile staleness of 1-10s is invisible; the complexity saved is real."

**Interviewer**: "How do you test a cache system? What invariants matter?"

**Candidate**: "Three categories. **(1) Correctness invariants under race**: simulate the stale-populate race (writer invalidates while a reader populates) — the versioned/lease approach must never serve a value older than the last acknowledged write *after* the invalidation completed (bounded staleness); property-test random interleavings of writes/reads/invalidations and check 'the cache never serves V1 after V2's invalidation was applied and the populate ran' — that's the core linearizability-ish check for the *observed* value stream. **(2) Failure behavior**: cache cluster down → app falls back to DB (circuit breaker trips, no 5xx); cache partial failure (one shard down) → only that shard's keys go to DB; pub/sub disconnect during invalidation → the key is *at most TTL-stale*, never forever-stale — the TTL is the safety net, and a test must assert it. **(3) Performance/load**: herd test — expire one key, fire 10k concurrent reads, assert DB sees ~1 query (single-flight); peak-QPS flattening with TTL jitter; P99 tail under cache-node failure. The interview answer: *correctness under races, bounded staleness under failures, and load flattening under misses* — those three are the cache test suite."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| Consistency model | Bounded staleness framing, per-key choice |
| Invalidation | At-least-once + ordering; pub/sub vs CDC/outbox |
| Races | Stale-populate race and the lease/version fix |
| Load behavior | Single-flight, leases, TTL jitter vs herd |
| Topology | L1/L2 hybrid, circuit breakers, DB fallback |
| Testing | Race invariants, failure fallbacks, herd metrics |

### Candidate strengths
- "Invalidation must be at-least-once and ordered — CDC gives that, pub/sub doesn't" — crisp.
- Generation-based keys as *pointer movement instead of deletion* — the correct mental model.
- The design answer matched staleness budget to change rate — a senior instinct.

### Gaps to work on
- Didn't explicitly cover **write-through vs write-behind** caches (populate-on-write) as alternatives — mentioned only briefly.
- Could have named **Redis `SETNX` / conditional populate** mechanics for single-flight in the herd answer.
- No mention of **cache warming** (populate popular keys before traffic peaks).

## Follow-up study prompts
1. Implement the stale-populate race with a lease gate and prove the fix: what exactly does the lease version check compare?
2. Why is Redis pub/sub invalidation *at-most-once* — and what does a delivery guarantee upgrade (streams/consumer groups) change?
3. Derive the herd amplification factor: N concurrent misses → DB QPS, with and without single-flight, with and without TTL jitter.

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Let's go deep on write-through vs write-behind — the populate-on-write family. When do you use each?"

**Candidate**: "**Write-through**: the write path updates the cache *synchronously* with (or right after) the DB write — every write populates the cache, so reads almost never miss. Pros: no invalidation race at all (the cache never holds a pre-write value — it holds the post-write value); simplest consistency story. Cons: the write path pays cache latency and the cache can be a write bottleneck; if the cache write fails, you must decide (fail the write, or proceed and risk staleness). **Write-behind (write-back)**: writes go to the cache first; a background worker flushes to the DB — batch and coalesce. Pros: massive write throughput (batched, sorted, deduped — the classic for time-series and counters). Cons: *data loss window* — a cache crash between accept and flush loses the write; the cache is now a durability dependency, so you need WAL-flavored recovery. The selection rule: *write-through for consistency-critical low-rate writes; write-behind when the write rate exceeds the DB's capability and the durability budget permits a flush window*."

**Interviewer**: "Cache stampede — walk me through the *full* anatomy: not just the miss, but the expiry herd and the coordination failure."

**Candidate**: "Three overlapping mechanisms. (1) **Concurrent miss (the herd proper)**: key expires → N concurrent readers all miss → all N query the DB simultaneously → DB spikes to N× QPS; the spike is *synchronized* because all N saw the expiry at the same moment. (2) **Expiry herd (correlated expiry)**: keys with identical TTLs expire in lockstep — a dashboard rendering 100 charts with the same TTL expires 100 keys at once → a combined spike; the fix is TTL *jitter* (each key's TTL is base + random offset). (3) **Coordination failure (the deep one)**: the single-flight lock itself must not be the bottleneck — if the lock has a TTL shorter than the DB query, the lock expires *while the loader is still loading* → a second wave of loaders starts → the herd re-forms *through* the protection. The engineering answer: jitter the TTLs, bound the single-flight lock duration to (DB query timeout + margin), use lease-with-version (from the Facebook paper) so late loaders can't install stale values, and monitor 'loads per expiry' as the metric."

**Interviewer**: "A stale-value incident: cache says V1, DB says V2, for 4 hours. How do you investigate, and what are the possible root causes?"

**Candidate**: "Investigation path: (1) confirm the staleness window (when did the cache start serving V1?) and correlate with deployments/writes; (2) check *which* mechanism could have served V1 after V2's invalidation: (a) **missed invalidation** — pub/sub subscriber disconnected at publish time (at-most-once loss) and TTL was long → staleness until TTL; (b) **stale populate** — a loader that started before the write installed V1 after it (the race we discussed); (c) **expired lease/version check** — the version gate compared against a stale pointer; (d) **clock skew** — if the version is timestamp-based, the writer's clock behind the loader's clock; (e) **backup/restore artifact** — someone restored a snapshot. The fix depends on the cause: (a) switch invalidation to streams/CDC; (b) add the version gate; (d) monotonic logical clocks. The interview point: *each mechanism has a distinct signature — the fix is only correct if you identify which of the five produced the stale window*."

**Interviewer**: "Cache eviction policies — LRU vs LFU vs ARC vs TTL. What do you pick for a read-heavy e-commerce catalog?"

**Candidate**: "**LRU** — evict least-recently-used: good when recency tracks popularity; pathological for *scan* workloads (a one-time scan flushes the hot set — 'cache pollution'). **LFU** — evict least-frequently-used: robust to scans, but never adapts to *new* hot items (an item popular last week blocks a new one) and needs frequency decay. **ARC** (adaptive replacement) — balances recency and frequency with two ghost lists, adaptively weighted: the standard answer for web workloads, hard to implement well. **TTL-based** — bounded staleness, not an eviction policy. For a catalog: recency + frequency both matter (seasonal items spike, evergreen items stay hot) → **ARC or a size-bounded LFU with decay**; plus *TTL as the correctness layer* (staleness bound) and LRU/LFU as the *capacity* layer. The rule: *TTL handles correctness, the eviction policy handles memory — pick the policy by the workload's access distribution, and protect the hot set from scans*."

**Interviewer**: "Final: caching and the failure domain — the cache cluster is down. What's the app's behavior, and how do you test it?"

**Candidate**: "The app must **degrade to the DB with a bounded circuit**: on cache connection failure, (1) fail fast on the cache call (short connect/read timeouts — 10-50ms), (2) trip a per-shard circuit breaker, (3) serve from the DB with the request *still meeting its SLO* (DB reads for hot keys are often fast enough), (4) when the cache returns, the breaker half-opens and traffic flows back. The failure design: *the cache is a performance dependency, never an availability dependency* — a cache outage must cost latency, not 5xxs. Testing: (a) kill the cache cluster in staging with traffic → assert zero 5xx and SLO p99 degradation within budget; (b) kill *one* shard → assert only that shard's keys hit the DB; (c) slow the cache (artificial latency) → assert the circuit trips and the app stops waiting on it; (d) the recovery: cache back → breakers close → load shifts back without a stampede (the DB fallback had been absorbing traffic — shift back gradually, e.g., TTL-staggered or per-shard reopening)."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Lead with the *five-mechanism* staleness taxonomy in any incident-style question — it structures the answer instantly.
- Prepare the write-through/write-behind decision with the durability-budget framing — it was slightly late in the answer.
- Rehearse the expiry-herd story including the *lock-expiry* failure — the deepest stampede point.

### One-sentence takeaway
- "A cache is a latency optimization with a correctness contract: TTL bounds staleness, version gates kill races, single-flight kills herds, and the DB fallback + circuit breaker make the cache a performance dependency instead of an availability one."

### Self-check questions (run before the real interview)
1. Can I enumerate the five stale-value root causes with their signatures?
2. Can I explain write-through vs write-behind including the loss window?
3. Can I derive the stampede anatomy (miss herd, expiry herd, lock expiry)?
4. Can I compare LRU/LFU/ARC/TTL for a given workload?
5. Can I specify the cache-failure behavior: timeouts, circuit, DB fallback, gradual recovery?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** What is the stale-populate race?
**Hint.** A loader that started before a write installs the old value after invalidation — version gate or lease fixes it.

**Q2.** Why is pub/sub invalidation at-most-once?
**Hint.** A disconnected subscriber misses the publish with no replay — staleness until TTL.

**Q3.** What does a generation-based key buy you?
**Hint.** Invalidation = pointer movement, not deletion — no eviction race, old generations age out.

**Q4.** How does single-flight stop a stampede?
**Hint.** One in-flight loader per key; waiters attach to the same future; DB sees ~1 query.

**Q5.** Why jitter TTLs?
**Hint.** Correlated expiry — keys with identical TTLs expire in lockstep and spike the DB together.

**Q6.** What happens if the single-flight lock expires mid-load?
**Hint.** A second wave of loaders starts — the herd re-forms *through* the protection; lock TTL > query time.

**Q7.** Write-through vs write-behind — the loss window.
**Hint.** Write-through: none (cache always fresh); write-behind: crash between accept and flush loses writes.

**Q8.** LRU vs LFU — each one's pathological case.
**Hint.** LRU: scans flush the hot set; LFU: never adapts to new hot items (needs decay).

**Q9.** The cache cluster is down — what's the app behavior?
**Hint.** Fast-fail timeouts, per-shard circuit trip, DB fallback, no 5xx — cache is a performance dependency.

**Q10.** Name the five stale-value root causes.
**Hint.** Missed invalidation, stale populate, expired lease gate, clock skew, backup restore.

### Scoring
- **8-10 correct**: ready for the caching loop.
- **5-7**: revise the race taxonomy and stampede anatomy.
- **<5**: re-read the walkthrough before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab (`InvalidationCache`) and pass the stale-populate and herd tests.
**Day 3**: Quick-Fire rounds; write the five-root-cause taxonomy from memory.
**Day 4**: Rehearse the stampede anatomy (miss herd, expiry herd, lock expiry).
**Day 5**: Drill the extended rounds (write-through/behind, eviction policies, failure domain).
**Day 6**: Mock interview, 45 minutes, no notes.
**Day 7**: Score against the Debrief table; study the follow-up prompts.
