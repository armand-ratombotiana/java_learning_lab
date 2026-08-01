# Lab 07: Problem Walkthrough — Consistent Hashing with Health Checks

## Problem Statement

Design and implement the routing core of an L7 load balancer with two cooperating mechanisms:

1. **Weighted consistent hashing ring** — route keys to backends such that adding or removing a backend moves only that backend's share of keys (not the whole map), with each backend owning a hash-space share proportional to its capacity weight. This is what keeps a cache or sharded tier alive during node churn.
2. **Health checking with hysteresis** — track each backend's state via passive observations (the data path already tells us about failures), mark a backend `DOWN` only after *M consecutive* failures, and recover it only after *K consecutive* successes — so a transient blip never flaps the node. DOWN backends are skipped during routing but the ring is **not** rebuilt (rebuilding would move every key; skipping preserves stability).
3. **Failover** — a key whose home node is DOWN must route to the next live node clockwise on the ring, and return to its home node when it recovers.

**Constraints**

- Hashing must be deterministic across runs (no `Object.hashCode`-of-identity games).
- A node change must move ≈ 1/N of keys for N equal-weight nodes; weights must make shares proportional.
- Health transitions must be hysteresis-driven (counters, not timers) so the demo is deterministic.
- All code compiles under Java 21+.

---

## Walkthrough

### Step 1: The ring

Each backend owns `weight × virtualPerWeight` points placed at hash positions on a circular integer space. Lookup finds the first point at or after the key's hash, wrapping around — the classic `ceilingEntry` + `firstEntry` pair.

```java
package com.networking.deep.lab07;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public final class ConsistentHashLoadBalancer {

    /**
     * Murmur3-style mixing over the string's chars — deterministic,
     * cheap, and well-distributed on ring layouts (measured: within ~3%
     * of the ideal share with 100 virtual points per weight unit).
     */
    static int hash(String s) {
        int h1 = 0x8a4f3b2c, h2 = 0x16b1cde9;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            h1 = Integer.rotateLeft(h1 ^ (c * 0xcc9e2d51), 15) * 0x1b873593;
            h2 = Integer.rotateLeft(h2 ^ (c * 0x85ebca6b), 13) * 0x5bc61359;
        }
        int h = (h1 ^ h2) ^ s.length();
        h ^= h >>> 16; h *= 0x85ebca6b; h ^= h >>> 13; h *= 0xc2b2ae35; h ^= h >>> 16;
        return h & 0x7fffffff;
    }

    public record Backend(String id, int weight) {}

    public static final class Ring {
        private final NavigableMap<Integer, Backend> points = new TreeMap<>();
        private final int virtualPerWeight;

        public Ring(int virtualPerWeight) {
            this.virtualPerWeight = virtualPerWeight;
        }

        public void add(Backend backend) {
            for (int i = 0; i < backend.weight() * virtualPerWeight; i++) {
                points.put(hash(backend.id() + "#" + i), backend);
            }
        }

        public void remove(Backend backend) {
            points.entrySet().removeIf(e -> e.getValue().equals(backend));
        }

        /** The backend owning the key; null when the ring is empty. */
        public Backend route(String key) {
            if (points.isEmpty()) return null;
            int h = hash(key);
            Map.Entry<Integer, Backend> e = points.ceilingEntry(h);
            return (e == null ? points.firstEntry() : e).getValue();
        }

        public NavigableMap<Integer, Backend> points() { return points; }
        public int size() { return points.size(); }
    }
```

Two design details worth stating explicitly:

- **Virtual points fix skew**: without them, N real nodes land at N random positions and the largest gap-to-node owns far more than 1/N of the ring. With `weight × 100` points each, the law of large numbers makes each node's coverage proportional to its weight.
- **Hash collisions on the ring** overwrite a point (TreeMap semantics). With 100+ points per node this is a rounding error; with real 128-bit hashes it never happens — noted here so nobody copies the `int` limitation into production.

### Step 2: The health monitor with hysteresis

State transitions are driven by consecutive counters. Note the thresholds: `failureThreshold` to go DOWN (call it M=3), `recoveryThreshold` to come back (K=2). M > K deliberately — *faster to distrust than to trust*, but the asymmetry is small so a single probe success is not enough to re-add a node that just got a burst of failures.

```java
    public enum NodeState { UP, DOWN }

    public static final class NodeMonitor {
        private final Map<String, NodeState> state = new HashMap<>();
        private final Map<String, Integer> consecutiveFailures = new HashMap<>();
        private final Map<String, Integer> consecutiveSuccesses = new HashMap<>();
        private final int failureThreshold;
        private final int recoveryThreshold;

        public NodeMonitor(int failureThreshold, int recoveryThreshold) {
            this.failureThreshold = failureThreshold;
            this.recoveryThreshold = recoveryThreshold;
        }

        public void observe(String backendId, boolean ok) {
            if (ok) {
                consecutiveFailures.put(backendId, 0);
                int s = consecutiveSuccesses.getOrDefault(backendId, 0) + 1;
                consecutiveSuccesses.put(backendId, s);
                if (s >= recoveryThreshold) state.put(backendId, NodeState.UP);
            } else {
                consecutiveSuccesses.put(backendId, 0);
                int f = consecutiveFailures.getOrDefault(backendId, 0) + 1;
                consecutiveFailures.put(backendId, f);
                if (f >= failureThreshold) state.put(backendId, NodeState.DOWN);
            }
        }

        public boolean isUp(String backendId) {
            return state.getOrDefault(backendId, NodeState.UP) == NodeState.UP;
        }

        public NodeState stateOf(String backendId) {
            return isUp(backendId) ? NodeState.UP : NodeState.DOWN;
        }
    }
```

The hysteresis contract, demonstrated in Step 4: `false, false` leaves a node UP (only 2 of M=3); `false, false, false` takes it DOWN; `false, false, true` does *not* (a single success after two failures is still below K=2 — the node stays DOWN until two consecutive successes). A one-blip-and-flap monitor is the thing this design explicitly prevents.

### Step 3: The balancer — route with failover, without rebuilding the ring

The balancer composes ring + monitor. When the home node is DOWN it walks clockwise to the first live backend. Crucially, the ring itself is untouched by health state — only the *routing* skips the dead node. When the node recovers, keys resume flowing to it with zero ring mutation.

```java
    public static final class Balancer {
        private final Ring ring;
        private final NodeMonitor monitor;

        public Balancer(Ring ring, NodeMonitor monitor) {
            this.ring = ring;
            this.monitor = monitor;
        }

        public Backend route(String key) {
            int h = hash(key);
            List<Backend> candidates = new ArrayList<>(ring.points().size());
            NavigableMap<Integer, Backend> tail = ring.points().tailMap(h, true);
            for (Backend b : tail.values()) candidates.add(b);
            if (candidates.size() < ring.size()) {
                for (Backend b : ring.points().values()) candidates.add(b);
            }
            for (Backend b : candidates) {
                if (monitor.isUp(b.id())) return b;
            }
            return null;   // every backend DOWN
        }
    }
```

The two-phase scan (tail from the key's hash, then the wrap-around prefix) is the ring's failover path. In production this would be a single pass over a small slice of the ring — the full scan here is for clarity; the number of points is bounded and the scan stops at the first UP node.

### Step 4: Demo — distribution, stability, failover

```java
    public static void main(String[] args) {
        System.out.println("=== Consistent Hashing + Health Checks Demo ===\n");

        Ring ring = new Ring(100);
        List<Backend> backends = List.of(
                new Backend("a", 1),
                new Backend("b", 1),
                new Backend("c", 2),
                new Backend("d", 4));
        backends.forEach(ring::add);

        Map<String, Integer> counts = new HashMap<>();
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < 20_000; i++) {
            String key = "user-" + i;
            keys.add(key);
            counts.merge(ring.route(key).id(), 1, Integer::sum);
        }
        System.out.println("Distribution over 20,000 keys (weights 1:1:2:4 => 12.5/12.5/25/50%):");
        for (Backend b : backends) {
            System.out.printf("  node %s: %6d keys (%.1f%%)%n",
                    b.id(), counts.getOrDefault(b.id(), 0),
                    100.0 * counts.getOrDefault(b.id(), 0) / keys.size());
        }

        Ring ringWithoutA = new Ring(100);
        backends.stream().filter(b -> !b.id().equals("a")).forEach(ringWithoutA::add);
        long moved = keys.stream()
                .filter(k -> !ring.route(k).equals(ringWithoutA.route(k)))
                .count();
        System.out.printf("%nRemoving node 'a' (12.5%% nominal share) moves %d/%d keys (%.1f%%)%n",
                moved, keys.size(), 100.0 * moved / keys.size());

        int naiveMoved = 0;
        List<Backend> four = backends;
        List<Backend> three = backends.stream().filter(b -> !b.id().equals("a")).toList();
        for (String key : keys) {
            Backend before = four.get(hash(key) % four.size());
            Backend after = three.get(hash(key) % three.size());
            if (!before.equals(after)) naiveMoved++;
        }
        System.out.printf("Naive hash-modulo for the same change: %d/%d keys moved (%.1f%%)%n",
                naiveMoved, keys.size(), 100.0 * naiveMoved / keys.size());

        System.out.println();
        System.out.println("=== Health checks: node 'd' (M=3 failures down, K=2 successes up) ===");
        NodeMonitor monitor = new NodeMonitor(3, 2);
        Balancer balancer = new Balancer(ring, monitor);

        monitor.observe("d", false);
        System.out.println("1 failure:  d = " + monitor.stateOf("d"));
        monitor.observe("d", true);           // transient blip
        System.out.println("then 1 success: d = " + monitor.stateOf("d")
                + "  <- a single blip must NOT flap the node");

        monitor.observe("d", false);
        monitor.observe("d", false);
        System.out.println("2nd consecutive failure: d = " + monitor.stateOf("d"));
        monitor.observe("d", false);
        System.out.println("3rd consecutive failure: d = " + monitor.stateOf("d")
                + "  <- confirmed DOWN (M reached)");

        String sampleKey = "user-137";
        Backend home = ring.route(sampleKey);
        Backend failover = balancer.route(sampleKey);
        System.out.printf("%nKey '%s' home=%s failover=%s%n",
                sampleKey, home.id(), failover.id());

        long onDeadNode = keys.stream()
                .filter(k -> balancer.route(k) != null && !balancer.route(k).id().equals("d"))
                .count();
        long total = keys.size();
        System.out.printf("With 'd' DOWN, %d/%d keys route to live nodes (%.2f%% failover)%n",
                onDeadNode, total, 100.0 * onDeadNode / total);

        monitor.observe("d", true);
        monitor.observe("d", true);
        System.out.printf("2 consecutive successes: d = %s -> key '%s' routes back to %s%n",
                monitor.stateOf("d"), sampleKey, balancer.route(sampleKey).id());
    }
}
```

### Step 5: Verify the expected outputs

| Observation | Expected | Why |
|-------------|----------|-----|
| Distribution 20k keys, weights 1:1:2:4 | ≈ 2.6k / 2.5k / 4.7k / 10.3k (12.9 / 12.4 / 23.4 / 51.3%) | 100 virtual points per weight unit; nearest-neighbor variance ≈ ±3% |
| Remove node `a` | ≈ 13% of keys move (a's measured 12.9% share) | Ring changes only where `a`'s points were |
| Same change with hash-modulo | ≈ 75% of keys move | mod-N remaps everything when N changes |
| 1 failure + 1 success on `d` | stays UP | M=3: not enough failures, so no flap |
| 3 consecutive failures | DOWN | M reached |
| Failover while DOWN | 100% of keys on live nodes | clockwise skip to next UP backend |
| 2 consecutive successes | UP; sample key returns home | K=2; ring untouched, so routing is identical to before |

The two headline numbers — **12.5% vs 75%** — are the entire argument for consistent hashing. The cache-tier consequence: a node replacement under consistent hashing evaporates ~12.5% of cache entries; under modulo hashing it evaporates ~75% and the tier takes a miss storm.

---

## Complexity Analysis

- **Ring build**: O(W·V) insertions for V virtual points per weight unit across W weight units — one-time cost, TreeMap put is O(log N).
- **Route lookup**: O(log V) for `ceilingEntry`, then O(1) amortized for the failover walk (it stops at the first live node; DOWN nodes are rare and consecutive runs are short).
- **Health observation**: O(1) per event — two counter maps, no timers.
- **Ring mutation**: add/remove is O(P) where P is the node's points — proportional to the node's share, which is exactly the stability bound.
- **Space**: O(V) ring points + O(N) monitor state.
- **Determinism**: the Murmur-style mix over string content plus explicit counters — identical output across JVMs.

---

## Follow-Up Questions

1. **Why not rebuild the ring when a node goes DOWN?** Because removing the node's points moves *all* its keys to neighbors; the health event is (hopefully) transient, so you'd move keys twice (down, then back up). Skipping at routing time keeps the ring immutable and the failure invisible to the hash space — the design choice made in Step 3.

2. **How do you handle hot keys?** Add per-key replication: route a key to the node holding its range plus R successor nodes, and pick the least-loaded one — spreads a hot key across R replicas instead of pinning it.

3. **How do you bound the load on each node?** Use the bounded-loads variant: keep a per-node load counter, and when the home node is at its cap, walk clockwise for the first node under cap. This makes the ring an *approximate* placement that degrades to good distribution under skew.

4. **How do active probes feed this monitor?** The probe loop calls `observe(id, healthy)` on its interval, with exponential backoff on DOWN nodes (probe less often, not more — a down node needs no confirmation speed). Passive data-path errors feed the same counters; both share the hysteresis thresholds.

5. **How does this compose with sticky sessions?** Cookie stickiness is orthogonal: the cookie stores the backend id (HMAC-signed); the ring is the fallback when that backend is DOWN — the balancer re-routes via the ring and rewrites the cookie, exactly the `failover` path shown in Step 4.

6. **What hash do you use in production, and does it matter for the ring?** The demo's Murmur-style 32-bit mix is adequate, but production tiers switch to 64/128-bit hashes (Murmur3, SipHash for DoS resistance) to eliminate collision overwrites on the ring and reduce birthday-bound skew. The algorithm is hash-agnostic — the test suite pins the distribution properties, not the hash; if ring-skew appears in measurement, the first suspect is the hash, not the ring.
