# Performance Exercises

## Exercise 1: JVM Tuning

**Problem:** Configure JVM settings for a high-throughput batch processing application.

**Requirements:**
1. Set appropriate heap sizes
2. Configure parallel GC
3. Optimize young generation
4. Add GC logging

**Solution:**

```java
// JVM options for high-throughput batch processing
/*
# JVM options for batch application
# BatchProcessing.java

// Run with these JVM flags:
// java -Xms4g -Xmx4g \n
// -XX:+UseParallelGC \n
// -XX:ParallelGCThreads=8 \n
// -XX:+UseParallelOldGC \n
// -XX:NewSize=1g -XX:MaxNewSize=2g \n
// -XX:SurvivorRatio=8 \n
// -XX:MaxTenuringThreshold=15 \n
// -XX:+UseLargePages \n
// -XX:PreTouch \n
// -Xlog:gc*:file=gc.log:time,level,tags:filecount=5,filesize=50m \n
// -XX:+PrintGCDetails \n
// -XX:+PrintGCDateStamps \n
// BatchProcessing

public class BatchProcessing {
    
    public static void main(String[] args) {
        // Print JVM info
        System.out.println("Max Memory: " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "MB");
        System.out.println("Total Memory: " + Runtime.getRuntime().totalMemory() / 1024 / 1024 + "MB");
        System.out.println("Free Memory: " + Runtime.getRuntime().freeMemory() / 1024 / 1024 + "MB");
        System.out.println("Available Processors: " + Runtime.getRuntime().availableProcessors());
        
        // Process data
        for (int batch = 0; batch < 100; batch++) {
            processBatch(batch);
        }
    }
    
    private static void processBatch(int batchId) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            items.add("item-" + batchId + "-" + i);
        }
        
        // Heavy processing
        items.parallelStream().forEach(item -> {
            String result = processItem(item);
            storeResult(result);
        });
        
        items.clear();
        
        if (batchId % 10 == 0) {
            System.gc();
        }
    }
    
    private static String processItem(String item) {
        // Simulate processing
        return item.toUpperCase() + "-processed";
    }
    
    private static void storeResult(String result) {
        // Store in memory
    }
}

// G1GC variant for lower latency requirements
/*
java -Xms4g -Xmx4g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:G1HeapRegionSize=16m \
     -XX:InitiatingHeapOccupancyPercent=50 \
     -XX:G1ReservePercent=10 \
     -XX:G1HeapWastePercent=5 \
     -XX:+ParallelRefProcEnabled \
     -Xlog:gc*:file=gc-g1.log:time:filecount=3,filesize=100m \
     BatchProcessing
*/
```

---

## Exercise 2: Profiling CPU Issues

**Problem:** Use JMH to benchmark different string concatenation methods.

**Solution:**

```java
// StringConcatBenchmark.java
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 2)
@Fork(2)
public class StringConcatBenchmark {
    
    private static final int ITERATIONS = 1000;
    private String data;
    
    @Setup
    public void setup() {
        data = "performance testing is important ";
    }
    
    @Benchmark
    public String stringBuilder() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ITERATIONS; i++) {
            sb.append(data);
        }
        return sb.toString();
    }
    
    @Benchmark
    public String stringBuffer() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ITERATIONS; i++) {
            sb.append(data);
        }
        return sb.toString();
    }
    
    @Benchmark
    public String stringConcat() {
        String result = "";
        for (int i = 0; i < ITERATIONS; i++) {
            result += data;
        }
        return result;
    }
    
    @Benchmark
    public String join() {
        String[] arr = new String[ITERATIONS];
        for (int i = 0; i < ITERATIONS; i++) {
            arr[i] = data;
        }
        return String.join("", arr);
    }
    
    @Benchmark
    public String repeat() {
        return data.repeat(ITERATIONS);
    }
}

// Run with:
// java -jar target/benchmarks.jar StringConcatBenchmark -wi 3 -w 2 -i 5 -f 2

// CollectionBenchmark.java
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class CollectionBenchmark {
    
    @State(Scope.Thread)
    public static class MyState {
        public static final int SIZE = 10000;
        public List<Integer> arrayList = new ArrayList<>();
        public LinkedList<Integer> linkedList = new LinkedList<>();
        public ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        
        @Setup
        public void setup() {
            for (int i = 0; i < SIZE; i++) {
                arrayList.add(i);
                linkedList.add(i);
                arrayDeque.add(i);
            }
        }
    }
    
    @Benchmark
    public int arrayListGet(MyState state) {
        int sum = 0;
        for (int i = 0; i < MyState.SIZE; i++) {
            sum += state.arrayList.get(i);
        }
        return sum;
    }
    
    @Benchmark
    public int arrayListIterator(MyState state) {
        int sum = 0;
        for (int i : state.arrayList) {
            sum += i;
        }
        return sum;
    }
    
    @Benchmark
    public int arrayDequeIterate(MyState state) {
        int sum = 0;
        for (int i : state.arrayDeque) {
            sum += i;
        }
        return sum;
    }
}
```

---

## Exercise 3: Memory Profiling

**Problem:** Identify and fix memory issues using profiling tools.

**Solution:**

```java
// MemoryLeakExample.java - Contains intentional memory leaks

public class MemoryLeakExample {
    
    // Leak 1: Static collection
    private static final List<byte[]> LEAKING_LIST = new ArrayList<>();
    
    // Leak 2: Unbounded cache
    private static final Map<String, Object> CACHE = new HashMap<>();
    
    public static void main(String[] args) throws Exception {
        System.out.println("Starting memory leak demonstration");
        
        // Simulate work
        simulateMemoryLeak();
        
        // Print memory stats
        printMemoryStats();
        
        // Force GC
        System.gc();
        Thread.sleep(1000);
        printMemoryStats();
        
        // Create heap dump
        // jmap -dump:format=b,file=heap.bin <pid>
    }
    
    private static void simulateMemoryLeak() throws Exception {
        for (int i = 0; i < 100; i++) {
            // Add to static collection (never removed)
            LEAKING_LIST.add(new byte[1024 * 1024]); // 1MB each
            
            // Add to cache
            CACHE.put("key-" + i, new byte[512 * 1024]); // 512KB each
            
            Thread.sleep(100);
            
            if (i % 10 == 0) {
                printMemoryStats();
            }
        }
    }
    
    private static void printMemoryStats() {
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory() / 1024 / 1024;
        long free = rt.freeMemory() / 1024 / 1024;
        long used = total - free;
        
        System.out.println("Memory: " + used + "MB used, " + free + "MB free, " + total + "MB total");
    }
}

// Fixed version
public class FixedMemoryExample {
    
    // Solution 1: Use WeakHashMap
    private static final Map<String, WeakReference<byte[]>> CACHE = new WeakHashMap<>();
    
    // Solution 2: Bounded cache with eviction
    private static final int MAX_CACHE_SIZE = 100;
    private static final Map<String, Object> CACHE_BOUNDED = new LinkedHashMap<String, Object>(MAX_CACHE_SIZE * 10, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Entry<String, Object> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };
    
    // Solution 3: Use proper resource management
    public void processFile() throws IOException {
        try (InputStream in = new FileInputStream("data.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            // Process
        }
    }
    
    public void processWithCleanup() {
        Connection conn = null;
        try {
            conn = getConnection();
            // work
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // log
                }
            }
        }
    }
}
```

---

## Exercise 4: GC Tuning

**Problem:** Tune G1GC for a low-latency microservice.

**Solution:**

```java
// GCTuningConfiguration.java

public class GCTuningConfig {
    
    /*
    JVM Settings for low-latency microservice:
    
    -Xms2g -Xmx2g
    
    // Use G1GC
    -XX:+UseG1GC
    
    // Target pause time
    -XX:MaxGCPauseMillis=100
    
    // Heap region size (1MB to 32MB)
    -XX:G1HeapRegionSize=8m
    
    // Set young generation size
    -XX:G1NewSizePercent=20
    -XX:G1MaxNewSizePercent=40
    
    // Initiating heap occupancy
    -XX:InitiatingHeapOccupancyPercent=45
    
    // Reserve space
    -XX:G1ReservePercent=10
    
    // GC threads
    -XX:ParallelGCThreads=4
    -XX:ConcGCThreads=2
    
    // Logging
    -Xlog:gc=debug:file=gc.log:time,uptime,level,tags:filecount=5,filesize=20m
    
    // Additional settings
    -XX:+UseStringDeduplication
    -XX:+ParallelRefProcEnabled
    -XX:-O4 // Disable JIT optimization for faster startup
    */
    
    public static void main(String[] args) {
        // Demonstrate GC tuning options
        printGCInfo();
    }
    
    private static void printGCInfo() {
        System.out.println("Garbage Collector: " + 
            ManagementFactory.getGarbageCollectorMXBeans().stream()
                .map(MemoryManagerMXBean::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Unknown"));
        
        System.out.println("Memory Pools:");
        ManagementFactory.getMemoryPoolMXBeans().forEach(pool -> 
            System.out.println("  " + pool.getName() + ": " + pool.getType()));
    }
}

// GC Log Analyzer
public class GCLogAnalyzer {
    
    public static void main(String[] args) throws IOException {
        // Analyze GC logs
        // Download GCViewer from https://github.com/chewiebug/GCViewer
        
        // Or use jstat
        // jstat -gcutil <pid> 1000
        
        // Java 9+ jcmd
        // jcmd <pid> GC.heap_info
        
        // Parse GC logs manually
        parseGCLogs("gc.log");
    }
    
    private static void parseGCLogs(String file) throws IOException {
        // Look for key metrics:
        // - Full GC frequency
        // - Pause times
        // - Heap usage patterns
        // - Allocation rate
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int fullGCCount = 0;
            long totalPause = 0;
            
            while ((line = reader.readLine()) != null) {
                if (line.contains("Full GC")) {
                    fullGCCount++;
                }
                if (line.contains("Pause")) {
                    // Parse pause time
                }
            }
            
            System.out.println("Full GC count: " + fullGCCount);
            System.out.println("Total pause: " + totalPause + "ms");
        }
    }
}
```

---

## Exercise 5: Application Performance Optimization

**Problem:** Optimize a sample application using profiling and tuning.

**Solution:**

```java
// OptimizationExample.java

public class OptimizationExample {
    
    // BEFORE: Naive implementation
    public static List<Order> getOrdersSlow(List<Long> customerIds) {
        List<Order> result = new ArrayList<>();
        for (Long customerId : customerIds) {
            result.addAll(ordersDao.findByCustomerId(customerId));
        }
        return result;
    }
    
    // AFTER: Batch query
    public static List<Order> getOrdersFast(List<Long> customerIds) {
        // Single query with IN clause
        return ordersDao.findByCustomerIdIn(customerIds);
    }
    
    // BEFORE: N+1 query problem
    public static List<CustomerOrder> getCustomerOrdersNPlusOne(List<Customer> customers) {
        List<CustomerOrder> result = new ArrayList<>();
        for (Customer customer : customers) {
            List<Order> orders = orderDao.findByCustomerId(customer.getId());
            for (Order order : orders) {
                result.add(new CustomerOrder(customer, order));
            }
        }
        return result;
    }
    
    // AFTER: JOIN FETCH
    public static List<CustomerOrder> getCustomerOrdersOptimized(List<Customer> customers) {
        // Use JOIN FETCH in JPQL
        return orderDao.findAllWithCustomers();
    }
    
    // Connection Pool Optimization
    @Configuration
    public class DataSourceConfig {
        
        @Bean
        public DataSource dataSource() {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
            config.setUsername("user");
            config.setPassword("password");
            
            // Optimize pool for concurrent requests
            int processors = Runtime.getRuntime().availableProcessors();
            config.setMaximumPoolSize(processors * 2); // e.g., 16
            config.setMinimumIdle(processors); // e.g., 8
            
            // Timeouts
            config.setConnectionTimeout(20000); // 20 sec
            config.setIdleTimeout(300000); // 5 min
            config.setMaxLifetime(900000); // 15 min
            
            // Validation
            config.setConnectionTestQuery("SELECT 1");
            config.setValidationTimeout(5000);
            
            // Performance
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            
            return new HikariDataSource(config);
        }
    }
    
    // Caching layer
    @Service
    public class CachedProductService {
        
        private final Cache<Long, Product> productCache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats()
            .build();
        
        public Product getProduct(Long id) {
            return productCache.get(id, () -> productRepository.findById(id));
        }
        
        public void invalidateProduct(Long id) {
            productCache.invalidate(id);
        }
    }
    
    // Async processing
    @Service
    public class AsyncNotificationService {
        
        @Async
        public Future<String> sendAsync(String message) {
            // Process async
            return new AsyncResult<>(message);
        }
    }
    
    // Connection pooling settings for high concurrency
    // -XX:InitialCodeCacheSize=256m
    // -XX:ReservedCodeCacheSize=512m
    // -XX:+UseCodeCacheFlushing
    // -XX:CompileThreshold=10000
}
```

---

## Summary

| Exercise | Key Concepts |
|----------|-------------|
| 1 | JVM flags, heap tuning, parallel GC |
| 2 | JMH benchmarks, string operations, collections |
| 3 | Memory leaks, profiling, WeakHashMap |
| 4 | G1GC tuning, pause time, heap regions |
| 5 | Query optimization, caching, connection pool |