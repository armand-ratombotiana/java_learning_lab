# Problem Walkthrough: Lease-Based Leader Election with Fencing Tokens

## Problem Statement

Three identical workers run a cluster. Exactly one must act as leader and own a
job that writes to a data layer (here: "pay invoices"). If the leader dies or is
partitioned from the store, a surviving worker must take over within seconds.
The hard requirement: **a partitioned leader that comes back must never be able
to write again** — even though it still believes it is the leader. Duplicated
work is catastrophic.

Implement lease-based leader election (compare-and-set acquire, token-preserving
renewal, expiry as demotion) and **fencing tokens** — a monotonic generation
counter that the data layer enforces, so a stale leader's writes are rejected by
the resource itself.

## Requirements

- **Single leader:** at most one worker holds the lease at any time.
- **Bounded failover:** if the leader stops renewing, a follower takes over after
  a bounded delay (lease expiry + poll interval).
- **Fencing:** every write carries the leader's token; the resource rejects any
  token older than the latest token it has seen *or* older than the registry's
  current token.
- **Token-preserving renewal:** a healthy leader renewing its lease keeps the same
  token — renewal never churns leadership.
- **Stale-leader self-demotion:** a worker that fails to renew must stop working
  when its lease would have expired (the partition is a death sentence for its
  identity).
- **Observable demo:** the acquire/expire/takeover/stale-write-rejected timeline
  must be demonstrated, plus a renewal path that preserves the token.

## Constraints & Assumptions

- Arbitration store is a consensus-backed KV with atomic compare-and-set (etcd /
  ZooKeeper semantics), modeled in-process here.
- Clocks: the *store's* clock decides lease expiry (`expiresAt` lives in the
  store record); worker clocks only decide when to renew.
- Renewal cadence ~1 s, lease duration ~10 s (failover ≈ 10-12 s).
- The job's writes are fenced individually; the fence token rides inside each
  write request (never in a forgeable header).

## Why Fencing Is the Real Guarantee

Leader election alone cannot protect a money-moving job. Consider:

```
t0  A acquires lease. token = 1. A pays invoice #77.
t1  A partitions (network cut). Cannot renew.
t2  Lease expires. B acquires. token = 2. B pays invoice #77.   <- money paid
t3  A's partition heals. A still holds token 1 and believes it is leader.
t4  A pays invoice #77 again.                                    <- double payment
```

Election cannot prevent t3: A *was* leader, and from A's view nothing is wrong.
Only the data layer can stop t4. The **fencing token** is a monotonic generation
counter: the store increments it on every leadership change, and the resource
rejects any write whose token is not the *current* registry token and not newer
than the last token it applied. A stale leader's writes bounce — the resource
trusts token history, not the writer's self-belief.

This reframes the design: **election is a weak guarantee (someone wins the CAS);
fencing is the strong guarantee (only the current generation's token lands).**

## Solution Overview

```
             acquire(owner, duration)                 write(lease, data)
 Worker A  ------------------------>  LeaseRegistry  <---------------------- FencedResource
            CAS: record absent or     (the "store")    token must equal       |
            expired -> owner=A,                         current registry      |
            token=++generation                         token AND token >      |
            expiresAt=now+duration                     lastApplied            |
                                                       else REJECTED          v
             renew(lease):                                             [data safe]
            CAS: record is (A, token) -> push expiresAt forward
```

### Invariants

1. **Acquire** succeeds only via CAS on `(absent or expired)` — two workers cannot
   both win.
2. **Renewal** succeeds only via CAS on `(owner, token)` — after a takeover, the
   old leader's renewal fails and it self-demotes.
3. **Fence** — a write is applied only if `token == registry.current.token`
   and `token > lastApplied`. This closes the "new leader hasn't written yet"
   hole: last-seen alone would admit the stale leader's token while the new
   leader's first write is still pending.
4. **Expiry is the demotion decision** — the worker that cannot renew stops
   working by its own deadline, so the takeover window is bounded by the lease.

## Step-by-Step Solution

### Step 1: Define the lease record

`Lease(owner, token, expiresAt)`. `expiresAt` is set by the store at acquire/
renewal time so the store's clock is the single arbiter.

### Step 2: Implement the registry (the store)

- `tryAcquire(owner, duration)`: under the store's lock — if the current lease is
  absent or expired, install `(owner, ++generation, now + duration)` and return
  it. Otherwise return empty.
- `renew(lease, duration)`: under the lock — succeed only if the current record
  is the *same* lease (owner and token match) and not expired; then push
  `expiresAt` forward. The token is preserved.
- `current()`: snapshot accessor used by the resource's fence check.

### Step 3: Implement the fenced resource

Every write path holds `lastAppliedToken` and consults the registry:

```
write(lease, data):
    current = registry.current().orElse(null)
    if current == null || current.token() != lease.token(): return REJECTED
    loop:                                     // CAS on lastApplied
        applied = lastAppliedToken.get()
        if lease.token() <= applied: return REJECTED
        if lastAppliedToken.compareAndSet(applied, lease.token()): break
    apply(data); return ACCEPTED
```

The registry check kills writes from a superseded generation (even one that
outlasted its lease by clock confusion); the CAS serializes concurrent writes
within one generation.

### Step 4: The worker lifecycle

1. Acquire; on success, become leader.
2. Renew on a cadence ≪ lease duration. Renewal failure → self-demote and stop
   work immediately (the lease is expiring or was taken).
3. On explicit shutdown, release the lease (delete) so failover is fast instead
   of lease-bound.

### Step 5: Demonstrate both timelines

- **Stale leader rejected:** A acquires (token 1), writes; lease expires while A
  is "partitioned" (no renewal); B acquires (token 2), writes; A resumes and its
  stale write is rejected by the fence.
- **Healthy renewal:** A acquires (token 1), renews repeatedly — token stays 1;
  the renewal CAS does not churn leadership.

## Java 21+ Implementation

```java
package com.systemdesign.deep.lab05;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Lab 05: Lease-Based Leader Election with Fencing Tokens.
 * Demonstrates: CAS acquire, token-preserving renewal, expiry as demotion,
 * and resource-side fencing that rejects stale leaders' writes.
 */
public class LeaderElectionLab {

    /** A lease: who owns leadership, with which generation token, until when. */
    public record Lease(String owner, long token, Instant expiresAt) {
        public boolean expired() { return Instant.now().isAfter(expiresAt); }
    }

    /** The arbitration store: acquire, renew, current. Token = generation counter. */
    public static final class LeaseRegistry {
        private final AtomicLong generation = new AtomicLong(0);
        private volatile Lease current;

        /** CAS acquire: succeeds only if no valid lease exists. */
        public synchronized Optional<Lease> tryAcquire(String owner, Duration duration) {
            if (current != null && !current.expired()) return Optional.empty();
            Lease lease = new Lease(owner, generation.incrementAndGet(),
                    Instant.now().plus(duration));
            current = lease;
            return Optional.of(lease);
        }

        /** Token-preserving renewal: only the current owner with the same token may renew. */
        public synchronized boolean renew(Lease lease, Duration duration) {
            if (current == null || !current.equals(lease) || current.expired()) return false;
            current = new Lease(lease.owner(), lease.token(), Instant.now().plus(duration));
            return true;
        }

        /** Explicit handoff: remove the lease so followers acquire immediately. */
        public synchronized void release(Lease lease) {
            if (current != null && current.equals(lease)) current = null;
        }

        public synchronized Optional<Lease> current() { return Optional.ofNullable(current); }
        public synchronized long generation() { return generation.get(); }
    }

    /** The data layer: applies a write only if the token is the current generation. */
    public static final class FencedResource {
        private final LeaseRegistry registry;
        private final AtomicLong lastAppliedToken = new AtomicLong(0);
        private final AtomicLong acceptedWrites = new AtomicLong();
        private final AtomicLong rejectedWrites = new AtomicLong();

        public FencedResource(LeaseRegistry registry) { this.registry = registry; }

        /**
         * Fenced write: token must equal the registry's current token (kills stale
         * generations) and strictly exceed the last applied token (serializes within
         * a generation).
         */
        public boolean write(Lease lease, String data) {
            Lease current = registry.current().orElse(null);
            if (current == null || current.token() != lease.token()) {
                rejectedWrites.incrementAndGet();
                return false;
            }
            while (true) {
                long applied = lastAppliedToken.get();
                if (lease.token() <= applied) {
                    rejectedWrites.incrementAndGet();
                    return false;
                }
                if (lastAppliedToken.compareAndSet(applied, lease.token())) break;
            }
            acceptedWrites.incrementAndGet();
            return true;
        }

        public long acceptedWrites() { return acceptedWrites.get(); }
        public long rejectedWrites() { return rejectedWrites.get(); }
    }

    public static void main(String[] args) throws InterruptedException {
        LeaseRegistry registry = new LeaseRegistry();
        FencedResource resource = new FencedResource(registry);

        // --- Timeline 1: stale leader is fenced out ---
        System.out.println("== Timeline 1: partition, takeover, fenced stale write ==");

        Lease leaseA = registry.tryAcquire("worker-A", Duration.ofMillis(300)).orElseThrow();
        System.out.printf("A acquired: token=%d%n", leaseA.token());
        System.out.println("A writes #77: " + resource.write(leaseA, "pay invoice 77"));

        Thread.sleep(400);                       // A's lease expires; A is "partitioned"
        System.out.println("A lease expired: " + leaseA.expired());

        Lease leaseB = registry.tryAcquire("worker-B", Duration.ofMillis(1000)).orElseThrow();
        System.out.printf("B acquired: token=%d%n", leaseB.token());
        System.out.println("B writes #77: " + resource.write(leaseB, "pay invoice 77"));

        System.out.println("A resumes, stale write: " + resource.write(leaseA, "pay invoice 77 AGAIN")
                + "  <- fenced (token " + leaseA.token() + " < current " + registry.generation() + ")");

        registry.release(leaseB);              // graceful handoff so the next timeline is clean

        // --- Timeline 2: healthy leader renews with the same token ---
        System.out.println("== Timeline 2: healthy renewal preserves the token ==");
        Lease leaseC = registry.tryAcquire("worker-C", Duration.ofMillis(1000)).orElseThrow();
        System.out.printf("C acquired: token=%d%n", leaseC.token());
        for (int i = 1; i <= 3; i++) {
            Thread.sleep(100);
            boolean renewed = registry.renew(leaseC, Duration.ofMillis(1000));
            System.out.printf("C renewal %d: %s (token still %d)%n", i, renewed, leaseC.token());
        }
        System.out.println("C writes: " + resource.write(leaseC, "pay invoice 78"));

        // --- Timeline 3: renewal after takeover fails (CAS on owner+token) ---
        System.out.println("== Timeline 3: stale renewal fails after takeover ==");
        Thread.sleep(1100);                      // leaseC expires
        Lease leaseD = registry.tryAcquire("worker-D", Duration.ofMillis(1000)).orElseThrow();
        System.out.printf("D acquired: token=%d%n", leaseD.token());
        System.out.println("C attempts renewal: " + registry.renew(leaseC, Duration.ofMillis(1000))
                + "  <- fails: record is now D's");

        System.out.println("== Totals ==");
        System.out.printf("accepted writes=%d, rejected writes=%d, generation=%d%n",
                resource.acceptedWrites(), resource.rejectedWrites(), registry.generation());
    }
}
```

## Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| `tryAcquire` | O(1) | O(1) | Single CAS on the store record |
| `renew` | O(1) | O(1) | CAS on (owner, token); token preserved |
| `current()` | O(1) | O(1) | Volatile read |
| Fenced `write` | O(1) amortized | O(1) | Registry read + CAS loop on lastApplied; the loop only re-iterates under concurrent writers |
| Worker lifecycle | O(1) per tick | O(1) | Renewal thread is a timer loop |

**Latency bounds:** failover time = lease expiry + poll interval (≈ 10-12 s at
1 s renewal / 10 s lease). Graceful handoff (explicit release) cuts this to one
round trip. Write latency = one registry read + local CAS — no lock over the
data layer.

## Edge Cases & Failure Modes

| Scenario | Behavior | Why it's correct |
|----------|----------|------------------|
| Leader partitions; lease expires; new leader elected | Old leader's writes rejected by token check | The fence is the guarantee; election is just the enabler |
| New leader acquired but hasn't written yet | Stale leader's write still rejected | Resource checks the *registry's* current token, not just last-applied |
| Stale renewal after takeover | Renewal CAS fails; worker self-demotes | Renewal binds to (owner, token) |
| Two workers race acquire | Exactly one CAS wins | Atomic compare-and-set on the store |
| Store itself splits (non-linearized) | Fence still rejects stale tokens | Fencing doesn't trust the election layer's verdict |
| Leader stalls (thread deadlock) | Watchdog self-demotes before writes stop mattering | Local watchdog is the last line of defense |
| Clock skew on the worker | Only renewal timing shifts; expiry is store-side | `expiresAt` is computed and compared by the store |
| Graceful shutdown | Explicit release → immediate takeover | No wait for expiry |

## Verification Walkthrough

1. **Fenced stale write:** after B's takeover (token 2), A's write with token 1
   returns `false` — the double-payment timeline is stopped at the resource.
2. **Token preservation:** C renews three times and the token never changes —
   renewal is not a re-election.
3. **Stale renewal:** after D takes over, C's renewal fails — the CAS on
   (owner, token) rejects it and C must self-demote.
4. **Expiry semantics:** `leaseA.expired()` becomes true after the duration —
   the store-side `expiresAt` drives both demotion and takeover.
5. **Counters:** `rejectedWrites` rises only in the fenced path — the demo's
   assertions are observable, not hand-waved.

## Follow-Up Questions

1. **Watchdog integration:** a local thread asserts lease validity every
   `renewalInterval/2`; on failure it triggers self-demotion and shuts down the
   worker's write path *before* attempting re-election.
2. **Leader-based job distribution:** followers become candidates for *other*
   leases — the registry generalizes to N roles, each with its own generation.
3. **Token transport in production:** the token rides inside the RPC payload
   (or is re-read from the store inside the resource), never in a header the
   stale leader can forge.
4. **Leader liveness ≠ process liveness:** the lease proves the renewal loop is
   alive, not that the worker's work is healthy — hence the watchdog plus
   per-role health checks.
5. **Lease vs epoch in Kafka-style systems:** the same generation idea appears as
   Kafka's controller epoch and HDFS's NN epoch — the fence is the universal
   answer to stale writers after leader failover.
6. **Making the demo concurrent:** run N worker threads against the registry and
   assert that (a) at most one holds the lease at any instant, and (b) the sum of
   accepted writes across all workers never double-applies the same logical unit
   of work — the fence property, tested under contention.

## Summary

- **Lease answers "who is leader"; the fencing token answers "is this writer the
  latest generation"** — the second is the guarantee that protects duplicated
  work.
- **Expiry is the demotion decision and the failover latency; renewal is a
  token-preserving CAS**, so a healthy leader never churns and a stale one
  self-demotes.
- **The resource fence checks both last-applied and the registry's current
  token** — closing the "new leader hasn't written yet" hole that
  last-seen-only fencing misses.
- **Election only needs to be good enough** — even store-level split-brain
  degrades to a rejected write, never a double payment.
- **The watchdog is not optional**: the lease proves the renewal loop lives; the
  watchdog proves the worker isn't a zombie.
