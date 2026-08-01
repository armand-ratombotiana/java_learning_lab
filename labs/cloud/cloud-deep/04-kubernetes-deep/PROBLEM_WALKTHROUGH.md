# Lab 04: Problem Walkthrough — Kubernetes Scheduler with Priority and Preemption

## Problem Statement

Implement the scheduling core of a Kubernetes-like cluster scheduler. Given a set of nodes with allocatable resources and a set of pending pods with resource requests and priority, the scheduler must:

1. Run the **filtering phase**: for each pending pod, find nodes whose remaining capacity can satisfy the pod's requests (CPU and memory).
2. Run the **scoring phase**: rank candidate nodes by least-allocated score (spread) or most-allocated (bin-pack), weighted per resource.
3. **Bind** the highest-priority pending pod to the best-scoring feasible node.
4. When a pod cannot fit on any node, attempt **preemption**: find a node where evicting the lowest-priority victim set makes room, respecting the rule that a pod may never preempt another pod of equal or higher priority.
5. Maintain a **priority queue** of pending pods — scheduling always pops the highest-priority pod first (FIFO within equal priority).
6. Track unschedulable pods so the scheduler can demonstrate fair behavior in the demo.

**Constraints**

- A node's memory/cpu totals are `allocatable`; used amounts are the sum of requests of bound pods.
- Preemption must be idempotent and must not double-evict.
- All code must compile under Java 21+.

---

## Walkthrough

### Step 1: Model nodes, pods, and resources

A `Node` holds allocatable CPU and memory (in millicores and MiB) and the set of bound pods. A `Pod` carries requests, priority, and a name. Pods are immutable once created.

```java
package com.cloud.deep.lab04;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class KubernetesScheduler {

    public record Resources(int cpuMillicores, int memoryMiB) {
        public Resources add(Resources o) {
            return new Resources(cpuMillicores + o.cpuMillicores, memoryMiB + o.memoryMiB);
        }
        public Resources sub(Resources o) {
            return new Resources(cpuMillicores - o.cpuMillicores, memoryMiB - o.memoryMiB);
        }
    }

    public record Pod(String name, int priority, Resources requests) {}

    public static final class Node {
        private final String name;
        private final Resources allocatable;
        private Resources used = new Resources(0, 0);
        private final List<Pod> bound = new ArrayList<>();

        Node(String name, Resources allocatable) {
            this.name = name;
            this.allocatable = allocatable;
        }

        synchronized boolean fits(Pod pod) {
            return used.cpuMillicores() + pod.requests().cpuMillicores() <= allocatable.cpuMillicores()
                    && used.memoryMiB() + pod.requests().memoryMiB() <= allocatable.memoryMiB();
        }

        synchronized void bind(Pod pod) {
            if (!fits(pod)) throw new IllegalStateException("Node " + name + " cannot fit " + pod.name());
            used = used.add(pod.requests());
            bound.add(pod);
        }

        synchronized void evict(Pod pod) {
            if (bound.remove(pod)) {
                used = used.sub(pod.requests());
            }
        }

        synchronized Resources remaining() { return allocatable.sub(used); }
        synchronized int boundCount() { return bound.size(); }
        synchronized List<Pod> boundPods() { return List.copyOf(bound); }
        synchronized double utilization() {
            return (used.cpuMillicores() + used.memoryMiB())
                    / (double) (allocatable.cpuMillicores() + allocatable.memoryMiB());
        }

        String name() { return name; }
        Resources allocatable() { return allocatable; }
    }
```

### Step 2: Implement the priority queue

The scheduler's active queue pops the highest-priority pod first; equal priorities break ties by insertion order (FIFO). `PriorityQueue` with a comparator that orders by priority descending and a monotonically increasing sequence number ascending gives us exactly that.

```java
    static final class PodQueue {
        private record Entry(Pod pod, long seq) {}
        private final PriorityQueue<Entry> q = new PriorityQueue<>(
                Comparator.comparingInt((Entry e) -> e.pod().priority())
                        .reversed()
                        .thenComparingLong(Entry::seq));
        private final AtomicLong seq = new AtomicLong();
        private final AtomicInteger size = new AtomicInteger();

        void add(Pod pod) { q.add(new Entry(pod, seq.incrementAndGet())); size.incrementAndGet(); }
        Pod poll() {
            Entry e = q.poll();
            if (e == null) return null;
            size.decrementAndGet();
            return e.pod();
        }
        int size() { return size.get(); }
    }
```

### Step 3: Implement filtering

Filtering evaluates each node for hard feasibility. A node fails if the sum of requests of bound pods plus the incoming pod's requests exceeds allocatable for either resource.

```java
    public record FilterResult(List<Node> feasible) {}

    static List<Node> filter(List<Node> nodes, Pod pod) {
        return nodes.stream().filter(n -> n.fits(pod)).toList();
    }
```

### Step 4: Implement scoring

Scoring computes a 0-100 score per node. The least-allocated strategy scores `100 - utilizationPercent`; the most-allocated strategy scores `utilizationPercent`. Per-resource scores are weighted equally (CPU 50%, memory 50%) and rounded.

```java
    enum ScoringStrategy { LEAST_ALLOCATED, MOST_ALLOCATED }

    static int score(Node node, ScoringStrategy strategy) {
        double cpuPct = node.allocatable().cpuMillicores() == 0 ? 0
                : (node.allocatable().cpuMillicores() - node.remaining().cpuMillicores())
                * 100.0 / node.allocatable().cpuMillicores();
        double memPct = node.allocatable().memoryMiB() == 0 ? 0
                : (node.allocatable().memoryMiB() - node.remaining().memoryMiB())
                * 100.0 / node.allocatable().memoryMiB();
        double utilization = 0.5 * cpuPct + 0.5 * memPct;
        double score = strategy == ScoringStrategy.LEAST_ALLOCATED ? 100 - utilization : utilization;
        return (int) Math.round(score);
    }
```

### Step 5: Implement preemption

Preemption answers: "pod P does not fit anywhere; find a node where evicting a victim set makes room." The algorithm:

1. For each node, consider its bound pods **with strictly lower priority than P**.
2. Greedily evict the *lowest-priority victims first* (to minimize total evicted priority) until the pod fits — or try the combination that frees enough resources with minimum total priority.
3. Choose the node whose victim set has the lowest total priority; if tied, prefer the node with fewer evictions.

The equal-or-higher priority protection is enforced by only considering `victim.priority < pod.priority`.

```java
    public record PreemptionResult(Optional<Node> node, List<Pod> victims) {
        static PreemptionResult none() { return new PreemptionResult(Optional.empty(), List.of()); }
    }

    static PreemptionResult findPreemption(List<Node> nodes, Pod pod) {
        PreemptionResult best = PreemptionResult.none();
        long bestTotalPriority = Long.MAX_VALUE;

        for (Node node : nodes) {
            List<Pod> evictable = node.boundPods().stream()
                    .filter(v -> v.priority() < pod.priority())
                    .sorted(Comparator.comparingInt(Pod::priority))
                    .toList();

            List<Pod> victims = new ArrayList<>();
            Resources freed = new Resources(0, 0);
            Resources need = new Resources(
                    Math.max(0, pod.requests().cpuMillicores() - node.remaining().cpuMillicores()),
                    Math.max(0, pod.requests().memoryMiB() - node.remaining().memoryMiB()));

            for (Pod v : evictable) {
                victims.add(v);
                freed = freed.add(v.requests());
                if (freed.cpuMillicores() >= need.cpuMillicores()
                        && freed.memoryMiB() >= need.memoryMiB()) {
                    break;
                }
            }

            if (freed.cpuMillicores() >= need.cpuMillicores()
                    && freed.memoryMiB() >= need.memoryMiB()) {
                long totalPriority = victims.stream().mapToLong(Pod::priority).sum();
                boolean better = totalPriority < bestTotalPriority
                        || (totalPriority == bestTotalPriority && victims.size() < best.victims().size());
                if (better) {
                    best = new PreemptionResult(Optional.of(node), List.copyOf(victims));
                    bestTotalPriority = totalPriority;
                }
            }
        }
        return best;
    }
```

### Step 6: Implement the scheduler's scheduling cycle

The cycle: pop the highest-priority pod → filter → score → bind. If no node fits, attempt preemption; if preemption finds victims, evict them (record it) and bind. Otherwise the pod is recorded as unschedulable and stays queued for a later attempt.

```java
    public static final class Scheduler {
        private final List<Node> nodes = new ArrayList<>();
        private final PodQueue queue = new PodQueue();
        private final ScoringStrategy strategy;
        private final Map<String, Integer> preemptions = new ConcurrentHashMap<>();
        private final Map<String, Integer> evictions = new ConcurrentHashMap<>();

        public Scheduler(List<Node> nodes, ScoringStrategy strategy) {
            this.nodes.addAll(nodes);
            this.strategy = strategy;
        }

        public void addNode(Node n) { nodes.add(n); }
        public void submit(Pod pod) { queue.add(pod); }
        public int pendingCount() { return queue.size(); }

        public ScheduleResult scheduleNext() {
            Pod pod = queue.poll();
            if (pod == null) return ScheduleResult.drained();

            List<Node> feasible = filter(nodes, pod);
            if (!feasible.isEmpty()) {
                Node best = feasible.stream()
                        .max(Comparator.comparingInt(n -> score(n, strategy)))
                        .orElseThrow();
                best.bind(pod);
                return ScheduleResult.bound(pod, best.name(), score(best, strategy));
            }

            PreemptionResult preemption = findPreemption(nodes, pod);
            if (preemption.node().isPresent()) {
                for (Pod victim : preemption.victims()) {
                    preemption.node().get().evict(victim);
                    evictions.merge(victim.name(), 1, Integer::sum);
                }
                preemption.node().get().bind(pod);
                preemptions.merge(pod.name(), 1, Integer::sum);
                return ScheduleResult.preempted(pod, preemption.node().get().name(),
                        preemption.victims().stream().map(Pod::name).toList());
            }

            queue.add(pod); // unschedulable for now — back to the queue
            return ScheduleResult.unschedulable(pod.name());
        }

        public Map<String, Integer> preemptions() { return Map.copyOf(preemptions); }
        public Map<String, Integer> evictions() { return Map.copyOf(evictions); }
    }

    public record ScheduleResult(String pod, String node, String outcome, int score,
                                 List<String> victims) {
        static ScheduleResult bound(Pod pod, String node, int score) {
            return new ScheduleResult(pod.name(), node, "BOUND", score, List.of());
        }
        static ScheduleResult preempted(Pod pod, String node, List<String> victims) {
            return new ScheduleResult(pod.name(), node, "PREEMPTED", 0, victims);
        }
        static ScheduleResult unschedulable(String pod) {
            return new ScheduleResult(pod, "-", "UNSCHEDULABLE", 0, List.of());
        }
        static ScheduleResult drained() {
            return new ScheduleResult("(none)", "-", "DRAINED", 0, List.of());
        }
    }
```

### Step 7: Demo — two phases, priority ordering and preemption

The demo runs in two phases, mirroring real clusters: regular workload binds first, then critical pods arrive that cannot fit and must preempt. Phase 1: two batch pods (priority 10) and a best-effort pod (priority 0) fill the two nodes. Phase 2: two critical pods (priority 1000) arrive; neither fits anywhere, so each preempts the lowest-priority victims. Preempted victims are *terminated*, not re-queued — exactly like kube-scheduler, where the workload controller (Deployment, etc.) recreates them and they land where capacity exists.

```java
    public static void main(String[] args) {
        Node n1 = new Node("node-a", new Resources(2000, 4096));
        Node n2 = new Node("node-b", new Resources(2000, 4096));

        Scheduler scheduler = new Scheduler(List.of(n1, n2), ScoringStrategy.LEAST_ALLOCATED);

        System.out.println("=== Kubernetes Scheduler Demo ===");

        System.out.println("-- Phase 1: regular workload arrives first and binds --");
        scheduler.submit(new Pod("best-effort", 0, new Resources(300, 512)));
        scheduler.submit(new Pod("batch-1", 10, new Resources(1000, 2048)));
        scheduler.submit(new Pod("batch-2", 10, new Resources(1000, 2048)));
        drain(scheduler);

        System.out.println("\n-- Phase 2: critical pods arrive --");
        scheduler.submit(new Pod("critical-1", 1000, new Resources(1800, 3072)));
        scheduler.submit(new Pod("critical-2", 1000, new Resources(1500, 2560)));
        drain(scheduler);

        System.out.println("\n-- End state --");
        System.out.println("  pending pods: " + scheduler.pendingCount()
                + " (queue drained; preempted victims are terminated, not re-queued)");

        System.out.println("\n-- Preemption/eviction ledger --");
        scheduler.preemptions().forEach((p, c) -> System.out.println("  preempted pod: " + p + " x" + c));
        scheduler.evictions().forEach((p, c) -> System.out.println("  evicted victim: " + p + " x" + c));

        System.out.println("\n-- Node utilization --");
        for (Node n : List.of(n1, n2)) {
            System.out.printf("  %-8s used=%.1f%% bound=%d%n",
                    n.name(), n.utilization() * 100, n.boundCount());
        }
    }

    /** Drains the queue; stops after 5 rounds without progress (unschedulable pods). */
    private static void drain(Scheduler scheduler) {
        int noProgress = 0;
        while (scheduler.pendingCount() > 0 && noProgress < 5) {
            ScheduleResult r = scheduler.scheduleNext();
            System.out.printf("  %-12s -> %-8s %-13s score=%d%s%n",
                    r.pod(), r.node(), r.outcome(), r.score(),
                    r.victims().isEmpty() ? "" : " victims=" + r.victims());
            noProgress = (r.outcome().equals("BOUND") || r.outcome().equals("PREEMPTED"))
                    ? 0 : noProgress + 1;
        }
    }
}
```

### Step 8: Verify the expected scheduling order

| Round | Popped pod | Decision | Why |
|-------|------------|----------|-----|
| 1 | batch-1 (prio 10) | BOUND node-a | Empty cluster; least-allocated tie → first node |
| 2 | batch-2 (prio 10) | BOUND node-b | node-a is 50% utilized, node-b empty |
| 3 | best-effort (prio 0) | BOUND node-a | Both nodes 50%; tie → first node |
| 4 | critical-1 (prio 1000) | PREEMPTED node-b, victims [batch-2] | Fits nowhere; node-b's set (1 victim, total prio 10) beats node-a's (best-effort + batch-1, 2 victims, total prio 10) — fewer evictions wins |
| 5 | critical-2 (prio 1000) | PREEMPTED node-a, victims [best-effort, batch-1] | node-b's only bound pod is critical-1 (equal priority — protected); node-a has the lower-priority victims, evicted lowest-first |
| End | queue empty | all pods bound; 3 victims terminated | preempted victims are not re-queued |

The two guarantees demonstrated: (1) **preemption only ever evicts strictly lower-priority pods** — critical-2 never evicted critical-1 (both prio 1000), which is precisely why it had to preempt node-a instead; (2) **victims are chosen lowest-priority-first** with minimum total priority — best-effort (prio 0) is always the first victim. Round 4 shows the tie-breaker: two candidate victim sets with equal total priority — the node with fewer evictions wins.

---

## Complexity Analysis

- **Filter**: O(N) per pod, where N = number of nodes.
- **Score**: O(N) per pod (score is O(1) per node).
- **Bind/evict**: O(1) amortized per pod.
- **Preemption**: O(N · K log K) worst case, where K = bound pods per node (sort victims by priority). For the typical cluster, K is small (tens of pods per node) and N is bounded by the scheduler's node cache — the real scheduler parallelizes filtering/scoring across workers.
- **Queue**: O(log Q) per push/pop, Q = pending pods.
- **Space**: O(N · K + Q) — node state plus pending queue.

---

## Follow-Up Questions

1. **How would you parallelize the scheduling cycle?** Filtering and scoring are embarrassingly parallel — shard nodes across worker threads, then merge the top-N scored candidates and pick the best (topology- and cache-aware merging to avoid thundering herding on the API server).

2. **What happens if the node cache is stale?** The scheduler must reconcile bind decisions against the kubelet's reported state (informers). A `PodSchedulingContext`/resync mechanism re-evaluates pending pods when a node's used-resources update arrives; a wrong bind is eventually corrected via `kubelet` re-sync and pod re-queuing.

3. **How do you prevent preemption storms?** Rate-limit preemption per node (e.g., at most one preemption per node per 30s), require the victim's PodDisruptionBudget to allow eviction, and back off re-queued unschedulable pods exponentially.

4. **How do you extend the model to multiple resources (GPUs, ephemeral storage)?** Generalize `Resources` to a `Map<String, Long>` with per-resource fit checks and per-resource scoring weights; the filter checks every key in the union of the pod's requests and the node's allocatable set.

5. **How does the real kube-scheduler differ from this model?** Plugins (filter/score/bind/preempt extension points), the scheduling framework's `QueueSort`/`PreFilter`/`PostFilter` cycle, per-pod `spec.priorityClassName` admission validation, and the unschedulable queue's event-driven re-queueing with a periodic sweep.

6. **How do you test preemption correctness?** Property tests over random clusters asserting: (a) a pod never preempts equal-or-higher priority, (b) after preemption the pod fits the node, (c) eviction is idempotent, (d) the victim set has minimal total priority.
