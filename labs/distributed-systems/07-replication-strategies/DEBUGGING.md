# Debugging Replication Issues

## Replication Lag Monitoring
```java
public class ReplicationLagMonitor {
    public static long measureLag(Node leader, Node follower) {
        // Write a timestamp to leader
        String key = "_lag:" + System.currentTimeMillis();
        leader.write(key, System.currentTimeMillis());
        
        // Read from follower
        long start = System.nanoTime();
        while (true) {
            Object val = follower.read(key);
            if (val != null) {
                long writeTime = (long) val;
                return System.currentTimeMillis() - writeTime;
            }
            if (System.nanoTime() - start > 5_000_000_000L) {
                return -1; // Timeout
            }
            Thread.sleep(10);
        }
    }
}
```

## Common Issues
- Broken replication: check binlog position, network, permissions
- Duplicate key errors: ensure idempotent replication
- Inconsistent data: run checksum comparison
