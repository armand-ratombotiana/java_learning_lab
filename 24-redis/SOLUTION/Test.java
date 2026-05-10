package solution;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import redis.clients.jedis.JedisPool;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RedisSolutionTest {

    private RedisSolution solution;
    private GenericContainer<?> redisContainer;

    @BeforeAll
    void setUp() {
        redisContainer = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);
        redisContainer.start();

        JedisPool jedisPool = new JedisPool(
            redisContainer.getHost(),
            redisContainer.getMappedPort(6379)
        );
        solution = new RedisSolution(jedisPool);
    }

    @AfterAll
    void tearDown() {
        redisContainer.stop();
    }

    @Test
    void testCacheSetAndGet() {
        solution.cacheSet("test:key", "test:value", 60);
        String value = solution.cacheGet("test:key");
        assertEquals("test:value", value);
    }

    @Test
    void testCacheDelete() {
        solution.cacheSet("delete:key", "value", 60);
        solution.cacheDelete("delete:key");
        assertNull(solution.cacheGet("delete:key"));
    }

    @Test
    void testHashOperations() {
        String key = "user:1";
        solution.hashSet(key, java.util.Map.of("name", "John", "email", "john@test.com"));
        var hash = solution.hashGetAll(key);
        assertEquals("John", hash.get("name"));
        assertEquals("john@test.com", hash.get("email"));
    }

    @Test
    void testSetOperations() {
        String key = "tags:java";
        solution.setAdd(key, "spring", "hibernate", "junit");
        var members = solution.setMembers(key);
        assertTrue(members.contains("spring"));
        assertEquals(3, members.size());
    }

    @Test
    void testLeaderboard() {
        String leaderboard = "scores";
        solution.addToLeaderboard(leaderboard, "player1", 100);
        solution.addToLeaderboard(leaderboard, "player2", 200);
        var top = solution.getTopScores(leaderboard, 2);
        assertEquals("player2", top.get(0));
        assertEquals("player1", top.get(1));
    }

    @Test
    void testDistributedLock() {
        String lockKey = "resource:lock";
        boolean acquired = solution.acquireLock(lockKey, 10);
        assertTrue(acquired);
        boolean alreadyAcquired = solution.acquireLock(lockKey, 10);
        assertFalse(alreadyAcquired);
    }

    @Test
    void testHyperLogLog() {
        String key = "visitors";
        solution.addToHLL(key, "user1", "user2", "user3", "user1");
        long count = solution.countHLL(key);
        assertTrue(count >= 3);
    }
}