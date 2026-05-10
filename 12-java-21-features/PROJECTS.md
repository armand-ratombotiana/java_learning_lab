# Java 21 Features Module - PROJECTS.md

---

# Mini-Project: Virtual Threads Demo

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Virtual Threads, Sequenced Collections, Pattern Matching for Switch, Record Patterns, String Templates (Preview)

This project demonstrates key Java 21 features through practical examples.

---

## Project Structure

```
12-java-21-features/src/main/java/com/learning/
├── Main.java
├── virtualthreads/
│   ├── VirtualThreadDemo.java
│   ├── ThreadPoolExamples.java
│   └── WebServerDemo.java
├── collections/
│   ├── SequencedCollectionsDemo.java
│   └── CollectionFactories.java
├── patternmatching/
│   ├── PatternMatchingDemo.java
│   └── SwitchEnhancements.java
└── records/
    └── RecordPatternsDemo.java
```

---

## Step 1: Virtual Threads Basics

```java
// virtualthreads/VirtualThreadDemo.java
package com.learning.virtualthreads;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class VirtualThreadDemo {
    
    public static void main(String[] args) throws Exception {
        createVirtualThreads();
        virtualThreadVsPlatformThread();
        structuredConcurrency();
    }
    
    private static void createVirtualThreads() throws Exception {
        System.out.println("=== Creating Virtual Threads ===");
        
        Thread virtualThread = Thread.ofVirtual().start(() -> {
            System.out.println("Running in virtual thread: " + 
                Thread.currentThread());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        virtualThread.join();
        System.out.println("Virtual thread completed");
    }
    
    private static void virtualThreadVsPlatformThread() throws Exception {
        System.out.println("\n=== Virtual vs Platform Threads ===");
        
        int iterations = 1000;
        
        long startV = System.nanoTime();
        ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
        IntStream.range(0, iterations).forEach(i -> 
            virtualExecutor.submit(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        virtualExecutor.shutdown();
        virtualExecutor.awaitTermination(1, TimeUnit.MINUTES);
        long timeV = System.nanoTime() - startV;
        
        long startP = System.nanoTime();
        ExecutorService platformExecutor = Executors.newFixedThreadPool(10);
        IntStream.range(0, iterations).forEach(i -> 
            platformExecutor.submit(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        platformExecutor.shutdown();
        platformExecutor.awaitTermination(1, TimeUnit.MINUTES);
        long timeP = System.nanoTime() - startP;
        
        System.out.println("Virtual threads: " + timeV / 1_000_000 + "ms");
        System.out.println("Platform threads: " + timeP / 1_000_000 + "ms");
        System.out.println("Speedup: " + (double) timeP / timeV + "x");
    }
    
    private static void structuredConcurrency() throws Exception {
        System.out.println("\n=== Structured Concurrency ===");
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var future1 = executor.submit(() -> {
                Thread.sleep(100);
                return "Result 1";
            });
            
            var future2 = executor.submit(() -> {
                Thread.sleep(50);
                return "Result 2";
            });
            
            String r1 = future1.get();
            String r2 = future2.get();
            
            System.out.println("Results: " + r1 + ", " + r2);
        }
    }
}
```

---

## Step 2: Web Server Demo with Virtual Threads

```java
// virtualthreads/WebServerDemo.java
package com.learning.virtualthreads;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class WebServerDemo {
    private static final int PORT = 8080;
    private static final ExecutorService EXECUTOR = 
        Executors.newVirtualThreadPerTaskExecutor();
    
    public static void main(String[] args) throws IOException {
        System.out.println("Starting web server on port " + PORT);
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                EXECUTOR.submit(() -> handleRequest(clientSocket));
            }
        }
    }
    
    private static void handleRequest(Socket clientSocket) {
        try (clientSocket;
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(
                clientSocket.getOutputStream())) {
            
            String requestLine = reader.readLine();
            if (requestLine == null) return;
            
            System.out.println("Request: " + requestLine);
            
            String[] parts = requestLine.split(" ");
            String path = parts.length > 1 ? parts[1] : "/";
            
            if (path.equals("/health")) {
                writer.println("HTTP/1.1 200 OK");
                writer.println("Content-Type: application/json");
                writer.println();
                writer.println("{\"status\":\"ok\"}");
            } else {
                writer.println("HTTP/1.1 200 OK");
                writer.println("Content-Type: text/html");
                writer.println();
                writer.println("<html><body><h1>Hello from Virtual Thread!</h1></body></html>");
            }
            
        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
        }
    }
    
    public static void stop() {
        EXECUTOR.shutdown();
    }
}
```

---

## Step 3: Sequenced Collections

```java
// collections/SequencedCollectionsDemo.java
package com.learning.collections;

import java.util.*;

public class SequencedCollectionsDemo {
    
    public static void main(String[] args) {
        demonstrateSequencedSet();
        demonstrateSequencedMap();
        demonstrateReversed();
    }
    
    private static void demonstrateSequencedSet() {
        System.out.println("=== SequencedSet ===");
        
        SequencedSet<String> set = new LinkedHashSet<>();
        set.add("first");
        set.add("second");
        set.add("third");
        
        System.out.println("First: " + set.getFirst());
        System.out.println("Last: " + set.getLast());
        
        set.addFirst("zero");
        set.addLast("fourth");
        
        System.out.println("After adding first/last: " + set);
        
        List<String> reversed = set.reversed().stream().toList();
        System.out.println("Reversed: " + reversed);
    }
    
    private static void demonstrateSequencedMap() {
        System.out.println("\n=== SequencedMap ===");
        
        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        
        System.out.println("First key: " + map.firstKey());
        System.out.println("Last key: " + map.lastKey());
        
        map.putFirst("zero", 0);
        map.putLast("four", 4);
        
        System.out.println("After adding first/last: " + map);
        
        map.reversed().forEach((k, v) -> 
            System.out.println(k + "=" + v));
    }
    
    private static void demonstrateReversed() {
        System.out.println("\n=== Reversed View ===");
        
        SequencedCollection<Integer> seq = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        
        System.out.println("Original: " + seq);
        System.out.println("Reversed: " + seq.reversed());
    }
}
```

---

## Step 4: Pattern Matching Enhancements

```java
// patternmatching/PatternMatchingDemo.java
package com.learning.patternmatching;

import java.util.*;

public class PatternMatchingDemo {
    
    public static void main(String[] args) {
        recordPatternMatching();
        typePatternMatching();
        arrayPatternMatching();
    }
    
    private static void recordPatternMatching() {
        System.out.println("=== Record Pattern Matching ===");
        
        Object obj = new Point(10, 20);
        
        if (obj instanceof Point(int x, int y)) {
            System.out.println("Point: x=" + x + ", y=" + y);
        }
        
        Object[] objects = {
            new Point(1, 2),
            new Circle(5),
            "hello",
            42
        };
        
        for (Object o : objects) {
            String result = switch (o) {
                case Point(int x, int y) -> "Point(" + x + ", " + y + ")";
                case Circle(int r) -> "Circle(r=" + r + ")";
                case String s -> "String: " + s;
                case null -> "null";
                default -> "Unknown: " + o.getClass().getSimpleName();
            };
            System.out.println(result);
        }
    }
    
    private static void typePatternMatching() {
        System.out.println("\n=== Type Pattern Matching ===");
        
        List<Object> items = List.of("hello", 42, 3.14, List.of(1, 2, 3));
        
        for (Object item : items) {
            if (item instanceof String s && s.length() > 3) {
                System.out.println("Long string: " + s);
            } else if (item instanceof Integer i && i > 10) {
                System.out.println("Big integer: " + i);
            }
        }
    }
    
    private static void arrayPatternMatching() {
        System.out.println("\n=== Array Pattern Matching ===");
        
        Object[] arr1 = new Object[]{1, 2, 3};
        Object[] arr2 = new Object[]{"a", "b"};
        
        checkArray(arr1);
        checkArray(arr2);
    }
    
    private static void checkArray(Object arr) {
        if (arr instanceof Object[] {int length: int n, Object[] var}) {
            System.out.println("Array length: " + n + ", first: " + var[0]);
        }
    }
    
    record Point(int x, int y) {}
    record Circle(int radius) {}
}
```

---

## Step 5: Switch Enhancements

```java
// patternmatching/SwitchEnhancements.java
package com.learning.patternmatching;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public class SwitchEnhancements {
    
    public static void main(String[] args) {
        switchWithGuards();
        switchWithScope();
        switchWithEnum();
    }
    
    private static void switchWithGuards() {
        System.out.println("=== Switch with Guards ===");
        
        System.out.println(classify(5));
        System.out.println(classify(15));
        System.out.println(classify(-3));
    }
    
    private static String classify(int n) {
        return switch (n) {
            case int i when i < 0 -> "negative";
            case int i when i == 0 -> "zero";
            case int i when i > 0 && i < 10 -> "small positive";
            case int i when i >= 10 -> "large positive";
            default -> "unknown";
        };
    }
    
    private static void switchWithScope() {
        System.out.println("\n=== Switch with Scope ===");
        
        Object obj = "hello";
        
        if (obj instanceof String s) {
            switch (s) {
                case String a when a.length() > 3 -> 
                    System.out.println("Long string");
                case String a -> 
                    System.out.println("Short string");
            }
        }
    }
    
    private static void switchWithEnum() {
        System.out.println("\n=== Switch with Enums ===");
        
        for (DayOfWeek day : DayOfWeek.values()) {
            System.out.println(day + " is " + getDayType(day));
        }
    }
    
    private static String getDayType(DayOfWeek day) {
        return switch (day) {
            case SATURDAY, SUNDAY -> "weekend";
            default -> "weekday";
        };
    }
}
```

---

## Step 6: Record Patterns

```java
// records/RecordPatternsDemo.java
package com.learning.records;

import java.util.*;

public class RecordPatternsDemo {
    
    public static void main(String[] args) {
        basicRecordPattern();
        nestedRecordPattern();
        genericRecordPattern();
    }
    
    private static void basicRecordPattern() {
        System.out.println("=== Basic Record Pattern ===");
        
        Object box = new Box<>("hello");
        
        if (box instanceof Box<String>(String contents)) {
            System.out.println("Box contains: " + contents);
        }
    }
    
    private static void nestedRecordPattern() {
        System.out.println("\n=== Nested Record Pattern ===");
        
        Object nested = new Wrapper<>(new Box<>(new Person("John", 30)));
        
        if (nested instanceof Wrapper<Box<Person>>(
                Box<Person>(Person(String name, int age)))) {
            System.out.println("Nested: name=" + name + ", age=" + age);
        }
    }
    
    private static void genericRecordPattern() {
        System.out.println("\n=== Generic Record Pattern ===");
        
        List<Record<String>> records = List.of(
            new Record<>("A", 1),
            new Record<>("B", 2)
        );
        
        for (Record<String> r : records) {
            if (r instanceof Record<String>(String key, int value)) {
                System.out.println(key + "=" + value);
            }
        }
    }
    
    record Box<T>(T contents) {}
    record Wrapper<T>(T value) {}
    record Person(String name, int age) {}
    record Record<K>(K key, int value) {}
}
```

---

## Build Instructions

```bash
cd 12-java-21-features
javac --release 21 -d target/classes -sourcepath src/main/java \
    src/main/java/com/learning/**/*.java
java --add-opens java.base/java.lang=ALL-UNNAMED \
    -cp target/classes com.learning.virtualthreads.VirtualThreadDemo
```

---

# Real-World Project: Async File Processing with Virtual Threads

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Virtual Threads, Structured Concurrency, Pattern Matching, Sequenced Collections, Records, Parallel Processing

This project implements a high-performance async file processing system using Java 21 virtual threads.

---

## Project Structure

```
12-java-21-features/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── async/
│   │   ├── AsyncFileProcessor.java
│   │   ├── AsyncSearchEngine.java
│   │   └── AsyncPipeline.java
│   ├── model/
│   │   ├── FileResult.java
│   │   ├── SearchCriteria.java
│   │   └── ProcessingReport.java
│   ├── search/
│   │   ├── FileSearcher.java
│   │   └── ContentIndexer.java
│   └── service/
│       └── FileProcessingService.java
└── src/main/resources/
    └── test-files/
```

---

## POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>java-21-features</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>32.1.3-jre</version>
        </dependency>
    </dependencies>
</project>
```

---

## Model Classes

```java
// model/FileResult.java
package com.learning.model;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public record FileResult(
    Path path,
    String fileName,
    long size,
    Duration processingTime,
    List<String> matches,
    boolean success,
    String errorMessage
) {
    public static FileResult success(Path path, List<String> matches, 
                                   Duration processingTime) {
        return new FileResult(
            path,
            path.getFileName().toString(),
            Files.size(path),
            processingTime,
            matches,
            true,
            null
        );
    }
    
    public static FileResult failure(Path path, String error, 
                                    Duration processingTime) {
        return new FileResult(
            path,
            path.getFileName().toString(),
            0,
            processingTime,
            List.of(),
            false,
            error
        );
    }
    
    public double getMatchRate() {
        return size() > 0 ? (double) matches().size() / size() * 100 : 0;
    }
}

// model/SearchCriteria.java
package com.learning.model;

import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public record SearchCriteria(
    String query,
    Pattern regex,
    boolean caseSensitive,
    boolean wholeWord,
    List<String> extensions,
    long maxFileSize
) {
    public static SearchCriteria simple(String query) {
        return new SearchCriteria(
            query,
            Pattern.compile(Pattern.quote(query)),
            false,
            false,
            null,
            Long.MAX_VALUE
        );
    }
    
    public static SearchCriteria regex(String pattern, boolean caseSensitive) {
        int flags = caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
        return new SearchCriteria(
            pattern,
            Pattern.compile(pattern, flags),
            caseSensitive,
            false,
            null,
            Long.MAX_VALUE
        );
    }
    
    public boolean matchesFile(Path path) {
        if (extensions() != null && !extensions().isEmpty()) {
            String ext = getExtension(path);
            if (!extensions().contains(ext)) {
                return false;
            }
        }
        
        long size;
        try {
            size = java.nio.file.Files.size(path);
        } catch (java.io.IOException e) {
            return false;
        }
        
        return size <= maxFileSize();
    }
    
    private String getExtension(Path path) {
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(dot + 1) : "";
    }
}

// model/ProcessingReport.java
package com.learning.model;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ProcessingReport(
    Instant startTime,
    Instant endTime,
    Duration totalDuration,
    int totalFiles,
    int successCount,
    int failureCount,
    long totalBytes,
    Map<String, Integer> filesByExtension,
    List<FileResult> results
) {
    public static ProcessingReport create(List<FileResult> results) {
        Instant now = Instant.now();
        
        int success = (int) results.stream().filter(FileResult::success).count();
        int failure = results.size() - success;
        
        long totalBytes = results.stream()
            .mapToLong(FileResult::size)
            .sum();
        
        Map<String, Integer> byExt = results.stream()
            .map(f -> {
                String name = f.fileName();
                int dot = name.lastIndexOf('.');
                return dot > 0 ? name.substring(dot + 1) : "";
            })
            .collect(java.util.Collectors.groupingBy(
                e -> e,
                java.util.Collectors.counting()))
            .entrySet().stream()
            .filter(e -> !e.getKey().isEmpty())
            .collect(java.util.Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue))
            .entrySet().stream()
            .collect(java.util.Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().intValue()));
        
        return new ProcessingReport(
            now,
            now,
            Duration.ZERO,
            results.size(),
            success,
            failure,
            totalBytes,
            byExt,
            results
        );
    }
    
    public double getSuccessRate() {
        return totalFiles() > 0 ? (double) successCount() / totalFiles() * 100 : 0;
    }
    
    public String summary() {
        return String.format(
            "Processed %d files in %s. Success: %d (%.1f%%), Failure: %d, Total: %d bytes",
            totalFiles(),
            totalDuration(),
            successCount(),
            getSuccessRate(),
            failureCount(),
            totalBytes()
        );
    }
}
```

---

## Async File Processor

```java
// async/AsyncFileProcessor.java
package com.learning.async;

import com.learning.model.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class AsyncFileProcessor {
    private final ExecutorService executor;
    private final SearchCriteria criteria;
    private final List<Path> directories;
    
    public AsyncFileProcessor(SearchCriteria criteria, Path... directories) {
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.criteria = criteria;
        this.directories = List.of(directories);
    }
    
    public AsyncFileProcessor(SearchCriteria criteria, List<Path> directories) {
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.criteria = criteria;
        this.directories = directories;
    }
    
    public CompletableFuture<ProcessingReport> processAsync() {
        return CompletableFuture.supplyAsync(this::process, executor);
    }
    
    public ProcessingReport process() {
        Instant start = Instant.now();
        
        List<Path> files = findFiles();
        
        System.out.println("Found " + files.size() + " files to process");
        
        List<FileResult> results = files.parallelStream()
            .map(this::processFile)
            .toList();
        
        Instant end = Instant.now();
        
        return new ProcessingReport(
            start,
            end,
            Duration.between(start, end),
            results.size(),
            (int) results.stream().filter(FileResult::success).count(),
            (int) results.stream().filter(r -> !r.success()).count(),
            results.stream().mapToLong(FileResult::size).sum(),
            Map.of(),
            results
        );
    }
    
    private List<Path> findFiles() {
        return directories.stream()
            .flatMap(dir -> {
                try {
                    return Files.walk(dir)
                        .filter(Files::isRegularFile)
                        .filter(criteria::matchesFile);
                } catch (IOException e) {
                    return Stream.empty();
                }
            })
            .toList();
    }
    
    private FileResult processFile(Path file) {
        Instant start = Instant.now();
        
        try {
            String content = Files.readString(file);
            List<String> matches = findMatches(content);
            
            Instant end = Instant.now();
            return FileResult.success(file, matches, Duration.between(start, end));
            
        } catch (Exception e) {
            Instant end = Instant.now();
            return FileResult.failure(file, e.getMessage(), 
                Duration.between(start, end));
        }
    }
    
    private List<String> findMatches(String content) {
        var matcher = criteria.regex().matcher(content);
        List<String> matches = new ArrayList<>();
        
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        
        return matches;
    }
    
    public void shutdown() throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    }
}
```

---

## Async Search Engine

```java
// async/AsyncSearchEngine.java
package com.learning.async;

import com.learning.model.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class AsyncSearchEngine {
    private final ExecutorService executor;
    private final List<AsyncFileProcessor> processors;
    
    public AsyncSearchEngine(int numWorkers) {
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.processors = new ArrayList<>();
    }
    
    public void addProcessor(AsyncFileProcessor processor) {
        processors.add(processor);
    }
    
    public CompletableFuture<ProcessingReport> searchAsync(
            SearchCriteria criteria, Path... directories) {
        
        return CompletableFuture.supplyAsync(() -> {
            AsyncFileProcessor processor = new AsyncFileProcessor(
                criteria, List.of(directories));
            return processor.process();
        }, executor);
    }
    
    public CompletableFuture<ProcessingReport> searchMultipleAsync(
            List<SearchCriteria> criteriaList, 
            List<Path> directories) {
        
        List<CompletableFuture<ProcessingReport>> futures = 
            criteriaList.stream()
                .map(criteria -> CompletableFuture.supplyAsync(() -> {
                    AsyncFileProcessor processor = new AsyncFileProcessor(
                        criteria, directories);
                    return processor.process();
                }, executor))
                .toList();
        
        return futures.stream()
            .reduce(CompletableFuture::allOf)
            .map(cf -> cf.thenApply(v -> {
                return new ProcessingReport(
                    Instant.now(),
                    Instant.now(),
                    Duration.ZERO,
                    0,
                    0,
                    0,
                    0,
                    Map.of(),
                    List.of());
            }))
            .orElse(CompletableFuture.completedFuture(
                new ProcessingReport(
                    Instant.now(),
                    Instant.now(),
                    Duration.ZERO,
                    0,
                    0,
                    0,
                    0,
                    Map.of(),
                    List.of())));
    }
    
    public List<FileResult> searchParallel(
            SearchCriteria criteria, List<Path> files) {
        
        return files.parallelStream()
            .map(this::searchInFile)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }
    
    private Optional<FileResult> searchInFile(Path file) {
        if (!criteria.matchesFile(file)) {
            return Optional.empty();
        }
        
        try {
            Instant start = Instant.now();
            String content = Files.readString(file);
            
            List<String> matches = criteria.regex().matcher(content)
                .results()
                .map(MatchResult::group)
                .toList();
            
            Instant end = Instant.now();
            return Optional.of(FileResult.success(file, matches, 
                Duration.between(start, end)));
            
        } catch (Exception e) {
            return Optional.of(FileResult.failure(file, e.getMessage(), 
                Duration.ZERO));
        }
    }
    
    public void shutdown() throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    }
}
```

---

## Main Application

```java
// Main.java
package com.learning;

import com.learning.async.*;
import com.learning.model.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Java 21 Feature Demo ===\n");
        
        demoVirtualThreads();
        demoSequencedCollections();
        demoPatternMatching();
    }
    
    private static void demoVirtualThreads() throws Exception {
        System.out.println("=== Virtual Thread Processing ===");
        
        SearchCriteria criteria = SearchCriteria.simple("public");
        
        Path tempDir = Files.createTempDirectory("search");
        for (int i = 0; i < 10; i++) {
            Path file = tempDir.resolve("file" + i + ".java");
            Files.writeString(file, "public class Test" + i + " {}");
        }
        
        AsyncFileProcessor processor = new AsyncFileProcessor(criteria, tempDir);
        
        ProcessorReport report = processor.process();
        
        System.out.println(report.summary());
        
        Files.walk(tempDir).forEach(file -> {
            try { Files.deleteIfExists(file); } catch (Exception e) {}
        });
    }
    
    private static void demoSequencedCollections() {
        System.out.println("\n=== Sequenced Collections Demo ===");
        
        SequencedSet<Integer> set = new LinkedHashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        
        System.out.println("First: " + set.getFirst());
        System.out.println("Last: " + set.getLast());
        
        set.addFirst(0);
        set.addLast(4);
        
        System.out.println("Set: " + set);
    }
    
    private static void demoPatternMatching() {
        System.out.println("\n=== Pattern Matching Demo ===");
        
        Object[] objects = {
            new Person("Alice", 30),
            new String[]{"a", "b"},
            42,
            "hello"
        };
        
        for (Object obj : objects) {
            String result = switch (obj) {
                case Person(String name, int age) when age > 25 -> 
                    name + " is older than 25";
                case Person(String name, int age) -> name + " is " + age;
                case String[] strs -> "Array with " + strs.length + " elements";
                case int i -> "Integer: " + i;
                default -> "Unknown";
            };
            System.out.println(result);
        }
    }
    
    record Person(String name, int age) {}
}
```

---

## Build Instructions

```bash
cd 12-java-21-features
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

This project demonstrates advanced Java 21 features including virtual threads for high-concurrency file processing, pattern matching, and sequenced collections.