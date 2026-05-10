# Performance Optimization Projects - Module 12

This module covers Java performance optimization, JMH benchmarks, profiling, memory management, and performance tuning techniques.

## Mini-Project: JMH Benchmarks (2-4 hours)

### Overview
Create comprehensive Java Microbenchmark Harness (JMH) benchmarks to measure and compare performance of different data structures and algorithms.

### Project Structure
```
performance-benchmarks/
├── pom.xml
├── src/main/java/com/learning/perf/
│   ├── PerformanceDemoApplication.java
│   └── model/PerformanceData.java
└── src/main/java/jmh/
    ├── StringBenchmark.java
    ├── CollectionBenchmark.java
    ├── StreamBenchmark.java
    └── ConcurrencyBenchmark.java
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>performance-benchmarks</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <jmh.version>1.37</jmh.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.openjdk.jmh</groupId>
            <artifactId>jmh-core</artifactId>
            <version>${jmh.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjdk.jmh</groupId>
            <artifactId>jmh-generator-annprocess</artifactId>
            <version>${jmh.version}</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <finalName>${project.artifactId}-${project.version}-benchmarks</finalName>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>org.openjdk.jmh.Main</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

#### StringBenchmark
```java
package jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 2)
public class StringBenchmark {
    
    private String sourceString;
    private Pattern pattern;
    private StringBuilder stringBuilder;
    
    @Setup
    public void setup() {
        sourceString = "The quick brown fox jumps over the lazy dog. " +
            "Performance testing is important for Java applications. " +
            "This is a test string for benchmarking string operations. " +
            "Java provides many ways to manipulate strings efficiently.";
        
        pattern = Pattern.compile("\\b\\w+\\b");
        stringBuilder = new StringBuilder();
    }
    
    @Benchmark
    public void stringConcatenation(Blackhole bh) {
        String result = "";
        for (int i = 0; i < 100; i++) {
            result += sourceString.charAt(i % sourceString.length());
        }
        bh.consume(result);
    }
    
    @Benchmark
    public void stringBuilderConcatenation(Blackhole bh) {
        stringBuilder.setLength(0);
        for (int i = 0; i < 100; i++) {
            stringBuilder.append(sourceString.charAt(i % sourceString.length()));
        }
        bh.consume(stringBuilder.toString());
    }
    
    @Benchmark
    public void stringFormat(Blackhole bh) {
        String result = String.format("Value: %d, Name: %s, Price: %.2f", 
            123, "Test", 99.99);
        bh.consume(result);
    }
    
    @Benchmark
    public void stringBuilderFormat(Blackhole bh) {
        StringBuilder sb = new StringBuilder();
        sb.append("Value: ").append(123)
          .append(", Name: ").append("Test")
          .append(", Price: ").append(String.format("%.2f", 99.99));
        bh.consume(sb.toString());
    }
    
    @Benchmark
    public void regexMatching(Blackhole bh) {
        java.util.regex.Matcher matcher = pattern.matcher(sourceString);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        bh.consume(count);
    }
    
    @Benchmark
    public void stringSplit(Blackhole bh) {
        String[] parts = sourceString.split("\\s+");
        bh.consume(parts);
    }
    
    @Benchmark
    public void stringReplace(Blackhole bh) {
        String result = sourceString.replace("Java", "Python")
            .replace("important", "crucial")
            .replace("fast", "quick");
        bh.consume(result);
    }
    
    @Benchmark
    public void stringIsEmpty(Blackhole bh) {
        boolean result = sourceString != null && !sourceString.isEmpty();
        bh.consume(result);
    }
    
    @Benchmark
    public void stringEquals(Blackhole bh) {
        boolean result = sourceString.equals("different string");
        bh.consume(result);
    }
    
    @Benchmark
    public void stringEqualsIgnoreCase(Blackhole bh) {
        boolean result = sourceString.equalsIgnoreCase("DIFFERENT STRING");
        bh.consume(result);
    }
}
```

#### CollectionBenchmark
```java
package jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 2)
public class CollectionBenchmark {
    
    private List<String> arrayList;
    private List<String> linkedList;
    private Map<String, String> hashMap;
    private Map<String, String> linkedHashMap;
    private Map<String, String> treeMap;
    private Map<String, String> concurrentHashMap;
    private Set<String> hashSet;
    
    @Param({"100", "1000", "10000"})
    private int size;
    
    @Setup
    public void setup() {
        arrayList = new ArrayList<>();
        linkedList = new LinkedList<>();
        hashMap = new HashMap<>();
        linkedHashMap = new LinkedHashMap<>();
        treeMap = new TreeMap<>();
        concurrentHashMap = new ConcurrentHashMap<>();
        hashSet = new HashSet<>();
        
        for (int i = 0; i < size; i++) {
            String key = "key" + i;
            String value = "value" + i;
            
            arrayList.add(value);
            linkedList.add(value);
            hashMap.put(key, value);
            linkedHashMap.put(key, value);
            treeMap.put(key, value);
            concurrentHashMap.put(key, value);
            hashSet.add(key);
        }
    }
    
    @Benchmark
    public void arrayListAdd(Blackhole bh) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add("item" + i);
        }
        bh.consume(list);
    }
    
    @Benchmark
    public void linkedListAdd(Blackhole bh) {
        List<String> list = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            list.add("item" + i);
        }
        bh.consume(list);
    }
    
    @Benchmark
    public void arrayListGet(Blackhole bh) {
        for (int i = 0; i < size; i++) {
            bh.consume(arrayList.get(i));
        }
    }
    
    @Benchmark
    public void linkedListGet(Blackhole bh) {
        for (int i = 0; i < size; i++) {
            bh.consume(linkedList.get(i));
        }
    }
    
    @Benchmark
    public void hashMapGet(Blackhole bh) {
        for (int i = 0; i < size; i++) {
            bh.consume(hashMap.get("key" + i));
        }
    }
    
    @Benchmark
    public void treeMapGet(Blackhole bh) {
        for (int i = 0; i < size; i++) {
            bh.consume(treeMap.get("key" + i));
        }
    }
    
    @Benchmark
    public void concurrentHashMapGet(Blackhole bh) {
        for (int i = 0; i < size; i++) {
            bh.consume(concurrentHashMap.get("key" + i));
        }
    }
    
    @Benchmark
    public void hashMapIterate(Blackhole bh) {
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            bh.consume(entry.getKey() + entry.getValue());
        }
    }
    
    @Benchmark
    public void arrayListIterate(Blackhole bh) {
        for (String s : arrayList) {
            bh.consume(s);
        }
    }
    
    @Benchmark
    public void hashSetContains(Blackhole bh) {
        for (int i = 0; i < size; i++) {
            bh.consume(hashSet.contains("key" + i));
        }
    }
}
```

#### StreamBenchmark
```java
package jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 2)
public class StreamBenchmark {
    
    private List<Integer> sourceList;
    private List<String> stringList;
    
    @Param({"100", "1000", "10000"})
    private int size;
    
    @Setup
    public void setup() {
        sourceList = new ArrayList<>();
        stringList = new ArrayList<>();
        
        Random random = new Random(42);
        for (int i = 0; i < size; i++) {
            sourceList.add(random.nextInt(1000));
            stringList.add("item" + i);
        }
    }
    
    @Benchmark
    public void streamForEach(Blackhole bh) {
        sourceList.stream().forEach(bh::consume);
    }
    
    @Benchmark
    public void streamFilter(Blackhole bh) {
        List<Integer> result = sourceList.stream()
            .filter(i -> i > 500)
            .collect(Collectors.toList());
        bh.consume(result);
    }
    
    @Benchmark
    public void streamMap(Blackhole bh) {
        List<Integer> result = sourceList.stream()
            .map(i -> i * 2)
            .collect(Collectors.toList());
        bh.consume(result);
    }
    
    @Benchmark
    public void streamMapToInt(Blackhole bh) {
        int sum = sourceList.stream()
            .mapToInt(Integer::intValue)
            .sum();
        bh.consume(sum);
    }
    
    @Benchmark
    public void streamFilterMap(Blackhole bh) {
        List<Integer> result = sourceList.stream()
            .filter(i -> i > 500)
            .map(i -> i * 2)
            .collect(Collectors.toList());
        bh.consume(result);
    }
    
    @Benchmark
    public void streamReduce(Blackhole bh) {
        Optional<Integer> sum = sourceList.stream()
            .reduce(Integer::sum);
        bh.consume(sum.orElse(0));
    }
    
    @Benchmark
    public void streamCollect(Blackhole bh) {
        Map<Integer, List<Integer>> grouped = sourceList.stream()
            .collect(Collectors.groupingBy(i -> i % 10));
        bh.consume(grouped);
    }
    
    @Benchmark
    public void parallelStream(Blackhole bh) {
        List<Integer> result = sourceList.parallelStream()
            .filter(i -> i > 500)
            .map(i -> i * 2)
            .collect(Collectors.toList());
        bh.consume(result);
    }
    
    @Benchmark
    public void forLoop(Blackhole bh) {
        int sum = 0;
        for (Integer i : sourceList) {
            if (i > 500) {
                sum += i;
            }
        }
        bh.consume(sum);
    }
    
    @Benchmark
    public void streamSorted(Blackhole bh) {
        List<Integer> sorted = sourceList.stream()
            .sorted()
            .collect(Collectors.toList());
        bh.consume(sorted);
    }
    
    @Benchmark
    public void streamDistinct(Blackhole bh) {
        List<Integer> distinct = sourceList.stream()
            .distinct()
            .collect(Collectors.toList());
        bh.consume(distinct);
    }
}
```

#### ConcurrencyBenchmark
```java
package jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.infra.ThreadParams;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 2)
public class ConcurrencyBenchmark {
    
    private AtomicInteger atomicInteger;
    private AtomicLong atomicLong;
    private LongAdder longAdder;
    private AtomicReference<String> atomicReference;
    private volatile int volatileInt;
    private int plainInt;
    
    private ReentrantLock reentrantLock;
    private ReadWriteLock readWriteLock;
    private StampedLock stampedLock;
    private CountDownLatch latch;
    private CyclicBarrier barrier;
    
    @Param({"10000"})
    private int iterations;
    
    @Setup
    public void setup() {
        atomicInteger = new AtomicInteger(0);
        atomicLong = new AtomicLong(0L);
        longAdder = new LongAdder();
        atomicReference = new AtomicReference<>("initial");
        reentrantLock = new ReentrantLock();
        readWriteLock = new ReentrantReadWriteLock();
        stampedLock = new StampedLock();
    }
    
    @Benchmark
    public void atomicIntegerIncrement(Blackhole bh) {
        for (int i = 0; i < iterations; i++) {
            atomicInteger.incrementAndGet();
        }
    }
    
    @Benchmark
    public void atomicLongIncrement(Blackhole bh) {
        for (int i = 0; i < iterations; i++) {
            atomicLong.incrementAndGet();
        }
    }
    
    @Benchmark
    public void longAdderIncrement(Blackhole bh) {
        for (int i = 0; i < iterations; i++) {
            longAdder.increment();
        }
    }
    
    @Benchmark
    public void volatileIncrement(Blackhole bh) {
        for (int i = 0; i < iterations; i++) {
            volatileInt++;
        }
    }
    
    @Benchmark
    public void synchronizedIncrement(Blackhole bh) {
        synchronized (this) {
            for (int i = 0; i < iterations; i++) {
                plainInt++;
            }
        }
    }
    
    @Benchmark
    public void reentrantLockIncrement(Blackhole bh) {
        for (int i = 0; i < iterations; i++) {
            reentrantLock.lock();
            try {
                plainInt++;
            } finally {
                reentrantLock.unlock();
            }
        }
    }
    
    @Benchmark
    public void readWriteLockRead(Blackhole bh) {
        for (int i = 0; i < iterations; i++) {
            readWriteLock.readLock().lock();
            try {
                bh.consume(atomicReference.get());
            } finally {
                readWriteLock.readLock().unlock();
            }
        }
    }
    
    @Benchmark
    public void readWriteLockWrite(Blackhole bh) {
        for (int i = 0; i < iterations / 10; i++) {
            readWriteLock.writeLock().lock();
            try {
                atomicReference.set("value" + i);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }
    }
    
    @Benchmark
    public void stampedLockOptimisticRead(Blackhole bh) {
        for (int i = 0; i < iterations; i++) {
            long stamp = stampedLock.tryOptimisticRead();
            String value = atomicReference.get();
            if (stampedLock.validate(stamp)) {
                bh.consume(value);
            }
        }
    }
    
    @Benchmark
    public void compareAndSet(Blackhole bh) {
        for (int i = 0; i < iterations; i++) {
            int current;
            do {
                current = atomicInteger.get();
            } while (!atomicInteger.compareAndSet(current, current + 1));
        }
    }
    
    @Benchmark
    public void atomicReferenceUpdate(Blackhole bh) {
        for (int i = 0; i < iterations; i++) {
            atomicReference.updateAndGet(s -> s + "x");
        }
    }
}
```

### Build and Run Instructions
```bash
# Build the benchmark jar
cd performance-benchmarks
mvn clean package

# Run all benchmarks
java -jar target/performance-benchmarks-1.0.0-benchmarks.jar

# Run specific benchmark
java -jar target/performance-benchmarks-1.0.0-benchmarks.jar jmh.StringBenchmark

# Run with detailed output
java -jar target/performance-benchmarks-1.0.0-benchmarks.jar jmh.CollectionBenchmark -rf json -rff results.json

# Run with more iterations
java -jar target/performance-benchmarks-1.0.0-benchmarks.jar jmh.StreamBenchmark -i 10 -r 5

# Run with threading
java -jar target/performance-benchmarks-1.0.0-benchmarks.jar jmh.ConcurrencyBenchmark -t 4

# Run specific test with parameters
java -jar target/performance-benchmarks-1.0.0-benchmarks.jar jmh.CollectionBenchmark -p size=10000
```

---

## Real-World Project: Performance Optimization for E-commerce System (8+ hours)

### Overview
Optimize a real e-commerce application with comprehensive performance analysis, caching strategies, database optimization, and async processing.

### Project Structure
```
ecommerce-performance/
├── pom.xml
├── src/main/java/com/learning/ecommerce/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   └── model/
├── src/main/java/com/learning/performance/
│   ├── config/
│   │   ├── CacheConfig.java
│   │   ├── AsyncConfig.java
│   │   └── QueryOptimizationConfig.java
│   ├── interceptor/
│   │   └── PerformanceInterceptor.java
│   ├── aspect/
│   │   └── PerformanceAspect.java
│   └── monitoring/
│       └── MetricsCollector.java
└── src/test/java/com/learning/performance/
    └── LoadTest.java
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>ecommerce-performance</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>com.github.ben-manes.caffeine</groupId>
            <artifactId>caffeine</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

#### Cache Configuration
```java
package com.learning.performance.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
public class CacheConfig implements CachingConfigurer {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "products",
            "categories",
            "orders",
            "customers"
        );
        
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .recordStats()
        );
        
        return cacheManager;
    }
    
    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.setRejectedExecutionHandler(
            new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy()
        );
        executor.initialize();
        return executor;
    }
}
```

#### Product Service with Caching
```java
package com.learning.ecommerce.service;

import com.learning.ecommerce.model.Product;
import com.learning.ecommerce.repository.ProductRepository;
import org.springframework.cache.annotation.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@CacheConfig(cacheNames = "products")
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Cacheable(key = "#id")
    public Optional<Product> findById(Long id) {
        simulateSlowQuery();
        return productRepository.findById(id);
    }
    
    @Cacheable(key = "'all'")
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    @Cacheable(key = "'category:' + #category")
    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    @Cacheable(key = "'sku:' + #sku")
    public Optional<Product> findBySku(String sku) {
        return productRepository.findBySku(sku);
    }
    
    @CachePut(key = "#result.id")
    public Product save(Product product) {
        return productRepository.save(product);
    }
    
    @CacheEvict(key = "#id")
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
    
    @CacheEvict(allEntries = true)
    public void clearCache() {
    }
    
    @Async("asyncExecutor")
    public CompletableFuture<List<Product>> findAllAsync() {
        return CompletableFuture.completedFuture(productRepository.findAll());
    }
    
    @Async("asyncExecutor")
    public CompletableFuture<Product> saveAsync(Product product) {
        return CompletableFuture.completedFuture(productRepository.save(product));
    }
    
    @Caching(evict = {
        @CacheEvict(key = "#product.id"),
        @CacheEvict(key = "'sku:' + #product.sku"),
        @CacheEvict(allEntries = true)
    })
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }
    
    private void simulateSlowQuery() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

#### Performance Aspect for AOP
```java
package com.learning.performance.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class PerformanceAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);
    
    @Pointcut("execution(* com.learning.ecommerce.service..*(..))")
    public void serviceMethods() {}
    
    @Pointcut("execution(* com.learning.ecommerce.repository..*(..))")
    public void repositoryMethods() {}
    
    @Around("serviceMethods() || repositoryMethods()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (executionTime > 100) {
                logger.warn("Slow method: {}.{} executed in {} ms",
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    executionTime);
            } else {
                logger.debug("Method: {}.{} executed in {} ms",
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    executionTime);
            }
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Method: {}.{} failed after {} ms",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                executionTime);
            throw e;
        }
    }
    
    @Around("execution(* com.learning.ecommerce.controller..*(..))")
    public Object measureControllerTime(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) 
            RequestContextHolder.currentRequestAttributes())
            .getRequest();
        
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;
        
        logger.info("Request: {} {} completed in {} ms",
            request.getMethod(),
            request.getRequestURI(),
            executionTime);
        
        request.setAttribute("executionTime", executionTime);
        
        return result;
    }
}
```

#### Database Configuration with HikariCP
```java
package com.learning.performance.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.learning.ecommerce.repository")
public class DatabaseConfig {
    
    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    
    @Value("${spring.datasource.username}")
    private String username;
    
    @Value("${spring.datasource.password}")
    private String password;
    
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(10000);
        config.setMaxLifetime(1800000);
        config.setAutoCommit(true);
        
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        
        return new HikariDataSource(config);
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.learning.ecommerce.model");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(false);
        em.setJpaVendorAdapter(vendorAdapter);
        
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        
        properties.setProperty("hibernate.cache.use_second_level_cache", "true");
        properties.setProperty("hibernate.cache.use_query_cache", "true");
        properties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.jcache.JCacheRegionFactory");
        
        properties.setProperty("hibernate.jdbc.batch_size", "50");
        properties.setProperty("hibernate.order_inserts", "true");
        properties.setProperty("hibernate.order_updates", "true");
        properties.setProperty("hibernate.jdbc.batch_versioned_data", "true");
        
        properties.setProperty("hibernate.generate_statistics", "true");
        
        em.setJpaProperties(properties);
        
        return em;
    }
    
    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
}
```

#### Optimized Repository
```java
package com.learning.ecommerce.repository;

import com.learning.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import static org.hibernate.jpa.HibernateHints.HINT_COMMENT;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @QueryHints({
        @QueryHint(name = HINT_COMMENT, value = "Find by SKU")
    })
    Optional<Product> findBySku(String sku);
    
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.active = true")
    @QueryHints({
        @QueryHint(name = HINT_COMMENT, value = "Find active products by category")
    })
    List<Product> findActiveByCategory(@Param("category") String category);
    
    @Query(value = """
        SELECT p.* FROM products p 
        WHERE p.price BETWEEN :minPrice AND :maxPrice 
        ORDER BY p.price DESC
        """, nativeQuery = true)
    List<Product> findByPriceRangeNative(
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice
    );
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Product> searchByKeyword(@Param("keyword") String keyword);
    
    @Query(value = """
        SELECT p.*, c.name as category_name 
        FROM products p 
        LEFT JOIN categories c ON p.category_id = c.id 
        WHERE p.active = true 
        ORDER BY p.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findAllProductsWithCategory();
}
```

#### Application Properties
```yaml
server:
  port: 8080
  tomcat:
    threads:
      max: 200
      min-spare: 20
    max-connections: 10000
    accept-count: 100

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommerce
    username: postgres
    password: postgres
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 10000
      idle-timeout: 300000
      max-lifetime: 1800000
      pool-name: EcommercePool
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 50
        generate_statistics: true
        format_sql: true
    open-in-view: false

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus,caches
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    org.hibernate.SQL: WARN
    org.hibernate.stat: DEBUG
    com.learning.performance: DEBUG
```

#### Performance Monitoring Service
```java
package com.learning.performance.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsCollector {
    
    private final Counter productFetchCounter;
    private final Counter orderCreatedCounter;
    private final Timer databaseQueryTimer;
    private final Timer cacheHitTimer;
    private final AtomicLong activeUsers;
    private final AtomicLong requestLatency;
    
    public MetricsCollector(MeterRegistry registry) {
        this.productFetchCounter = Counter.builder("products.fetched")
            .description("Number of product fetch operations")
            .register(registry);
        
        this.orderCreatedCounter = Counter.builder("orders.created")
            .description("Number of orders created")
            .register(registry);
        
        this.databaseQueryTimer = Timer.builder("database.query.time")
            .description("Database query execution time")
            .register(registry);
        
        this.cacheHitTimer = Timer.builder("cache.hit.time")
            .description("Cache hit time")
            .register(registry);
        
        this.activeUsers = registry.gauge("active.users", new AtomicLong(0));
        
        this.requestLatency = registry.gauge("request.latency.ms", new AtomicLong(0));
    }
    
    public void recordProductFetch() {
        productFetchCounter.increment();
    }
    
    public void recordOrderCreated() {
        orderCreatedCounter.increment();
    }
    
    public void recordDatabaseQueryTime(long timeMs) {
        databaseQueryTimer.record(timeMs, TimeUnit.MILLISECONDS);
    }
    
    public void recordCacheHit(long timeMs) {
        cacheHitTimer.record(timeMs, TimeUnit.MILLISECONDS);
    }
    
    public void updateActiveUsers(long count) {
        activeUsers.set(count);
    }
    
    public void updateRequestLatency(long latencyMs) {
        requestLatency.set(latencyMs);
    }
}
```

#### Load Test
```java
package com.learning.performance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import java.util.concurrent.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoadTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testConcurrentProductRequests() throws Exception {
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            final int requestNumber = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    mockMvc.perform(get("/api/products/" + (requestNumber % 100)))
                        .andExpect(status().isOk());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    completionLatch.countDown();
                }
            });
        }
        
        startLatch.countDown();
        completionLatch.await(30, TimeUnit.SECONDS);
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
    
    @Test
    void testSustainedLoad() throws Exception {
        long duration = 30000;
        long startTime = System.currentTimeMillis();
        int requestCount = 0;
        
        while (System.currentTimeMillis() - startTime < duration) {
            mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
            requestCount++;
            
            if (requestCount % 100 == 0) {
                Thread.sleep(10);
            }
        }
        
        System.out.println("Completed " + requestCount + " requests in " + duration + "ms");
    }
}
```

### Build and Run Instructions
```bash
# Build the project
cd ecommerce-performance
mvn clean package

# Run the application
java -jar target/ecommerce-performance-1.0.0.jar

# Run load tests
mvn test -Dtest=LoadTest

# Run with profiling
java -jar target/ecommerce-performance-1.0.0.jar \
  -Xmx512m \
  -XX:+UseG1GC \
  -XX:+PrintGCDetails \
  -Xloggc:gc.log

# View metrics
curl http://localhost:8080/actuator/prometheus
curl http://localhost:8080/actuator/metrics

# Check caches
curl http://localhost:8080/actuator/caches

# Connect JConsole
jconsole &
```

### Learning Outcomes
- Write JMH benchmarks for different code patterns
- Implement caching with Caffeine
- Configure HikariCP for optimal database connection pooling
- Use AOP for performance monitoring
- Configure async processing with thread pools
- Monitor with Micrometer and Prometheus
- Perform load testing and profiling