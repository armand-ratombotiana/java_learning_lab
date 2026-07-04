# Performance

## Online Store
```java
JedisCluster cluster = new JedisCluster(
    new HostAndPort("redis-1", 6379));
Pipeline pipeline = cluster.pipelined();
// Batch reads for multiple features
```

## Caching
```java
@Cacheable(value = "features", key = "#type + ':' + #id")
public Map<String, Object> getFeatures(String type, String id) { ... }
```
