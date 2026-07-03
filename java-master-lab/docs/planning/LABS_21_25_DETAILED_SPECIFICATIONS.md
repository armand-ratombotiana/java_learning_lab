# Java Master Lab - Labs 21-25 Detailed Specifications

## 📋 Overview

**Purpose**: Detailed specifications for Labs 21-25  
**Target Audience**: Developers implementing these labs  
**Duration**: 2 weeks (Weeks 6-8)  
**Status**: Ready for implementation  

---

## 🎯 Lab 21: Design Patterns - Creational

### Lab Overview
**Duration**: 5 hours  
**Content**: 4,500+ lines  
**Examples**: 100+  
**Tests**: 150+  
**Projects**: 1  

### Learning Objectives
By completing this lab, learners will:
- ✅ Understand creational design patterns
- ✅ Implement singleton pattern correctly
- ✅ Use factory patterns effectively
- ✅ Apply builder pattern for complex objects
- ✅ Implement prototype pattern
- ✅ Understand dependency injection
- ✅ Apply patterns in real-world scenarios

### Patterns to Implement

#### 1. Singleton Pattern
**Concept**: Ensure a class has only one instance

**Implementation Approaches**:
- [ ] Eager initialization
- [ ] Lazy initialization
- [ ] Thread-safe lazy initialization
- [ ] Bill Pugh Singleton
- [ ] Enum Singleton

**Code Structure**:
```java
// Eager Singleton
public class EagerSingleton {
    private static final EagerSingleton INSTANCE = new EagerSingleton();
    
    private EagerSingleton() {}
    
    public static EagerSingleton getInstance() {
        return INSTANCE;
    }
}

// Lazy Singleton (Thread-safe)
public class LazySingleton {
    private static volatile LazySingleton instance;
    
    private LazySingleton() {}
    
    public static LazySingleton getInstance() {
        if (instance == null) {
            synchronized (LazySingleton.class) {
                if (instance == null) {
                    instance = new LazySingleton();
                }
            }
        }
        return instance;
    }
}

// Bill Pugh Singleton
public class BillPughSingleton {
    private BillPughSingleton() {}
    
    private static class SingletonHelper {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }
    
    public static BillPughSingleton getInstance() {
        return SingletonHelper.INSTANCE;
    }
}

// Enum Singleton
public enum EnumSingleton {
    INSTANCE;
    
    public void doSomething() {
        // Implementation
    }
}
```

**Test Cases**:
- [ ] Single instance verification
- [ ] Thread safety testing
- [ ] Serialization/deserialization
- [ ] Reflection attacks
- [ ] Performance testing

#### 2. Factory Pattern
**Concept**: Create objects without specifying exact classes

**Implementation Approaches**:
- [ ] Simple factory
- [ ] Factory method
- [ ] Abstract factory
- [ ] Static factory methods

**Code Structure**:
```java
// Simple Factory
public class DatabaseFactory {
    public static Database createDatabase(String type) {
        switch (type.toLowerCase()) {
            case "mysql":
                return new MySQLDatabase();
            case "postgresql":
                return new PostgreSQLDatabase();
            case "mongodb":
                return new MongoDBDatabase();
            default:
                throw new IllegalArgumentException("Unknown database type");
        }
    }
}

// Factory Method
public abstract class DatabaseCreator {
    public abstract Database createDatabase();
    
    public void initializeDatabase() {
        Database db = createDatabase();
        db.connect();
    }
}

public class MySQLDatabaseCreator extends DatabaseCreator {
    @Override
    public Database createDatabase() {
        return new MySQLDatabase();
    }
}

// Abstract Factory
public interface DatabaseFactory {
    Connection createConnection();
    Statement createStatement();
}

public class MySQLFactory implements DatabaseFactory {
    @Override
    public Connection createConnection() {
        return new MySQLConnection();
    }
    
    @Override
    public Statement createStatement() {
        return new MySQLStatement();
    }
}
```

**Test Cases**:
- [ ] Object creation verification
- [ ] Type checking
- [ ] Error handling
- [ ] Performance testing
- [ ] Integration testing

#### 3. Builder Pattern
**Concept**: Construct complex objects step by step

**Code Structure**:
```java
public class DatabaseConfig {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final int poolSize;
    private final boolean ssl;
    
    private DatabaseConfig(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.database = builder.database;
        this.username = builder.username;
        this.password = builder.password;
        this.poolSize = builder.poolSize;
        this.ssl = builder.ssl;
    }
    
    public static class Builder {
        private String host = "localhost";
        private int port = 3306;
        private String database;
        private String username;
        private String password;
        private int poolSize = 10;
        private boolean ssl = false;
        
        public Builder host(String host) {
            this.host = host;
            return this;
        }
        
        public Builder port(int port) {
            this.port = port;
            return this;
        }
        
        public Builder database(String database) {
            this.database = database;
            return this;
        }
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        
        public Builder poolSize(int poolSize) {
            this.poolSize = poolSize;
            return this;
        }
        
        public Builder ssl(boolean ssl) {
            this.ssl = ssl;
            return this;
        }
        
        public DatabaseConfig build() {
            validate();
            return new DatabaseConfig(this);
        }
        
        private void validate() {
            if (database == null || database.isEmpty()) {
                throw new IllegalArgumentException("Database name is required");
            }
            if (username == null || username.isEmpty()) {
                throw new IllegalArgumentException("Username is required");
            }
        }
    }
}

// Usage
DatabaseConfig config = new DatabaseConfig.Builder()
    .host("db.example.com")
    .port(3306)
    .database("myapp")
    .username("admin")
    .password("secret")
    .poolSize(20)
    .ssl(true)
    .build();
```

**Test Cases**:
- [ ] Object construction
- [ ] Validation testing
- [ ] Fluent interface
- [ ] Immutability
- [ ] Performance testing

#### 4. Prototype Pattern
**Concept**: Create new objects by cloning existing ones

**Code Structure**:
```java
public abstract class Shape implements Cloneable {
    protected String id;
    protected String type;
    
    public abstract void draw();
    
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

public class Circle extends Shape {
    private int radius;
    
    public Circle(int radius) {
        this.radius = radius;
        this.type = "Circle";
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing circle with radius: " + radius);
    }
}

public class ShapeCache {
    private static Map<String, Shape> shapeMap = new HashMap<>();
    
    public static Shape getShape(String shapeId) {
        Shape cachedShape = shapeMap.get(shapeId);
        return (Shape) cachedShape.clone();
    }
    
    public static void loadCache() {
        Circle circle = new Circle(10);
        circle.setId("1");
        shapeMap.put(circle.getId(), circle);
        
        Rectangle rectangle = new Rectangle(20, 30);
        rectangle.setId("2");
        shapeMap.put(rectangle.getId(), rectangle);
    }
}
```

**Test Cases**:
- [ ] Cloning verification
- [ ] Deep copy testing
- [ ] Shallow copy testing
- [ ] Performance testing
- [ ] Serialization testing

#### 5. Dependency Injection Pattern
**Concept**: Inject dependencies rather than creating them

**Code Structure**:
```java
// Constructor Injection
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    
    public void registerUser(User user) {
        userRepository.save(user);
        emailService.sendWelcomeEmail(user.getEmail());
    }
}

// Setter Injection
public class OrderService {
    private PaymentService paymentService;
    private NotificationService notificationService;
    
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    public void processOrder(Order order) {
        paymentService.processPayment(order);
        notificationService.notifyCustomer(order);
    }
}

// Interface Injection
public interface ServiceInjector {
    void inject(Service service);
}

public class ServiceImpl implements Service, ServiceInjector {
    private Dependency dependency;
    
    @Override
    public void inject(Service service) {
        this.dependency = service.getDependency();
    }
}
```

**Test Cases**:
- [ ] Dependency injection verification
- [ ] Mock object testing
- [ ] Integration testing
- [ ] Performance testing
- [ ] Configuration testing

### Additional Patterns

#### 6. Object Pool Pattern
- [ ] Pool creation and management
- [ ] Object reuse
- [ ] Thread safety
- [ ] Performance optimization

#### 7. Lazy Initialization Pattern
- [ ] Lazy loading
- [ ] Thread safety
- [ ] Performance optimization

#### 8. Multiton Pattern
- [ ] Multiple instances
- [ ] Instance management
- [ ] Thread safety

#### 9. RAII Pattern
- [ ] Resource management
- [ ] Try-with-resources
- [ ] Cleanup handling

#### 10. Dependency Injection Pattern
- [ ] Constructor injection
- [ ] Setter injection
- [ ] Interface injection

### Portfolio Project: Configuration Management System

**Project Description**:
Build a configuration management system using creational patterns

**Requirements**:
- [ ] Load configurations from multiple sources
- [ ] Support different configuration formats (JSON, YAML, Properties)
- [ ] Cache configurations using singleton pattern
- [ ] Create configurations using builder pattern
- [ ] Clone configurations using prototype pattern
- [ ] Inject dependencies using DI pattern

**Deliverables**:
- [ ] Complete implementation
- [ ] 50+ unit tests
- [ ] Documentation
- [ ] Usage examples

---

## 🎯 Lab 22: Design Patterns - Structural

### Lab Overview
**Duration**: 5 hours  
**Content**: 4,500+ lines  
**Examples**: 100+  
**Tests**: 150+  
**Projects**: 1  

### Learning Objectives
By completing this lab, learners will:
- ✅ Understand structural design patterns
- ✅ Implement adapter pattern
- ✅ Use decorator pattern effectively
- ✅ Apply facade pattern
- ✅ Implement proxy pattern
- ✅ Use composite pattern
- ✅ Apply patterns in real-world scenarios

### Patterns to Implement

#### 1. Adapter Pattern
**Concept**: Convert interface to another interface clients expect

**Code Structure**:
```java
// Target interface
public interface PaymentProcessor {
    void processPayment(double amount);
}

// Existing class with different interface
public class LegacyPaymentGateway {
    public void pay(double amount) {
        System.out.println("Processing payment: " + amount);
    }
}

// Adapter
public class PaymentGatewayAdapter implements PaymentProcessor {
    private LegacyPaymentGateway gateway;
    
    public PaymentGatewayAdapter(LegacyPaymentGateway gateway) {
        this.gateway = gateway;
    }
    
    @Override
    public void processPayment(double amount) {
        gateway.pay(amount);
    }
}
```

#### 2. Decorator Pattern
**Concept**: Attach additional responsibilities to object dynamically

**Code Structure**:
```java
public interface Coffee {
    double getCost();
    String getDescription();
}

public class SimpleCoffee implements Coffee {
    @Override
    public double getCost() {
        return 2.0;
    }
    
    @Override
    public String getDescription() {
        return "Simple Coffee";
    }
}

public abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}

public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public double getCost() {
        return coffee.getCost() + 0.5;
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Milk";
    }
}

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public double getCost() {
        return coffee.getCost() + 0.2;
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Sugar";
    }
}

// Usage
Coffee coffee = new SimpleCoffee();
coffee = new MilkDecorator(coffee);
coffee = new SugarDecorator(coffee);
System.out.println(coffee.getDescription()); // Simple Coffee, Milk, Sugar
System.out.println(coffee.getCost()); // 2.7
```

#### 3. Facade Pattern
**Concept**: Provide unified interface to complex subsystem

**Code Structure**:
```java
public class DatabaseFacade {
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    
    public void connect(String url, String user, String password) {
        // Complex connection logic
    }
    
    public List<User> getAllUsers() {
        // Complex query logic
    }
    
    public void saveUser(User user) {
        // Complex save logic
    }
    
    public void disconnect() {
        // Complex cleanup logic
    }
}
```

#### 4. Proxy Pattern
**Concept**: Provide surrogate for another object

**Code Structure**:
```java
public interface Image {
    void display();
}

public class RealImage implements Image {
    private String filename;
    
    public RealImage(String filename) {
        this.filename = filename;
        loadImage();
    }
    
    private void loadImage() {
        System.out.println("Loading image: " + filename);
    }
    
    @Override
    public void display() {
        System.out.println("Displaying image: " + filename);
    }
}

public class ProxyImage implements Image {
    private String filename;
    private RealImage realImage;
    
    public ProxyImage(String filename) {
        this.filename = filename;
    }
    
    @Override
    public void display() {
        if (realImage == null) {
            realImage = new RealImage(filename);
        }
        realImage.display();
    }
}
```

#### 5. Composite Pattern
**Concept**: Compose objects into tree structures

**Code Structure**:
```java
public interface FileSystemComponent {
    void display();
    long getSize();
}

public class File implements FileSystemComponent {
    private String name;
    private long size;
    
    public File(String name, long size) {
        this.name = name;
        this.size = size;
    }
    
    @Override
    public void display() {
        System.out.println("File: " + name + " (" + size + " bytes)");
    }
    
    @Override
    public long getSize() {
        return size;
    }
}

public class Directory implements FileSystemComponent {
    private String name;
    private List<FileSystemComponent> components = new ArrayList<>();
    
    public Directory(String name) {
        this.name = name;
    }
    
    public void add(FileSystemComponent component) {
        components.add(component);
    }
    
    @Override
    public void display() {
        System.out.println("Directory: " + name);
        for (FileSystemComponent component : components) {
            component.display();
        }
    }
    
    @Override
    public long getSize() {
        return components.stream()
            .mapToLong(FileSystemComponent::getSize)
            .sum();
    }
}
```

### Additional Patterns

#### 6. Bridge Pattern
- [ ] Abstraction and implementation separation
- [ ] Multiple inheritance alternatives

#### 7. Flyweight Pattern
- [ ] Object sharing
- [ ] Memory optimization

#### 8. Module Pattern
- [ ] Encapsulation
- [ ] Namespace management

#### 9. Private Class Data Pattern
- [ ] Data protection
- [ ] Immutability

#### 10. Structural Combinations
- [ ] Pattern combinations
- [ ] Complex scenarios

### Portfolio Project: UI Component Library

**Project Description**:
Build a UI component library using structural patterns

**Requirements**:
- [ ] Create adaptable components
- [ ] Decorate components with additional features
- [ ] Provide facade for complex UI operations
- [ ] Use proxy for lazy loading
- [ ] Compose components into hierarchies

**Deliverables**:
- [ ] Complete implementation
- [ ] 50+ unit tests
- [ ] Documentation
- [ ] Usage examples

---

## 🎯 Lab 23: Design Patterns - Behavioral

### Lab Overview
**Duration**: 5 hours  
**Content**: 4,500+ lines  
**Examples**: 100+  
**Tests**: 150+  
**Projects**: 1  

### Learning Objectives
By completing this lab, learners will:
- ✅ Understand behavioral design patterns
- ✅ Implement observer pattern
- ✅ Use strategy pattern effectively
- ✅ Apply command pattern
- ✅ Implement state pattern
- ✅ Use template method pattern
- ✅ Apply patterns in real-world scenarios

### Patterns to Implement

#### 1. Observer Pattern
**Concept**: Define one-to-many dependency between objects

**Code Structure**:
```java
public interface Observer {
    void update(String message);
}

public class Subject {
    private List<Observer> observers = new ArrayList<>();
    
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}

public class ConcreteObserver implements Observer {
    private String name;
    
    public ConcreteObserver(String name) {
        this.name = name;
    }
    
    @Override
    public void update(String message) {
        System.out.println(name + " received: " + message);
    }
}
```

#### 2. Strategy Pattern
**Concept**: Define family of algorithms, encapsulate each one

**Code Structure**:
```java
public interface PaymentStrategy {
    void pay(double amount);
}

public class CreditCardPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Paying " + amount + " with credit card");
    }
}

public class PayPalPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Paying " + amount + " with PayPal");
    }
}

public class PaymentContext {
    private PaymentStrategy strategy;
    
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void executePayment(double amount) {
        strategy.pay(amount);
    }
}
```

#### 3. Command Pattern
**Concept**: Encapsulate request as object

**Code Structure**:
```java
public interface Command {
    void execute();
    void undo();
}

public class LightOnCommand implements Command {
    private Light light;
    
    public LightOnCommand(Light light) {
        this.light = light;
    }
    
    @Override
    public void execute() {
        light.on();
    }
    
    @Override
    public void undo() {
        light.off();
    }
}

public class RemoteControl {
    private Command command;
    private Stack<Command> history = new Stack<>();
    
    public void setCommand(Command command) {
        this.command = command;
    }
    
    public void pressButton() {
        command.execute();
        history.push(command);
    }
    
    public void pressUndo() {
        if (!history.isEmpty()) {
            history.pop().undo();
        }
    }
}
```

#### 4. State Pattern
**Concept**: Allow object to alter behavior when state changes

**Code Structure**:
```java
public interface State {
    void handle(Context context);
}

public class StartState implements State {
    @Override
    public void handle(Context context) {
        System.out.println("Starting...");
        context.setState(new RunningState());
    }
}

public class RunningState implements State {
    @Override
    public void handle(Context context) {
        System.out.println("Running...");
        context.setState(new StoppedState());
    }
}

public class StoppedState implements State {
    @Override
    public void handle(Context context) {
        System.out.println("Stopped");
        context.setState(new StartState());
    }
}

public class Context {
    private State state;
    
    public void setState(State state) {
        this.state = state;
    }
    
    public void request() {
        state.handle(this);
    }
}
```

#### 5. Template Method Pattern
**Concept**: Define skeleton of algorithm in base class

**Code Structure**:
```java
public abstract class DataProcessor {
    public final void process() {
        readData();
        validateData();
        transformData();
        saveData();
    }
    
    protected abstract void readData();
    protected abstract void validateData();
    protected abstract void transformData();
    protected abstract void saveData();
}

public class CSVProcessor extends DataProcessor {
    @Override
    protected void readData() {
        System.out.println("Reading CSV file");
    }
    
    @Override
    protected void validateData() {
        System.out.println("Validating CSV data");
    }
    
    @Override
    protected void transformData() {
        System.out.println("Transforming CSV data");
    }
    
    @Override
    protected void saveData() {
        System.out.println("Saving CSV data");
    }
}
```

### Additional Patterns

#### 6. Chain of Responsibility Pattern
- [ ] Request handling chain
- [ ] Handler delegation

#### 7. Interpreter Pattern
- [ ] Language interpretation
- [ ] Expression evaluation

#### 8. Iterator Pattern
- [ ] Sequential access
- [ ] Collection traversal

#### 9. Mediator Pattern
- [ ] Object communication
- [ ] Decoupling

#### 10. Visitor Pattern
- [ ] Operation on elements
- [ ] Algorithm separation

### Portfolio Project: Workflow Engine

**Project Description**:
Build a workflow engine using behavioral patterns

**Requirements**:
- [ ] Implement observer pattern for notifications
- [ ] Use strategy pattern for different workflows
- [ ] Apply command pattern for actions
- [ ] Implement state pattern for workflow states
- [ ] Use template method for workflow steps

**Deliverables**:
- [ ] Complete implementation
- [ ] 50+ unit tests
- [ ] Documentation
- [ ] Usage examples

---

## 🎯 Lab 24: Regular Expressions

### Lab Overview
**Duration**: 4 hours  
**Content**: 4,000+ lines  
**Examples**: 80+  
**Tests**: 150+  
**Projects**: 1  

### Learning Objectives
By completing this lab, learners will:
- ✅ Master regex syntax and patterns
- ✅ Use Pattern and Matcher classes
- ✅ Implement complex pattern matching
- ✅ Optimize regex performance
- ✅ Apply regex in real-world scenarios

### Topics to Cover

#### 1. Regex Basics
- [ ] Character classes
- [ ] Quantifiers
- [ ] Anchors
- [ ] Alternation
- [ ] Grouping

#### 2. Pattern Matching
- [ ] Pattern compilation
- [ ] Matcher creation
- [ ] Finding matches
- [ ] Replacing text
- [ ] Splitting strings

#### 3. Advanced Patterns
- [ ] Lookahead and lookbehind
- [ ] Named groups
- [ ] Backreferences
- [ ] Unicode support
- [ ] Performance optimization

#### 4. Real-World Examples
- [ ] Email validation
- [ ] URL parsing
- [ ] Phone number validation
- [ ] Date parsing
- [ ] Log parsing

### Portfolio Project: Text Processing Tool

**Project Description**:
Build a text processing tool using regular expressions

**Requirements**:
- [ ] Parse and validate various formats
- [ ] Extract information from text
- [ ] Transform text using patterns
- [ ] Optimize regex performance
- [ ] Handle edge cases

**Deliverables**:
- [ ] Complete implementation
- [ ] 50+ unit tests
- [ ] Documentation
- [ ] Usage examples

---

## 🎯 Lab 25: Date & Time API

### Lab Overview
**Duration**: 4 hours  
**Content**: 4,000+ lines  
**Examples**: 80+  
**Tests**: 150+  
**Projects**: 1  

### Learning Objectives
By completing this lab, learners will:
- ✅ Master Java 8+ Date/Time API
- ✅ Work with LocalDate, LocalTime, LocalDateTime
- ✅ Handle timezones and ZonedDateTime
- ✅ Calculate durations and periods
- ✅ Format and parse dates
- ✅ Apply in real-world scenarios

### Topics to Cover

#### 1. Core Classes
- [ ] LocalDate
- [ ] LocalTime
- [ ] LocalDateTime
- [ ] ZonedDateTime
- [ ] Instant

#### 2. Duration and Period
- [ ] Duration calculations
- [ ] Period calculations
- [ ] Temporal arithmetic
- [ ] Comparisons

#### 3. Formatting and Parsing
- [ ] DateTimeFormatter
- [ ] Custom formats
- [ ] Parsing dates
- [ ] Locale support

#### 4. Timezone Handling
- [ ] ZoneId
- [ ] ZoneOffset
- [ ] Timezone conversions
- [ ] DST handling

#### 5. Real-World Scenarios
- [ ] Scheduling
- [ ] Reporting
- [ ] Logging
- [ ] Event handling

### Portfolio Project: Event Scheduling System

**Project Description**:
Build an event scheduling system using Date/Time API

**Requirements**:
- [ ] Create and manage events
- [ ] Handle timezones
- [ ] Calculate durations
- [ ] Format dates for display
- [ ] Handle recurring events

**Deliverables**:
- [ ] Complete implementation
- [ ] 50+ unit tests
- [ ] Documentation
- [ ] Usage examples

---

## 📊 Implementation Summary

### Timeline
- **Week 6**: Labs 21-23 (Design Patterns) - 15 hours
- **Week 7-8**: Labs 24-25 (Utilities) - 8 hours
- **Total**: 23 hours, 21,500+ lines

### Deliverables
- [ ] 5 complete labs
- [ ] 400+ code examples
- [ ] 750+ unit tests
- [ ] 5 portfolio projects
- [ ] 25 exercises
- [ ] 50 quiz questions
- [ ] 5 advanced challenges
- [ ] Best practices guides

### Quality Standards
- ✅ 80%+ test coverage
- ✅ Clean code principles
- ✅ SOLID design patterns
- ✅ Professional documentation
- ✅ Real-world examples

---

## 📄 Document Information

| Property | Value |
|----------|-------|
| **Document Type** | Detailed Specifications |
| **Version** | 1.0 |
| **Created** | 2024 |
| **Status** | Active |
| **Target Labs** | 21-25 |

---

**Java Master Lab - Labs 21-25 Detailed Specifications**

*Comprehensive specifications for Phase 2B implementation*

**Status: Ready for Implementation | Duration: 2 weeks | Quality: Professional**

---

*Ready to implement Labs 21-25!* 🚀