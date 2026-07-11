# CAP Theorem Code Deep Dive

This lab provides a pure Java simulation of a distributed database with two nodes. It demonstrates how a network partition forces the system designer to choose between returning an error (CP) or returning stale data (AP).

## 💻 Pure Java Implementation

```java file="labs/system-design/04-databases/cap-theorem/SOLUTION/DistributedDatabaseSim.java"
package systemdesign.databases.cap;

import java.util.HashMap;
import java.util.Map;

/**
 * A simulation of a 2-node distributed database to demonstrate the CAP theorem.
 */
public class DistributedDatabaseSim {

    // Simulates the network link between the two nodes
    private boolean networkPartitionActive = false;

    // Node A
    private final Map<String, String> nodeA_Storage = new HashMap<>();
    
    // Node B (Replica)
    private final Map<String, String> nodeB_Storage = new HashMap<>();

    public DistributedDatabaseSim() {
        // Initial state
        nodeA_Storage.put("user_1", "Alice");
        nodeB_Storage.put("user_1", "Alice");
    }

    public void triggerNetworkPartition() {
        this.networkPartitionActive = true;
        System.out.println("\n[NETWORK] 🚨 Partition Occurred! Nodes can no longer communicate.");
    }

    public void healNetworkPartition() {
        this.networkPartitionActive = false;
        System.out.println("\n[NETWORK] ✅ Partition Healed! Nodes are communicating.");
    }

    // ==========================================
    // AP System Simulation (Cassandra-style)
    // ==========================================
    public void write_AP(String key, String value) {
        System.out.println("[AP System] Client writing '" + value + "' to Node A...");
        nodeA_Storage.put(key, value);
        
        if (networkPartitionActive) {
            System.out.println("[AP System] Write succeeded locally on Node A, but could not replicate to Node B.");
        } else {
            nodeB_Storage.put(key, value); // Replicate
            System.out.println("[AP System] Write replicated to Node B successfully.");
        }
    }

    public String read_AP(String key, boolean readFromNodeB) {
        String result = readFromNodeB ? nodeB_Storage.get(key) : nodeA_Storage.get(key);
        System.out.println("[AP System] Client reading from Node " + (readFromNodeB ? "B" : "A") + ": " + result);
        return result;
    }

    // ==========================================
    // CP System Simulation (MongoDB-style)
    // ==========================================
    public void write_CP(String key, String value) throws RuntimeException {
        System.out.println("[CP System] Client writing '" + value + "' to Node A...");
        
        if (networkPartitionActive) {
            System.err.println("[CP System] ❌ ERROR: Cannot reach Node B to guarantee consistency. Write rejected.");
            throw new RuntimeException("Consistency constraint failed due to network partition.");
        }
        
        // If no partition, write to both to ensure consistency
        nodeA_Storage.put(key, value);
        nodeB_Storage.put(key, value);
        System.out.println("[CP System] Write succeeded and replicated to Node B.");
    }

    public static void main(String[] args) {
        DistributedDatabaseSim db = new DistributedDatabaseSim();

        System.out.println("--- NORMAL OPERATION ---");
        db.write_AP("user_1", "Alice_Updated");
        db.read_AP("user_1", true); // Read from Node B

        db.triggerNetworkPartition();

        System.out.println("\n--- AP SYSTEM BEHAVIOR DURING PARTITION ---");
        // AP System accepts the write (Highly Available)
        db.write_AP("user_1", "Alice_Final"); 
        
        // AP System returns stale data from Node B (Sacrifices Consistency)
        db.read_AP("user_1", true); 

        System.out.println("\n--- CP SYSTEM BEHAVIOR DURING PARTITION ---");
        try {
            // CP System rejects the write (Sacrifices Availability to guarantee Consistency)
            db.write_CP("user_1", "Alice_Final");
        } catch (RuntimeException e) {
            System.out.println("Client received error: " + e.getMessage());
        }
    }
}
```

## 🔍 Key Takeaways
1. **The AP Choice**: Look at `write_AP`. When the partition is active, it writes to Node A and immediately returns success to the user. This is highly available. However, when the user subsequently reads from Node B, they get stale data. Consistency is broken.
2. **The CP Choice**: Look at `write_CP`. When the partition is active, it throws an exception and refuses to save the data. It guarantees that if a read ever occurs on Node B, it will match Node A. However, the system is now effectively down for writes. Availability is broken.