# CAP Theorem: Internals

## Formal Proof Sketch

### Assumptions
- System has at least two nodes
- Nodes can communicate only via messages
- Network may drop or delay messages arbitrarily

### Proof by Contradiction
1. Assume a system provides C, A, and P simultaneously
2. Create partition: nodes {N1, N2} split from {N3, N4}
3. Write to N1 → N1 updates, partition prevents propagation
4. Read from N4 → no knowledge of write on N1
5. Can either: respond with stale data (violates C) or fail to respond (violates A)
6. Therefore, all three cannot coexist

## Partition Detection
```java
public class PartitionDetector {
    private final HeartbeatSender sender;
    private final HeartbeatReceiver receiver;
    private final long timeout;
    
    public boolean isPartitioned() {
        long lastHeartbeat = receiver.getLastHeartbeatTime();
        return (System.currentTimeMillis() - lastHeartbeat) > timeout;
    }
}
```
