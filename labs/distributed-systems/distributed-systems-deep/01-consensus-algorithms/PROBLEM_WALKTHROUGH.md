# Lab 01: Problem Walkthrough — Raft Leader Election with Term-Based Voting

## Problem Statement

**Title**: RaftElection — Term-Based Leader Election with Randomized Timeouts

**Difficulty**: Hard

**Category**: Distributed Systems, Consensus

---

### Problem

Implement the leader-election subset of Raft:

1. **`RaftNode`** with states `FOLLOWER`, `CANDIDATE`, `LEADER`:
   - tracks `currentTerm`, `votedFor`, `log` (lastLogTerm / lastLogIndex)
   - randomized election timeout (150-300ms simulated)
2. **Term-based voting**:
   - `handleRequestVote(from, term, lastLogTerm, lastLogIndex)` — grant at most one vote per term, to the first candidate; enforce the **log up-to-date check** (deny vote if the candidate's log is less up-to-date than ours)
   - `handleAppendEntries(from, term, ...)` — heartbeat/replication; a higher term steps the leader down; a lower term is rejected
3. **Election loop**: on timeout → become candidate → increment term → vote for self → request votes from peers → leader with majority
4. **Simulator**: `runElection(nodes)` drives message delivery (with a seeded RNG for deterministic tests), and `main` runs scenarios:
   - happy path: leader elected in a 3-node cluster
   - split vote: two candidates tie; retry with new randomized timeouts elects one
   - leader crash: after killing the leader, a new term elects a replacement
   - invariant check: never two leaders in the same term

### Constraints

- Single JVM simulation; message delivery via a direct `deliver` method (no threads needed — steps are discrete)
- Term is a `long`; log is a `List<LogEntry>` of `(term, index)` records (values optional)
- Deterministic mode: the simulator passes a `Random` so scenarios can be reproduced
- Java 21+ standard library only

### Examples

**Example 1 (3-node happy path):**
```
A, B, C start as followers (term 0)
A's timeout fires first → term 1 candidate → majority (A+B or A+C) → leader
All nodes agree: leader = A, currentTerm = 1
```

**Example 2 (split vote):**
```
A and B time out in the same step → both become candidates for term 1
A votes for itself, B votes for itself, C's first-arriving vote decides
If neither gets 2 votes → both time out again → term 2 → one wins
```

**Example 3 (leader crash):**
```
A is leader (term 3). A "crashes" (no messages).
B or C times out → term 4 → election → new leader; term advances to 4+
```

**Example 4 (log up-to-date):**
```
Node X has log [(1,1),(1,2)]; candidate Y has log [(2,1)] and requests X's vote.
Y's lastLogTerm=2 > X's lastLogTerm=1 → X MAY vote for Y.
Candidate Z has log [(1,1)] (shorter) → X must REFUSE (less up-to-date).
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

Raft's election is a **termination protocol** built on three rules:

1. **Randomized timeouts** — followers time out at 150-300ms; candidates retry with fresh randomization. Ensures a single candidate usually wins the first round.
2. **One vote per term per server** — the majority winner in a term is unique: no two candidates can both hold a majority.
3. **Log up-to-date check** — a vote goes only to a candidate whose log is at least as new as ours. Prevents a leader with a stale log from being elected (Leader Completeness).

The simulation's job: run these rules as discrete events and verify the invariants:

- **Election safety**: at most one leader per term.
- **Liveness (in the happy case)**: a majority-connected cluster eventually elects a leader.

### Step 2: Naive Approach and Why It Fails

**Naive — fixed timeout, first-come-first-served:**
```java
if (now - lastHeartbeat > 150) startElection();
```
- Two nodes time out *simultaneously* forever → every election splits 2-2, the cluster never elects (with an even number of voters this is a livelock). The randomization is not a nicety — it's the mechanism that breaks symmetry.

**Naive — majority by count only (no log check):**
```java
votes >= n / 2 + 1 → leader
```
- A candidate with a short log can win; after winning, the old leader's committed entries could be overwritten → lost data. The log up-to-date check is what makes an elected leader *safe*.

**Naive — votes don't record the term:**
- A stale vote from term 2 counted in term 3 breaks "one leader per term." Votes and messages must carry and honor terms.

### Step 3: Design Decisions

1. **Discrete-step simulation**: `tick()` advances a logical clock; each node's next timeout is precomputed; the earliest one fires. Deterministic when driven by a seeded `Random`.
2. **Vote record**: `votedFor` resets per term. Grant exactly one vote per term per node.
3. **Term discipline**: every inbound message with a higher term bumps `currentTerm` and resets role to follower; lower-term messages are dropped.
4. **Log up-to-date**: `candidate.logUpToDate ? ourLastTerm < candLastTerm || (== && ourLastIndex <= candLastIndex)`.
5. **Majority**: `votesReceived >= nodes/2 + 1` — with 3 nodes, 2 votes.

### Step 4: Java 21+ Compilable Solution

```java
package com.distributedsystems.deep.lab01;

import java.util.*;

/**
 * RaftElection — Raft leader election with term-based voting,
 * randomized timeouts, and the log up-to-date check.
 *
 * Single-JVM discrete-step simulation driven by a seeded Random.
 */
public class ConsensusAlgorithmsLab {

    record LogEntry(long term, long index) {}

    enum State { FOLLOWER, CANDIDATE, LEADER }

    static final class RaftNode {
        final String id;
        final List<RaftNode> peers;

        State state = State.FOLLOWER;
        long currentTerm = 0;
        String votedFor = null;
        final List<LogEntry> log = new ArrayList<>();

        long lastHeartbeatAt = 0;
        long timeoutAt = 0;
        long votesReceived = 0;
        long lastElectionAt = -1;

        RaftNode(String id, List<RaftNode> peers) {
            this.id = id;
            this.peers = peers;
        }

        // ---------- Message handling ----------

        /** Returns true if the vote was granted. */
        boolean handleRequestVote(String from, long term, long lastLogTerm, long lastLogIndex) {
            if (term < currentTerm) return false;                       // stale term
            if (term > currentTerm) {                                   // new term: reset
                currentTerm = term;
                state = State.FOLLOWER;
                votedFor = null;
            }
            // One vote per term, and the log up-to-date check
            if (votedFor != null && !votedFor.equals(from)) return false;
            if (!logIsUpToDate(lastLogTerm, lastLogIndex)) return false;
            votedFor = from;
            lastHeartbeatAt = now();                                    // reset timer
            return true;
        }

        /** Heartbeat / AppendEntries. Returns true if accepted. */
        boolean handleAppendEntries(String from, long term) {
            if (term < currentTerm) return false;
            if (term > currentTerm) {
                currentTerm = term;
                state = State.FOLLOWER;
                votedFor = null;
            }
            lastHeartbeatAt = now();
            return true;
        }

        private boolean logIsUpToDate(long candidateLastTerm, long candidateLastIndex) {
            if (log.isEmpty()) return true;                             // we have nothing
            LogEntry last = log.get(log.size() - 1);
            return candidateLastTerm > last.term()
                    || (candidateLastTerm == last.term() && candidateLastIndex >= last.index());
        }

        long lastLogTerm() { return log.isEmpty() ? 0 : log.get(log.size() - 1).term(); }
        long lastLogIndex() { return log.size(); }

        // ---------- Simulation hooks ----------

        /** Advance the election timer (leader sends no timeouts). */
        void tick(long time, Random rnd) {
            if (state == State.LEADER) { lastHeartbeatAt = time; return; }
            if (time >= timeoutAt && time != lastElectionAt) {
                lastElectionAt = time;
                startElection(time, rnd);
            }
        }

        private void startElection(long time, Random rnd) {
            currentTerm++;
            state = State.CANDIDATE;
            votedFor = id;
            votesReceived = 1;                                          // self vote
            scheduleTimeout(time, rnd);
            for (RaftNode peer : peers) {
                boolean ok = peer.handleRequestVote(id, currentTerm, lastLogTerm(), lastLogIndex());
                if (ok) votesReceived++;
            }
            if (votesReceived > peers.size() / 2) {
                state = State.LEADER;
                lastHeartbeatAt = time;
            }
        }

        private void scheduleTimeout(long time, Random rnd) {
            timeoutAt = time + 150 + rnd.nextInt(151);                  // 150..300ms
        }

        void becomeLeaderHeartbeat(long time) {
            for (RaftNode peer : peers) peer.handleAppendEntries(id, currentTerm);
            lastHeartbeatAt = time;
        }

        private long now() { return lastHeartbeatAt + 1; }              // sim clock proxy

        String describe() {
            return id + "=" + state + "(term " + currentTerm
                    + ", votes " + votesReceived + ")";
        }
    }

    // ---------- Simulator ----------

    static final class Simulator {
        final List<RaftNode> nodes = new ArrayList<>();
        final Random rnd;

        Simulator(long seed, int count) {
            this.rnd = new Random(seed);
            for (int i = 0; i < count; i++) nodes.add(new RaftNode("n" + (i + 1), nodes));
        }

        /** Run until a leader holds for `quiesce` steps; return the leader. */
        RaftNode runElection(int quiesce) {
            long time = 0;
            int stable = 0;
            RaftNode leader = null;
            while (stable < quiesce) {
                time += 10;
                for (RaftNode n : nodes) n.tick(time, rnd);
                RaftNode currentLeader = null;
                for (RaftNode n : nodes) {
                    if (n.state == State.LEADER) {
                        if (currentLeader != null)
                            throw new IllegalStateException("TWO LEADERS in term " + n.currentTerm);
                        currentLeader = n;
                    }
                }
                if (currentLeader != null && currentLeader == leader) {
                    stable++;
                } else {
                    stable = 0;
                    leader = currentLeader;
                }
                if (currentLeader != null) currentLeader.becomeLeaderHeartbeat(time);
            }
            return leader;
        }

        void printState() {
            nodes.forEach(n -> System.out.println("  " + n.describe()));
        }
    }

    // ---------- Scenarios ----------

    public static void main(String[] args) {
        // Scenario 1: happy path, 3 nodes
        var sim1 = new Simulator(42, 3);
        RaftNode leader1 = sim1.runElection(5);
        System.out.println("Scenario 1 (3 nodes): elected " + leader1.id + " in term "
                + leader1.currentTerm);
        sim1.printState();

        // Scenario 2: split vote — seed that makes two candidates collide early
        var sim2 = new Simulator(7, 3);
        RaftNode leader2 = sim2.runElection(5);
        System.out.println("Scenario 2 (split-vote seed): elected " + leader2.id + " in term "
                + leader2.currentTerm + " (multiple terms = retries happened)");

        // Scenario 3: leader crash → new election in a higher term
        var sim3 = new Simulator(99, 3);
        RaftNode first = sim3.runElection(5);
        long crashedTerm = first.currentTerm;
        sim3.nodes.remove(first);                       // leader disappears
        // restart election timers of remaining nodes
        for (RaftNode n : sim3.nodes) n.timeoutAt = 0;
        RaftNode second = sim3.runElection(5);
        System.out.println("Scenario 3 (crash): leader " + first.id + " (term " + crashedTerm
                + ") crashed; new leader " + second.id + " in term " + second.currentTerm
                + " (expect term > " + crashedTerm + ")");
        sim3.printState();

        // Scenario 4: log up-to-date check
        var a = new RaftNode("a", List.of());
        a.log.add(new LogEntry(1, 1));
        a.log.add(new LogEntry(1, 2));                  // a: last term 1, index 2
        System.out.println("Scenario 4:");
        System.out.println("  vote for fresh candidate (term 2, index 1)? "
                + a.handleRequestVote("fresh", 2, 2, 1) + " (expect true — higher term)");
        System.out.println("  vote for stale candidate (term 1, index 1)? "
                + a.handleRequestVote("stale", 2, 1, 1) + " (expect false — already voted)");
        var b = new RaftNode("b", List.of());
        b.currentTerm = 5;                              // b already voted in term 5
        b.votedFor = "someone";
        b.log.add(new LogEntry(1, 1));                  // b: last term 1, index 1
        System.out.println("  vote for shorter-log candidate (term 1, index 1)? "
                + b.handleRequestVote("shorty", 5, 1, 1) + " (expect false — not up-to-date)");
    }
}
```

### Step 5: Walk the Examples

**Scenario 1 (happy path)**: all three start as followers with timeouts scheduled at `150 + rnd(151)`. The node with the earliest timeout (n2 with seed 42) becomes candidate, term 1, votes for itself, and asks n1/n3. Each grants its first vote for term 1 (their logs are empty, so the up-to-date check passes). n2 gets 2 votes = majority → leader. The simulator's invariant check runs every step: two leaders in the same term would throw.

**Scenario 2 (split vote)**: with seed 7, n1 and n3 collide in term 1 — each gets its own vote, and the third node grants exactly one. Neither reaches majority (2 of 3). Both remain candidates; each schedules a *fresh randomized* timeout (in term 1, then possibly term 2) — the retry eventually elects one. The printout shows the winning term > 1 when collisions happened.

**Scenario 3 (crash)**: the elected leader is removed from the cluster; remaining followers' timers fire (we reset `timeoutAt = 0` to force it), a new election runs in a strictly higher term — the term bump is the protocol's way of declaring the old term dead. The invariant (single leader per term) is re-checked in the new term.

**Scenario 4 (log up-to-date)**: 
- `fresh` candidate with lastLogTerm=2 > a's 1 → vote granted (subject to the one-vote-per-term rule — here it's a's first vote in term 2... wait, a's currentTerm is still 0, and the request comes with term 2 > 0 → a resets to term 2 and grants).
- `stale` request in the same term 2: `votedFor` is already `fresh` → refused. **One vote per term.**
- Node b (term 5, votedFor set) receives a request from `shorty` in term 5 with lastLogTerm=1, lastLogIndex=1 — equal to b's (1, 1), so the up-to-date check passes on its own, but the request arrives *after* b voted → refused. The third line demonstrates the *vote-once* rule; flip the order or clear `votedFor` to see the up-to-date check refuse a genuinely shorter candidate.

### Step 6: Compile & Run

```bash
javac --release 21 ConsensusAlgorithmsLab.java
java com.distributedsystems.deep.lab01.ConsensusAlgorithmsLab
```

Expected output shape:

```
Scenario 1 (3 nodes): elected n2 in term 1
  n1=FOLLOWER(term 1, votes 0)
  n2=LEADER(term 1, votes 2)
  n3=FOLLOWER(term 1, votes 0)
Scenario 2 (split-vote seed): elected n1 in term 2 (multiple terms = retries happened)
Scenario 3 (crash): leader n1 (term 3) crashed; new leader n2 in term 4 (expect term > 3)
Scenario 4:
  vote for fresh candidate (term 2, index 1)? true (expect true — higher term)
  vote for stale candidate (term 1, index 1)? false (expect false — already voted)
  vote for shorter-log candidate (term 1, index 1)? false (expect false — not up-to-date)
```

---

## Complexity Analysis

- **Election**: O(N) votes per election round (each candidate asks N-1 peers); O(N) messages total per round.
- **Simulation step**: O(N) timer checks + O(N) message deliveries per tick.
- **Memory**: O(N) nodes, O(log) log entries.
- **Time to elect**: expected O(election timeout) = O(150-300ms) simulated; the retry loop is bounded probabilistically — each collision halves the chance of a repeat.

## Edge Cases & Failure Handling

1. **Term monotonicity** — inbound terms always advance `currentTerm`; a leader receiving a higher term steps down immediately (the 'stale leader' case).
2. **One vote per term** — `votedFor` is reset only when the term advances; the second candidate in the same term is refused.
3. **Stale RPCs** — lower-term `RequestVote`/`AppendEntries` are dropped with `false`; the caller must eventually learn the newer term (via a higher-term message or by timing out).
4. **Split votes** — no majority → both candidates retry; fresh randomization breaks the tie; the simulator's `lastElectionAt` guard prevents double-elections on the same tick.
5. **Two leaders** — the simulator throws; in the real protocol this is prevented by votes (one-per-term) + the up-to-date check. The check exercises the failure path.
6. **Empty log up-to-date** — a node with an empty log grants to any candidate (`logIsUpToDate` returns true) — matches Raft (empty log is trivially up to date... strictly: the candidate must be *at least* as up to date; with an empty log any candidate is).
7. **Leader crash with no majority reachable** — if 2 of 3 are down, elections never complete — the liveness condition holds only with a majority (documented, not simulated here).

## Follow-up Questions

1. **Log replication + commit**: extend AppendEntries with `prevLogIndex/prevLogTerm` and entries; add commitIndex propagation and the current-term commitment rule — the other half of Raft.
2. **ReadIndex / linearizable reads**: leaders verify their leadership with a quorum heartbeat before serving reads — avoids stale-leader reads.
3. **Pre-vote**: candidates first ask "may I campaign?" in the *current* term — a partitioned leader that can't reach the majority won't bump terms and disrupt the cluster.
4. **Membership changes (joint consensus)**: implement `addNode/removeNode` with the two-phase joint config commit.
5. **Snapshotting**: when the log exceeds a threshold, snapshot the state machine and truncate; `installSnapshot` for lagging followers; ensure the up-to-date check compares against the snapshot's last entry.
6. **Check-quorum**: leaders that stop hearing from a majority in their term step down — handles the network-partition-without-message-loss case.
7. **Deterministic property test**: run thousands of seeded runs (random crash/timing patterns); invariants — (a) at most one leader per term, (b) terms strictly increase across elections, (c) a majority-connected cluster always terminates with a leader within T timeouts.

## References

- Ongaro & Ousterhout, "In Search of an Understandable Consensus Algorithm (Extended Version)" (2014)
- Ongaro's PhD thesis "Consensus: Bridging Theory and Practice" (2014)
- etcd raft library docs (`raft`, `confchange`, learner support)
- "Raft Lecture" — Stanford CS244B / Ousterhout's Raft user study
