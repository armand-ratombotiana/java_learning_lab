# OOP - Exercises

## Exercise Set 1: Encapsulation

### Exercise 1.1: Bank Account
Create a BankAccount class with proper encapsulation:
- Private fields: accountNumber, holderName, balance
- Public methods for deposit/withdrawal with validation
- Getters for all fields
- No setter for balance (use deposit/withdraw only)

```java
public class BankAccount {
    private String accountNumber;
    private String holderName;
    private double balance;
    
    public BankAccount(String accountNumber, String holderName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = initialBalance >= 0 ? initialBalance : 0;
    }
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }
}
```

### Exercise 1.2: Immutable Person
Create an immutable Person class with:
- Final fields
- Defensive copying for collections
- Only getters, no setters

### Exercise 1.3: Validation
Add validation to a User class:
- Email must contain @
- Password must be at least 8 characters
- Age must be positive and less than 150

---

## Exercise Set 2: Inheritance

### Exercise 2.1: Animal Hierarchy
Create an animal hierarchy:
```
Animal (abstract)
├── Mammal
│   ├── Dog
│   └── Cat
├── Bird
│   ├── Eagle
│   └── Penguin
└── Fish
    ├── Shark
    └── Goldfish
```

Each should have appropriate overridden methods.

```java
public abstract class Animal {
    protected String name;
    
    public Animal(String name) {
        this.name = name;
    }
    
    public abstract void makeSound();
    public abstract void move();
    
    public String getName() { return name; }
}

public class Dog extends Mammal {
    public Dog(String name) { super(name); }
    
    @Override
    public void makeSound() {
        System.out.println(name + " says: Woof!");
    }
    
    @Override
    public void move() {
        System.out.println(name + " runs on land");
    }
}
```

### Exercise 2.2: Shape Hierarchy
Create a shape hierarchy with area/perimeter calculations:
- Abstract Shape class
- Circle, Rectangle, Triangle subclasses
- Use abstract methods for area and perimeter

### Exercise 2.3: super Keyword
Practice using super:
- Call parent constructor
- Access parent fields
- Call parent methods

---

## Exercise Set 3: Polymorphism

### Exercise 3.1: Payment System
Create a polymorphic payment system:
```java
public interface PaymentMethod {
    boolean pay(double amount);
    double getBalance();
}

public class CreditCard implements PaymentMethod {
    // Implementation
}

public class PayPal implements PaymentMethod {
    // Implementation
}

public class Crypto implements PaymentMethod {
    // Implementation
}

// Process payment polymorphically
public void processPayment(PaymentMethod method, double amount) {
    if (method.pay(amount)) {
        System.out.println("Payment successful");
    }
}
```

### Exercise 3.2: Shape Calculator
Use polymorphism to calculate total area:
```java
public double calculateTotalArea(List<Shape> shapes) {
    double total = 0;
    for (Shape shape : shapes) {
        total += shape.area();  // Polymorphic call
    }
    return total;
}
```

### Exercise 3.3: instanceof Pattern Matching
Write code using Java 16+ pattern matching to:
- Check object types
- Extract values safely
- Handle different types differently

---

## Exercise Set 4: Abstraction

### Exercise 4.1: Database Abstraction
Create abstract data access:
```java
public abstract class DataRepository<T> {
    protected List<T> items = new ArrayList<>();
    
    public abstract void save(T item);
    public abstract T findById(String id);
    public abstract List<T> findAll();
    public abstract void delete(String id);
    
    public int count() {
        return items.size();
    }
}

public class UserRepository extends DataRepository<User> {
    @Override
    public void save(User user) {
        // User-specific implementation
    }
    // ... other overrides
}
```

### Exercise 4.2: Plugin System
Create an abstract plugin architecture:
- Plugin interface with lifecycle methods
- Concrete plugins for different purposes

### Exercise 4.3: Default Methods
Create an interface with default methods:
- Default implementation for common behavior
- Optional methods that can be overridden

---

## Exercise Set 5: Design Patterns

### Exercise 5.1: Factory Pattern
Create a notification factory:
```java
public interface Notification {
    void send(String recipient, String message);
}

public class EmailNotification implements Notification {
    @Override
    public void send(String recipient, String message) {
        System.out.println("Email to " + recipient + ": " + message);
    }
}

public class SMSNotification implements Notification {
    @Override
    public void send(String recipient, String message) {
        System.out.println("SMS to " + recipient + ": " + message);
    }
}

public class NotificationFactory {
    public static Notification create(String type) {
        return switch (type.toLowerCase()) {
            case "email" -> new EmailNotification();
            case "sms" -> new SMSNotification();
            default -> throw new IllegalArgumentException("Unknown type");
        };
    }
}
```

### Exercise 5.2: Singleton Pattern
Implement singleton in three ways:
1. Eager initialization
2. Lazy initialization (thread-safe)
3. Bill Pugh singleton (inner class)

### Exercise 5.3: Strategy Pattern
Create a sorting system:
```java
public interface SortStrategy<T> {
    List<T> sort(List<T> items);
}

public class BubbleSort implements SortStrategy<Integer> {
    // Implementation
}

public class QuickSort implements SortStrategy<Integer> {
    // Implementation
}

public class Sorter<T> {
    private SortStrategy<T> strategy;
    
    public void setStrategy(SortStrategy<T> strategy) {
        this.strategy = strategy;
    }
    
    public List<T> sort(List<T> items) {
        return strategy.sort(items);
    }
}
```

### Exercise 5.4: Observer Pattern
Build an event notification system:
- Subject with attach/detach/notify
- Multiple observer types
- Event data transmission

### Exercise 5.5: Builder Pattern
Create a complex object with builder:
```java
public class Computer {
    private String CPU;
    private String RAM;
    private String storage;
    private String GPU;
    private boolean hasWifi;
    
    private Computer(Builder builder) {
        this.CPU = builder.CPU;
        this.RAM = builder.RAM;
        // ... copy all fields
    }
    
    public static class Builder {
        private String CPU;
        private String RAM;
        private String storage;
        private String GPU;
        private boolean hasWifi;
        
        public Builder CPU(String CPU) {
            this.CPU = CPU;
            return this;
        }
        
        // ... other builder methods
        
        public Computer build() {
            return new Computer(this);
        }
    }
}
```

---

## Exercise Set 6: SOLID Principles

### Exercise 6.1: SRP - Refactor This
Refactor a class that violates SRP:
```java
// BAD - Does too much
public class Employee {
    public void calculateSalary() { }
    public void saveToDatabase() { }
    public void generateReport() { }
    public void sendEmail() { }
    public void manageBenefits() { }
}

// GOOD - Separated
public class PayrollService { /* salary calculation */ }
public class EmployeeRepository { /* database operations */ }
public class ReportGenerator { /* report generation */ }
public class NotificationService { /* email */ }
public class BenefitsManager { /* benefits */ }
```

### Exercise 6.2: OCP - Shape Calculator
Refactor to follow OCP:
```java
// Add new shapes without modifying existing code
public interface Shape {
    double area();
}
```

### Exercise 6.3: LSP - Rectangle/Square
Fix the LSP violation:
```java
// The square is-a rectangle, but behaves differently
// Refactor to model correctly
```

### Exercise 6.4: ISP - Worker Interface
Split a bloated interface:
```java
// Before: Worker with many unrelated methods
// After: Segregated interfaces
```

### Exercise 6.5: DIP - Dependency Injection
Refactor to depend on abstractions:
```java
// Use constructor injection
// Depend on interfaces, not concrete classes
```

---

## Challenge Problems

### Challenge 1: Composite Pattern
Build a file system with files and directories:
- Both have common operations
- Directory contains other files/directories

### Challenge 2: Decorator Pattern
Build a coffee shop ordering system:
- Base coffee
- Add decorators (milk, sugar, whip)
- Calculate total price dynamically

### Challenge 3: State Pattern
Build a vending machine:
- Different states: no money, has money, dispense, out of stock
- State transitions

### Challenge 4: Command Pattern
Build an undo/redo text editor:
- Commands with execute/undo
- Command history

### Challenge 5: MVC Architecture
Implement Model-View-Controller:
- Model: Data and business logic
- View: Display
- Controller: User input handling

---

## Project Ideas

### Project 1: Library Management System
- Book, Member, Loan classes
- Abstract repository pattern
- Polymorphic search

### Project 2: Game Character System
- Abstract character class
- Different character types
- Strategy pattern for weapons/abilities

### Project 3: Restaurant Order System
- Order, MenuItem, Payment classes
- Factory for creating orders
- Observer for kitchen notifications
