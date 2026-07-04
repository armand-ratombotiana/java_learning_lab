# Debugging Lock Issues

## Lock Contention Detection
```java
public class LockMonitor {
    private final Map<String, Long> lockAcquireTimes = new ConcurrentHashMap<>();
    
    public void recordAcquire(String lockName, long timeMs) {
        lockAcquireTimes.put(lockName, timeMs);
    }
    
    public void reportContention() {
        lockAcquireTimes.forEach((lock, time) -> {
            if (time > 1000) {
                System.err.println("Lock contention on " + lock + 
                    ": waited " + time + "ms");
            }
        });
    }
}
```

## Common Issues
- **Lease expiry**: Check GC logs, increase lease duration
- **Deadlock**: Ensure lock ordering across all services
- **Stale locks**: Verify cleanup on service shutdown
- **Watch drift**: ZooKeeper watches can miss events
