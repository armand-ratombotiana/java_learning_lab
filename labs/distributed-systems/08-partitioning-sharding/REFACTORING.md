# Refactoring for Sharding

## Migrating to Sharded Architecture

### Before (Single Database):
```java
public class UserRepository {
    private final Database db;
    
    public User findById(String id) {
        return db.query("SELECT * FROM users WHERE id = ?", id);
    }
}
```

### After (Sharded):
```java
public class ShardedUserRepository {
    private final ShardManager shardManager;
    private final Database[] shards;
    
    public User findById(String id) {
        int shardId = shardManager.getShardForId(id);
        return shards[shardId].query("SELECT * FROM users WHERE id = ?", id);
    }
}
```
