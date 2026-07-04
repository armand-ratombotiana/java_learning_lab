# Consistency Models: Architecture

## Layered Consistency Architecture

```
┌──────────────────────────────────────┐
│        Application Layer            │
│  (Chooses per-operation consistency) │
├──────────────────────────────────────┤
│        Consistency Layer            │
│  (Strong / Causal / Eventual)       │
├──────────────────────────────────────┤
│        Replication Layer            │
│  (Leader/Follower, Multi-Leader)    │
├──────────────────────────────────────┤
│        Storage Engine               │
└──────────────────────────────────────┘
```

## Per-Request Consistency Selection
```java
public class ConfigurableConsistencyStore {
    public Object read(String key, ConsistencyLevel level) {
        switch (level) {
            case STRONG: return readWithQuorum(key);
            case EVENTUAL: return readFromLocal(key);
            case CAUSAL: return readWithVectorClock(key);
        }
    }
}
```
