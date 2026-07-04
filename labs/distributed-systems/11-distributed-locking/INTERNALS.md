# Distributed Locking: Internals

## Fencing Tokens

### Problem
A lock holder pauses (GC pause), lease expires, lock acquired by another, original holder resumes and operates on stale assumption.

### Solution
```java
public class FencingTokenLock {
    private final AtomicLong tokenCounter = new AtomicLong();
    private final Map<String, Long> activeTokens = new ConcurrentHashMap<>();
    
    public LockResult acquire(String ownerId) {
        long token = tokenCounter.incrementAndGet();
        activeTokens.put(ownerId, token);
        return new LockResult(true, token);
    }
    
    public boolean validateToken(long token) {
        return activeTokens.containsValue(token);
    }
}

// Resource side
public class ResourceGuard {
    private long lastToken = 0;
    
    public void write(WriteOperation op, long fencingToken) {
        if (fencingToken <= lastToken) {
            throw new SecurityException("Stale fencing token");
        }
        lastToken = fencingToken;
        // Perform write
    }
}
```

## Lease Mechanism
- Lock automatically expires after lease duration
- Holder must renew lease before expiry
- If holder crashes, lock expires and can be acquired
- Lease duration must account for GC pauses (typically 10-60 seconds)
