# Exercises: Streams API

<div align="center">

![Module](https://img.shields.io/badge/Module-04-blue?style=for-the-badge)
![Exercises](https://img.shields.io/badge/Exercises-30-green?style=for-the-badge)
![Difficulty](https://img.shields.io/badge/Difficulty-Easy%20to%20Hard-orange?style=for-the-badge)

**30 comprehensive exercises for Streams API module**

</div>

---

## 📚 Table of Contents

1. [Easy Exercises (1-10)](#easy-exercises-1-10)
2. [Medium Exercises (11-20)](#medium-exercises-11-20)
3. [Hard Exercises (21-25)](#hard-exercises-21-25)
4. [Interview Exercises (26-30)](#interview-exercises-26-30)

---

## 🟢 Easy Exercises (1-10)

### Exercise 1: Stream Creation
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Stream creation, sources

**Pedagogic Objective:**
Understand different ways to create streams from various data sources.

**Problem:**
Create streams from arrays, lists, and ranges. Print elements using forEach.

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.IntStream;

public class StreamCreation {
    public static void main(String[] args) {
        // From array
        String[] fruits = {"Apple", "Banana", "Orange"};
        Arrays.stream(fruits).forEach(System.out::println);
        
        // From list
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        numbers.stream().forEach(System.out::println);
        
        // From range
        IntStream.range(1, 5).forEach(System.out::println);
        
        // From Stream.of()
        Stream.of("A", "B", "C").forEach(System.out::println);
    }
}
```

**Key Concepts:**
- Streams are lazy and functional
- Multiple sources: arrays, collections, ranges
- Terminal operations trigger evaluation
- Streams are one-time use

---

### Exercise 2: Filter Operation
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Filter, predicates, intermediate operations

**Pedagogic Objective:**
Understand filtering to select elements matching a condition.

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class FilterOperation {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Filter even numbers
        List<Integer> evens = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("Even numbers: " + evens);
        
        // Filter numbers > 5
        List<Integer> greaterThan5 = numbers.stream()
                .filter(n -> n > 5)
                .collect(Collectors.toList());
        System.out.println("Greater than 5: " + greaterThan5);
        
        // Multiple filters
        List<Integer> result = numbers.stream()
                .filter(n -> n > 3)
                .filter(n -> n < 8)
                .collect(Collectors.toList());
        System.out.println("Between 3 and 8: " + result);
    }
}
```

**Key Concepts:**
- Filter is an intermediate operation
- Returns a new stream with matching elements
- Predicates define the condition
- Can chain multiple filters

---

### Exercise 3: Map Operation
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Map, transformation, intermediate operations

**Pedagogic Objective:**
Understand mapping to transform elements.

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class MapOperation {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("hello", "world", "java");
        
        // Map to uppercase
        List<String> uppercase = words.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println("Uppercase: " + uppercase);
        
        // Map to length
        List<Integer> lengths = words.stream()
                .map(String::length)
                .collect(Collectors.toList());
        System.out.println("Lengths: " + lengths);
        
        // Map with lambda
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> doubled = numbers.stream()
                .map(n -> n * 2)
                .collect(Collectors.toList());
        System.out.println("Doubled: " + doubled);
    }
}
```

**Key Concepts:**
- Map transforms each element
- Returns a new stream with transformed elements
- One-to-one mapping
- Can chain with other operations

---

### Exercise 4: Collect Terminal Operation
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Collect, terminal operations, collectors

**Pedagogic Objective:**
Understand collecting stream results into collections.

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class CollectOperation {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // Collect to List
        List<Integer> list = numbers.stream()
                .filter(n -> n > 2)
                .collect(Collectors.toList());
        System.out.println("List: " + list);
        
        // Collect to Set
        Set<Integer> set = numbers.stream()
                .collect(Collectors.toSet());
        System.out.println("Set: " + set);
        
        // Collect to String
        String joined = Arrays.asList("a", "b", "c").stream()
                .collect(Collectors.joining(", "));
        System.out.println("Joined: " + joined);
        
        // Collect to Map
        Map<Integer, String> map = numbers.stream()
                .collect(Collectors.toMap(
                    n -> n,
                    n -> "Number: " + n
                ));
        System.out.println("Map: " + map);
    }
}
```

**Key Concepts:**
- Collect is a terminal operation
- Triggers stream evaluation
- Various collectors available
- Converts stream to collection

---

### Exercise 5: ForEach Terminal Operation
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** ForEach, terminal operations, side effects

**Pedagogic Objective:**
Understand forEach for performing actions on elements.

**Complete Solution:**
```java
import java.util.*;

public class ForEachOperation {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        
        // Simple forEach
        names.stream().forEach(System.out::println);
        
        // forEach with lambda
        names.stream().forEach(name -> System.out.println("Hello, " + name));
        
        // forEach with index (using IntStream)
        names.stream()
                .forEach(name -> System.out.println(name.length()));
        
        // forEach with side effects
        List<String> processed = new ArrayList<>();
        names.stream()
                .forEach(processed::add);
        System.out.println("Processed: " + processed);
    }
}
```

**Key Concepts:**
- ForEach is a terminal operation
- Performs action on each element
- Causes side effects
- No return value

---

### Exercise 6: Reduce Terminal Operation
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Reduce, aggregation, terminal operations

**Pedagogic Objective:**
Understand reducing streams to single values.

**Complete Solution:**
```java
import java.util.*;

public class ReduceOperation {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // Sum using reduce
        int sum = numbers.stream()
                .reduce(0, (a, b) -> a + b);
        System.out.println("Sum: " + sum);
        
        // Product using reduce
        int product = numbers.stream()
                .reduce(1, (a, b) -> a * b);
        System.out.println("Product: " + product);
        
        // Max using reduce
        int max = numbers.stream()
                .reduce(Integer.MIN_VALUE, Math::max);
        System.out.println("Max: " + max);
        
        // String concatenation
        String result = Arrays.asList("a", "b", "c").stream()
                .reduce("", (a, b) -> a + b);
        System.out.println("Concatenated: " + result);
    }
}
```

**Key Concepts:**
- Reduce combines elements into single value
- Requires identity and accumulator
- Terminal operation
- Useful for aggregations

---

### Exercise 7: FlatMap Operation
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** FlatMap, flattening, intermediate operations

**Pedagogic Objective:**
Understand flattening nested structures.

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class FlatMapOperation {
    public static void main(String[] args) {
        List<List<Integer>> lists = Arrays.asList(
            Arrays.asList(1, 2, 3),
            Arrays.asList(4, 5),
            Arrays.asList(6, 7, 8)
        );
        
        // Flatten lists
        List<Integer> flattened = lists.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        System.out.println("Flattened: " + flattened);
        
        // Flatten and filter
        List<Integer> filtered = lists.stream()
                .flatMap(List::stream)
                .filter(n -> n > 3)
                .collect(Collectors.toList());
        System.out.println("Filtered: " + filtered);
        
        // Flatten strings to characters
        List<String> words = Arrays.asList("hello", "world");
        List<String> chars = words.stream()
                .flatMap(word -> word.split("").stream())
                .collect(Collectors.toList());
        System.out.println("Characters: " + chars);
    }
}
```

**Key Concepts:**
- FlatMap flattens nested structures
- One-to-many mapping
- Intermediate operation
- Useful for nested collections

---

### Exercise 8: Sorting Streams
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Sorted, ordering, intermediate operations

**Pedagogic Objective:**
Understand sorting elements in streams.

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class SortingStreams {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9, 3);
        
        // Natural order
        List<Integer> sorted = numbers.stream()
                .sorted()
                .collect(Collectors.toList());
        System.out.println("Sorted: " + sorted);
        
        // Reverse order
        List<Integer> reversed = numbers.stream()
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
        System.out.println("Reversed: " + reversed);
        
        // Custom comparator
        List<String> words = Arrays.asList("apple", "pie", "banana");
        List<String> byLength = words.stream()
                .sorted(Comparator.comparingInt(String::length))
                .collect(Collectors.toList());
        System.out.println("By length: " + byLength);
    }
}
```

**Key Concepts:**
- Sorted is an intermediate operation
- Can use natural order or custom comparators
- Returns new sorted stream
- Stateful operation

---

### Exercise 9: Distinct Operation
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Distinct, duplicates, intermediate operations

**Pedagogic Objective:**
Understand removing duplicates from streams.

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class DistinctOperation {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 5, 5);
        
        // Remove duplicates
        List<Integer> distinct = numbers.stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println("Distinct: " + distinct);
        
        // Distinct with strings
        List<String> words = Arrays.asList("apple", "banana", "apple", "cherry");
        List<String> uniqueWords = words.stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println("Unique words: " + uniqueWords);
        
        // Count distinct
        long count = numbers.stream()
                .distinct()
                .count();
        System.out.println("Count distinct: " + count);
    }
}
```

**Key Concepts:**
- Distinct removes duplicates
- Intermediate operation
- Stateful operation
- Uses equals() for comparison

---

### Exercise 10: Limit and Skip
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Limit, skip, pagination, intermediate operations

**Pedagogic Objective:**
Understand limiting and skipping elements.

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class LimitAndSkip {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Limit to first 5
        List<Integer> limited = numbers.stream()
                .limit(5)
                .collect(Collectors.toList());
        System.out.println("Limited: " + limited);
        
        // Skip first 3
        List<Integer> skipped = numbers.stream()
                .skip(3)
                .collect(Collectors.toList());
        System.out.println("Skipped: " + skipped);
        
        // Pagination: skip 5, limit 3
        List<Integer> page = numbers.stream()
                .skip(5)
                .limit(3)
                .collect(Collectors.toList());
        System.out.println("Page: " + page);
        
        // Skip and limit with filter
        List<Integer> result = numbers.stream()
                .filter(n -> n > 2)
                .skip(2)
                .limit(3)
                .collect(Collectors.toList());
        System.out.println("Result: " + result);
    }
}
```

**Key Concepts:**
- Limit restricts number of elements
- Skip skips first n elements
- Useful for pagination
- Intermediate operations

---

## 🟡 Medium Exercises (11-20)

### Exercise 11: Grouping By
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** GroupingBy, collectors, aggregation

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class Student {
    private String name;
    private String grade;
    private int score;
    
    public Student(String name, String grade, int score) {
        this.name = name;
        this.grade = grade;
        this.score = score;
    }
    
    public String getGrade() { return grade; }
    public int getScore() { return score; }
    
    @Override
    public String toString() {
        return name + " (" + score + ")";
    }
}

public class GroupingByExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", "A", 90),
            new Student("Bob", "B", 80),
            new Student("Charlie", "A", 92),
            new Student("David", "B", 85)
        );
        
        // Group by grade
        Map<String, List<Student>> byGrade = students.stream()
                .collect(Collectors.groupingBy(Student::getGrade));
        System.out.println("By grade: " + byGrade);
        
        // Group by grade and count
        Map<String, Long> countByGrade = students.stream()
                .collect(Collectors.groupingBy(
                    Student::getGrade,
                    Collectors.counting()
                ));
        System.out.println("Count by grade: " + countByGrade);
        
        // Group by grade and average score
        Map<String, Double> avgByGrade = students.stream()
                .collect(Collectors.groupingBy(
                    Student::getGrade,
                    Collectors.averagingInt(Student::getScore)
                ));
        System.out.println("Average by grade: " + avgByGrade);
    }
}
```

---

### Exercise 12: Partitioning By
**Difficulty:** Medium  
**Time:** 20 minutes  
**Topics:** PartitioningBy, boolean grouping

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class PartitioningByExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Partition into even and odd
        Map<Boolean, List<Integer>> partition = numbers.stream()
                .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        System.out.println("Even: " + partition.get(true));
        System.out.println("Odd: " + partition.get(false));
        
        // Partition and count
        Map<Boolean, Long> counts = numbers.stream()
                .collect(Collectors.partitioningBy(
                    n -> n > 5,
                    Collectors.counting()
                ));
        System.out.println("Greater than 5: " + counts.get(true));
        System.out.println("Less than or equal to 5: " + counts.get(false));
    }
}
```

---

### Exercise 13: Peek Operation
**Difficulty:** Medium  
**Time:** 20 minutes  
**Topics:** Peek, debugging, intermediate operations

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class PeekOperation {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // Peek for debugging
        List<Integer> result = numbers.stream()
                .peek(n -> System.out.println("Original: " + n))
                .filter(n -> n > 2)
                .peek(n -> System.out.println("After filter: " + n))
                .map(n -> n * 2)
                .peek(n -> System.out.println("After map: " + n))
                .collect(Collectors.toList());
        
        System.out.println("Final result: " + result);
    }
}
```

---

### Exercise 14: Match Operations
**Difficulty:** Medium  
**Time:** 20 minutes  
**Topics:** AnyMatch, AllMatch, NoneMatch, terminal operations

**Complete Solution:**
```java
import java.util.*;

public class MatchOperations {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // anyMatch
        boolean hasEven = numbers.stream()
                .anyMatch(n -> n % 2 == 0);
        System.out.println("Has even: " + hasEven);
        
        // allMatch
        boolean allPositive = numbers.stream()
                .allMatch(n -> n > 0);
        System.out.println("All positive: " + allPositive);
        
        // noneMatch
        boolean noNegative = numbers.stream()
                .noneMatch(n -> n < 0);
        System.out.println("No negative: " + noNegative);
    }
}
```

---

### Exercise 15: Find Operations
**Difficulty:** Medium  
**Time:** 20 minutes  
**Topics:** FindFirst, FindAny, Optional, terminal operations

**Complete Solution:**
```java
import java.util.*;

public class FindOperations {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // findFirst
        Optional<Integer> first = numbers.stream()
                .filter(n -> n > 2)
                .findFirst();
        System.out.println("First > 2: " + first.orElse(-1));
        
        // findAny
        Optional<Integer> any = numbers.stream()
                .filter(n -> n > 2)
                .findAny();
        System.out.println("Any > 2: " + any.orElse(-1));
        
        // With ifPresent
        numbers.stream()
                .filter(n -> n > 3)
                .findFirst()
                .ifPresent(n -> System.out.println("Found: " + n));
    }
}
```

---

### Exercise 16: Count and Min/Max
**Difficulty:** Medium  
**Time:** 20 minutes  
**Topics:** Count, min, max, terminal operations

**Complete Solution:**
```java
import java.util.*;

public class CountMinMax {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9, 3);
        
        // Count
        long count = numbers.stream()
                .filter(n -> n > 3)
                .count();
        System.out.println("Count > 3: " + count);
        
        // Min
        Optional<Integer> min = numbers.stream()
                .min(Integer::compareTo);
        System.out.println("Min: " + min.orElse(-1));
        
        // Max
        Optional<Integer> max = numbers.stream()
                .max(Integer::compareTo);
        System.out.println("Max: " + max.orElse(-1));
        
        // Min with filter
        Optional<Integer> minEven = numbers.stream()
                .filter(n -> n % 2 == 0)
                .min(Integer::compareTo);
        System.out.println("Min even: " + minEven.orElse(-1));
    }
}
```

---

### Exercise 17: Parallel Streams
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Parallel streams, performance, thread safety

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class ParallelStreamsExample {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 1000000; i++) {
            numbers.add(i);
        }
        
        // Sequential stream
        long start = System.currentTimeMillis();
        long sumSeq = numbers.stream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * 2)
                .reduce(0L, Long::sum);
        long seqTime = System.currentTimeMillis() - start;
        System.out.println("Sequential: " + sumSeq + " (" + seqTime + "ms)");
        
        // Parallel stream
        start = System.currentTimeMillis();
        long sumPar = numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * 2)
                .reduce(0L, Long::sum);
        long parTime = System.currentTimeMillis() - start;
        System.out.println("Parallel: " + sumPar + " (" + parTime + "ms)");
    }
}
```

---

### Exercise 18: Custom Collectors
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Custom collectors, Collector interface

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CustomCollectors {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // Custom collector: sum
        Integer sum = numbers.stream()
                .collect(Collector.of(
                    () -> new int[1],
                    (acc, n) -> acc[0] += n,
                    (acc1, acc2) -> {
                        acc1[0] += acc2[0];
                        return acc1;
                    },
                    acc -> acc[0]
                ));
        System.out.println("Sum: " + sum);
        
        // Using Collectors.reducing
        Integer product = numbers.stream()
                .collect(Collectors.reducing(1, (a, b) -> a * b));
        System.out.println("Product: " + product);
    }
}
```

---

### Exercise 19: Stream Performance
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Performance, lazy evaluation, optimization

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class StreamPerformance {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Inefficient: multiple terminal operations
        long start = System.nanoTime();
        long count1 = numbers.stream().filter(n -> n > 5).count();
        long sum1 = numbers.stream().filter(n -> n > 5).mapToInt(Integer::intValue).sum();
        long time1 = System.nanoTime() - start;
        
        // Efficient: single pass
        start = System.nanoTime();
        List<Integer> filtered = numbers.stream()
                .filter(n -> n > 5)
                .collect(Collectors.toList());
        long count2 = filtered.size();
        long sum2 = filtered.stream().mapToInt(Integer::intValue).sum();
        long time2 = System.nanoTime() - start;
        
        System.out.println("Multiple passes: " + time1 + "ns");
        System.out.println("Single pass: " + time2 + "ns");
    }
}
```

---

### Exercise 20: Stream Chaining
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Method chaining, fluent API, composition

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class StreamChaining {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("hello", "world", "java", "stream");
        
        // Complex stream chain
        Map<Integer, List<String>> result = words.stream()
                .filter(w -> w.length() > 3)
                .map(String::toUpperCase)
                .collect(Collectors.groupingBy(
                    String::length,
                    Collectors.toList()
                ));
        System.out.println("Grouped by length: " + result);
        
        // Another example
        String output = words.stream()
                .filter(w -> w.length() > 4)
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.joining(", "));
        System.out.println("Output: " + output);
    }
}
```

---

## 🔴 Hard Exercises (21-25)

### Exercise 21: Advanced Collectors
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Complex collectors, nested grouping

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class AdvancedCollectors {
    static class Person {
        String name;
        String department;
        int salary;
        
        Person(String name, String department, int salary) {
            this.name = name;
            this.department = department;
            this.salary = salary;
        }
    }
    
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(
            new Person("Alice", "IT", 50000),
            new Person("Bob", "HR", 40000),
            new Person("Charlie", "IT", 55000),
            new Person("David", "HR", 42000)
        );
        
        // Nested grouping
        Map<String, Map<Boolean, List<Person>>> nested = people.stream()
                .collect(Collectors.groupingBy(
                    p -> p.department,
                    Collectors.groupingBy(p -> p.salary > 45000)
                ));
        System.out.println("Nested grouping: " + nested);
        
        // Grouping with statistics
        Map<String, IntSummaryStatistics> stats = people.stream()
                .collect(Collectors.groupingBy(
                    p -> p.department,
                    Collectors.summarizingInt(p -> p.salary)
                ));
        System.out.println("Statistics: " + stats);
    }
}
```

---

### Exercise 22: Stream with Optional
**Difficulty:** Hard  
**Time:** 35 minutes  
**Topics:** Optional, null handling, functional approach

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class StreamWithOptional {
    static class User {
        String name;
        Optional<String> email;
        
        User(String name, Optional<String> email) {
            this.name = name;
            this.email = email;
        }
    }
    
    public static void main(String[] args) {
        List<User> users = Arrays.asList(
            new User("Alice", Optional.of("alice@example.com")),
            new User("Bob", Optional.empty()),
            new User("Charlie", Optional.of("charlie@example.com"))
        );
        
        // Filter users with email
        List<String> emails = users.stream()
                .flatMap(u -> u.email.stream())
                .collect(Collectors.toList());
        System.out.println("Emails: " + emails);
        
        // Map with Optional
        List<String> emailsOrDefault = users.stream()
                .map(u -> u.email.orElse("no-email"))
                .collect(Collectors.toList());
        System.out.println("Emails or default: " + emailsOrDefault);
    }
}
```

---

### Exercise 23: Infinite Streams
**Difficulty:** Hard  
**Time:** 35 minutes  
**Topics:** Infinite streams, generate, iterate, limit

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfiniteStreams {
    public static void main(String[] args) {
        // Generate infinite stream
        List<Double> randoms = Stream.generate(Math::random)
                .limit(5)
                .collect(Collectors.toList());
        System.out.println("Random numbers: " + randoms);
        
        // Iterate infinite stream
        List<Integer> sequence = Stream.iterate(1, n -> n + 1)
                .limit(10)
                .collect(Collectors.toList());
        System.out.println("Sequence: " + sequence);
        
        // Fibonacci sequence
        List<Integer> fibonacci = Stream.iterate(
                new int[]{0, 1},
                arr -> new int[]{arr[1], arr[0] + arr[1]}
            )
            .limit(10)
            .map(arr -> arr[0])
            .collect(Collectors.toList());
        System.out.println("Fibonacci: " + fibonacci);
    }
}
```

---

### Exercise 24: Stream Reduction Patterns
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Reduce, accumulation, complex reductions

**Complete Solution:**
```java
import java.util.*;

public class StreamReductionPatterns {
    static class Result {
        int sum = 0;
        int count = 0;
        int max = Integer.MIN_VALUE;
        
        Result combine(Result other) {
            this.sum += other.sum;
            this.count += other.count;
            this.max = Math.max(this.max, other.max);
            return this;
        }
    }
    
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // Complex reduction
        Result result = numbers.stream()
                .reduce(
                    new Result(),
                    (acc, n) -> {
                        acc.sum += n;
                        acc.count++;
                        acc.max = Math.max(acc.max, n);
                        return acc;
                    },
                    Result::combine
                );
        
        System.out.println("Sum: " + result.sum);
        System.out.println("Count: " + result.count);
        System.out.println("Max: " + result.max);
        System.out.println("Average: " + (result.sum / (double) result.count));
    }
}
```

---

### Exercise 25: Stream Composition
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Composing streams, reusable pipelines

**Complete Solution:**
```java
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamComposition {
    // Reusable stream operations
    static <T> Function<Stream<T>, Stream<T>> filterBy(java.util.function.Predicate<T> predicate) {
        return stream -> stream.filter(predicate);
    }
    
    static <T, R> Function<Stream<T>, Stream<R>> mapBy(java.util.function.Function<T, R> mapper) {
        return stream -> stream.map(mapper);
    }
    
    static <T> Function<Stream<T>, Stream<T>> limitTo(int n) {
        return stream -> stream.limit(n);
    }
    
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Compose operations
        Function<Stream<Integer>, Stream<Integer>> pipeline = 
            filterBy((Integer n) -> n > 2)
            .andThen(mapBy(n -> n * 2))
            .andThen(limitTo(3));
        
        List<Integer> result = pipeline.apply(numbers.stream())
                .collect(Collectors.toList());
        System.out.println("Result: " + result);
    }
}
```

---

## 🎯 Interview Exercises (26-30)

### Exercise 26: Top K Elements
**Difficulty:** Interview  
**Time:** 25 minutes

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class TopKElements {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
        
        // Top 3 elements
        List<Integer> topK = numbers.stream()
                .sorted(Collections.reverseOrder())
                .limit(3)
                .collect(Collectors.toList());
        System.out.println("Top 3: " + topK);
        
        // Top K distinct
        List<Integer> topKDistinct = numbers.stream()
                .distinct()
                .sorted(Collections.reverseOrder())
                .limit(3)
                .collect(Collectors.toList());
        System.out.println("Top 3 distinct: " + topKDistinct);
    }
}
```

---

### Exercise 27: Word Frequency
**Difficulty:** Interview  
**Time:** 25 minutes

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class WordFrequency {
    public static void main(String[] args) {
        String text = "hello world hello java world java java";
        
        Map<String, Long> frequency = Arrays.stream(text.split(" "))
                .collect(Collectors.groupingBy(
                    String::toString,
                    Collectors.counting()
                ));
        System.out.println("Frequency: " + frequency);
        
        // Sorted by frequency
        List<Map.Entry<String, Long>> sorted = frequency.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toList());
        System.out.println("Sorted: " + sorted);
    }
}
```

---

### Exercise 28: Anagram Grouping
**Difficulty:** Interview  
**Time:** 30 minutes

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class AnagramGrouping {
    static String sortChars(String s) {
        return s.chars()
                .sorted()
                .collect(StringBuilder::new, 
                    (sb, c) -> sb.append((char) c),
                    StringBuilder::append)
                .toString();
    }
    
    public static void main(String[] args) {
        List<String> words = Arrays.asList("listen", "silent", "hello", "world", "enlist");
        
        Map<String, List<String>> anagrams = words.stream()
                .collect(Collectors.groupingBy(AnagramGrouping::sortChars));
        System.out.println("Anagrams: " + anagrams);
    }
}
```

---

### Exercise 29: Longest Increasing Subsequence
**Difficulty:** Interview  
**Time:** 35 minutes

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class LongestIncreasingSubsequence {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(10, 9, 2, 5, 3, 7, 101, 18);
        
        // Find LIS length using streams (simplified)
        int n = numbers.size();
        List<Integer> dp = new ArrayList<>(Collections.nCopies(n, 1));
        
        for (int i = 1; i < n; i++) {
            final int idx = i;
            int maxLen = IntStream.range(0, i)
                    .filter(j -> numbers.get(j) < numbers.get(idx))
                    .map(j -> dp.get(j))
                    .max()
                    .orElse(0);
            dp.set(i, maxLen + 1);
        }
        
        int lisLength = dp.stream().max(Integer::compareTo).orElse(0);
        System.out.println("LIS length: " + lisLength);
    }
}
```

---

### Exercise 30: Stream Pipeline Optimization
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class StreamPipelineOptimization {
    static class Product {
        String name;
        int price;
        int quantity;
        
        Product(String name, int price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
    }
    
    public static void main(String[] args) {
        List<Product> products = Arrays.asList(
            new Product("A", 100, 5),
            new Product("B", 200, 3),
            new Product("C", 150, 8),
            new Product("D", 300, 2)
        );
        
        // Optimized pipeline
        Map<String, Integer> result = products.stream()
                .filter(p -> p.quantity > 2)
                .collect(Collectors.toMap(
                    p -> p.name,
                    p -> p.price * p.quantity
                ));
        System.out.println("Result: " + result);
        
        // Total value
        int totalValue = products.stream()
                .filter(p -> p.quantity > 2)
                .mapToInt(p -> p.price * p.quantity)
                .sum();
        System.out.println("Total value: " + totalValue);
    }
}
```

---

## 📊 Solutions Summary

| Exercise | Title | Difficulty | Time | Topics |
|----------|-------|-----------|------|--------|
| 1 | Stream Creation | Easy | 15 min | Sources |
| 2 | Filter Operation | Easy | 15 min | Filtering |
| 3 | Map Operation | Easy | 15 min | Transformation |
| 4 | Collect Terminal | Easy | 20 min | Collectors |
| 5 | ForEach Terminal | Easy | 15 min | Side effects |
| 6 | Reduce Terminal | Easy | 20 min | Aggregation |
| 7 | FlatMap Operation | Easy | 20 min | Flattening |
| 8 | Sorting Streams | Easy | 15 min | Ordering |
| 9 | Distinct Operation | Easy | 15 min | Duplicates |
| 10 | Limit and Skip | Easy | 15 min | Pagination |
| 11 | Grouping By | Medium | 25 min | Grouping |
| 12 | Partitioning By | Medium | 20 min | Boolean grouping |
| 13 | Peek Operation | Medium | 20 min | Debugging |
| 14 | Match Operations | Medium | 20 min | Predicates |
| 15 | Find Operations | Medium | 20 min | Optional |
| 16 | Count Min/Max | Medium | 20 min | Aggregation |
| 17 | Parallel Streams | Medium | 25 min | Performance |
| 18 | Custom Collectors | Medium | 30 min | Collector API |
| 19 | Stream Performance | Medium | 25 min | Optimization |
| 20 | Stream Chaining | Medium | 25 min | Composition |
| 21 | Advanced Collectors | Hard | 40 min | Complex collectors |
| 22 | Stream with Optional | Hard | 35 min | Null handling |
| 23 | Infinite Streams | Hard | 35 min | Generate/iterate |
| 24 | Reduction Patterns | Hard | 40 min | Complex reductions |
| 25 | Stream Composition | Hard | 40 min | Reusable pipelines |
| 26 | Top K Elements | Interview | 25 min | Sorting |
| 27 | Word Frequency | Interview | 25 min | Grouping |
| 28 | Anagram Grouping | Interview | 30 min | Grouping |
| 29 | LIS | Interview | 35 min | Algorithms |
| 30 | Pipeline Optimization | Interview | 40 min | Performance |

---

<div align="center">

## Exercises: Streams API

**30 Comprehensive Exercises**

**Easy (10) | Medium (10) | Hard (5) | Interview (5)**

**Total Time: 10-12 hours**

---

[Back to Module →](./README.md)

[View Pedagogic Guide →](./PEDAGOGIC_GUIDE.md)

[Take Quizzes →](./QUIZZES.md)

</div>

(ending readme)