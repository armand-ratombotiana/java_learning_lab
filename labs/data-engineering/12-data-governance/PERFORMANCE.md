# Performance

## Indexing
```java
@Table(name = "data_assets", indexes = {
    @Index(name = "idx_name", columnList = "name"),
    @Index(name = "idx_domain", columnList = "domain"),
})
```

## Caching
```java
@Cacheable(value = "lineage")
public LineageGraph getLineage(String id, Direction dir) { ... }

@Cacheable(value = "policies")
public AccessDecision evaluate(AccessRequest r) { ... }
```
