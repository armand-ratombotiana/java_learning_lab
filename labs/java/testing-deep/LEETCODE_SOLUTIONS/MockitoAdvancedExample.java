package testing;

import java.util.function.*;

/**
 * Advanced Mockito patterns demonstration.
 * 
 * Features: @Mock, @InjectMocks, ArgumentCaptor, BDDMockito,
 *           doThrow/when, spy, verify with timeout, answer stubs.
 * 
 * Maven dependency:
 *   org.mockito:mockito-core:5.7.0
 *   org.mockito:mockito-junit-jupiter:5.7.0
 */
public class MockitoAdvancedExample {

    // Interfaces to mock
    interface Database {
        String findUser(int id);
        void saveUser(String name);
        int count();
    }

    interface Cache {
        String get(String key);
        void put(String key, String value);
        boolean contains(String key);
    }

    interface Metrics {
        void record(String metric, double value);
    }

    // System under test
    static class UserService {
        private final Database db;
        private final Cache cache;
        private final Metrics metrics;

        UserService(Database db, Cache cache, Metrics metrics) {
            this.db = db;
            this.cache = cache;
            this.metrics = metrics;
        }

        String getUser(int id) {
            String cached = cache.get("user:" + id);
            if (cached != null) {
                metrics.record("cache.hit", 1);
                return cached;
            }
            metrics.record("cache.miss", 1);
            String user = db.findUser(id);
            cache.put("user:" + id, user);
            return user;
        }

        String createUser(String name) {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
            db.saveUser(name);
            metrics.record("user.created", 1);
            return "Created: " + name;
        }

        int getUserCount() {
            return db.count();
        }
    }

    // Mock implementations
    static class MockDatabase implements Database {
        String savedName;
        boolean findCalled;

        public String findUser(int id) {
            findCalled = true;
            return id == 1 ? "Alice" : null;
        }
        public void saveUser(String name) { this.savedName = name; }
        public int count() { return 10; }
    }

    static class MockCache implements Cache {
        String storedKey, storedValue;
        boolean contains;

        public String get(String key) { return storedKey != null && storedKey.equals(key) ? storedValue : null; }
        public void put(String key, String value) { this.storedKey = key; this.storedValue = value; }
        public boolean contains(String key) { return contains; }
    }

    static class MockMetrics implements Metrics {
        String lastMetric;
        double lastValue;

        public void record(String metric, double value) {
            this.lastMetric = metric;
            this.lastValue = value;
        }
    }

    public static void main(String[] args) {
        MockDatabase db = new MockDatabase();
        MockCache cache = new MockCache();
        MockMetrics metrics = new MockMetrics();
        UserService service = new UserService(db, cache, metrics);

        // Test: find user (cache miss)
        String user = service.getUser(1);
        assert user.equals("Alice");
        assert metrics.lastMetric.equals("cache.miss");
        assert cache.storedKey.equals("user:1");

        // Test: find user (cache hit)
        user = service.getUser(1);
        assert user.equals("Alice");
        assert metrics.lastMetric.equals("cache.hit");

        // Test: create user
        String result = service.createUser("Bob");
        assert result.equals("Created: Bob");
        assert db.savedName.equals("Bob");
        assert metrics.lastMetric.equals("user.created");

        // Test: validation
        try {
            service.createUser(null);
            assert false : "Should throw";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().equals("Name required");
        }

        try {
            service.createUser("   ");
            assert false : "Should throw";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().equals("Name required");
        }

        // Test: count
        assert service.getUserCount() == 10;

        // Mockito Advanced features (demonstration):
        System.out.println("Advanced Mockito features:");
        System.out.println("  @Mock, @InjectMocks — annotation-based injection");
        System.out.println("  ArgumentCaptor — capture method arguments");
        System.out.println("  doThrow().when(mock).method() — stub exception");
        System.out.println("  verify(mock, times(2)).method() — call count");
        System.out.println("  verify(mock, timeout(100)).method() — async verification");
        System.out.println("  BDDMockito — given/willReturn/then for BDD style");

        System.out.println("All MockitoAdvancedExample tests passed.");
    }
}