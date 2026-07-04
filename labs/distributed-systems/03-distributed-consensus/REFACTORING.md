# Refactoring Consensus Implementations

## From Single Leader to Consensus Group

### Before (Single Point of Failure):
```java
public class SingleLeaderService {
    private final Node leader; // Single point of failure
}
```

### After (Raft Group):
```java
public class RaftService {
    private final List<RaftNode> cluster;
    private volatile RaftNode leader;
    
    public void submit(Command cmd) {
        RaftNode leader = this.leader;
        if (leader == null) throw new NoLeaderException();
        leader.clientRequest(cmd.serialize());
    }
}
```

## From Paxos to Multi-Paxos
Single Paxos requires two round trips per value. Multi-Paxos uses stable leader for single round trip.
