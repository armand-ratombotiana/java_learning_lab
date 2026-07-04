# Distributed Consensus: Step by Step

## Building Raft from Scratch

### Step 1: Server skeleton
```java
public class ConsensusServer {
    private int port;
    private List<String> peers;
    
    public void start() { /* Initialize and listen */ }
}
```

### Step 2: Add log
```java
class Log {
    List<Entry> entries = new ArrayList<>();
    int commitIndex = 0;
    int lastApplied = 0;
}
```

### Step 3: Implement election timer
```java
class ElectionTimer extends TimerTask {
    public void run() {
        if (noHeartbeatReceived()) {
            becomeCandidate();
        }
    }
}
```

### Step 4: Implement vote request/response
```java
class VoteRequest {
    int term;
    String candidateId;
    int lastLogIndex;
    int lastLogTerm;
}
```

### Step 5: Implement log replication
Follower receives AppendEntries → checks log match → appends → responds

### Step 6: Implement commit
Leader tracks matchIndex for each follower → commits when majority replicated
