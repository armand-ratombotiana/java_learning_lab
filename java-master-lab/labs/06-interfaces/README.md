# Lab 06: Interfaces

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Beginner-Intermediate |
| **Estimated Time** | 5 hours |
| **Real-World Context** | Building a payment gateway system with multiple payment methods |
| **Prerequisites** | Lab 05: Inheritance |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Understand interfaces** and their purpose
2. **Implement interfaces** in classes
3. **Use multiple interface implementation**
4. **Work with functional interfaces**
5. **Apply interface-based design patterns**
6. **Build a payment gateway system** with multiple payment methods

## 📚 Prerequisites

- Lab 05: Inheritance completed
- Understanding of abstract classes
- Knowledge of polymorphism
- Familiarity with method contracts

## 🧠 Concept Theory

### 1. Interface Basics

An interface defines a contract that classes must follow:

```java
// Interface definition
public interface Animal {
    void eat();
    void sleep();
    String getName();
}

// Class implementing interface
public class Dog implements Animal {
    private String name;
    
    public Dog(String name) {
        this.name = name;
    }
    
    @Override
    public void eat() {
        System.out.println(name + " is eating");
    }
    
    @Override
    public void sleep() {
        System.out.println(name + " is sleeping");
    }
    
    @Override
    public String getName() {
        return name;
    }
}

// Usage
Animal dog = new Dog("Buddy");
dog.eat();      // Buddy is eating
dog.sleep();    // Buddy is sleeping
```

**Key Concepts**:
- **Interface**: Contract defining methods
- **implements**: Keyword to implement interface
- **Abstract methods**: Methods without implementation
- **Polymorphism**: Treat objects as interface type

### 2. Multiple Interface Implementation

A class can implement multiple interfaces:

```java
public interface Drawable {
    void draw();
}

public interface Resizable {
    void resize(double factor);
}

public class Circle implements Drawable, Resizable {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing circle with radius " + radius);
    }
    
    @Override
    public void resize(double factor) {
        radius *= factor;
    }
}

// Usage
Circle circle = new Circle(5);
circle.draw();      // Drawing circle with radius 5
circle.resize(2);
circle.draw();      // Drawing circle with radius 10
```

### 3. Interface Inheritance

Interfaces can extend other interfaces:

```java
public interface Animal {
    void eat();
}

public interface Mammal extends Animal {
    void nurse();
}

public class Dog implements Mammal {
    @Override
    public void eat() {
        System.out.println("Dog is eating");
    }
    
    @Override
    public void nurse() {
        System.out.println("Dog is nursing");
    }
}
```

### 4. Default Methods

Interfaces can have default implementations:

```java
public interface Vehicle {
    void start();
    
    // Default method with implementation
    default void stop() {
        System.out.println("Vehicle stopped");
    }
    
    // Static method
    static void honk() {
        System.out.println("Honk!");
    }
}

public class Car implements Vehicle {
    @Override
    public void start() {
        System.out.println("Car started");
    }
    
    // Can override default method
    @Override
    public void stop() {
        System.out.println("Car stopped with brakes");
    }
}

// Usage
Car car = new Car();
car.start();        // Car started
car.stop();         // Car stopped with brakes
Vehicle.honk();     // Honk!
```

### 5. Functional Interfaces

Interfaces with single abstract method:

```java
@FunctionalInterface
public interface Calculator {
    int calculate(int a, int b);
}

// Implementation with lambda
Calculator add = (a, b) -> a + b;
Calculator multiply = (a, b) -> a * b;

System.out.println(add.calculate(5, 3));       // 8
System.out.println(multiply.calculate(5, 3));  // 15
```

### 6. Interface vs Abstract Class

Key differences:

```java
// Abstract Class
public abstract class Animal {
    private String name;  // Can have state
    
    public Animal(String name) {
        this.name = name;  // Constructor
    }
    
    abstract void makeSound();
    
    public void sleep() {  // Concrete method
        System.out.println("Sleeping");
    }
}

// Interface
public interface Drawable {
    void draw();  // Abstract method
    
    default void erase() {  // Default method
        System.out.println("Erased");
    }
}

// Class can extend one abstract class but implement multiple interfaces
public class Dog extends Animal implements Drawable {
    public Dog(String name) {
        super(name);
    }
    
    @Override
    void makeSound() {
        System.out.println("Woof!");
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing dog");
    }
}
```

### 7. Marker Interfaces

Interfaces with no methods:

```java
// Marker interface
public interface Serializable {
    // No methods - just marks the class
}

public class Person implements Serializable {
    private String name;
    // Can be serialized
}

// Check if object is serializable
if (person instanceof Serializable) {
    // Can serialize
}
```

### 8. Comparable Interface

Built-in interface for comparison:

```java
public class Student implements Comparable<Student> {
    private String name;
    private double gpa;
    
    public Student(String name, double gpa) {
        this.name = name;
        this.gpa = gpa;
    }
    
    @Override
    public int compareTo(Student other) {
        // Compare by GPA (descending)
        return Double.compare(other.gpa, this.gpa);
    }
}

// Usage
List<Student> students = new ArrayList<>();
students.add(new Student("John", 3.5));
students.add(new Student("Jane", 3.8));
students.add(new Student("Bob", 3.2));

Collections.sort(students);  // Sorted by GPA
```

### 9. Iterable Interface

For enhanced for loops:

```java
public class CustomList<T> implements Iterable<T> {
    private List<T> items = new ArrayList<>();
    
    public void add(T item) {
        items.add(item);
    }
    
    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }
}

// Usage
CustomList<String> list = new CustomList<>();
list.add("Apple");
list.add("Banana");

for (String item : list) {
    System.out.println(item);
}
```

### 10. Interface Segregation Principle

Keep interfaces focused and small:

```java
// ❌ Bad: Fat interface
public interface Worker {
    void work();
    void eat();
    void sleep();
}

// ✅ Good: Segregated interfaces
public interface Workable {
    void work();
}

public interface Eatable {
    void eat();
}

public interface Sleepable {
    void sleep();
}

public class Employee implements Workable, Eatable, Sleepable {
    @Override
    public void work() { }
    
    @Override
    public void eat() { }
    
    @Override
    public void sleep() { }
}
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Create and Implement Interfaces

**Objective**: Define interfaces and implement them in classes

**Acceptance Criteria**:
- [ ] Interface defined with abstract methods
- [ ] Class implements interface
- [ ] All methods implemented
- [ ] Polymorphism works correctly
- [ ] Code compiles without errors

**Instructions**:
1. Create a `Shape` interface
2. Create `Circle` and `Rectangle` classes
3. Implement all interface methods
4. Test polymorphic behavior

### Task 2: Multiple Interface Implementation

**Objective**: Implement multiple interfaces in one class

**Acceptance Criteria**:
- [ ] Class implements multiple interfaces
- [ ] All methods from all interfaces implemented
- [ ] No conflicts between interfaces
- [ ] Polymorphism works with all interfaces

**Instructions**:
1. Create `Drawable` interface
2. Create `Resizable` interface
3. Create class implementing both
4. Test both interface behaviors

### Task 3: Use Default Methods

**Objective**: Work with default methods in interfaces

**Acceptance Criteria**:
- [ ] Interface has default method
- [ ] Class can override default
- [ ] Default method works when not overridden
- [ ] Override works correctly

**Instructions**:
1. Create interface with default method
2. Create class implementing interface
3. Override default method
4. Test both scenarios

---

## 🎨 Mini-Project: Payment Gateway System

### Project Overview

**Description**: Create a comprehensive payment gateway system supporting multiple payment methods.

**Real-World Application**: E-commerce systems, payment processing, financial applications.

**Learning Value**: Master interfaces, polymorphism, and design patterns.

### Project Requirements

#### Functional Requirements
- [ ] Support multiple payment methods
- [ ] Process payments
- [ ] Validate payment information
- [ ] Track transaction history
- [ ] Generate receipts
- [ ] Handle refunds

#### Non-Functional Requirements
- [ ] Clean code structure
- [ ] Proper encapsulation
- [ ] Comprehensive documentation
- [ ] Unit tests
- [ ] Error handling

### Project Structure

```
payment-gateway-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── PaymentMethod.java (interface)
│   │           ├── CreditCard.java
│   │           ├── PayPal.java
│   │           ├── Bitcoin.java
│   │           ├── Transaction.java
│   │           ├── PaymentProcessor.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               ├── PaymentMethodTest.java
│               └── PaymentProcessorTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create PaymentMethod Interface

```java
package com.learning;

/**
 * Interface for payment methods.
 */
public interface PaymentMethod {
    /**
     * Process payment.
     */
    boolean processPayment(double amount);
    
    /**
     * Refund payment.
     */
    boolean refund(double amount);
    
    /**
     * Validate payment method.
     */
    boolean validate();
    
    /**
     * Get payment method name.
     */
    String getPaymentMethodName();
    
    /**
     * Get transaction fee.
     */
    double getTransactionFee(double amount);
}
```

#### Step 2: Create CreditCard Class

```java
package com.learning;

/**
 * Credit card payment method.
 */
public class CreditCard implements PaymentMethod {
    private String cardNumber;
    private String cardHolder;
    private String expiryDate;
    private String cvv;
    private double balance;
    
    /**
     * Constructor for CreditCard.
     */
    public CreditCard(String cardNumber, String cardHolder, 
                      String expiryDate, String cvv, double balance) {
        setCardNumber(cardNumber);
        setCardHolder(cardHolder);
        setExpiryDate(expiryDate);
        setCVV(cvv);
        this.balance = balance;
    }
    
    // Getters
    public String getCardNumber() {
        return cardNumber;
    }
    
    public String getCardHolder() {
        return cardHolder;
    }
    
    public String getExpiryDate() {
        return expiryDate;
    }
    
    public double getBalance() {
        return balance;
    }
    
    // Setters with validation
    public void setCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            throw new IllegalArgumentException("Card number must be 16 digits");
        }
        this.cardNumber = cardNumber;
    }
    
    public void setCardHolder(String cardHolder) {
        if (cardHolder == null || cardHolder.trim().isEmpty()) {
            throw new IllegalArgumentException("Card holder cannot be empty");
        }
        this.cardHolder = cardHolder;
    }
    
    public void setExpiryDate(String expiryDate) {
        if (expiryDate == null || !expiryDate.matches("\\d{2}/\\d{2}")) {
            throw new IllegalArgumentException("Invalid expiry date format (MM/YY)");
        }
        this.expiryDate = expiryDate;
    }
    
    public void setCVV(String cvv) {
        if (cvv == null || cvv.length() != 3) {
            throw new IllegalArgumentException("CVV must be 3 digits");
        }
        this.cvv = cvv;
    }
    
    @Override
    public boolean processPayment(double amount) {
        if (!validate()) {
            System.out.println("Credit card validation failed");
            return false;
        }
        
        if (amount > balance) {
            System.out.println("Insufficient balance");
            return false;
        }
        
        balance -= amount;
        System.out.println("Payment of $" + String.format("%.2f", amount) + 
                         " processed via Credit Card");
        return true;
    }
    
    @Override
    public boolean refund(double amount) {
        balance += amount;
        System.out.println("Refund of $" + String.format("%.2f", amount) + 
                         " processed to Credit Card");
        return true;
    }
    
    @Override
    public boolean validate() {
        // Check if card is not expired
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        
        return month >= 1 && month <= 12 && year >= 24;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Credit Card";
    }
    
    @Override
    public double getTransactionFee(double amount) {
        return amount * 0.02;  // 2% fee
    }
    
    @Override
    public String toString() {
        return "CreditCard{" +
                "cardNumber='" + cardNumber + '\'' +
                ", cardHolder='" + cardHolder + '\'' +
                ", balance=" + String.format("%.2f", balance) +
                '}';
    }
}
```

#### Step 3: Create PayPal Class

```java
package com.learning;

/**
 * PayPal payment method.
 */
public class PayPal implements PaymentMethod {
    private String email;
    private String password;
    private double balance;
    private boolean verified;
    
    /**
     * Constructor for PayPal.
     */
    public PayPal(String email, String password, double balance) {
        setEmail(email);
        setPassword(password);
        this.balance = balance;
        this.verified = false;
    }
    
    // Getters
    public String getEmail() {
        return email;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public boolean isVerified() {
        return verified;
    }
    
    // Setters
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }
    
    public void setPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        this.password = password;
    }
    
    public void verify() {
        this.verified = true;
    }
    
    @Override
    public boolean processPayment(double amount) {
        if (!validate()) {
            System.out.println("PayPal account not verified");
            return false;
        }
        
        if (amount > balance) {
            System.out.println("Insufficient PayPal balance");
            return false;
        }
        
        balance -= amount;
        System.out.println("Payment of $" + String.format("%.2f", amount) + 
                         " processed via PayPal");
        return true;
    }
    
    @Override
    public boolean refund(double amount) {
        balance += amount;
        System.out.println("Refund of $" + String.format("%.2f", amount) + 
                         " processed to PayPal");
        return true;
    }
    
    @Override
    public boolean validate() {
        return verified && email != null && password != null;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "PayPal";
    }
    
    @Override
    public double getTransactionFee(double amount) {
        return amount * 0.03;  // 3% fee
    }
    
    @Override
    public String toString() {
        return "PayPal{" +
                "email='" + email + '\'' +
                ", balance=" + String.format("%.2f", balance) +
                ", verified=" + verified +
                '}';
    }
}
```

#### Step 4: Create Bitcoin Class

```java
package com.learning;

/**
 * Bitcoin payment method.
 */
public class Bitcoin implements PaymentMethod {
    private String walletAddress;
    private double balance;
    private String privateKey;
    
    /**
     * Constructor for Bitcoin.
     */
    public Bitcoin(String walletAddress, double balance, String privateKey) {
        setWalletAddress(walletAddress);
        this.balance = balance;
        setPrivateKey(privateKey);
    }
    
    // Getters
    public String getWalletAddress() {
        return walletAddress;
    }
    
    public double getBalance() {
        return balance;
    }
    
    // Setters
    public void setWalletAddress(String walletAddress) {
        if (walletAddress == null || walletAddress.length() < 26) {
            throw new IllegalArgumentException("Invalid wallet address");
        }
        this.walletAddress = walletAddress;
    }
    
    public void setPrivateKey(String privateKey) {
        if (privateKey == null || privateKey.length() < 64) {
            throw new IllegalArgumentException("Invalid private key");
        }
        this.privateKey = privateKey;
    }
    
    @Override
    public boolean processPayment(double amount) {
        if (!validate()) {
            System.out.println("Bitcoin wallet validation failed");
            return false;
        }
        
        if (amount > balance) {
            System.out.println("Insufficient Bitcoin balance");
            return false;
        }
        
        balance -= amount;
        System.out.println("Payment of " + String.format("%.8f", amount) + 
                         " BTC processed via Bitcoin");
        return true;
    }
    
    @Override
    public boolean refund(double amount) {
        balance += amount;
        System.out.println("Refund of " + String.format("%.8f", amount) + 
                         " BTC processed to Bitcoin wallet");
        return true;
    }
    
    @Override
    public boolean validate() {
        return walletAddress != null && privateKey != null && balance > 0;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Bitcoin";
    }
    
    @Override
    public double getTransactionFee(double amount) {
        return 0.0001;  // Fixed fee in BTC
    }
    
    @Override
    public String toString() {
        return "Bitcoin{" +
                "walletAddress='" + walletAddress + '\'' +
                ", balance=" + String.format("%.8f", balance) +
                '}';
    }
}
```

#### Step 5: Create Transaction Class

```java
package com.learning;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a transaction.
 */
public class Transaction {
    private String transactionId;
    private PaymentMethod paymentMethod;
    private double amount;
    private double fee;
    private LocalDateTime timestamp;
    private boolean successful;
    
    /**
     * Constructor for Transaction.
     */
    public Transaction(PaymentMethod paymentMethod, double amount) {
        this.transactionId = generateTransactionId();
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.fee = paymentMethod.getTransactionFee(amount);
        this.timestamp = LocalDateTime.now();
        this.successful = false;
    }
    
    // Getters
    public String getTransactionId() {
        return transactionId;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public double getFee() {
        return fee;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public boolean isSuccessful() {
        return successful;
    }
    
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
    
    /**
     * Generate unique transaction ID.
     */
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }
    
    /**
     * Display transaction receipt.
     */
    public void displayReceipt() {
        System.out.println("\n========== RECEIPT ==========");
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Payment Method: " + paymentMethod.getPaymentMethodName());
        System.out.println("Amount: $" + String.format("%.2f", amount));
        System.out.println("Fee: $" + String.format("%.2f", fee));
        System.out.println("Total: $" + String.format("%.2f", amount + fee));
        System.out.println("Status: " + (successful ? "SUCCESS" : "FAILED"));
        System.out.println("Timestamp: " + timestamp.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=============================\n");
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + transactionId + '\'' +
                ", method='" + paymentMethod.getPaymentMethodName() + '\'' +
                ", amount=" + String.format("%.2f", amount) +
                ", status=" + (successful ? "SUCCESS" : "FAILED") +
                '}';
    }
}
```

#### Step 6: Create PaymentProcessor Class

```java
package com.learning;

import java.util.ArrayList;
import java.util.List;

/**
 * Processes payments using different payment methods.
 */
public class PaymentProcessor {
    private List<Transaction> transactions;
    
    public PaymentProcessor() {
        this.transactions = new ArrayList<>();
    }
    
    /**
     * Process payment.
     */
    public boolean processPayment(PaymentMethod paymentMethod, double amount) {
        if (paymentMethod == null || amount <= 0) {
            System.out.println("Invalid payment parameters");
            return false;
        }
        
        Transaction transaction = new Transaction(paymentMethod, amount);
        
        if (paymentMethod.processPayment(amount)) {
            transaction.setSuccessful(true);
            transactions.add(transaction);
            transaction.displayReceipt();
            return true;
        } else {
            transactions.add(transaction);
            transaction.displayReceipt();
            return false;
        }
    }
    
    /**
     * Refund transaction.
     */
    public boolean refundTransaction(String transactionId) {
        for (Transaction txn : transactions) {
            if (txn.getTransactionId().equals(transactionId)) {
                if (txn.isSuccessful()) {
                    txn.getPaymentMethod().refund(txn.getAmount());
                    System.out.println("Refund processed for transaction: " + transactionId);
                    return true;
                }
            }
        }
        System.out.println("Transaction not found or not successful");
        return false;
    }
    
    /**
     * Get transaction history.
     */
    public void displayTransactionHistory() {
        System.out.println("\n=== Transaction History ===");
        for (Transaction txn : transactions) {
            System.out.println(txn);
        }
    }
    
    /**
     * Calculate total revenue.
     */
    public double calculateTotalRevenue() {
        double total = 0;
        for (Transaction txn : transactions) {
            if (txn.isSuccessful()) {
                total += txn.getFee();
            }
        }
        return total;
    }
    
    /**
     * Get transaction count.
     */
    public int getTransactionCount() {
        return transactions.size();
    }
    
    /**
     * Get successful transaction count.
     */
    public int getSuccessfulTransactionCount() {
        int count = 0;
        for (Transaction txn : transactions) {
            if (txn.isSuccessful()) {
                count++;
            }
        }
        return count;
    }
}
```

#### Step 7: Create Main Class

```java
package com.learning;

/**
 * Main entry point for Payment Gateway System.
 */
public class Main {
    public static void main(String[] args) {
        // Create payment processor
        PaymentProcessor processor = new PaymentProcessor();
        
        // Create payment methods
        CreditCard creditCard = new CreditCard("1234567890123456", "John Doe", 
                                               "12/25", "123", 5000);
        
        PayPal paypal = new PayPal("john@example.com", "password123", 3000);
        paypal.verify();  // Verify PayPal account
        
        Bitcoin bitcoin = new Bitcoin("1A1z7agoat2YLZW51Cd8vDffwESN2KeZi", 
                                      0.5, 
                                      "5KN7MzqK5wt2TP1fQCYyHBtDrXdJuXbUzm4A9rKAteYv3Qi5CVR");
        
        // Process payments
        System.out.println("=== Processing Payments ===\n");
        
        processor.processPayment(creditCard, 100);
        processor.processPayment(paypal, 50);
        processor.processPayment(bitcoin, 0.01);
        
        // Display transaction history
        processor.displayTransactionHistory();
        
        // Display statistics
        System.out.println("\n=== Payment Statistics ===");
        System.out.println("Total Transactions: " + processor.getTransactionCount());
        System.out.println("Successful Transactions: " + processor.getSuccessfulTransactionCount());
        System.out.println("Total Revenue (Fees): $" + 
                         String.format("%.2f", processor.calculateTotalRevenue()));
    }
}
```

#### Step 8: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for payment methods.
 */
public class PaymentMethodTest {
    
    private CreditCard creditCard;
    private PayPal paypal;
    private Bitcoin bitcoin;
    
    @BeforeEach
    void setUp() {
        creditCard = new CreditCard("1234567890123456", "John Doe", "12/25", "123", 1000);
        paypal = new PayPal("john@example.com", "password123", 500);
        paypal.verify();
        bitcoin = new Bitcoin("1A1z7agoat2YLZW51Cd8vDffwESN2KeZi", 0.5, 
                             "5KN7MzqK5wt2TP1fQCYyHBtDrXdJuXbUzm4A9rKAteYv3Qi5CVR");
    }
    
    @Test
    void testCreditCardPayment() {
        assertTrue(creditCard.processPayment(100));
        assertEquals(900, creditCard.getBalance());
    }
    
    @Test
    void testCreditCardInsufficientBalance() {
        assertFalse(creditCard.processPayment(2000));
    }
    
    @Test
    void testPayPalPayment() {
        assertTrue(paypal.processPayment(100));
        assertEquals(400, paypal.getBalance());
    }
    
    @Test
    void testPayPalNotVerified() {
        PayPal unverified = new PayPal("test@example.com", "password123", 500);
        assertFalse(unverified.processPayment(100));
    }
    
    @Test
    void testBitcoinPayment() {
        assertTrue(bitcoin.processPayment(0.1));
        assertEquals(0.4, bitcoin.getBalance(), 0.0001);
    }
    
    @Test
    void testTransactionFees() {
        assertEquals(2.0, creditCard.getTransactionFee(100));  // 2%
        assertEquals(1.5, paypal.getTransactionFee(50));       // 3%
        assertEquals(0.0001, bitcoin.getTransactionFee(1));    // Fixed
    }
    
    @Test
    void testRefund() {
        creditCard.processPayment(100);
        assertTrue(creditCard.refund(100));
        assertEquals(1000, creditCard.getBalance());
    }
}

/**
 * Unit tests for payment processor.
 */
public class PaymentProcessorTest {
    
    private PaymentProcessor processor;
    private CreditCard creditCard;
    
    @BeforeEach
    void setUp() {
        processor = new PaymentProcessor();
        creditCard = new CreditCard("1234567890123456", "John Doe", "12/25", "123", 1000);
    }
    
    @Test
    void testProcessPayment() {
        assertTrue(processor.processPayment(creditCard, 100));
        assertEquals(1, processor.getSuccessfulTransactionCount());
    }
    
    @Test
    void testTransactionCount() {
        processor.processPayment(creditCard, 100);
        processor.processPayment(creditCard, 50);
        assertEquals(2, processor.getTransactionCount());
    }
    
    @Test
    void testCalculateRevenue() {
        processor.processPayment(creditCard, 100);  // Fee: 2
        processor.processPayment(creditCard, 50);   // Fee: 1
        assertEquals(3.0, processor.calculateTotalRevenue());
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

### Exercise 1: Notification System

**Objective**: Practice interfaces with different notification types

**Task Description**:
Create notification system with Email, SMS, and Push notifications

**Acceptance Criteria**:
- [ ] Notification interface
- [ ] Multiple notification types
- [ ] Send notifications
- [ ] Track delivery status
- [ ] Retry mechanism

### Exercise 2: Data Storage System

**Objective**: Practice interfaces with different storage backends

**Task Description**:
Create storage system supporting File, Database, and Cloud storage

**Acceptance Criteria**:
- [ ] Storage interface
- [ ] Multiple storage types
- [ ] Save/retrieve data
- [ ] Delete operations
- [ ] Backup functionality

### Exercise 3: Logger System

**Objective**: Practice interfaces with different logging levels

**Task Description**:
Create logger with Console, File, and Database logging

**Acceptance Criteria**:
- [ ] Logger interface
- [ ] Multiple log destinations
- [ ] Log levels
- [ ] Formatting
- [ ] Filtering

---

## 🧪 Quiz

### Question 1: What is an interface?

A) A class with methods  
B) A contract defining methods  
C) A way to inherit  
D) A type of variable  

**Answer**: B) A contract defining methods

### Question 2: Can a class implement multiple interfaces?

A) No  
B) Yes  
C) Only two  
D) Only if abstract  

**Answer**: B) Yes

### Question 3: What is a functional interface?

A) Interface with multiple methods  
B) Interface with one abstract method  
C) Interface with default methods  
D) Interface with static methods  

**Answer**: B) Interface with one abstract method

### Question 4: Can interfaces have constructors?

A) Yes  
B) No  
C) Only default  
D) Only parameterized  

**Answer**: B) No

### Question 5: What is the purpose of default methods?

A) Required implementation  
B) Optional implementation  
C) Backward compatibility  
D) Both B and C  

**Answer**: D) Both B and C

---

## 🚀 Advanced Challenge

### Challenge: Complete E-Commerce Payment System

**Difficulty**: Intermediate

**Objective**: Build comprehensive payment system with advanced features

**Requirements**:
- [ ] Multiple payment methods
- [ ] Subscription payments
- [ ] Recurring billing
- [ ] Payment plans
- [ ] Fraud detection
- [ ] Analytics

---

## 🏆 Best Practices

### Interface Design

1. **Single Responsibility**
   - Each interface should have one purpose
   - Keep interfaces focused

2. **Meaningful Names**
   - Use descriptive names
   - Indicate purpose clearly

3. **Minimal Methods**
   - Only essential methods
   - Avoid bloated interfaces

---

## 🔗 Next Steps

**Next Lab**: [Lab 07: Exception Handling](../07-exception-handling/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built payment gateway system
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 06! 🎉**

You've mastered interfaces and polymorphism. Ready for exception handling? Move on to [Lab 07: Exception Handling](../07-exception-handling/README.md).