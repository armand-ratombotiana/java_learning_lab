# Caching - MINI PROJECT

Build a caching layer with:
- Cache-aside pattern
- Redis backend
- TTL management
- Cache warming

```java
@Service
public class UserCacheService {
    public User getUser(String id) {
        return cache.getOrCompute("user:" + id, 
            () -> userRepository.findById(id).orElseThrow());
    }
}
```