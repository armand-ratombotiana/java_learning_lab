# Mini Project: Thread-Safe Immutable Configuration

## Objective
Build a complex, deeply immutable configuration object. You will demonstrate the Builder pattern for initial construction, defensive deep copying of collections, and "Wither" methods for creating updated copies. Finally, you will prove its thread safety.

## Prerequisites
*   Java 17+

## Step 1: The Mutable Element
Create a mutable element to prove that our defensive copying works.

```java
public class ServerNode {
    private String ipAddress;

    public ServerNode(String ipAddress) { this.ipAddress = ipAddress; }
    
    // Copy Constructor for Deep Copying
    public ServerNode(ServerNode other) { this.ipAddress = other.ipAddress; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; } // MUTABLE!
    
    @Override public String toString() { return ipAddress; }
}
```

## Step 2: The Immutable Configuration Class
Implement the Builder, deep defensive copying, and a Wither method.

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ClusterConfig {
    private final String clusterName;
    private final int maxRetries;
    private final List<ServerNode> nodes; // Contains mutable objects!

    // Private constructor forces use of the Builder
    private ClusterConfig(Builder builder) {
        this.clusterName = builder.clusterName;
        this.maxRetries = builder.maxRetries;
        
        // DEEP COPY: We must copy the list AND the mutable objects inside it
        List<ServerNode> copyList = new ArrayList<>();
        for (ServerNode node : builder.nodes) {
            copyList.add(new ServerNode(node)); // Using the copy constructor
        }
        // Wrap in unmodifiable view so the structure cannot be changed
        this.nodes = Collections.unmodifiableList(copyList);
    }

    // Getters
    public String getClusterName() { return clusterName; }
    public int getMaxRetries() { return maxRetries; }
    
    public List<ServerNode> getNodes() { 
        // Because we deep copied on the way in, and wrapped in unmodifiableList,
        // returning the reference is safe structurally.
        // However, to be perfectly safe, we should deep copy on the way out too,
        // or return a list of immutable DTOs. For this exercise, we will deep copy on the way out.
        List<ServerNode> copyList = new ArrayList<>();
        for (ServerNode node : this.nodes) {
            copyList.add(new ServerNode(node));
        }
        return Collections.unmodifiableList(copyList);
    }

    // Wither Method
    public ClusterConfig withMaxRetries(int newRetries) {
        if (this.maxRetries == newRetries) return this; // Optimization
        
        // Use the builder to easily construct the new instance
        return new Builder()
                .clusterName(this.clusterName)
                .maxRetries(newRetries)
                .nodes(this.nodes) // The builder will deep copy this again
                .build();
    }

    // The Builder
    public static class Builder {
        private String clusterName = "Default";
        private int maxRetries = 3;
        private List<ServerNode> nodes = new ArrayList<>();

        public Builder clusterName(String name) { this.clusterName = name; return this; }
        public Builder maxRetries(int retries) { this.maxRetries = retries; return this; }
        public Builder nodes(List<ServerNode> nodes) { this.nodes = nodes; return this; }
        public Builder addNode(ServerNode node) { this.nodes.add(node); return this; }

        public ClusterConfig build() { return new ClusterConfig(this); }
    }
    
    @Override
    public String toString() {
        return "ClusterConfig{name='" + clusterName + "', retries=" + maxRetries + ", nodes=" + nodes + "}";
    }
}
```

## Step 3: Test Security and Wither Mechanics
```java
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- 1. Building the Config ---");
        ServerNode node1 = new ServerNode("192.168.1.1");
        
        ClusterConfig config1 = new ClusterConfig.Builder()
                .clusterName("ProdCluster")
                .maxRetries(5)
                .addNode(node1)
                .build();
                
        System.out.println("Config 1: " + config1);

        System.out.println("\n--- 2. Testing Deep Encapsulation ---");
        // Attack 1: Try to mutate the original object passed to the builder
        node1.setIpAddress("HACKED-IP");
        System.out.println("Config 1 after Attack 1: " + config1); // Should still be 192.168.1.1

        // Attack 2: Try to mutate the object returned by the getter
        ServerNode retrievedNode = config1.getNodes().get(0);
        retrievedNode.setIpAddress("HACKED-IP-2");
        System.out.println("Config 1 after Attack 2: " + config1); // Should still be 192.168.1.1

        System.out.println("\n--- 3. Testing Wither Methods ---");
        // Create a new config based on the old one, but with 10 retries
        ClusterConfig config2 = config1.withMaxRetries(10);
        
        System.out.println("Config 1 (Original) : " + config1);
        System.out.println("Config 2 (New)      : " + config2);
    }
}
```

## Expected Output
Notice how the original configuration remains completely isolated from external mutations, and the Wither method cleanly generates a new, independent state.
```text
--- 1. Building the Config ---
Config 1: ClusterConfig{name='ProdCluster', retries=5, nodes=[192.168.1.1]}

--- 2. Testing Deep Encapsulation ---
Config 1 after Attack 1: ClusterConfig{name='ProdCluster', retries=5, nodes=[192.168.1.1]}
Config 1 after Attack 2: ClusterConfig{name='ProdCluster', retries=5, nodes=[192.168.1.1]}

--- 3. Testing Wither Methods ---
Config 1 (Original) : ClusterConfig{name='ProdCluster', retries=5, nodes=[192.168.1.1]}
Config 2 (New)      : ClusterConfig{name='ProdCluster', retries=10, nodes=[192.168.1.1]}
```