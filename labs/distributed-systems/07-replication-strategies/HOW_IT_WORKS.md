# How Replication Works

## Single-Leader Replication

```java
public class LeaderReplication {
    private final Node leader;
    private final List<Node> followers;
    
    public void write(String key, Object value) {
        // Write to leader
        leader.write(key, value);
        
        // Replicate to followers
        for (Node follower : followers) {
            try {
                follower.replicate(key, value);
            } catch (Exception e) {
                // Handle replication failure
                log.error("Replication to " + follower.id() + " failed", e);
            }
        }
    }
    
    public Object read(String key, boolean consistent) {
        if (consistent) {
            return leader.read(key); // Strong consistency
        }
        // Read from any (may be stale)
        Node target = followers.get(ThreadLocalRandom.current().nextInt(followers.size()));
        return target.read(key);
    }
}
```

## Quorum-Based Replication

```java
public class QuorumReplication {
    private final List<Node> nodes;
    private final int readQuorum;
    private final int writeQuorum;
    
    public boolean write(String key, Object value, long version) {
        int acks = 0;
        for (Node node : nodes) {
            try {
                if (node.write(key, value, version)) acks++;
            } catch (Exception e) { /* ignore */ }
        }
        return acks >= writeQuorum;
    }
    
    public ReadResult read(String key) {
        ValueVersion best = null;
        int acks = 0;
        for (Node node : nodes) {
            try {
                ReadResult result = node.read(key);
                acks++;
                if (best == null || result.version > best.version) {
                    best = new ValueVersion(result.value, result.version);
                }
            } catch (Exception e) { /* ignore */ }
        }
        if (acks < readQuorum) throw new ReadException("Read quorum not met");
        return best;
    }
}
```
