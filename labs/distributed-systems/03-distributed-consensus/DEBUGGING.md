# Debugging Consensus Issues

## Common Problems

### Leader Election Failure
- Check election timeout values
- Verify network connectivity between nodes
- Ensure majority of nodes are alive

### Log Divergence
```java
public class RaftDebugger {
    public static void checkLogConsistency(List<RaftNode> nodes) {
        Set<Integer> commitIndices = new HashSet<>();
        for (RaftNode node : nodes) {
            commitIndices.add(node.getCommitIndex());
        }
        if (commitIndices.size() > 1) {
            System.err.println("Inconsistent commit indices: " + commitIndices);
        }
    }
}
```

### Split-Brain Detection
```java
public static boolean isSplitBrain(List<RaftNode> nodes) {
    long leaderCount = nodes.stream()
        .filter(n -> n.getState() == RaftNode.State.LEADER)
        .count();
    return leaderCount > 1;
}
```
