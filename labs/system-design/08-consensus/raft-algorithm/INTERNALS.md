# Raft Algorithm Internals

## ⏱️ Leader Election Mechanics

Raft divides time into **Terms** of arbitrary length. Each term begins with an election.

### 1. The Randomized Election Timeout
The most brilliant innovation in Raft is the **Randomized Election Timeout**.
Every Follower has a countdown timer (e.g., between 150ms and 300ms).
- If the Follower receives a Heartbeat from the Leader, it resets its timer.
- If the timer hits zero (because the Leader crashed or the network dropped), the Follower assumes the Leader is dead.

It immediately increments the current Term number, transitions to the **Candidate** state, votes for itself, and sends `RequestVote` RPCs to all other nodes.

**Why Randomized?** If all Followers had a 150ms timeout, they would all become Candidates at the exact same millisecond, vote for themselves, and no one would win a majority (a Split Vote). By randomizing the timeouts, one Follower will always wake up a few milliseconds before the others, request their votes, and win the election before the others even wake up.

### 2. Winning an Election
A Candidate wins if it receives votes from a **majority** of the nodes in the cluster.
- In a 5-node cluster, a majority is 3. 
- This requires clusters to have an odd number of nodes to tolerate failures (e.g., a 5-node cluster can survive 2 node failures and still maintain a majority of 3).

## 📜 Log Replication Mechanics

Once a Leader is elected, it begins servicing client requests.

### 1. The AppendEntries RPC
When a client sends a command (e.g., `x = 5`), the Leader:
1. Appends the command to its own log as an *uncommitted* entry.
2. Sends an `AppendEntries` RPC containing the command to all Followers.

### 2. The Two-Phase Commit (Raft Style)
The Leader does not immediately execute the command. It waits for the Followers to respond.
- If a **majority** of Followers reply that they successfully appended the command to their logs, the Leader considers the entry **Committed**.
- The Leader then executes the command on its State Machine (e.g., actually updates the database to $x=5$) and returns a success response to the client.
- In the next Heartbeat, the Leader informs the Followers that the entry is committed, and the Followers execute it on their own State Machines.

### 3. Log Consistency
If a Follower's log differs from the Leader's log (because it was offline during previous terms), the Leader forces the Follower to duplicate its own log. The Leader finds the latest point where their logs agree, deletes everything after that point on the Follower, and sends it the correct entries.