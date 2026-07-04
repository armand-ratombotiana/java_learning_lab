# Code Deep Dive: Redis with Java

## Caching with Spring Data Redis

```java
@Service
public class ProductService {

    @Autowired
    private RedisTemplate<String, Product> redisTemplate;

    @Autowired
    private ProductRepository productRepository;

    private static final String CACHE_KEY = "product:";

    public Product getProduct(Long id) {
        String key = CACHE_KEY + id;

        // Check cache first
        Product cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }

        // Cache miss — load from database
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));

        // Store in cache with 1 hour TTL
        redisTemplate.opsForValue().set(key, product, 1, TimeUnit.HOURS);
        return product;
    }

    public void updateProduct(Product product) {
        productRepository.save(product);
        // Invalidate cache
        redisTemplate.delete(CACHE_KEY + product.getId());
    }
}
```

## Rate Limiting with Atomic Operations

```java
@Component
public class RateLimiter {

    private final RedisTemplate<String, String> redisTemplate;

    public RateLimiter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String userId, int maxRequests, int windowSeconds) {
        String key = "ratelimit:" + userId + ":" + Instant.now().getEpochSecond() / windowSeconds;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);
        }
        return count <= maxRequests;
    }
}
```

## Job Queue with Redis Lists

```java
@Component
public class JobQueue {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String QUEUE_KEY = "job:queue";

    public void enqueue(String jobJson) {
        redisTemplate.opsForList().rightPush(QUEUE_KEY, jobJson);
    }

    public String dequeue(long timeout, TimeUnit unit) {
        // Blocking pop — waits for jobs without polling
        List<String> result = redisTemplate.opsForList()
            .leftPop(QUEUE_KEY, timeout, unit);
        return result != null ? result.get(0) : null;
    }
}
```

## Leaderboard with Sorted Sets

```java
@Component
public class LeaderboardService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String LEADERBOARD_KEY = "game:leaderboard";

    public void submitScore(String player, int score) {
        redisTemplate.opsForZSet().add(LEADERBOARD_KEY, player, score);
    }

    public Set<String> getTopPlayers(int count) {
        return redisTemplate.opsForZSet()
            .reverseRange(LEADERBOARD_KEY, 0, count - 1);
    }

    public Long getRank(String player) {
        return redisTemplate.opsForZSet()
            .reverseRank(LEADERBOARD_KEY, player);
    }

    public Double getScore(String player) {
        return redisTemplate.opsForZSet()
            .score(LEADERBOARD_KEY, player);
    }
}
```

## Distributed Lock (Lettuce)

```java
@Component
public class DistributedLock {

    private final RedisCommands<String, String> sync;

    public boolean tryLock(String key, String value, long ttlSeconds) {
        return sync.setnx(key, value) && sync.expire(key, ttlSeconds);
    }

    public void unlock(String key, String value) {
        // Lua script for atomic unlock (only if value matches)
        String script = """
            if redis.call("get", KEYS[1]) == ARGV[1] then
                return redis.call("del", KEYS[1])
            else
                return 0
            end
            """;
        sync.eval(script, ScriptOutputType.INTEGER, new String[]{key}, value);
    }
}
```

## Session Store

```java
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class SessionConfig {
    // Spring Session + Redis = distributed HTTP sessions
}
```
