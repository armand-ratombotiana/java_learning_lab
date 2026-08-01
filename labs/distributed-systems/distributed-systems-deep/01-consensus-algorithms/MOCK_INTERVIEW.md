# Lab 01: Mock Interview — Consensus Algorithms (Raft)

**Role**: Senior Distributed Systems Engineer
**Duration**: 45 minutes
**Company style**: FAANG / infrastructure (etcd, Consul, ZooKeeper, CockroachDB)

---

**Interviewer**: "Start with the fundamentals: what problem does consensus solve, and what does Raft guarantee?"

**Candidate**: "Consensus solves the problem of *agreement under failures*: a set of servers must agree on a single value (or sequence of values — the log) even though some servers crash, messages are lost or delayed, and processes may run at different speeds. Raft guarantees: **safety under all conditions** — at most one leader per term, log entries are never lost once committed, leaders never overwrite committed entries — and **liveness under partial synchrony**: if a majority of servers are up and can communicate, a leader is eventually elected and the log makes progress. Raft's key contribution is *understandability*: it decomposes the problem into leader election, log replication, safety, and membership changes, rather than Paxos's single opaque protocol."

**Interviewer**: "Walk me through Raft leader election — the algorithm, the messages, the timeouts."

**Candidate**: "Every server is a follower, candidate, or leader, and tracks a **term** — a monotonically increasing integer, like an election number. Followers expect heartbeats from the leader within an **election timeout** (150-300ms randomized). On timeout, a follower becomes a candidate, increments its term, votes for itself, and sends `RequestVote` RPCs to all peers. Each peer grants one vote per term — the first candidate whose term it sees — with the caveat that it won't vote for a candidate whose log is *less up-to-date* (compare last log term, then last log index — this is the log up-to-date check). A candidate wins with votes from a majority; then it becomes leader, sends empty AppendEntries (heartbeats) immediately to reset everyone's timers and establish authority. If no majority (split vote), it times out and retries with a new randomized timeout. The randomization is what breaks ties — two candidates rarely collide twice."

**Interviewer**: "What's the role of the term in voting — why is term-based voting enough to prevent two leaders?"

**Candidate**: "The term is the backbone of the protocol. Every RPC carries the sender's term. A receiver updates its term if the sender's is higher (and steps down if it was leader). A vote is only valid in the term it was cast — and each server votes at most once per term. So in a given term, at most one candidate can collect a majority of votes. Combined with the election restriction (log up-to-date check), a majority vote *in a term* implies the winner's log contains all entries committed in previous terms — the Leader Completeness property. Term-based voting alone gives 'one leader per term'; the log checks make that leader *safe* — it can't overwrite committed entries."

**Interviewer**: "Log replication — how do entries get committed, and what exactly does 'commit' mean?"

**Candidate**: "The leader appends entries to its own log, then sends AppendEntries (with prevLogIndex/prevLogTerm for consistency checks) to followers. A follower appends only if its log matches at prevLogIndex — otherwise it rejects and the leader decrements its `nextIndex` and retries, eventually finding the match point (this is also how the leader repairs divergent follower logs, overwriting from the match point onward). An entry is **committed** when the leader has replicated it to a *majority* of servers; the leader then advances its `commitIndex` and the next AppendEntries tells followers 'commit up to this index.' Followers apply committed entries to their state machines in order. The safety property: an entry committed at term T remains in every future leader's log — guaranteed by the election restriction, because the next leader must have the votes of a majority, and a majority must include a server that had the entry."

**Interviewer**: "What happens when the leader crashes mid-replication — say it sent AppendEntries to 2 of 5 servers, then dies?"

**Candidate**: "The entry is *uncommitted* (2 of 5 is not a majority). Servers time out, a new election happens; the new leader's log — by the election restriction — contains every entry that *was* committed, but this uncommitted entry may or may not survive. Here's the subtle part: **uncommitted entries from the old term must not be committed by counting them toward a majority in the new term** — Raft's *current-term commitment rule*: a leader only counts entries from *its own term* toward commit. Entries from previous terms are committed only indirectly, when a later entry in the new term commits (which implies the log prefix is on a majority). Without this rule, a new leader could commit stale entries that a future leader would then be unable to overwrite without violating safety — the classic protocol bug that appears in many Paxos variants too."

**Interviewer**: "Let's talk about what 'leader election' means in terms of availability. How long is the cluster unavailable when a leader dies?"

**Candidate**: "Election timeout, roughly. With the standard 150-300ms randomized range, the mean detection delay is ~225ms, plus the RPC round trip. So downtime is typically 0.3-1 second. The liveness guarantee is probabilistic: two candidates could keep splitting votes, but the randomization makes repeated collisions exponentially unlikely — the expected time to elect is a few timeouts. There's a tension: shorter timeouts detect failures faster but cause more spurious elections under network jitter; that's why production deployments tune `heartbeat-interval` and `election-timeout` carefully, with a ratio of at least 3-5x between them (etcd defaults: 100ms heartbeat, 1000ms election timeout)."

**Interviewer**: "Your lab implements election with term-based voting. What are the edge cases you'd test to prove correctness?"

**Candidate**: "The canonical test matrix: (1) **single-node cluster** — wins immediately with its own vote. (2) **split vote** — two candidates in the same term each get one vote; verify neither becomes leader, and a later term elects one. (3) **term bump on stale RPC** — a follower with a higher term must reject the old leader's AppendEntries and step the leader down. (4) **log up-to-date check** — a candidate with a shorter log must not steal votes from a peer whose log is longer. (5) **leader crash mid-term** — after a timeout, a new term elects a new leader; the old term never produces two leaders. (6) **no quorum** — 3-node cluster with 2 down: no election completes, no term advances forever — the liveness condition. I'd also verify the invariant: *within any term, at most one server believes it is leader*."

**Interviewer**: "How does Raft handle membership changes — adding and removing servers?"

**Candidate**: "Raft uses **joint consensus**: the cluster transitions through a configuration where both the old and new configurations must agree. The leader appends a special membership-change entry (joint config), commits it (majority of *both* configs), then appends the new config and commits that too. This avoids the classic problem: with naive one-at-a-time changes, you can have two majorities that don't overlap — e.g., moving from 3 to 5 servers by adding one at a time passes through a 4-server configuration where a majority of old (2 of 3) and a majority of new (2 of 4... actually 3 of 4) can elect different leaders. Joint consensus makes the transition atomic. Modern systems (etcd) also add a `learner` mode — new servers join as non-voting learners and only vote once their logs catch up, avoiding the availability dip when a new server joins with an empty log."

**Interviewer**: "Compare Raft to Paxos and to Zab briefly — when do you pick which?"

**Candidate**: "**Paxos** is the more general, minimal protocol — elegant, but notoriously hard to implement correctly; Multi-Paxos is really a different beast from single-instance Paxos, and most 'Paxos' systems (Spanner, Raft-inspired etc.) actually use optimizations Raft formalizes. **Raft** is the engineering choice: leader-centric, decouples election from replication, and its log-matching property makes it straightforward to implement safely — it's why etcd, Consul, and CockroachDB picked it. **Zab** (ZooKeeper) is the closest relative — also a leader-based atomic broadcast protocol with a distinct epoch-based election; its recovery phase (FLE — fast leader election) guarantees the leader has the highest epoch and the newest data. Practical guidance: new system → Raft (or its industrial variants); need raw throughput with Paxos research heritage → Multi-Paxos; greenfield ZK-compatible → Zab. The semantics you get are equivalent: totally ordered atomic broadcast."

**Interviewer**: "Final question — your lab simulates election in one JVM. How would you test it as a *distributed* system?"

**Candidate**: "Three levels. (1) **Deterministic simulation**: a driver that injects message delays/drops and crashes at scripted points, asserting invariants after every step — this catches protocol bugs the probabilistic path never hits (this is what Raft's reference implementation, the 'raft-simulator', does). (2) **Model checking**: enumerate states (small term, small log) and verify no reachable state violates single-leader-per-term — effectively what tools like TLA+/Jepsen's model check do; Jepsen runs the real binary and injects partitions at the network level. (3) **Chaos in production**: kill leaders randomly, partition the cluster, measure availability and safety (no lost committed entries via a journal comparison). The invariant to check in all three: committed entries are never lost, never reordered, and never overwritten — that's the contract consensus sells."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| Protocol mechanics | Election timeout, RequestVote, randomized retries |
| Safety intuition | Log up-to-date check, Leader Completeness, one-leader-per-term |
| Commit semantics | Majority replication, commitIndex propagation |
| Subtle correctness | Current-term commitment rule — the classic trap |
| Operations | Timeout tuning, downtime expectations, learner nodes |
| Testing | Deterministic simulation + model checking + chaos |

### Candidate strengths
- Named the current-term commitment rule unprompted — the mark of someone who's read the paper carefully.
- Knew the 3-5x heartbeat/election ratio and etcd defaults.
- Testing answer covered simulation *and* Jepsen-style validation.

### Gaps to work on
- Didn't mention **client interaction** (linearizable reads via leader read-only with commit-index barrier, `ReadIndex`).
- Could have discussed **snapshotting/compaction** of the log (install snapshot RPC).
- Missed **pre-vote** extension (prevents disruption from partitioned leaders) — good to mention in follow-ups.

## Follow-up study prompts
1. What is the ReadIndex optimization and why does a leader need a quorum check to serve linearizable reads?
2. How does snapshotting interact with the election restriction (log up-to-date check against a snapshot)?
3. What does etcd's raft library add beyond the paper (pre-vote, check-quorum, learner)? 4. Why does Raft require the log-matching property and how does the AppendEntries consistency check enforce it?

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Let's go deep on log safety. Walk me through the AppendEntries consistency check and what it guarantees."

**Candidate**: "Every AppendEntries carries `prevLogIndex` and `prevLogTerm` — the entry *before* the new ones. The follower only appends if its log entry at prevLogIndex has term prevLogTerm — i.e., its log matches the leader's at that point. If not, it rejects; the leader decrements `nextIndex` and retries with an earlier point until the logs match. This drives three properties: (1) **Log Matching** — if two logs contain an entry at the same index with the same term, they contain identical prefixes up to that index; (2) **Leader Completeness** — committed entries survive into the next leader (via the election restriction); (3) **overwrite convergence** — a new leader eventually rewrites divergent follower logs to match its own (deleting uncommitted garbage), which is safe because the deleted entries were never committed. The subtle failure the check prevents: a follower that accepted an entry from a *stale leader* in an old term must not accept conflicting entries at the same index from a different leader without the term check — the prevLogTerm match is what forces the overlap at a consistent point."

**Interviewer**: "Linearizable reads — why can't a leader just answer reads from its local state?"

**Candidate**: "Because 'I am the leader' may be stale knowledge: a partitioned leader that lost its quorum still believes it's the leader, and serving reads from its (possibly stale) state violates linearizability — a client could read an old value after a new leader committed a write, even with real-time ordering. Fixes: (1) **leader lease** — the leader has a quorum-confirmed lease (e.g., it received a majority of heartbeats/acks within a time bound); reads are linearizable while the lease is valid; this is how many systems avoid the read quorum cost; (2) **ReadIndex** — the leader records its `commitIndex` as a read index, does a *quorum round-trip* (a heartbeat) to confirm it's still leader *at that time*, then serves the read at that index; (3) **quorum read** — the leader asks a majority of followers for their commit indexes, takes the max — the expensive but simple option. The interview point: *a leader's authority expires the moment it loses its quorum — reads must re-confirm leadership (lease or quorum ping) or they can serve stale state*."

**Interviewer**: "Snapshots and log compaction — how do they interact with the log up-to-date check?"

**Candidate**: "Logs grow forever, so leaders snapshot their state machine and send `InstallSnapshot` RPCs to lagging followers. The subtlety: a follower that has been down long enough has *no log at all* beyond its last snapshot — so the up-to-date check during elections must compare against the snapshot: 'last log term/index' becomes 'last term/index including the snapshot'. A candidate whose snapshot is newer than a peer's log must win that peer's vote — otherwise a lagging-but-eligible candidate could win an election and then *overwrite* committed state that only exists in snapshots. Real implementations (etcd) store the snapshot term/index in the metadata and treat it as the log's prefix. The second subtlety: when a leader installs a snapshot, the follower discards its log *up to* the snapshot — and the AppendEntries match must never try to re-apply entries older than the snapshot (the leader tracks `matchIndex ≥ snapshotIndex`)."

**Interviewer**: "Pre-vote — why does etcd add it, and what problem does it solve?"

**Candidate**: "Pre-vote is the fix for **disruption by partitioned leaders**: suppose the leader loses connectivity to a majority (its partition is small). The majority times out and elects a new leader in a *higher term*. When the old leader's partition heals, its heartbeats carry a *lower* term — the followers step it down — but if the old leader's partition also contains servers, it can bump its own term and start an election *with a higher term than the cluster's*, forcing the *healthy* majority to step down and re-elect — an availability blip caused by a node that was never actually needed. Pre-vote: a candidate first asks peers 'would you vote for me in term T+1?' — peers say yes only if they haven't heard from a current leader. If the old leader's partition has no quorum... wait — the key case: the old leader *would* get no votes in its pre-vote because the healthy followers just voted for the new leader and won't support a higher-term candidate they've recently heard from. Pre-vote prevents term inflation from disruptive candidates, keeping the healthy majority stable."

**Interviewer**: "Final deep-dive: your lab simulates election with a discrete-step driver. How do you make the simulation *convincing* — what makes it a real test rather than a demo?"

**Candidate**: "Three upgrades. (1) **Adversarial event injection**: the driver must be able to delay, drop, duplicate, and reorder *arbitrary* messages and kill/revive nodes at arbitrary steps — a scripted scenario file that can express 'drop the first RequestVote from A' — because the interesting bugs hide in exactly these events. (2) **Invariant checking at every step**, not just at the end: single-leader-per-term, log-matching (compare all logs' prefixes), committed-entries-never-lost (checkpoint a committed entry, then run chaos, then assert it's in the new leader's log), and the current-term commitment rule (no entry commits from an old term without a newer-term entry covering it). (3) **Statistical runs**: run thousands of randomized scenarios and report the *distribution* — election completion time, number of terms per run, availability gaps — because a consensus protocol's liveness is probabilistic, and one green run proves nothing. That's the difference between a demo and a test: *scripted adversarial events + step-level invariants + statistical coverage*."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Memorize the AppendEntries consistency-check story with the 'match at prevLogIndex/prevLogTerm' names — it came out slightly muddled under pressure.
- Prepare a whiteboard of the Raft state diagram (follower/candidate/leader with all transitions and term rules) before the interview.
- Rehearse the linearizable-read answer (lease vs ReadIndex vs quorum read) as three bullet options.

### One-sentence takeaway
- "Raft is a log with an election on top: every safety property reduces to 'the leader that wins a majority in a term must have the complete committed prefix'."

### Self-check questions (run before the real interview)
1. Can I explain why a leader must count only *its own term's* entries toward commit?
2. Can I enumerate the three ways to serve linearizable reads and the cost of each?
3. Can I describe the pre-vote problem and its fix in one minute?
4. Do I know how snapshots interact with the log up-to-date check?
5. Can I write the canonical six-case election test matrix from memory?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** What does the election timeout do, and why is it randomized?
**Hint.** Triggers candidacy; randomization breaks split-vote ties across retries.

**Q2.** What is the log up-to-date check?
**Hint.** Voters reject candidates whose (lastTerm, lastIndex) is behind their own — protects committed entries.

**Q3.** When is an entry 'committed'?
**Hint.** Replicated to a majority AND (for old-term entries) covered by a committed entry in the current term.

**Q4.** Why can't a leader count old-term entries toward commit?
**Hint.** The current-term rule — old entries commit only indirectly, else stale entries could be committed and become un-overwritable.

**Q5.** What is the Leader Completeness property?
**Hint.** Every committed entry exists in the next leader's log — via majority overlap + election restriction.

**Q6.** How does a new leader repair a divergent follower log?
**Hint.** nextIndex decrement + AppendEntries consistency check (prevLogIndex/prevLogTerm) until match, then overwrite.

**Q7.** What is a learner node for?
**Hint.** Joins as non-voting until caught up — no availability dip from an empty-log voter.

**Q8.** How do you serve linearizable reads without a quorum per read?
**Hint.** Leader lease, or ReadIndex (quorum heartbeat + commitIndex snapshot).

**Q9.** What does pre-vote prevent?
**Hint.** Term inflation by a partitioned ex-leader disrupting a healthy majority.

**Q10.** Raft, Paxos, or Zab for a new system?
**Hint.** Raft — engineering-friendly; Zab if ZK-compatible; Paxos heritage for research-grade throughput.

### Scoring
- **8-10 correct**: ready for the consensus loop.
- **5-7**: revise commit semantics and the election restriction.
- **<5**: re-read the walkthrough before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab (`RaftElection`) and run the six-case test matrix.
**Day 3**: Quick-Fire rounds; draw the Raft state diagram from memory.
**Day 4**: Rehearse the current-term commitment rule and ReadIndex/lease answers.
**Day 5**: Drill the extended rounds (AppendEntries check, snapshots, pre-vote, simulation design).
**Day 6**: Mock interview, 45 minutes, no notes.
**Day 7**: Score against the Debrief table; study the follow-up prompts.
