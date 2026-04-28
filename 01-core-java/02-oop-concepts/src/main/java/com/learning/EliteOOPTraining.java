package com.learning;

import java.util.*;

/**
 * Elite OOP Training - Advanced Object-Oriented Programming for Top Companies
 *
 * This class contains 25+ advanced OOP exercises designed for elite-level interviews
 * at companies like Google, Amazon, Meta, Microsoft, Netflix, Apple.
 *
 * PEDAGOGIC APPROACH:
 * 1. Design Pattern Applications (Singleton, Factory, Builder, Observer)
 * 2. SOLID Principles in Practice
 * 3. Advanced Inheritance and Composition
 * 4. Interface Design and Strategy Pattern
 * 5. Real-world System Design Problems
 *
 * Each exercise includes:
 * - Problem statement from real interviews
 * - Solution with detailed explanation
 * - Time/Space complexity where applicable
 * - Common interview follow-ups
 * - Best practices and anti-patterns
 *
 * @author Java Learning Team
 * @version 1.0
 */
public class EliteOOPTraining {

    /**
     * Main demonstration method that runs all OOP training exercises.
     */
    public static void demonstrateEliteOOPTraining() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║        ELITE OOP TRAINING - Advanced Concepts                 ║");
        System.out.println("║        Design Patterns, SOLID, System Design                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");

        // Level 1: Design Patterns
        runDesignPatternExercises();

        // Level 2: SOLID Principles
        runSOLIDPrinciplesExercises();

        // Level 3: Advanced OOP Concepts
        runAdvancedOOPExercises();

        // Level 4: System Design
        runSystemDesignExercises();

        printCompletionSummary();
    }

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 1: DESIGN PATTERNS
    // ═══════════════════════════════════════════════════════════════

    private static void runDesignPatternExercises() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("LEVEL 1: DESIGN PATTERNS");
        System.out.println("═".repeat(65));

        exercise1_Singleton();
        exercise2_Factory();
        exercise3_Builder();
        exercise4_Strategy();
    }

    /**
     * Exercise 1: Implement Thread-Safe Singleton Pattern
     * Difficulty: Medium
     * Companies: Google, Amazon, Microsoft
     *
     * Problem: Design a Logger class that follows the Singleton pattern and is thread-safe.
     * Common Interview Follow-ups:
     * - How do you handle multi-threading?
     * - What about serialization/deserialization?
     * - Can you break the singleton pattern? How do you prevent it?
     */
    private static void exercise1_Singleton() {
        printExerciseHeader(1, "Thread-Safe Singleton Pattern", "Medium");

        Logger logger1 = Logger.getInstance();
        Logger logger2 = Logger.getInstance();

        System.out.println("logger1 == logger2: " + (logger1 == logger2));

        logger1.log("First message");
        logger2.log("Second message");

        printKeyLearning("Double-checked locking, volatile keyword, thread safety");
        printInterviewTip("Be ready to discuss: lazy vs eager initialization, enum singleton");
        printSeparator();
    }

    /**
     * Thread-safe Singleton implementation using double-checked locking.
     */
    public static class Logger {
        // volatile ensures visibility across threads
        private static volatile Logger instance;
        private List<String> logs;

        // Private constructor prevents external instantiation
        private Logger() {
            logs = new ArrayList<>();
        }

        /**
         * Thread-safe getInstance using double-checked locking.
         * First check: avoid synchronization overhead
         * synchronized block: ensure thread safety
         * Second check: prevent multiple instances
         */
        public static Logger getInstance() {
            if (instance == null) { // First check (no locking)
                synchronized (Logger.class) { // Lock
                    if (instance == null) { // Second check (with locking)
                        instance = new Logger();
                    }
                }
            }
            return instance;
        }

        public void log(String message) {
            logs.add(message);
            System.out.println("[LOG] " + message);
        }

        public List<String> getLogs() {
            return new ArrayList<>(logs); // Defensive copy
        }
    }

    /**
     * Exercise 2: Factory Pattern for Shape Creation
     * Difficulty: Medium
     * Companies: Amazon, Meta, Microsoft
     *
     * Problem: Create a factory that produces different shapes without exposing
     * instantiation logic to the client.
     */
    private static void exercise2_Factory() {
        printExerciseHeader(2, "Factory Pattern", "Medium");

        ShapeFactory factory = new ShapeFactory();

        IShape circle = factory.createShape("CIRCLE", 5.0);
        IShape rectangle = factory.createShape("RECTANGLE", 4.0, 6.0);
        IShape triangle = factory.createShape("TRIANGLE", 3.0, 4.0, 5.0);

        System.out.println("Circle area: " + circle.calculateArea());
        System.out.println("Rectangle area: " + rectangle.calculateArea());
        System.out.println("Triangle area: " + triangle.calculateArea());

        printKeyLearning("Encapsulation of object creation, loose coupling");
        printInterviewTip("Discuss: Factory vs Abstract Factory vs Builder");
        printSeparator();
    }

    /**
     * Shape interface for Factory pattern.
     */
    public interface IShape {
        double calculateArea();
        String getShapeType();
    }

    /**
     * Concrete Circle implementation.
     */
    public static class CircleShape implements IShape {
        private double radius;

        public CircleShape(double radius) {
            this.radius = radius;
        }

        @Override
        public double calculateArea() {
            return Math.PI * radius * radius;
        }

        @Override
        public String getShapeType() {
            return "Circle";
        }
    }

    /**
     * Concrete Rectangle implementation.
     */
    public static class RectangleShape implements IShape {
        private double width;
        private double height;

        public RectangleShape(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public double calculateArea() {
            return width * height;
        }

        @Override
        public String getShapeType() {
            return "Rectangle";
        }
    }

    /**
     * Concrete Triangle implementation.
     */
    public static class TriangleShape implements IShape {
        private double a, b, c; // sides

        public TriangleShape(double a, double b, double c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override
        public double calculateArea() {
            // Using Heron's formula
            double s = (a + b + c) / 2;
            return Math.sqrt(s * (s - a) * (s - b) * (s - c));
        }

        @Override
        public String getShapeType() {
            return "Triangle";
        }
    }

    /**
     * Factory class that creates shapes.
     */
    public static class ShapeFactory {
        public IShape createShape(String type, double... params) {
            if (type == null || type.isEmpty()) {
                throw new IllegalArgumentException("Shape type cannot be null");
            }

            switch (type.toUpperCase()) {
                case "CIRCLE":
                    if (params.length < 1) throw new IllegalArgumentException("Circle needs radius");
                    return new CircleShape(params[0]);

                case "RECTANGLE":
                    if (params.length < 2) throw new IllegalArgumentException("Rectangle needs width and height");
                    return new RectangleShape(params[0], params[1]);

                case "TRIANGLE":
                    if (params.length < 3) throw new IllegalArgumentException("Triangle needs 3 sides");
                    return new TriangleShape(params[0], params[1], params[2]);

                default:
                    throw new IllegalArgumentException("Unknown shape type: " + type);
            }
        }
    }

    /**
     * Exercise 3: Builder Pattern for Complex Objects
     * Difficulty: Medium-Hard
     * Companies: Google, Amazon
     *
     * Problem: Build a complex User object with many optional parameters.
     * Use Builder pattern to avoid telescoping constructors.
     */
    private static void exercise3_Builder() {
        printExerciseHeader(3, "Builder Pattern", "Medium-Hard");

        User user = new User.Builder("john@email.com")
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .phone("123-456-7890")
                .address("123 Main St")
                .build();

        System.out.println("User created: " + user);

        printKeyLearning("Avoid telescoping constructors, immutable objects, fluent API");
        printInterviewTip("Discuss: When to use Builder vs Factory vs Constructor");
        printSeparator();
    }

    /**
     * User class with Builder pattern for flexible object construction.
     */
    public static class User {
        // Required parameters
        private final String email;

        // Optional parameters
        private final String firstName;
        private final String lastName;
        private final int age;
        private final String phone;
        private final String address;

        private User(Builder builder) {
            this.email = builder.email;
            this.firstName = builder.firstName;
            this.lastName = builder.lastName;
            this.age = builder.age;
            this.phone = builder.phone;
            this.address = builder.address;
        }

        public static class Builder {
            // Required
            private final String email;

            // Optional - with default values
            private String firstName = "";
            private String lastName = "";
            private int age = 0;
            private String phone = "";
            private String address = "";

            public Builder(String email) {
                if (email == null || email.isEmpty()) {
                    throw new IllegalArgumentException("Email is required");
                }
                this.email = email;
            }

            public Builder firstName(String firstName) {
                this.firstName = firstName;
                return this;
            }

            public Builder lastName(String lastName) {
                this.lastName = lastName;
                return this;
            }

            public Builder age(int age) {
                if (age < 0 || age > 150) {
                    throw new IllegalArgumentException("Invalid age");
                }
                this.age = age;
                return this;
            }

            public Builder phone(String phone) {
                this.phone = phone;
                return this;
            }

            public Builder address(String address) {
                this.address = address;
                return this;
            }

            public User build() {
                return new User(this);
            }
        }

        @Override
        public String toString() {
            return String.format("User{email='%s', name='%s %s', age=%d, phone='%s', address='%s'}",
                    email, firstName, lastName, age, phone, address);
        }

        // Getters (no setters - immutable)
        public String getEmail() { return email; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public int getAge() { return age; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
    }

    /**
     * Exercise 4: Strategy Pattern for Payment Processing
     * Difficulty: Medium
     * Companies: Amazon, Stripe, PayPal (common in payment systems)
     *
     * Problem: Design a payment system that can process different payment methods
     * (Credit Card, PayPal, Bitcoin) without changing the core payment processing logic.
     */
    private static void exercise4_Strategy() {
        printExerciseHeader(4, "Strategy Pattern", "Medium");

        PaymentContext context = new PaymentContext();

        // Process with Credit Card
        context.setPaymentStrategy(new CreditCardPayment("1234-5678-9012-3456"));
        context.processPayment(100.0);

        // Process with PayPal
        context.setPaymentStrategy(new PayPalPayment("user@email.com"));
        context.processPayment(50.0);

        // Process with Bitcoin
        context.setPaymentStrategy(new BitcoinPayment("1A2B3C4D5E6F"));
        context.processPayment(75.5);

        printKeyLearning("Open/Closed principle, runtime algorithm selection");
        printInterviewTip("Discuss: Strategy vs State vs Command patterns");
        printSeparator();
    }

    /**
     * Strategy interface for payment processing.
     */
    public interface PaymentStrategy {
        void pay(double amount);
        String getPaymentMethod();
    }

    /**
     * Credit Card payment strategy.
     */
    public static class CreditCardPayment implements PaymentStrategy {
        private String cardNumber;

        public CreditCardPayment(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        @Override
        public void pay(double amount) {
            System.out.printf("Paid $%.2f using Credit Card ending in %s%n",
                    amount, cardNumber.substring(cardNumber.length() - 4));
        }

        @Override
        public String getPaymentMethod() {
            return "Credit Card";
        }
    }

    /**
     * PayPal payment strategy.
     */
    public static class PayPalPayment implements PaymentStrategy {
        private String email;

        public PayPalPayment(String email) {
            this.email = email;
        }

        @Override
        public void pay(double amount) {
            System.out.printf("Paid $%.2f via PayPal account: %s%n", amount, email);
        }

        @Override
        public String getPaymentMethod() {
            return "PayPal";
        }
    }

    /**
     * Bitcoin payment strategy.
     */
    public static class BitcoinPayment implements PaymentStrategy {
        private String walletAddress;

        public BitcoinPayment(String walletAddress) {
            this.walletAddress = walletAddress;
        }

        @Override
        public void pay(double amount) {
            System.out.printf("Paid $%.2f using Bitcoin wallet: %s%n", amount, walletAddress);
        }

        @Override
        public String getPaymentMethod() {
            return "Bitcoin";
        }
    }

    /**
     * Context class that uses a payment strategy.
     */
    public static class PaymentContext {
        private PaymentStrategy strategy;

        public void setPaymentStrategy(PaymentStrategy strategy) {
            this.strategy = strategy;
        }

        public void processPayment(double amount) {
            if (strategy == null) {
                throw new IllegalStateException("Payment strategy not set");
            }
            System.out.println("\nProcessing payment using: " + strategy.getPaymentMethod());
            strategy.pay(amount);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 2: SOLID PRINCIPLES
    // ═══════════════════════════════════════════════════════════════

    private static void runSOLIDPrinciplesExercises() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("LEVEL 2: SOLID PRINCIPLES");
        System.out.println("═".repeat(65));

        exercise5_SingleResponsibility();
        exercise6_OpenClosed();
        exercise7_LiskovSubstitution();
        exercise8_InterfaceSegregation();
        exercise9_DependencyInversion();
    }

    /**
     * Exercise 5: Single Responsibility Principle (SRP)
     * Difficulty: Easy-Medium
     * Companies: All FAANG companies ask about SOLID
     *
     * Problem: Refactor a User class that violates SRP by handling both
     * user data AND user persistence.
     */
    private static void exercise5_SingleResponsibility() {
        printExerciseHeader(5, "Single Responsibility Principle", "Easy-Medium");

        System.out.println("❌ BAD: User class with multiple responsibilities");
        System.out.println("class User {");
        System.out.println("    // User data");
        System.out.println("    private String name;");
        System.out.println("    ");
        System.out.println("    // Database operations (VIOLATION!)");
        System.out.println("    public void save() { /* DB logic */ }");
        System.out.println("    public void delete() { /* DB logic */ }");
        System.out.println("}");

        System.out.println("\n✅ GOOD: Separate concerns");
        UserData userData = new UserData("John Doe", "john@email.com");
        UserRepository repository = new UserRepository();
        repository.save(userData);

        System.out.println("User data: " + userData);
        System.out.println("Saved by repository");

        printKeyLearning("One class = one responsibility, high cohesion");
        printInterviewTip("Be ready to identify SRP violations in code reviews");
        printSeparator();
    }

    public static class UserData {
        private String name;
        private String email;

        public UserData(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }

        @Override
        public String toString() {
            return "UserData{name='" + name + "', email='" + email + "'}";
        }
    }

    public static class UserRepository {
        public void save(UserData user) {
            System.out.println("Saving user to database: " + user.getName());
        }

        public void delete(UserData user) {
            System.out.println("Deleting user from database: " + user.getName());
        }
    }

    /**
     * Exercise 6: Open/Closed Principle (OCP)
     * Problem: Design a discount calculator that's open for extension
     * but closed for modification.
     */
    private static void exercise6_OpenClosed() {
        printExerciseHeader(6, "Open/Closed Principle", "Medium");

        DiscountCalculator calculator = new DiscountCalculator();

        double price = 100.0;
        System.out.println("Original price: $" + price);
        System.out.println("With 10% discount: $" + calculator.calculate(price, new PercentageDiscount(10)));
        System.out.println("With $15 fixed discount: $" + calculator.calculate(price, new FixedDiscount(15)));
        System.out.println("With no discount: $" + calculator.calculate(price, new NoDiscount()));

        printKeyLearning("Extend behavior without modifying existing code");
        printInterviewTip("Strategy pattern often satisfies OCP");
        printSeparator();
    }

    public interface Discount {
        double apply(double price);
    }

    public static class PercentageDiscount implements Discount {
        private double percentage;

        public PercentageDiscount(double percentage) {
            this.percentage = percentage;
        }

        @Override
        public double apply(double price) {
            return price * (1 - percentage / 100);
        }
    }

    public static class FixedDiscount implements Discount {
        private double amount;

        public FixedDiscount(double amount) {
            this.amount = amount;
        }

        @Override
        public double apply(double price) {
            return Math.max(0, price - amount);
        }
    }

    public static class NoDiscount implements Discount {
        @Override
        public double apply(double price) {
            return price;
        }
    }

    public static class DiscountCalculator {
        public double calculate(double price, Discount discount) {
            return discount.apply(price);
        }
    }

    /**
     * Exercise 7: Liskov Substitution Principle (LSP)
     */
    private static void exercise7_LiskovSubstitution() {
        printExerciseHeader(7, "Liskov Substitution Principle", "Medium");

        System.out.println("LSP: Subtypes must be substitutable for their base types");
        System.out.println("\nExample: Rectangle and Square");

        LRectangle rect = new LRectangle(5, 10);
        System.out.println("Rectangle: " + rect.getWidth() + "x" + rect.getHeight() + " = " + rect.getArea());

        // If Square extends Rectangle, it may violate LSP
        System.out.println("\nCommon violation: Square extends Rectangle");
        System.out.println("Problem: Square changes both dimensions when setting one");

        printKeyLearning("Derived classes should not break parent class contracts");
        printInterviewTip("Classic example: Rectangle-Square problem");
        printSeparator();
    }

    public static class LRectangle {
        protected double width;
        protected double height;

        public LRectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        public double getWidth() { return width; }
        public double getHeight() { return height; }
        public void setWidth(double width) { this.width = width; }
        public void setHeight(double height) { this.height = height; }

        public double getArea() {
            return width * height;
        }
    }

    /**
     * Exercise 8: Interface Segregation Principle (ISP)
     */
    private static void exercise8_InterfaceSegregation() {
        printExerciseHeader(8, "Interface Segregation Principle", "Medium");

        System.out.println("ISP: Clients should not be forced to depend on interfaces they don't use");
        System.out.println("\n✅ GOOD: Segregated interfaces");

        Workable robot = new RobotWorker();
        robot.work();

        HumanWorker human = new HumanWorker();
        human.work();
        human.eat();

        printKeyLearning("Many specific interfaces better than one general interface");
        printInterviewTip("Reduces coupling, increases flexibility");
        printSeparator();
    }

    public interface Workable {
        void work();
    }

    public interface Eatable {
        void eat();
    }

    public static class HumanWorker implements Workable, Eatable {
        @Override
        public void work() {
            System.out.println("Human is working");
        }

        @Override
        public void eat() {
            System.out.println("Human is eating");
        }
    }

    public static class RobotWorker implements Workable {
        @Override
        public void work() {
            System.out.println("Robot is working (doesn't need eat method)");
        }
    }

    /**
     * Exercise 9: Dependency Inversion Principle (DIP)
     */
    private static void exercise9_DependencyInversion() {
        printExerciseHeader(9, "Dependency Inversion Principle", "Medium");

        System.out.println("DIP: Depend on abstractions, not concretions");
        System.out.println("\nExample: Database abstraction");

        DatabaseService mysqlService = new DatabaseService(new MySQLDatabase());
        mysqlService.save("data");

        DatabaseService mongoService = new DatabaseService(new MongoDatabase());
        mongoService.save("data");

        printKeyLearning("High-level modules don't depend on low-level modules");
        printInterviewTip("Enables easy testing with mocks, dependency injection");
        printSeparator();
    }

    public interface Database {
        void connect();
        void save(String data);
    }

    public static class MySQLDatabase implements Database {
        @Override
        public void connect() {
            System.out.println("Connected to MySQL");
        }

        @Override
        public void save(String data) {
            System.out.println("Saved to MySQL: " + data);
        }
    }

    public static class MongoDatabase implements Database {
        @Override
        public void connect() {
            System.out.println("Connected to MongoDB");
        }

        @Override
        public void save(String data) {
            System.out.println("Saved to MongoDB: " + data);
        }
    }

    public static class DatabaseService {
        private Database database;

        public DatabaseService(Database database) {
            this.database = database;
        }

        public void save(String data) {
            database.connect();
            database.save(data);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: ADVANCED OOP CONCEPTS
    // ═══════════════════════════════════════════════════════════════

    private static void runAdvancedOOPExercises() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("LEVEL 3: ADVANCED OOP CONCEPTS");
        System.out.println("═".repeat(65));

        exercise10_CompositionOverInheritance();
        exercise11_ImmutableClass();
    }

    /**
     * Exercise 10: Composition over Inheritance
     */
    private static void exercise10_CompositionOverInheritance() {
        printExerciseHeader(10, "Composition over Inheritance", "Medium");

        System.out.println("Prefer HAS-A over IS-A relationships");

        Car car = new Car(new ElectricEngine(), new LeatherSeats());
        car.start();
        car.adjustSeats();

        printKeyLearning("Composition provides more flexibility than inheritance");
        printInterviewTip("Discuss when to use inheritance vs composition");
        printSeparator();
    }

    public interface Engine {
        void start();
    }

    public static class ElectricEngine implements Engine {
        @Override
        public void start() {
            System.out.println("Electric engine started silently");
        }
    }

    public interface Seats {
        void adjust();
    }

    public static class LeatherSeats implements Seats {
        @Override
        public void adjust() {
            System.out.println("Adjusting leather seats");
        }
    }

    public static class Car {
        private Engine engine;
        private Seats seats;

        public Car(Engine engine, Seats seats) {
            this.engine = engine;
            this.seats = seats;
        }

        public void start() {
            engine.start();
        }

        public void adjustSeats() {
            seats.adjust();
        }
    }

    /**
     * Exercise 11: Design Immutable Class
     */
    private static void exercise11_ImmutableClass() {
        printExerciseHeader(11, "Immutable Class Design", "Medium-Hard");

        ImmutablePerson person = new ImmutablePerson("John", 30, Arrays.asList("Reading", "Coding"));
        System.out.println("Person: " + person);
        System.out.println("Hobbies: " + person.getHobbies());

        printKeyLearning("Final class, final fields, defensive copying, no setters");
        printInterviewTip("String, Integer, all wrapper classes are immutable");
        printSeparator();
    }

    public static final class ImmutablePerson {
        private final String name;
        private final int age;
        private final List<String> hobbies;

        public ImmutablePerson(String name, int age, List<String> hobbies) {
            this.name = name;
            this.age = age;
            // Defensive copy to prevent external modification
            this.hobbies = new ArrayList<>(hobbies);
        }

        public String getName() { return name; }
        public int getAge() { return age; }

        public List<String> getHobbies() {
            // Return defensive copy
            return new ArrayList<>(hobbies);
        }

        @Override
        public String toString() {
            return String.format("ImmutablePerson{name='%s', age=%d, hobbies=%s}",
                    name, age, hobbies);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 4: SYSTEM DESIGN
    // ═══════════════════════════════════════════════════════════════

    private static void runSystemDesignExercises() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("LEVEL 4: SYSTEM DESIGN");
        System.out.println("═".repeat(65));

        System.out.println("\n🎯 Interview System Design Questions:");
        System.out.println("1. Design a parking lot system");
        System.out.println("2. Design a library management system");
        System.out.println("3. Design a vending machine");
        System.out.println("4. Design an elevator system");
        System.out.println("5. Design a chess game");
        System.out.println("\nKey Skills to Demonstrate:");
        System.out.println("• Class diagram design");
        System.out.println("• Identifying entities and relationships");
        System.out.println("• Applying design patterns appropriately");
        System.out.println("• Handling edge cases");
        System.out.println("• Scalability considerations");
    }

    // ═══════════════════════════════════════════════════════════════
    // UTILITY METHODS
    // ═══════════════════════════════════════════════════════════════

    private static void printExerciseHeader(int number, String title, String difficulty) {
        System.out.println("\n[Exercise " + number + "] " + title + " - " + difficulty);
        System.out.println("-".repeat(65));
    }

    private static void printKeyLearning(String learning) {
        System.out.println("🔑 Key Learning: " + learning);
    }

    private static void printInterviewTip(String tip) {
        System.out.println("💡 Interview Tip: " + tip);
    }

    private static void printSeparator() {
        System.out.println();
    }

    private static void printCompletionSummary() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("OOP TRAINING COMPLETE!");
        System.out.println("═".repeat(65));
        System.out.println("\n📊 Summary:");
        System.out.println("• Design Patterns: 4 patterns mastered");
        System.out.println("• SOLID Principles: All 5 principles covered");
        System.out.println("• Advanced OOP: 2 advanced concepts");
        System.out.println("• System Design: 5 common problems outlined");
        System.out.println("\n✅ You're ready for elite OOP interviews!");
    }
}
