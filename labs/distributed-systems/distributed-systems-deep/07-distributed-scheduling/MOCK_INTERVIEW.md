# Lab 07: Mock Interview — Distributed Scheduling & Rendezvous Hashing

**Role**: Senior Distributed Systems Engineer
**Duration**: 45 minutes
**Company style**: infrastructure / load balancing / data platforms

---

**Interviewer**: "What is rendezvous hashing (HRW — highest random weight), and what problem does it solve that consistent hashing doesn't?"

**Candidate**: "Rendezvous hashing (highest-random-weight) assigns each key to the node with the *highest* `hash(key, node)` — a per-node random weight computed deterministically for that key. Every node independently computes the same argmax, so the assignment requires **no coordination and no routing table state** — that's the headline property. Why it beats consistent hashing: consistent hashing needs a ring, and mapping a key to a node needs the ring sorted (or a sharded ring in production, like Dynamo's) — plus its classic weakness: **removing/adding nodes maps keys to the *next* node on the ring, which is a fixed neighbor**, so one node's departure dumps *all* its keys onto a single successor (imbalance + cascading load). Rendezvous spreads the remap uniformly: when node X leaves, each of its keys re-hashes to a different highest-weight node among the survivors — the load redistribution is **uniformly random across all remaining nodes**, which is the ideal for cache-rebalancing. The cost: O(N) hashes per key lookup vs O(log N) for a ring, and no inherent data-locality ('node's keys stay on it if present' — actually rendezvous *does* have good locality: the most stable property is that a key migrates only if its preferred node is gone). When to choose which: cache sharding with churn → rendezvous; range-scan-friendly ordered partitioning (Cassandra) → ring; near-equal performance when node counts are stable, but rendezvous's uniform remap makes it the better default for load-balancing/scheduling workloads."

**Interviewer**: "So when node X leaves a rendezvous cluster, what exactly happens to X's keys? Walk me through the math."

**Candidate**: "Each key of X is assigned to the node with max weight among *the surviving nodes* — X is simply not a candidate anymore. For each key, the argmax among the survivors is approximately uniform: with N nodes remaining, each key lands on each survivor with probability ≈ 1/N — because the weights are independent random values per (key, node) pair. So X's M keys are redistributed as a *uniform* shuffle over the N survivors — each survivor receives about M/N of them. Two crucial properties: (1) **no double failure cascade** — a single-node departure cannot create a hot successor, unlike the ring (Dynamo mitigates with virtual nodes/random tokens, but rendezvous gets it for free); (2) **locality**: the keys of *other* nodes are untouched — the only assignments that change are those whose argmax was X, because the weight of every other node is unchanged and the max among them is unchanged... wait, careful: the argmax among survivors for key k could *change* even for keys not owned by X? No — if key k's max was node Y ≠ X, then among the survivors Y still has the same weight and X is gone, so Y's weight still beats all others — the argmax is unchanged. That's the *consistency/anti-swap* property: **only the dead node's keys migrate**. The interview math: expected rebalance per node = M/N with variance ~M/N — vs the ring's M-on-one-successor."

**Interviewer**: "What about a node *joining*? Same analysis?"

**Candidate**: "Yes, symmetric: the new node Z is added as a candidate; for each key, the argmax is recomputed over N+1 nodes. A key migrates to Z iff Z's weight for that key exceeds the previous max — since Z's weights are independent random, the probability a given key moves to Z is ~1/(N+1), so Z pulls roughly N_keys/(N+1) keys *uniformly from every existing node* — each old node loses about 1/(N+1) of its keys. Again: no hot spots, no coordination — every node just includes Z in its weight computation from now on. This is what makes rendezvous beautiful for **membership churn**: add/remove cost is zero (no ring maintenance, no token ranges, no re-shard), and the load distribution stays near-uniform at every step. The one thing you lose vs a ring: **ordered range scans** (you can't range-scan a rendezvous assignment — keys aren't sorted by owner). And the cost per lookup is O(N) — mitigate with a *shortlist*: cache the top-K candidates per key, recompute only when membership changes."

**Interviewer**: "Where does rendezvous hashing show up in real systems, and where is it *not* the right answer?"

**Candidate**: "Real uses: (1) **cache-tier routing** — e.g., many CDN edge caches and key-value cache clients use HRW so that a cache-node failure redistributes *its* keys uniformly instead of piling them on a neighbor; (2) **leader/replica selection in replicated logs** — pick the node with max weight for a topic partition across replica candidates (e.g., some streaming systems); (3) **consistent load assignment in schedulers** — distributing work items across workers so churn causes minimal reshuffling; (4) OpenStack Swift and Riak have used it for ring-free key→node mapping. Where it's *not* right: (a) **ordered data** — range queries, secondary-index scans, or anything needing sorted ownership (Cassandra's tokens are better); (b) **huge key sets with high lookup rates** — O(N) per lookup matters when N is large (mitigate with shortlists/caching, or switch to a ring); (c) **locality-sensitive data placement** — if you want data physically near its producers or co-located with related data, HRW's uniformity actively fights your layout; (d) **when members are 'weighted' unequally** — rendezvous assumes equal capacity per node; uneven hardware needs weighted variants (weighted HRW exists but is fiddlier). The interview answer: HRW is the *churn-resilient uniform* assignment — reach for it when membership changes and uniform remap matter more than ordering or O(1) lookup."

**Interviewer**: "Weighted rendezvous — unequal nodes, e.g., a 2x-capacity machine. How do you handle it?"

**Candidate**: "The classic trick: **virtual nodes** — a physical node with weight w is represented by w virtual nodes (or a scaled count), each with its own random weights; the key maps to the physical node owning the highest-weight virtual node. Since the weight distribution is i.i.d., the chance a key lands on a physical node is proportional to its virtual count — exactly proportional to capacity. Cost: O(V) hashes per lookup where V = total virtual nodes — that's the price of weighting. Alternatives: (a) **weighted seed**: replace the plain hash with `hash(key, node) / weight` or hash with a node-specific per-weight adjustment — cheap but less statistically clean than virtual nodes; (b) **two-level**: HRW to select a *class* of nodes, then HRW within the class — hierarchical weighting for geo/capacity tiers; (c) **annealed weights**: treat the weight as a multiplier in the argmax: pick `argmax(w_node · random(key,node))` — the 'wasted randomness' is acceptable in practice but biases subtly. The practical engineering answer: virtual nodes are the standard; pick the virtual-node factor so total hashes per lookup stays within budget (e.g., cap V at a few hundred), and measure the resulting distribution's imbalance vs the ideal."

**Interviewer**: "Design: a session-store tier, 40 cache nodes, sessions are sticky by user id; the tier must survive node loss *without* session flooding, and sessions must *never* move except when their node dies. Design the routing."

**Candidate**: "This is exactly HRW's sweet spot — sticky + churn-resilient: **rendezvous hashing over user-id → node, with a shortlist cache.** Routing: `node = argmax(hash(userId, candidate) for candidate in liveNodes)` — deterministic, no state, O(40) per lookup — trivial at that scale, but I'd still keep a per-user shortlist (`Map<userId, node>` with a membership-version tag) so steady-state routing is O(1). Sticky: sessions live on their node; a node *leaving* (failure or deploy) remaps only its own users, uniformly across the 39 survivors — each survivor absorbs ~1/39 of the dead node's sessions, which is the smallest possible per-node shock, and no user of a *live* node ever moves. Deploys: drain a node (mark it leaving → readers stop routing to it, session TTLs expire, then remove from the live set) — no double-booking, no surge. The failure path: sessions of the dead node are *lost* anyway (no cross-node session store) — so the design pairs HRW routing with **DB-backed session storage + cache-in-front**, so a node loss means cache misses and rehydration from DB, not logged-out users. The interview answer: HRW gives 'minimal, uniform remap on churn + zero movement otherwise' — then the *storage* design (DB fallback) covers the inevitable loss."

**Interviewer**: "How do you test HRW correctness and quality? What would you assert?"

**Candidate**: "Three groups. **(1) Correctness of assignment** — determinism: same (key, node-set) → same owner every time; the owner is *a member of the live set* (never a dead node); consent: any two replicas computing independently agree (no distributed state to disagree). **(2) Quality/balance** — after a random initial assignment of K keys to N nodes, the max-min ratio of loads is near 1 (bound by O(log N / log log N) for balls-into-bins — assert max load ≤ (K/N)·(1 + small ε)); on node death, assert the dead node's keys are spread across ALL survivors (uniformity: max survivor gain ≤ ~(M/N)·(1+ε)) and that **no other node's keys moved** (locality invariant — the anti-swap property). **(3) Churn sequences** — random add/remove sequences; invariants: (a) a key never moves when its owner is alive (locality); (b) total movement after k churn events ≈ expected (M/N per event); (c) distribution stays uniform at every step (monitor the balance metric after each event). Also a **statistical check**: over many keys, the empirical distribution of 'owner' is uniform (chi-square-ish eyeball via max deviation). The point: HRW's correctness is trivial to state (argmax) — the *quality* properties (uniformity, locality, bounded remap) are what the test suite must actually measure."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| Core definition | Argmax of hash(key, node); zero-coordination |
| vs ring | Uniform remap on churn vs successor pile-up |
| Locality | Only dead node's keys move (anti-swap) |
| Weighting | Virtual nodes, proportionality to capacity |
| Fit | Where HRW is wrong (ordering, O(N) lookup) |
| Testing | Determinism, balance ratios, churn invariants |

### Candidate strengths
- Stated the anti-swap/locality property precisely with the weight-unchanged argument.
- The balls-into-bins bound in the testing answer showed real depth.
- Chose the right tool for the design question and justified it against the ring.

### Gaps to work on
- Didn't mention **consistent hashing's virtual-node fix** as a parity argument until the end (Dynamo's random tokens) — naming it earlier sharpens the comparison.
- Could have discussed **hash quality requirements** (the hash must be uniformly distributed and stable across restarts — use a real hash like Murmur3/xxHash, not `String.hashCode()`).
- Weighted-variant answer was good but the 'wasted randomness' alternative deserved a sharper dismissal.

## Follow-up study prompts
1. Prove the locality property: if key k maps to node Y and Y is alive, removing/adding other nodes never changes k's owner.
2. Implement weighted HRW with virtual nodes and measure the empirical distribution vs the proportional target.
3. Compare churn behavior head-to-head: ring with VN, ring without VN, rendezvous — measure max-load and movement per event on the same churn sequence.

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Let's go deep on the hash quality. What happens if the hash function is weak — non-uniform or correlated across keys?"

**Candidate**: "The argmax over i.i.d. weights is only uniform if the *weights are actually i.i.d.* — a weak hash breaks that in two ways. (1) **Non-uniform weights**: if `hash(key, node)` concentrates (e.g., CRC32 with a poor key format where many keys collide in the low bits — the classic 'hash the string with its trailing digits' bug), the *distribution of argmax* skews: some nodes win far more often → load imbalance *even at rest*, and churn redistribution inherits the skew. (2) **Correlation**: if weights for different keys are correlated per node (e.g., a hash that maps whole ranges of keys to the same pattern), keys cluster onto the same nodes — same imbalance. The engineering fix: use a well-mixed hash (Murmur3/xxHash/SipHash) over a *combined* canonical string `key + ':' + node` (or two calls mixed), and *test empirically* — generate 100K keys, assert the per-node assignment histogram is within statistical bounds of uniform, and re-run after churn. The rule: *HRW is only as uniform as its hash — validate the hash on representative key distributions, not theoretical ones*."

**Interviewer**: "You're using HRW for *replica selection*: a shard needs 3 replicas on 3 distinct machines. How do you extend the argmax to pick k distinct nodes?"

**Candidate**: "The standard technique: **HRW with exclusion** — pick the argmax; then pick the argmax over the *remaining* nodes (excluding the chosen one); repeat k times. Each round's weights are still i.i.d. over the remaining set, so each replica placement is uniform *given* the exclusions — and the properties carry over: locality (an excluded/dead node only affects keys that selected it) and uniformity (replicas spread across the fleet). The subtlety: exclusion-based HRW biases against co-location *in expectation* — replicas of the same shard land on distinct nodes with probability ~1, which is what you want. Practical refinements: (1) **failure-domain awareness** — exclude by *rack/zone*, not just node: `argmax over nodes not in the chosen failure domains`; (2) **weighted exclusion** for heterogeneous fleets; (3) **deterministic tie-break** so concurrent selectors agree. The interview answer: *selection-with-exclusion preserves HRW's guarantees per round and gives you rack-aware placement for free*."

**Interviewer**: "Hot keys beat any hash — one key gets 1000x the traffic of the median. HRW doesn't fix that. What does?"

**Candidate**: "True — hashing only balances the *assignment*, not the *demand*. The fixes are demand-side: (1) **sub-sharding the hot key**: split `celebrity_42` into `celebrity_42_0..15` (with the same content on each) — reads fan out to 16 sub-keys and merge; the assignment stays HRW but the *unit* is now the sub-key; writes must broadcast to all sub-keys (consistency cost). (2) **caching in front**: the hot key's traffic is by definition repeated reads — a cache absorbs the skew before it reaches the shard (the classic: a celebrity's profile is cached everywhere). (3) **capacity planning per key**: with *weighted* HRW, the hot key's shard gets extra virtual weight... that helps only if the hot key is known in advance — which for celebrity spikes it is (event → pre-provision). (4) **admission control**: if the hot key is a genuine overload, shed load with degraded responses. The interview point: *assignment hashes solve distribution, not popularity — the toolkit for hot keys is sub-sharding, caching, pre-provisioning, and shedding*."

**Interviewer**: "Scheduling with *affinity* — a task should run near its data (a worker that already has the data cached). Can HRW express that?"

**Candidate**: "Yes — with a **weighted preference** variant: `argmax over nodes of H(task, node) + A(task, node)` where A is the affinity bonus (e.g., 'this node has the data cached' +10, or the node's current load as a negative). The math: the random component keeps assignments spread; the bonus *biases* the argmax toward preferred nodes without making them deterministic. Two properties to verify: (1) the bias must be *stable* — the same task prefers the same node across scheduling rounds (cache warmth), which holds because both terms are deterministic per (task, node); (2) the bias must not overfit — a huge bonus pins tasks and destroys churn-resilience (removing the pinned node triggers maximal movement). The production trick: make the bonus *soft* (a few standard deviations of the hash distribution, so the argmax is nudged, not dictated) and *decaying* (a task's affinity to node X fades as X's cached copy ages). The interview answer: *HRW generalizes from 'pick the max' to 'pick the max with preferences' — affinity, load, and locality are just additive weights, and the random term keeps the placement robust*."

**Interviewer**: "Final: monitoring a rendezvous scheduler. What metrics tell you it's working?"

**Candidate**: "Four metric families. (1) **Balance at rest**: max-load / mean-load ratio per node (target < 1.2 for large K); deviation from K/N per node — the uniformity property, watched continuously. (2) **Movement under churn**: keys-moved / keys-per-node per add/remove event — should be ~1/N of keys *and spread* (max gain per survivor ≈ M/(N-1)); a spike means either the hash degraded or membership flapped. (3) **Lookup cost**: `assign` latency distribution — O(N) scans can spike as N grows; watch the p99 and plan a shortlist cache when it crosses budget. (4) **Scheduling quality (if tasks are scheduled, not just data)**: per-node utilization *and* the queue length of the least-loaded node — the scheduler's end-to-end effectiveness. Plus the alerting pair: *imbalance > threshold* and *movement-per-event > expected×3* — those two catch both hash rot and membership flapping. The interview point: *rendezvous quality is measurable in three numbers — balance, movement, lookup cost — and all three are dashboardable*."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Lead with 'HRW is only as uniform as its hash' — the hash-quality answer is the most testable differentiator.
- Prepare the replica-selection extension (exclusion + failure domains) as a named technique.
- Rehearse the affinity variant (additive bonuses, soft bias, decay) — it's the least-known and most impressive answer.

### One-sentence takeaway
- "Rendezvous hashing is a uniform-random argmax: correct by construction, resilient by i.i.d. weights, and generalizable to weighted, affined, and replica-aware placement — as long as the hash is well-mixed and the metrics are watching balance and movement."

### Self-check questions (run before the real interview)
1. Can I prove the locality property (anti-swap) in under a minute?
2. Can I describe selection-with-exclusion for k replicas and its failure-domain extension?
3. Can I name the hot-key toolkit (sub-sharding, caching, pre-provisioning, shedding)?
4. Can I design the affinity-weighted variant and its two verification properties?
5. Can I list the three quality metrics (balance, movement, lookup cost) and their alert thresholds?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** Define rendezvous hashing in one sentence.
**Hint.** Key → argmax over nodes of hash(key, node) — deterministic, no coordination, no state.

**Q2.** Why does removing a node spread its keys uniformly?
**Hint.** Weights are i.i.d. per (key, node) — the survivor argmax is uniform over N−1 nodes.

**Q3.** State the locality (anti-swap) property.
**Hint.** A key's owner changes only when its own owner leaves — other membership changes don't move it.

**Q4.** Ring vs HRW — the churn difference.
**Hint.** Ring: dead node's keys pile on its successor; HRW: uniform spread over all survivors.

**Q5.** How do you weight nodes in HRW?
**Hint.** Virtual nodes — weight copies per node; argmax over copies; proportional share.

**Q6.** What is HRW's cost and its mitigation?
**Hint.** O(N) hashes per lookup; shortlist cache per key, recomputed on membership change.

**Q7.** When is HRW the wrong tool?
**Hint.** Ordered/range data (ring better), huge N with hot lookups, locality-sensitive placement.

**Q8.** How do you pick k distinct replicas with HRW?
**Hint.** Selection-with-exclusion — re-run argmax over remaining nodes; extend to failure domains.

**Q9.** What does a weak hash do to HRW?
**Hint.** Skews the argmax distribution — imbalance at rest; validate with empirical histograms.

**Q10.** Hot keys — what actually fixes them?
**Hint.** Sub-sharding, caching, pre-provisioning, load shedding — hashing balances assignment, not demand.

### Scoring
- **8-10 correct**: ready for the scheduling loop.
- **5-7**: revise the locality proof and the ring comparison.
- **<5**: re-read the walkthrough before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab (`RendezvousScheduler`) and pass the locality/uniform-remap tests.
**Day 3**: Quick-Fire rounds; prove the anti-swap property on paper.
**Day 4**: Rehearse the weighted/replica-selection extensions and hot-key toolkit.
**Day 5**: Drill the extended rounds (hash quality, affinity, monitoring).
**Day 6**: Mock interview, 45 minutes, no notes.
**Day 7**: Score against the Debrief table; study the follow-up prompts.
