# Refactoring for Distributed Locking

## From Database Lock to ZooKeeper Lock

### Before (Database Lock):
```java
public class DBLockService {
    public void executeWithLock(String taskId, Runnable task) {
        // SELECT ... FOR UPDATE on task table
        database.query("SELECT * FROM tasks WHERE id = ? FOR UPDATE", taskId);
        task.run();
        database.commit();
    }
}
```

### After (ZooKeeper Lock):
```java
public class ZKLockService {
    public void executeWithLock(String taskId, Runnable task) {
        ZooKeeperDistributedLock lock = new ZooKeeperDistributedLock(
            zkConn, "/locks/" + taskId);
        
        try {
            if (lock.acquire()) {
                task.run();
            }
        } finally {
            lock.release();
        }
    }
}
```
