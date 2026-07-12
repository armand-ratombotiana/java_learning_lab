# Raft Algorithm Code Deep Dive

This lab provides a pure Java simulation of a Raft node's state machine, focusing specifically on the Randomized Election Timeout and state transitions.

## 💻 Pure Java Implementation

```java file="labs/system-design/08-consensus/raft-algorithm/SOLUTION/RaftNodeSim.java"
package systemdesign.consensus.raft;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simulation of a single Raft Node's state machine and election timeout.
 */
public class RaftNodeSim {

    enum State { FOLLOWER, CANDIDATE, LEADER }

    private final String nodeId;
    private final int totalNodesInCluster;
    
    private State currentState = State.FOLLOWER;
    private int currentTerm = 0;
    
    // The randomized election timeout
    private final Random random = new Random();
    private long lastHeartbeatTime = System.currentTimeMillis();
    private int electionTimeoutMs;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public RaftNodeSim(String nodeId, int totalNodesInCluster) {
        this.nodeId = nodeId;
        this.totalNodesInCluster = totalNodesInCluster;
        resetElectionTimeout();
        
        // Start the background thread that monitors the timeout
        startTimeoutMonitor();
    }

    /**
     * Called whenever a valid Heartbeat (AppendEntries) is received from the Leader.
     */
    public void receiveHeartbeat(int leaderTerm) {
        if (leaderTerm >= currentTerm) {
            currentTerm = leaderTerm;
            if (currentState != State.FOLLOWER) {
                System.out.println(nodeId + " stepping down to FOLLOWER.");
                currentState = State.FOLLOWER;
            }
            lastHeartbeatTime = System.currentTimeMillis();
            // System.out.println(nodeId + " received heartbeat. Timer reset.");
        }
    }

    /**
     * Randomizes the timeout between 150ms and 300ms.
     */
    private void resetElectionTimeout() {
        this.electionTimeoutMs = 150 + random.nextInt(150);
    }

    /**
     * Continuously checks if the election timeout has elapsed.
     */
    private void startTimeoutMonitor() {
        scheduler.scheduleAtFixedRate(() -> {
            if (currentState == State.LEADER) return; // Leaders don't have election timeouts

            long timeSinceLastHeartbeat = System.currentTimeMillis() - lastHeartbeatTime;
            
            if (timeSinceLastHeartbeat > electionTimeoutMs) {
                startElection();
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
    }

    /**
     * Transitions to CANDIDATE and requests votes.
     */
    private void startElection() {
        currentState = State.CANDIDATE;
        currentTerm++;
        System.out.println("\n[ELECTION] " + nodeId + " timed out! Starting election for Term " + currentTerm);
        
        // 1. Vote for self
        AtomicInteger votesReceived = new AtomicInteger(1);
        
        // 2. Reset timer (in case this election results in a split vote)
        lastHeartbeatTime = System.currentTimeMillis();
        resetElectionTimeout();
        
        // 3. Request votes from others (Simulated)
        simulateRequestVotes(votesReceived);
        
        // 4. Check if we won a majority
        int majority = (totalNodesInCluster / 2) + 1;
        if (votesReceived.get() >= majority) {
            becomeLeader();
        }
    }

    private void simulateRequestVotes(AtomicInteger votes) {
        System.out.println(nodeId + " requesting votes...");
        // Simulate network calls to other nodes. 
        // In a real system, nodes only vote YES if the Candidate's log is at least as up-to-date as theirs.
        
        // Simulate receiving votes from the rest of the cluster
        for (int i = 0; i < totalNodesInCluster - 1; i++) {
            // 80% chance they vote yes in this simulation
            if (random.nextDouble() > 0.2) { 
                votes.incrementAndGet();
            }
        }
    }

    private void becomeLeader() {
        currentState = State.LEADER;
        System.out.println("👑 [LEADER] " + nodeId + " won the election! Now acting as Leader for Term " + currentTerm);
        
        // Immediately start sending heartbeats to assert authority
        startSendingHeartbeats();
    }

    private void startSendingHeartbeats() {
        scheduler.scheduleAtFixedRate(() -> {
            if (currentState != State.LEADER) throw new RuntimeException("Should not happen");
            // System.out.println(nodeId + " sending Heartbeats...");
            // Simulate sending to network...
        }, 0, 50, TimeUnit.MILLISECONDS); // Heartbeats must be faster than the minimum election timeout (150ms)
    }

    public static void main(String[] args) throws InterruptedException {
        // Simulate a 5-node cluster
        RaftNodeSim nodeA = new RaftNodeSim("Node-A", 5);
        
        // We simulate the cluster running smoothly. No heartbeats are being sent to Node A, 
        // so its timer will inevitably run out and it will start an election.
        
        Thread.sleep(1000); // Let the simulation run for 1 second
        
        // Once Node A is leader, simulate a network partition where a new leader with a higher term emerges
        System.out.println("\n[NETWORK] Simulating partition recovery. A new Leader (Term 5) contacts " + nodeA.nodeId);
        nodeA.receiveHeartbeat(5);
        
        Thread.sleep(500);
        System.exit(0);
    }
}
```

## 🔍 Key Takeaways
1. **The Randomized Timer**: Look at `resetElectionTimeout()`. It generates a random integer between 150 and 300. This tiny detail is what prevents infinite loops of split votes. Without this `random.nextInt()`, Raft would fail to elect a leader if multiple nodes crashed simultaneously.
2. **Stepping Down**: Look at `receiveHeartbeat()`. Even if `Node-A` is currently the undisputed `LEADER`, if it receives a heartbeat containing a `leaderTerm` higher than its own, it immediately steps down to `FOLLOWER`. This handles network partitions gracefully (e.g., the old leader was partitioned away, the rest of the cluster elected a new leader, and then the partition healed).