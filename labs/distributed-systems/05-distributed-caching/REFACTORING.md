# Refactoring for Caching

## Adding Cache to Existing Service

### Before:
```java
public class UserService {
    private final Database db;
    
    public User getUser(String id) {
        return db.query("SELECT * FROM users WHERE id = ?", id);
    }
}
```

### After:
```java
public class CachedUserService {
    private final Database db;
    private final Cache cache;
    
    public User getUser(String id) {
        User cached = cache.get("user:" + id);
        if (cached != null) return cached;
        
        User user = db.query("SELECT * FROM users WHERE id = ?", id);
        if (user != null) cache.put("user:" + id, user);
        return user;
    }
    
    public void updateUser(String id, User user) {
        db.update("UPDATE users SET ... WHERE id = ?", user);
        cache.invalidate("user:" + id);
    }
}
```
