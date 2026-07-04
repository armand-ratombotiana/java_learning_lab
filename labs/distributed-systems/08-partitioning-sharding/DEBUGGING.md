# Debugging Partitioning Issues

## Hot Spot Detection
```java
public class ShardMonitor {
    public static Map<Integer, Long> measureLoad(List<Shard> shards) {
        Map<Integer, Long> load = new HashMap<>();
        for (int i = 0; i < shards.size(); i++) {
            load.put(i, shards.get(i).getOperationCount());
        }
        return load;
    }
    
    public static double getBalanceFactor(Map<Integer, Long> load) {
        long max = Collections.max(load.values());
        long min = Collections.min(load.values());
        return (double) max / min;
    }
}
```

## Common Issues
- Uneven data distribution: check shard key distribution
- Slow cross-shard queries: add denormalization
- Rebalancing stuck: check disk space, network, locks
