# Distributed Consensus: Code Deep Dive

## Complete Raft Implementation (Single Node)

```java
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class RaftNode {
    // Persistent state
    private int currentTerm;
    private String votedFor;
    private final List<LogEntry> log;
    
    // Volatile state
    private int commitIndex;
    private int lastApplied;
    private State state;
    private final String nodeId;
    private final List<RaftNode> peers;
    private final ScheduledExecutorService scheduler;
    private final Random random = new Random();
    
    private volatile long lastHeartbeat;
    private ScheduledFuture<?> electionTimer;
    
    enum State { FOLLOWER, CANDIDATE, LEADER }
    
    public RaftNode(String nodeId, List<RaftNode> peers) {
        this.nodeId = nodeId;
        this.peers = peers;
        this.log = new CopyOnWriteArrayList<>();
        this.currentTerm = 0;
        this.votedFor = null;
        this.commitIndex = 0;
        this.lastApplied = 0;
        this.state = State.FOLLOWER;
        this.scheduler = Executors.newScheduledThreadPool(2);
        resetElectionTimer();
    }
    
    private void resetElectionTimer() {
        if (electionTimer != null) electionTimer.cancel(false);
        long timeout = 150 + random.nextInt(150); // 150-300ms
        electionTimer = scheduler.schedule(this::startElection, 
            timeout, TimeUnit.MILLISECONDS);
    }
    
    public synchronized void startElection() {
        if (state == State.LEADER) return;
        
        state = State.CANDIDATE;
        currentTerm++;
        votedFor = nodeId;
        lastHeartbeat = System.currentTimeMillis();
        
        int votes = 1; // Vote for self
        for (RaftNode peer : peers) {
            if (peer.requestVote(currentTerm, nodeId, 
                log.size() - 1, 
                log.isEmpty() ? 0 : log.get(log.size()-1).term)) {
                votes++;
            }
        }
        
        if (votes > peers.size() / 2 && state == State.CANDIDATE) {
            state = State.LEADER;
            startHeartbeat();
        } else {
            resetElectionTimer();
        }
    }
    
    private void startHeartbeat() {
        scheduler.scheduleAtFixedRate(() -> {
            if (state == State.LEADER) {
                for (RaftNode peer : peers) {
                    peer.appendEntries(currentTerm, nodeId, 
                        log.size() - 1, 
                        log.isEmpty() ? 0 : log.get(log.size()-1).term,
                        null, commitIndex);
                }
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }
    
    public synchronized boolean requestVote(int term, String candidateId,
            int lastLogIndex, int lastLogTerm) {
        if (term > currentTerm) {
            currentTerm = term;
            state = State.FOLLOWER;
            votedFor = null;
        }
        
        if (term == currentTerm && (votedFor == null || votedFor.equals(candidateId))) {
            // Check candidate's log is at least as up-to-date
            int myLastIndex = log.size() - 1;
            int myLastTerm = log.isEmpty() ? 0 : log.get(myLastIndex).term;
            
            if (lastLogTerm > myLastTerm || 
                (lastLogTerm == myLastTerm && lastLogIndex >= myLastIndex)) {
                votedFor = candidateId;
                lastHeartbeat = System.currentTimeMillis();
                resetElectionTimer();
                return true;
            }
        }
        return false;
    }
    
    public synchronized boolean appendEntries(int term, String leaderId,
            int prevLogIndex, int prevLogTerm, LogEntry entry, int leaderCommit) {
        if (term < currentTerm) return false;
        
        currentTerm = term;
        state = State.FOLLOWER;
        lastHeartbeat = System.currentTimeMillis();
        resetElectionTimer();
        
        // Check log matching
        if (prevLogIndex >= 0) {
            if (prevLogIndex >= log.size()) return false;
            if (log.get(prevLogIndex).term != prevLogTerm) return false;
        }
        
        if (entry != null) {
            // Append new entry
            log.add(entry);
        }
        
        // Update commit index
        if (leaderCommit > commitIndex) {
            commitIndex = Math.min(leaderCommit, log.size() - 1);
        }
        
        return true;
    }
    
    public void clientRequest(byte[] command) {
        if (state != State.LEADER) {
            throw new NotLeaderException();
        }
        LogEntry entry = new LogEntry(currentTerm, log.size(), command);
        log.add(entry);
        
        // Replicate to peers
        int acks = 1;
        for (RaftNode peer : peers) {
            if (peer.appendEntries(currentTerm, nodeId,
                log.size() - 2,
                log.size() >= 2 ? log.get(log.size()-2).term : 0,
                entry, commitIndex)) {
                acks++;
            }
        }
        
        if (acks > peers.size() / 2) {
            commitIndex = entry.index;
            applyToStateMachine(entry.command);
        }
    }
    
    static class LogEntry {
        final int term;
        final int index;
        final byte[] command;
        
        LogEntry(int term, int index, byte[] command) {
            this.term = term;
            this.index = index;
            this.command = command;
        }
    }
}
```
