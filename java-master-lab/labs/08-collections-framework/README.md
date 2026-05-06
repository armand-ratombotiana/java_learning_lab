# Lab 08: Collections Framework

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Intermediate |
| **Estimated Time** | 5 hours |
| **Real-World Context** | Building an inventory management system with collections |
| **Prerequisites** | Lab 07: Exception Handling |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Understand the Collections Framework** hierarchy and interfaces
2. **Use Lists, Sets, Maps, and Queues** effectively
3. **Choose appropriate collection types** for different scenarios
4. **Implement sorting and searching** operations
5. **Work with iterators and streams** (introduction)
6. **Build an inventory management system** with collections

## 📚 Prerequisites

- Lab 07: Exception Handling completed
- Understanding of interfaces
- Knowledge of generics (basic)
- Familiarity with exception handling

## 🧠 Concept Theory

### 1. Collections Framework Overview

The Collections Framework provides interfaces and implementations for storing groups of objects:

```java
// Collections hierarchy
// Collection (interface)
//   ├── List (ordered, allows duplicates)
//   ├── Set (unordered, no duplicates)
//   └── Queue (FIFO ordering)
// Map (separate hierarchy, key-value pairs)

// Using List
List<String> fruits = new ArrayList<>();
fruits.add("Apple");
fruits.add("Banana");
fruits.add("Apple");  // Duplicates allowed
System.out.println(fruits.size());  // 3

// Using Set
Set<String> uniqueFruits = new HashSet<>();
uniqueFruits.add("Apple");
uniqueFruits.add("Banana");
uniqueFruits.add("Apple");  // Duplicate ignored
System.out.println(uniqueFruits.size());  // 2

// Using Map
Map<String, Integer> prices = new HashMap<>();
prices.put("Apple", 1);
prices.put("Banana", 2);
System.out.println(prices.get("Apple"));  // 1
```

### 2. List Interface

Ordered collection allowing duplicates:

```java
// ArrayList - resizable array
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");
list.add("C");
System.out.println(list.get(0));  // A
list.set(1, "X");  // Replace
list.remove(2);    // Remove

// LinkedList - doubly-linked list
List<String> linked = new LinkedList<>();
linked.add("A");
linked.addFirst("Z");  // Add at beginning
linked.addLast("Y");   // Add at end
System.out.println(linked.getFirst());  // Z

// Iteration
for (String item : list) {
    System.out.println(item);
}

// Using Iterator
Iterator<String> iterator = list.iterator();
while (iterator.hasNext()) {
    System.out.println(iterator.next());
}
```

### 3. Set Interface

Unordered collection with no duplicates:

```java
// HashSet - unordered, fast lookup
Set<String> hashSet = new HashSet<>();
hashSet.add("Apple");
hashSet.add("Banana");
hashSet.add("Apple");  // Ignored
System.out.println(hashSet.size());  // 2

// TreeSet - sorted, slower lookup
Set<String> treeSet = new TreeSet<>();
treeSet.add("Banana");
treeSet.add("Apple");
treeSet.add("Cherry");
for (String item : treeSet) {
    System.out.println(item);  // Alphabetical order
}

// LinkedHashSet - insertion order
Set<String> linkedSet = new LinkedHashSet<>();
linkedSet.add("C");
linkedSet.add("A");
linkedSet.add("B");
for (String item : linkedSet) {
    System.out.println(item);  // C, A, B (insertion order)
}

// Set operations
Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3));
Set<Integer> set2 = new HashSet<>(Arrays.asList(2, 3, 4));

set1.retainAll(set2);  // Intersection: {2, 3}
set1.addAll(set2);     // Union
set1.removeAll(set2);  // Difference
```

### 4. Map Interface

Key-value pairs:

```java
// HashMap - unordered, fast lookup
Map<String, Integer> map = new HashMap<>();
map.put("Apple", 1);
map.put("Banana", 2);
System.out.println(map.get("Apple"));  // 1
System.out.println(map.containsKey("Apple"));  // true

// TreeMap - sorted by key
Map<String, Integer> treeMap = new TreeMap<>();
treeMap.put("Banana", 2);
treeMap.put("Apple", 1);
treeMap.put("Cherry", 3);
for (String key : treeMap.keySet()) {
    System.out.println(key);  // Alphabetical order
}

// LinkedHashMap - insertion order
Map<String, Integer> linkedMap = new LinkedHashMap<>();
linkedMap.put("C", 3);
linkedMap.put("A", 1);
linkedMap.put("B", 2);
for (String key : linkedMap.keySet()) {
    System.out.println(key);  // C, A, B (insertion order)
}

// Iteration
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}

// Get with default
int value = map.getOrDefault("Orange", 0);  // 0 if not found
```

### 5. Queue Interface

FIFO (First-In-First-Out) ordering:

```java
// Queue - FIFO
Queue<String> queue = new LinkedList<>();
queue.add("A");
queue.add("B");
queue.add("C");
System.out.println(queue.peek());  // A (without removing)
System.out.println(queue.poll());  // A (with removing)

// PriorityQueue - ordered by priority
Queue<Integer> pq = new PriorityQueue<>();
pq.add(3);
pq.add(1);
pq.add(2);
while (!pq.isEmpty()) {
    System.out.println(pq.poll());  // 1, 2, 3 (ascending order)
}

// Deque - double-ended queue
Deque<String> deque = new LinkedList<>();
deque.addFirst("A");
deque.addLast("B");
System.out.println(deque.removeFirst());  // A
System.out.println(deque.removeLast());   // B
```

### 6. Sorting Collections

Ordering elements:

```java
// Sort List
List<Integer> numbers = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5));
Collections.sort(numbers);  // Ascending
System.out.println(numbers);  // [1, 1, 3, 4, 5]

Collections.sort(numbers, Collections.reverseOrder());  // Descending
System.out.println(numbers);  // [5, 4, 3, 1, 1]

// Sort custom objects
List<Person> people = new ArrayList<>();
people.add(new Person("John", 30));
people.add(new Person("Jane", 25));

// Using Comparable
Collections.sort(people);  // Uses compareTo method

// Using Comparator
Collections.sort(people, (p1, p2) -> p1.getAge() - p2.getAge());

// Sort by multiple criteria
Collections.sort(people, Comparator
    .comparing(Person::getAge)
    .thenComparing(Person::getName));
```

### 7. Searching Collections

Finding elements:

```java
// Linear search
List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
int index = list.indexOf(3);  // 2
boolean contains = list.contains(3);  // true

// Binary search (requires sorted list)
Collections.sort(list);
int searchIndex = Collections.binarySearch(list, 3);  // 2

// Custom search
List<Person> people = new ArrayList<>();
Person found = people.stream()
    .filter(p -> p.getName().equals("John"))
    .findFirst()
    .orElse(null);
```

### 8. Collections Utility Methods

Helper methods:

```java
// Create immutable collections
List<String> immutableList = Collections.unmodifiableList(
    new ArrayList<>(Arrays.asList("A", "B", "C"))
);

// Create synchronized collections
List<String> syncList = Collections.synchronizedList(new ArrayList<>());

// Fill collection
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));
Collections.fill(list, "X");  // ["X", "X", "X"]

// Reverse
Collections.reverse(list);

// Shuffle
Collections.shuffle(list);

// Frequency
int count = Collections.frequency(list, "A");

// Min/Max
int min = Collections.min(Arrays.asList(3, 1, 4, 1, 5));
int max = Collections.max(Arrays.asList(3, 1, 4, 1, 5));
```

### 9. Stream Operations (Introduction)

Functional-style operations:

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Filter
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());

// Map
List<Integer> doubled = numbers.stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());

// Reduce
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);

// Collect to different types
Set<Integer> set = numbers.stream()
    .collect(Collectors.toSet());

Map<Integer, Integer> map = numbers.stream()
    .collect(Collectors.toMap(n -> n, n -> n * 2));
```

### 10. Performance Considerations

Choosing the right collection:

```java
// ArrayList: Fast random access, slow insertion/deletion
List<String> arrayList = new ArrayList<>();  // O(1) get, O(n) add/remove

// LinkedList: Slow random access, fast insertion/deletion
List<String> linkedList = new LinkedList<>();  // O(n) get, O(1) add/remove

// HashSet: Fast lookup, unordered
Set<String> hashSet = new HashSet<>();  // O(1) add/remove/contains

// TreeSet: Slower lookup, ordered
Set<String> treeSet = new TreeSet<>();  // O(log n) add/remove/contains

// HashMap: Fast lookup, unordered
Map<String, Integer> hashMap = new HashMap<>();  // O(1) get/put

// TreeMap: Slower lookup, ordered
Map<String, Integer> treeMap = new TreeMap<>();  // O(log n) get/put

// Selection guide:
// - Need fast random access? → ArrayList
// - Need fast insertion/deletion? → LinkedList
// - Need unique elements? → HashSet
// - Need sorted elements? → TreeSet
// - Need key-value pairs? → HashMap
// - Need sorted key-value pairs? → TreeMap
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Work with Lists

**Objective**: Implement list operations and understand different list types

**Acceptance Criteria**:
- [ ] Create ArrayList and LinkedList
- [ ] Add, remove, and access elements
- [ ] Iterate through lists
- [ ] Sort and search
- [ ] Code compiles without errors

**Instructions**:
1. Create ArrayList of strings
2. Add multiple elements
3. Remove specific elements
4. Sort the list
5. Search for elements

### Task 2: Work with Sets and Maps

**Objective**: Use sets for unique elements and maps for key-value pairs

**Acceptance Criteria**:
- [ ] Create HashSet and TreeSet
- [ ] Create HashMap and TreeMap
- [ ] Add and retrieve elements
- [ ] Iterate through collections
- [ ] Perform set operations

**Instructions**:
1. Create HashSet and TreeSet
2. Add duplicate elements
3. Create HashMap with key-value pairs
4. Retrieve values by key
5. Iterate and display

### Task 3: Sort and Search

**Objective**: Implement sorting and searching operations

**Acceptance Criteria**:
- [ ] Sort collections
- [ ] Use custom comparators
- [ ] Perform binary search
- [ ] Filter elements
- [ ] Code works correctly

**Instructions**:
1. Create list of objects
2. Sort by different criteria
3. Implement custom comparator
4. Search for elements
5. Filter using streams

---

## 🎨 Mini-Project: Inventory Management System

### Project Overview

**Description**: Create a comprehensive inventory management system using collections.

**Real-World Application**: Retail systems, warehouse management, stock tracking.

**Learning Value**: Master collections, sorting, searching, and stream operations.

### Project Requirements

#### Functional Requirements
- [ ] Manage products in inventory
- [ ] Track stock levels
- [ ] Search and filter products
- [ ] Generate reports
- [ ] Handle low stock alerts
- [ ] Calculate inventory value

#### Non-Functional Requirements
- [ ] Clean code structure
- [ ] Proper encapsulation
- [ ] Comprehensive documentation
- [ ] Unit tests
- [ ] Error handling

### Project Structure

```
inventory-management-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── Product.java
│   │           ├── Inventory.java
│   │           ├── InventoryManager.java
│   │           ├── Report.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               ├── ProductTest.java
│               └── InventoryTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create Product Class

```java
package com.learning;

/**
 * Represents a product in inventory.
 */
public class Product implements Comparable<Product> {
    private String productId;
    private String name;
    private double price;
    private int quantity;
    private String category;
    
    /**
     * Constructor for Product.
     */
    public Product(String productId, String name, double price, 
                   int quantity, String category) {
        setProductId(productId);
        setName(name);
        setPrice(price);
        setQuantity(quantity);
        setCategory(category);
    }
    
    // Getters
    public String getProductId() {
        return productId;
    }
    
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public String getCategory() {
        return category;
    }
    
    // Setters with validation
    public void setProductId(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        this.productId = productId;
    }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }
    
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }
    
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }
    
    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        this.category = category;
    }
    
    /**
     * Get inventory value.
     */
    public double getInventoryValue() {
        return price * quantity;
    }
    
    /**
     * Check if low stock.
     */
    public boolean isLowStock(int threshold) {
        return quantity < threshold;
    }
    
    /**
     * Update quantity.
     */
    public void updateQuantity(int amount) {
        int newQuantity = quantity + amount;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        this.quantity = newQuantity;
    }
    
    @Override
    public int compareTo(Product other) {
        return this.productId.compareTo(other.productId);
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + String.format("%.2f", price) +
                ", quantity=" + quantity +
                ", category='" + category + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product other = (Product) obj;
        return productId.equals(other.productId);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(productId);
    }
}
```

#### Step 2: Create Inventory Class

```java
package com.learning;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages inventory of products.
 */
public class Inventory {
    private Map<String, Product> products;
    private List<String> transactionLog;
    
    /**
     * Constructor for Inventory.
     */
    public Inventory() {
        this.products = new HashMap<>();
        this.transactionLog = new ArrayList<>();
    }
    
    /**
     * Add product to inventory.
     */
    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        products.put(product.getProductId(), product);
        logTransaction("Added product: " + product.getName());
    }
    
    /**
     * Remove product from inventory.
     */
    public void removeProduct(String productId) {
        if (products.containsKey(productId)) {
            Product product = products.remove(productId);
            logTransaction("Removed product: " + product.getName());
        }
    }
    
    /**
     * Get product by ID.
     */
    public Product getProduct(String productId) {
        return products.get(productId);
    }
    
    /**
     * Get all products.
     */
    public Collection<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }
    
    /**
     * Get products by category.
     */
    public List<Product> getProductsByCategory(String category) {
        return products.values().stream()
            .filter(p -> p.getCategory().equals(category))
            .collect(Collectors.toList());
    }
    
    /**
     * Search products by name.
     */
    public List<Product> searchByName(String keyword) {
        return products.values().stream()
            .filter(p -> p.getName().toLowerCase()
                .contains(keyword.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get low stock products.
     */
    public List<Product> getLowStockProducts(int threshold) {
        return products.values().stream()
            .filter(p -> p.isLowStock(threshold))
            .sorted(Comparator.comparingInt(Product::getQuantity))
            .collect(Collectors.toList());
    }
    
    /**
     * Get products sorted by price.
     */
    public List<Product> getProductsSortedByPrice() {
        return products.values().stream()
            .sorted(Comparator.comparingDouble(Product::getPrice))
            .collect(Collectors.toList());
    }
    
    /**
     * Get total inventory value.
     */
    public double getTotalInventoryValue() {
        return products.values().stream()
            .mapToDouble(Product::getInventoryValue)
            .sum();
    }
    
    /**
     * Get product count.
     */
    public int getProductCount() {
        return products.size();
    }
    
    /**
     * Get total quantity.
     */
    public int getTotalQuantity() {
        return products.values().stream()
            .mapToInt(Product::getQuantity)
            .sum();
    }
    
    /**
     * Update product quantity.
     */
    public void updateProductQuantity(String productId, int amount) {
        Product product = products.get(productId);
        if (product != null) {
            product.updateQuantity(amount);
            logTransaction("Updated " + product.getName() + 
                         " quantity by " + amount);
        }
    }
    
    /**
     * Log transaction.
     */
    private void logTransaction(String message) {
        String timestamp = java.time.LocalDateTime.now().toString();
        transactionLog.add("[" + timestamp + "] " + message);
    }
    
    /**
     * Get transaction log.
     */
    public List<String> getTransactionLog() {
        return new ArrayList<>(transactionLog);
    }
}
```

#### Step 3: Create InventoryManager Class

```java
package com.learning;

import java.util.*;

/**
 * Manages inventory operations.
 */
public class InventoryManager {
    private Inventory inventory;
    
    /**
     * Constructor for InventoryManager.
     */
    public InventoryManager() {
        this.inventory = new Inventory();
    }
    
    /**
     * Add product.
     */
    public void addProduct(Product product) {
        inventory.addProduct(product);
    }
    
    /**
     * Remove product.
     */
    public void removeProduct(String productId) {
        inventory.removeProduct(productId);
    }
    
    /**
     * Display all products.
     */
    public void displayAllProducts() {
        System.out.println("\n=== All Products ===");
        inventory.getAllProducts().forEach(System.out::println);
    }
    
    /**
     * Display products by category.
     */
    public void displayProductsByCategory(String category) {
        System.out.println("\n=== Products in " + category + " ===");
        inventory.getProductsByCategory(category)
            .forEach(System.out::println);
    }
    
    /**
     * Display low stock products.
     */
    public void displayLowStockProducts(int threshold) {
        System.out.println("\n=== Low Stock Products (< " + threshold + ") ===");
        List<Product> lowStock = inventory.getLowStockProducts(threshold);
        if (lowStock.isEmpty()) {
            System.out.println("No low stock products");
        } else {
            lowStock.forEach(System.out::println);
        }
    }
    
    /**
     * Display inventory summary.
     */
    public void displayInventorySummary() {
        System.out.println("\n=== Inventory Summary ===");
        System.out.println("Total Products: " + inventory.getProductCount());
        System.out.println("Total Quantity: " + inventory.getTotalQuantity());
        System.out.println("Total Value: $" + 
                         String.format("%.2f", inventory.getTotalInventoryValue()));
    }
    
    /**
     * Display transaction log.
     */
    public void displayTransactionLog() {
        System.out.println("\n=== Transaction Log ===");
        inventory.getTransactionLog().forEach(System.out::println);
    }
    
    /**
     * Get inventory.
     */
    public Inventory getInventory() {
        return inventory;
    }
}
```

#### Step 4: Create Report Class

```java
package com.learning;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates inventory reports.
 */
public class Report {
    private Inventory inventory;
    
    /**
     * Constructor for Report.
     */
    public Report(Inventory inventory) {
        this.inventory = inventory;
    }
    
    /**
     * Generate inventory report.
     */
    public void generateInventoryReport() {
        System.out.println("\n========== INVENTORY REPORT ==========");
        System.out.println("Total Products: " + inventory.getProductCount());
        System.out.println("Total Quantity: " + inventory.getTotalQuantity());
        System.out.println("Total Value: $" + 
                         String.format("%.2f", inventory.getTotalInventoryValue()));
        System.out.println("=====================================\n");
    }
    
    /**
     * Generate category report.
     */
    public void generateCategoryReport() {
        System.out.println("\n========== CATEGORY REPORT ==========");
        
        Map<String, List<Product>> byCategory = inventory.getAllProducts()
            .stream()
            .collect(Collectors.groupingBy(Product::getCategory));
        
        byCategory.forEach((category, products) -> {
            double value = products.stream()
                .mapToDouble(Product::getInventoryValue)
                .sum();
            System.out.println(category + ": " + products.size() + 
                             " products, Value: $" + String.format("%.2f", value));
        });
        System.out.println("====================================\n");
    }
    
    /**
     * Generate price range report.
     */
    public void generatePriceRangeReport() {
        System.out.println("\n========== PRICE RANGE REPORT ==========");
        
        List<Product> sorted = inventory.getAllProducts().stream()
            .sorted(Comparator.comparingDouble(Product::getPrice))
            .collect(Collectors.toList());
        
        if (!sorted.isEmpty()) {
            Product cheapest = sorted.get(0);
            Product expensive = sorted.get(sorted.size() - 1);
            
            System.out.println("Cheapest: " + cheapest.getName() + 
                             " - $" + String.format("%.2f", cheapest.getPrice()));
            System.out.println("Most Expensive: " + expensive.getName() + 
                             " - $" + String.format("%.2f", expensive.getPrice()));
        }
        System.out.println("=======================================\n");
    }
}
```

#### Step 5: Create Main Class

```java
package com.learning;

/**
 * Main entry point for Inventory Management System.
 */
public class Main {
    public static void main(String[] args) {
        // Create inventory manager
        InventoryManager manager = new InventoryManager();
        
        // Add products
        manager.addProduct(new Product("P001", "Laptop", 999.99, 10, "Electronics"));
        manager.addProduct(new Product("P002", "Mouse", 29.99, 50, "Electronics"));
        manager.addProduct(new Product("P003", "Keyboard", 79.99, 30, "Electronics"));
        manager.addProduct(new Product("P004", "Monitor", 299.99, 5, "Electronics"));
        manager.addProduct(new Product("P005", "Desk", 199.99, 15, "Furniture"));
        manager.addProduct(new Product("P006", "Chair", 149.99, 8, "Furniture"));
        manager.addProduct(new Product("P007", "Lamp", 49.99, 2, "Furniture"));
        
        // Display all products
        manager.displayAllProducts();
        
        // Display by category
        manager.displayProductsByCategory("Electronics");
        manager.displayProductsByCategory("Furniture");
        
        // Display low stock
        manager.displayLowStockProducts(10);
        
        // Display summary
        manager.displayInventorySummary();
        
        // Generate reports
        Report report = new Report(manager.getInventory());
        report.generateInventoryReport();
        report.generateCategoryReport();
        report.generatePriceRangeReport();
        
        // Update quantities
        manager.getInventory().updateProductQuantity("P001", -2);
        manager.getInventory().updateProductQuantity("P002", 10);
        
        // Display updated summary
        manager.displayInventorySummary();
        
        // Display transaction log
        manager.displayTransactionLog();
    }
}
```

#### Step 6: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Product class.
 */
public class ProductTest {
    
    private Product product;
    
    @BeforeEach
    void setUp() {
        product = new Product("P001", "Laptop", 999.99, 10, "Electronics");
    }
    
    @Test
    void testProductCreation() {
        assertEquals("P001", product.getProductId());
        assertEquals("Laptop", product.getName());
        assertEquals(999.99, product.getPrice());
        assertEquals(10, product.getQuantity());
    }
    
    @Test
    void testInventoryValue() {
        assertEquals(9999.9, product.getInventoryValue());
    }
    
    @Test
    void testLowStock() {
        assertTrue(product.isLowStock(20));
        assertFalse(product.isLowStock(5));
    }
    
    @Test
    void testUpdateQuantity() {
        product.updateQuantity(5);
        assertEquals(15, product.getQuantity());
        
        product.updateQuantity(-3);
        assertEquals(12, product.getQuantity());
    }
    
    @Test
    void testInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> {
            product.updateQuantity(-20);
        });
    }
}

/**
 * Unit tests for Inventory class.
 */
public class InventoryTest {
    
    private Inventory inventory;
    private Product product1;
    private Product product2;
    
    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        product1 = new Product("P001", "Laptop", 999.99, 10, "Electronics");
        product2 = new Product("P002", "Mouse", 29.99, 50, "Electronics");
    }
    
    @Test
    void testAddProduct() {
        inventory.addProduct(product1);
        assertEquals(1, inventory.getProductCount());
    }
    
    @Test
    void testGetProduct() {
        inventory.addProduct(product1);
        Product retrieved = inventory.getProduct("P001");
        assertEquals(product1, retrieved);
    }
    
    @Test
    void testRemoveProduct() {
        inventory.addProduct(product1);
        inventory.removeProduct("P001");
        assertEquals(0, inventory.getProductCount());
    }
    
    @Test
    void testGetTotalValue() {
        inventory.addProduct(product1);
        inventory.addProduct(product2);
        double expected = 9999.9 + 1499.5;
        assertEquals(expected, inventory.getTotalInventoryValue());
    }
    
    @Test
    void testSearchByName() {
        inventory.addProduct(product1);
        inventory.addProduct(product2);
        assertEquals(1, inventory.searchByName("Laptop").size());
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

### Exercise 1: Student Grade System

**Objective**: Practice collections with student grades

**Task Description**:
Create system to manage student grades using collections

**Acceptance Criteria**:
- [ ] Store student grades
- [ ] Calculate averages
- [ ] Find top students
- [ ] Generate reports
- [ ] Sort by grade

### Exercise 2: Contact Management System

**Objective**: Practice maps and searching

**Task Description**:
Create contact management system with search and filtering

**Acceptance Criteria**:
- [ ] Store contacts
- [ ] Search by name
- [ ] Filter by category
- [ ] Sort contacts
- [ ] Export data

### Exercise 3: Task Priority Queue

**Objective**: Practice queues and priority ordering

**Task Description**:
Create task management system with priority queue

**Acceptance Criteria**:
- [ ] Add tasks with priority
- [ ] Process by priority
- [ ] Track completion
- [ ] Generate reports
- [ ] Handle deadlines

---

## 🧪 Quiz

### Question 1: What is the difference between List and Set?

A) List allows duplicates, Set doesn't  
B) Set allows duplicates, List doesn't  
C) No difference  
D) List is ordered, Set isn't  

**Answer**: A) List allows duplicates, Set doesn't

### Question 2: Which collection is best for fast lookup?

A) ArrayList  
B) LinkedList  
C) HashMap  
D) TreeSet  

**Answer**: C) HashMap

### Question 3: What does TreeSet do?

A) Stores duplicates  
B) Maintains insertion order  
C) Maintains sorted order  
D) Allows null values  

**Answer**: C) Maintains sorted order

### Question 4: How do you iterate through a Map?

A) Using for-each on values  
B) Using entrySet()  
C) Using keySet()  
D) All of the above  

**Answer**: D) All of the above

### Question 5: What is the time complexity of HashMap.get()?

A) O(1)  
B) O(n)  
C) O(log n)  
D) O(n log n)  

**Answer**: A) O(1)

---

## 🚀 Advanced Challenge

### Challenge: Complete E-Commerce Inventory System

**Difficulty**: Intermediate

**Objective**: Build comprehensive inventory system with advanced features

**Requirements**:
- [ ] Multiple warehouses
- [ ] Stock synchronization
- [ ] Reorder management
- [ ] Supplier tracking
- [ ] Analytics and reporting
- [ ] Performance optimization

---

## 🏆 Best Practices

### Collection Selection

1. **Choose the Right Collection**
   - Need ordered access? → List
   - Need unique elements? → Set
   - Need key-value pairs? → Map
   - Need FIFO? → Queue

2. **Performance Matters**
   - Random access? → ArrayList
   - Frequent insertion/deletion? → LinkedList
   - Fast lookup? → HashMap
   - Sorted data? → TreeSet/TreeMap

3. **Thread Safety**
   - Single-threaded? → Regular collections
   - Multi-threaded? → Synchronized or concurrent collections

---

## 🔗 Next Steps

**Next Lab**: [Lab 09: Generics](../09-generics/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built inventory management system
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 08! 🎉**

You've mastered collections and their operations. Ready for generics? Move on to [Lab 09: Generics](../09-generics/README.md).