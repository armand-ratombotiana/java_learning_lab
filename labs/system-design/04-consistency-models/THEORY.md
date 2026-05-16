# Consistency Models - THEORY

## Overview

Understanding consistency models is crucial for designing distributed systems. This lab covers CAP theorem, ACID vs BASE, and practical implementation strategies.

## 1. CAP Theorem

### The Theorem
- **C**onsistency: All nodes see the same data at the same time
- **A**vailability: Every request receives a response
- **P**artition tolerance: System operates despite network failures

### The Catch
You can only guarantee two of the three properties. In practice, partition tolerance is mandatory, so you're choosing between consistency and availability.

### CAP Trade-offs

| System | Consistency | Availability |
|--------|-------------|--------------|
| CP (Consistent + Partition-tolerant) | Strong | Partial during partition |
| AP (Available + Partition-tolerant) | Eventual | Always |

### Examples
- **CP**: MongoDB (leader-based), HBase, Redis Cluster
- **AP**: Cassandra, DynamoDB, CouchDB

## 2. Consistency Levels

### Strong Consistency
- All reads see the most recent write
- Implemented via consensus (Paxos, Raft)
- Higher latency, lower availability during partitions

```java
// Strong consistency example
public void transfer(Account from, Account to, BigDecimal amount) {
    // Lock both accounts
    lock(from);
    lock(to);
    try {
        from.withdraw(amount);
        to.deposit(amount);
    } finally {
        unlock(from);
        unlock(to);
    }
}
```

### Sequential Consistency
- All nodes see operations in the same order
- Weaker than strong, stronger than causal

### Eventual Consistency
- Updates propagate asynchronously
- Conflicts may occur and need resolution
- Examples: DNS, CDN, DynamoDB

```java
// Eventual consistency example
public void write(String key, String value) {
    // Write to local node first (fast)
    localStorage.put(key, value);
    // Async replication to other nodes
    asyncReplicate(key, value);
}
```

### Causal Consistency
- Preserves cause-and-effect relationships
- Operations that are causally related are seen by all nodes in order
- Operations that are independent may be seen in different order

## 3. ACID vs BASE

### ACID (Relational Databases)
- **Atomicity**: All or nothing
- **Consistency**: Valid state transitions
- **Isolation**: Concurrent transactions don't interfere
- **Durability**: Committed data survives failures

### BASE (NoSQL Systems)
- **Basically Available**: System guarantees availability
- **Soft State**: State may change over time
- **Eventually Consistent**: System will become consistent over time

### Comparison

| Aspect | ACID | BASE |
|--------|------|------|
| Consistency | Immediate | Delayed |
| Availability | Lower | Higher |
| Performance | Lower | Higher |
| Complexity | Higher | Lower |
| Use Cases | Financial, critical data | Scalable, flexible |

## 4. Distributed Transactions

### Two-Phase Commit (2PC)

#### Phase 1: Prepare
1. Coordinator asks all participants to prepare
2. Participants lock resources and vote yes/no

#### Phase 2: Commit/Rollback
1. If all vote yes, coordinator sends commit
2. If any votes no, coordinator sends rollback

```java
public class TwoPhaseCommit {
    public void execute(Transaction tx) {
        // Phase 1: Prepare
        List<Participant> participants = tx.getParticipants();
        boolean allReady = true;
        for (Participant p : participants) {
            if (!p.prepare()) {
                allReady = false;
                break;
            }
        }
        
        // Phase 2: Commit or Rollback
        if (allReady) {
            for (Participant p : participants) {
                p.commit();
            }
        } else {
            for (Participant p : participants) {
                p.rollback();
            }
        }
    }
}
```

### Saga Pattern (Eventual Consistency)

Instead of locking, use compensating transactions:

```java
public class OrderSaga {
    public void placeOrder(Order order) {
        try {
            reserveInventory(order);     // Step 1
            processPayment(order);       // Step 2
            confirmOrder(order);         // Step 3
        } catch (Exception e) {
            // Compensating transactions
            cancelReservation(order);    // Undo step 1
            refundPayment(order);        // Undo step 2
        }
    }
}
```

## 5. Conflict Resolution

### Last-Writer-Wins (LWW)
Simple but may lose data:

```java
public class LWWResolver<T> {
    public T resolve(T local, T remote, long localTs, long remoteTs) {
        return remoteTs > localTs ? remote : local;
    }
}
```

### Vector Clocks
Track causality:

```java
public class VectorClock {
    private Map<String, Long> clock;
    
    public void increment(String node) {
        clock.merge(node, 1L, Long::sum);
    }
    
    public boolean happensBefore(VectorClock other) {
        for (String node : clock.keySet()) {
            if (clock.getOrDefault(node, 0L) > other.getOrDefault(node, 0L)) {
                return false;
            }
        }
        return true;
    }
}
```

### CRDT (Conflict-free Replicated Data Types)
Mathematically proven to converge:

```java
public class GCounter {
    private Map<String, Long> counts;
    
    public void increment(String node) {
        counts.merge(node, 1L, Long::sum);
    }
    
    public long value() {
        return counts.values().stream().mapToLong(Long::longValue).sum();
    }
}
```

## 6. Implementation Strategies

### Read Your Own Writes
```java
public class SessionConsistency {
    // Use sticky sessions
    // Or track client's last write timestamp
    private ThreadLocal<Long> lastWrite = new ThreadLocal<>();
    
    public void write(String key, String value) {
        localStorage.put(key, value);
        lastWrite.set(System.currentTimeMillis());
    }
    
    public String read(String key) {
        Long ts = lastWrite.get();
        if (ts != null) {
            // Ensure local read includes recent writes
            awaitPropagation(ts);
        }
        return localStorage.get(key);
    }
}
```

### Quorum-Based Systems

```java
public class QuorumRead {
    private int replicationFactor;
    
    public String read(String key) {
        // Read from R replicas
        List<String> values = readFromReplicas(key, R);
        // Return majority (or most recent)
        return majorityVote(values);
    }
    
    public void write(String key, String value) {
        // Write to W replicas
        writeToReplicas(key, value, W);
    }
    
    // For strong consistency: W + R > N
    private boolean isConsistent() {
        return W + R > replicationFactor;
    }
}
```

## Summary

1. **CAP Theorem**: Choose CP or AP based on requirements
2. **ACID vs BASE**: Match consistency model to data needs
3. **Transactions**: Use 2PC for strong consistency, Saga for eventual
4. **Conflicts**: Implement resolution strategies (LWW, vector clocks, CRDT)
5. **Implementation**: Use quorums, read-your-own-writes for practical consistency