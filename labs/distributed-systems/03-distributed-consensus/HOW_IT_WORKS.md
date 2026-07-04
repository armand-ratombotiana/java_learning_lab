# How Distributed Consensus Works

## Raft Consensus Algorithm

### States
- **Leader**: Handles client requests, replicates log
- **Candidate**: Contends for leadership
- **Follower**: Passively replicates leader's log

### Leader Election
```java
public class RaftNode {
    enum State { FOLLOWER, CANDIDATE, LEADER }
    private State state = State.FOLLOWER;
    private int currentTerm = 0;
    private String votedFor = null;
    private int votesReceived = 0;
    
    public void startElection() {
        state = State.CANDIDATE;
        currentTerm++;
        votedFor = nodeId;
        votesReceived = 1;
        
        // Request votes from all other nodes
        for (RaftNode peer : peers) {
            peer.requestVote(currentTerm, nodeId, log.getLastIndex(), log.getLastTerm());
        }
    }
    
    public boolean requestVote(int term, String candidateId, 
                               int lastLogIndex, int lastLogTerm) {
        if (term > currentTerm) {
            currentTerm = term;
            state = State.FOLLOWER;
            votedFor = null;
        }
        
        // Grant vote if candidate's log is at least as up-to-date
        if (term == currentTerm && votedFor == null 
            && lastLogTerm >= log.getLastTerm()
            && lastLogIndex >= log.getLastIndex()) {
            votedFor = candidateId;
            return true;
        }
        return false;
    }
}
```

### Log Replication
```java
public class LogEntry {
    final int term;
    final int index;
    final byte[] command;
}

public class LogReplicator {
    public void replicate(LogEntry entry) {
        int successes = 0;
        for (RaftNode peer : peers) {
            if (peer.appendEntries(entry)) {
                successes++;
            }
        }
        // Commit when majority acknowledges
        if (successes >= majoritySize) {
            commitIndex = entry.index;
            applyToStateMachine(entry.command);
        }
    }
}
```
