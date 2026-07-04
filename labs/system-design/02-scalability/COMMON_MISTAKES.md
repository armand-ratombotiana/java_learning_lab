# Scalability - COMMON MISTAKES

## 1. Premature Optimization
Scaling before understanding actual bottlenecks wastes time and money.
```java
// WRONG: Adding distributed cache before measuring
@Cacheable("users")
public User getUser(String id) { /* ... */ }

// RIGHT: Profile first, cache only identified bottleneck
```
Always measure before optimizing.

## 2. Sticky Sessions by Default
Session affinity prevents even load distribution.
```java
// WRONG: Storing sessions in local memory
httpSession.setAttribute("user", user);

// RIGHT: Stateless JWT or external session store
String token = jwtService.generateToken(user);
```

## 3. Ignoring Database Bottlenecks
Scaling app servers without scaling the database just shifts the bottleneck.

## 4. Hot Shards
Bad shard key selection causes uneven load distribution.
```sql
-- WRONG: Sharding by country (US has 50% of data)
shard_key = country;

-- BETTER: Sharding by customer_id (uniform distribution)
shard_key = customer_id;
```

## 5. Cache Stampede
When many requests miss cache simultaneously, they all hit the database.
```java
// WRONG: No protection
Product p = cache.get(id);
if (p == null) p = db.findById(id);  // 1000 requests hit DB

// RIGHT: Use locking or early recompute
Product p = cache.get(id);
if (p == null) {
    synchronized (this) {  // or distributed lock
        p = cache.get(id);  // double-check
        if (p == null) p = db.findById(id);
    }
}
```

## 6. Unbounded Queues
Message queues without limits cause memory exhaustion under load.
```java
// WRONG: No capacity limit
BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

// RIGHT: Bounded queue
BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(10000);
```

## 7. Not Planning for Scale-Down
Aggressive scale-down causes thrashing. Always use cooldown periods.

## 8. Ignoring Compose-ability
Design services that work as building blocks. Monolithic scaling is limited.
