# Consistency Models - EXERCISES

## Exercise 1: CAP Theorem Analysis

Analyze these systems and classify them as CP or AP:
1. DynamoDB with strongly consistent reads disabled
2. MongoDB with replica set primary
3. ZooKeeper ensemble
4. Cassandra with QUORUM consistency

**Answer**: 1-AP, 2-CP, 3-CP, 4-AP (with QUORUM it's tunable)

## Exercise 2: Implement Vector Clock

Create a VectorClock class that:
1. Increments on local events
2. Merges with other clocks
3. Determines happens-before relationship

```java
// Solution
public class VectorClock {
    private Map<String, Long> clock = new HashMap<>();
    
    public void increment(String nodeId) {
        clock.merge(nodeId, 1L, Long::sum);
    }
    
    public boolean happensBefore(VectorClock other) {
        boolean dominated = false;
        for (String node : union(clock.keySet(), other.clock.keySet())) {
            long thisVal = clock.getOrDefault(node, 0L);
            long otherVal = other.clock.getOrDefault(node, 0L);
            if (thisVal > otherVal) return false;
            if (thisVal < otherVal) dominated = true;
        }
        return dominated;
    }
}
```

## Exercise 3: Quorum Implementation

Implement a quorum-based read/write system with:
1. W + R > N for strong consistency
2. Configurable quorum sizes

## Exercise 4: Last-Writer-Wins Register

Create an LWWRegister that resolves conflicts by timestamp.

## Exercise 5: Two-Phase Commit

Implement a simple 2PC coordinator with:
1. Prepare phase
2. Commit/Rollback phase

## Exercise 6: Saga Compensation

Design a saga for e-commerce checkout with compensation for:
1. Reserve inventory
2. Process payment
3. Create order

## Exercise 7: Circuit Breaker

Implement a circuit breaker that:
1. Opens after N failures
2. Half-opens to test recovery
3. Closes on success

## Exercise 8: Read-Your-Own-Writes

Implement a client that ensures read-your-own-writes consistency.

---

## Solutions

### Exercise 3: Quorum Implementation

```java
public class QuorumStore<T> {
    private int N; // total replicas
    private int R; // read quorum
    private int W; // write quorum
    
    public QuorumStore(int N) {
        this.N = N;
        this.R = N / 2 + 1;
        this.W = N / 2 + 1;
    }
    
    public Optional<T> read(String key) {
        List<Versioned<T>> versions = readFromN(R);
        return majority(versions).map(Versioned::getValue);
    }
    
    public boolean write(String key, T value) {
        int success = writeToN(W, value);
        return success >= W;
    }
}
```

### Exercise 4: LWWRegister

```java
public class LWWRegister<T> {
    private volatile T value;
    private volatile long timestamp;
    
    public synchronized void set(T newValue) {
        this.value = newValue;
        this.timestamp = System.currentTimeMillis();
    }
    
    public synchronized void merge(LWWRegister<T> other) {
        if (other.timestamp > this.timestamp) {
            this.value = other.value;
            this.timestamp = other.timestamp;
        }
    }
}
```

### Exercise 5: Two-Phase Commit

```java
public class TwoPhaseCoordinator {
    public boolean execute(Transaction tx) {
        // Phase 1: Prepare
        boolean allPrepared = tx.getParticipants().stream()
            .allMatch(Participant::prepare);
        
        if (!allPrepared) {
            tx.getParticipants().forEach(Participant::rollback);
            return false;
        }
        
        // Phase 2: Commit
        tx.getParticipants().forEach(Participant::commit);
        return true;
    }
}
```

### Exercise 7: Circuit Breaker

```java
public class CircuitBreaker {
    private AtomicInteger failures = new AtomicInteger(0);
    private volatile State state = State.CLOSED;
    
    public <T> T execute(Supplier<T> op) {
        if (state == State.OPEN) {
            if (canReset()) state = State.HALF_OPEN;
            else throw new CircuitOpenException();
        }
        
        try {
            T result = op.get();
            reset();
            return result;
        } catch (Exception e) {
            recordFailure();
            throw e;
        }
    }
    
    private void recordFailure() {
        if (failures.incrementAndGet() >= 5) state = State.OPEN;
    }
    
    private void reset() {
        failures.set(0);
        state = State.CLOSED;
    }
}
```

### Exercise 8: Read-Your-Own-Writes

```java
public class SessionClient {
    private Map<String, Versioned<T>> localCache = new HashMap<>();
    private long lastWrite;
    
    public void write(String key, T value) {
        // Write through to all replicas
        quorum.write(key, value);
        localCache.put(key, new Versioned<>(value, System.currentTimeMillis()));
        lastWrite = System.currentTimeMillis();
    }
    
    public T read(String key) {
        // Check local cache for recent writes
        Versioned<T> cached = localCache.get(key);
        if (cached != null && cached.timestamp >= lastWrite) {
            return cached.value;
        }
        return quorum.read(key).orElse(null);
    }
}
```