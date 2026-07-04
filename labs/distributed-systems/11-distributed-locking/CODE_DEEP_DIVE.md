# Distributed Locking: Code Deep Dive

## Redlock Implementation

```java
import java.util.*;
import redis.clients.jedis.*;

public class Redlock {
    private final List<Jedis> redisNodes;
    private final int quorum;
    private final long lockTimeoutMs;
    
    public Redlock(List<String> nodeHosts, long lockTimeoutMs) {
        this.redisNodes = nodeHosts.stream().map(Jedis::new).toList();
        this.quorum = nodeHosts.size() / 2 + 1;
        this.lockTimeoutMs = lockTimeoutMs;
    }
    
    public RedlockResult acquire(String resource, String requestId) {
        long startTime = System.currentTimeMillis();
        int acquired = 0;
        
        for (Jedis node : redisNodes) {
            String result = node.set(resource, requestId, 
                SetParams.setParams().nx().px(lockTimeoutMs));
            
            if ("OK".equals(result)) {
                acquired++;
            }
        }
        
        // Check if majority acquired within timeout
        long elapsed = System.currentTimeMillis() - startTime;
        if (acquired >= quorum && elapsed < lockTimeoutMs) {
            return new RedlockResult(true, requestId);
        }
        
        // Rollback - release all acquired locks
        for (Jedis node : redisNodes) {
            node.del(resource);
        }
        return new RedlockResult(false, null);
    }
    
    public boolean release(String resource, String requestId) {
        // Lua script ensures we only delete if we own the lock
        String script = """
            if redis.call("get", KEYS[1]) == ARGV[1] then
                return redis.call("del", KEYS[1])
            else
                return 0
            end
            """;
        
        for (Jedis node : redisNodes) {
            node.eval(script, List.of(resource), List.of(requestId));
        }
        return true;
    }
    
    record RedlockResult(boolean success, String requestId) {}
}
```

## Fencing Token Integration

```java
public class FencedResource {
    private final AtomicLong lastToken = new AtomicLong(0);
    private final Database db;
    
    public void writeData(String key, String value, long fencingToken) {
        long currentToken = lastToken.get();
        
        if (fencingToken <= currentToken) {
            throw new StaleLockException(
                "Fencing token " + fencingToken + " <= " + currentToken);
        }
        
        if (!lastToken.compareAndSet(currentToken, fencingToken)) {
            // Another thread updated the token
            throw new ConcurrentModificationException();
        }
        
        db.put(key, value);
    }
    
    static class StaleLockException extends RuntimeException {
        StaleLockException(String msg) { super(msg); }
    }
}
```
