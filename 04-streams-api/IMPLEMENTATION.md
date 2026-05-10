# Streams API - Implementation Guide

## Module Overview

This module covers Java Stream API for functional-style operations on collections. It demonstrates creating streams, intermediate operations, terminal operations, and collectors from scratch.

---

## Part 1: From-Scratch Java Implementation

### 1.1 Custom Stream Implementation

```java
package com.learning.streams.implementation;

import java.util.*;
import java.util.function.*;

public class CustomStream<T> {
    
    private final Iterator<T> iterator;
    
    private CustomStream(Iterator<T> iterator) {
        this.iterator = iterator;
    }
    
    public static <T> CustomStream<T> of(T... elements) {
        return new CustomStream<>(Arrays.asList(elements).iterator());
    }
    
    public static <T> CustomStream<T> fromIterable(Iterable<T> iterable) {
        return new CustomStream<>(iterable.iterator());
    }
    
    public static <T> CustomStream<T> generate(Supplier<T> supplier) {
        return new CustomStream<>(new Iterator<T>() {
            @Override
            public boolean hasNext() { return true; }
            @Override
            public T next() { return supplier.get(); }
        });
    }
    
    public static <T> CustomStream<T> iterate(T seed, UnaryOperator<T> operator) {
        return new CustomStream<>(new Iterator<T>() {
            private T current = seed;
            private boolean first = true;
            
            @Override
            public boolean hasNext() { return first || current != null; }
            
            @Override
            public T next() {
                if (!first) current = operator.apply(current);
                first = false;
                return current;
            }
        });
    }
    
    // Intermediate operations
    public CustomStream<T> filter(Predicate<T> predicate) {
        return new CustomStream<>(new Iterator<T>() {
            private T nextElement;
            private boolean hasNext;
            
            private void findNext() {
                while (iterator.hasNext()) {
                    T element = iterator.next();
                    if (predicate.test(element)) {
                        nextElement = element;
                        hasNext = true;
                        return;
                    }
                }
                hasNext = false;
            }
            
            @Override
            public boolean hasNext() {
                if (!hasNext) findNext();
                return hasNext;
            }
            
            @Override
            public T next() {
                if (!hasNext) findNext();
                hasNext = false;
                return nextElement;
            }
        });
    }
    
    public <R> CustomStream<R> map(Function<T, R> mapper) {
        return new CustomStream<>(new Iterator<R>() {
            @Override
            public boolean hasNext() { return iterator.hasNext(); }
            
            @Override
            public R next() { return mapper.apply(iterator.next()); }
        });
    }
    
    public <R> CustomStream<R> flatMap(Function<T, Iterable<R>> mapper) {
        return new CustomStream<>(new Iterator<R>() {
            private Iterator<R> currentIterator;
            
            private void advance() {
                while (currentIterator == null || !currentIterator.hasNext()) {
                    if (!iterator.hasNext()) return;
                    currentIterator = mapper.apply(iterator.next()).iterator();
                }
            }
            
            @Override
            public boolean hasNext() {
                advance();
                return currentIterator != null && currentIterator.hasNext();
            }
            
            @Override
            public R next() {
                advance();
                return currentIterator.next();
            }
        });
    }
    
    public CustomStream<T> distinct() {
        return new CustomStream<>(new Iterator<T>() {
            private final Set<T> seen = new HashSet<>();
            private T nextElement;
            private boolean hasNext;
            
            private void findNext() {
                while (iterator.hasNext()) {
                    T element = iterator.next();
                    if (seen.add(element)) {
                        nextElement = element;
                        hasNext = true;
                        return;
                    }
                }
                hasNext = false;
            }
            
            @Override
            public boolean hasNext() {
                if (!hasNext) findNext();
                return hasNext;
            }
            
            @Override
            public T next() {
                if (!hasNext) findNext();
                hasNext = false;
                return nextElement;
            }
        });
    }
    
    public CustomStream<T> limit(long maxSize) {
        return new CustomStream<>(new Iterator<T>() {
            private long count = 0;
            
            @Override
            public boolean hasNext() {
                return count < maxSize && iterator.hasNext();
            }
            
            @Override
            public T next() {
                count++;
                return iterator.next();
            }
        });
    }
    
    public CustomStream<T> skip(long n) {
        long skipped = 0;
        while (skipped < n && iterator.hasNext()) {
            iterator.next();
            skipped++;
        }
        return new CustomStream<>(iterator);
    }
    
    public CustomStream<T> sorted(Comparator<? super T> comparator) {
        List<T> list = toList();
        list.sort(comparator);
        return new CustomStream<>(list.iterator());
    }
    
    public CustomStream<T> peek(Consumer<T> action) {
        return new CustomStream<>(new Iterator<T>() {
            @Override
            public boolean hasNext() { return iterator.hasNext(); }
            
            @Override
            public T next() {
                T element = iterator.next();
                action.accept(element);
                return element;
            }
        });
    }
    
    // Terminal operations
    public void forEach(Consumer<T> action) {
        while (iterator.hasNext()) {
            action.accept(iterator.next());
        }
    }
    
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }
    
    public Set<T> toSet() {
        Set<T> set = new HashSet<>();
        while (iterator.hasNext()) {
            set.add(iterator.next());
        }
        return set;
    }
    
    public <K> Map<K, T> toMap(Function<T, K> keyMapper) {
        Map<K, T> map = new HashMap<>();
        while (iterator.hasNext()) {
            T element = iterator.next();
            map.put(keyMapper.apply(element), element);
        }
        return map;
    }
    
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        T result = identity;
        while (iterator.hasNext()) {
            result = accumulator.apply(result, iterator.next());
        }
        return result;
    }
    
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        if (!iterator.hasNext()) return Optional.empty();
        
        T result = iterator.next();
        while (iterator.hasNext()) {
            result = accumulator.apply(result, iterator.next());
        }
        return Optional.of(result);
    }
    
    public long count() {
        long count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }
    
    public Optional<T> findFirst() {
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }
    
    public Optional<T> findAny() {
        return findFirst();
    }
    
    public boolean anyMatch(Predicate<T> predicate) {
        while (iterator.hasNext()) {
            if (predicate.test(iterator.next())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean allMatch(Predicate<T> predicate) {
        while (iterator.hasNext()) {
            if (!predicate.test(iterator.next())) {
                return false;
            }
        }
        return true;
    }
    
    public boolean noneMatch(Predicate<T> predicate) {
        return !anyMatch(predicate);
    }
    
    public Object[] toArray() {
        return toList().toArray();
    }
    
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return toList().toArray(generator.apply(size()));
    }
    
    private long size() {
        long count = 0;
        Iterator<T> it = toList().iterator();
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }
}
```

### 1.2 Stream Pipeline Demo

```java
package com.learning.streams.implementation;

import java.util.*;
import java.util.stream.*;

public class StreamPipelineDemo {
    
    public static void main(String[] args) {
        demonstrateFiltering();
        demonstrateMapping();
        demonstrateFlatMap();
        demonstrateReduction();
        demonstrateCollectors();
        demonstrateParallelStreams();
    }
    
    private static void demonstrateFiltering() {
        System.out.println("=== Filtering ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Filter even numbers
        List<Integer> evens = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("Evens: " + evens);
        
        // Filter and limit
        List<Integer> firstThree = numbers.stream()
                .filter(n -> n > 3)
                .limit(3)
                .collect(Collectors.toList());
        System.out.println("First three > 3: " + firstThree);
        
        // Filter and skip
        List<Integer> skipFirstThree = numbers.stream()
                .skip(3)
                .collect(Collectors.toList());
        System.out.println("Skip first 3: " + skipFirstThree);
        
        // Distinct
        List<Integer> withDuplicates = Arrays.asList(1, 2, 2, 3, 3, 3);
        List<Integer> distinct = withDuplicates.stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println("Distinct: " + distinct);
    }
    
    private static void demonstrateMapping() {
        System.out.println("\n=== Mapping ===");
        
        List<String> words = Arrays.asList("hello", "world", "java", "streams");
        
        // Map to length
        List<Integer> lengths = words.stream()
                .map(String::length)
                .collect(Collectors.toList());
        System.out.println("Lengths: " + lengths);
        
        // Map to uppercase
        List<String> upper = words.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println("Uppercase: " + upper);
        
        // Map with complex transformation
        List<String> transformed = words.stream()
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .collect(Collectors.toList());
        System.out.println("Transformed: " + transformed);
        
        // Map to object
        List<String> names = Arrays.asList("John", "Jane", "Bob");
        List<Person> people = names.stream()
                .map(name -> new Person(name, 0))
                .collect(Collectors.toList());
        System.out.println("People: " + people);
    }
    
    private static void demonstrateFlatMap() {
        System.out.println("\n=== FlatMap ===");
        
        List<List<Integer>> nested = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5, 6),
                Arrays.asList(7, 8, 9)
        );
        
        // Flatten nested lists
        List<Integer> flat = nested.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        System.out.println("Flattened: " + flat);
        
        // FlatMap with optional
        List<String> sentences = Arrays.asList(
                "Hello world",
                "Java streams",
                "Are awesome"
        );
        
        List<String> words = sentences.stream()
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .collect(Collectors.toList());
        System.out.println("Words: " + words);
    }
    
    private static void demonstrateReduction() {
        System.out.println("\n=== Reduction ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // Sum with identity
        int sum = numbers.stream()
                .reduce(0, Integer::sum);
        System.out.println("Sum: " + sum);
        
        // Max
        Optional<Integer> max = numbers.stream()
                .reduce(Integer::max);
        System.out.println("Max: " + max.orElse(0));
        
        // Min
        Optional<Integer> min = numbers.stream()
                .reduce(Integer::min);
        System.out.println("Min: " + min.orElse(0));
        
        // String concatenation
        List<String> words = Arrays.asList("Hello", " ", "World", "!");
        String concatenated = words.stream()
                .reduce("", String::concat);
        System.out.println("Concatenated: " + concatenated);
        
        // Custom accumulation
        String result = numbers.stream()
                .map(String::valueOf)
                .reduce((a, b) -> a + "-" + b)
                .orElse("");
        System.out.println("Hyphenated: " + result);
    }
    
    private static void demonstrateCollectors() {
        System.out.println("\n=== Collectors ===");
        
        List<Product> products = Arrays.asList(
                new Product("Laptop", 1000, "Electronics"),
                new Product("Phone", 800, "Electronics"),
                new Product("Shirt", 50, "Clothing"),
                new Product("Pants", 40, "Clothing"),
                new Product("Shoes", 80, "Clothing")
        );
        
        // Collect to list
        List<String> names = products.stream()
                .map(Product::getName)
                .collect(Collectors.toList());
        System.out.println("Names: " + names);
        
        // Collect to set
        Set<String> categories = products.stream()
                .map(Product::getCategory)
                .collect(Collectors.toSet());
        System.out.println("Categories: " + categories);
        
        // Collect to map
        Map<String, Double> nameToPrice = products.stream()
                .collect(Collectors.toMap(
                        Product::getName,
                        Product::getPrice
                ));
        System.out.println("Name to Price: " + nameToPrice);
        
        // Grouping by
        Map<String, List<Product>> byCategory = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory));
        System.out.println("By Category: " + byCategory);
        
        // Grouping with counting
        Map<String, Long> countByCategory = products.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.counting()
                ));
        System.out.println("Count by Category: " + countByCategory);
        
        // Sum by category
        Map<String, Double> sumByCategory = products.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.summingDouble(Product::getPrice)
                ));
        System.out.println("Sum by Category: " + sumByCategory);
        
        // Partitioning
        Map<Boolean, List<Product>> partitioned = products.stream()
                .collect(Collectors.partitioningBy(p -> p.getPrice() > 100));
        System.out.println("Partitioned by price > 100: " + partitioned);
        
        // Collect joining
        String joinedNames = products.stream()
                .map(Product::getName)
                .collect(Collectors.joining(", "));
        System.out.println("Joined: " + joinedNames);
    }
    
    private static void demonstrateParallelStreams() {
        System.out.println("\n=== Parallel Streams ===");
        
        List<Integer> numbers = new Random()
                .ints(100, 1, 1000)
                .limit(100)
                .boxed()
                .collect(Collectors.toList());
        
        // Sequential stream
        long startSeq = System.currentTimeMillis();
        long countSeq = numbers.stream()
                .filter(n -> n % 2 == 0)
                .count();
        long timeSeq = System.currentTimeMillis() - startSeq;
        
        // Parallel stream
        long startPar = System.currentTimeMillis();
        long countPar = numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .count();
        long timePar = System.currentTimeMillis() - startPar;
        
        System.out.println("Sequential: " + countSeq + " in " + timeSeq + "ms");
        System.out.println("Parallel: " + countPar + " in " + timePar + "ms");
    }
    
    static class Product {
        private String name;
        private double price;
        private String category;
        
        Product(String name, double price, String category) {
            this.name = name;
            this.price = price;
            this.category = category;
        }
        
        String getName() { return name; }
        double getPrice() { return price; }
        String getCategory() { return category; }
        
        @Override
        public String toString() {
            return name + "(" + price + ")";
        }
    }
    
    static class Person {
        private String name;
        private int age;
        
        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        String getName() { return name; }
        int getAge() { return age; }
        
        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }
}
```

---

## Part 2: Production Variant with Spring Boot

### 2.1 Stream Service with Database

```java
package com.learning.streams.service;

import com.learning.streams.entity.Order;
import com.learning.streams.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderAnalysisService {
    
    private final OrderRepository repository;
    
    public OrderAnalysisService(OrderRepository repository) {
        this.repository = repository;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Long> getOrderCountByStatus() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Order::getStatus,
                        Collectors.counting()
                ));
    }
    
    @Transactional(readOnly = true)
    public List<Order> getHighValueOrders(double threshold) {
        return repository.findAll().stream()
                .filter(o -> o.getTotal().doubleValue() > threshold)
                .sorted(Comparator.comparing(Order::getTotal).reversed())
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Map<LocalDate, BigDecimal> getDailyRevenue() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Order::getOrderDate,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Order::getTotal,
                                BigDecimal::add
                        )
                ));
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getCustomerStatistics(String customerId) {
        List<Order> orders = repository.findByCustomerId(customerId);
        
        if (orders.isEmpty()) {
            return Collections.emptyMap();
        }
        
        return Map.of(
                "totalOrders", orders.size(),
                "totalSpent", orders.stream()
                        .map(Order::getTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                "averageOrderValue", orders.stream()
                        .map(Order::getTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(orders.size())),
                "mostExpensiveOrder", orders.stream()
                        .max(Comparator.comparing(Order::getTotal))
                        .orElse(null)
        );
    }
    
    @Transactional(readOnly = true)
    public List<String> getTopCustomers(int limit) {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Order::getCustomerId,
                        Collectors.summingDouble(o -> o.getTotal().doubleValue())
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
```

### 2.2 Stream-based Data Processing

```java
package com.learning.streams.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.*;

@Service
public class DataProcessingService {
    
    public record ProcessingResult<T>(
            long processedCount,
            long successCount,
            long failureCount,
            List<T> results,
            List<String> errors
    ) {}
    
    public <T> ProcessingResult<T> processWithErrorHandling(
            List<T> items,
            java.util.function.Function<T, T> processor) {
        
        List<T> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        long successCount = 0;
        long failureCount = 0;
        
        for (T item : items) {
            try {
                T result = processor.apply(item);
                results.add(result);
                successCount++;
            } catch (Exception e) {
                errors.add("Failed to process " + item + ": " + e.getMessage());
                failureCount++;
            }
        }
        
        return new ProcessingResult<>(
                items.size(),
                successCount,
                failureCount,
                results,
                errors
        );
    }
    
    public <T, K> Map<K, List<T>> groupByKey(
            List<T> items,
            java.util.function.Function<T, K> keyExtractor) {
        
        return items.stream()
                .collect(Collectors.groupingBy(keyExtractor));
    }
    
    public <T> List<T> removeDuplicates(List<T> items) {
        return items.stream()
                .distinct()
                .collect(Collectors.toList());
    }
    
    public <T extends Comparable<T>> List<T> sortAndFilter(
            List<T> items,
            java.util.function.Predicate<T> filter) {
        
        return items.stream()
                .filter(filter)
                .sorted()
                .collect(Collectors.toList());
    }
}
```

---

## Part 3: Step-by-Step Code Explanation

### 3.1 Custom Stream Design

**Step 1: Iterator-based Implementation**
- Wrap existing iterators
- Lazy evaluation via internal iterator

**Step 2: Intermediate Operations**
- filter(): New stream with matching elements
- map(): Transform elements to new type
- distinct(): Remove duplicates using Set
- limit/skip: Boundary operations
- sorted(): Eager sorting

**Step 3: Terminal Operations**
- forEach(): Execute action for each element
- collect(): Gather to collection
- reduce(): Aggregate to single value
- count/find: Return single value

### 3.2 Stream Pipeline

**Step 1: Source**
- Collections, arrays, generators
- Infinite streams via iterate/generate

**Step 2: Intermediate Operations**
- Return new stream
- Lazy execution
- Can be chained

**Step 3: Terminal Operations**
- Produce result
- Trigger pipeline execution
- Terminal operation required

### 3.3 Collectors

**Step 1: Basic Collectors**
- toList, toSet, toMap

**Step 2: Grouping**
- groupingBy: Group by key
- partitioningBy: Binary partition

**Step 3: Aggregation**
- counting, summing, averaging
- reducing with custom logic

---

## Part 4: Key Concepts Demonstrated

| Concept | Implementation | Usage |
|---------|---------------|-------|
| **Lazy Evaluation** | Intermediate ops return new stream | Efficient processing |
| **Map-Reduce** | map() + reduce() | Aggregation |
| **FlatMap** | Transform and flatten | Nested collections |
| **Parallel Streams** | parallelStream() | Multi-core processing |
| **Collectors** | groupingBy, partitioningBy | Complex aggregation |
| **Optional Integration** | Optional as terminal | Null safety |

---

## Key Takeaways

1. Streams are lazy - terminal operation triggers execution
2. Use parallel streams carefully - not always faster
3. Prefer method references for readability
4. Use collectors for complex transformations
5. Remember to handle exceptions in stream pipelines