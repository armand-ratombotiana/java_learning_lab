# Distributed Systems Design Cheatsheet

> Comprehensive reference for distributed systems design patterns with Java code examples.

---

## Table of Contents
1. [Consensus Protocols](#consensus-protocols)
2. [Consistent Hashing](#consistent-hashing)
3. [Gossip Protocols](#gossip-protocols)
4. [CRDTs](#cr-dts)
5. [Distributed Transaction Patterns](#distributed-transaction-patterns)
6. [Replication Patterns](#replication-patterns)
7. [Partitioning Strategies](#partitioning-strategies)
8. [Distributed Caching Patterns](#distributed-caching-patterns)
9. [Distributed Messaging Patterns](#distributed-messaging-patterns)
10. [Failure Detection Patterns](#failure-detection-patterns)
11. [Leader Election Patterns](#leader-election-patterns)
12. [Distributed Locking Patterns](#distributed-locking-patterns)

---

## Consensus Protocols

### Paxos

**When to use**: When you need strong consistency across a distributed system where nodes can fail and messages can be delayed. Used in Google's Chubby lock service, Spanner, and many other systems.

**System context**: Acceptors (voters), Proposers (leaders), Learners (observers). Requires majority (quorum) to make progress. Tolerates N/2 - 1 failures.

```java
class PaxosRound {
    private final int proposerId;
    private int maxBallot = 0;
    private String acceptedValue = null;
    private int acceptedBallot = 0;

    public PaxosRound(int proposerId) {
        this.proposerId = proposerId;
    }

    // Phase 1a: Prepare
    public PrepareResponse prepare(int ballot) {
        if (ballot > maxBallot) {
            maxBallot = ballot;
            return new PrepareResponse(true, acceptedBallot, acceptedValue);
        }
        return new PrepareResponse(false, -1, null);
    }

    // Phase 2a: Accept
    public AcceptResponse accept(int ballot, String value) {
        if (ballot >= maxBallot) {
            maxBallot = ballot;
            acceptedBallot = ballot;
            acceptedValue = value;
            return new AcceptResponse(true);
        }
        return new AcceptResponse(false);
    }

    static class PrepareResponse {
        boolean ok; int lastAcceptedBallot; String lastAcceptedValue;
        PrepareResponse(boolean ok, int lab, String lav) {
            this.ok = ok; this.lastAcceptedBallot = lab; this.lastAcceptedValue = lav;
        }
    }

    static class AcceptResponse {
        boolean ok;
        AcceptResponse(boolean ok) { this.ok = ok; }
    }
}
```

**Tradeoffs**: 
- Liveness requires leader election (separate mechanism)
- Ballot numbers must be unique
- Blocking if proposers compete (livelock)
- Difficult to understand and implement correctly

### Raft

**When to use**: When you need a more understandable consensus protocol. Used in etcd, Consul, Kafka (KRaft), and many modern systems.

**System context**: Leader initiates all consensus; followers replicate logs. Leader election on failure. Log entries flow leader -> followers.

```java
class RaftNode {
    enum Role { FOLLOWER, CANDIDATE, LEADER }
    private Role role = Role.FOLLOWER;
    private int currentTerm = 0;
    private String votedFor = null;
    private final List<LogEntry> log = new ArrayList<>();
    private int commitIndex = 0;
    private int lastApplied = 0;

    static class LogEntry {
        int term; String command;
        LogEntry(int term, String command) {
            this.term = term; this.command = command;
        }
    }

    // RequestVote RPC (candidate -> peers)
    public RequestVoteResponse requestVote(int term, String candidateId,
                                            int lastLogIndex, int lastLogTerm) {
        if (term < currentTerm) {
            return new RequestVoteResponse(currentTerm, false);
        }
        if (term > currentTerm) {
            currentTerm = term;
            role = Role.FOLLOWER;
            votedFor = null;
        }
        if (votedFor == null || votedFor.equals(candidateId)) {
            if (lastLogTerm > getLastLogTerm() ||
                (lastLogTerm == getLastLogTerm() && lastLogIndex >= log.size() - 1)) {
                votedFor = candidateId;
                return new RequestVoteResponse(currentTerm, true);
            }
        }
        return new RequestVoteResponse(currentTerm, false);
    }

    // AppendEntries RPC (leader -> followers)
    public AppendEntriesResponse appendEntries(int term, String leaderId,
                                                int prevLogIndex, int prevLogTerm,
                                                List<LogEntry> entries, int leaderCommit) {
        if (term < currentTerm) {
            return new AppendEntriesResponse(currentTerm, false);
        }
        if (term > currentTerm) {
            currentTerm = term;
            role = Role.FOLLOWER;
            votedFor = null;
        }
        // Reset election timeout
        if (prevLogIndex >= 0) {
            if (prevLogIndex >= log.size() || log.get(prevLogIndex).term != prevLogTerm) {
                return new AppendEntriesResponse(currentTerm, false);
            }
        }
        // Handle log conflicts
        for (int i = 0; i < entries.size(); i++) {
            int idx = prevLogIndex + 1 + i;
            if (idx < log.size()) {
                if (log.get(idx).term != entries.get(i).term) {
                    log.subList(idx, log.size()).clear();
                    log.add(entries.get(i));
                }
            } else {
                log.add(entries.get(i));
            }
        }
        if (leaderCommit > commitIndex) {
            commitIndex = Math.min(leaderCommit, log.size() - 1);
        }
        return new AppendEntriesResponse(currentTerm, true);
    }

    private int getLastLogTerm() {
        if (log.isEmpty()) return 0;
        return log.get(log.size() - 1).term;
    }

    // Leader election
    public void startElection() {
        role = Role.CANDIDATE;
        currentTerm++;
        votedFor = null; // self
        // Send RequestVote to all peers
    }

    static class RequestVoteResponse {
        int term; boolean voteGranted;
        RequestVoteResponse(int term, boolean voteGranted) {
            this.term = term; this.voteGranted = voteGranted;
        }
    }

    static class AppendEntriesResponse {
        int term; boolean success;
        AppendEntriesResponse(int term, boolean success) {
            this.term = term; this.success = success;
        }
    }
}
```

**Tradeoffs**:
- Leader-centric: all traffic through leader can be bottleneck
- Election timeouts must be tuned carefully
- Log compaction (snapshotting) needed for long-running systems
- Read scalability requires separate mechanism

### Zab (ZooKeeper Atomic Broadcast)

**When to use**: For systems needing ordered, reliable broadcast. Used in Apache ZooKeeper. Similar to Raft but with different message ordering guarantees.

**System context**: Leader writes to all followers via atomic broadcast. Total order of messages. Crash recovery via epoch numbers.

```java
// Zab's total order broadcast abstraction
interface ZabProcessor {
    void deliver(Transaction txn); // delivered in total order
    void commit(Transaction txn);  // committed after quorum ack
}

class ZabTransaction {
    long zxid; // ZooKeeper transaction ID (epoch + counter)
    byte[] data;

    // ZXID structure: high 32 bits = epoch, low 32 bits = counter
    static long makeZxid(long epoch, long counter) {
        return (epoch << 32) | counter;
    }
}
```

**When to choose**: Paxos for when you need flexibility in roles and optimal multi-leader; Raft for understandability and single-leader systems; Zab for ZooKeeper environments.

---

## Consistent Hashing

**When to use**: For distributing data across nodes with minimal reshuffling when nodes join/leave. Used in Amazon DynamoDB, Discord, Akamai CDN.

**System context**: Each node assigned multiple points on a hash ring. Key lookup finds nearest clockwise node. Virtual nodes for load balancing.

```java
class ConsistentHashRing<T> {
    private final TreeMap<Integer, T> ring = new TreeMap<>();
    private final int virtualNodes;
    private final HashFunction hash;

    public ConsistentHashRing(int virtualNodes, HashFunction hash) {
        this.virtualNodes = virtualNodes;
        this.hash = hash;
    }

    public void addNode(T node) {
        for (int i = 0; i < virtualNodes; i++) {
            ring.put(hash.hash(node.toString() + i), node);
        }
    }

    public void removeNode(T node) {
        for (int i = 0; i < virtualNodes; i++) {
            ring.remove(hash.hash(node.toString() + i));
        }
    }

    public T get(String key) {
        if (ring.isEmpty()) return null;
        int hashVal = hash.hash(key);
        Map.Entry<Integer, T> entry = ring.ceilingEntry(hashVal);
        if (entry == null) {
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }

    public List<T> getN(String key, int n) {
        List<T> nodes = new ArrayList<>();
        if (ring.isEmpty()) return nodes;

        int hashVal = hash.hash(key);
        NavigableMap<Integer, T> tailMap = ring.tailMap(hashVal, false);

        for (Map.Entry<Integer, T> entry : tailMap.entrySet()) {
            if (nodes.size() >= n) break;
            if (!nodes.contains(entry.getValue())) {
                nodes.add(entry.getValue());
            }
        }

        if (nodes.size() < n) {
            for (Map.Entry<Integer, T> entry : ring.entrySet()) {
                if (nodes.size() >= n) break;
                if (!nodes.contains(entry.getValue())) {
                    nodes.add(entry.getValue());
                }
            }
        }
        return nodes;
    }

    interface HashFunction {
        int hash(String input);
    }
}
```

**Tradeoffs**:
- Virtual nodes create ~O(log N) redistribution
- Hot spots still possible with skewed data
- Need to handle node heterogeneity with weights
- Ring must be persisted across restarts

---

## Gossip Protocols

**When to use**: For decentralized information dissemination, failure detection, membership management. Used in DynamoDB, Cassandra, Redis Cluster.

**System context**: Each node periodically exchanges state with a random peer. Information spreads exponentially (O(log N) rounds).

```java
class GossipNode {
    private final String nodeId;
    private final Map<String, NodeState> clusterState = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final List<GossipNode> peers;

    static class NodeState {
        String id; String status; long heartbeat; long lastUpdated;
        NodeState(String id, String status) {
            this.id = id; this.status = status;
            this.heartbeat = System.currentTimeMillis();
            this.lastUpdated = heartbeat;
        }
    }

    public GossipNode(String nodeId, List<GossipNode> peers) {
        this.nodeId = nodeId;
        this.peers = peers;
        clusterState.put(nodeId, new NodeState(nodeId, "ALIVE"));
    }

    public void gossip() {
        // Exchange state with random peer
        GossipNode peer = peers.get(random.nextInt(peers.size()));
        if (peer != null) {
            peer.mergeState(clusterState);
        }
    }

    public synchronized void mergeState(Map<String, NodeState> incoming) {
        for (Map.Entry<String, NodeState> entry : incoming.entrySet()) {
            NodeState local = clusterState.get(entry.getKey());
            if (local == null || entry.getValue().heartbeat > local.heartbeat) {
                clusterState.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public void reportNodeFailure(String failedNode) {
        NodeState state = clusterState.get(failedNode);
        if (state != null) {
            state.status = "SUSPECTED";
            // Phi Accrual Failure Detector increment
        }
    }

    public Set<String> getAliveNodes() {
        Set<String> alive = new HashSet<>();
        long now = System.currentTimeMillis();
        for (Map.Entry<String, NodeState> entry : clusterState.entrySet()) {
            if (now - entry.getValue().lastUpdated < 10_000) {
                alive.add(entry.getKey());
            }
        }
        return alive;
    }
}
```

**Tradeoffs**:
- Eventual consistency of membership
- Bandwidth O(N) per node per round (all nodes send all state)
- Convergence time depends on fanout and round interval
- Need suspicion mechanisms to avoid premature failure

---

## CRDTs (Conflict-Free Replicated Data Types)

**When to use**: For eventually consistent systems that must allow concurrent writes without conflict resolution. Used in Riak, Redis CRDTs, collaborative editing (Google Docs).

**System context**: State-based (CvRDT) or operation-based (CmRDT). Operations commute or merge idempotently.

```java
// G-Counter (Grow-only Counter) - State-based CRDT
class GCounter {
    private final int[] values; // per-node counts
    private final int nodeId;

    public GCounter(int numNodes, int nodeId) {
        this.values = new int[numNodes];
        this.nodeId = nodeId;
    }

    public void increment() {
        values[nodeId]++;
    }

    public int value() {
        return Arrays.stream(values).sum();
    }

    public void merge(GCounter other) {
        for (int i = 0; i < values.length; i++) {
            values[i] = Math.max(values[i], other.values[i]);
        }
    }
}

// PN-Counter (Positive-Negative Counter) - supports decrement
class PNCounter {
    private final GCounter positive;
    private final GCounter negative;

    public PNCounter(int numNodes, int nodeId) {
        this.positive = new GCounter(numNodes, nodeId);
        this.negative = new GCounter(numNodes, nodeId);
    }

    public void increment() { positive.increment(); }
    public void decrement() { negative.increment(); }
    public int value() { return positive.value() - negative.value(); }
    public void merge(PNCounter other) {
        positive.merge(other.positive);
        negative.merge(other.negative);
    }
}

// LWW-Register (Last-Writer-Wins Register)
class LWWRegister<T> {
    private T value;
    private long timestamp;
    private final String nodeId;

    public LWWRegister(String nodeId) { this.nodeId = nodeId; }

    public void set(T newValue, long timestamp) {
        if (timestamp > this.timestamp ||
            (timestamp == this.timestamp && nodeId.compareTo(this.nodeId) > 0)) {
            this.value = newValue;
            this.timestamp = timestamp;
        }
    }

    public T get() { return value; }

    public void merge(LWWRegister<T> other) {
        if (other.timestamp > this.timestamp ||
            (other.timestamp == this.timestamp && other.nodeId.compareTo(this.nodeId) > 0)) {
            this.value = other.value;
            this.timestamp = other.timestamp;
        }
    }
}

// OR-Set (Observed-Remove Set)
class ORSet<T> {
    private final Map<T, Set<String>> elements = new HashMap<>();

    public void add(T element, String nodeId) {
        elements.computeIfAbsent(element, k -> new HashSet<>()).add(nodeId);
    }

    public void remove(T element) {
        Set<String> tags = elements.remove(element);
        // In operation-based CRDT, add "tombstone" tags
    }

    public boolean contains(T element) {
        return elements.containsKey(element) && !elements.get(element).isEmpty();
    }

    public Set<T> value() {
        return elements.keySet();
    }

    public void merge(ORSet<T> other) {
        for (Map.Entry<T, Set<String>> entry : other.elements.entrySet()) {
            elements.merge(entry.getKey(), entry.getValue(),
                (v1, v2) -> { v1.addAll(v2); return v1; });
        }
    }
}
```

**Tradeoffs**:
- Must know set of peers (state-based)
- Operation-based needs reliable exactly-once delivery
- Converges eventually but may need tombstones
- Limited data type support compared to full database

---

## Distributed Transaction Patterns

### Two-Phase Commit (2PC)

**When to use**: For atomic transactions across multiple distributed resources with ACID guarantees. Used in traditional distributed databases.

**System context**: Coordinator sends prepare to all participants. If all vote yes, coordinator sends commit. If any votes no, coordinator sends abort.

```java
class TwoPhaseCommitCoordinator {
    private final List<Participant> participants = new ArrayList<>();

    interface Participant {
        boolean prepare();
        boolean commit();
        boolean abort();
    }

    public boolean execute() {
        // Phase 1: Prepare
        for (Participant p : participants) {
            if (!p.prepare()) {
                // Abort all
                for (Participant rollback : participants) {
                    try { rollback.abort(); } catch (Exception e) {}
                }
                return false;
            }
        }

        // Phase 2: Commit
        for (Participant p : participants) {
            if (!p.commit()) {
                // Inconsistent state - needs recovery
                return false;
            }
        }
        return true;
    }
}
```

**Tradeoffs**:
- Blocking protocol - coordinator failure blocks participants
- No fault tolerance for coordinator itself
- Performance cost of two rounds

### Three-Phase Commit (3PC)

**When to use**: When you need non-blocking distributed transactions. Addresses 2PC's blocking problem.

**System context**: Adds a pre-commit phase. Can make progress after coordinator failure if all participants are in pre-commit state.

```java
class ThreePhaseCommit {
    enum State { INIT, PREPARE, PRECOMMIT, COMMIT, ABORT }

    public boolean execute() {
        // Phase 1: CanCommit?
        for (Participant p : participants) {
            if (!p.canCommit()) {
                abort();
                return false;
            }
        }

        // Phase 2: PreCommit
        for (Participant p : participants) {
            p.preCommit();
        }

        // Phase 3: DoCommit
        for (Participant p : participants) {
            if (!p.doCommit()) {
                return false;
            }
        }
        return true;
    }
}
```

**Tradeoffs**:
- More rounds = more latency
- Still can block under some failure scenarios
- Rarely used in practice (2PC + compensation is more common)

### SAGA Pattern

**When to use**: For long-running transactions where each step has a compensating action. Used in event-driven microservices architectures.

**System context**: Choreography (each service publishes events) or Orchestration (central coordinator manages steps).

```java
class SagaOrchestrator {
    private final Stack<SagaStep> history = new Stack<>();

    static class SagaStep {
        String name;
        Runnable action;
        Runnable compensate;

        SagaStep(String name, Runnable action, Runnable compensate) {
            this.name = name; this.action = action; this.compensate = compensate;
        }
    }

    public void execute(SagaStep step) {
        try {
            step.action.run();
            history.push(step);
        } catch (Exception e) {
            compensate();
            throw new SagaException("SAGA failed at step: " + step.name, e);
        }
    }

    private void compensate() {
        while (!history.isEmpty()) {
            SagaStep step = history.pop();
            try {
                step.compensate.run();
            } catch (Exception e) {
                // Log and continue - compensating actions should be idempotent
            }
        }
    }

    static class SagaException extends RuntimeException {
        SagaException(String msg, Throwable cause) { super(msg, cause); }
    }
}
```

### TCC (Try-Confirm/Cancel)

**When to use**: When you need reserved resources across services. Used in payment systems, booking systems.

```java
class TccTransaction {
    interface TccService {
        boolean tryReserve();   // Reserve resources
        boolean confirm();      // Commit
        boolean cancel();       // Release reservation
    }

    public static void run(List<TccService> services) {
        List<TccService> tried = new ArrayList<>();
        for (TccService svc : services) {
            if (svc.tryReserve()) {
                tried.add(svc);
            } else {
                for (TccService rollback : tried) rollback.cancel();
                return;
            }
        }
        for (TccService svc : tried) {
            if (!svc.confirm()) {
                // Partial confirm - needs reconciliation
            }
        }
    }
}
```

---

## Replication Patterns

### Leader-Follower (Single-Leader)

**When to use**: When you need strong consistency on writes and can tolerate stale reads. Used in MySQL replication, Redis Sentinel, PostgreSQL.

**System context**: One leader accepts writes, replicates to followers. Followers can serve read traffic.

```java
class LeaderFollowerReplication {
    static class LogEntry { long seq; String data; }

    private final List<LogEntry> log = new ArrayList<>();
    private final List<Replica> followers = new ArrayList<>();
    private long currentSeq = 0;

    static class Replica {
        String id; long replicatedSeq;
        Queue<LogEntry> pending = new LinkedList<>();

        boolean apply(LogEntry entry) {
            // Apply to local state
            replicatedSeq = entry.seq;
            return true;
        }
    }

    public void write(String data) {
        LogEntry entry = new LogEntry();
        entry.seq = ++currentSeq;
        entry.data = data;
        log.add(entry);

        for (Replica follower : followers) {
            follower.pending.offer(entry);
        }
    }

    public String read() {
        if (log.isEmpty()) return null;
        return log.get(log.size() - 1).data;
    }

    public String readFromFollower(Replica follower) {
        // May return stale data
        if (follower.replicatedSeq < currentSeq) {
            return null; // stale
        }
        return log.get((int)follower.replicatedSeq - 1).data;
    }
}
```

### Multi-Leader

**When to use**: For multi-region deployments, offline-first applications. Used in Cassandra, CouchDB, Google Docs.

```java
class MultiLeaderReplicator {
    static class Write {
        String key, value, nodeId;
        long timestamp;

        Write(String key, String value, String nodeId, long timestamp) {
            this.key = key; this.value = value;
            this.nodeId = nodeId; this.timestamp = timestamp;
        }
    }

    private final Queue<Write> conflictQueue = new ConcurrentLinkedQueue<>();

    // Last-writer-wins conflict resolution
    public Write resolveConflict(Write a, Write b) {
        if (a.timestamp > b.timestamp) return a;
        if (b.timestamp > a.timestamp) return b;
        return a.nodeId.compareTo(b.nodeId) > 0 ? a : b;
    }
}
```

### Quorum (NWR)

**When to use**: When you need configurable consistency vs latency tradeoff. Used in Amazon DynamoDB, Cassandra.

```java
class QuorumStore {
    private final int N; // total replicas
    private final int W; // write quorum
    private final int R; // read quorum

    public QuorumStore(int n, int w, int r) {
        this.N = n; this.W = w; this.R = r;
    }

    // For write: W replicas must acknowledge
    // For read: R replicas must respond, pick latest version
}
```

---

## Partitioning Strategies

### Range Partitioning

**When to use**: For range queries, ordered scans. Used in Bigtable, HBase, MongoDB.

```java
class RangePartitioner {
    private final NavigableMap<String, String> partitions = new TreeMap<>();

    public RangePartitioner() {
        // Define partition boundaries
        partitions.put("a", "node1");
        partitions.put("m", "node2");
        partitions.put("t", "node3");
    }

    public String getNode(String key) {
        if (key == null || key.isEmpty()) return partitions.firstEntry().getValue();
        Map.Entry<String, String> entry = partitions.floorEntry(key);
        return entry != null ? entry.getValue() : partitions.lastEntry().getValue();
    }
}
```

### Hash Partitioning

**When to use**: For even data distribution, point queries. Used in DynamoDB, Cassandra, Kafka.

```java
class HashPartitioner {
    private final String[] nodes;

    public HashPartitioner(String... nodes) { this.nodes = nodes; }

    public String getNode(String key) {
        return nodes[Math.abs(key.hashCode()) % nodes.length];
    }
}
```

### Consistent Partitioning (Cassandra)

```java
class CassandraPartitioner {
    private final TreeMap<BigInteger, String> ring = new TreeMap<>();

    public String getNode(String key) {
        BigInteger hash = MD5Hash(key);
        Map.Entry<BigInteger, String> entry = ring.ceilingEntry(hash);
        return entry != null ? entry.getValue() : ring.firstEntry().getValue();
    }

    private BigInteger MD5Hash(String key) {
        // Standard Cassandra uses MD5 for partitioner
        return new BigInteger(1, md5Bytes(key));
    }
}
```

### Geo-Partitioning

**When to use**: For latency optimization by region, data sovereignty compliance.

**Idea**: Partition by geographic region. Each region has its own set of nodes. Cross-region queries are routed to appropriate region.

---

## Distributed Caching Patterns

### CDN Cache

**When to use**: For serving static content globally with low latency.

```java
class CDNCache {
    private final Map<String, CachedContent> cache = new ConcurrentHashMap<>();
    private final int maxSize;
    private final String originUrl;

    static class CachedContent {
        byte[] data; String contentType; long cachedAt; long ttl;

        boolean isExpired() { return System.currentTimeMillis() - cachedAt > ttl; }
    }

    public byte[] get(String key) {
        CachedContent content = cache.get(key);
        if (content == null || content.isExpired()) {
            // Fetch from origin
            content = fetchFromOrigin(key);
            cache.put(key, content);
        }
        return content.data;
    }
}
```

### Write-Through Cache

**When to use**: For consistent cache + database writes.

```java
class WriteThroughCache<K, V> {
    private final Map<K, V> cache = new ConcurrentHashMap<>();
    private final Database<K, V> db;

    public V get(K key) {
        V val = cache.get(key);
        if (val == null) {
            val = db.read(key);
            if (val != null) cache.put(key, val);
        }
        return val;
    }

    public void put(K key, V value) {
        db.write(key, value);
        cache.put(key, value);
    }

    interface Database<K, V> { V read(K key); void write(K key, V value); }
}
```

### Write-Behind (Write-Back) Cache

**When to use**: For high write throughput, batch writes to DB.

```java
class WriteBehindCache<K, V> {
    private final Map<K, V> cache = new ConcurrentHashMap<>();
    private final Queue<K> dirty = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService flusher;

    public void put(K key, V value) {
        cache.put(key, value);
        dirty.offer(key);
    }

    public V get(K key) { return cache.get(key); }

    private void flush() {
        Set<K> batch = new HashSet<>();
        while (!dirty.isEmpty() && batch.size() < 100) {
            batch.add(dirty.poll());
        }
        // Batch write to database
    }
}
```

### Cache-Aside

**When to use**: For lazy-loading scenarios, high read:write ratio.

```java
class CacheAside<K, V> {
    private final Map<K, V> cache = new ConcurrentHashMap<>();
    private final Database<K, V> db;

    public V get(K key) {
        V val = cache.get(key);
        if (val == null) {
            val = db.read(key);
            if (val != null) cache.put(key, val);
        }
        return val;
    }

    public void put(K key, V value) {
        db.write(key, value);
        cache.remove(key); // invalidate, not update
    }
}
```

---

## Distributed Messaging Patterns

### Pub/Sub (Topic-based)

**When to use**: For one-to-many event distribution. Used in Kafka, Google Pub/Sub.

```java
class PubSubSystem {
    private final Map<String, List<Subscriber>> topics = new ConcurrentHashMap<>();

    interface Subscriber { void onMessage(String message); }

    public void subscribe(String topic, Subscriber sub) {
        topics.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(sub);
    }

    public void publish(String topic, String message) {
        List<Subscriber> subs = topics.get(topic);
        if (subs != null) {
            for (Subscriber sub : subs) {
                sub.onMessage(message);
            }
        }
    }
}
```

### Message Queue (Point-to-Point)

```java
class MessageQueue {
    private final Queue<Message> queue = new ConcurrentLinkedQueue<>();

    static class Message {
        String id; byte[] payload;
        Map<String, String> headers = new HashMap<>();
    }

    public void send(Message msg) { queue.offer(msg); }

    public Message receive() { return queue.poll(); }
}
```

### Log-based (Kafka-style)

```java
class DistributedLog {
    private final List<LogEntry> log = new ArrayList<>();
    private final Map<Integer, Long> consumerOffsets = new ConcurrentHashMap<>();

    static class LogEntry { long offset; String topic; int partition; byte[] data; }

    public long append(String topic, int partition, byte[] data) {
        LogEntry entry = new LogEntry();
        entry.offset = log.size();
        entry.topic = topic;
        entry.partition = partition;
        entry.data = data;
        log.add(entry);
        return entry.offset;
    }

    public List<LogEntry> read(String consumerId, int maxMessages) {
        long offset = consumerOffsets.getOrDefault(consumerId, 0L);
        List<LogEntry> result = new ArrayList<>();
        for (long i = offset; i < log.size() && result.size() < maxMessages; i++) {
            result.add(log.get((int)i));
        }
        consumerOffsets.put(consumerId, offset + result.size());
        return result;
    }
}
```

---

## Failure Detection Patterns

### Phi Accrual Failure Detector

**When to use**: For adaptive failure detection that accounts for network variance. Used in Cassandra, Akka.

```java
class PhiAccrualFailureDetector {
    private final long[] intervals = new long[1000];
    private int intervalIndex = 0;
    private long lastHeartbeat = System.currentTimeMillis();

    public void heartbeat() {
        long now = System.currentTimeMillis();
        long interval = now - lastHeartbeat;
        intervals[intervalIndex++ % intervals.length] = interval;
        lastHeartbeat = now;
    }

    public double phi(long now) {
        long elapsed = now - lastHeartbeat;
        double mean = Arrays.stream(intervals).average().orElse(1000);
        double variance = Arrays.stream(intervals)
            .mapToDouble(i -> Math.pow(i - mean, 2))
            .average().orElse(0);
        double stdDev = Math.sqrt(variance);
        if (stdDev < 1) stdDev = 1;

        // Probability that heartbeat arrives after elapsed time
        double probability = 1 - normalCdf((elapsed - mean) / stdDev);
        return -Math.log10(probability);
    }

    public boolean isAvailable(double threshold) {
        return phi(System.currentTimeMillis()) < threshold;
    }

    private double normalCdf(double x) {
        return 0.5 * (1 + erf(x / Math.sqrt(2)));
    }

    private double erf(double x) {
        // Approximation of error function
        double a1 = 0.254829592, a2 = -0.284496736;
        double a3 = 1.421413741, a4 = -1.453152027;
        double a5 = 1.061405429, p = 0.3275911;

        int sign = x < 0 ? -1 : 1;
        x = Math.abs(x);

        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
        return sign * y;
    }
}
```

### SWIM (Scalable Weakly-consistent Infection-style Membership)

**When to use**: For scalable group membership with eventual consistency. Used in Hashicorp Serf, memberlist.

**Protocol**: Each round, node pings a random member. If no ack, pings through indirect K members. On failure detection, disseminates via gossip.

---

## Leader Election Patterns

### Bully Algorithm

**When to use**: For small, reliable clusters. Simple to implement.

```java
class BullyElection {
    private final int nodeId;
    private int leaderId = -1;

    public void startElection() {
        List<Integer> higherNodes = getHigherNodes();
        if (higherNodes.isEmpty()) {
            leaderId = nodeId;
            announceVictory();
        } else {
            for (int higher : higherNodes) {
                sendElection(higher);
            }
            waitForOk(timeout);
            if (!receivedOk) {
                leaderId = nodeId;
                announceVictory();
            }
        }
    }

    public void receiveElection(int fromNode) {
        if (fromNode < nodeId) {
            sendOk(fromNode);
            startElection();
        }
    }

    private void announceVictory() { /* broadcast to all nodes */ }
    private List<Integer> getHigherNodes() { /* nodes with id > this.nodeId */ }
    private void sendElection(int node) { /* send ELECTION message */ }
    private void sendOk(int node) { /* send OK message */ }
}
```

### ZooKeeper-Style (Sequential Ephemeral)

```java
class ZooKeeperLeaderElection {
    private final String znodePath = "/election/";
    private String currentZnode;

    public void electLeader() {
        currentZnode = createEphemeralSequential(znodePath, nodeId);
        String smallest = getChildren(znodePath);
        if (currentZnode.equals(smallest)) {
            leaderId = nodeId;
            leaderCallback();
        } else {
            // Watch the preceding znode
            watchPreceding(currentZnode);
        }
    }
}
```

---

## LeetCode Problem Mapping

| Pattern | LeetCode Problems | Companies |
|---------|------------------|-----------|
| Consistent Hashing | 706 Design HashMap | Amazon, Google |
| Leader Election | 310 Min Height Trees | Google |
| Gossip Propagation | 994 Rotting Oranges | Amazon |
| CRDT Merge | 56 Merge Intervals | Amazon |
| 2PC | 207 Course Schedule | Google |
| Quorum | 199 Binary Tree Right Side | Meta |
| Heartbeat | 128 Longest Consecutive | Google, Meta |
| Vector Clock | 981 Time Based KV Store | Google |
| Write-ahead Log | 208 Implement Trie | Google |

---

> **Design principles**: Start simple, identify bottlenecks, make reasoned tradeoffs. Know your CAP theorem for each design decision.