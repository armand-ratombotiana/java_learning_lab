# Partitioning: Security

## Security Implications
- Shard key may leak information about data distribution
- Canary attacks: observe which shard handles which data
- Uneven shard distribution may reveal usage patterns

## Best Practices
1. **Hash shard keys** on sensitive identifiers
2. **Encrypt cross-shard communication**
3. **Audit cross-shard queries** (may indicate data access pattern mining)
4. **Consistent hashing** prevents shard enumeration attacks
5. **Isolate shards** in separate security zones when possible

```java
public class SecureShardRouter {
    public int getShardId(String sensitiveKey) {
        String hashed = sha256(sensitiveKey);
        return hashRing.getNode(hashed);
    }
}
```
