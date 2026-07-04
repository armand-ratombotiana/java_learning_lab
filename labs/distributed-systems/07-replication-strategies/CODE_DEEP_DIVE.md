# Replication: Code Deep Dive

## Replication Manager Implementation

```java
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class ReplicationManager<K, V> {
    private final String nodeId;
    private final Map<K, VersionedValue<V>> data = new ConcurrentHashMap<>();
    private final List<ReplicaPeer> peers;
    private final boolean isLeader;
    
    public ReplicationManager(String nodeId, List<ReplicaPeer> peers, boolean isLeader) {
        this.nodeId = nodeId;
        this.peers = peers;
        this.isLeader = isLeader;
        
        if (!isLeader) {
            startReplicationReceiver();
        }
    }
    
    public WriteResult write(K key, V value) {
        if (!isLeader) {
            // Forward to leader
            ReplicaPeer leader = getLeader();
            return leader.forwardWrite(key, value);
        }
        
        long version = System.currentTimeMillis();
        data.put(key, new VersionedValue<>(value, version));
        
        // Asynchronous replication
        for (ReplicaPeer peer : peers) {
            if (!peer.isLeader()) {
                peer.replicate(key, value, version);
            }
        }
        
        return new WriteResult(true, version);
    }
    
    public ReadResult<V> read(K key) {
        VersionedValue<V> value = data.get(key);
        if (value == null) return null;
        return new ReadResult<>(value.value, value.version);
    }
    
    private void startReplicationReceiver() {
        // Listen for replication requests from leader
        new Thread(() -> {
            while (true) {
                ReplicationRequest req = receiveReplication();
                data.put(req.key, 
                    new VersionedValue<>((V) req.value, req.version));
            }
        }).start();
    }
    
    static class VersionedValue<V> {
        final V value;
        final long version;
        
        VersionedValue(V value, long version) {
            this.value = value;
            this.version = version;
        }
    }
    
    static class WriteResult {
        final boolean success;
        final long version;
        WriteResult(boolean s, long v) { success = s; version = v; }
    }
    
    static class ReadResult<V> {
        final V value;
        final long version;
        ReadResult(V v, long ver) { value = v; version = ver; }
    }
    
    static class ReplicationRequest {
        final Object key;
        final Object value;
        final long version;
    }
}
```
