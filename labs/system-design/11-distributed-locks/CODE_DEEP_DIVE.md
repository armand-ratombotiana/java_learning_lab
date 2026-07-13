# Distributed Locks Code Deep Dive

This lab provides a pure Java simulation of a Redis-backed Distributed Lock. It demonstrates the atomic `SETNX` operation and the safe release using a unique identifier.

## 💻 Pure Java Implementation

```java file="labs/system-design/11-distributed-locks/SOLUTION/RedisDistributedLockSim.java"
package systemdesign.distributedlocks;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A simulation of a Redis-based Distributed Lock.
 */
public class RedisDistributedLockSim {

    // Simulating the Redis server's in-memory storage
    private static final ConcurrentHashMap<String, String> redisStorage = new ConcurrentHashMap<>();
    
    // Simulating Redis TTL expiration
    private static final ScheduledExecutorService redisTtlScheduler = Executors.newScheduledThreadPool(1);

    /**
     * Simulates the Redis command: SET key value NX PX ttl
     */
    private static boolean setNxPx(String key, String value, long ttlMillis) {
        // putIfAbsent is the Java equivalent of Redis SETNX (Set if Not eXists)
        String existingValue = redisStorage.putIfAbsent(key, value);
        
        if (existingValue == null) {
            // We acquired the lock. Schedule the TTL expiration.
            redisTtlScheduler.schedule(() -> {
                // In reality, Redis expires this automatically.
                // We only remove it if it still belongs to us (simulated here blindly for simplicity)
                redisStorage.remove(key, value);
            }, ttlMillis, TimeUnit.MILLISECONDS);
            return true;
        }
        return false;
    }

    /**
     * Simulates a Lua script that atomically checks the value and deletes the key.
     */
    private static boolean safeRelease(String key, String expectedValue) {
        // remove(key, value) is atomic in ConcurrentHashMap, just like a Lua script in Redis.
        // It only removes the key if the current value matches the expected value.
        return redisStorage.remove(key, expectedValue);
    }

    // --- The Client Implementation ---

    static class DistributedLockClient {
        private final String lockKey;
        private final String clientId;
        private final long ttlMillis;

        public DistributedLockClient(String lockKey, long ttlMillis) {
            this.lockKey = lockKey;
            // Generate a unique ID for this specific client/thread
            this.clientId = UUID.randomUUID().toString(); 
            this.ttlMillis = ttlMillis;
        }

        public boolean acquire() {
            System.out.println("Client [" + clientId + "] attempting to acquire lock...");
            boolean success = setNxPx(lockKey, clientId, ttlMillis);
            if (success) {
                System.out.println("Client [" + clientId + "] ACQUIRED the lock.");
            } else {
                System.out.println("Client [" + clientId + "] FAILED to acquire lock. Already held.");
            }
            return success;
        }

        public void release() {
            System.out.println("Client [" + clientId + "] attempting to release lock...");
            boolean success = safeRelease(lockKey, clientId);
            if (success) {
                System.out.println("Client [" + clientId + "] RELEASED the lock successfully.");
            } else {
                System.out.println("Client [" + clientId + "] FAILED to release lock. (It expired or was stolen!)");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String resourceName = "payment_processing_lock";
        
        DistributedLockClient nodeA = new DistributedLockClient(resourceName, 2000); // 2 second TTL
        DistributedLockClient nodeB = new DistributedLockClient(resourceName, 2000);

        // 1. Node A acquires the lock
        nodeA.acquire();

        // 2. Node B tries to acquire it and fails
        nodeB.acquire();

        // 3. Simulate Node A taking too long (e.g., a GC pause or slow DB query)
        System.out.println("\n[SIMULATION] Node A is pausing for 3 seconds...");
        Thread.sleep(3000);

        // 4. The lock has now expired in Redis. Node B tries again and succeeds!
        System.out.println("\n[SIMULATION] Lock expired in Redis.");
        nodeB.acquire();

        // 5. Node A wakes up and tries to release the lock
        // Because we use safeRelease() with the clientId, Node A will NOT accidentally delete Node B's lock!
        nodeA.release();
        
        // 6. Node B finishes and releases its lock
        nodeB.release();
        
        System.exit(0);
    }
}
```

## 🔍 Key Takeaways
1. **The Unique Identifier**: Notice that `clientId` is a random UUID. If we just used `redisStorage.remove(key)`, Node A would have deleted Node B's lock in Step 5, causing complete chaos. By checking that the value in Redis still matches Node A's UUID before deleting, we prevent accidental lock deletion.
2. **The TTL Danger**: If you run this code, you'll see Node A failed to release the lock because it expired. However, Node A *already executed its critical section* while Node B also held the lock. This proves that TTLs alone do not guarantee mutual exclusion if a process pauses. You must use Fencing Tokens in your database to be truly safe.