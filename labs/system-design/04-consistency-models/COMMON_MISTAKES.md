# Consistency Models - COMMON MISTAKES

## 1. Assuming Strong Consistency with Async Replication
```java
// WRONG: Configuring async replication, expecting strong consistency
datasource.synchronousReplication = false  // async
// User A writes, User B reads from replica — sees old data

// RIGHT: Use sync replication or read from master
datasource.synchronousReplication = true
```

## 2. Ignoring Clock Drift
```java
// WRONG: Using system time for conflict resolution
if (localTime > remoteTime) useLocalValue();

// RIGHT: Use vector clocks or hybrid logical clocks
VectorClock resolvedClock = VectorClock.merge(clock1, clock2);
```

## 3. Not Handling Concurrent Writes
Eventual consistency systems must handle write conflicts. Last-writer-wins loses data.

## 4. Breaking Causal Consistency
```java
// WRONG: Post reply shows before the post
api.createReply(postId, replyText);
api.getPost(postId);  // might not see the reply yet

// RIGHT: Track causal dependencies
api.createReply(postId, replyText).thenCompose(v ->
    api.getPost(postId, withCausalConsistency)
);
```

## 5. Not Setting Quorum Properly
W + R must be > N for strong consistency.
```java
// WRONG: N=3, W=1, R=1  (W+R=2 ≤ N=3 → weak)
quorum = new Quorum(N=3, W=1, R=1);

// RIGHT: N=3, W=2, R=2  (W+R=4 > N=3 → strong)
quorum = new Quorum(N=3, W=2, R=2);
```

## 6. Client-Side Caching without Invalidation
Cached data may be stale for hours. Implement TTL or push-based invalidation.

## 7. Split-Brain in Consensus Systems
Network partition causes two leaders. Use quorum-based election with majority requirement.

## 8. Ignoring Session Consistency
User's own writes must be visible to them immediately. Use session tokens to track last write timestamp.

## 9. Misunderstanding PACELC
CAP only applies during partitions. PACELC adds: Else (no partition), trade-off Latency vs Consistency.
