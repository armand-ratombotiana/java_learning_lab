package com.learning.patterns;

import java.util.ArrayList;
import java.util.List;

public class DesignPatternsLab {

    public static void main(String[] args) {
        demonstratePatterns();
    }

    static void demonstratePatterns() {
        System.out.println("=== Design Patterns in Java ===\n");

        // 1. Singleton Pattern
        System.out.println("1. Singleton Pattern:");
        Logger logger1 = Logger.getInstance();
        Logger logger2 = Logger.getInstance();
        System.out.println("   Same instance: " + (logger1 == logger2));

        // 2. Builder Pattern
        System.out.println("\n2. Builder Pattern:");
        User user = new User.Builder()
                .name("Alice")
                .email("alice@example.com")
                .role("ADMIN")
                .build();
        System.out.println("   User: " + user);

        // 3. Factory Pattern
        System.out.println("\n3. Factory Pattern:");
        PaymentProcessor creditCard = PaymentFactory.createProcessor("CREDIT_CARD");
        creditCard.process(100.0);
        PaymentProcessor paypal = PaymentFactory.createProcessor("PAYPAL");
        paypal.process(50.0);

        // 4. Strategy Pattern
        System.out.println("\n4. Strategy Pattern:");
        SortStrategy bubbleSort = new BubbleSortStrategy();
        SortStrategy quickSort = new QuickSortStrategy();
        SortContext context = new SortContext();
        context.setStrategy(bubbleSort);
        context.sort(new int[]{5, 2, 8, 1, 9});
        context.setStrategy(quickSort);
        context.sort(new int[]{3, 7, 1, 4, 6});

        // 5. Observer Pattern
        System.out.println("\n5. Observer Pattern:");
        StockMarket market = new StockMarket();
        Investor investor1 = new Investor("Alice");
        Investor investor2 = new Investor("Bob");
        market.addObserver(investor1);
        market.addObserver(investor2);
        market.setStockPrice("AAPL", 150.00);
        market.setStockPrice("GOOGL", 2800.00);

        // 6. Decorator Pattern
        System.out.println("\n6. Decorator Pattern:");
        Coffee coffee = new SimpleCoffee();
        System.out.println("   Simple: " + coffee.getDescription() + " - $" + coffee.getCost());
        coffee = new MilkDecorator(coffee);
        System.out.println("   With Milk: " + coffee.getDescription() + " - $" + coffee.getCost());
        coffee = new SugarDecorator(coffee);
        System.out.println("   With Milk+Sugar: " + coffee.getDescription() + " - $" + coffee.getCost());

        // 7. Command Pattern
        System.out.println("\n7. Command Pattern:");
        Light light = new Light();
        Command turnOn = new TurnOnCommand(light);
        Command turnOff = new TurnOffCommand(light);
        RemoteControl remote = new RemoteControl();
        remote.setCommand(turnOn);
        remote.pressButton();
        remote.setCommand(turnOff);
        remote.pressButton();

        // 8. Chain of Responsibility
        System.out.println("\n8. Chain of Responsibility:");
        LoggerChain loggerChain = new LoggerChain();
        loggerChain.log(LogLevel.INFO, "Application started");
        loggerChain.log(LogLevel.WARNING, "Low disk space");
        loggerChain.log(LogLevel.ERROR, "Database connection failed");

        System.out.println("\n=== All patterns demonstrated ===");
    }
}

// Singleton
class Logger {
    private static final Logger INSTANCE = new Logger();
    private Logger() {}
    public static Logger getInstance() { return INSTANCE; }
    public void log(String message) { System.out.println("   [LOG] " + message); }
}

// Builder
class User {
    private final String name;
    private final String email;
    private final String role;
    private User(Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.role = builder.role;
    }
    public String toString() {
        return "User{name='" + name + "', email='" + email + "', role='" + role + "'}";
    }
    static class Builder {
        private String name; private String email; private String role;
        Builder name(String name) { this.name = name; return this; }
        Builder email(String email) { this.email = email; return this; }
        Builder role(String role) { this.role = role; return this; }
        User build() { return new User(this); }
    }
}

// Factory
interface PaymentProcessor { void process(double amount); }
class CreditCardProcessor implements PaymentProcessor {
    public void process(double amount) { System.out.println("   Processing $" + amount + " via Credit Card"); }
}
class PayPalProcessor implements PaymentProcessor {
    public void process(double amount) { System.out.println("   Processing $" + amount + " via PayPal"); }
}
class PaymentFactory {
    public static PaymentProcessor createProcessor(String type) {
        return switch (type.toUpperCase()) {
            case "CREDIT_CARD" -> new CreditCardProcessor();
            case "PAYPAL" -> new PayPalProcessor();
            default -> throw new IllegalArgumentException("Unknown payment type: " + type);
        };
    }
}

// Strategy
interface SortStrategy { void sort(int[] array); }
class BubbleSortStrategy implements SortStrategy {
    public void sort(int[] array) { System.out.println("   Sorting with Bubble Sort: " + java.util.Arrays.toString(array)); }
}
class QuickSortStrategy implements SortStrategy {
    public void sort(int[] array) { System.out.println("   Sorting with Quick Sort: " + java.util.Arrays.toString(array)); }
}
class SortContext {
    private SortStrategy strategy;
    void setStrategy(SortStrategy strategy) { this.strategy = strategy; }
    void sort(int[] array) { strategy.sort(array); }
}

// Observer
interface Observer { void update(String stock, double price); }
class StockMarket {
    private final List<Observer> observers = new ArrayList<>();
    void addObserver(Observer o) { observers.add(o); }
    void setStockPrice(String stock, double price) {
        System.out.println("   Stock price changed: " + stock + " = $" + price);
        observers.forEach(o -> o.update(stock, price));
    }
}
class Investor implements Observer {
    private final String name;
    Investor(String name) { this.name = name; }
    public void update(String stock, double price) {
        System.out.println("   " + name + " notified: " + stock + " at $" + price);
    }
}

// Decorator
interface Coffee { String getDescription(); double getCost(); }
class SimpleCoffee implements Coffee {
    public String getDescription() { return "Simple Coffee"; }
    public double getCost() { return 2.0; }
}
abstract class CoffeeDecorator implements Coffee {
    protected final Coffee decorated;
    CoffeeDecorator(Coffee decorated) { this.decorated = decorated; }
}
class MilkDecorator extends CoffeeDecorator {
    MilkDecorator(Coffee c) { super(c); }
    public String getDescription() { return decorated.getDescription() + ", Milk"; }
    public double getCost() { return decorated.getCost() + 0.5; }
}
class SugarDecorator extends CoffeeDecorator {
    SugarDecorator(Coffee c) { super(c); }
    public String getDescription() { return decorated.getDescription() + ", Sugar"; }
    public double getCost() { return decorated.getCost() + 0.3; }
}

// Command
interface Command { void execute(); }
class Light {
    void on() { System.out.println("   Light is ON"); }
    void off() { System.out.println("   Light is OFF"); }
}
class TurnOnCommand implements Command {
    private final Light light;
    TurnOnCommand(Light light) { this.light = light; }
    public void execute() { light.on(); }
}
class TurnOffCommand implements Command {
    private final Light light;
    TurnOffCommand(Light light) { this.light = light; }
    public void execute() { light.off(); }
}
class RemoteControl {
    private Command command;
    void setCommand(Command command) { this.command = command; }
    void pressButton() { command.execute(); }
}

// Chain of Responsibility
enum LogLevel { INFO, WARNING, ERROR }
abstract class LogHandler {
    protected LogHandler next;
    void setNext(LogHandler next) { this.next = next; }
    abstract void handle(LogLevel level, String message);
}
class InfoHandler extends LogHandler {
    void handle(LogLevel level, String message) {
        if (level == LogLevel.INFO) System.out.println("   [INFO] " + message);
        else if (next != null) next.handle(level, message);
    }
}
class WarningHandler extends LogHandler {
    void handle(LogLevel level, String message) {
        if (level == LogLevel.WARNING) System.out.println("   [WARNING] " + message);
        else if (next != null) next.handle(level, message);
    }
}
class ErrorHandler extends LogHandler {
    void handle(LogLevel level, String message) {
        if (level == LogLevel.ERROR) System.out.println("   [ERROR] " + message);
        else if (next != null) next.handle(level, message);
    }
}
class LoggerChain {
    private final LogHandler chain;
    LoggerChain() {
        LogHandler info = new InfoHandler();
        LogHandler warning = new WarningHandler();
        LogHandler error = new ErrorHandler();
        info.setNext(warning);
        warning.setNext(error);
        chain = info;
    }
    void log(LogLevel level, String message) { chain.handle(level, message); }
}