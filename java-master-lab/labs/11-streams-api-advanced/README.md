# Lab 11: Streams API (Advanced)

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Intermediate-Advanced |
| **Estimated Time** | 5 hours |
| **Real-World Context** | Building a data processing pipeline system |
| **Prerequisites** | Lab 10: Functional Programming |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Master advanced stream operations** including intermediate and terminal operations
2. **Implement custom collectors** for specialized data aggregation
3. **Use parallel streams** effectively for performance optimization
4. **Optimize stream performance** and understand trade-offs
5. **Process complex data transformations** with streams
6. **Build a data processing pipeline system** with advanced stream operations

## 📚 Prerequisites

- Lab 10: Functional Programming completed
- Understanding of lambda expressions
- Knowledge of functional interfaces
- Familiarity with basic streams

## 🧠 Concept Theory

### 1. Advanced Intermediate Operations

Stream operations that transform data:

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Filter with complex conditions
List<Integer> filtered = numbers.stream()
    .filter(n -> n > 3 && n < 8)
    .collect(Collectors.toList());

// Map with transformation
List<String> mapped = numbers.stream()
    .map(n -> "Number: " + n)
    .collect(Collectors.toList());

// FlatMap for nested structures
List<List<Integer>> nested = Arrays.asList(
    Arrays.asList(1, 2),
    Arrays.asList(3, 4),
    Arrays.asList(5, 6)
);

List<Integer> flattened = nested.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());

// Distinct removes duplicates
List<Integer> distinct = Arrays.asList(1, 2, 2, 3, 3, 3)
    .stream()
    .distinct()
    .collect(Collectors.toList());

// Sorted with custom comparator
List<Integer> sorted = numbers.stream()
    .sorted(Comparator.reverseOrder())
    .collect(Collectors.toList());

// Peek for debugging
List<Integer> peeked = numbers.stream()
    .peek(n -> System.out.println("Processing: " + n))
    .filter(n -> n > 5)
    .collect(Collectors.toList());

// Skip and limit for pagination
List<Integer> paginated = numbers.stream()
    .skip(3)
    .limit(4)
    .collect(Collectors.toList());
```

### 2. Advanced Terminal Operations

Operations that produce final results:

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Collect to different types
List<Integer> list = numbers.stream()
    .collect(Collectors.toList());

Set<Integer> set = numbers.stream()
    .collect(Collectors.toSet());

Map<Integer, Integer> map = numbers.stream()
    .collect(Collectors.toMap(n -> n, n -> n * 2));

// Reduce for aggregation
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);

int product = numbers.stream()
    .reduce(1, (a, b) -> a * b);

// Matching operations
boolean anyEven = numbers.stream()
    .anyMatch(n -> n % 2 == 0);

boolean allPositive = numbers.stream()
    .allMatch(n -> n > 0);

boolean noneNegative = numbers.stream()
    .noneMatch(n -> n < 0);

// Finding operations
Optional<Integer> first = numbers.stream()
    .findFirst();

Optional<Integer> any = numbers.stream()
    .findAny();

// Count and statistics
long count = numbers.stream()
    .count();

IntSummaryStatistics stats = numbers.stream()
    .mapToInt(Integer::intValue)
    .summaryStatistics();

System.out.println("Count: " + stats.getCount());
System.out.println("Sum: " + stats.getSum());
System.out.println("Average: " + stats.getAverage());
System.out.println("Min: " + stats.getMin());
System.out.println("Max: " + stats.getMax());
```

### 3. Custom Collectors

Creating specialized collectors:

```java
// Grouping by category
List<Person> people = Arrays.asList(
    new Person("John", 30),
    new Person("Jane", 25),
    new Person("Bob", 30)
);

Map<Integer, List<Person>> byAge = people.stream()
    .collect(Collectors.groupingBy(Person::getAge));

// Partitioning by condition
Map<Boolean, List<Integer>> partition = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));

// Joining strings
String joined = people.stream()
    .map(Person::getName)
    .collect(Collectors.joining(", "));

// Custom collector
Collector<Integer, ?, Integer> sumCollector = 
    Collector.of(
        () -> new int[1],
        (acc, n) -> acc[0] += n,
        (acc1, acc2) -> {
            acc1[0] += acc2[0];
            return acc1;
        },
        acc -> acc[0]
    );

int customSum = numbers.stream()
    .collect(sumCollector);

// Grouping with custom collector
Map<Integer, String> groupedNames = people.stream()
    .collect(Collectors.groupingBy(
        Person::getAge,
        Collectors.mapping(Person::getName, Collectors.joining(", "))
    ));
```

### 4. Parallel Streams

Processing data in parallel:

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Sequential stream
long seqStart = System.currentTimeMillis();
int seqSum = numbers.stream()
    .map(n -> expensiveOperation(n))
    .reduce(0, Integer::sum);
long seqTime = System.currentTimeMillis() - seqStart;

// Parallel stream
long parStart = System.currentTimeMillis();
int parSum = numbers.parallelStream()
    .map(n -> expensiveOperation(n))
    .reduce(0, Integer::sum);
long parTime = System.currentTimeMillis() - parStart;

System.out.println("Sequential: " + seqTime + "ms");
System.out.println("Parallel: " + parTime + "ms");

// Converting to parallel
int result = numbers.stream()
    .parallel()
    .filter(n -> n > 5)
    .map(n -> n * 2)
    .reduce(0, Integer::sum);

// Sequential again
int result2 = numbers.stream()
    .parallel()
    .filter(n -> n > 5)
    .sequential()
    .map(n -> n * 2)
    .reduce(0, Integer::sum);
```

### 5. Stream Performance Optimization

Optimizing stream operations:

```java
// ❌ Bad: Multiple passes
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
long count1 = numbers.stream().filter(n -> n > 2).count();
long count2 = numbers.stream().filter(n -> n < 4).count();

// ✅ Good: Single pass
Map<String, Long> counts = numbers.stream()
    .collect(Collectors.partitioningBy(
        n -> n > 2,
        Collectors.counting()
    ));

// ❌ Bad: Unnecessary intermediate collections
List<Integer> filtered = numbers.stream()
    .filter(n -> n > 2)
    .collect(Collectors.toList());
List<Integer> mapped = filtered.stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());

// ✅ Good: Chain operations
List<Integer> result = numbers.stream()
    .filter(n -> n > 2)
    .map(n -> n * 2)
    .collect(Collectors.toList());

// ❌ Bad: Parallel for small datasets
List<Integer> small = Arrays.asList(1, 2, 3);
int sum = small.parallelStream()
    .reduce(0, Integer::sum);

// ✅ Good: Sequential for small datasets
int sum2 = small.stream()
    .reduce(0, Integer::sum);

// ✅ Good: Parallel for large datasets
List<Integer> large = generateLargeList();
int sum3 = large.parallelStream()
    .reduce(0, Integer::sum);
```

### 6. Stream with Optional

Handling optional values:

```java
List<String> strings = Arrays.asList("hello", "world", "java");

// Optional operations
Optional<String> first = strings.stream()
    .findFirst();

String result = first
    .map(String::toUpperCase)
    .orElse("DEFAULT");

// Filtering with Optional
Optional<String> found = strings.stream()
    .filter(s -> s.length() > 5)
    .findFirst();

found.ifPresent(System.out::println);

// FlatMap with Optional
List<Integer> lengths = strings.stream()
    .map(String::length)
    .collect(Collectors.toList());

// Chaining Optional operations
String result2 = strings.stream()
    .filter(s -> s.startsWith("j"))
    .findFirst()
    .map(String::toUpperCase)
    .orElseGet(() -> "NOT FOUND");
```

### 7. Specialized Streams

Streams for primitive types:

```java
// IntStream
IntStream.range(1, 6)
    .forEach(System.out::println);  // 1, 2, 3, 4, 5

// LongStream
LongStream.rangeClosed(1, 5)
    .forEach(System.out::println);  // 1, 2, 3, 4, 5

// DoubleStream
DoubleStream.of(1.1, 2.2, 3.3)
    .forEach(System.out::println);

// Converting to primitive streams
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
int sum = numbers.stream()
    .mapToInt(Integer::intValue)
    .sum();

double average = numbers.stream()
    .mapToDouble(Integer::doubleValue)
    .average()
    .orElse(0);

// Statistics
IntSummaryStatistics stats = IntStream.range(1, 6)
    .summaryStatistics();
```

### 8. Stream Debugging

Debugging stream operations:

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Using peek for debugging
List<Integer> result = numbers.stream()
    .peek(n -> System.out.println("Original: " + n))
    .filter(n -> n > 2)
    .peek(n -> System.out.println("After filter: " + n))
    .map(n -> n * 2)
    .peek(n -> System.out.println("After map: " + n))
    .collect(Collectors.toList());

// Using forEach for side effects
numbers.stream()
    .filter(n -> n > 2)
    .forEach(n -> System.out.println("Processing: " + n));
```

### 9. Complex Stream Transformations

Advanced data transformations:

```java
// Nested grouping
List<Person> people = Arrays.asList(
    new Person("John", 30, "Engineering"),
    new Person("Jane", 25, "Sales"),
    new Person("Bob", 30, "Engineering")
);

Map<String, Map<Integer, List<Person>>> grouped = people.stream()
    .collect(Collectors.groupingBy(
        Person::getDepartment,
        Collectors.groupingBy(Person::getAge)
    ));

// Counting by group
Map<String, Long> counts = people.stream()
    .collect(Collectors.groupingBy(
        Person::getDepartment,
        Collectors.counting()
    ));

// Mapping values in groups
Map<String, List<String>> names = people.stream()
    .collect(Collectors.groupingBy(
        Person::getDepartment,
        Collectors.mapping(Person::getName, Collectors.toList())
    ));
```

### 10. Best Practices

Stream programming guidelines:

```java
// ❌ Bad: Modifying external state
List<Integer> results = new ArrayList<>();
numbers.stream()
    .filter(n -> n > 2)
    .forEach(n -> results.add(n * 2));

// ✅ Good: Using collect
List<Integer> results2 = numbers.stream()
    .filter(n -> n > 2)
    .map(n -> n * 2)
    .collect(Collectors.toList());

// ❌ Bad: Ignoring exceptions
List<Integer> parsed = strings.stream()
    .map(Integer::parseInt)  // May throw
    .collect(Collectors.toList());

// ✅ Good: Handling exceptions
List<Integer> parsed2 = strings.stream()
    .filter(s -> isNumeric(s))
    .map(Integer::parseInt)
    .collect(Collectors.toList());

// ❌ Bad: Reusing streams
Stream<Integer> stream = numbers.stream();
stream.forEach(System.out::println);
stream.forEach(System.out::println);  // ERROR: Stream already consumed

// ✅ Good: Create new streams
numbers.stream().forEach(System.out::println);
numbers.stream().forEach(System.out::println);
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Advanced Stream Operations

**Objective**: Master intermediate and terminal operations

**Acceptance Criteria**:
- [ ] Filter, map, flatMap operations
- [ ] Distinct, sorted, limit operations
- [ ] Reduce and collect operations
- [ ] Matching operations
- [ ] Code compiles without errors

**Instructions**:
1. Create list of complex objects
2. Apply multiple stream operations
3. Chain operations effectively
4. Collect results appropriately
5. Test all operations

### Task 2: Custom Collectors

**Objective**: Implement custom collectors for data aggregation

**Acceptance Criteria**:
- [ ] Custom collector created
- [ ] Grouping operations
- [ ] Partitioning operations
- [ ] Mapping in collectors
- [ ] Works correctly

**Instructions**:
1. Create custom collector
2. Implement grouping
3. Implement partitioning
4. Combine collectors
5. Test with data

### Task 3: Parallel Streams

**Objective**: Use parallel streams for performance

**Acceptance Criteria**:
- [ ] Parallel stream created
- [ ] Performance comparison
- [ ] Correct results
- [ ] Thread safety
- [ ] Appropriate usage

**Instructions**:
1. Create sequential stream
2. Create parallel stream
3. Compare performance
4. Verify correctness
5. Analyze trade-offs

---

## 🎨 Mini-Project: Data Processing Pipeline System

### Project Overview

**Description**: Create a comprehensive data processing pipeline using advanced stream operations.

**Real-World Application**: ETL systems, data analytics, log processing, data transformation.

**Learning Value**: Master advanced streams, custom collectors, and performance optimization.

### Project Requirements

#### Functional Requirements
- [ ] Load data from multiple sources
- [ ] Transform data with streams
- [ ] Filter and aggregate data
- [ ] Generate reports
- [ ] Handle errors gracefully
- [ ] Optimize performance

#### Non-Functional Requirements
- [ ] Clean code structure
- [ ] Proper encapsulation
- [ ] Comprehensive documentation
- [ ] Unit tests
- [ ] Error handling

### Project Structure

```
data-processing-pipeline-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── DataRecord.java
│   │           ├── Pipeline.java
│   │           ├── DataProcessor.java
│   │           ├── Report.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               ├── PipelineTest.java
│               └── DataProcessorTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create DataRecord Class

```java
package com.learning;

import java.time.LocalDate;

/**
 * Represents a data record.
 */
public class DataRecord {
    private String id;
    private String category;
    private double value;
    private LocalDate date;
    private String status;
    
    /**
     * Constructor for DataRecord.
     */
    public DataRecord(String id, String category, double value, 
                      LocalDate date, String status) {
        this.id = id;
        this.category = category;
        this.value = value;
        this.date = date;
        this.status = status;
    }
    
    // Getters
    public String getId() { return id; }
    public String getCategory() { return category; }
    public double getValue() { return value; }
    public LocalDate getDate() { return date; }
    public String getStatus() { return status; }
    
    @Override
    public String toString() {
        return "DataRecord{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", value=" + value +
                ", date=" + date +
                ", status='" + status + '\'' +
                '}';
    }
}
```

#### Step 2: Create Pipeline Class

```java
package com.learning;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Data processing pipeline.
 */
public class Pipeline {
    private List<DataRecord> records;
    
    /**
     * Constructor for Pipeline.
     */
    public Pipeline() {
        this.records = new ArrayList<>();
    }
    
    /**
     * Add record.
     */
    public void addRecord(DataRecord record) {
        if (record != null) {
            records.add(record);
        }
    }
    
    /**
     * Add multiple records.
     */
    public void addRecords(List<DataRecord> newRecords) {
        records.addAll(newRecords);
    }
    
    /**
     * Filter by category.
     */
    public List<DataRecord> filterByCategory(String category) {
        return records.stream()
            .filter(r -> r.getCategory().equals(category))
            .collect(Collectors.toList());
    }
    
    /**
     * Filter by value range.
     */
    public List<DataRecord> filterByValueRange(double min, double max) {
        return records.stream()
            .filter(r -> r.getValue() >= min && r.getValue() <= max)
            .collect(Collectors.toList());
    }
    
    /**
     * Group by category.
     */
    public Map<String, List<DataRecord>> groupByCategory() {
        return records.stream()
            .collect(Collectors.groupingBy(DataRecord::getCategory));
    }
    
    /**
     * Calculate sum by category.
     */
    public Map<String, Double> sumByCategory() {
        return records.stream()
            .collect(Collectors.groupingBy(
                DataRecord::getCategory,
                Collectors.summingDouble(DataRecord::getValue)
            ));
    }
    
    /**
     * Calculate average by category.
     */
    public Map<String, Double> averageByCategory() {
        return records.stream()
            .collect(Collectors.groupingBy(
                DataRecord::getCategory,
                Collectors.averagingDouble(DataRecord::getValue)
            ));
    }
    
    /**
     * Partition by status.
     */
    public Map<Boolean, List<DataRecord>> partitionByStatus(String status) {
        return records.stream()
            .collect(Collectors.partitioningBy(
                r -> r.getStatus().equals(status)
            ));
    }
    
    /**
     * Get top records by value.
     */
    public List<DataRecord> getTopRecords(int limit) {
        return records.stream()
            .sorted(Comparator.comparingDouble(DataRecord::getValue).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * Get statistics.
     */
    public DoubleSummaryStatistics getStatistics() {
        return records.stream()
            .mapToDouble(DataRecord::getValue)
            .summaryStatistics();
    }
    
    /**
     * Get record count.
     */
    public long getRecordCount() {
        return records.size();
    }
}
```

#### Step 3: Create DataProcessor Class

```java
package com.learning;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Processes data with advanced streams.
 */
public class DataProcessor {
    private Pipeline pipeline;
    
    /**
     * Constructor for DataProcessor.
     */
    public DataProcessor(Pipeline pipeline) {
        this.pipeline = pipeline;
    }
    
    /**
     * Process and transform data.
     */
    public Map<String, ProcessingResult> processData() {
        return pipeline.groupByCategory().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new ProcessingResult(
                    entry.getKey(),
                    entry.getValue().size(),
                    entry.getValue().stream()
                        .mapToDouble(DataRecord::getValue)
                        .sum(),
                    entry.getValue().stream()
                        .mapToDouble(DataRecord::getValue)
                        .average()
                        .orElse(0)
                )
            ));
    }
    
    /**
     * Processing result.
     */
    public static class ProcessingResult {
        private String category;
        private int count;
        private double sum;
        private double average;
        
        public ProcessingResult(String category, int count, double sum, double average) {
            this.category = category;
            this.count = count;
            this.sum = sum;
            this.average = average;
        }
        
        public String getCategory() { return category; }
        public int getCount() { return count; }
        public double getSum() { return sum; }
        public double getAverage() { return average; }
        
        @Override
        public String toString() {
            return "ProcessingResult{" +
                    "category='" + category + '\'' +
                    ", count=" + count +
                    ", sum=" + String.format("%.2f", sum) +
                    ", average=" + String.format("%.2f", average) +
                    '}';
        }
    }
}
```

#### Step 4: Create Report Class

```java
package com.learning;

import java.util.*;

/**
 * Generates reports from pipeline data.
 */
public class Report {
    private Pipeline pipeline;
    
    /**
     * Constructor for Report.
     */
    public Report(Pipeline pipeline) {
        this.pipeline = pipeline;
    }
    
    /**
     * Generate summary report.
     */
    public void generateSummaryReport() {
        System.out.println("\n========== SUMMARY REPORT ==========");
        System.out.println("Total Records: " + pipeline.getRecordCount());
        
        DoubleSummaryStatistics stats = pipeline.getStatistics();
        System.out.println("Sum: " + String.format("%.2f", stats.getSum()));
        System.out.println("Average: " + String.format("%.2f", stats.getAverage()));
        System.out.println("Min: " + String.format("%.2f", stats.getMin()));
        System.out.println("Max: " + String.format("%.2f", stats.getMax()));
        System.out.println("====================================\n");
    }
    
    /**
     * Generate category report.
     */
    public void generateCategoryReport() {
        System.out.println("\n========== CATEGORY REPORT ==========");
        
        Map<String, Double> sums = pipeline.sumByCategory();
        Map<String, Double> averages = pipeline.averageByCategory();
        
        sums.forEach((category, sum) -> {
            double avg = averages.get(category);
            System.out.println(category + ": Sum=" + String.format("%.2f", sum) + 
                             ", Avg=" + String.format("%.2f", avg));
        });
        System.out.println("====================================\n");
    }
    
    /**
     * Generate top records report.
     */
    public void generateTopRecordsReport(int limit) {
        System.out.println("\n========== TOP " + limit + " RECORDS ==========");
        pipeline.getTopRecords(limit).forEach(System.out::println);
        System.out.println("====================================\n");
    }
}
```

#### Step 5: Create Main Class

```java
package com.learning;

import java.time.LocalDate;
import java.util.*;

/**
 * Main entry point for Data Processing Pipeline System.
 */
public class Main {
    public static void main(String[] args) {
        // Create pipeline
        Pipeline pipeline = new Pipeline();
        
        // Add sample data
        pipeline.addRecords(generateSampleData());
        
        // Create processor and report
        DataProcessor processor = new DataProcessor(pipeline);
        Report report = new Report(pipeline);
        
        // Generate reports
        report.generateSummaryReport();
        report.generateCategoryReport();
        report.generateTopRecordsReport(5);
        
        // Process data
        System.out.println("\n========== PROCESSING RESULTS ==========");
        processor.processData().forEach((category, result) -> {
            System.out.println(result);
        });
        System.out.println("=======================================\n");
        
        // Filter operations
        System.out.println("========== FILTERED DATA ==========");
        System.out.println("Category 'Sales':");
        pipeline.filterByCategory("Sales").forEach(System.out::println);
        System.out.println("==================================\n");
    }
    
    /**
     * Generate sample data.
     */
    private static List<DataRecord> generateSampleData() {
        List<DataRecord> records = new ArrayList<>();
        
        records.add(new DataRecord("R001", "Sales", 1000, LocalDate.now(), "Active"));
        records.add(new DataRecord("R002", "Engineering", 2000, LocalDate.now(), "Active"));
        records.add(new DataRecord("R003", "Sales", 1500, LocalDate.now(), "Active"));
        records.add(new DataRecord("R004", "Engineering", 2500, LocalDate.now(), "Inactive"));
        records.add(new DataRecord("R005", "Marketing", 800, LocalDate.now(), "Active"));
        records.add(new DataRecord("R006", "Sales", 1200, LocalDate.now(), "Active"));
        records.add(new DataRecord("R007", "Engineering", 2200, LocalDate.now(), "Active"));
        records.add(new DataRecord("R008", "Marketing", 900, LocalDate.now(), "Inactive"));
        
        return records;
    }
}
```

#### Step 6: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Pipeline.
 */
public class PipelineTest {
    
    private Pipeline pipeline;
    
    @BeforeEach
    void setUp() {
        pipeline = new Pipeline();
        pipeline.addRecord(new DataRecord("R1", "Sales", 1000, LocalDate.now(), "Active"));
        pipeline.addRecord(new DataRecord("R2", "Engineering", 2000, LocalDate.now(), "Active"));
        pipeline.addRecord(new DataRecord("R3", "Sales", 1500, LocalDate.now(), "Active"));
    }
    
    @Test
    void testFilterByCategory() {
        List<DataRecord> sales = pipeline.filterByCategory("Sales");
        assertEquals(2, sales.size());
    }
    
    @Test
    void testGroupByCategory() {
        Map<String, List<DataRecord>> grouped = pipeline.groupByCategory();
        assertEquals(2, grouped.size());
        assertEquals(2, grouped.get("Sales").size());
    }
    
    @Test
    void testSumByCategory() {
        Map<String, Double> sums = pipeline.sumByCategory();
        assertEquals(2500, sums.get("Sales"));
        assertEquals(2000, sums.get("Engineering"));
    }
    
    @Test
    void testGetTopRecords() {
        List<DataRecord> top = pipeline.getTopRecords(2);
        assertEquals(2, top.size());
        assertEquals(2000, top.get(0).getValue());
    }
    
    @Test
    void testGetStatistics() {
        DoubleSummaryStatistics stats = pipeline.getStatistics();
        assertEquals(3, stats.getCount());
        assertEquals(4500, stats.getSum());
    }
}
```

### Running the Project

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Run the application
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

---

## 📝 Exercises

### Exercise 1: CSV Data Processing

**Objective**: Process CSV files with streams

**Task Description**:
Create system to read, transform, and analyze CSV data

**Acceptance Criteria**:
- [ ] Read CSV files
- [ ] Parse data
- [ ] Transform records
- [ ] Generate statistics
- [ ] Export results

### Exercise 2: Log File Analysis

**Objective**: Analyze log files with streams

**Task Description**:
Create log analyzer with filtering and aggregation

**Acceptance Criteria**:
- [ ] Parse log entries
- [ ] Filter by level
- [ ] Group by source
- [ ] Generate reports
- [ ] Performance metrics

### Exercise 3: Data Validation Pipeline

**Objective**: Validate data with streams

**Task Description**:
Create validation pipeline with error reporting

**Acceptance Criteria**:
- [ ] Validate records
- [ ] Collect errors
- [ ] Generate reports
- [ ] Handle exceptions
- [ ] Performance optimization

---

## 🧪 Quiz

### Question 1: What is flatMap used for?

A) Filtering elements  
B) Flattening nested structures  
C) Mapping elements  
D) Collecting results  

**Answer**: B) Flattening nested structures

### Question 2: When should you use parallel streams?

A) Always  
B) For small datasets  
C) For large datasets with expensive operations  
D) Never  

**Answer**: C) For large datasets with expensive operations

### Question 3: What does Collectors.groupingBy do?

A) Filters elements  
B) Groups elements by key  
C) Sorts elements  
D) Collects to list  

**Answer**: B) Groups elements by key

### Question 4: Can streams be reused?

A) Yes  
B) No  
C) Only once  
D) Depends on type  

**Answer**: B) No

### Question 5: What is the purpose of peek()?

A) Get first element  
B) Debug stream operations  
C) Filter elements  
D) Collect results  

**Answer**: B) Debug stream operations

---

## 🚀 Advanced Challenge

### Challenge: Complete ETL System

**Difficulty**: Advanced

**Objective**: Build comprehensive ETL system with streams

**Requirements**:
- [ ] Extract from multiple sources
- [ ] Transform with complex logic
- [ ] Load to destinations
- [ ] Error handling
- [ ] Performance optimization
- [ ] Monitoring and logging

---

## 🏆 Best Practices

### Stream Programming

1. **Chain Operations**
   - Combine operations efficiently
   - Avoid intermediate collections
   - Use appropriate collectors

2. **Performance**
   - Use parallel for large datasets
   - Avoid unnecessary operations
   - Profile before optimizing

3. **Readability**
   - Keep operations simple
   - Use method references
   - Document complex logic

---

## 🔗 Next Steps

**Next Lab**: [Lab 12: Concurrency Basics](../12-concurrency-basics/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built data processing pipeline
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 11! 🎉**

You've mastered advanced stream operations and data processing. Ready for concurrency? Move on to [Lab 12: Concurrency Basics](../12-concurrency-basics/README.md).