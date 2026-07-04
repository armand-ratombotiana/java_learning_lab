# Consistency Models: Step by Step

## Building a Strongly Consistent Store

### Step 1: Define the store interface
```java
interface ConsistentStore<K, V> {
    void write(K key, V value);
    V read(K key);
}
```

### Step 2: Add versioning
```java
class VersionedValue<V> {
    final V value;
    final long version;
}
```

### Step 3: Implement leader-based replication
```java
class LeaderStore implements ConsistentStore {
    private final Node leader;
    private final List<Node> followers;
    
    public void write(K key, V value) {
        // Write to leader
        leader.write(key, value);
        // Replicate to all followers synchronously
        for (Node f : followers) {
            f.replicate(key, value);
        }
    }
}
```

### Step 4: Add quorum for fault tolerance
```java
class QuorumStore implements ConsistentStore {
    public void write(K key, V value) {
        int successes = 0;
        for (Node n : nodes) {
            if (n.write(key, value)) successes++;
        }
        if (successes < WRITE_QUORUM) throw new WriteException();
    }
}
```
