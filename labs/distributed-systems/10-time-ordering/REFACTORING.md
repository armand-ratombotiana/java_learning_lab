# Refactoring for Time and Ordering

## From Physical to Logical Clocks

### Before (Physical Clock):
```java
public class EventLogger {
    public void log(String event) {
        long timestamp = System.currentTimeMillis();
        store(timestamp, event);
    }
}
```

### After (Lamport Clock):
```java
public class DistributedEventLogger {
    private final LamportClock clock = new LamportClock();
    
    public void log(String event) {
        long timestamp = clock.tick();
        broadcast(timestamp, event);
    }
    
    public void receiveLog(long remoteTimestamp, String event) {
        clock.update(remoteTimestamp);
        store(Math.max(remoteTimestamp, clock.getValue()), event);
    }
}
```
