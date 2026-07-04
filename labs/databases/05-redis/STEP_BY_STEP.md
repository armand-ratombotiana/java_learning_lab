# Step-by-Step: Redis

## Step 1: Install Redis
```bash
# Docker
docker run --name redis -p 6379:6379 -d redis:7-alpine

# Verify
redis-cli ping
# PONG
```

## Step 2: Add Redis Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

## Step 3: Configure Redis Connection
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 16
        max-idle: 8
        min-idle: 4
```

## Step 4: Configure RedisTemplate
```java
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return template;
    }
}
```

## Step 5: Basic Operations
```java
@Autowired
private RedisTemplate<String, String> redis;

// String
redis.opsForValue().set("key", "value");
String val = redis.opsForValue().get("key");

// List
redis.opsForList().rightPush("queue", "job1");
String job = redis.opsForList().leftPop("queue");

// Set
redis.opsForSet().add("tags", "java");
Set<String> tags = redis.opsForSet().members("tags");
```

## Step 6: Set Expiration
```java
redis.opsForValue().set("session:123", userId, 30, TimeUnit.MINUTES);
redis.expire("temp:key", 1, TimeUnit.HOURS);
Long ttl = redis.getExpire("session:123");
```

## Step 7: Enable Spring Cache
```java
@SpringBootApplication
@EnableCaching
public class Application {}

@Service
public class ProductService {
    @Cacheable(value = "products", key = "#id")
    public Product getProduct(Long id) { ... }

    @CacheEvict(value = "products", key = "#product.id")
    public Product update(Product product) { ... }
}
```

## Step 8: Set Up Redis-Cluster
```bash
# Create 6 nodes (3 masters, 3 replicas)
for port in 7000 7001 7002 7003 7004 7005; do
  mkdir -p /redis/$port
  cat > /redis/$port/redis.conf <<EOF
port $port
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
appendonly yes
EOF
  redis-server /redis/$port/redis.conf
done

# Create cluster
redis-cli --cluster create \
  127.0.0.1:7000 127.0.0.1:7001 127.0.0.1:7002 \
  127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 \
  --cluster-replicas 1
```

## Step 9: Monitor
```bash
redis-cli MONITOR
redis-cli INFO
redis-cli --stat
redis-cli SLOWLOG GET 10
```

## Step 10: Configure Persistence
```conf
save 900 1         # RDB: 1 change in 15 min
save 300 10        # RDB: 10 changes in 5 min
save 60 10000      # RDB: 10000 changes in 1 min
appendonly yes     # AOF: append-only file
appendfsync everysec  # AOF fsync every second
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb
```
