# Lab 09: Generics

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Intermediate |
| **Estimated Time** | 4 hours |
| **Real-World Context** | Building a generic data repository system |
| **Prerequisites** | Lab 08: Collections Framework |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Understand generics** and type parameters
2. **Create generic classes and methods**
3. **Use bounded type parameters**
4. **Work with wildcards** effectively
5. **Apply PECS principle** (Producer Extends, Consumer Super)
6. **Build a generic data repository system**

## 📚 Prerequisites

- Lab 08: Collections Framework completed
- Understanding of collections
- Knowledge of inheritance
- Familiarity with interfaces

## 🧠 Concept Theory

### 1. Generics Basics

Type-safe collections and classes:

```java
// Without generics (type casting required)
List list = new ArrayList();
list.add("Hello");
String str = (String) list.get(0);  // Casting needed

// With generics (type-safe)
List<String> list = new ArrayList<>();
list.add("Hello");
String str = list.get(0);  // No casting needed

// Generic class
public class Box<T> {
    private T value;
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }
}

// Usage
Box<String> stringBox = new Box<>();
stringBox.setValue("Hello");
String value = stringBox.getValue();

Box<Integer> intBox = new Box<>();
intBox.setValue(42);
Integer intValue = intBox.getValue();
```

### 2. Generic Classes

Creating reusable generic classes:

```java
// Generic class with single type parameter
public class Container<T> {
    private T item;
    
    public Container(T item) {
        this.item = item;
    }
    
    public T getItem() {
        return item;
    }
    
    public void setItem(T item) {
        this.item = item;
    }
}

// Generic class with multiple type parameters
public class Pair<K, V> {
    private K key;
    private V value;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    public K getKey() {
        return key;
    }
    
    public V getValue() {
        return value;
    }
}

// Usage
Pair<String, Integer> pair = new Pair<>("Age", 25);
String key = pair.getKey();
Integer value = pair.getValue();
```

### 3. Generic Methods

Methods with type parameters:

```java
// Generic method
public class Utilities {
    // Single type parameter
    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.println(element);
        }
    }
    
    // Multiple type parameters
    public static <K, V> void printMap(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    
    // Return generic type
    public static <T> T getFirst(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }
}

// Usage
String[] strings = {"A", "B", "C"};
Utilities.printArray(strings);

Integer[] integers = {1, 2, 3};
Utilities.printArray(integers);

String first = Utilities.getFirst(Arrays.asList("A", "B", "C"));
```

### 4. Bounded Type Parameters

Restricting type parameters:

```java
// Upper bound - extends
public class NumberBox<T extends Number> {
    private T value;
    
    public NumberBox(T value) {
        this.value = value;
    }
    
    public double doubleValue() {
        return value.doubleValue();
    }
}

// Usage
NumberBox<Integer> intBox = new NumberBox<>(42);
NumberBox<Double> doubleBox = new NumberBox<>(3.14);
// NumberBox<String> stringBox = new NumberBox<>("Hello");  // ERROR

// Multiple bounds
public class Comparable<T extends Number & Comparable<T>> {
    // T must extend Number AND implement Comparable
}

// Lower bound - super
public static void addNumbers(List<? super Integer> list) {
    list.add(1);
    list.add(2);
    list.add(3);
}

// Usage
List<Number> numbers = new ArrayList<>();
addNumbers(numbers);  // OK
```

### 5. Wildcards

Using wildcards for flexibility:

```java
// Unbounded wildcard
public static void printList(List<?> list) {
    for (Object item : list) {
        System.out.println(item);
    }
}

// Upper bounded wildcard (extends)
public static double sumNumbers(List<? extends Number> list) {
    double sum = 0;
    for (Number num : list) {
        sum += num.doubleValue();
    }
    return sum;
}

// Lower bounded wildcard (super)
public static void addIntegers(List<? super Integer> list) {
    list.add(1);
    list.add(2);
    list.add(3);
}

// Usage
List<Integer> integers = Arrays.asList(1, 2, 3);
double sum = sumNumbers(integers);

List<Number> numbers = new ArrayList<>();
addIntegers(numbers);
```

### 6. PECS Principle

Producer Extends, Consumer Super:

```java
// Producer (read-only) - use extends
public static double sumNumbers(List<? extends Number> list) {
    double sum = 0;
    for (Number num : list) {
        sum += num.doubleValue();
    }
    return sum;
}

// Consumer (write-only) - use super
public static void fillNumbers(List<? super Integer> list) {
    list.add(1);
    list.add(2);
    list.add(3);
}

// Both - use exact type
public static <T> void copy(List<? extends T> source, List<? super T> dest) {
    for (T item : source) {
        dest.add(item);
    }
}
```

### 7. Type Erasure

How generics work at runtime:

```java
// Generics are erased at runtime
List<String> stringList = new ArrayList<>();
List<Integer> intList = new ArrayList<>();

// At runtime, both are just List
System.out.println(stringList.getClass() == intList.getClass());  // true

// Type information is lost
List<String> list = new ArrayList<>();
// list.getClass().getTypeParameters();  // Returns raw types

// Cannot use instanceof with generics
// if (list instanceof List<String>) { }  // ERROR

// Cannot create generic arrays
// List<String>[] array = new ArrayList<String>[10];  // ERROR
```

### 8. Generic Inheritance

Extending generic classes:

```java
// Generic parent class
public class Container<T> {
    protected T item;
    
    public void setItem(T item) {
        this.item = item;
    }
    
    public T getItem() {
        return item;
    }
}

// Concrete child class
public class StringContainer extends Container<String> {
    // T is bound to String
}

// Generic child class
public class ExtendedContainer<T> extends Container<T> {
    public void printItem() {
        System.out.println(item);
    }
}

// Usage
StringContainer stringContainer = new StringContainer();
stringContainer.setItem("Hello");

ExtendedContainer<Integer> intContainer = new ExtendedContainer<>();
intContainer.setItem(42);
```

### 9. Generic Interfaces

Implementing generic interfaces:

```java
// Generic interface
public interface Repository<T> {
    void save(T item);
    T findById(String id);
    List<T> findAll();
    void delete(String id);
}

// Concrete implementation
public class UserRepository implements Repository<User> {
    private Map<String, User> users = new HashMap<>();
    
    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }
    
    @Override
    public User findById(String id) {
        return users.get(id);
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public void delete(String id) {
        users.remove(id);
    }
}

// Generic implementation
public class GenericRepository<T> implements Repository<T> {
    private Map<String, T> items = new HashMap<>();
    
    @Override
    public void save(T item) {
        // Implementation
    }
    
    @Override
    public T findById(String id) {
        return items.get(id);
    }
    
    @Override
    public List<T> findAll() {
        return new ArrayList<>(items.values());
    }
    
    @Override
    public void delete(String id) {
        items.remove(id);
    }
}
```

### 10. Best Practices

Generic programming guidelines:

```java
// ❌ Bad: Raw types
List list = new ArrayList();  // Raw type
list.add("Hello");
String str = (String) list.get(0);  // Casting needed

// ✅ Good: Use generics
List<String> list = new ArrayList<>();
list.add("Hello");
String str = list.get(0);  // Type-safe

// ❌ Bad: Unbounded wildcards
public void process(List<?> list) {
    // Can only read, cannot write
}

// ✅ Good: Bounded wildcards
public void process(List<? extends Number> list) {
    // Clear intent: reading numbers
}

// ❌ Bad: Mixing generics and raw types
List<String> strings = new ArrayList();  // Raw type on right
strings.add("Hello");

// ✅ Good: Consistent generics
List<String> strings = new ArrayList<>();
strings.add("Hello");

// ❌ Bad: Generic arrays
List<String>[] arrays = new ArrayList[10];  // Unchecked

// ✅ Good: Use List of Lists
List<List<String>> lists = new ArrayList<>();
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Create Generic Classes

**Objective**: Implement generic classes with type parameters

**Acceptance Criteria**:
- [ ] Generic class with single type parameter
- [ ] Generic class with multiple type parameters
- [ ] Proper type safety
- [ ] Works with different types
- [ ] Code compiles without warnings

**Instructions**:
1. Create generic Box class
2. Create generic Pair class
3. Test with different types
4. Verify type safety
5. No casting needed

### Task 2: Create Generic Methods

**Objective**: Implement generic methods for reusability

**Acceptance Criteria**:
- [ ] Generic method with type parameter
- [ ] Works with different types
- [ ] Type inference works
- [ ] Proper bounds applied
- [ ] No casting needed

**Instructions**:
1. Create generic utility methods
2. Implement type parameters
3. Test with different types
4. Verify type safety
5. Use type inference

### Task 3: Use Bounded Types

**Objective**: Apply bounded type parameters

**Acceptance Criteria**:
- [ ] Upper bounded type parameter
- [ ] Lower bounded wildcard
- [ ] PECS principle applied
- [ ] Proper restrictions enforced
- [ ] Type safety maintained

**Instructions**:
1. Create bounded generic class
2. Implement upper bounds
3. Use wildcards appropriately
4. Apply PECS principle
5. Test restrictions

---

## 🎨 Mini-Project: Generic Data Repository System

### Project Overview

**Description**: Create a generic repository system for managing different data types.

**Real-World Application**: ORM frameworks, data access layers, persistence systems.

**Learning Value**: Master generics, type safety, and reusable components.

### Project Requirements

#### Functional Requirements
- [ ] Generic repository interface
- [ ] Multiple repository implementations
- [ ] CRUD operations
- [ ] Search and filtering
- [ ] Data persistence
- [ ] Transaction support

#### Non-Functional Requirements
- [ ] Type-safe code
- [ ] Reusable components
- [ ] Comprehensive documentation
- [ ] Unit tests
- [ ] Error handling

### Project Structure

```
generic-repository-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── Repository.java (interface)
│   │           ├── GenericRepository.java
│   │           ├── Entity.java (interface)
│   │           ├── User.java
│   │           ├── Product.java
│   │           ├── UserRepository.java
│   │           ├── ProductRepository.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               ├── RepositoryTest.java
│               └── GenericRepositoryTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create Entity Interface

```java
package com.learning;

/**
 * Base interface for entities.
 */
public interface Entity {
    String getId();
    void setId(String id);
}
```

#### Step 2: Create Repository Interface

```java
package com.learning;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface.
 */
public interface Repository<T extends Entity> {
    /**
     * Save entity.
     */
    void save(T entity);
    
    /**
     * Find by ID.
     */
    Optional<T> findById(String id);
    
    /**
     * Find all entities.
     */
    List<T> findAll();
    
    /**
     * Update entity.
     */
    void update(T entity);
    
    /**
     * Delete entity.
     */
    void delete(String id);
    
    /**
     * Get count.
     */
    int count();
    
    /**
     * Check if exists.
     */
    boolean exists(String id);
}
```

#### Step 3: Create GenericRepository Class

```java
package com.learning;

import java.util.*;

/**
 * Generic repository implementation.
 */
public class GenericRepository<T extends Entity> implements Repository<T> {
    protected Map<String, T> storage;
    
    /**
     * Constructor for GenericRepository.
     */
    public GenericRepository() {
        this.storage = new HashMap<>();
    }
    
    @Override
    public void save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        if (entity.getId() == null) {
            entity.setId(generateId());
        }
        storage.put(entity.getId(), entity);
    }
    
    @Override
    public Optional<T> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }
    
    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }
    
    @Override
    public void update(T entity) {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Invalid entity");
        }
        if (!storage.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Entity not found");
        }
        storage.put(entity.getId(), entity);
    }
    
    @Override
    public void delete(String id) {
        storage.remove(id);
    }
    
    @Override
    public int count() {
        return storage.size();
    }
    
    @Override
    public boolean exists(String id) {
        return storage.containsKey(id);
    }
    
    /**
     * Generate unique ID.
     */
    protected String generateId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Find by predicate.
     */
    public List<T> findBy(java.util.function.Predicate<T> predicate) {
        return storage.values().stream()
            .filter(predicate)
            .collect(java.util.stream.Collectors.toList());
    }
}
```

#### Step 4: Create User Class

```java
package com.learning;

/**
 * User entity.
 */
public class User implements Entity {
    private String id;
    private String name;
    private String email;
    private int age;
    
    /**
     * Constructor for User.
     */
    public User(String name, String email, int age) {
        setName(name);
        setEmail(email);
        setAge(age);
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.email = email;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Invalid age");
        }
        this.age = age;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User other = (User) obj;
        return id != null && id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
```

#### Step 5: Create Product Class

```java
package com.learning;

/**
 * Product entity.
 */
public class Product implements Entity {
    private String id;
    private String name;
    private double price;
    private int quantity;
    
    /**
     * Constructor for Product.
     */
    public Product(String name, double price, int quantity) {
        setName(name);
        setPrice(price);
        setQuantity(quantity);
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + String.format("%.2f", price) +
                ", quantity=" + quantity +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product other = (Product) obj;
        return id != null && id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
```

#### Step 6: Create Specialized Repositories

```java
package com.learning;

import java.util.List;

/**
 * User repository.
 */
public class UserRepository extends GenericRepository<User> {
    /**
     * Find by email.
     */
    public User findByEmail(String email) {
        return findBy(u -> u.getEmail().equals(email))
            .stream()
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Find by age range.
     */
    public List<User> findByAgeRange(int minAge, int maxAge) {
        return findBy(u -> u.getAge() >= minAge && u.getAge() <= maxAge);
    }
}

/**
 * Product repository.
 */
public class ProductRepository extends GenericRepository<Product> {
    /**
     * Find by price range.
     */
    public List<Product> findByPriceRange(double minPrice, double maxPrice) {
        return findBy(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice);
    }
    
    /**
     * Find low stock products.
     */
    public List<Product> findLowStock(int threshold) {
        return findBy(p -> p.getQuantity() < threshold);
    }
}
```

#### Step 7: Create Main Class

```java
package com.learning;

/**
 * Main entry point for Generic Repository System.
 */
public class Main {
    public static void main(String[] args) {
        // Create repositories
        UserRepository userRepo = new UserRepository();
        ProductRepository productRepo = new ProductRepository();
        
        // Add users
        User user1 = new User("John Doe", "john@example.com", 30);
        User user2 = new User("Jane Smith", "jane@example.com", 25);
        User user3 = new User("Bob Johnson", "bob@example.com", 35);
        
        userRepo.save(user1);
        userRepo.save(user2);
        userRepo.save(user3);
        
        // Add products
        Product product1 = new Product("Laptop", 999.99, 10);
        Product product2 = new Product("Mouse", 29.99, 50);
        Product product3 = new Product("Keyboard", 79.99, 5);
        
        productRepo.save(product1);
        productRepo.save(product2);
        productRepo.save(product3);
        
        // Display all users
        System.out.println("\n=== All Users ===");
        userRepo.findAll().forEach(System.out::println);
        
        // Display all products
        System.out.println("\n=== All Products ===");
        productRepo.findAll().forEach(System.out::println);
        
        // Find by email
        System.out.println("\n=== Find User by Email ===");
        User found = userRepo.findByEmail("john@example.com");
        System.out.println(found);
        
        // Find by age range
        System.out.println("\n=== Users aged 25-30 ===");
        userRepo.findByAgeRange(25, 30).forEach(System.out::println);
        
        // Find by price range
        System.out.println("\n=== Products $50-$100 ===");
        productRepo.findByPriceRange(50, 100).forEach(System.out::println);
        
        // Find low stock
        System.out.println("\n=== Low Stock Products (< 20) ===");
        productRepo.findLowStock(20).forEach(System.out::println);
        
        // Display counts
        System.out.println("\n=== Repository Counts ===");
        System.out.println("Total Users: " + userRepo.count());
        System.out.println("Total Products: " + productRepo.count());
    }
}
```

#### Step 8: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for generic repository.
 */
public class GenericRepositoryTest {
    
    private GenericRepository<User> repository;
    private User user;
    
    @BeforeEach
    void setUp() {
        repository = new GenericRepository<>();
        user = new User("John", "john@example.com", 30);
    }
    
    @Test
    void testSave() {
        repository.save(user);
        assertEquals(1, repository.count());
    }
    
    @Test
    void testFindById() {
        repository.save(user);
        Optional<User> found = repository.findById(user.getId());
        assertTrue(found.isPresent());
        assertEquals(user, found.get());
    }
    
    @Test
    void testFindAll() {
        repository.save(user);
        User user2 = new User("Jane", "jane@example.com", 25);
        repository.save(user2);
        assertEquals(2, repository.findAll().size());
    }
    
    @Test
    void testUpdate() {
        repository.save(user);
        user.setName("Updated");
        repository.update(user);
        assertEquals("Updated", repository.findById(user.getId()).get().getName());
    }
    
    @Test
    void testDelete() {
        repository.save(user);
        repository.delete(user.getId());
        assertEquals(0, repository.count());
    }
    
    @Test
    void testExists() {
        repository.save(user);
        assertTrue(repository.exists(user.getId()));
        assertFalse(repository.exists("nonexistent"));
    }
}

/**
 * Unit tests for specialized repositories.
 */
public class RepositoryTest {
    
    private UserRepository userRepo;
    private ProductRepository productRepo;
    
    @BeforeEach
    void setUp() {
        userRepo = new UserRepository();
        productRepo = new ProductRepository();
    }
    
    @Test
    void testFindUserByEmail() {
        User user = new User("John", "john@example.com", 30);
        userRepo.save(user);
        User found = userRepo.findByEmail("john@example.com");
        assertEquals(user, found);
    }
    
    @Test
    void testFindUserByAgeRange() {
        userRepo.save(new User("John", "john@example.com", 30));
        userRepo.save(new User("Jane", "jane@example.com", 25));
        userRepo.save(new User("Bob", "bob@example.com", 35));
        assertEquals(2, userRepo.findByAgeRange(25, 30).size());
    }
    
    @Test
    void testFindProductByPriceRange() {
        productRepo.save(new Product("Laptop", 999.99, 10));
        productRepo.save(new Product("Mouse", 29.99, 50));
        productRepo.save(new Product("Keyboard", 79.99, 5));
        assertEquals(1, productRepo.findByPriceRange(50, 100).size());
    }
    
    @Test
    void testFindLowStock() {
        productRepo.save(new Product("Laptop", 999.99, 10));
        productRepo.save(new Product("Mouse", 29.99, 50));
        productRepo.save(new Product("Keyboard", 79.99, 5));
        assertEquals(2, productRepo.findLowStock(20).size());
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

### Exercise 1: Generic Stack Implementation

**Objective**: Implement a generic stack data structure

**Task Description**:
Create a generic stack with push, pop, and peek operations

**Acceptance Criteria**:
- [ ] Generic Stack class
- [ ] Push operation
- [ ] Pop operation
- [ ] Peek operation
- [ ] Size tracking

### Exercise 2: Generic Cache System

**Objective**: Build a generic caching system

**Task Description**:
Create a generic cache with TTL and eviction policies

**Acceptance Criteria**:
- [ ] Generic Cache class
- [ ] Put/Get operations
- [ ] TTL support
- [ ] Eviction policies
- [ ] Statistics

### Exercise 3: Generic Tree Structure

**Objective**: Implement a generic tree data structure

**Task Description**:
Create a generic tree with traversal operations

**Acceptance Criteria**:
- [ ] Generic Tree class
- [ ] Add/Remove nodes
- [ ] Tree traversal
- [ ] Search operations
- [ ] Tree statistics

---

## 🧪 Quiz

### Question 1: What is a type parameter?

A) A parameter of type Object  
B) A placeholder for a type  
C) A generic interface  
D) A wildcard  

**Answer**: B) A placeholder for a type

### Question 2: What does <T extends Number> mean?

A) T can be any type  
B) T must be Number or subclass  
C) T must be a Number instance  
D) T is a wildcard  

**Answer**: B) T must be Number or subclass

### Question 3: What is type erasure?

A) Removing generic information at runtime  
B) Removing type parameters  
C) Removing type safety  
D) Removing generics  

**Answer**: A) Removing generic information at runtime

### Question 4: When should you use <? extends T>?

A) When writing to collection  
B) When reading from collection  
C) When both reading and writing  
D) Never  

**Answer**: B) When reading from collection

### Question 5: Can you create generic arrays?

A) Yes  
B) No  
C) Only with wildcards  
D) Only with bounds  

**Answer**: B) No

---

## 🚀 Advanced Challenge

### Challenge: Complete Generic Framework

**Difficulty**: Intermediate

**Objective**: Build comprehensive generic framework with advanced features

**Requirements**:
- [ ] Generic repository pattern
- [ ] Query builder
- [ ] Lazy loading
- [ ] Caching layer
- [ ] Transaction support
- [ ] Performance optimization

---

## 🏆 Best Practices

### Generic Programming

1. **Use Generics**
   - Avoid raw types
   - Provide type safety
   - Enable reusability

2. **Bounded Types**
   - Restrict when needed
   - Use PECS principle
   - Clear intent

3. **Wildcards**
   - Use appropriately
   - Extends for producers
   - Super for consumers

---

## 🔗 Next Steps

**Next Lab**: [Lab 10: Functional Programming](../10-functional-programming/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built generic repository system
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 09! 🎉**

You've mastered generics and type-safe programming. Ready for functional programming? Move on to [Lab 10: Functional Programming](../10-functional-programming/README.md).