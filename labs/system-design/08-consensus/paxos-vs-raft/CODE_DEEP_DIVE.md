# Paxos Simulation Code Deep Dive

This lab provides a pure Java simulation of a single Paxos "Prepare" phase to demonstrate how nodes make promises to each other.

## 💻 Pure Java Implementation

```java file="labs/system-design/08-consensus/paxos-vs-raft/SOLUTION/PaxosPrepareSim.java"
package systemdesign.consensus.paxos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A simulation of the Paxos 'Prepare' phase.
 */
public class PaxosPrepareSim {

    static class Acceptor {
        private int promisedId = -1;
        private final String name;

        Acceptor(String name) { this.name = name; }

        public synchronized Optional<Integer> handlePrepare(int proposalId) {
            if (proposalId > promisedId) {
                System.out.println(name + ": Promised not to accept anything less than " + proposalId);
                promisedId = proposalId;
                return Optional.of(promisedId);
            }
            System.out.println(name + ": REJECTED " + proposalId + " (Already promised " + promisedId + ")");
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
        List<Acceptor> cluster = new ArrayList<>();
        cluster.add(new Acceptor("Acceptor-1"));
        cluster.add(new Acceptor("Acceptor-2"));
        cluster.add(new Acceptor("Acceptor-3"));

        int majority = (cluster.size() / 2) + 1;

        // Proposer 1 tries with ID 10
        System.out.println("--- Proposer 1 (ID: 10) ---");
        long votes1 = cluster.stream().map(a -> a.handlePrepare(10)).filter(Optional::isPresent).count();
        System.out.println("Votes: " + votes1 + (votes1 >= majority ? " (MAJORITY WON)" : " (FAILED)"));

        // Proposer 2 tries with ID 5 (Late and smaller)
        System.out.println("\n--- Proposer 2 (ID: 5) ---");
        long votes2 = cluster.stream().map(a -> a.handlePrepare(5)).filter(Optional::isPresent).count();
        System.out.println("Votes: " + votes2 + (votes2 >= majority ? " (MAJORITY WON)" : " (FAILED)"));

        // Proposer 3 tries with ID 20 (Higher)
        System.out.println("\n--- Proposer 3 (ID: 20) ---");
        long votes3 = cluster.stream().map(a -> a.handlePrepare(20)).filter(Optional::isPresent).count();
        System.out.println("Votes: " + votes3 + (votes3 >= majority ? " (MAJORITY WON)" : " (FAILED)"));
    }
}
```

## 🔍 Key Takeaways
1. **The Promise**: Look at `handlePrepare`. The Acceptor doesn't store a value yet. It just makes a promise: "I won't listen to anyone with a lower ID than yours."
2. **Majority Rule**: Paxos only works if a majority of nodes respond. This ensures that even if some nodes crash, the cluster state remains consistent.
3. **Numbering**: The `proposalId` must be unique and strictly increasing. In real systems, this is often achieved by combining a local counter with the node's unique ID (e.g., `counter.nodeId`).