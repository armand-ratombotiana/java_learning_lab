# Refactoring for Replication

## Adding Replication to Single-Node Store

### Before (Single Node):
```java
public class SingleNodeStore {
    private final Map<String, Object> data = new HashMap<>();
    
    public void put(String key, Object value) {
        data.put(key, value);
    }
}
```

### After (Replicated):
```java
public class ReplicatedStore {
    private final Map<String, Object> localData = new HashMap<>();
    private final ReplicationManager replication;
    
    public void put(String key, Object value) {
        localData.put(key, value);
        replication.broadcast(key, value);
    }
}
```
