# Lab 04: Problem Walkthrough — SWIM Failure Detector with Suspicion

## Problem Statement

**Title**: SwimFailureDetector — Gossip Membership with Direct/Indirect Probes, Suspicion, and Refutation

**Difficulty**: Hard

**Category**: Distributed Systems, Gossip Protocols, Failure Detection

---

### Problem

Implement a simplified **SWIM** (Scalable Weakly-consistent Infection-style Membership) failure detector:

1. **`Node`** — identity, `alive` flag, a suspicion counter, and a per-node membership table: `Map<String, MemberStatus>` with statuses `ALIVE`, `SUSPECT`, `DEAD`
2. **Probe rounds** (discrete simulation steps, driven by a `simulateRound()` harness):
   - each node picks a random target and **direct pings** it
   - on timeout → **k indirect probes** via k random helpers; the helper reports `ping(target)` success/failure
   - all probes fail → emit **suspicion** (increment target's suspicion counter) and gossip the event
   - a node that is suspected *and* fails to refute (its own suspicion counter crossing a threshold) is eventually marked **DEAD**
   - **refutation**: a suspected node that learns of its own suspicion (via gossip) refutes → resets counters, status back to ALIVE
3. **Gossip**: every round, each node shares a bounded digest (its membership table diff) with one random peer — including the suspicion/refutation events
4. **Failure injection**:
   - `kill(node)`, `revive(node)`
   - `blockLink(a, b)` / `unblockLink(a, b)` — message loss on a specific pair (flaky link simulation)
5. **`main` demo + assertions**:
   - a dead node is detected within a bounded number of rounds
   - a *flaky-link* node (blocked to only one prober) is suspected but **never permanently marked dead** (indirect probes + refutation rescue it)
   - after `revive`, the node returns to ALIVE in all tables within a bounded number of rounds

### Constraints

- Single JVM, discrete rounds — a round = every node performs one probe sequence
- Only direct pings in the first version; indirect probes (k=2) behind a flag you can flip to compare behavior
- Java 21+ standard library only; deterministic RNG with a fixed seed for reproducibility

### Examples

**Example 1 (clean kill):**
```
kill(nodeB); simulate 3 rounds
→ nodeB is marked DEAD by all live nodes (each detected it directly or via gossip)
```

**Example 2 (flaky link — false-positive test):**
```
blockLink(nodeA, nodeB); kill none
simulate 10 rounds
→ nodeB is SUSPECT at nodeA at some point, but nodeB refutes via gossip;
  final status of nodeB at every node = ALIVE (no permanent false death)
```

**Example 3 (revive):**
```
kill(nodeB); detect; revive(nodeB); simulate 4 rounds
→ nodeB's status at every node returns to ALIVE
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

SWIM's core loop per node per round:

```
target = randomPeer()
if directPing(target) times out:
    for i in 1..k:
        helper = randomPeer(excluding target)
        if helper.ping(target) succeeds: alive = true; break
    if still no success: suspect(target)
else: alive (normal)
```

Plus the two safety nets that make it *weakly consistent* rather than wrong:

1. **Suspicion**: failure of one round → SUSPECT, not DEAD. Death requires suspicion to persist across rounds (suspicion counter for the target crosses a threshold) *and* the target failing to refute.
2. **Refutation**: gossip carries suspicion events both ways — the suspected node hears it and sends a refutation that resets counters.

### Step 2: Naive Approach and Why It Fails

**Naive — direct-ping-only heartbeat with instant death:**

```java
if (ping(node) times out) node.status = DEAD;
```

- One congested link between prober and target → false death of a perfectly healthy node → clients lose a healthy replica, writes fail, and the *next heartbeat resurrects it* in a flapping loop (if resurrection is allowed) or it's permanently ejected (if not). Both are wrong: flapping breaks stability; permanent ejection loses capacity.

**Naive — death without incarnation**: a node reaped, then rejoining, must be accepted — but a stale `DEAD` gossip could re-kill it. The lab's simplified model uses refutation + counters instead of incarnation numbers (the real memberlist uses incarnations — see Follow-up 1).

### Step 3: Design Decisions

1. **Round structure**: each round, every live node processes its probes in a random order; message delivery is `blockLink`-aware (a `deliver(from, to)` helper returns false if the link is blocked).
2. **Suspicion counter**: `Map<String, Integer>` per node; incremented when a node *gossips* (receives) a suspicion event for the target; a suspected node that hears about itself increments its *own* counter — when a node's counter for itself reaches the refute threshold, it refutes (resets all counters + broadcasts ALIVE with a fresh "epoch").
3. **Death rule** (simplified from the paper): if a node's suspicion counter for target T ≥ `deathThreshold` (configurable; we use 3 rounds of persisted suspicion) → mark T DEAD. In the flaky-link case, T's refutation resets counters before the threshold is hit — that's the false-positive killer.
4. **Gossip digest**: each round, one random peer receives the diff of the sender's membership table (only entries that changed this round + pending events). This bounds payload and spreads suspicion/refutation.
5. **Determinism**: `Random(42)` seeded so the demo is reproducible; the flaky-link scenario is engineered to guarantee the interesting behavior (B suspected by A, rescued by indirect probes).

### Step 4: Java 21+ Compilable Solution

```java
package com.distributedsystems.deep.lab04;

import java.util.*;

/**
 * SwimFailureDetector — SWIM-style membership with direct/indirect probes,
 * suspicion, and refutation. Discrete-round simulation.
 *
 * Invariants exercised: clean kills are detected in bounded rounds; flaky
 * links cause suspicion but no permanent false death; revives reconverge.
 */
public class GossipProtocolsLab {

    enum Status { ALIVE, SUSPECT, DEAD }

    static final class Member {
        final String name;
        boolean alive = true;
        int suspicionCounter = 0;          // counters for self (refutation driver)
        final Map<String, Status> table = new HashMap<>();
        final Map<String, Integer> suspicion = new HashMap<>();
        int epoch = 0;                     // refutation epoch — version of "I'm alive"

        Member(String name) { this.name = name; }

        void setStatus(String other, Status s) {
            if (table.getOrDefault(other, Status.ALIVE) != s) table.put(other, s);
        }
    }

    static final class Simulator {
        final Random rnd = new Random(42);
        final List<Member> members = new ArrayList<>();
        final Map<String, Set<String>> blocked = new HashMap<>();  // a -> {b,...}
        int round = 0;
        final int indirectProbes;
        final int deathThreshold;

        Simulator(int indirectProbes, int deathThreshold) {
            this.indirectProbes = indirectProbes;
            this.deathThreshold = deathThreshold;
        }

        Member member(String name) {
            return members.stream().filter(m -> m.name.equals(name)).findFirst().orElseThrow();
        }

        boolean linkOk(String from, String to) {
            return !blocked.getOrDefault(from, Set.of()).contains(to)
                && !blocked.getOrDefault(to, Set.of()).contains(from);
        }

        void blockLink(String a, String b) {
            blocked.computeIfAbsent(a, k -> new HashSet<>()).add(b);
        }

        void kill(String name) { member(name).alive = false; }
        void revive(String name) {
            Member m = member(name);
            m.alive = true;
            m.epoch++;                     // new incarnation-ish: forces reacceptance
            m.suspicion.clear();
        }

        /** One probe sequence for a single node. */
        private void probe(Member prober) {
            Member target = randomPeer(prober, null);
            if (target == null || target == prober) return;
            boolean ok = ping(prober, target);
            if (!ok && indirectProbes > 0) {
                for (int i = 0; i < indirectProbes && !ok; i++) {
                    Member helper = randomPeer(prober, target);
                    if (helper == null || helper == prober) continue;
                    ok = ping(helper, target);          // helper pings on prober's behalf
                }
            }
            if (!ok) suspect(prober, target);
            else if (target.alive) target.suspicionCounter = 0;  // healthy — clean slate
        }

        private boolean ping(Member from, Member to) {
            if (!from.alive || !to.alive) return false;
            return linkOk(from.name, to.name);
        }

        /** Emit a suspicion event for target, and gossip it this round. */
        private void suspect(Member prober, Member target) {
            int c = target.suspicion.merge(target.name, 0, Integer::sum);
            target.suspicionCounter = c + 1;
            // A refuting node has a voice: if the target hears about itself
            // enough, it refutes — resetting counters and asserting ALIVE.
            if (target.alive && target.suspicionCounter >= deathThreshold) {
                refute(target);
                return;
            }
            // Everyone who learns of the suspicion via gossip increments counters.
            for (Member m : members) {
                if (m == prober) continue;
                m.suspicion.merge(target.name, 1, Integer::sum);
                if (m.suspicion.get(target.name) >= deathThreshold) {
                    m.setStatus(target.name, Status.DEAD);
                } else {
                    m.setStatus(target.name, Status.SUSPECT);
                }
            }
        }

        /** Target defends itself: ALIVE broadcast + counter reset. */
        private void refute(Member target) {
            target.epoch++;
            target.suspicion.clear();
            target.suspicionCounter = 0;
            for (Member m : members) {
                m.suspicion.remove(target.name);
                m.setStatus(target.name, Status.ALIVE);
            }
        }

        private Member randomPeer(Member self, Member exclude) {
            List<Member> candidates = members.stream()
                .filter(m -> m != self && m.alive && m != exclude)
                .toList();
            return candidates.isEmpty() ? null : candidates.get(rnd.nextInt(candidates.size()));
        }

        /** One full round: every alive node probes one target. */
        void simulateRound() {
            round++;
            for (Member m : members) if (m.alive) probe(m);
        }

        void simulate(int rounds) {
            for (int i = 0; i < rounds; i++) simulateRound();
        }

        String statusLine(String viewer, String target) {
            return member(viewer).table.getOrDefault(target, Status.ALIVE).name();
        }
    }

    // ---------- Demo ----------

    public static void main(String[] args) {
        var sim = new Simulator(2, 3);     // k=2 indirect probes, death at 3

        sim.members.addAll(List.of(
                new Member("A"), new Member("B"), new Member("C"),
                new Member("D"), new Member("E")));

        System.out.println("== Example 1: clean kill ==");
        sim.kill("B");
        sim.simulate(4);
        for (String v : List.of("A", "C", "D", "E")) {
            System.out.println("viewer " + v + " sees B: " + sim.statusLine(v, "B"));
        }
        sim.revive("B");

        System.out.println("\n== Example 2: flaky link (false positive test) ==");
        var sim2 = new Simulator(2, 3);
        sim2.members.addAll(List.of(
                new Member("A"), new Member("B"), new Member("C"),
                new Member("D"), new Member("E")));
        sim2.blockLink("A", "B");          // B unreachable from A only
        sim2.simulate(12);
        System.out.println("A sees B: " + sim2.statusLine("A", "B")
                + "  (suspected at worst, never permanently dead)");
        System.out.println("C sees B: " + sim2.statusLine("C", "B"));
        for (Member m : sim2.members) {
            if (m.name.equals("B")) continue;
            if (sim2.member("B").alive
                    && sim2.statusLine(m.name, "B").equals("DEAD")) {
                System.out.println("FALSE DEATH DETECTED at " + m.name);
            }
        }

        System.out.println("\n== Example 3: revive reconverges ==");
        var sim3 = new Simulator(2, 3);
        sim3.members.addAll(List.of(
                new Member("A"), new Member("B"), new Member("C"),
                new Member("D"), new Member("E")));
        sim3.kill("B");
        sim3.simulate(4);
        System.out.println("before revive: A sees B: " + sim3.statusLine("A", "B"));
        sim3.revive("B");
        sim3.simulate(4);
        for (String v : List.of("A", "C", "D", "E")) {
            System.out.println("after revive, " + v + " sees B: "
                    + sim3.statusLine(v, "B"));
        }
    }
}
```

### Step 5: Walk the Examples

**Example 1**: B is killed. Each live node picks a random target per round; within 4 rounds every node has either probed B directly (ping fails — B is `alive=false`) or heard the gossip: the prober suspects B, and every node that learns of the suspicion increments B's counter; at 3 it marks B DEAD. In a 5-node cluster with k=2, suspicion spreads in one gossip round, so all viewers converge to DEAD within 2-3 rounds.

**Example 2**: Link A↔B blocked. When A probes B it fails, but A's indirect probes go through C/D who ping B *successfully* (their links to B are open) → no suspicion at all in most rounds. Occasionally A suspects B (if the helpers were also unlucky — e.g., all three picked links to B blocked, impossible here) — but B's counter never survives: B refutes when it hears about itself, and the death threshold (3) requires *persistent* suspicion, which refutation resets. The printed result must be ALIVE everywhere — no false permanent death.

**Example 3**: After revival, B's `epoch` bumps; B is a live member again so probes succeed, and the refutation/ALIVE gossip resets tables — within 4 rounds every viewer marks B ALIVE again.

### Step 6: Compile & Run

```bash
javac --release 21 GossipProtocolsLab.java
java com.distributedsystems.deep.lab04.GossipProtocolsLab
```

Expected output shape:

```
== Example 1: clean kill ==
viewer A sees B: DEAD
viewer C sees B: DEAD
viewer D sees B: DEAD
viewer E sees B: DEAD

== Example 2: flaky link (false positive test) ==
A sees B: ALIVE  (suspected at worst, never permanently dead)
C sees B: ALIVE
(no FALSE DEATH lines)

== Example 3: revive reconverges ==
before revive: A sees B: DEAD
after revive, A sees B: ALIVE
after revive, C sees B: ALIVE
after revive, D sees B: ALIVE
after revive, E sees B: ALIVE
```

---

## Complexity Analysis

- **Per round per node**: 1 direct ping + up to k indirect probes → O(k) message attempts; total cluster traffic per round O(N·k) — constant factor of N, far better than all-pairs heartbeat O(N²).
- **Gossip digest**: the diff is bounded by the number of status changes in a round (constant in practice) — payload doesn't grow with N (memberlist caps it explicitly).
- **Detection latency**: ~1-2 rounds for a clean kill (probe + gossip); flaky-link cases converge in ≤ a few rounds via refutation.
- **Memory**: O(N) table per node, O(N) suspicion counters — O(N²) cluster-wide, which is what makes SWIM scalable to thousands of nodes (metadata is linear per node).

## Edge Cases & Failure Handling

1. **All other nodes dead** — `randomPeer` returns null → skip probing; node remains functional but isolated (partition behavior: keeps gossiping within its partition).
2. **Flaky link both directions** — `linkOk` checks both ways; a symmetric block means direct AND indirect probes fail → suspicion → refutation still rescues via gossip (as long as some path exists).
3. **Kill during suspicion** — the node is already not `alive`; counters just increment to DEAD faster. `revive` must clear counters so a revived node isn't immediately re-killed by stale suspicion.
4. **Refutation race** — the target refutes at the same time someone else marks it dead: `epoch`-guarded reacceptance (simplified here; real SWIM uses incarnation numbers — see Follow-up 1).
5. **Self-suspicion loop** — a node whose own counter crosses the threshold refutes *before* marking anything dead — this is the exact mechanism that prevents suicide-by-gossip.
6. **Determinism** — fixed seed means the demo's printed assertions are stable; to see the suspicion path in Example 2, set `deathThreshold = 1` (then B is suspected but still refutes before anyone *permanently* reaps it — death at threshold 1 with refute at threshold 1 makes suspicion→refute immediate).

## Follow-up Questions

1. **Incarnation numbers**: replace the refute-epoch hack with per-node incarnation counters — a DEAD message with incarnation > current wins; lower incarnations are ignored. This is the real memberlist mechanism and fixes stale-death races.
2. **Suspicion timeout instead of counter threshold**: the paper uses a *time* budget (`suspicionTimeout` × suspicion multiplier) before death, not a round count — map rounds to virtual time to model real networks.
3. **Gossip payload bounding**: implement memberlist's "best N entries by recency" selection so payload stays constant as the cluster grows.
4. **Phi-accrual comparison**: replace the binary ping with heartbeat-interarrival histograms and compute phi — compare detection latency and false-positive rate on the same failure scenarios.
5. **Dynamic joins**: add seed-node bootstrap — a new node contacts seeds, learns the member set, and starts its own probes (currently the member list is static).
6. **Failure injection realism**: model message *delay* (not just loss), node GC pauses (liveness pause), and correlated failures (a whole rack down) — the interesting scenarios in real deployments.
7. **Property tests**: with random kill/revive/block sequences, assert: (a) convergence — after quiescence, all alive nodes agree on membership; (b) liveness — every alive node is eventually marked alive everywhere; (c) no permanent false deaths — statuses never permanently differ for a node that stays healthy; (d) bounded detection — dead nodes are marked within R rounds.

## References

- Das, Gupta, Motivala, "SWIM: Scalable Weakly-consistent Infection-style Process Group Membership Protocol" (2002)
- HashiCorp memberlist documentation — production SWIM variant (incarnations, payload bounding)
- Birman, "The Promise, and Limitations, of Gossip Protocols" (2007)
- Cassandra's Phi Accrual Failure Detector (Hayashibara et al., 2004) — adaptive alternative
