# Implementation Guide: Leader Election

## 1. Bully Algorithm

### Concept
Nodes have unique numeric IDs. The node with the highest ID becomes leader. When the leader fails, the next highest initiates election.

### Election Process
1. Node detects leader failure (timeout)
2. Sends ELECTION message to all nodes with higher IDs
3. If no response: declares itself leader, sends COORDINATOR
4. If response: waits for COORDINATOR from higher node
5. If timeout: restarts election

### Implementation
```java
class BullyNode {
    int id;
    int leaderId;
    boolean electionInProgress;

    void startElection() {
        electionInProgress = true;
        List<Integer> higher = getHigherNodes();
        if (higher.isEmpty()) {
            becomeLeader();
            return;
        }
        for (int h : higher) sendElection(h);
        // Wait for response or timeout
        scheduleTimeout(() -> {
            if (electionInProgress) becomeLeader();
        });
    }
}
```

## 2. Raft Leader Election

### Key Concepts
- **Term**: monotonically increasing election epoch
- **States**: Follower, Candidate, Leader
- **Votes**: Candidate needs majority (N/2 + 1)
- **Election timeout**: randomized (150-300ms) to avoid split votes

### Process
1. Follower increments term, becomes Candidate
2. Candidate votes for itself, requests votes from peers
3. If majority: becomes Leader, sends heartbeats
4. If another leader discovered: steps down to Follower
5. If timeout: starts new election with incremented term

## 3. ZooKeeper-based Election

### Using Ephemeral Znodes
1. Create sequential ephemeral znode `/election/node-0000001`
2. Get all children of `/election`
3. The node with the smallest sequence number is leader
4. Each node watches the next lower node (successor pattern)
5. When the predecessor fails, successor becomes leader

### Benefits
- No external messaging needed
- ZooKeeper handles failure detection
- Ordered sequence guarantees unique leader

## 4. Lease-based Leadership

### Concept
Leader holds a lease (time-bound permission). Renews periodically. If lease expires without renewal, a new leader is elected.

### Fencing Tokens
When a lease expires, the ex-leader may still serve stale data. Fencing tokens prevent this:
1. Each lease grants a monotonically increasing token
2. Every request includes the token
3. Resources reject requests with stale tokens
4. Token generation is coordinated via ZooKeeper/Etcd

## 5. Etcd-based Election

Using Etcd's `concurrency` package:
```java
Session session = new Session(etcdClient);
Election election = new Election(session, "/election");
election.campaign(leaderValue);
// Leader holds until session expires
// Followers watch election and wait
```
