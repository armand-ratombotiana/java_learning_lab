# Object-Oriented Programming - Implementation Guide

## Module Overview

This module explores OOP principles in Java: encapsulation, inheritance, polymorphism, abstraction, interfaces, and design patterns. The implementations demonstrate both fundamental concepts and production-ready patterns.

---

## Part 1: From-Scratch Java Implementation

### 1.1 Encapsulation Implementation

```java
package com.learning.oop.implementation;

/**
 * Encapsulation demonstration - data hiding with access control
 */
public class EncapsulationDemo {
    
    // Private fields - data hiding
    private String name;
    private int age;
    private double salary;
    private String email;
    
    // Static field - shared across all instances
    private static int instanceCount = 0;
    
    // Final field - immutable
    private final String id;
    
    // Constructors
    public EncapsulationDemo() {
        this.id = generateId();
        instanceCount++;
    }
    
    public EncapsulationDemo(String name, int age, double salary) {
        this();
        setName(name);
        setAge(age);
        setSalary(salary);
    }
    
    // Getter and Setter methods with validation
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Name too long");
        }
        this.name = name.trim();
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
    
    public double getSalary() {
        return salary;
    }
    
    public void setSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        this.salary = salary;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }
    
    public String getId() {
        return id;
    }
    
    public static int getInstanceCount() {
        return instanceCount;
    }
    
    private static String generateId() {
        return "EMP-" + System.currentTimeMillis();
    }
    
    // Business methods
    public void increaseSalary(double percentage) {
        if (percentage <= 0) {
            throw new IllegalArgumentException("Percentage must be positive");
        }
        this.salary *= (1 + percentage / 100);
    }
    
    @Override
    public String toString() {
        return String.format("Employee[id=%s, name=%s, age=%d, salary=%.2f, email=%s]",
                id, name, age, salary, email);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EncapsulationDemo other = (EncapsulationDemo) obj;
        return id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
```

### 1.2 Inheritance Implementation

```java
package com.learning.oop.implementation;

/**
 * Inheritance demonstration - parent and child classes
 */

// Parent class
class Animal {
    protected String name;
    protected int age;
    protected String color;
    
    public Animal(String name, int age, String color) {
        this.name = name;
        this.age = age;
        this.color = color;
    }
    
    // Final method - cannot be overridden
    public final String getKingdom() {
        return "Animalia";
    }
    
    // Overridable methods
    public void eat() {
        System.out.println(name + " is eating");
    }
    
    public void sleep() {
        System.out.println(name + " is sleeping");
    }
    
    public void makeSound() {
        System.out.println(name + " makes a sound");
    }
    
    public void displayInfo() {
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Color: " + color);
    }
}

// Child class - extends parent
class Dog extends Animal {
    private String breed;
    private boolean trained;
    
    public Dog(String name, int age, String color, String breed) {
        super(name, age, color); // Call parent constructor
        this.breed = breed;
        this.trained = false;
    }
    
    // Override parent method
    @Override
    public void makeSound() {
        System.out.println(name + " barks: Woof! Woof!");
    }
    
    // Specific method for Dog
    public void fetch() {
        System.out.println(name + " fetches the ball");
    }
    
    public void train() {
        this.trained = true;
        System.out.println(name + " has been trained");
    }
    
    // Use parent's protected field
    public void displayBreedInfo() {
        System.out.println("Breed: " + breed + ", Trained: " + trained);
    }
    
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Breed: " + breed);
        System.out.println("Trained: " + trained);
    }
}

// Another child class
class Cat extends Animal {
    private boolean indoor;
    
    public Cat(String name, int age, String color, boolean indoor) {
        super(name, age, color);
        this.indoor = indoor;
    }
    
    @Override
    public void makeSound() {
        System.out.println(name + " meows: Meow!");
    }
    
    public void climb() {
        System.out.println(name + " climbs the tree");
    }
    
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Indoor: " + indoor);
    }
}

// Hierarchical inheritance
class Bird extends Animal {
    private boolean canFly;
    
    public Bird(String name, int age, String color, boolean canFly) {
        super(name, age, color);
        this.canFly = canFly;
    }
    
    @Override
    public void makeSound() {
        System.out.println(name + " chirps");
    }
    
    public void fly() {
        if (canFly) {
            System.out.println(name + " is flying");
        } else {
            System.out.println(name + " cannot fly");
        }
    }
}
```

### 1.3 Polymorphism Implementation

```java
package com.learning.oop.implementation;

/**
 * Polymorphism demonstration - runtime and compile-time
 */

// Abstract base class - abstraction
abstract class Shape {
    protected String color;
    
    public Shape(String color) {
        this.color = color;
    }
    
    // Abstract methods - must be implemented by subclasses
    public abstract double calculateArea();
    public abstract double calculatePerimeter();
    
    // Concrete method
    public void displayColor() {
        System.out.println("Color: " + color);
    }
}

// Concrete implementation - Circle
class Circle extends Shape {
    private double radius;
    
    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }
    
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    public double calculatePerimeter() {
        return 2 * Math.PI * radius;
    }
    
    public double getRadius() {
        return radius;
    }
}

// Concrete implementation - Rectangle
class Rectangle extends Shape {
    private double width;
    private double height;
    
    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double calculateArea() {
        return width * height;
    }
    
    @Override
    public double calculatePerimeter() {
        return 2 * (width + height);
    }
    
    // Method overloading - compile-time polymorphism
    public double calculateArea(double multiplier) {
        return width * height * multiplier;
    }
}

// Concrete implementation - Triangle
class Triangle extends Shape {
    private double base;
    private double height;
    
    public Triangle(String color, double base, double height) {
        super(color);
        this.base = base;
        this.height = height;
    }
    
    @Override
    public double calculateArea() {
        return 0.5 * base * height;
    }
    
    @Override
    public double calculatePerimeter() {
        // Assuming it'sosceles for simplicity
        double side = Math.sqrt(Math.pow(base/2, 2) + Math.pow(height, 2));
        return base + 2 * side;
    }
}

// Interface - abstraction through contract
interface Drawable {
    void draw();
    void move(int x, int y);
}

// Implement interface
class Graphic implements Drawable {
    private int x;
    private int y;
    private String type;
    
    public Graphic(String type) {
        this.type = type;
        this.x = 0;
        this.y = 0;
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing " + type + " at (" + x + ", " + y + ")");
    }
    
    @Override
    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

// Polymorphic demonstration class
class PolymorphismDemo {
    
    // Method accepting parent type - polymorphism
    public void printShapeInfo(Shape shape) {
        System.out.println("--- Shape Info ---");
        shape.displayColor();
        System.out.println("Area: " + shape.calculateArea());
        System.out.println("Perimeter: " + shape.calculatePerimeter());
    }
    
    // Using interface - another form of polymorphism
    public void performDraw(Drawable drawable) {
        drawable.draw();
    }
    
    // Covariant return type demonstration
    public Animal createAnimal(String type) {
        return switch (type.toLowerCase()) {
            case "dog" -> new Dog("Buddy", 3, "Brown", "Labrador");
            case "cat" -> new Cat("Whiskers", 2, "Orange", true);
            case "bird" -> new Bird("Tweety", 1, "Yellow", true);
            default -> new Animal("Unknown", 0, "Unknown");
        };
    }
}
```

### 1.4 Design Patterns Implementation

```java
package com.learning.oop.implementation.patterns;

/**
 * Design Patterns - Factory, Singleton, Builder
 */

// Factory Pattern
interface Notification {
    void send(String message);
}

class EmailNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("Email sent: " + message);
    }
}

class SMSNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("SMS sent: " + message);
    }
}

class PushNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("Push notification: " + message);
    }
}

enum NotificationType {
    EMAIL, SMS, PUSH
}

class NotificationFactory {
    public static Notification createNotification(NotificationType type) {
        return switch (type) {
            case EMAIL -> new EmailNotification();
            case SMS -> new SMSNotification();
            case PUSH -> new PushNotification();
        };
    }
}

// Singleton Pattern - eager initialization
class DatabaseConnection {
    private static final DatabaseConnection instance = new DatabaseConnection();
    
    private String url;
    private String username;
    
    private DatabaseConnection() {
        this.url = "jdbc:mysql://localhost:3306/db";
        this.username = "root";
    }
    
    public static DatabaseConnection getInstance() {
        return instance;
    }
    
    public void connect() {
        System.out.println("Connecting to: " + url);
    }
}

// Singleton Pattern - lazy initialization (thread-safe)
class ConfigurationManager {
    private static volatile ConfigurationManager instance;
    private Properties properties;
    
    private ConfigurationManager() {
        properties = new Properties();
    }
    
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }
    
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}

// Builder Pattern
class User {
    private final String id;
    private final String name;
    private final String email;
    private final int age;
    private final String address;
    private final String phone;
    
    private User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.age = builder.age;
        this.address = builder.address;
        this.phone = builder.phone;
    }
    
    public static class Builder {
        private String id;
        private String name;
        private String email;
        private int age;
        private String address;
        private String phone;
        
        public Builder(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder age(int age) {
            this.age = age;
            return this;
        }
        
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        
        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }
        
        public User build() {
            return new User(this);
        }
    }
    
    @Override
    public String toString() {
        return String.format("User[id=%s, name=%s, email=%s, age=%d, address=%s, phone=%s]",
                id, name, email, age, address, phone);
    }
}

// Strategy Pattern
interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    
    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " via Credit Card: " + cardNumber);
    }
}

class PayPalPayment implements PaymentStrategy {
    private String email;
    
    public PayPalPayment(String email) {
        this.email = email;
    }
    
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " via PayPal: " + email);
    }
}

class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }
    
    public void checkout(double amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment strategy not set");
        }
        paymentStrategy.pay(amount);
    }
}

// Observer Pattern
interface Observer {
    void update(String message);
}

class NewsAgency {
    private List<Observer> observers = new ArrayList<>();
    private String latestNews;
    
    public void subscribe(Observer observer) {
        observers.add(observer);
    }
    
    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }
    
    public void setNews(String news) {
        this.latestNews = news;
        notifyObservers();
    }
    
    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(latestNews);
        }
    }
}

class NewsChannel implements Observer {
    private String name;
    
    public NewsChannel(String name) {
        this.name = name;
    }
    
    @Override
    public void update(String message) {
        System.out.println(name + " received: " + message);
    }
}
```

---

## Part 2: Production Variant with Spring Boot

### 2.1 Entity Classes with JPA

```java
package com.learning.oop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(name = "email", unique = true)
    private String email;
    
    @Column(name = "salary")
    private Double salary;
    
    @Enumerated(EnumType.STRING)
    private Department department;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    public enum Department {
        ENGINEERING, SALES, MARKETING, HR, FINANCE
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
}
```

### 2.2 Service Layer with Transactions

```java
package com.learning.oop.service;

import com.learning.oop.entity.Employee;
import com.learning.oop.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    
    private final EmployeeRepository repository;
    
    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }
    
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return repository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Employee> findById(Long id) {
        return repository.findById(id);
    }
    
    @Transactional
    public Employee save(Employee employee) {
        return repository.save(employee);
    }
    
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Employee> findByDepartment(Employee.Department department) {
        return repository.findByDepartment(department);
    }
    
    @Transactional(readOnly = true)
    public List<Employee> findBySalaryGreaterThan(Double salary) {
        return repository.findBySalaryGreaterThan(salary);
    }
}
```

### 2.3 Repository Interface

```java
package com.learning.oop.repository;

import com.learning.oop.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    List<Employee> findByDepartment(Employee.Department department);
    
    List<Employee> findBySalaryGreaterThan(Double salary);
    
    @Query("SELECT e FROM Employee e WHERE e.name LIKE %:name%")
    List<Employee> findByNameContaining(String name);
    
    @Query("SELECT e FROM Employee e ORDER BY e.salary DESC")
    List<Employee> findAllOrderBySalaryDesc();
}
```

### 2.4 REST Controller

```java
package com.learning.oop.controller;

import com.learning.oop.entity.Employee;
import com.learning.oop.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    
    private final EmployeeService service;
    
    public EmployeeController(EmployeeService service) {
        this.service = service;
    }
    
    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        Employee saved = service.save(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(
            @PathVariable Long id,
            @RequestBody Employee employee) {
        employee.setId(id);
        Employee updated = service.save(employee);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## Part 3: Step-by-Step Code Explanation

### 3.1 Encapsulation

**Step 1: Data Hiding**
- Private fields prevent direct access
- Only accessible through public methods
- Validation in setters ensures data integrity

**Step 2: Getters and Setters**
- Provide controlled access to private data
- Can add business logic in methods
- Enable future changes without breaking clients

**Step 3: Static Members**
- Shared across all instances
- Accessed via class name
- Useful for constants and counters

### 3.2 Inheritance

**Step 1: extends Keyword**
- Child class inherits from parent
- Can access non-private parent members
- Uses `super` to call parent constructor/methods

**Step 2: Method Overriding**
- Child provides specific implementation
- `@Override` annotation ensures correct signature
- Cannot override final methods

**Step 3: Inheritance Types**
- Single: one parent
- Multilevel: chain of inheritance
- Hierarchical: multiple children

### 3.3 Polymorphism

**Step 1: Runtime Polymorphism**
- Method overriding at runtime
- Parent reference holds child object
- Method called depends on actual object type

**Step 2: Compile-Time Polymorphism**
- Method overloading
- Same name, different parameters
- Resolved at compile time

**Step 3: Interface Polymorphism**
- Multiple implementations possible
- Decouple client from implementation
- Enables dependency injection

### 3.4 Design Patterns

**Step 1: Factory Pattern**
- Creates objects without exposing creation logic
- Returns interface type
- Enables easy switching of implementations

**Step 2: Singleton Pattern**
- One instance per application
- Eager or lazy initialization
- Thread-safe variations

**Step 3: Builder Pattern**
- Complex object construction
- Fluent API with method chaining
- Immutable objects

---

## Part 4: Key Concepts Demonstrated

| Concept | Implementation | Location |
|---------|---------------|----------|
| **Encapsulation** | Private fields, public getters/setters | EncapsulationDemo.java |
| **Inheritance** | extends keyword, super, method overriding | Animal hierarchy |
| **Polymorphism** | Method overriding, interfaces | Shape hierarchy |
| **Abstraction** | Abstract classes, interfaces | Shape, Drawable |
| **Factory Pattern** | NotificationFactory | DesignPatterns.java |
| **Singleton Pattern** | DatabaseConnection | DesignPatterns.java |
| **Builder Pattern** | User.Builder | DesignPatterns.java |
| **Strategy Pattern** | Payment strategies | DesignPatterns.java |
| **Observer Pattern** | NewsAgency | DesignPatterns.java |

---

## Key Takeaways

1. **Encapsulation**: Protects data, enables validation, promotes maintainability
2. **Inheritance**: Enables code reuse, establishes "is-a" relationships
3. **Polymorphism**: Provides flexibility, enables interfaces, supports extension
4. **Abstraction**: Simplifies complexity, defines contracts
5. **Design Patterns**: Proven solutions, best practices, industry standards

---

## Next Steps

- Study SOLID principles in depth
- Implement more design patterns
- Explore Spring Boot advanced features
- Practice TDD with OOP designs