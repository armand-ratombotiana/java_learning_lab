# Mock Interview Transcript: Lease-Based Leader Election with Fencing Tokens

| Field | Detail |
|-------|--------|
| **Level** | Senior Backend Engineer (L5) |
| **Duration** | 45 minutes |
| **Format** | Whiteboard + implementation |
| **Problem** | "Implement lease-based leader election with fencing tokens: exactly one worker may act as leader at a time, and a stale leader must never be able to write." |

---

## Part 0: Scene Setting (2 minutes)

**Interviewer (I):** We run a cluster of three identical workers, one of which must
be the leader — it owns a cron-like job that writes to a database. If the leader
dies or partitions, another worker must take over quickly, and the *old* leader —
when it comes back — must never, ever be able to write again. Design and implement
this: lease-based leader election with fencing tokens.

**Candidate (C):** Let me make sure I understand what "never be able to write
again" means operationally — that's the part that separates this problem from a
naive lock.

---

## Part 1: Clarifying Questions (5 minutes)

**C:** Three questions. One: what is the arbitration store — a single node like
etcd/ZooKeeper/Redis, or a database? Two: what's the failover budget — how fast
must the cluster elect a new leader? Three: is the *work* the leader performs
safe to duplicate, or does duplication corrupt data — e.g., double-payment?

**I:** Use any store you can reason about; we have a consensus-backed KV store with
atomic compare-and-set. Failover within a few seconds. Duplication is
catastrophic — the job pays money.

**C:** "Pays money" settles it: the failure mode we design for is **split-brain** —
two workers both believing they're leader. Fencing tokens exist specifically so
that even if split-brain happens at the election level, the *data layer* rejects
the stale leader. Let me lay out the mechanism.

---

## Part 2: The Core Mechanism — Lease + Fence (8 minutes)

**C:** Two pieces, and they answer two different questions. The **lease** answers
"who is leader, and is that still true?" The **fencing token** answers "is this
caller the *latest* leader?" The lease is a record in the store: `owner, token,
expiresAt`. Acquiring it is a compare-and-set: only succeeds if the current
record is absent or expired. Renewal pushes `expiresAt` forward — but here's the
crucial part — **renewal must preserve the token**.

**I:** Why does the token have to be monotonic?

**C:** Because a token is a generation counter. The store increments the token
whenever leadership *changes hands*. A stale leader — one that partitioned and
missed its renewals — holds an old token. The data layer keeps the last token it
saw, and *rejects any write carrying a token older than the latest it has seen*.
That's the fence: a write from the old leader is rejected by the resource itself,
even though the old leader still *thinks* it's leader.

**C:** Timeline for the money case:

```
t0  A acquires lease. token = 1. A pays invoice #77.
t1  A partitions (network cut). Cannot renew.
t2  Lease expires. B acquires. token = 2. B pays invoice #77.  <- money paid
t3  A's partition heals. A still has token 1 and believes it is leader.
    A tries to pay invoice #77 again.
t4  Data layer: last seen token = 2. A's token 1 < 2 -> REJECTED.  <- safe
```

**I:** What if A's *renewal* lands after B took over?

**C:** Renewal must also be fenced: renewal succeeds only if the record still
belongs to A with A's token — a compare-and-set on `(owner, token)`. If B took
over, A's renewal fails, and — critically — A must then **self-demote**: stop
work immediately. That's why the lease duration is the correctness bound: any
worker that can't renew within `leaseDuration` must be assumed dead *by itself*,
not just by others. A partition is a death sentence for the leader's identity.

---

## Part 3: Choosing the Lease Duration (5 minutes)

**I:** How do you size the lease?

**C:** Three constraints. (1) `leaseDuration > maxRenewalInterval + maxClockDrift +
storeLatency` — so a healthy leader never loses its lease to a transient hiccup.
(2) `leaseDuration` sets the *worst-case* split-brain window: after it expires,
the new leader and the old leader can both believe they're leader for at most
`leaseDuration + networkGap` — the fence makes that window harmless. (3)
`leaseDuration` is also the failover latency: followers poll for expiry, so
failover ≈ `leaseDuration + pollInterval`. Typical numbers: renew every 1 s,
lease 10 s — failover in ~10-12 s, and the fence protects any residual window.
I would also renew on a jittered schedule and treat one missed renewal as a
warning, not a demotion — the demotion decision is the lease expiry, nothing else.

**I:** What clocks does this depend on?

**C:** Wall clocks on the store and the workers. That's why the lease *record* owns
the expiry — `expiresAt` is compared by the store, and the store's clock is the
arbiter. Worker clocks only decide *when to try* renewing. Consensus-backed stores
make this safe; a worker with a wildly skewed clock just renews early or late, but
can't extend the store's view of expiry.

---

## Part 4: Implementation Walkthrough (10 minutes)

**C:** (writing) Three components. The `Lease` record: owner, token, expiresAt.
The `LeaseRegistry` — the store: a compare-and-set acquire, a token-preserving
renew, a current-lease accessor. The `FencedResource` — the data layer: it keeps
`lastAppliedToken`, and every write is a CAS: accept only if the token equals the
*current registry token* and strictly exceeds the last applied — a stale token
bounces.

**I:** Why does the resource check the registry at all? Why not just the
monotonic last-seen?

**C:** Because last-seen alone has a hole: if B becomes leader and has *never
written*, the resource's last-seen is still 1. A's stale token 1 passes the
"token ≥ last-seen" check. Consulting the registry's current token closes it —
the resource asks "who is leader *now*?" and rejects anything that isn't the
current token. In production this is an etcd read or a token passed inside the
write path's RPC; in the demo, the registry object stands in for the store.

**C:** (writing) The demo stages the money timeline: A acquires and writes;
A's lease expires while A is "partitioned" (we simply stop renewing); B acquires
(token 2) and writes; A wakes up and attempts its stale write — rejected with a
fencing exception. Then the same path with A *renewing correctly* shows the
renewal preserving the token — no churn.

**I:** How do you test the concurrency property?

**C:** The property is "at most one holder at a time" — but the *assertable*
property is "no write with a stale token ever lands." So the test harness is: N
workers race to acquire; the losers must observe rejection; the winner's writes
all land; any write after a takeover fails the fence check. The fencing check is
the thing unit tests can verify deterministically — CAS against both lastApplied
and the current registry token.

---

## Part 5: Graceful Handoff and Watchdogs (5 minutes)

**I:** What does the leader do when it's *healthy* and wants to step down?

**C:** It releases the lease explicitly — delete the record — then finishes
in-flight work and stops. Followers see the deletion (or a watch/notification)
and race to acquire immediately, skipping the expiry wait. Failover in the
graceful case is round-trip fast instead of lease-duration slow.

**I:** Watchdog?

**C:** Yes — and it's not optional: a separate local thread that verifies the
lease is still valid and the renewal loop is alive. If the main thread deadlocks
or stalls, the watchdog triggers self-demotion — work stops even though nobody
took the lease yet. In many outages the *failure detector* is the bug; the
watchdog is the last line of defense against "zombie leader," and it's cheap.

**I:** Split-brain *at the store level* — two acquire CASes both succeed?

**C:** Only if the store itself is broken (e.g., a non-consensus single-node store
that hasn't linearized writes — a classic Redis failover race). That's exactly why
the fencing token is the *final* guarantee: even a false-positive acquire from a
broken store can't land stale writes, because the resource's token check doesn't
trust the election layer — it trusts the token history it saw. Election
correctness is a *weak* guarantee here; fencing is the strong one.

---

## Part 6: Closing and Feedback (3 minutes)

**I:** Summarize.

**C:** Lease answers "who is leader"; fencing answers "is this writer the latest
leader" — and the second is the one that protects money. Expiry is the demotion
decision and the failover latency; renewal is token-preserving CAS; the resource
layer rejects any token older than the registry's current one, so even
store-level split-brain degrades to a rejected write, never a double payment.

**I:** Outstanding. You inverted the problem correctly — most candidates design
an election and stop; you designed the *rejection path* and then proved the
election only needs to be "good enough" because of it. The only thing I'd add:
sketch the timeline on the board earlier, and mention that the fencing token
must ride *inside* the write request (or be re-read at the resource), never
trusted from a header the old leader can forge.

---

## Evaluation Scorecard

| Dimension | Observation | Score (1-5) |
|-----------|-------------|-------------|
| Problem framing | Identified split-brain and "pays money" as the real requirements | 5 |
| Lease mechanics | CAS acquire, token-preserving renewal, expiry as demotion | 5 |
| Fencing | Monotonic token, resource-side rejection, last-seen hole closed via registry check | 5 |
| Timing analysis | Lease duration sizing: renew cadence, clock drift, failover latency | 5 |
| Operations | Watchdog, graceful handoff, self-demotion on partition | 5 |
| Depth | Store-level split-brain analysis; token transport security | 5 |
| Brevity/communication | Structured, used the money timeline as the anchor example | 5 |

**Overall: Strong Hire** — this is the canonical senior answer: election as a
weak guarantee, fencing as the strong one.

## Common Pitfalls Candidates Hit

- Designing election and stopping — no fence, so a partitioned old leader writes.
- Fencing by last-seen token only — misses the "new leader hasn't written yet" hole.
- Renewal that doesn't preserve the token, churning leadership every renewal.
- Sizing the lease by feel instead of by renew cadence + drift + store latency.
- No watchdog, then claiming the worker "would never stall."
- Trusting the old leader's self-reported identity instead of the store's record.
