# Module 11: Design Patterns - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-10 (Core Java, OOP, Lambdas)  
**Estimated Reading Time**: 60-75 minutes  
**Code Examples**: 140+

---

## 📚 Table of Contents

1. [Introduction to Design Patterns](#introduction)
2. [Creational Patterns](#creational)
3. [Structural Patterns](#structural)
4. [Behavioral Patterns](#behavioral)
5. [Architectural Patterns](#architectural)
6. [Anti-Patterns](#antipatterns)
7. [Pattern Selection Guide](#selection)
8. [Real-World Applications](#realworld)

---

## <a name="introduction"></a>1. Introduction to Design Patterns

### What Are Design Patterns?

Design patterns are **reusable solutions** to common problems in software design. They provide templates for writing maintainable, scalable, and robust code.

### Why Design Patterns Matter

**Benefits**:
- ✅ Proven solutions to common problems
- ✅ Improved code maintainability
- ✅ Better communication among developers
- ✅ Reduced development time
- ✅ Increased code reusability
- ✅ Better architecture

**Categories**:
1. **Creational Patterns** - Object creation
2. **Structural Patterns** - Object composition
3. **Behavioral Patterns** - Object interaction
4. **Architectural Patterns** - System structure

---

## <a name="creational"></a>2. Creational Patterns

### Singleton Pattern

**Purpose**: Ensure a class has only one instance and provide global access.

```java
// Eager initialization
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    
    private Singleton() {
    }
    
    public static Singleton getInstance() {
        return INSTANCE;
    }
}

// Lazy initialization
public class LazySingleton {
    private static LazySingleton instance;
    
    private LazySingleton() {
    }
    
    public static synchronized LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
}

// Double-checked locking
public class DoubleCheckedSingleton {
    private static volatile DoubleCheckedSingleton instance;
    
    private DoubleCheckedSingleton() {
    }
    
    public static DoubleCheckedSingleton getInstance() {
        if (instance == null) {
            synchronized (DoubleCheckedSingleton.class) {
                if (instance == null) {
                    instance = new DoubleCheckedSingleton();
                }
            }
        }
        return instance;
    }
}

// Bill Pugh Singleton (best practice)
public class BillPughSingleton {
    private BillPughSingleton() {
    }
    
    private static class SingletonHelper {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }
    
    public static BillPughSingleton getInstance() {
        return SingletonHelper.INSTANCE;
    }
}

// Enum Singleton (thread-safe, serialization-safe)
public enum EnumSingleton {
    INSTANCE;
    
    public void doSomething() {
        System.out.println("Doing something");
    }
}

// Usage
EnumSingleton singleton = EnumSingleton.INSTANCE;
singleton.doSomething();
```

### Factory Pattern

**Purpose**: Create objects without specifying exact classes.

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
                throw new IllegalArgumentException("Unknown database type: " + type);
        }
    }
}

interface Database {
    void connect();
    void query(String sql);
}

class MySQLDatabase implements Database {
    @Override
    public void connect() {
        System.out.println("Connecting to MySQL");
    }
    
    @Override
    public void query(String sql) {
        System.out.println("Executing MySQL query: " + sql);
    }
}

// Usage
Database db = DatabaseFactory.createDatabase("mysql");
db.connect();
db.query("SELECT * FROM users");
```

### Builder Pattern

**Purpose**: Construct complex objects step by step.

```java
public class User {
    private final String name;
    private final String email;
    private final int age;
    private final String phone;
    private final String address;
    
    private User(Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.age = builder.age;
        this.phone = builder.phone;
        this.address = builder.address;
    }
    
    public static class Builder {
        private final String name;
        private final String email;
        private int age;
        private String phone;
        private String address;
        
        public Builder(String name, String email) {
            this.name = name;
            this.email = email;
        }
        
        public Builder age(int age) {
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
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

// Usage
User user = new User.Builder("John Doe", "john@example.com")
    .age(30)
    .phone("555-1234")
    .address("123 Main St")
    .build();
```

### Prototype Pattern

**Purpose**: Create new objects by cloning existing ones.

```java
public abstract class Shape implements Cloneable {
    protected String color;
    
    public Shape(String color) {
        this.color = color;
    }
    
    @Override
    public abstract Shape clone();
    
    public abstract void draw();
}

public class Circle extends Shape {
    private int radius;
    
    public Circle(String color, int radius) {
        super(color);
        this.radius = radius;
    }
    
    @Override
    public Shape clone() {
        try {
            return (Circle) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing circle with color: " + color + ", radius: " + radius);
    }
}

// Usage
Circle original = new Circle("Red", 5);
Circle clone = (Circle) original.clone();
clone.draw();
```

---

## <a name="structural"></a>3. Structural Patterns

### Adapter Pattern

**Purpose**: Convert interface of a class to another interface clients expect.

```java
// Existing interface
public interface MediaPlayer {
    void play(String audioType, String fileName);
}

// New interface
public interface AdvancedMediaPlayer {
    void playVlc(String fileName);
    void playMp4(String fileName);
}

// Implementation
public class VlcPlayer implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        System.out.println("Playing VLC file: " + fileName);
    }
    
    @Override
    public void playMp4(String fileName) {
        // Not supported
    }
}

// Adapter
public class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedMediaPlayer;
    
    public MediaAdapter(String audioType) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedMediaPlayer = new VlcPlayer();
        }
    }
    
    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedMediaPlayer.playVlc(fileName);
        }
    }
}

// Usage
MediaPlayer player = new MediaAdapter("vlc");
player.play("vlc", "movie.vlc");
```

### Decorator Pattern

**Purpose**: Attach additional responsibilities to an object dynamically.

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
System.out.println(coffee.getDescription());  // Simple Coffee, Milk, Sugar
System.out.println(coffee.getCost());         // 2.7
```

### Facade Pattern

**Purpose**: Provide unified interface to set of interfaces in subsystem.

```java
// Complex subsystem
public class CPU {
    public void freeze() {
        System.out.println("CPU freezing");
    }
    
    public void jump(long position) {
        System.out.println("CPU jumping to position: " + position);
    }
    
    public void execute() {
        System.out.println("CPU executing");
    }
}

public class Memory {
    public void load(long position, byte[] data) {
        System.out.println("Loading data at position: " + position);
    }
}

public class HardDrive {
    public byte[] read(long lba, int size) {
        System.out.println("Reading from hard drive");
        return new byte[size];
    }
}

// Facade
public class ComputerFacade {
    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;
    
    public ComputerFacade() {
        this.cpu = new CPU();
        this.memory = new Memory();
        this.hardDrive = new HardDrive();
    }
    
    public void start() {
        cpu.freeze();
        memory.load(0, hardDrive.read(0, 1024));
        cpu.jump(0);
        cpu.execute();
    }
}

// Usage
ComputerFacade computer = new ComputerFacade();
computer.start();
```

### Proxy Pattern

**Purpose**: Provide surrogate or placeholder for another object.

```java
public interface Image {
    void display();
}

public class RealImage implements Image {
    private String fileName;
    
    public RealImage(String fileName) {
        this.fileName = fileName;
        loadFromDisk();
    }
    
    private void loadFromDisk() {
        System.out.println("Loading image: " + fileName);
    }
    
    @Override
    public void display() {
        System.out.println("Displaying image: " + fileName);
    }
}

public class ProxyImage implements Image {
    private String fileName;
    private RealImage realImage;
    
    public ProxyImage(String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public void display() {
        if (realImage == null) {
            realImage = new RealImage(fileName);
        }
        realImage.display();
    }
}

// Usage
Image image = new ProxyImage("photo.jpg");
image.display();  // Loads and displays
image.display();  // Just displays (already loaded)
```

---

## <a name="behavioral"></a>4. Behavioral Patterns

### Observer Pattern

**Purpose**: Define one-to-many dependency between objects.

```java
public interface Observer {
    void update(String message);
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

// Usage
Subject subject = new Subject();
Observer observer1 = new ConcreteObserver("Observer 1");
Observer observer2 = new ConcreteObserver("Observer 2");

subject.attach(observer1);
subject.attach(observer2);
subject.notifyObservers("Hello Observers!");
```

### Strategy Pattern

**Purpose**: Define family of algorithms, encapsulate each one.

```java
public interface PaymentStrategy {
    void pay(double amount);
}

public class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    
    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    @Override
    public void pay(double amount) {
        System.out.println("Paying " + amount + " with credit card: " + cardNumber);
    }
}

public class PayPalPayment implements PaymentStrategy {
    private String email;
    
    public PayPalPayment(String email) {
        this.email = email;
    }
    
    @Override
    public void pay(double amount) {
        System.out.println("Paying " + amount + " with PayPal: " + email);
    }
}

public class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
    
    public void checkout(double amount) {
        paymentStrategy.pay(amount);
    }
}

// Usage
ShoppingCart cart = new ShoppingCart();
cart.setPaymentStrategy(new CreditCardPayment("1234-5678-9012-3456"));
cart.checkout(100.0);

cart.setPaymentStrategy(new PayPalPayment("user@example.com"));
cart.checkout(50.0);
```

### Command Pattern

**Purpose**: Encapsulate request as an object.

```java
public interface Command {
    void execute();
    void undo();
}

public class Light {
    public void on() {
        System.out.println("Light is on");
    }
    
    public void off() {
        System.out.println("Light is off");
    }
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
    
    public void setCommand(Command command) {
        this.command = command;
    }
    
    public void pressButton() {
        command.execute();
    }
    
    public void pressUndo() {
        command.undo();
    }
}

// Usage
Light light = new Light();
Command lightOn = new LightOnCommand(light);
RemoteControl remote = new RemoteControl();
remote.setCommand(lightOn);
remote.pressButton();  // Light is on
remote.pressUndo();    // Light is off
```

### State Pattern

**Purpose**: Allow object to alter behavior when state changes.

```java
public interface State {
    void handle(Context context);
}

public class ConcreteStateA implements State {
    @Override
    public void handle(Context context) {
        System.out.println("State A handling");
        context.setState(new ConcreteStateB());
    }
}

public class ConcreteStateB implements State {
    @Override
    public void handle(Context context) {
        System.out.println("State B handling");
        context.setState(new ConcreteStateA());
    }
}

public class Context {
    private State state;
    
    public Context(State state) {
        this.state = state;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    
    public void request() {
        state.handle(this);
    }
}

// Usage
Context context = new Context(new ConcreteStateA());
context.request();  // State A handling
context.request();  // State B handling
context.request();  // State A handling
```

---

## <a name="architectural"></a>5. Architectural Patterns

### MVC Pattern

**Purpose**: Separate application into Model, View, Controller.

```java
// Model
public class User {
    private String name;
    private String email;
    
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
}

// View
public class UserView {
    public void displayUser(String name, String email) {
        System.out.println("User: " + name + ", Email: " + email);
    }
}

// Controller
public class UserController {
    private User model;
    private UserView view;
    
    public UserController(User model, UserView view) {
        this.model = model;
        this.view = view;
    }
    
    public void updateView() {
        view.displayUser(model.getName(), model.getEmail());
    }
}

// Usage
User user = new User("John Doe", "john@example.com");
UserView view = new UserView();
UserController controller = new UserController(user, view);
controller.updateView();
```

### Repository Pattern

**Purpose**: Encapsulate data access logic.

```java
public interface UserRepository {
    void save(User user);
    User findById(int id);
    List<User> findAll();
    void delete(int id);
}

public class InMemoryUserRepository implements UserRepository {
    private Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;
    
    @Override
    public void save(User user) {
        users.put(nextId++, user);
    }
    
    @Override
    public User findById(int id) {
        return users.get(id);
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public void delete(int id) {
        users.remove(id);
    }
}

// Usage
UserRepository repository = new InMemoryUserRepository();
repository.save(new User("John", "john@example.com"));
User user = repository.findById(1);
```

---

## <a name="antipatterns"></a>6. Anti-Patterns

### God Object

**❌ WRONG**:
```java
// God Object - does too much
public class User {
    // User properties
    private String name;
    private String email;
    
    // Database operations
    public void save() { }
    public void delete() { }
    public User findById(int id) { }
    
    // Email operations
    public void sendEmail() { }
    
    // Validation
    public boolean validate() { }
    
    // Logging
    public void log() { }
}
```

**✅ CORRECT**:
```java
// Separated concerns
public class User {
    private String name;
    private String email;
}

public class UserRepository {
    public void save(User user) { }
    public void delete(User user) { }
    public User findById(int id) { }
}

public class EmailService {
    public void sendEmail(User user) { }
}

public class UserValidator {
    public boolean validate(User user) { }
}
```

### Spaghetti Code

**❌ WRONG**:
```java
// Spaghetti code - tangled logic
public void processOrder(Order order) {
    if (order.getItems().size() > 0) {
        double total = 0;
        for (Item item : order.getItems()) {
            total += item.getPrice() * item.getQuantity();
        }
        if (total > 100) {
            total *= 0.9;  // 10% discount
        }
        if (order.getCustomer().isPremium()) {
            total *= 0.95;  // 5% premium discount
        }
        // ... more logic
    }
}
```

**✅ CORRECT**:
```java
// Clean, organized code
public void processOrder(Order order) {
    double total = calculateTotal(order);
    total = applyDiscounts(total, order);
    saveOrder(order, total);
}

private double calculateTotal(Order order) {
    return order.getItems().stream()
        .mapToDouble(item -> item.getPrice() * item.getQuantity())
        .sum();
}

private double applyDiscounts(double total, Order order) {
    total = applyBulkDiscount(total);
    total = applyPremiumDiscount(total, order);
    return total;
}
```

---

## <a name="selection"></a>7. Pattern Selection Guide

### When to Use Each Pattern

```
Need to create objects?
├─ Single instance needed? → Singleton
├─ Complex construction? → Builder
├─ Different types? → Factory
└─ Clone existing? → Prototype

Need to compose objects?
├─ Adapt interface? → Adapter
├─ Add responsibilities? → Decorator
├─ Simplify subsystem? → Facade
└─ Control access? → Proxy

Need to define interactions?
├─ One-to-many notification? → Observer
├─ Encapsulate algorithm? → Strategy
├─ Encapsulate request? → Command
├─ Change behavior by state? → State
└─ Traverse structure? → Iterator
```

---

## <a name="realworld"></a>8. Real-World Applications

### Spring Framework Patterns

```java
// Singleton - Spring Beans
@Component
public class UserService {
    // Single instance managed by Spring
}

// Factory - BeanFactory
ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
UserService userService = context.getBean(UserService.class);

// Proxy - AOP
@Aspect
public class LoggingAspect {
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Calling: " + joinPoint.getSignature());
    }
}

// Observer - Event Publishing
@Component
public class UserEventPublisher {
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public void publishUserCreatedEvent(User user) {
        eventPublisher.publishEvent(new UserCreatedEvent(user));
    }
}
```

---

## 🎯 Key Takeaways

1. **Design patterns are proven solutions** to common problems
2. **Creational patterns** focus on object creation
3. **Structural patterns** focus on object composition
4. **Behavioral patterns** focus on object interaction
5. **Know when to use each pattern** - avoid over-engineering
6. **Combine patterns** for complex solutions
7. **Recognize anti-patterns** and avoid them

---

## 📚 Further Reading

- [Gang of Four Design Patterns](https://en.wikipedia.org/wiki/Design_Patterns)
- [Refactoring Guru Design Patterns](https://refactoring.guru/design-patterns)
- [Oracle Design Patterns](https://docs.oracle.com/javase/tutorial/java/concepts/index.html)

---

**Module 11 - Design Patterns**  
*Master proven solutions for software design*