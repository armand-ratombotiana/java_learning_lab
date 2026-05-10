# OOP Deep Dive

## Table of Contents

1. [Encapsulation](#encapsulation)
2. [Inheritance](#inheritance)
3. [Polymorphism](#polymorphism)
4. [Abstraction](#abstraction)
5. [Design Patterns](#design-patterns)
6. [SOLID Principles](#solid-principles)

---

## Encapsulation

### Concept Overview

Encapsulation bundles data and methods that operate on that data within a single unit (class), while restricting direct access to some components.

### Access Modifiers

```java
public class AccessModifiers {
    
    public String publicField = "Accessible everywhere";
    protected String protectedField = "Accessible in package and subclasses";
    private String privateField = "Accessible only in this class";
    String defaultField = "Package-private";
    
    public void publicMethod() {}
    protected void protectedMethod() {}
    private void privateMethod() {}
    void defaultMethod() {}
}
```

### Getters and Setters

```java
public class BankAccount {
    private double balance;
    private String accountNumber;
    
    // Read-only access
    public double getBalance() {
        return balance;
    }
    
    // Controlled write access
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
        }
    }
    
    // Validation in setter
    public void setAccountNumber(String accountNumber) {
        if (accountNumber != null && accountNumber.matches("\\d{10}")) {
            this.accountNumber = accountNumber;
        }
    }
}
```

### Immutability

```java
public final class ImmutablePerson {
    private final String name;  // Can't be changed after construction
    private final int age;
    private final List<String> hobbies;  // Defensive copy
    
    public ImmutablePerson(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        this.hobbies = new ArrayList<>(hobbies);  // Defensive copy
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
    public List<String> getHobbies() {
        return new ArrayList<>(hobbies);  // Return copy
    }
}
```

### Builder Pattern for Immutable Objects

```java
public class User {
    private final String username;
    private final String email;
    private final int age;
    private final List<String> roles;
    
    private User(Builder builder) {
        this.username = builder.username;
        this.email = builder.email;
        this.age = builder.age;
        this.roles = Collections.unmodifiableList(builder.roles);
    }
    
    public static class Builder {
        private String username;
        private String email;
        private int age = 0;
        private List<String> roles = new ArrayList<>();
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder age(int age) {
            this.age = age;
            return this;
        }
        
        public Builder addRole(String role) {
            this.roles.add(role);
            return this;
        }
        
        public User build() {
            return new User(this);
        }
    }
}

// Usage
User user = new User.Builder()
    .username("john")
    .email("john@example.com")
    .age(25)
    .addRole("admin")
    .build();
```

---

## Inheritance

### Class Extension

```java
public class Animal {
    protected String name;
    protected int age;
    
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public void eat() {
        System.out.println(name + " is eating");
    }
    
    public void sleep() {
        System.out.println(name + " is sleeping");
    }
    
    public String getName() { return name; }
}

public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, int age, String breed) {
        super(name, age);  // Call parent constructor
        this.breed = breed;
    }
    
    public void bark() {
        System.out.println(name + " is barking");
    }
    
    @Override  // Override parent's method
    public void eat() {
        System.out.println(name + " is eating dog food");
    }
}
```

### super Keyword

```java
public class Parent {
    protected int value = 10;
    
    public Parent() {
        System.out.println("Parent constructor");
    }
    
    public void display() {
        System.out.println("Parent display: " + value);
    }
}

public class Child extends Parent {
    protected int value = 20;
    
    public Child() {
        super();  // Call parent constructor
        System.out.println("Child constructor");
    }
    
    @Override
    public void display() {
        super.display();  // Call parent's method
        System.out.println("Child display: " + value);
        System.out.println("Parent value via super: " + super.value);
    }
}
```

### Method Overriding

```java
public class BaseClass {
    public void method() {
        System.out.println("Base");
    }
    
    public final void finalMethod() {
        System.out.println("Cannot override");
    }
    
    private void privateMethod() {
        System.out.println("Private base");
    }
}

public class DerivedClass extends BaseClass {
    @Override
    public void method() {
        System.out.println("Derived");
    }
    
    // This would cause compilation error:
    // @Override
    // public final void finalMethod() {}
    
    // This is NOT overriding (different signature)
    public void privateMethod(String s) {
        System.out.println("New method");
    }
}
```

### Object Class Methods

```java
public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && Objects.equals(name, person.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
    
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();  // Shallow copy
    }
}
```

---

## Polymorphism

### Runtime Polymorphism

```java
public abstract class Shape {
    public abstract double area();
}

public class Circle extends Shape {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

public class Rectangle extends Shape {
    private double width, height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double area() {
        return width * height;
    }
}

// Usage - polymorphism in action
public class AreaCalculator {
    public double totalArea(List<Shape> shapes) {
        double total = 0;
        for (Shape shape : shapes) {
            total += shape.area();  // Calls appropriate method
        }
        return total;
    }
}

// Polymorphic method call
Shape shape = new Circle(5);
System.out.println(shape.area());  // Calls Circle.area()
```

### instanceof and Pattern Matching

```java
public class TypeCheckDemo {
    public static void describe(Object obj) {
        // Traditional instanceof
        if (obj instanceof Circle) {
            Circle c = (Circle) obj;
            System.out.println("Circle with radius " + c.getRadius());
        } else if (obj instanceof Rectangle) {
            Rectangle r = (Rectangle) obj;
            System.out.println("Rectangle " + r.getWidth() + "x" + r.getHeight());
        }
        
        // Java 16+ Pattern Matching
        if (obj instanceof Circle c) {
            System.out.println("Circle with radius " + c.getRadius());
        }
        
        // With null check
        if (obj instanceof Circle c && c.getRadius() > 0) {
            System.out.println("Valid circle");
        }
    }
}
```

### Covariant Return Types

```java
public abstract class Animal {
    public abstract Animal create offspring();
}

public class Dog extends Animal {
    @Override
    public Dog createOffspring() {  // Returns Dog, not Animal
        return new Dog("Puppy", 0);
    }
}

public class Cat extends Animal {
    @Override
    public Cat createOffspring() {  // Returns Cat, not Animal
        return new Cat("Kitten", 0);
    }
}
```

### Method Overloading vs Overriding

```java
public class OverloadVsOverride {
    
    // OVERLOADING - same name, different parameters
    public void process(int x) {
        System.out.println("Processing int: " + x);
    }
    
    public void process(String x) {
        System.out.println("Processing String: " + x);
    }
    
    public void process(int x, int y) {
        System.out.println("Processing two ints: " + x + ", " + y);
    }
    
    // OVERRIDING - same signature in parent class
    public void execute() {
        System.out.println("Base execute");
    }
}

public class ExtendedOverload extends OverloadVsOverride {
    
    // This is OVERLOADING (new method)
    public void execute(String param) {
        System.out.println("Extended with param: " + param);
    }
    
    // This is OVERRIDING
    @Override
    public void execute() {
        System.out.println("Extended execute");
    }
}
```

---

## Abstraction

### Abstract Classes

```java
public abstract class Vehicle {
    protected String brand;
    protected int speed;
    
    // Abstract methods - must be implemented by subclasses
    public abstract void start();
    public abstract void stop();
    
    // Concrete method - inherited by all subclasses
    public void honk() {
        System.out.println("Honk!");
    }
    
    // Can have constructors
    public Vehicle(String brand) {
        this.brand = brand;
    }
    
    // Can have static methods
    public static void printType() {
        System.out.println("Generic vehicle");
    }
}

public class Car extends Vehicle {
    private int doors;
    
    public Car(String brand, int doors) {
        super(brand);
        this.doors = doors;
    }
    
    @Override
    public void start() {
        System.out.println(brand + " car is starting");
    }
    
    @Override
    public void stop() {
        System.out.println(brand + " car is stopping");
    }
    
    // Can add additional methods
    public void openSunroof() {
        System.out.println("Sunroof opened");
    }
}
```

### Interfaces

```java
public interface Flyable {
    int MAX_ALTITUDE = 10000;  // implicitly public static final
    
    void fly();  // implicitly public abstract
    
    // Java 8+ default method
    default void land() {
        System.out.println("Landing...");
    }
    
    // Java 8+ static method
    static void checkAltitude(int altitude) {
        if (altitude > MAX_ALTITUDE) {
            System.out.println("Too high!");
        }
    }
    
    // Java 9+ private method (for default methods)
    private void log(String message) {
        System.out.println("[LOG] " + message);
    }
}

public interface Swimmable {
    void swim();
    default void float() {
        System.out.println("Floating...");
    }
}

// Multiple interfaces
public class Duck implements Flyable, Swimmable {
    @Override
    public void fly() {
        System.out.println("Duck flying");
    }
    
    @Override
    public void swim() {
        System.out.println("Duck swimming");
    }
}
```

### Interface Inheritance

```java
public interface Readable {
    void read();
}

public interface Writeable {
    void write();
}

// Extends multiple interfaces
public interface ReadWrite extends Readable, Writeable {
    void flush();
}

public class FileHandler implements ReadWrite {
    @Override
    public void read() {
        System.out.println("Reading...");
    }
    
    @Override
    public void write() {
        System.out.println("Writing...");
    }
    
    @Override
    public void flush() {
        System.out.println("Flushing...");
    }
}
```

### Functional Interfaces

```java
// Single abstract method
@FunctionalInterface
public interface Processor<T> {
    T process(T input);
    
    // Can have default methods
    default Processor<T> andThen(Processor<T> after) {
        return t -> after.process(process(t));
    }
}

// Usage with lambda
public class LambdaDemo {
    public static void main(String[] args) {
        Processor<String> upperCase = String::toUpperCase;
        Processor<String> trim = String::trim;
        Processor<String> addPrefix = s -> ">> " + s;
        
        String result = upperCase
            .andThen(trim)
            .andThen(addPrefix)
            .process("  hello  ");
        
        System.out.println(result);  // >> HELLO
    }
}
```

---

## Design Patterns

### Factory Pattern

```java
// Product interface
public interface Notification {
    void send(String message);
}

// Concrete products
public class EmailNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("Email: " + message);
    }
}

public class SMSNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("SMS: " + message);
    }
}

public class PushNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("Push: " + message);
    }
}

// Factory
public class NotificationFactory {
    public static Notification create(String type) {
        return switch (type.toLowerCase()) {
            case "email" -> new EmailNotification();
            case "sms" -> new SMSNotification();
            case "push" -> new PushNotification();
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }
}

// Usage
Notification notification = NotificationFactory.create("email");
notification.send("Hello!");
```

### Singleton Pattern

```java
// Eager initialization
public class SingletonEager {
    private static final SingletonEager INSTANCE = new SingletonEager();
    
    private SingletonEager() {}
    
    public static SingletonEager getInstance() {
        return INSTANCE;
    }
}

// Lazy initialization (thread-safe)
public class SingletonLazy {
    private static volatile SingletonLazy instance;
    
    private SingletonLazy() {}
    
    public static synchronized SingletonLazy getInstance() {
        if (instance == null) {
            instance = new SingletonLazy();
        }
        return instance;
    }
}

// Bill Pugh Singleton (using inner class)
public class SingletonBillPugh {
    private SingletonBillPugh() {}
    
    private static class Holder {
        private static final SingletonBillPugh INSTANCE = new SingletonBillPugh();
    }
    
    public static SingletonBillPugh getInstance() {
        return Holder.INSTANCE;
    }
}

// Enum Singleton (recommended)
public enum SingletonEnum {
    INSTANCE;
    
    public void doSomething() {
        System.out.println("Singleton enum method");
    }
}
```

### Strategy Pattern

```java
// Strategy interface
public interface PaymentStrategy {
    void pay(double amount);
}

// Concrete strategies
public class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    
    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    @Override
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " with credit card " + cardNumber);
    }
}

public class PayPalPayment implements PaymentStrategy {
    private String email;
    
    public PayPalPayment(String email) {
        this.email = email;
    }
    
    @Override
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " with PayPal " + email);
    }
}

// Context
public class ShoppingCart {
    private List<Item> items = new ArrayList<>();
    private PaymentStrategy paymentStrategy;
    
    public void addItem(Item item) {
        items.add(item);
    }
    
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }
    
    public void checkout() {
        double total = items.stream().mapToDouble(Item::getPrice).sum();
        paymentStrategy.pay(total);
    }
}
```

### Observer Pattern

```java
// Observer interface
public interface Observer {
    void update(String message);
}

// Subject interface
public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers();
}

// Concrete subject
public class NewsAgency implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private String news;
    
    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(news);
        }
    }
    
    public void setNews(String news) {
        this.news = news;
        notifyObservers();
    }
}

// Concrete observers
public class NewsChannel implements Observer {
    private String name;
    
    public NewsChannel(String name) {
        this.name = name;
    }
    
    @Override
    public void update(String news) {
        System.out.println(name + " received: " + news);
    }
}

// Usage
NewsAgency agency = new NewsAgency();
NewsChannel cnn = new NewsChannel("CNN");
NewsChannel bbc = new NewsChannel("BBC");

agency.attach(cnn);
agency.attach(bbc);
agency.setNews("Breaking news!");
```

### Builder Pattern

```java
public class Pizza {
    private String dough;
    private String sauce;
    private String topping;
    private boolean cheese;
    private boolean pepperoni;
    
    private Pizza(Builder builder) {
        this.dough = builder.dough;
        this.sauce = builder.sauce;
        this.topping = builder.topping;
        this.cheese = builder.cheese;
        this.pepperoni = builder.pepperoni;
    }
    
    public static class Builder {
        private String dough = "thin";
        private String sauce = "tomato";
        private String topping = "none";
        private boolean cheese = false;
        private boolean pepperoni = false;
        
        public Builder dough(String dough) {
            this.dough = dough;
            return this;
        }
        
        public Builder sauce(String sauce) {
            this.sauce = sauce;
            return this;
        }
        
        public Builder topping(String topping) {
            this.topping = topping;
            return this;
        }
        
        public Builder cheese(boolean cheese) {
            this.cheese = cheese;
            return this;
        }
        
        public Builder pepperoni(boolean pepperoni) {
            this.pepperoni = pepperoni;
            return this;
        }
        
        public Pizza build() {
            return new Pizza(this);
        }
    }
}

// Usage
Pizza pizza = new Pizza.Builder()
    .dough("thick")
    .sauce("bbq")
    .topping("chicken")
    .cheese(true)
    .pepperoni(true)
    .build();
```

---

## SOLID Principles

### Single Responsibility Principle (SRP)

```java
// BAD - One class does too much
public class User {
    public void createUser() { /* ... */ }
    public void saveToDatabase() { /* ... */ }
    public void sendEmail() { /* ... */ }
    public void generateReport() { /* ... */ }
}

// GOOD - Each class has one responsibility
public class UserService {
    public void createUser() { /* ... */ }
}

public class UserRepository {
    public void save(User user) { /* ... */ }
}

public class EmailService {
    public void sendEmail(User user) { /* ... */ }
}

public class ReportGenerator {
    public void generateReport(User user) { /* ... */ }
}
```

### Open/Closed Principle (OCP)

```java
// BAD - Must modify to add new shapes
public class AreaCalculator {
    public double calculate(Object shape) {
        if (shape instanceof Circle) {
            Circle c = (Circle) shape;
            return Math.PI * c.getRadius() * c.getRadius();
        } else if (shape instanceof Rectangle) {
            Rectangle r = (Rectangle) shape;
            return r.getWidth() * r.getHeight();
        }
        return 0;
    }
}

// GOOD - Open for extension, closed for modification
public interface Shape {
    double area();
}

public class Circle implements Shape {
    private double radius;
    
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

public class AreaCalculator {
    public double calculate(Shape shape) {
        return shape.area();
    }
}
```

### Liskov Substitution Principle (LSP)

```java
// BAD - Violates LSP
public class Bird {
    public void fly() { /* ... */ }
}

public class Ostrich extends Bird {
    @Override
    public void fly() {
        throw new UnsupportedOperationException("Ostrich cannot fly!");
    }
}

// GOOD - Model correctly
public interface Flyable {
    void fly();
}

public class Bird {
    // Common bird behavior
}

public class FlyingBird extends Bird implements Flyable {
    @Override
    public void fly() { /* ... */ }
}

public class Ostrich extends Bird {
    // No fly() method - correct!
}
```

### Interface Segregation Principle (ISP)

```java
// BAD - Too large interface
public interface Worker {
    void work();
    void eat();
    void sleep();
    void attendMeeting();
}

public class Robot implements Worker {
    @Override
    public void work() { /* ... */ }
    
    @Override
    public void eat() {
        throw new UnsupportedOperationException("Robots don't eat!");
    }
    
    @Override
    public void sleep() {
        throw new UnsupportedOperationException("Robots don't sleep!");
    }
    
    @Override
    public void attendMeeting() {
        throw new UnsupportedOperationException("Robots don't attend meetings!");
    }
}

// GOOD - Segregated interfaces
public interface Workable {
    void work();
}

public interface Feedable {
    void eat();
}

public interface Meetingable {
    void attendMeeting();
}

public class HumanWorker implements Workable, Feedable, Meetingable {
    @Override
    public void work() { /* ... */ }
    
    @Override
    public void eat() { /* ... */ }
    
    @Override
    public void attendMeeting() { /* ... */ }
}

public class Robot implements Workable {
    @Override
    public void work() { /* ... */ }
}
```

### Dependency Inversion Principle (DIP)

```java
// BAD - Depends on concrete class
public class OrderService {
    private MySQLDatabase database = new MySQLDatabase();
    
    public void saveOrder(Order order) {
        database.save(order);
    }
}

// GOOD - Depends on abstraction
public interface OrderRepository {
    void save(Order order);
}

public class MySQLOrderRepository implements OrderRepository {
    @Override
    public void save(Order order) {
        // MySQL specific implementation
    }
}

public class OrderService {
    private final OrderRepository repository;
    
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }
    
    public void saveOrder(Order order) {
        repository.save(order);
    }
}

// Usage - inject dependency
OrderRepository repository = new MySQLOrderRepository();
OrderService service = new OrderService(repository);
```

---

## Best Practices Summary

| Principle | Practice |
|-----------|----------|
| Encapsulation | Keep fields private, expose minimal interface |
| Inheritance | Prefer composition over inheritance |
| Polymorphism | Program to interfaces, not implementations |
| Abstraction | Hide implementation details |
| SOLID | Apply principles consistently |
| Patterns | Use appropriate patterns, don't over-engineer |
