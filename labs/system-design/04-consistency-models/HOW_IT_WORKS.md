# Consistency Models - HOW IT WORKS

## Strong Consistency

### Single-Master Replication
```
Write: Client → Master (persist + replicate to all replicas) → Ack
Read:  Client → Master or any replica that has confirmed replication
```

### Implementation
```java
// PostgreSQL synchronous replication
-- On primary:
synchronous_standby_names = 'replica1, replica2'

-- Wait for at least 1 replica to confirm
synchronous_commit = 'on'

-- In Java, using read-after-write:
public Order createAndRead(Order order) {
    // Write to master (waits for replica sync)
    orderRepository.save(order);

    // Read from master to guarantee consistency
    return orderRepository.findByIdMaster(order.getId());
}
```

## Quorum-Based Consistency

### Read/Write Quorums
```java
// N = 3 replicas, W = 2 (write quorum), R = 2 (read quorum)
// Satisfies W + R > N → strong consistency

public class QuorumStore {
    private List<Node> nodes;
    private int N = 3, W = 2, R = 2;

    public void write(String key, String value) {
        int acks = 0;
        for (Node node : nodes) {
            if (node.write(key, value)) acks++;
        }
        if (acks < W) throw new WriteException("Not enough acks");
    }

    public String read(String key) {
        Map<String, Integer> versions = new HashMap<>();
        for (Node node : nodes) {
            NodeValue nv = node.read(key);
            versions.merge(nv.value, 1, Integer::sum);
        }
        // Return value with highest version count
        return versions.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .get().getKey();
    }
}
```

## Eventual Consistency

### Asynchronous Replication
```java
// Master receives write, responds immediately, replicates async
// Temporary inconsistency window = replication lag

public void writeAsync(Order order) {
    master.save(order);                            // immediate
    kafkaTemplate.send("db-replication", order);   // async replication
}
```

### Conflict Resolution
```java
// Last-Writer-Wins (LWW) using timestamps
public void resolveConflict(String key, Value v1, Value v2) {
    Value resolved = v1.timestamp > v2.timestamp ? v1 : v2;
    save(key, resolved);
}
```

## Causal Consistency

### Tracking Causal Relationships
```java
// Using vector clocks to track causality
public class VectorClock {
    private Map<String, Integer> clock = new HashMap<>();

    public void increment(String node) {
        clock.merge(node, 1, Integer::sum);
    }

    public boolean happensBefore(VectorClock other) {
        return clock.entrySet().stream()
            .allMatch(e -> other.clock.getOrDefault(e.getKey(), 0) >= e.getValue());
    }
}
```

## Distributed Consensus (Raft)

### Leader Election
```java
// Raft nodes in Java (using Atomix library)
RaftServer server = RaftServer.builder(address)
    .withStateMachine(new OrderStateMachine())
    .build();

// Write through leader
RaftClient client = RaftClient.builder()
    .withMemberGroupProvider(
        new FixedMemberGroupProvider(members))
    .build();

CompletableFuture<byte[]> result = client.submit(
    new WriteCommand("order-123", orderBytes)
);
```
