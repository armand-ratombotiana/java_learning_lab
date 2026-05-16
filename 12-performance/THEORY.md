# Performance Theory

## Table of Contents
1. [JVM Tuning](#jvm-tuning)
2. [Profiling](#profiling)
3. [Benchmarking](#benchmarking)
4. [Memory Management](#memory-management)

---

## 1. JVM Tuning

### 1.1 JVM Memory Areas

```
┌─────────────────────────────────────────────────────────────┐
│                      JVM Process                            │
├─────────────────────────────────────────────────────────────┤
│  Heap Memory                                                │
│  ┌───────────────────┐  ┌───────────────────┐            │
│  │      Young Gen    │  │     Old Gen       │            │
│  │  ┌─────┐ ┌─────┐  │  │  ┌─────────────┐  │            │
│  │  │ Eden │ │S0/S1│  │  │  │             │  │            │
│  │  └─────┘ └─────┘  │  │  │             │  │            │
│  └───────────────────┘  └───────────────────┘            │
├─────────────────────────────────────────────────────────────┤
│  Metaspace (class metadata)                                │
├─────────────────────────────────────────────────────────────┤
│  Stack (per thread)                                         │
├─────────────────────────────────────────────────────────────┤
│  Native Memory (native code, JNI)                           │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Garbage Collectors

**Serial GC:** Single-threaded, for small applications
```bash
java -XX:+UseSerialGC -Xms512m -Xmx512m MyApp
```

**Parallel GC:** Multi-threaded, throughput-focused (default in Java 8)
```bash
java -XX:+UseParallelGC -Xms2g -Xmx2g -XX:ParallelGCThreads=4 MyApp
```

**CMS (Concurrent Mark Sweep):** Low latency, phased
```bash
java -XX:+UseConcMarkSweepGC -Xms2g -Xmx2g MyApp
```

**G1 (Garbage First):** Modern default, region-based
```bash
java -XX:+UseG1GC -Xms2g -Xmx2g -XX:MaxGCPauseMillis=200 MyApp
```

**ZGC (Z Garbage Collector):** Ultra-low latency, scalable
```bash
java -XX:+UseZGC -Xms16g -Xmx16g MyApp
```

**Shenandoah:** Low pause, concurrent
```bash
java -XX:+UseShenandoahGC -Xms2g -Xmx2g MyApp
```

### 1.3 JVM Tuning Flags

```java
// Common JVM arguments
public class JvmFlagsDemo {
    public static void main(String[] args) {
        // Heap size
        // -Xms512m - Minimum heap
        // -Xmx2g - Maximum heap
        // -Xmn256m - Young generation size
        
        // GC selection
        // -XX:+UseG1GC
        // -XX:+UseZGC
        
        // G1 tuning
        // -XX:MaxGCPauseMillis=200
        // -XX:G1HeapRegionSize=8m
        // -XX:InitiatingHeapOccupancyPercent=45
        
        // Thread settings
        // -XX:ParallelGCThreads=4
        // -XX:ConcGCThreads=2
        
        // Logging
        // -Xlog:gc*:file=gc.log:time:filecount=5,filesize=10m
        
        System.out.println("JVM flags set via command line");
    }
}
```

### 1.4 G1GC Tuning

```java
// G1GC specific settings
public class G1TuningDemo {
    public void applyTuning() {
        // Key settings for G1GC:
        
        // 1. Max pause time goal (try to achieve)
        // -XX:MaxGCPauseMillis=200
        
        // 2. Heap region size (1MB to 32MB, power of 2)
        // -XX:G1HeapRegionSize=8m
        
        // 3. Initiating heap occupancy threshold
        // -XX:InitiatingHeapOccupancyPercent=45
        
        // 4. Number of mixed GC after young GC
        // -XX:G1MixedGCLiveThresholdPercent=85
        // -XX:G1HeapWastePercent=5
        
        // 5. Remembered set (RS) logging
        // -XX:G1SummarizeRSetStatsPeriod=1g
    }
}
```

---

## 2. Profiling

### 2.1 Profiling Types

**CPU Profiling:** Identifies methods consuming CPU time
```java
public class CpuProfilerExample {
    public static void main(String[] args) throws Exception {
        // Using async-profiler via command line:
        // java -XX:StartFlightRecording:filename=recording.jfr ...
        
        // Sample code to profile
        for (int i = 0; i < 10000; i++) {
            processData(i);
        }
    }
    
    public static void processData(int i) {
        // Complex processing
        String result = computeHeavy(i);
        saveResult(result);
    }
    
    public static String computeHeavy(int i) {
        // Simulate heavy computation
        String s = "";
        for (int j = 0; j < 1000; j++) {
            s += i * j;
        }
        return s;
    }
    
    public static void saveResult(String result) {
        // Save to memory (mock)
    }
}
```

**Memory Profiling:** Identifies memory allocation and leaks
```java
public class MemoryProfilerExample {
    private static final List<byte[]> cache = new ArrayList<>();
    
    public static void main(String[] args) {
        // Simulate memory leak
        for (int i = 0; i < 1000; i++) {
            cache.add(new byte[1024 * 1024]); // 1MB each
        }
        
        System.out.println("Total memory: " + Runtime.getRuntime().totalMemory());
        System.out.println("Free memory: " + Runtime.getRuntime().freeMemory());
    }
    
    // Using JMap
    // jmap -heap <pid> - heap summary
    // jmap -histo <pid> - histogram of objects
    // jmap -dump:format=b,file=heap.bin <pid> - heap dump
}
```

### 2.2 JFR (Java Flight Recorder)

```java
// Enable JFR via JVM flags
// -XX:StartFlightRecording=dumponexit=true,filename=recording.jfr

// Programmatic JFR
public class JfrExample {
    
    @JfrEvent("my.event")
    static class MyEvent extends Event {
        String message;
    }
    
    public void recordEvent() {
        MyEvent event = new MyEvent();
        event.message = "Processing started";
        event.commit();
    }
}
```

### 2.3 VisualVM and JConsole

```java
// JMX connection for remote monitoring
// java -Dcom.sun.management.jmxremote \
//      -Dcom.sun.management.jmxremote.port=9010 \
//      -Dcom.sun.management.jmxremote.authenticate=false \
//      -Dcom.sun.management.jmxremote.ssl=false \
//      -jar start.jar
```

### 2.4 Profiling Tools Comparison

| Tool | Purpose | Usage |
|------|---------|-------|
| VisualVM | CPU/Memory profiling | GUI, local/remote |
| JProfiler | Commercial, comprehensive | IDE integration |
| YourKit | Commercial, detailed | Deep analysis |
| async-profiler | Low-overhead CPU | Production-safe |
| JFR | Production recording | Flight recording |
| GCViewer | GC logs analysis | Visualize GC |

---

## 3. Benchmarking

### 3.1 JMH (Java Microbenchmark Harness)

```xml
<!-- pom.xml dependency -->
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-core</artifactId>
    <version>1.36</version>
</dependency>
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-generator-annprocess</artifactId>
    <version>1.36</version>
</dependency>
```

```java
// JMH Benchmark Example
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 2)
@Fork(2)
public class StringBuilderBenchmark {
    
    private String data;
    
    @Setup
    public void setup() {
        data = "Hello World ".repeat(100);
    }
    
    @Benchmark
    public String stringBuilder() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append(data);
        }
        return sb.toString();
    }
    
    @Benchmark
    public String stringConcat() {
        String result = "";
        for (int i = 0; i < 100; i++) {
            result += data;
        }
        return result;
    }
    
    public static void main(String[] args) throws Exception {
        new Runner(
            OptionsBuilder.defaultOptions()
                .forks(2)
                .build()
        ).run();
    }
}
```

### 3.2 More Complex Benchmark

```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class CollectionBenchmark {
    
    @State(Scope.Thread)
    public static class MyState {
        public List<Integer> arrayList = new ArrayList<>();
        public List<Integer> linkedList = new LinkedList<>();
        
        @Setup
        public void setup() {
            for (int i = 0; i < 1000; i++) {
                arrayList.add(i);
                linkedList.add(i);
            }
        }
    }
    
    @Benchmark
    public int arrayListGet(MyState state) {
        int sum = 0;
        for (int i = 0; i < 1000; i++) {
            sum += state.arrayList.get(i);
        }
        return sum;
    }
    
    @Benchmark
    public int linkedListGet(MyState state) {
        int sum = 0;
        for (int i = 0; i < 1000; i++) {
            sum += state.linkedList.get(i);
        }
        return sum;
    }
    
    @Benchmark
    public void arrayListAdd(MyState state) {
        for (int i = 0; i < 100; i++) {
            state.arrayList.add(i);
        }
    }
    
    @Benchmark
    public void linkedListAdd(MyState state) {
        for (int i = 0; i < 100; i++) {
            state.linkedList.add(i);
        }
    }
}
```

### 3.3 JMH Options

| Option | Description |
|--------|-------------|
| @Fork | Number of JVM forks |
| @Warmup | Iterations before measurement |
| @Measurement | Iterations for measurement |
| @BenchmarkMode | Throughput, AverageTime, SampleTime, SingleShotTime |
| @OutputTimeUnit | Time unit for output |
| @Threads | Number of threads |

---

## 4. Memory Management

### 4.1 GC Algorithms

**Serial GC:** Stop-the-world, single thread
```bash
java -XX:+UseSerialGC -Xms1g -Xmx1g MyApp
```

**Parallel GC:** Stop-the-world, multiple threads
```bash
java -XX:+UseParallelGC -Xms4g -Xmx4g -XX:ParallelGCThreads=8 MyApp
```

**CMS:** Concurrent, low pause
```bash
java -XX:+UseConcMarkSweepGC -Xms4g -Xmx4g -XX:+UseParNewGC MyApp
```

**G1:** Region-based, predictable pause
```bash
java -XX:+UseG1GC -Xms4g -Xmx4g -XX:MaxGCPauseMillis=200 MyApp
```

**ZGC:** Concurrent, sub-millisecond pause
```bash
java -XX:+UseZGC -Xms16g -Xmx16g MyApp
```

### 4.2 GC Tuning

```java
// Common GC tuning scenarios

// Scenario 1: High throughput batch job
// -XX:+UseParallelGC -Xms4g -Xmx4g -XX:+UseParallelOldGC
// -XX:ParallelGCThreads=8
// -XX:+UseAdaptiveSizePolicy

// Scenario 2: Low latency service
// -XX:+UseG1GC -Xms4g -Xmx4g
// -XX:MaxGCPauseMillis=100
// -XX:G1HeapRegionSize=8m

// Scenario 3: Very low latency
// -XX:+UseZGC -Xms16g -Xmx16g

// G1 specific tuning
// -XX:InitiatingHeapOccupancyPercent=45
// -XX:G1NewSizePercent=20
// -XX:G1MaxNewSizePercent=60
// -XX:G1ReservePercent=10
// -XX:G1HeapWastePercent=5
```

### 4.3 Analyzing GC Logs

```java
// Enable GC logging
// -Xlog:gc*:file=gc.log:time:uptime,level,tags:filecount=10,filesize=100m
// or in Java 9+
// -Xlog:gc*:file=gc.log

// GC log analysis commands
// jstat -gcutil <pid> 1000
// Java 9+: jcmd <pid> GC.heap_info
```

### 4.4 Memory Leak Analysis

```java
// Common memory leak patterns

// 1. Unbounded collections
public class LeakyCache {
    private static final Map<String, Object> cache = new HashMap<>();
    
    public void add(String key, Object value) {
        cache.put(key, value); // Never evicted
    }
}

// Solution: Use WeakHashMap or bounded cache
public class GoodCache {
    private static final int MAX_SIZE = 1000;
    private final Map<String, Object> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Entry eldest) {
            return size() > MAX_SIZE;
        }
    };
}

// 2. Unclosed resources
public class ResourceLeak {
    public void process() throws Exception {
        InputStream in = new FileInputStream("file.txt");
        // Should use try-with-resources
    }
}

// 3. Static references
public class StaticReference {
    private static final List<Listener> listeners = new ArrayList<>();
    
    public static void register(Listener l) {
        listeners.add(l); // Never removed
    }
}
```

### 4.5 OutOfMemoryError Analysis

```java
// Different OOM scenarios

// 1. Java heap space
// java.lang.OutOfMemoryError: Java heap space
// Solution: Increase -Xmx, fix memory leak, optimize memory usage

// 2. GC overhead limit exceeded
// java.lang.OutOfMemoryError: GC overhead limit exceeded
// Solution: Fix memory leak, increase heap, reduce GC pressure

// 3. Metaspace
// java.lang.OutOfMemoryError: Metaspace
// Solution: Increase -XX:MaxMetaspaceSize, reduce class loading

// 4. Unable to create native thread
// java.lang.OutOfMemoryError: Unable to create new native thread
// Solution: Reduce thread stack size -Xss, reduce number of threads

// 5. Direct buffer memory
// java.lang.OutOfMemoryError: Direct buffer memory
// Solution: Reduce -XX:MaxDirectMemorySize, release buffers properly
```

---

## Key Concepts Summary

| Area | Key Concepts |
|------|-------------|
| JVM Tuning | Heap sizes, GC selection, tuning flags |
| Profiling | CPU, memory, JFR, JVisualVM |
| Benchmarking | JMH, benchmarks, modes |
| Memory | GC algorithms, tuning, leak analysis |