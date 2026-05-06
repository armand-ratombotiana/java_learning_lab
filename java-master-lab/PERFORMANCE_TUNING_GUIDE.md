# Java Master Lab - Performance Tuning Guide

## ⚡ Comprehensive Performance Tuning

**Purpose**: Guide for optimizing Java application performance  
**Target Audience**: Senior developers and architects  
**Focus**: Speed, efficiency, and scalability  

---

## 🔍 PERFORMANCE PROFILING

### JVM Profiling Tools

#### Java Flight Recorder (JFR)
```bash
# Start application with JFR
java -XX:+UnlockCommercialFeatures \
     -XX:+FlightRecorder \
     -XX:StartFlightRecording=duration=60s,filename=recording.jfr \
     -jar application.jar

# Analyze recording
jmc -open recording.jfr
```

#### JProfiler
```bash
# Start with JProfiler agent
java -agentpath=/path/to/jprofiler/bin/linux-x64/libjprofilerti.so \
     -jar application.jar

# Connect to profiler GUI
# Monitor CPU, memory, threads
```

#### YourKit
```bash
# Start with YourKit agent
java -agentpath=/path/to/yjpagent.so \
     -jar application.jar

# Connect to profiler
# Real-time profiling and snapshots
```

### Metrics Collection

```java
public class PerformanceMonitor {
    private final Map<String, PerformanceMetric> metrics = new ConcurrentHashMap<>();
    
    public void recordOperation(String name, long durationMs) {
        metrics.computeIfAbsent(name, k -> new PerformanceMetric())
            .record(durationMs);
    }
    
    public void printReport() {
        metrics.forEach((name, metric) -> {
            System.out.printf("%s: avg=%.2fms, min=%dms, max=%dms, p95=%.2fms%n",
                name, metric.getAverage(), metric.getMin(), 
                metric.getMax(), metric.getPercentile(95));
        });
    }
    
    private static class PerformanceMetric {
        private final List<Long> durations = new ArrayList<>();
        
        synchronized void record(long duration) {
            durations.add(duration);
        }
        
        double getAverage() {
            return durations.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);
        }
        
        long getMin() {
            return durations.stream()
                .mapToLong(Long::longValue)
                .min()
                .orElse(0);
        }
        
        long getMax() {
            return durations.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        }
        
        double getPercentile(int percentile) {
            List<Long> sorted = durations.stream()
                .sorted()
                .collect(Collectors.toList());
            int index = (int) Math.ceil((percentile / 100.0) * sorted.size()) - 1;
            return sorted.get(Math.max(0, index));
        }
    }
}
```

---

## 💾 MEMORY OPTIMIZATION

### Heap Size Configuration

```bash
# Set heap size
java -Xms2G -Xmx4G -jar application.jar

# Xms: Initial heap size
# Xmx: Maximum heap size

# Monitor heap usage
jstat -gc -h10 <pid> 1000
```

### Garbage Collection Tuning

#### G1GC (Recommended for modern JVMs)
```bash
java -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:InitiatingHeapOccupancyPercent=35 \
     -jar application.jar
```

#### ZGC (Ultra-low latency)
```bash
java -XX:+UnlockExperimentalVMOptions \
     -XX:+UseZGC \
     -XX:ConcGCThreads=2 \
     -jar application.jar
```

### Memory Leak Detection

```java
public class MemoryLeakDetector {
    
    public static void detectMemoryLeaks() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        
        long usedMemory = heapUsage.getUsed();
        long maxMemory = heapUsage.getMax();
        double usagePercent = (usedMemory * 100.0) / maxMemory;
        
        if (usagePercent > 90) {
            System.out.println("WARNING: Heap usage at " + usagePercent + "%");
            dumpHeap();
        }
    }
    
    private static void dumpHeap() {
        try {
            HotSpotDiagnosticMXBean diagnostic = 
                ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
            diagnostic.dumpHeap("heap-dump.hprof", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

---

## ⚙️ CPU OPTIMIZATION

### Thread Optimization

```java
// ✅ Good - Appropriate thread pool size
ExecutorService executor = Executors.newFixedThreadPool(
    Runtime.getRuntime().availableProcessors()
);

// ❌ Bad - Too many threads
ExecutorService executor = Executors.newFixedThreadPool(1000);

// ✅ Better - Custom thread pool
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    Runtime.getRuntime().availableProcessors(),      // Core threads
    Runtime.getRuntime().availableProcessors() * 2,  // Max threads
    60,                                               // Keep-alive time
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(1000),                  // Queue size
    new ThreadPoolExecutor.CallerRunsPolicy()         // Rejection policy
);
```

### Lock Optimization

```java
// ❌ Bad - Coarse-grained locking
public class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
}

// ✅ Good - Fine-grained locking
public class Counter {
    private final AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();
    }
    
    public int getCount() {
        return count.get();
    }
}

// ✅ Better - Read-write lock
public class Cache<K, V> {
    private final Map<K, V> cache = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public V get(K key) {
        lock.readLock().lock();
        try {
            return cache.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            cache.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

---

## 🗄️ DATABASE OPTIMIZATION

### Query Optimization

```java
// ❌ Bad - Full table scan
SELECT * FROM users WHERE name LIKE '%john%';

// ✅ Good - Indexed search
SELECT * FROM users WHERE email = 'john@example.com';

// ✅ Better - Selective columns
SELECT id, name, email FROM users WHERE email = 'john@example.com';
```

### Index Strategy

```sql
-- Single column index
CREATE INDEX idx_email ON users(email);

-- Composite index
CREATE INDEX idx_user_status ON users(status, created_date);

-- Full-text index
CREATE FULLTEXT INDEX idx_description ON products(description);

-- Analyze query performance
EXPLAIN SELECT * FROM users WHERE email = 'john@example.com';
```

### Connection Pooling

```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
        config.setUsername("user");
        config.setPassword("password");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);
        return new HikariDataSource(config);
    }
}
```

---

## 🚀 APPLICATION OPTIMIZATION

### Caching Strategy

```java
// ✅ Multi-level caching
public class MultiLevelCache<K, V> {
    private final Map<K, V> l1Cache = new ConcurrentHashMap<>();
    private final Map<K, V> l2Cache = new ConcurrentHashMap<>();
    private final DataLoader<K, V> loader;
    
    public V get(K key) {
        // L1 cache (fast, small)
        V value = l1Cache.get(key);
        if (value != null) return value;
        
        // L2 cache (slower, larger)
        value = l2Cache.get(key);
        if (value != null) {
            l1Cache.put(key, value);
            return value;
        }
        
        // Load from source
        value = loader.load(key);
        l1Cache.put(key, value);
        l2Cache.put(key, value);
        return value;
    }
}
```

### Batch Processing

```java
// ❌ Bad - Individual operations
for (User user : users) {
    userRepository.save(user);  // N database calls
}

// ✅ Good - Batch operations
userRepository.saveAll(users);  // 1 database call

// ✅ Better - Batch with size limit
List<List<User>> batches = Lists.partition(users, 1000);
for (List<User> batch : batches) {
    userRepository.saveAll(batch);
}
```

### Lazy Loading

```java
// ❌ Bad - Eager loading everything
@Entity
public class User {
    @OneToMany(fetch = FetchType.EAGER)
    private List<Order> orders;
}

// ✅ Good - Lazy loading
@Entity
public class User {
    @OneToMany(fetch = FetchType.LAZY)
    private List<Order> orders;
}

// ✅ Better - Explicit JOIN FETCH
@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
Optional<User> findByIdWithOrders(@Param("id") Long id);
```

---

## 📊 PERFORMANCE BENCHMARKING

### JMH Benchmarking

```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class StringConcatenationBenchmark {
    
    private static final int ITERATIONS = 10000;
    
    @Benchmark
    public String stringConcatenation() {
        String result = "";
        for (int i = 0; i < ITERATIONS; i++) {
            result += "item" + i;
        }
        return result;
    }
    
    @Benchmark
    public String stringBuilder() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ITERATIONS; i++) {
            sb.append("item").append(i);
        }
        return sb.toString();
    }
    
    @Benchmark
    public String stringBuilderPreallocated() {
        StringBuilder sb = new StringBuilder(ITERATIONS * 10);
        for (int i = 0; i < ITERATIONS; i++) {
            sb.append("item").append(i);
        }
        return sb.toString();
    }
}
```

### Load Testing

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class LoadTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    public void testConcurrentLoad() throws InterruptedException {
        int threadCount = 100;
        int operationsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        userService.findUserById((long) (Math.random() * 10000));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        long duration = System.currentTimeMillis() - startTime;
        
        System.out.println("Total operations: " + (threadCount * operationsPerThread));
        System.out.println("Duration: " + duration + "ms");
        System.out.println("Throughput: " + 
            ((threadCount * operationsPerThread * 1000) / duration) + " ops/sec");
    }
}
```

---

## 📈 MONITORING & ALERTING

### Metrics Collection

```java
@Configuration
public class MetricsConfig {
    
    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
    
    @Bean
    public UserServiceMetrics userServiceMetrics(MeterRegistry registry) {
        return new UserServiceMetrics(registry);
    }
}

@Component
public class UserServiceMetrics {
    
    private final Timer findUserTimer;
    private final Counter createUserCounter;
    
    public UserServiceMetrics(MeterRegistry registry) {
        this.findUserTimer = Timer.builder("user.find")
            .description("Time to find user")
            .register(registry);
        
        this.createUserCounter = Counter.builder("user.create")
            .description("Number of users created")
            .register(registry);
    }
    
    public void recordFindUser(long durationMs) {
        findUserTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }
    
    public void recordCreateUser() {
        createUserCounter.increment();
    }
}
```

### Health Checks

```java
@Component
public class PerformanceHealthIndicator implements HealthIndicator {
    
    @Autowired
    private PerformanceMonitor monitor;
    
    @Override
    public Health health() {
        double avgResponseTime = monitor.getAverageResponseTime();
        
        if (avgResponseTime < 100) {
            return Health.up()
                .withDetail("avgResponseTime", avgResponseTime + "ms")
                .build();
        } else if (avgResponseTime < 500) {
            return Health.outOfService()
                .withDetail("avgResponseTime", avgResponseTime + "ms")
                .build();
        } else {
            return Health.down()
                .withDetail("avgResponseTime", avgResponseTime + "ms")
                .build();
        }
    }
}
```

---

## 📄 Document Information

| Property | Value |
|----------|-------|
| **Document Type** | Performance Tuning Guide |
| **Version** | 1.0 |
| **Created** | 2024 |
| **Status** | Active |
| **Focus** | Performance |

---

**Java Master Lab - Performance Tuning Guide**

*Comprehensive Performance Optimization Strategies*

**Status: Active | Focus: Performance | Impact: High**

---

*Optimize for peak performance!* ⚡