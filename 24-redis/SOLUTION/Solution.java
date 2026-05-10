package solution;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RedisSolution {

    private final JedisPool jedisPool;

    public RedisSolution(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    // Caching
    public void cacheSet(String key, String value, int ttlSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, ttlSeconds, value);
        }
    }

    public String cacheGet(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public void cacheDelete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    // Pub/Sub
    public void publish(String channel, String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(channel, message);
        }
    }

    public void subscribe(String channel) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.subscribe(new RedisSubscriber(), channel);
        }
    }

    // Distributed Lock
    public boolean acquireLock(String lockKey, int ttlSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            String result = jedis.set(lockKey, UUID.randomUUID().toString(),
                SetParams.setParams().nx().ex(ttlSeconds));
            return "OK".equals(result);
        }
    }

    public boolean releaseLock(String lockKey, String uniqueId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            return jedis.eval(script, 1, lockKey, uniqueId).equals(1L);
        }
    }

    // Sorted Sets for leaderboards
    public void addToLeaderboard(String leaderboardKey, String member, double score) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zadd(leaderboardKey, score, member);
        }
    }

    public List<String> getTopScores(String leaderboardKey, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrange(leaderboardKey, 0, count - 1);
        }
    }

    // Hash operations
    public void hashSet(String key, Map<String, String> fieldValues) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(key, fieldValues);
        }
    }

    public Map<String, String> hashGetAll(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(key);
        }
    }

    // Set operations
    public void setAdd(String key, String... members) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd(key, members);
        }
    }

    public Set<String> setMembers(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(key);
        }
    }

    // Bitmap operations
    public void setBit(String key, long offset, boolean value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setbit(key, offset, value ? "1" : "0");
        }
    }

    public boolean getBit(String key, long offset) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.getbit(key, offset);
        }
    }

    // HyperLogLog for cardinality estimation
    public void addToHLL(String key, String... elements) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.pfadd(key, elements);
        }
    }

    public long countHLL(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.pfcount(key);
        }
    }
}