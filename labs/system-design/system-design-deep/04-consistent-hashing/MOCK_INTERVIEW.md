# Mock Interview Transcript: Consistent Hashing Ring with Virtual Nodes

| Field | Detail |
|-------|--------|
| **Level** | Senior Backend Engineer (L5) |
| **Duration** | 45 minutes |
| **Format** | Whiteboard + implementation |
| **Problem** | "Implement a consistent hashing ring with virtual nodes for a distributed key-value store. A node join or leave should move as few keys as possible." |

---

## Part 0: Scene Setting (2 minutes)

**Interviewer (I):** We run a distributed cache cluster — 100 nodes, keys sharded
across them. The cluster scales up and down: nodes are added for capacity, and
nodes die and get replaced. Today's naive scheme, `hash(key) % n`, moves almost
every key whenever `n` changes — the cluster thrashes. I want you to implement
consistent hashing with virtual nodes, and then tell me the failure story.

**Candidate (C):** Let me confirm the topology and the operations that matter
before writing code.

---

## Part 1: Clarifying Questions (5 minutes)

**C:** Three questions. One: how many keys, and what's the acceptable fraction of
keys that move when one node joins or leaves? Two: is replica placement on the
ring, or a separate concern? Three: what hash function and how many virtual nodes
per physical node — or do you want me to pick and justify?

**I:** Tens of millions of keys. The whole point is that one node's departure moves
roughly `1/n` of keys. Replicas are out of scope today. Pick the parameters and
justify them.

**C:** Good — `1/n` is exactly the consistent hashing contract. I'll implement the
ring, then make the parameters measurable: distribution balance and moved-keys
fraction on join/leave. Those two numbers are the entire success criterion, and
I'll show them with a demo, not with hand-waving.

---

## Part 2: Why Not Modular Hashing (6 minutes)

**I:** Start with why `hash(key) % n` is wrong.

**C:** Two failures. First, when `n` changes from 100 to 101, every key's bucket
index changes — roughly 99% of keys move. The cluster thrashes: a cache miss storm,
network transfer of almost the entire dataset, and during a cascade of node
failures you can get a "miss amplification" loop where each death triggers more
moves, which triggers more failures. Second, hot nodes: real-world keys aren't
uniform, and `% n` has no mechanism to spread the load of a hot key across more
than one node.

**C:** Consistent hashing fixes the first problem structurally. Every node maps to
one or more points on a circle; every key maps to a point and walks clockwise to
the first node. When a node leaves, only the keys in its arc — the stretch of ring
between it and the previous node — move to its clockwise successor. Expected
fraction: `1/n`. When a node joins, it inherits exactly the arc between itself and
its predecessor. Everything else stays put.

---

## Part 3: Why Virtual Nodes (7 minutes)

**I:** But a plain one-point-per-node ring has a problem. What is it?

**C:** Uneven arcs. With one point per node, the arcs are the random gaps between
`n` uniform points — by the birthday-paradox intuition, the biggest arc is roughly
`(log n)/n` of the ring instead of `1/n`. With 100 nodes the unluckiest node can
hold ~4-5× its fair share, and the luckiest far less. Keys aren't the only load —
if nodes serve hot keys, an unlucky arc is a hotspot, and when it dies, the whole
arc — hotspot included — lands on a single successor.

**I:** And the fix?

**C:** **Virtual nodes**: each physical node registers `v` points on the ring,
e.g. `node-a#0 ... node-a#149`. The ring now has `n × v` points, so arcs
concentrate around the fair share `1/(n×v)`, and each physical node's `v` points
are spread around the ring, so its total load is the sum of `v` independent arcs —
the variance drops by a factor of `v`. That's the same idea as sharding one
physical resource into many independent bets. A second benefit: when node A dies,
its `v` arcs each fall to *different* successors, so the load of the dead node is
spread across `v` survivors instead of one — that directly softens the cascade
failure mode I mentioned.

**I:** How do you pick `v`?

**C:** It's a trade. Higher `v` = better balance and better failover spreading, but
more ring memory (150 points per node is nothing — a few KB) and, more importantly,
more *movement granularity*: when one physical node leaves, its `v` arcs each
relocate to different successors, so the moved set is spread across many nodes.
In practice, 100-200 vnodes per physical node at this scale; I'll make it a
parameter and demonstrate the balance improvement from v=1 to v=100 with
measured imbalance.

---

## Part 4: Implementation Discussion (8 minutes)

**C:** Structure: a `TreeMap<Long, String>` — the sorted map *is* the circle, and
clockwise lookup is `ceilingEntry(hash(key))`, wrapping to `firstEntry()` past the
end. Node add: insert `v` points `hash(node + "#" + i)`. Node remove: drop those
points. The core query is O(log(n×v)).

**I:** Which hash?

**C:** Requirements: 64-bit range, fast, deterministic across processes. I'll use
FNV-1a 64-bit — trivial to implement portably, no crypto dependency, and hashing
quality on these keys is fine (MD5's statistical quality buys nothing here since
the ring's correctness doesn't depend on collision resistance — it's a distribution
problem, not a security one).

**I:** What can go wrong with the simple "walk clockwise" rule in production?

**C:** Four things. (1) **Replica count awareness** — in a real cache, keys are
replicated to the next `R` nodes; the walk must skip nodes that are already
replicas of the same key. (2) **Failure detection lag** — the ring must not
exclude a node until the cluster agrees it's dead (a coordinator or gossip-based
suspicion protocol); otherwise a *transient* blip moves `1/n` of keys for nothing.
(3) **Hash domain edges** — the wrap at `firstEntry` must be handled exactly once;
off-by-one bugs here cause keys to silently map to the wrong node. (4) **Key
movement must be data-movement** — when a node joins, only keys in its new arc
migrate; the ownership change is the *trigger* for migration, and the migration
job is the part that actually has to be throttled and made resumable.

---

## Part 5: Implementation Walkthrough (10 minutes)

**C:** (writing) The ring keeps a `TreeMap<Long, String>`. `addNode` stamps `v`
points; `removeNode` does a conditional value-match remove so we never delete
another node's point if hashes ever collided. `getNode` is the ceiling lookup with
wrap-around. Then I'll write the measurement harness: distribute 10,000 keys,
print each node's share and the imbalance ratio (max share / fair share), then
simulate a node leaving and count exactly which keys moved.

**I:** What do you expect to see?

**C:** At v=1 the imbalance is visibly lumpy — some node gets 1.5-2× its share at
this key count. At v=100 it should sit near 1.1-1.2×, which is the variance
reduction from `v` independent arcs. And on leave: expected moved fraction ≈
`1/n` regardless of `v`, but at v=1 the dead node's entire load lands on one
successor, while at v=100 it lands on ~v distinct successors. Both numbers are
printed — the demo *is* the justification.

**I:** One subtle thing — when the same node's points are removed, keys that
hashed *between* its points also remap. Walk me through the ownership change.

**C:** Correct — removing node A's `v` points doesn't just move keys that pointed
at A. A key that pointed at A's point #7 now falls to whatever point follows #7
clockwise. If that point belongs to B, the key moves to B — but only if the key is
in the arc owned by A's point #7. Since A's points are spread across the ring, the
*key set* that moves is exactly the union of A's arcs — about `1/n` of the keys —
and each arc's successor is an independent node. That's the whole design in one
paragraph: removal moves `1/n` keys, spread across `v` receivers.

---

## Part 6: Hardening and Follow-Ups (6 minutes)

**I:** Hash collisions between node points?

**C:** Vanishingly rare with 64-bit FNV, but cheap to make safe: on insert, if the
position is occupied, append a salt and rehash (`node#i#1`, `node#i#2`, ...). My
remove uses value-matched deletion, so a collision can't delete the wrong node.

**I:** What if traffic is skewed — a hot key?

**C:** Consistent hashing spreads *ownership* uniformly but does nothing for hot
*keys*; a single celebrity key still hits one node. That's handled above the ring:
replication with a fanout factor, or the "spray" technique — the owner node
publishes the hot key under multiple pseudo-key suffixes, and ring lookup lands
the variants on different nodes. The ring stays dumb; the hot-key policy lives in
the data plane.

**I:** Weighted nodes — a 64 GB node vs a 16 GB one?

**C:** Easy and clean: more virtual nodes for the bigger node. `v` becomes
`weight × baseV`; since arcs are per-point, a heavier node simply owns more ring
fraction. This is the standard trick and it's why vnodes are the industry default
(Redis Cluster, Cassandra, DynamoDB-style designs all have a vnode notion).

**I:** Ordering guarantees, replication?

**C:** None from the ring — ownership is a function, not a sequence; replication is
"walk clockwise skipping self, take next R owners." The ring gives *stable
ownership*, which is the property that makes replica coordination and migration
jobs tractable.

---

## Part 7: Closing and Feedback (3 minutes)

**I:** Wrap it up.

**C:** Consistent hashing turns node churn from a full-cluster migration into a
`1/n` rebalance — stable ownership over a sorted ring. Virtual nodes fix the two
things one-point-per-node rings get wrong: arc variance and single-receiver
failover, both via the law of large numbers across `v` independent arcs. And the
implementation is 30 lines — the hard parts are the parameters, the hash domain,
and the migration job downstream, which is where production effort actually goes.

**I:** Excellent. You measured the claim instead of asserting it, knew the
`(log n)/n` max-arc result, and immediately connected vnodes to cascade
mitigation — that's the senior-level insight most candidates miss. Minor: you
could have sketched the ring and the arc-on-join migration on the board earlier,
and mentioned how the ring interacts with the cluster membership protocol
(joins/leaves are membership events, not hash changes).

---

## Evaluation Scorecard

| Dimension | Observation | Score (1-5) |
|-----------|-------------|-------------|
| Problem framing | `% n` failure modes: thrash, miss amplification, hot nodes | 5 |
| Theory | `1/n` movement contract; `(log n)/n` max arc; variance reduction | 5 |
| Virtual nodes | Two reasons: balance AND failover spreading — both articulated | 5 |
| Implementation | TreeMap ceiling lookup, value-matched delete, collision handling | 5 |
| Measurement | Imbalance ratio + moved-fraction demo as the success criteria | 5 |
| Operational depth | Membership protocol, migration job, hot-key spray | 4 |
| Breadth | Replication skip-walk mentioned; weighted vnodes covered | 4 |

**Overall: Strong Hire** — theory, implementation, and operations all anchored to
measurable claims.

## Common Pitfalls Candidates Hit

- Implementing one-point-per-node and calling it done — no balance argument.
- Missing that vnodes also spread a dead node's load across many receivers.
- Not defining the success metric (imbalance ratio, moved fraction) before coding.
- Forgetting ring wrap-around or colliding hash positions.
- Claiming consistent hashing fixes hot keys — it fixes ownership, not popularity.
- Ignoring that joins/leaves are membership-protocol events in production.
