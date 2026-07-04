# Consistency Models - INTERNALS

## Two-Phase Commit (2PC) Internals

### Phase 1: Prepare
```
Coordinator → All Participants: "Can you commit?"
Participant: Log prepare record, reply "Ready" or "Abort"
```

### Phase 2: Commit/Abort
```
If all Ready:
    Coordinator → All: "Commit"
    Participant: Log commit, release locks
Else:
    Coordinator → All: "Abort"
    Participant: Log abort, rollback
```

### 2PC in Java (JTA)
```java
@Transactional
public void transferFunds(String from, String to, BigDecimal amount) {
    debit(from, amount);           // Participant 1
    credit(to, amount);            // Participant 2
    // JTA manages 2PC between datasources
}
```

## Raft Consensus Internals

### Term and Election
- Time divided into terms
- Each term has at most one leader
- Leader sends heartbeats to maintain authority
- If followers don't hear from leader → start election
- Winner gets majority votes → becomes leader

### Log Replication
```
Leader → Follower: AppendEntries RPC (log entries)
Follower: Write log, reply success
Leader: Once majority confirms, apply to state machine
```

### Safety Properties
- **Election safety**: At most one leader per term
- **Log matching**: Logs are consistent across nodes
- **State machine safety**: If a node applies a log entry, no other node can apply a different entry at that index

## Vector Clocks Internals

### Partial Ordering
```java
// Node A increments its own counter: [A:1, B:0, C:0]
// Node B processes event from A: [A:1, B:1, C:0]

// Compare clock A and clock B:
// A: [A:2, B:0, C:0]
// B: [A:1, B:1, C:0]
// If all counters in A >= B and at least one > → A happens after B
// Here: A=2 >= 1, but B=0 < 1 → concurrent (conflict!)
```

## Quorum System Internals

### Configuration
```java
public class QuorumConfig {
    // N = total replicas, W = write quorum, R = read quorum

    // For strong consistency: W + R > N
    // Example: N=3, W=2, R=2  (tolerates 1 failure)

    // For write-heavy: W=1, R=N (fast writes)
    // For read-heavy: W=N, R=1 (fast reads)

    // For tail latency: W=R=(N+1)/2 (balanced)
}
```

## Single Master Replication Internals

### Write Path
```
1. Client sends write to master
2. Master acquires write lock
3. Master executes write locally
4. Master sends to replicas (sync or async)
5. Master waits for confirmation (configurable)
6. Master releases lock
7. Master sends ACK to client
```

### Read Path (with replica)
```
1. Client sends read (routed by @Transactional(readOnly=true))
2. Replica executes read
3. Replica returns result
  Risk: Stale read if replica hasn't applied latest write
```
