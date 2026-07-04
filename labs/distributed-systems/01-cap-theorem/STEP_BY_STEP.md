# CAP Theorem: Step by Step

## Simulating CAP Tradeoffs

### Step 1: Set up a 3-node cluster
```java
// Create nodes
Node n1 = new Node("node1", "192.168.1.10");
Node n2 = new Node("node2", "192.168.1.11");
Node n3 = new Node("node3", "192.168.1.12");
```

### Step 2: Define quorum size
```java
int totalNodes = 3;
int quorumSize = totalNodes / 2 + 1; // = 2
```

### Step 3: Normal operation
All three nodes are connected. Reads and writes succeed with quorum.

### Step 4: Simulate partition
```java
// Cut network between n1 and {n2, n3}
n1.disconnect();
```

### Step 5: CP behavior
Write to n1 → only n1 acknowledges → 1 < quorum (2) → write fails
Read from n2 → n2 responds but may miss writes from n1 partition

### Step 6: AP behavior
Write to n1 → n1 accepts locally, replicates when partition heals
Read from n2 → returns whatever n2 has (may be stale)
