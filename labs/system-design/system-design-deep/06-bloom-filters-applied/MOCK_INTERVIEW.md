# Mock Interview Transcript: Counting Bloom Filter with Deletion

| Field | Detail |
|-------|--------|
| **Level** | Senior Backend Engineer (L5) |
| **Duration** | 45 minutes |
| **Format** | Whiteboard + implementation |
| **Problem** | "Implement a counting Bloom filter: a Bloom filter that supports deletion without false negatives. We use it to deduplicate ad impressions at high throughput." |

---

## Part 0: Scene Setting (2 minutes)

**Interviewer (I):** Our ad platform sees each impression as an event; the same
impression id may be retried or replayed, and we must not double-count it for
billing. The dedup window is ~10 minutes, volume is hundreds of thousands of
impressions per second, and the filter must eventually forget old ids. A classic
Bloom filter is a natural fit except for one thing: ids leave the window, so we
need **deletion** without reintroducing false negatives. Implement a counting
Bloom filter.

**Candidate (C):** Let me start with the constraint that decides this design:
what's the false-positive budget, and what does a false positive *cost*?

---

## Part 1: Clarifying Questions (5 minutes)

**C:** Three questions. One: false positives are the filter's only error — a
false positive would drop a *legitimate duplicate-check*, i.e., we'd skip billing
for a real impression? Two: what's the scale — entries in flight, key size? Three:
does the count-per-position need to be bounded, or is unbounded growth acceptable?

**I:** A false positive means we think we've seen the impression when we haven't —
we drop it. Rare and low-cost per event, but it must be *rare*. Hundreds of
thousands of entries in flight, keys are ~40-byte strings. Bounded counts are
preferred — think about saturation.

**C:** Good. That's the standard profile: a counting Bloom filter with a small
saturating counter per position. Let me lay out the mechanics, then the two bugs
people actually hit — counter underflow and counter saturation — because they're
the entire engineering story here.

---

## Part 2: Mechanics (7 minutes)

**C:** A standard Bloom filter is a bit array plus `k` hash functions; adding a
key sets `k` bits, membership checks all `k` bits. Deletion is impossible because
clearing a bit might destroy evidence for other keys sharing that position. The
counting variant replaces each bit with a small counter: add increments `k`
counters, delete decrements them, and membership is "all `k` counters are
non-zero."

**C:** Why this works: `remove` only ever decrements counters that this key
incremented, so it can't *create* a false negative for a key whose counters were
already non-zero for other reasons — the count at a position is how many inserted
keys point at it, and as long as it stays above zero, the position still "says
yes." The error profile is unchanged on the *insert* side — the same k-hash
overlap math — and deletion can only ever *raise* the false-positive rate
slightly (counters that dropped to zero), never reintroduce false negatives.
That asymmetry is the whole point of counting vs plain Bloom.

---

## Part 3: The Two Bugs That Matter (8 minutes)

**I:** Walk me through the failure modes.

**C:** **Underflow**: deleting a key that was never added, or deleting twice, can
drive a counter negative — which then *permanently* sets that position to "no,"
creating false negatives for every key that legitimately maps there. This is the
classic counting-Bloom bug and the reason `remove` must refuse to decrement a
zero counter. My implementation returns `false` from `remove` if any position is
already zero — a no-op, and the caller learns the key wasn't present.

**C:** **Saturation**: with 4-bit counters (the classic choice — memory-efficient,
holds 0-15), a hot position shared by >15 keys overflows. Two policies: saturate
at 15 (keep 15, and accept that deletes may underflow the *true* count — the
filter degrades toward a plain Bloom with sticky ones) or let counters grow
unbounded (no overflow, but memory per position balloons and the cache-line
efficiency dies). For billing-grade dedup I choose saturation at a small cap and
handle the stickiness policy explicitly: a saturated counter is never decremented
to zero by *one* delete path — actually, the honest way to say it is: saturated
counters can only decrement down to the cap's *sticky floor* when the true count
exceeded the cap, so they act as permanent "yes" bits. That's a deliberate
trade — memory bounded, and the cost is a slightly higher false-positive floor.

**I:** How do you size the structure?

**C:** Same math as a plain Bloom, then multiply by counter width. For `n`
expected entries and false-positive rate `p`: `m = -n·ln(p) / ln(2)²` positions,
`k = (m/n)·ln(2)` hash functions. With 4-bit counters, memory is `m/2` bytes
plus negligible overhead. The demo will use 8-bit `int` counters for clarity —
same semantics, readable values.

---

## Part 4: Implementation Walkthrough (10 minutes)

**C:** (writing) The structure: `int[] counters`, `k` hash functions via double
hashing (two FNV passes combined as `h1 + i·h2` — gives `k` independent-looking
positions from two base hashes, no expensive hashing per round). `add` increments
with a `min(maxCount, c+1)` cap. `mightContain` returns false if any of the `k`
positions is zero. `remove` checks all positions are positive first — then
decrements — else returns false. That check-then-act order is what prevents
underflow.

**I:** One subtlety — `remove` checking *all* positions before decrementing any.
Why not decrement as you go?

**C:** Because then a partial decrement could underflow the last position. Check
all, then act, keeps `remove` atomic with respect to underflow. Under a single
thread it's trivially correct; with concurrent adds, the saturation cap bounds
the damage anyway.

**I:** Your demo?

**C:** Four scenarios. (1) Insert, check, delete, re-check — the full lifecycle
with no false negative. (2) The underflow guard: removing a key never inserted
returns false and leaves counters untouched. (3) **The delete-safety property
that defines counting Bloom filters**: insert key X twice, delete it once —
`mightContain(X)` stays true. A plain Bloom can't do that; it's the "no false
negatives on deletion" contract in its purest form. (4) A false-positive check —
a random absent key is almost certainly "not present," and I'll print the
observed false-positive rate over a few thousand trials.

---

## Part 5: Operations and Scale (6 minutes)

**I:** It's an in-memory structure. How does this dedup scale across the fleet?

**C:** Two patterns. **Sharded**: each instance keeps its own filter and we accept
that a replayed event hitting a different instance slips through — for billing,
that's a gap, so better: **a Redis-backed counting filter**, same counters, keyed
by position — `INCRBY/DECRBY/GET` with a Lua script for the check-and-increment.
The in-memory version here is the *algorithm*; the Redis version is the
deployment. For the 10-minute window, both need **age-based reclamation**:
periodic rebuild — swap in a fresh filter every 10 minutes and drop the old one —
which is simpler and more robust than per-entry TTLs, which would need a
timestamp per counter and turn the filter into a heap.

**I:** Watch the false-positive rate drift across window rotations.

**C:** Right — the new filter starts cold, so for the first seconds it has *no*
false positives and then converges; the *effective* protection dips at rotation.
Standard answer: rotate with overlap — keep the previous filter as a secondary
check for one extra window — or accept the dip as immaterial, since false
positives are rare and the cost is dropping a borderline event. I'd measure the
real drift rather than guess.

---

## Part 6: Closing and Feedback (3 minutes)

**I:** Summarize.

**C:** A counting Bloom filter is a Bloom filter with counters instead of bits —
add increments, delete decrements, membership is all-nonzero — and it preserves
the core asymmetry: deletion never introduces false negatives. The two bugs that
actually bite are underflow (guard by refusing to decrement zeros) and saturation
(bound it deliberately and know what it costs). At fleet scale, host it in Redis
and reclaim windows by rotation, not per-entry TTLs.

**I:** Very good. You named the two real bugs unprompted, gave the sizing math,
and correctly chose rotation over TTL for reclamation. One addition: say the
`ln(2)` factors out loud when sizing — `k = (m/n)·ln 2` — and consider whether
you want `remove` to be *conditional on membership first* in production, so a
replayed `remove` for an expired window doesn't silently corrupt the new window's
counters.

---

## Evaluation Scorecard

| Dimension | Observation | Score (1-5) |
|-----------|-------------|-------------|
| Mechanics | Counter-based membership; why deletion is safe (asymmetry) | 5 |
| Failure modes | Underflow guard and saturation policy discussed first, before code | 5 |
| Sizing | Standard Bloom math + counter-width memory accounting | 4 |
| Implementation | Double hashing, check-all-then-decrement ordering | 5 |
| Delete-safety demo | Insert-twice-delete-once case — the defining test — included | 5 |
| Operations | Redis hosting, window rotation, cold-start dip | 4 |
| Depth | Could have quantified saturation's false-positive floor | 4 |

**Overall: Strong Hire** — algorithm, failure modes, and deployment all covered
with the right emphasis.

## Common Pitfalls Candidates Hit

- Decrementing counters without checking, corrupting the filter into false
  negatives (the canonical counting-Bloom bug).
- Saying "deletion works" without discussing saturation of fixed-width counters.
- Reclaiming by per-entry TTLs — turning the filter into a priority queue.
- Forgetting the insert-twice-delete-once test — the property that distinguishes
  counting Bloom filters.
- Sizing by guesswork instead of `m = -n·ln p / ln² 2`.
- Assuming one instance can dedup fleet-wide traffic without a shared store.
