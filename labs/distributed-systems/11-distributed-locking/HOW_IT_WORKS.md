# How Distributed Locking Works

## ZooKeeper Lock

```java
import org.apache.zookeeper.*;

public class ZooKeeperDistributedLock {
    private final ZooKeeper zk;
    private final String lockPath;
    private final String lockNodePrefix = "lock-";
    private String currentNode;
    private String watchedNode;
    
    public ZooKeeperDistributedLock(String connectString, String lockPath) 
            throws Exception {
        this.zk = new ZooKeeper(connectString, 3000, null);
        this.lockPath = lockPath;
    }
    
    public boolean acquire() throws Exception {
        // Create sequential ephemeral node
        currentNode = zk.create(
            lockPath + "/" + lockNodePrefix, 
            new byte[0], 
            ZooDefs.Ids.OPEN_ACL_UNSAFE, 
            CreateMode.EPHEMERAL_SEQUENTIAL
        );
        
        // Try to acquire lock
        return attemptAcquire();
    }
    
    private boolean attemptAcquire() throws Exception {
        List<String> children = zk.getChildren(lockPath, false);
        Collections.sort(children);
        
        int currentIndex = children.indexOf(
            currentNode.substring(lockPath.length() + 1));
        
        if (currentIndex == 0) {
            // I'm the first in line - I have the lock
            return true;
        }
        
        // Watch the preceding node
        String previousNode = lockPath + "/" + children.get(currentIndex - 1);
        Stat stat = zk.exists(previousNode, watchedEvent -> {
            synchronized (this) {
                notifyAll();
            }
        });
        
        if (stat == null) {
            // Preceding node already deleted, try again
            return attemptAcquire();
        }
        
        watchedNode = previousNode;
        return false; // Wait for notification
    }
    
    public void release() throws Exception {
        zk.delete(currentNode, -1);
    }
}
```

## etcd Lock

```java
// Simplified etcd lock using TTL lease
public class EtcdLock {
    private final Client client;
    private final String lockKey;
    private long leaseId;
    
    public boolean tryAcquire(String lockKey, long ttlSeconds) {
        LeaseGrantResponse lease = client.getLeaseClient()
            .grant(ttlSeconds);
        this.leaseId = lease.getID();
        
        // Try to create key with lease
        try {
            client.getKVClient().put(
                ByteSequence.from(lockKey, UTF_8),
                ByteSequence.from("locked", UTF_8),
                PutOption.withLeaseId(leaseId)
            );
            return true;
        } catch (Exception e) {
            return false; // Key exists - lock held by another
        }
    }
    
    public void release() {
        client.getLeaseClient().revoke(leaseId);
    }
}
```
