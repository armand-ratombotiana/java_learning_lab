# How CAP Theorem Works

## Fundamental Mechanism

### 1. Normal Operation
In a single-node system, all three properties are achievable because there's no network.

### 2. During Partition
When a network split occurs:
- Nodes in different partitions cannot communicate
- Updates in one partition cannot propagate to others

### 3. The Choice
The system must choose:
- **Wait** for partition to heal (sacrifice Availability) → CP
- **Proceed** with potentially stale data (sacrifice Consistency) → AP

## Java Example: CP vs AP Decision

```java
// CP System - Block until consistent
public class CPSystem {
    private final List<Node> nodes;
    
    public Response read(String key) {
        int responses = 0;
        Response latest = null;
        for (Node node : nodes) {
            try {
                Response r = node.read(key);
                if (r.version > latest.version) latest = r;
                responses++;
            } catch (TimeoutException e) {
                // Fail if any node unreachable
                throw new SystemUnavailableException();
            }
        }
        int quorum = nodes.size() / 2 + 1;
        if (responses < quorum) throw new SystemUnavailableException();
        return latest;
    }
}

// AP System - Return best available
public class APSystem {
    public Response read(String key) {
        Response best = null;
        for (Node node : nodes) {
            try {
                Response r = node.read(key);
                if (best == null || r.timestamp > best.timestamp) {
                    best = r;
                }
            } catch (TimeoutException e) {
                // Skip failed nodes, respond with what we have
                continue;
            }
        }
        return best; // May be stale
    }
}
```
