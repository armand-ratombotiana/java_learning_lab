# Abstraction & Interfaces — Step-by-Step Tutorial

## Step 1: Create an Abstract Class

```java
public class Step1Abstract {
    public static void main(String[] args) {
        // Can't instantiate abstract class:
        // Vehicle v = new Vehicle("test"); — Error!
        
        Car car = new Car("Toyota", 2024);
        car.start();
        car.stop();
    }
}

abstract class Vehicle {
    protected String make;
    protected int year;
    
    public Vehicle(String make, int year) {
        this.make = make;
        this.year = year;
    }
    
    // Abstract — subclass must implement
    public abstract void start();
    public abstract void stop();
    
    // Concrete — shared implementation
    public String getInfo() {
        return year + " " + make;
    }
}

class Car extends Vehicle {
    public Car(String make, int year) {
        super(make, year);
    }
    
    @Override
    public void start() {
        System.out.println(make + " car: Turn key, engine starts");
    }
    
    @Override
    public void stop() {
        System.out.println(make + " car: Press brake, engine stops");
    }
}
```

## Step 2: Create an Interface

```java
public class Step2Interface {
    public static void main(String[] args) {
        PaymentProcessor processor = new CreditCardProcessor();
        processor.processPayment(100.0);
        processor.processRefund(25.0);
        
        PaymentProcessor paypal = new PayPalProcessor();
        paypal.processPayment(200.0);
    }
}

interface PaymentProcessor {
    // Abstract method
    void processPayment(double amount);
    
    // Default method (Java 8+)
    default void processRefund(double amount) {
        System.out.println("Processing refund of $" + amount);
    }
    
    // Static method (Java 8+)
    static boolean isValidAmount(double amount) {
        return amount > 0;
    }
}

class CreditCardProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("Credit card payment: $" + amount);
    }
}

class PayPalProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("PayPal payment: $" + amount);
    }
    
    @Override
    public void processRefund(double amount) {
        System.out.println("PayPal refund: $" + amount + " (3-5 business days)");
    }
}
```

## Step 3: Multiple Interfaces

```java
public class Step3Multiple {
    public static void main(String[] args) {
        AmphibiousVehicle av = new AmphibiousVehicle();
        av.drive();
        av.sail();
        
        // Can use as either type
        LandVehicle land = av;
        WaterVehicle water = av;
        land.drive();
        water.sail();
    }
}

interface LandVehicle {
    void drive();
}

interface WaterVehicle {
    void sail();
}

class AmphibiousVehicle implements LandVehicle, WaterVehicle {
    @Override
    public void drive() {
        System.out.println("Driving on land");
    }
    
    @Override
    public void sail() {
        System.out.println("Sailing on water");
    }
}
```

## Step 4: Functional Interface with Lambda

```java
import java.util.*;

public class Step4Lambda {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
        
        // Anonymous class
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.compareTo(b);
            }
        });
        
        // Lambda (functional interface)
        Collections.sort(names, (a, b) -> a.compareTo(b));
        
        // Even shorter
        names.sort(Comparator.naturalOrder());
        System.out.println("Sorted: " + names);
        
        // Custom functional interface
        StringTransformer upper = s -> s.toUpperCase();
        StringTransformer reverse = s -> new StringBuilder(s).reverse().toString();
        
        System.out.println(upper.transform("hello"));
        System.out.println(reverse.transform("hello"));
    }
}

@FunctionalInterface
interface StringTransformer {
    String transform(String input);
}
```

## Step 5: Abstract Class vs Interface — When to Use

```java
public class Step5Compare {
    public static void main(String[] args) {
        // Abstract class: shared state + behavior
        SavingsAccount savings = new SavingsAccount("Alice", 1000, 0.05);
        savings.deposit(500);
        savings.applyInterest();
        System.out.println("Savings: $" + savings.getBalance());
        
        // Interface: capability contract
        Loggable logger = new ConsoleLogger();
        logger.log("Application started");
    }
}

abstract class Account {
    protected String owner;
    protected double balance;
    
    public Account(String owner, double balance) {
        this.owner = owner;
        this.balance = balance;
    }
    
    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }
    
    public abstract void withdraw(double amount);
    public double getBalance() { return balance; }
}

class SavingsAccount extends Account {
    private double interestRate;
    
    public SavingsAccount(String owner, double balance, double rate) {
        super(owner, balance);
        this.interestRate = rate;
    }
    
    @Override
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) balance -= amount;
    }
    
    public void applyInterest() {
        balance += balance * interestRate;
    }
}

interface Loggable {
    void log(String message);
}

class ConsoleLogger implements Loggable {
    @Override
    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
}
```
