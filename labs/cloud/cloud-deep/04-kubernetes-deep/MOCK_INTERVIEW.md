# Lab 04: Mock Interview — Senior Kubernetes/SRE Engineer

**Role**: Senior Platform Engineer | **Topic**: Kubernetes Scheduler with Priority and Preemption | **Duration**: 45 minutes

---

## Interview Transcript

**Interviewer**: "Design the scheduling core of Kubernetes: the kube-scheduler. What are its responsibilities, and how would you structure the scheduling cycle?"

**Candidate**: "The scheduler's job is to assign pending pods to nodes subject to two kinds of constraints: hard constraints that must be satisfied — resources, affinity, taints and tolerations, node selectors, topology spread — and soft preferences that should be satisfied — balanced resource utilization, locality of data. The structure is the classic two-phase cycle: **filtering** (also called feasibility) and **scoring** (also called optimization), followed by binding. Filtering is cheap and parallelizable — it produces the set of candidate nodes. Scoring ranks the candidates. Then we bind: persist the pod-to-node assignment and tell the kubelet on that node to start the pod. The whole cycle must be resumable and crash-safe, because at any moment the scheduler can be preempted or restarted, and the pod must not be lost or double-scheduled."

**Interviewer**: "What happens if a pod can't fit anywhere? That's where preemption comes in."

**Candidate**: "Two cases: the pod is unschedulable *everywhere*, or it's unschedulable on every node that would prefer it. In the strictest case, the scheduler tries **preemption**: it looks for nodes where the incoming pod could fit if one or more lower-priority pods were evicted. The scheduler picks the node that yields the highest-priority victim set — meaning it evicts the *lowest* total priority — and emits eviction notices. The critical correctness property: preemption must be **non-preemptive to equal-or-higher priorities** — you can never preempt a pod with priority greater than or equal to the incoming pod's. And preemption should only happen when admission control can't be satisfied any other way, because an eviction is disruptive — it triggers a reschedule, container restart, possibly data loss for stateful workloads."

**Interviewer**: "Walk me through the priority model. How are priorities assigned and what breaks if you get it wrong?"

**Candidate**: "Priority is a 32-bit integer, `PriorityClass` objects define the named buckets — typically something like system-cluster-critical at 2 billion, then production workloads at 1000, batch at 100, best-effort at 0. The scheduler compares priorities when deciding preemption. What breaks if you get it wrong: if too many pods share high priority, the preemption machinery becomes useless because you can't evict peers — you get a priority inversion in reverse, a 'tragedy of the commons' where everyone sets high priority and no one can be evicted. The other classic failure is priority-based starvation: a steady stream of high-priority pods can starve lower-priority batch jobs forever. That's why real systems pair priority with quota and with **PriorityClass defaults enforced by admission control** — you can't let developers self-declare critical priority without an admission policy."

**Interviewer**: "What about the QoS classes — Guaranteed, Burstable, BestEffort? How do they interact with priority?"

**Candidate**: "QoS and priority are orthogonal axes. Priority decides *preemption order* — who gets evicted first when something must give. QoS decides *reclaim order* — when the node is under memory or CPU pressure, the kubelet's eviction manager kills BestEffort first, then Burstable, then Guaranteed. Both matter: priority is a scheduling-time decision, QoS is a runtime enforcement decision. The nasty interaction is when a Guaranteed pod has low priority: it won't be OOM-killed by the kubelet (that's the QoS guarantee) but it can still be preempted by a higher-priority pod, and vice versa: a high-priority BestEffort pod can be preempted rarely but OOM-killed readily. A well-run cluster needs admission policies that correlate them — critical workloads should be Guaranteed *and* high priority."

**Interviewer**: "How do you evaluate the resource fit during filtering? What's the actual math?"

**Candidate**: "The kubelet reports allocatable resources per node — capacity minus system-reserved and kube-reserved. Pods request `requests` (the amount the scheduler guarantees) and `limits` (the ceiling). The fit check: for each resource, the sum of requests of existing pods plus the incoming pod's requests must be ≤ the node's allocatable. This is a *requests-based* check — CPU and memory are the first-class resources, but the model extends to ephemeral storage, extended resources like GPUs, and for pods that set limits without requests (a common misconfiguration), the scheduler derives requests from limits. The subtle part: the check is per-node, and the scheduler maintains a cache of pod requests per node, which must be kept in sync with the kubelet's actual state — that's the informer/cache consistency problem that's a classic source of bugs: the scheduler binds to a node that's already overcommitted because the cache was stale."

**Interviewer**: "How does scoring balance the cluster? What's a good scoring strategy?"

**Candidate**: "The default scoring in modern Kubernetes is the *least-allocated* strategy combined with resource weights: a node with more free resources scores higher, so new pods spread to the emptiest node. There are alternatives: most-allocated (bin-packing — good for cost efficiency when you want to consolidate and scale down), and even-spread across zones. The scoring is normalized per resource and weighted — e.g., CPU weight 1, memory weight 1. Each extension point can plug in custom scores, and the final score is a weighted sum. There's an important subtlety: `RequestedToCapacityRatio` scoring normalizes by the node's capacity so a big node and a small node are compared fairly. My design would keep scoring pluggable — filter plugins and score plugins with an ordered extension-point API — because every organization ends up tuning this."

**Interviewer**: "What about topology spread and data locality — pods that should land near their stateful service or spread across failure domains?"

**Candidate**: "For spreading, the topology spread constraints use the label topology — node, zone, region — and the scheduler counts existing pods matching the constraint label in each topology domain, then prefers the domain with the fewest. For locality, the classic technique is scoring up nodes where the pod's data volumes already exist — the kubelet's volume manager reports attached volumes, and a `VolumeBinding` filter rejects nodes that can't satisfy the volume topology, like a regional disk that's only in one zone. The interlock between volume binding and pod scheduling is one of the trickiest parts: the scheduler may need to trigger volume provisioning *before* the pod can bind — the dynamic provisioning path — which means the scheduling cycle itself has to support waiting for external resources. In my design, the scheduler returns 'pending' with a re-queue rather than failing, and the queue key gets an exponential backoff."

**Interviewer**: "How does the scheduler queue work? There's more than one kind of pending pod."

**Candidate**: "The scheduling queue is priority-based: pods are ordered by priority, then by their queue-sort timestamp within the same priority (FIFO within priority level). There are actually three sub-queues: activeQ for pods ready to schedule; unschedulableQ for pods that failed with no node found — they sit there until an event (a node becomes available, a pod is deleted, a preemption happens) moves them back; and backoffQ for pods that failed with transient errors and must wait. The events that move pods between queues — node updates, pod deletions, new nodes — are the same informer events that feed the cache. The most common bug in naive implementations: a pod that's unschedulable due to resource shortage is retried in a tight loop, burning CPU — that's why the unschedulableQ with event-driven re-queueing and a periodic sweep is essential."

**Interviewer**: "How do you test a scheduler? This is notoriously hard to get right."

**Candidate**: "Unit tests with a fake client and a fake node informer: assert that filtering rejects the right nodes, that scoring ranks as expected, that preemption evicts the right victims and never equal-or-higher priorities. Then integration tests with a real kubelet-less API server — sig-scheduling's `integration` suite. Then chaos: kill the scheduler mid-cycle and verify no pod is lost and no node gets double-bound — the pod gets rescheduled on restart. And the best production test is shadow scheduling: run a second scheduler in the cluster in shadow mode that receives a copy of the pod queue and computes assignments without binding, then diff its decisions against the primary — this catches regressions at scale before they hurt anyone."

**Interviewer**: "Final question: what's the most surprising production issue you've seen with scheduling, and what design principle does it teach?"

**Candidate**: "The one I see most: scheduling storms after a node outage. A node dies, hundreds of pods become unschedulable simultaneously, the scheduler thrashes, and the cluster's remaining capacity can't absorb the workload — the result is a cascade where every pod is preempting every other pod's victims. The design principle: the scheduler must be *reactive but damped* — rate-limit preemption, prioritize re-scheduling of the highest-priority disrupted workloads first, and let the unschedulableQ back off rather than thrash. A scheduler that fails soft and retries slowly is far better than one that fails fast and cascades."

---

## Wrap-Up

**What the interviewer is looking for**:
- Clean separation of filter (feasibility) vs score (optimization) vs bind
- Precise preemption rules: never evict equal-or-higher priority, pick lowest-total-priority victims
- Understanding of requests-vs-limits and the allocatable math
- The queue architecture: activeQ / unschedulableQ / backoffQ with event-driven re-queue
- Testability: shadow scheduling, chaos, integration suites

**Common mistakes candidates make**:
- Confusing priority (preemption order) with QoS (reclaim order)
- Designing preemption without the equal-priority protection rule
- Assuming requests are optional in the fit check
- No discussion of the scheduling queue and backoff
- Ignoring volume topology and dynamic provisioning interlock
