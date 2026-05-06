# Java Master Lab - Advanced Optimization Guide

## 🚀 Advanced Optimization Strategies

**Purpose**: Comprehensive guide for performance optimization  
**Target Audience**: Advanced developers and architects  
**Focus**: Code, platform, and infrastructure optimization  

---

## 📊 Performance Baseline

### Current Metrics
- **Code Coverage**: 80%+
- **Test Execution Time**: < 5 minutes
- **Build Time**: < 2 minutes
- **Documentation Generation**: < 1 minute
- **Platform Response Time**: < 200ms

### Optimization Goals
- **Code Coverage**: 85%+
- **Test Execution Time**: < 3 minutes
- **Build Time**: < 1 minute
- **Documentation Generation**: < 30 seconds
- **Platform Response Time**: < 100ms

---

## 🔧 CODE OPTIMIZATION

### 1. Algorithm Optimization

#### Complexity Analysis
```java
// O(n²) - Inefficient
public static int[] bubbleSort(int[] arr) {
    for (int i = 0; i < arr.length; i++) {
        for (int j = 0; j < arr.length - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
        }
    }
    return arr;
}

// O(n log n) - Efficient
public static int[] mergeSort(int[] arr) {
    if (arr.length <= 1) return arr;
    
    int mid = arr.length / 2;
    int[] left = mergeSort(Arrays.copyOfRange(arr, 0, mid));
    int[] right = mergeSort(Arrays.copyOfRange(arr, mid, arr.length));
    
    return merge(left, right);
}

private static int[] merge(int[] left, int[] right) {
    int[] result = new int[left.length + right.length];
    int i = 0, j = 0, k = 0;
    
    while (i < left.length && j < right.length) {
        result[k++] = left[i] <= right[j] ? left[i++] : right[j++];
    }
    
    while (i < left.length) result[k++] = left[i++];
    while (j < right.length) result[k++] = right[j++];
    
    return result;
}
```

#### Optimization Techniques
- [ ] Use appropriate data structures
- [ ] Reduce algorithmic complexity
- [ ] Eliminate unnecessary iterations
- [ ] Cache computed values
- [ ] Use parallel processing

### 2. Memory Optimization

#### Object Pooling
```java
public class ObjectPool<T> {
    private final Queue<T> pool;
    private final ObjectFactory<T> factory;
    private final int maxSize;
    
    public ObjectPool(ObjectFactory<T> factory, int maxSize) {
        this.factory = factory;
        this.maxSize = maxSize;
        this.pool = new LinkedList<>();
    }
    
    public T acquire() {
        T object = pool.poll();
        if (object == null) {
            object = factory.create();
        }
        return object;
    }
    
    public void release(T object) {
        if (pool.size() < maxSize) {
            factory.reset(object);
            pool.offer(object);
        }
    }
}

// Usage
ObjectPool<StringBuilder> pool = new ObjectPool<>(
    new ObjectFactory<StringBuilder>() {
        @Override
        public StringBuilder create() {
            return new StringBuilder();
        }
        
        @Override
        public void reset(StringBuilder sb) {
            sb.setLength(0);
        }
    },
    100
);

StringBuilder sb = pool.acquire();
sb.append("Hello");
pool.release(sb);
```

#### Memory Leak Prevention
```java
// Potential memory leak
public class Cache {
    private Map<String, byte[]> cache = new HashMap<>();
    
    public void put(String key, byte[] value) {
        cache.put(key, value);
    }
}

// Fixed with WeakHashMap
public class CacheFixed {
    private Map<String, byte[]> cache = new WeakHashMap<>();
    
    public void put(String key, byte[] value) {
        cache.put(key, value);
    }
}

// Fixed with size limit
public class CacheLimited {
    private final int maxSize;
    private Map<String, byte[]> cache = new LinkedHashMap<String, byte[]>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > maxSize;
        }
    };
    
    public CacheLimited(int maxSize) {
        this.maxSize = maxSize;
    }
    
    public void put(String key, byte[] value) {
        cache.put(key, value);
    }
}
```

### 3. String Optimization

#### String Building
```java
// Inefficient - creates multiple String objects
String result = "";
for (int i = 0; i < 1000; i++) {
    result += "Item " + i + "\n";
}

// Efficient - uses StringBuilder
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append("Item ").append(i).append("\n");
}
String result = sb.toString();

// Most efficient - pre-allocate capacity
StringBuilder sb = new StringBuilder(50000);
for (int i = 0; i < 1000; i++) {
    sb.append("Item ").append(i).append("\n");
}
String result = sb.toString();
```

#### String Interning
```java
// Careful with string interning
String s1 = new String("hello").intern();
String s2 = "hello";
System.out.println(s1 == s2); // true

// Use for frequently used strings
private static final String COMMON_VALUE = "ACTIVE".intern();
```

### 4. Collection Optimization

#### Choosing Right Collections
```java
// ArrayList - fast random access
List<String> list = new ArrayList<>();
list.add("item");
String item = list.get(0); // O(1)

// LinkedList - fast insertion/deletion at ends
Deque<String> deque = new LinkedList<>();
deque.addFirst("item");
deque.removeFirst(); // O(1)

// HashMap - fast lookup
Map<String, Integer> map = new HashMap<>();
map.put("key", 1);
Integer value = map.get("key"); // O(1)

// TreeMap - sorted with log(n) operations
Map<String, Integer> sorted = new TreeMap<>();
sorted.put("key", 1);
Integer value = sorted.get("key"); // O(log n)

// HashSet - fast membership testing
Set<String> set = new HashSet<>();
set.add("item");
boolean contains = set.contains("item"); // O(1)
```

#### Capacity Initialization
```java
// Inefficient - multiple resizes
List<String> list = new ArrayList<>();
for (int i = 0; i < 10000; i++) {
    list.add("item " + i);
}

// Efficient - pre-allocate capacity
List<String> list = new ArrayList<>(10000);
for (int i = 0; i < 10000; i++) {
    list.add("item " + i);
}

// For HashMap
Map<String, Integer> map = new HashMap<>(10000 / 0.75f);
```

---

## ⚡ CONCURRENCY OPTIMIZATION

### 1. Lock Optimization

#### Lock Granularity
```java
// Coarse-grained locking - simple but slow
public class CoarseGrainedCounter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
}

// Fine-grained locking - complex but faster
public class FineGrainedCounter {
    private int[] counters = new int[16];
    
    public void increment() {
        int index = getIndex();
        synchronized (counters) {
            counters[index]++;
        }
    }
    
    public int getCount() {
        int sum = 0;
        synchronized (counters) {
            for (int count : counters) {
                sum += count;
            }
        }
        return sum;
    }
    
    private int getIndex() {
        return (int) (Thread.currentThread().getId() % counters.length);
    }
}

// Lock-free using AtomicInteger
public class LockFreeCounter {
    private AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();
    }
    
    public int getCount() {
        return count.get();
    }
}
```

#### Read-Write Locks
```java
public class ReadWriteCache<K, V> {
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

### 2. Parallel Processing

#### Parallel Streams
```java
// Sequential - slower for large datasets
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
int sum = numbers.stream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * n)
    .reduce(0, Integer::sum);

// Parallel - faster for large datasets
int sum = numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * n)
    .reduce(0, Integer::sum);
```

#### Thread Pools
```java
// Fixed thread pool
ExecutorService executor = Executors.newFixedThreadPool(4);
for (int i = 0; i < 100; i++) {
    executor.submit(() -> {
        // Task
    });
}
executor.shutdown();

// Work-stealing pool (better for recursive tasks)
ExecutorService executor = Executors.newWorkStealingPool();
for (int i = 0; i < 100; i++) {
    executor.submit(() -> {
        // Task
    });
}
executor.shutdown();
```

---

## 💾 DATABASE OPTIMIZATION

### 1. Query Optimization

#### Indexing Strategy
```java
// Without index - full table scan
SELECT * FROM users WHERE email = 'user@example.com';

// With index - fast lookup
CREATE INDEX idx_email ON users(email);
SELECT * FROM users WHERE email = 'user@example.com';

// Composite index
CREATE INDEX idx_user_status ON users(status, created_date);
SELECT * FROM users WHERE status = 'ACTIVE' AND created_date > '2024-01-01';
```

#### Query Optimization
```java
// N+1 query problem
List<users> users = userRepository.findAll();
for (User user : users) {
    List<Order> orders = orderRepository.findByUserId(user.getId()); // N queries
}

// Fixed with JOIN
List<User> users = userRepository.findAllWithOrders(); // 1 query with JOIN

// Or with batch loading
List<User> users = userRepository.findAll();
Map<Long, List<Order>> ordersByUser = orderRepository.findByUserIds(
    users.stream().map(User::getId).collect(Collectors.toList())
);
```

### 2. Connection Pooling

```java
// HikariCP configuration
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
config.setUsername("user");
config.setPassword("password");
config.setMaximumPoolSize(20);
config.setMinimumIdle(5);
config.setConnectionTimeout(30000);
config.setIdleTimeout(600000);
config.setMaxLifetime(1800000);

HikariDataSource dataSource = new HikariDataSource(config);
```

---

## 🌐 PLATFORM OPTIMIZATION

### 1. Caching Strategy

#### Multi-Level Caching
```java
public class MultiLevelCache<K, V> {
    private final Map<K, V> l1Cache = new ConcurrentHashMap<>();
    private final Map<K, V> l2Cache = new ConcurrentHashMap<>();
    private final DataLoader<K, V> loader;
    
    public V get(K key) {
        // L1 cache
        V value = l1Cache.get(key);
        if (value != null) return value;
        
        // L2 cache
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

#### Cache Invalidation
```java
public class CacheWithExpiration<K, V> {
    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;
    
    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }
    
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) return null;
        
        if (System.currentTimeMillis() - entry.timestamp > ttlMillis) {
            cache.remove(key);
            return null;
        }
        
        return entry.value;
    }
    
    private static class CacheEntry<V> {
        final V value;
        final long timestamp;
        
        CacheEntry(V value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }
}
```

### 2. Compression

```java
public class CompressionUtil {
    public static byte[] compress(String data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
            gzip.write(data.getBytes(StandardCharsets.UTF_8));
        }
        return baos.toByteArray();
    }
    
    public static String decompress(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try (GZIPInputStream gzip = new GZIPInputStream(bais)) {
            return new String(gzip.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
```

---

## 📊 MONITORING & PROFILING

### 1. Performance Metrics

```java
public class PerformanceMonitor {
    private final Map<String, PerformanceMetric> metrics = new ConcurrentHashMap<>();
    
    public void recordOperation(String name, long durationMillis) {
        metrics.computeIfAbsent(name, k -> new PerformanceMetric())
            .record(durationMillis);
    }
    
    public void printReport() {
        metrics.forEach((name, metric) -> {
            System.out.printf("%s: avg=%.2fms, min=%dms, max=%dms, count=%d%n",
                name, metric.getAverage(), metric.getMin(), metric.getMax(), metric.getCount());
        });
    }
    
    private static class PerformanceMetric {
        private long totalTime = 0;
        private long minTime = Long.MAX_VALUE;
        private long maxTime = 0;
        private long count = 0;
        
        synchronized void record(long duration) {
            totalTime += duration;
            minTime = Math.min(minTime, duration);
            maxTime = Math.max(maxTime, duration);
            count++;
        }
        
        double getAverage() {
            return count == 0 ? 0 : (double) totalTime / count;
        }
        
        long getMin() {
            return minTime == Long.MAX_VALUE ? 0 : minTime;
        }
        
        long getMax() {
            return maxTime;
        }
        
        long getCount() {
            return count;
        }
    }
}
```

### 2. JVM Profiling

```java
// Using JFR (Java Flight Recorder)
// Start with: java -XX:+UnlockCommercialFeatures -XX:+FlightRecorder ...

// Programmatic profiling
public class JFRProfiler {
    public static void startRecording(String filename) throws Exception {
        Recording recording = new Recording();
        recording.start();
        
        // Run your code
        
        recording.stop();
        recording.dumpToFile(Paths.get(filename));
    }
}
```

---

## 🎯 OPTIMIZATION CHECKLIST

### Code Level
- [ ] Use appropriate algorithms (O(n) vs O(n²))
- [ ] Optimize hot paths
- [ ] Use object pooling for frequently created objects
- [ ] Minimize string concatenation
- [ ] Choose right data structures
- [ ] Reduce memory allocations
- [ ] Use primitive types when possible
- [ ] Avoid unnecessary boxing/unboxing

### Concurrency Level
- [ ] Use fine-grained locking
- [ ] Consider lock-free algorithms
- [ ] Use thread pools appropriately
- [ ] Avoid deadlocks
- [ ] Use concurrent collections
- [ ] Minimize lock contention

### Database Level
- [ ] Create appropriate indexes
- [ ] Optimize queries
- [ ] Use connection pooling
- [ ] Batch operations
- [ ] Avoid N+1 queries
- [ ] Use prepared statements
- [ ] Monitor slow queries

### Platform Level
- [ ] Implement caching
- [ ] Use compression
- [ ] Optimize serialization
- [ ] Monitor performance
- [ ] Profile regularly
- [ ] Load test
- [ ] Optimize infrastructure

---

## 📄 Document Information

| Property | Value |
|----------|-------|
| **Document Type** | Advanced Optimization Guide |
| **Version** | 1.0 |
| **Created** | 2024 |
| **Status** | Active |
| **Focus** | Performance |

---

**Java Master Lab - Advanced Optimization Guide**

*Comprehensive Performance Optimization Strategies*

**Status: Active | Focus: Performance Excellence | Impact: High**

---

*Optimize for excellence!* 🚀