# Java Lambda Expressions Module - PROJECTS.md

---

# Mini-Project: Lambda Playground with Stream Operations

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Lambda Expressions, Method References, Stream API, Functional Interfaces, Functional Programming

This project demonstrates Java lambda expressions and stream operations through an interactive playground with various exercises.

---

## Project Structure

```
10-lambda-expressions/src/main/java/com/learning/
├── Main.java
├── playground/
│   ├── LambdaPlayground.java
│   ├── FunctionalInterfaces.java
│   └── StreamOperations.java
├── model/
│   ├── Person.java
│   ├── Product.java
│   └── Order.java
└── exercise/
    ├── Exercise1.java
    ├── Exercise2.java
    └── Exercise3.java
```

---

## Step 1: Functional Interface Examples

```java
// playground/FunctionalInterfaces.java
package com.learning.playground;

import java.util.function.*;

public class FunctionalInterfaces {
    
    public static void main(String[] args) {
        demonstratePredicates();
        demonstrateFunctions();
        demonstrateSuppliers();
        demonstrateConsumers();
        demonstrateBiFunctions();
    }
    
    private static void demonstratePredicates() {
        System.out.println("=== Predicate Examples ===");
        
        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> notEmpty = isEmpty.negate();
        
        System.out.println("isEmpty('') = " + isEmpty.test(""));
        System.out.println("isEmpty('hello') = " + isEmpty.test("hello"));
        
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<Integer> isPositiveEven = isPositive.and(isEven);
        
        System.out.println("isPositiveEven(4) = " + isPositiveEven.test(4));
        System.out.println("isPositiveEven(-2) = " + isPositiveEven.test(-2));
        
        Predicate<Integer> isGreaterThan10 = n -> n > 10;
        System.out.println("isGreaterThan10.or(isEven).test(7) = " + 
            isGreaterThan10.or(isEven).test(7));
    }
    
    private static void demonstrateFunctions() {
        System.out.println("\n=== Function Examples ===");
        
        Function<String, Integer> stringLength = String::length;
        Function<String, String> toUpperCase = String::toUpperCase;
        Function<String, String> compose = toUpperCase.compose(stringLength);
        
        System.out.println("length('hello') = " + stringLength.apply("hello"));
        System.out.println("upper('hello') = " + toUpperCase.apply("hello"));
        
        Function<Integer, Integer> factorial = n -> {
            int result = 1;
            for (int i = 2; i <= n; i++) result *= i;
            return result;
        };
        
        System.out.println("factorial(5) = " + factorial.apply(5));
        
        Function<Integer, String> format = n -> "Number: " + n;
        System.out.println(format.apply(42));
        
        BiFunction<String, String, String> concat = String::concat;
        System.out.println("concat('Hello', ' World') = " + 
            concat.apply("Hello", " World"));
    }
    
    private static void demonstrateSuppliers() {
        System.out.println("\n=== Supplier Examples ===");
        
        Supplier<Double> random = () -> Math.random() * 100;
        System.out.println("random() = " + random.get());
        
        Supplier<String> currentTime = () -> java.time.LocalDateTime.now().toString();
        System.out.println("currentTime() = " + currentTime.get());
        
        Supplier<String> uuid = () -> java.util.UUID.randomUUID().toString();
        System.out.println("uuid() = " + uuid.get());
        
        Supplier<List<String>> listSupplier = ArrayList::new;
        List<String> list = listSupplier.get();
        list.add("Item 1");
        System.out.println("listSupplier.get() = " + list);
    }
    
    private static void demonstrateConsumers() {
        System.out.println("\n=== Consumer Examples ===");
        
        Consumer<String> printer = System.out::println;
        printer.accept("Hello, World!");
        
        Consumer<String> upperPrinter = s -> System.out.println(s.toUpperCase());
        List<String> names = Arrays.asList("alice", "bob", "charlie");
        names.forEach(upperPrinter);
        
        BiConsumer<String, Integer> scorePrinter = (name, score) -> 
            System.out.println(name + ": " + score);
        scorePrinter.accept("Player1", 100);
        
        Consumer<String> errorLogger = s -> System.err.println("ERROR: " + s);
        errorLogger.accept("Something went wrong");
    }
    
    private static void demonstrateBiFunctions() {
        System.out.println("\n=== BiFunction Examples ===");
        
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        BiFunction<Integer, Integer, Integer> multiply = (a, b) -> a * b;
        
        System.out.println("add(3, 4) = " + add.apply(3, 4));
        System.out.println("multiply(3, 4) = " + multiply.apply(3, 4));
        
        BiFunction<String, String, Integer> compareLength = 
            (s1, s2) -> s1.length() - s2.length();
        System.out.println("compareLength('hello', 'world') = " + 
            compareLength.apply("hello", "world"));
        
        ToIntBiFunction<String, String> compareLengthInt = 
            (s1, s2) -> s1.length() - s2.length();
        System.out.println("compareLengthInt('hi', 'there') = " + 
            compareLengthInt.applyAsInt("hi", "there"));
    }
}
```

---

## Step 2: Stream Operations

```java
// playground/StreamOperations.java
package com.learning.playground;

import java.util.*;
import java.util.stream.*;

public class StreamOperations {
    
    public static void main(String[] args) {
        streamCreation();
        intermediateOperations();
        terminalOperations();
        collectors();
        advancedStreams();
    }
    
    private static void streamCreation() {
        System.out.println("=== Stream Creation ===");
        
        Stream<String> fromArray = Stream.of("a", "b", "c");
        fromArray.forEach(System.out::println);
        
        String[] arr = {"one", "two", "three"};
        Arrays.stream(arr).forEach(System.out::println);
        
        Stream<Integer> fromIter = Stream.iterate(1, n -> n + 2).limit(5);
        fromIter.forEach(System.out::println);
        
        Stream<Double> fromGen = Stream.generate(() -> Math.random()).limit(3);
        fromGen.forEach(System.out::println);
        
        Stream<String> empty = Stream.empty();
        System.out.println("empty.count() = " + empty.count());
    }
    
    private static void intermediateOperations() {
        System.out.println("\n=== Intermediate Operations ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        System.out.println("Filter (even): ");
        numbers.stream()
            .filter(n -> n % 2 == 0)
            .forEach(System.out::println);
        
        System.out.println("Map (square): ");
        numbers.stream()
            .map(n -> n * n)
            .forEach(System.out::println);
        
        System.out.println("FlatMap:");
        List<List<Integer>> nested = Arrays.asList(
            Arrays.asList(1, 2),
            Arrays.asList(3, 4),
            Arrays.asList(5, 6)
        );
        nested.stream()
            .flatMap(List::stream)
            .forEach(System.out::println);
        
        System.out.println("Distinct:");
        Stream.of(1, 2, 2, 3, 3, 3, 4)
            .distinct()
            .forEach(System.out::println);
        
        System.out.println("Sorted:");
        Stream.of(5, 2, 8, 1, 9, 3)
            .sorted()
            .forEach(System.out::println);
        
        System.out.println("Limit(3):");
        Stream.iterate(1, n -> n + 1)
            .limit(3)
            .forEach(System.out::println);
        
        System.out.println("Skip(2):");
        Stream.of(1, 2, 3, 4, 5)
            .skip(2)
            .forEach(System.out::println);
        
        System.out.println("TakeWhile (n < 5):");
        Stream.of(1, 2, 3, 4, 5, 6, 7)
            .takeWhile(n -> n < 5)
            .forEach(System.out::println);
        
        System.out.println("DropWhile (n < 3):");
        Stream.of(1, 2, 3, 4, 5)
            .dropWhile(n -> n < 3)
            .forEach(System.out::println);
    }
    
    private static void terminalOperations() {
        System.out.println("\n=== Terminal Operations ===");
        
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana");
        
        System.out.println("forEach:");
        names.stream().forEach(System.out::println);
        
        System.out.println("collect(toList):");
        List<String> collected = names.stream()
            .filter(s -> s.startsWith("A"))
            .collect(Collectors.toList());
        System.out.println(collected);
        
        System.out.println("reduce:");
        int sum = Stream.of(1, 2, 3, 4, 5)
            .reduce(0, Integer::sum);
        System.out.println("sum = " + sum);
        
        System.out.println("count:");
        long count = names.stream().count();
        System.out.println("count = " + count);
        
        System.out.println("anyMatch:");
        boolean anyStartsWithA = names.stream()
            .anyMatch(s -> s.startsWith("A"));
        System.out.println("anyStartsWithA = " + anyStartsWithA);
        
        System.out.println("allMatch:");
        boolean allLength4 = names.stream()
            .allMatch(s -> s.length() == 4);
        System.out.println("allLength4 = " + allLength4);
        
        System.out.println("noneMatch:");
        boolean noneStartsWithZ = names.stream()
            .noneMatch(s -> s.startsWith("Z"));
        System.out.println("noneStartsWithZ = " + noneStartsWithZ);
        
        System.out.println("findFirst:");
        String first = names.stream()
            .filter(s -> s.length() > 3)
            .findFirst()
            .orElse("Not found");
        System.out.println("first = " + first);
        
        System.out.println("findAny:");
        String any = names.stream()
            .findAny()
            .orElse("Not found");
        System.out.println("any = " + any);
        
        System.out.println("min/max:");
        String min = names.stream().min(String::compareTo).orElse("");
        String max = names.stream().max(String::compareTo).orElse("");
        System.out.println("min = " + min + ", max = " + max);
    }
    
    private static void collectors() {
        System.out.println("\n=== Collectors ===");
        
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana");
        
        System.out.println("toList:");
        List<String> list = names.stream()
            .collect(Collectors.toList());
        System.out.println(list);
        
        System.out.println("toSet:");
        List<String> duplicates = Arrays.asList("A", "B", "A", "C", "B");
        Set<String> set = duplicates.stream()
            .collect(Collectors.toSet());
        System.out.println(set);
        
        System.out.println("toMap:");
        Map<String, Integer> nameLengths = names.stream()
            .collect(Collectors.toMap(s -> s, String::length));
        System.out.println(nameLengths);
        
        System.out.println("joining:");
        String joined = names.stream()
            .collect(Collectors.joining(", "));
        System.out.println(joined);
        
        System.out.println("groupingBy:");
        Map<Integer, List<String>> byLength = names.stream()
            .collect(Collectors.groupingBy(String::length));
        System.out.println(byLength);
        
        System.out.println("partitioningBy:");
        Map<Boolean, List<String>> byStartsWith = names.stream()
            .collect(Collectors.partitioningBy(s -> s.startsWith("A")));
        System.out.println(byStartsWith);
        
        System.out.println("counting:");
        Map<Integer, Long> countByLength = names.stream()
            .collect(Collectors.groupingBy(
                String::length,
                Collectors.counting()));
        System.out.println(countByLength);
        
        System.out.println("summarizingInt:");
        IntSummaryStatistics stats = names.stream()
            .collect(Collectors.summarizingInt(String::length));
        System.out.println(stats);
    }
    
    private static void advancedStreams() {
        System.out.println("\n=== Advanced Streams ===");
        
        List<Person> people = Arrays.asList(
            new Person("Alice", 25, "New York"),
            new Person("Bob", 30, "Los Angeles"),
            new Person("Charlie", 25, "New York"),
            new Person("Diana", 35, "Chicago")
        );
        
        System.out.println("Group by age:");
        Map<Integer, List<Person>> byAge = people.stream()
            .collect(Collectors.groupingBy(Person::getAge));
        byAge.forEach((age, list) -> System.out.println(age + ": " + list));
        
        System.out.println("Average age:");
        double avgAge = people.stream()
            .collect(Collectors.averagingInt(Person::getAge));
        System.out.println("Average: " + avgAge);
        
        System.out.println("Sum of ages:");
        int totalAge = people.stream()
            .mapToInt(Person::getAge)
            .sum();
        System.out.println("Total: " + totalAge);
        
        System.out.println("Partition by age >= 30:");
        Map<Boolean, List<Person>> partition = people.stream()
            .collect(Collectors.partitioningBy(p -> p.getAge() >= 30));
        partition.forEach((k, v) -> System.out.println(k + ": " + v));
        
        System.out.println("Mapping names:");
        List<String> names = people.stream()
            .map(Person::getName)
            .collect(Collectors.toList());
        System.out.println(names);
    }
}
```

---

## Step 3: Model Classes

```java
// model/Person.java
package com.learning.model;

public class Person {
    private String name;
    private int age;
    private String city;
    
    public Person(String name, int age, String city) {
        this.name = name;
        this.age = age;
        this.city = city;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    @Override
    public String toString() {
        return name + "(" + age + ", " + city + ")";
    }
}

// model/Product.java
package com.learning.model;

import java.math.BigDecimal;

public class Product {
    private String id;
    private String name;
    private String category;
    private BigDecimal price;
    private double rating;
    
    public Product(String id, String name, String category, 
                 BigDecimal price, double rating) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.rating = rating;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
    public double getRating() { return rating; }
    
    @Override
    public String toString() {
        return name + " [" + category + "] $" + price + " (" + rating + ")";
    }
}

// model/Order.java
package com.learning.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String id;
    private String customerId;
    private List<Product> products;
    private LocalDateTime orderDate;
    private String status;
    
    public Order(String id, String customerId, List<Product> products,
                LocalDateTime orderDate, String status) {
        this.id = id;
        this.customerId = customerId;
        this.products = products;
        this.orderDate = orderDate;
        this.status = status;
    }
    
    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public List<Product> getProducts() { return products; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
}
```

---

## Step 4: Lambda Playground Main

```java
// playground/LambdaPlayground.java
package com.learning.playground;

import com.learning.model.*;
import java.util.*;
import java.util.stream.*;

public class LambdaPlayground {
    
    public static void main(String[] args) {
        demonstrateMethodReferences();
        demonstrateComparator();
        demonstrateAdvancedPatterns();
    }
    
    private static void demonstrateMethodReferences() {
        System.out.println("=== Method References ===");
        
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        
        List<String> upperNames = names.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        System.out.println("toUpperCase: " + upperNames);
        
        List<Integer> lengths = names.stream()
            .map(String::length)
            .collect(Collectors.toList());
        System.out.println("lengths: " + lengths);
        
        String[] nameArray = names.toArray(String[]::new);
        System.out.println("toArray: " + Arrays.toString(nameArray));
        
        Comparator<String> byLength = Comparator.comparingInt(String::length);
        List<String> sorted = names.stream()
            .sorted(byLength)
            .collect(Collectors.toList());
        System.out.println("sorted by length: " + sorted);
        
        Runnable runnable = LambdaPlayground::run;
        runnable.run();
    }
    
    private static void demonstrateComparator() {
        System.out.println("\n=== Comparator ===");
        
        List<Product> products = Arrays.asList(
            new Product("1", "Laptop", "Electronics", 
                new java.math.BigDecimal("999.99"), 4.5),
            new Product("2", "Phone", "Electronics", 
                new java.math.BigDecimal("599.99"), 4.7),
            new Product("3", "Book", "Books", 
                new java.math.BigDecimal("29.99"), 4.2)
        );
        
        List<Product> byPrice = products.stream()
            .sorted(Comparator.comparing(Product::getPrice))
            .collect(Collectors.toList());
        System.out.println("by price: " + byPrice);
        
        List<Product> byRatingDesc = products.stream()
            .sorted(Comparator.comparing(Product::getRating).reversed())
            .collect(Collectors.toList());
        System.out.println("by rating desc: " + byRatingDesc);
        
        List<Product> byCategoryPrice = products.stream()
            .sorted(Comparator
                .comparing(Product::getCategory)
                .thenComparing(Product::getPrice))
            .collect(Collectors.toList());
        System.out.println("by category then price: " + byCategoryPrice);
    }
    
    private static void demonstrateAdvancedPatterns() {
        System.out.println("\n=== Advanced Patterns ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        int result = numbers.stream()
            .reduce(1, (a, b) -> a * b);
        System.out.println("product = " + result);
        
        Optional<String> found = Stream.of("a", "b", "c")
            .filter(s -> s.startsWith("b"))
            .findFirst();
        System.out.println("found: " + found.orElse("not found"));
        
        Map<Boolean, List<Integer>> partitioned = numbers.stream()
            .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        System.out.println("partitioned: " + partitioned);
        
        Map<String, Long> wordCount = Stream.of(
            "hello world", "hello java", "world cup"
        )
            .flatMap(s -> Arrays.stream(s.split(" ")))
            .collect(Collectors.groupingBy(
                w -> w,
                Collectors.counting()));
        System.out.println("wordCount: " + wordCount);
    }
    
    private static void run() {
        System.out.println("Method reference run!");
    }
}
```

---

## Build Instructions

```bash
cd 10-lambda-expressions
javac -d target/classes -sourcepath src/main/java \
    src/main/java/com/learning/playground/*.java \
    src/main/java/com/learning/model/*.java
java -cp target/classes com.learning.playground.LambdaPlayground
```

---

# Real-World Project: E-Commerce Stream Processing System

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Stream API, Parallel Streams, Collectors, Functional Programming, Reactive Streams

This project implements a comprehensive e-commerce data processing system using Java streams.

---

## Project Structure

```
10-lambda-expressions/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── ecommerce/
│   │   ├── EcommerceAnalyzer.java
│   │   ├── OrderProcessor.java
│   │   └── ReportGenerator.java
│   ├── model/
│   │   ├── Product.java
│   │   ├── Customer.java
│   │   ├── Order.java
│   │   └── Review.java
│   ├── repository/
│   │   └── DataRepository.java
│   └── service/
│       ├── AnalyticsService.java
│       └── RecommendationService.java
└── src/main/resources/
    └── data.properties
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
    <artifactId>lambda-ecommerce</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
    </dependencies>
</project>
```

---

## Model Classes

```java
// model/Product.java
package com.learning.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Product {
    private String id;
    private String name;
    private String description;
    private String category;
    private String subcategory;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private int stockQuantity;
    private double rating;
    private int reviewCount;
    private List<String> tags;
    private String brand;
    private String sellerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    
    public Product() {}
    
    public Product(String id, String name, String category, 
                 BigDecimal price, double rating) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.rating = rating;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getSubcategory() { return subcategory; }
    public void setSubcategory(String subcategory) { this.subcategory = subcategory; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public BigDecimal getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(BigDecimal discountedPrice) { 
        this.discountedPrice = discountedPrice; 
    }
    
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { 
        this.stockQuantity = stockQuantity; 
    }
    
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public BigDecimal getEffectivePrice() {
        return discountedPrice != null ? discountedPrice : price;
    }
}

// model/Customer.java
package com.learning.model;

import java.time.LocalDateTime;
import java.util.List;

public class Customer {
    private String id;
    private String name;
    private String email;
    private String phone;
    private Address address;
    private CustomerTier tier;
    private List<String> preferences;
    private LocalDateTime registeredAt;
    private boolean active;
    
    public enum CustomerTier {
        BRONZE, SILVER, GOLD, PLATINUM
    }
    
    public Customer() {}
    
    public Customer(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    
    public CustomerTier getTier() { return tier; }
    public void setTier(CustomerTier tier) { this.tier = tier; }
    
    public List<String> getPreferences() { return preferences; }
    public void setPreferences(List<String> preferences) { 
        this.preferences = preferences; 
    }
    
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { 
        this.registeredAt = registeredAt; 
    }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

public class Address {
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    
    public Address() {}
    
    public Address(String street, String city, String state, 
                  String country, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipCode = zipCode;
    }
    
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    
    @Override
    public String toString() {
        return street + ", " + city + ", " + state + " " + zipCode;
    }
}
```

---

## Repository

```java
// repository/DataRepository.java
package com.learning.repository;

import com.learning.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DataRepository {
    private final List<Product> products;
    private final List<Customer> customers;
    private final List<Order> orders;
    private final List<Review> reviews;
    
    public DataRepository() {
        this.products = generateProducts();
        this.customers = generateCustomers();
        this.orders = generateOrders();
        this.reviews = generateReviews();
    }
    
    public List<Product> getProducts() { return products; }
    public List<Customer> getCustomers() { return customers; }
    public List<Order> getOrders() { return orders; }
    public List<Review> getReviews() { return reviews; }
    
    private List<Product> generateProducts() {
        return List.of(
            new Product("P001", "MacBook Pro", "Electronics", 
                new BigDecimal("2499.99"), 4.8),
            new Product("P002", "iPhone 15", "Electronics", 
                new BigDecimal("999.99"), 4.7),
            new Product("P003", "Sony TV", "Electronics", 
                new BigDecimal("1299.99"), 4.5),
            new Product("P004", "Nike Shoes", "Sports", 
                new BigDecimal("149.99"), 4.3),
            new Product("P005", "Adidas Jacket", "Sports", 
                new BigDecimal("199.99"), 4.4),
            new Product("P006", "Programming Book", "Books", 
                new BigDecimal("49.99"), 4.6),
            new Product("P007", "Coffee Maker", "Home", 
                new BigDecimal("89.99"), 4.2),
            new Product("P008", "Blender", "Home", 
                new BigDecimal("59.99"), 4.1),
            new Product("P009", "Gaming Chair", "Furniture", 
                new BigDecimal("399.99"), 4.5),
            new Product("P010", "Desk Lamp", "Furniture", 
                new BigDecimal("79.99"), 4.3)
        );
    }
    
    private List<Customer> generateCustomers() {
        return List.of(
            new Customer("C001", "John Doe", "john@email.com"),
            new Customer("C002", "Jane Smith", "jane@email.com"),
            new Customer("C003", "Bob Johnson", "bob@email.com"),
            new Customer("C004", "Alice Williams", "alice@email.com"),
            new Customer("C005", "Charlie Brown", "charlie@email.com")
        );
    }
    
    private List<Order> generateOrders() {
        List<Order> orderList = new ArrayList<>();
        
        orderList.add(new Order("O001", "C001", 
            List.of(products.get(0), products.get(1)),
            LocalDateTime.now().minusDays(5), "DELIVERED"));
        
        orderList.add(new Order("O002", "C002",
            List.of(products.get(2)),
            LocalDateTime.now().minusDays(3), "SHIPPED"));
        
        orderList.add(new Order("O003", "C001",
            List.of(products.get(3), products.get(4)),
            LocalDateTime.now().minusDays(2), "PENDING"));
        
        orderList.add(new Order("O004", "C003",
            List.of(products.get(5)),
            LocalDateTime.now().minusDays(1), "DELIVERED"));
        
        orderList.add(new Order("O005", "C004",
            List.of(products.get(6), products.get(7)),
            LocalDateTime.now(), "PROCESSING"));
        
        return orderList;
    }
    
    private List<Review> generateReviews() {
        return List.of(
            new Review("R001", "P001", "C001", 5, "Amazing product!"),
            new Review("R002", "P001", "C002", 4, "Great but expensive"),
            new Review("R003", "P002", "C001", 5, "Best phone ever!"),
            new Review("R004", "P003", "C003", 4, "Good TV"),
            new Review("R005", "P004", "C002", 3, "Decent shoes")
        );
    }
}
```

---

## Analytics Service

```java
// service/AnalyticsService.java
package com.learning.service;

import com.learning.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyticsService {
    private final DataRepository repository;
    
    public AnalyticsService(DataRepository repository) {
        this.repository = repository;
    }
    
    public Map<String, Long> getCategoryProductCount() {
        return repository.getProducts().stream()
            .collect(Collectors.groupingBy(
                Product::getCategory,
                Collectors.counting()));
    }
    
    public Map<String, BigDecimal> getCategoryTotalRevenue() {
        return repository.getOrders().stream()
            .filter(o -> "DELIVERED".equals(o.getStatus()))
            .flatMap(o -> o.getProducts().stream())
            .collect(Collectors.groupingBy(
                Product::getCategory,
                Collectors.reducing(
                    BigDecimal.ZERO,
                    Product::getEffectivePrice,
                    BigDecimal::add)));
    }
    
    public List<Product> getTopRatedProducts(int limit) {
        return repository.getProducts().stream()
            .sorted(Comparator.comparing(Product::getRating).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    public Map<String, Double> getAverageRatingByCategory() {
        return repository.getProducts().stream()
            .collect(Collectors.groupingBy(
                Product::getCategory,
                Collectors.averagingDouble(Product::getRating)));
    }
    
    public List<Customer> getTopCustomers(int limit) {
        return repository.getOrders().stream()
            .collect(Collectors.groupingBy(
                Order::getCustomerId,
                Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .map(e -> repository.getCustomers().stream()
                .filter(c -> c.getId().equals(e.getKey()))
                .findFirst()
                .orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    public Map<String, List<Order>> getOrdersByStatus() {
        return repository.getOrders().stream()
            .collect(Collectors.groupingBy(Order::getStatus));
    }
    
    public DoubleSummaryStatistics getOrderStatistics() {
        return repository.getOrders().stream()
            .collect(Collectors.summarizingDouble(
                o -> o.getProducts().stream()
                    .mapToDouble(p -> p.getEffectivePrice().doubleValue())
                    .sum()));
    }
    
    public Map<String, List<Product>> getProductsByPriceRange(
            BigDecimal min, BigDecimal max) {
        return repository.getProducts().stream()
            .filter(p -> p.getEffectivePrice().compareTo(min) >= 0 &&
                        p.getEffectivePrice().compareTo(max) <= 0)
            .collect(Collectors.groupingBy(Product::getCategory));
    }
}
```

---

## Recommendation Service

```java
// service/RecommendationService.java
package com.learning.service;

import com.learning.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class RecommendationService {
    private final DataRepository repository;
    
    public RecommendationService(DataRepository repository) {
        this.repository = repository;
    }
    
    public List<Product> getRecommendationsForCustomer(String customerId) {
        List<Order> customerOrders = repository.getOrders().stream()
            .filter(o -> o.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
        
        if (customerOrders.isEmpty()) {
            return getPopularProducts(5);
        }
        
        Set<String> purchasedCategories = customerOrders.stream()
            .flatMap(o -> o.getProducts().stream())
            .map(Product::getCategory)
            .collect(Collectors.toSet());
        
        return repository.getProducts().stream()
            .filter(p -> purchasedCategories.contains(p.getCategory()))
            .sorted(Comparator.comparing(Product::getRating).reversed())
            .limit(5)
            .collect(Collectors.toList());
    }
    
    public List<Product> getPopularProducts(int limit) {
        return repository.getProducts().stream()
            .sorted(Comparator.comparing(Product::getRating).reversed())
            .thenComparing(Product::getReviewCount)
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    public List<Product> getSimilarProducts(String productId) {
        Product target = repository.getProducts().stream()
            .filter(p -> p.getId().equals(productId))
            .findFirst()
            .orElse(null);
        
        if (target == null) {
            return Collections.emptyList();
        }
        
        return repository.getProducts().stream()
            .filter(p -> !p.getId().equals(productId))
            .filter(p -> p.getCategory().equals(target.getCategory()))
            .sorted(Comparator.comparingDouble(
                p -> Math.abs(p.getRating() - target.getRating())))
            .limit(5)
            .collect(Collectors.toList());
    }
    
    public List<Product> getFrequentlyPurchasedTogether(
            String productId) {
        return repository.getOrders().stream()
            .filter(o -> o.getProducts().stream()
                .anyMatch(p -> p.getId().equals(productId)))
            .flatMap(o -> o.getProducts().stream())
            .filter(p -> !p.getId().equals(productId))
            .collect(Collectors.groupingBy(
                p -> p,
                Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<Product, Long>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .limit(3)
            .collect(Collectors.toList());
    }
}
```

---

## Order Processor

```java
// ecommerce/OrderProcessor.java
package com.learning.ecommerce;

import com.learning.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class OrderProcessor {
    
    public List<Order> processOrders(List<Order> orders) {
        return orders.stream()
            .map(this::processOrder)
            .collect(Collectors.toList());
    }
    
    private Order processOrder(Order order) {
        if ("PENDING".equals(order.getStatus())) {
            order.setStatus("PROCESSING");
        }
        return order;
    }
    
    public Map<String, List<Order>> groupByCustomer(List<Order> orders) {
        return orders.stream()
            .collect(Collectors.groupingBy(Order::getCustomerId));
    }
    
    public List<Order> filterByStatus(List<Order> orders, String status) {
        return orders.stream()
            .filter(o -> o.getStatus().equals(status))
            .collect(Collectors.toList());
    }
    
    public BigDecimal calculateTotal(Order order) {
        return order.getProducts().stream()
            .map(Product::getEffectivePrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public Optional<Order> findLargestOrder(List<Order> orders) {
        return orders.stream()
            .max(Comparator.comparing(this::calculateTotal));
    }
}
```

---

## Report Generator

```java
// ecommerce/ReportGenerator.java
package com.learning.ecommerce;

import com.learning.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class ReportGenerator {
    private final DataRepository repository;
    
    public ReportGenerator(DataRepository repository) {
        this.repository = repository;
    }
    
    public String generateSalesReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Sales Report ===\n\n");
        
        Map<String, Long> ordersByStatus = repository.getOrders().stream()
            .collect(Collectors.groupingBy(
                Order::getStatus,
                Collectors.counting()));
        
        sb.append("Orders by Status:\n");
        ordersByStatus.forEach((status, count) -> 
            sb.append(String.format("  %s: %d\n", status, count)));
        
        double totalRevenue = repository.getOrders().stream()
            .filter(o -> "DELIVERED".equals(o.getStatus()))
            .flatMap(o -> o.getProducts().stream())
            .mapToDouble(p -> p.getEffectivePrice().doubleValue())
            .sum();
        
        sb.append(String.format("\nTotal Revenue: $%.2f\n", totalRevenue));
        
        return sb.toString();
    }
    
    public String generateProductReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Product Report ===\n\n");
        
        Map<String, Long> productCount = repository.getProducts().stream()
            .collect(Collectors.groupingBy(
                Product::getCategory,
                Collectors.counting()));
        
        sb.append("Products by Category:\n");
        productCount.forEach((category, count) -> 
            sb.append(String.format("  %s: %d\n", category, count)));
        
        Map<String, Double> avgRating = repository.getProducts().stream()
            .collect(Collectors.groupingBy(
                Product::getCategory,
                Collectors.averagingDouble(Product::getRating)));
        
        sb.append("\nAverage Rating by Category:\n");
        avgRating.forEach((category, rating) -> 
            sb.append(String.format("  %s: %.2f\n", category, rating)));
        
        return sb.toString();
    }
}
```

---

## Main Application

```java
// Main.java
package com.learning;

import com.learning.ecommerce.*;
import com.learning.repository.DataRepository;
import com.learning.service.*;
import com.learning.model.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        DataRepository repository = new DataRepository();
        
        System.out.println("=== E-Commerce Stream Processing ===\n");
        
        AnalyticsService analytics = new AnalyticsService(repository);
        
        System.out.println("Category Product Count:");
        analytics.getCategoryProductCount()
            .forEach((k, v) -> System.out.println("  " + k + ": " + v));
        
        System.out.println("\nTop 3 Rated Products:");
        analytics.getTopRatedProducts(3)
            .forEach(p -> System.out.println("  " + p.getName() + 
                " (Rating: " + p.getRating() + ")"));
        
        RecommendationService recommendations = 
            new RecommendationService(repository);
        
        System.out.println("\nRecommendations for Customer C001:");
        recommendations.getRecommendationsForCustomer("C001")
            .forEach(p -> System.out.println("  " + p.getName()));
        
        System.out.println("\nPopular Products:");
        recommendations.getPopularProducts(3)
            .forEach(p -> System.out.println("  " + p.getName()));
        
        OrderProcessor processor = new OrderProcessor();
        
        System.out.println("\n=== Processing Orders ===");
        repository.getOrders().stream()
            .map(o -> processor.calculateTotal(o))
            .forEach(t -> System.out.println("Order Total: $" + t));
        
        ReportGenerator reportGen = new ReportGenerator(repository);
        System.out.println("\n" + reportGen.generateSalesReport());
        System.out.println(reportGen.generateProductReport());
    }
}
```

---

## Build Instructions

```bash
cd 10-lambda-expressions
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

This project demonstrates advanced stream operations for real-world e-commerce analysis, including aggregation, grouping, filtering, and parallel processing capabilities.