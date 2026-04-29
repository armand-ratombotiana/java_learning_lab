# Module 11: Design Patterns - Edge Cases & Pitfalls

**Critical Pitfalls**: 18  
**Prevention Strategies**: 18  
**Real-World Scenarios**: 12

---

## 🚨 Critical Pitfalls & Prevention

### 1. Singleton Serialization Issue

**❌ PITFALL**:
```java
public class Singleton implements Serializable {
    private static final Singleton INSTANCE = new Singleton();
    
    private Singleton() {
    }
    
    public static Singleton getInstance() {
        return INSTANCE;
    }
}

// Problem: Deserialization creates a new instance
Singleton singleton1 = Singleton.getInstance();
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(singleton1);

ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
Singleton singleton2 = (Singleton) ois.readObject();

// singleton1 != singleton2  ❌ BROKEN!
```

**✅ PREVENTION**:
```java
public class Singleton implements Serializable {
    private static final Singleton INSTANCE = new Singleton();
    
    private Singleton() {
    }
    
    public static Singleton getInstance() {
        return INSTANCE;
    }
    
    // Prevent deserialization from creating new instance
    protected Object readResolve() {
        return getInstance();
    }
}

// Now singleton1 == singleton2 ✅ CORRECT!
```

**Why It Matters**: Serialization can break the Singleton pattern by creating multiple instances during deserialization.

---

### 2. Singleton Reflection Attack

**❌ PITFALL**:
```java
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    
    private Singleton() {
    }
    
    public static Singleton getInstance() {
        return INSTANCE;
    }
}

// Reflection can break Singleton
try {
    Constructor<Singleton> constructor = Singleton.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    Singleton singleton2 = constructor.newInstance();
    // singleton2 is a different instance! ❌ BROKEN!
} catch (Exception e) {
    e.printStackTrace();
}
```

**✅ PREVENTION**:
```java
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    private static boolean instantiated = false;
    
    private Singleton() {
        if (instantiated) {
            throw new IllegalStateException("Singleton already instantiated");
        }
        instantiated = true;
    }
    
    public static Singleton getInstance() {
        return INSTANCE;
    }
}

// Or use Enum (best practice)
public enum Singleton {
    INSTANCE;
    
    public void doSomething() {
        System.out.println("Doing something");
    }
}
```

**Why It Matters**: Reflection can bypass private constructors, breaking the Singleton pattern.

---

### 3. Factory Pattern Over-Engineering

**❌ PITFALL**:
```java
// Over-engineered factory for simple objects
public interface DatabaseFactory {
    Database createDatabase();
}

public class MySQLDatabaseFactory implements DatabaseFactory {
    @Override
    public Database createDatabase() {
        return new MySQLDatabase();
    }
}

public class PostgreSQLDatabaseFactory implements DatabaseFactory {
    @Override
    public Database createDatabase() {
        return new PostgreSQLDatabase();
    }
}

// Excessive complexity for simple creation
```

**✅ PREVENTION**:
```java
// Use simple factory for straightforward cases
public class DatabaseFactory {
    public static Database createDatabase(String type) {
        switch (type.toLowerCase()) {
            case "mysql":
                return new MySQLDatabase();
            case "postgresql":
                return new PostgreSQLDatabase();
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}

// Use Abstract Factory only when creating families of related objects
public interface DatabaseFactory {
    Database createDatabase();
    ConnectionPool createConnectionPool();
}
```

**Why It Matters**: Over-engineering with patterns adds unnecessary complexity.

---

### 4. Builder Pattern Immutability Loss

**❌ PITFALL**:
```java
public class User {
    private String name;
    private String email;
    private List<String> roles;
    
    private User(Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.roles = builder.roles;  // Mutable reference!
    }
    
    public List<String> getRoles() {
        return roles;  // Exposes mutable list
    }
    
    public static class Builder {
        private String name;
        private String email;
        private List<String> roles = new ArrayList<>();
        
        public Builder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }
        
        public User build() {
            return new User(this);
        }
    }
}

// Problem: Roles can be modified after creation
User user = new User.Builder("John", "john@example.com")
    .roles(new ArrayList<>(Arrays.asList("admin")))
    .build();

user.getRoles().add("user");  // ❌ Mutated!
```

**✅ PREVENTION**:
```java
public class User {
    private final String name;
    private final String email;
    private final List<String> roles;
    
    private User(Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
        // Create defensive copy
        this.roles = Collections.unmodifiableList(
            new ArrayList<>(builder.roles)
        );
    }
    
    public List<String> getRoles() {
        return roles;  // Unmodifiable
    }
    
    public static class Builder {
        private final String name;
        private final String email;
        private final List<String> roles = new ArrayList<>();
        
        public Builder(String name, String email) {
            this.name = name;
            this.email = email;
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

// Now roles cannot be modified
User user = new User.Builder("John", "john@example.com")
    .addRole("admin")
    .build();

// user.getRoles().add("user");  // ❌ UnsupportedOperationException
```

**Why It Matters**: Builder pattern should create immutable objects; exposing mutable collections breaks this guarantee.

---

### 5. Observer Pattern Memory Leaks

**❌ PITFALL**:
```java
public class Subject {
    private List<Observer> observers = new ArrayList<>();
    
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}

// Problem: Observers are never removed
Subject subject = new Subject();
Observer observer = new ConcreteObserver("Observer 1");
subject.attach(observer);

// Observer goes out of scope but remains in subject
observer = null;  // ❌ Still referenced by subject!
```

**✅ PREVENTION**:
```java
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

// Properly unsubscribe
Subject subject = new Subject();
Observer observer = new ConcreteObserver("Observer 1");
subject.attach(observer);

// ... use observer ...

subject.detach(observer);  // ✅ Properly removed
observer = null;
```

**Why It Matters**: Observers that are not unsubscribed can cause memory leaks.

---

### 6. Strategy Pattern Runtime Errors

**❌ PITFALL**:
```java
public class PaymentProcessor {
    private PaymentStrategy strategy;
    
    public void processPayment(double amount) {
        if (strategy == null) {
            System.out.println("No strategy set");  // ❌ Silent failure
            return;
        }
        strategy.pay(amount);
    }
}

// Problem: Null strategy causes silent failure
PaymentProcessor processor = new PaymentProcessor();
processor.processPayment(100);  // Silent failure
```

**✅ PREVENTION**:
```java
public class PaymentProcessor {
    private PaymentStrategy strategy;
    
    public void setStrategy(PaymentStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }
        this.strategy = strategy;
    }
    
    public void processPayment(double amount) {
        if (strategy == null) {
            throw new IllegalStateException("Payment strategy not set");
        }
        strategy.pay(amount);
    }
}

// Or use Optional
public class PaymentProcessor {
    private Optional<PaymentStrategy> strategy = Optional.empty();
    
    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = Optional.of(strategy);
    }
    
    public void processPayment(double amount) {
        strategy.orElseThrow(() -> 
            new IllegalStateException("Payment strategy not set")
        ).pay(amount);
    }
}
```

**Why It Matters**: Null strategies can cause silent failures or unexpected behavior.

---

### 7. Decorator Pattern Infinite Recursion

**❌ PITFALL**:
```java
public abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
    
    @Override
    public double getCost() {
        return coffee.getCost();  // Delegates to wrapped object
    }
}

// Problem: Circular decoration
Coffee coffee = new SimpleCoffee();
CoffeeDecorator decorator1 = new MilkDecorator(coffee);
CoffeeDecorator decorator2 = new SugarDecorator(decorator1);
CoffeeDecorator decorator3 = new MilkDecorator(decorator2);

// If decorator3 somehow wraps itself, infinite recursion!
```

**✅ PREVENTION**:
```java
public abstract class CoffeeDecorator implements Coffee {
    protected final Coffee coffee;  // Final to prevent reassignment
    
    public CoffeeDecorator(Coffee coffee) {
        if (coffee == null) {
            throw new IllegalArgumentException("Coffee cannot be null");
        }
        // Prevent wrapping a decorator with itself
        if (coffee instanceof CoffeeDecorator && 
            coffee.getClass() == this.getClass()) {
            throw new IllegalArgumentException(
                "Cannot wrap same decorator type"
            );
        }
        this.coffee = coffee;
    }
    
    @Override
    public double getCost() {
        return coffee.getCost();
    }
}
```

**Why It Matters**: Circular decoration can cause infinite recursion and stack overflow.

---

### 8. Facade Pattern Hidden Complexity

**❌ PITFALL**:
```java
// Facade that hides too much
public class ComputerFacade {
    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;
    
    public void start() {
        // 50 lines of complex initialization
        // Hides all details from client
    }
    
    public void shutdown() {
        // 50 lines of complex shutdown
    }
}

// Problem: Clients can't customize behavior
ComputerFacade computer = new ComputerFacade();
computer.start();  // All-or-nothing approach
```

**✅ PREVENTION**:
```java
// Facade that provides both simple and detailed access
public class ComputerFacade {
    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;
    
    // Simple interface for common use case
    public void start() {
        startCPU();
        loadMemory();
        initializeHardDrive();
    }
    
    // Detailed methods for customization
    public void startCPU() {
        cpu.freeze();
        cpu.jump(0);
    }
    
    public void loadMemory() {
        memory.load(0, hardDrive.read(0, 1024));
    }
    
    public void initializeHardDrive() {
        // Initialization logic
    }
}

// Clients can use simple or detailed interface
ComputerFacade computer = new ComputerFacade();
computer.start();  // Simple
computer.startCPU();  // Detailed
```

**Why It Matters**: Facades should provide both simple and detailed access.

---

### 9. Proxy Pattern Performance Overhead

**❌ PITFALL**:
```java
// Proxy that adds unnecessary overhead
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
        // Additional overhead on every call
        logAccess();
        validatePermissions();
        realImage.display();
    }
    
    private void logAccess() {
        // Logging overhead
    }
    
    private void validatePermissions() {
        // Permission check overhead
    }
}

// Problem: Overhead on every call, not just creation
```

**✅ PREVENTION**:
```java
// Proxy with appropriate overhead
public class ProxyImage implements Image {
    private String fileName;
    private RealImage realImage;
    private boolean initialized = false;
    
    public ProxyImage(String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public void display() {
        // Lazy load only once
        if (!initialized) {
            realImage = new RealImage(fileName);
            initialized = true;
        }
        // Minimal overhead after initialization
        realImage.display();
    }
}

// Or use proxy only for expensive operations
public class ExpensiveOperationProxy {
    private ExpensiveOperation operation;
    private boolean cached = false;
    private Object result;
    
    public Object execute() {
        if (!cached) {
            operation = new ExpensiveOperation();
            result = operation.execute();
            cached = true;
        }
        return result;
    }
}
```

**Why It Matters**: Proxies should add overhead only when necessary.

---

### 10. Command Pattern Undo Stack Issues

**❌ PITFALL**:
```java
public class CommandHistory {
    private Stack<Command> history = new Stack<>();
    
    public void execute(Command command) {
        command.execute();
        history.push(command);
    }
    
    public void undo() {
        if (!history.isEmpty()) {
            Command command = history.pop();
            command.undo();
        }
    }
}

// Problem: No limit on history size
CommandHistory history = new CommandHistory();
for (int i = 0; i < 1000000; i++) {
    history.execute(new SomeCommand());  // ❌ Memory leak!
}
```

**✅ PREVENTION**:
```java
public class CommandHistory {
    private final Deque<Command> history = new LinkedList<>();
    private final int maxSize;
    
    public CommandHistory(int maxSize) {
        this.maxSize = maxSize;
    }
    
    public void execute(Command command) {
        command.execute();
        history.push(command);
        
        // Limit history size
        if (history.size() > maxSize) {
            history.removeLast();
        }
    }
    
    public void undo() {
        if (!history.isEmpty()) {
            Command command = history.pop();
            command.undo();
        }
    }
}

// Usage with limited history
CommandHistory history = new CommandHistory(100);  // Max 100 commands
```

**Why It Matters**: Unlimited undo history can cause memory issues.

---

### 11. State Pattern State Explosion

**❌ PITFALL**:
```java
// Too many states
public interface State {
    void handle(Context context);
}

public class State1 implements State { }
public class State2 implements State { }
public class State3 implements State { }
public class State4 implements State { }
public class State5 implements State { }
// ... 50 more states

// Problem: Difficult to manage and maintain
```

**✅ PREVENTION**:
```java
// Simplify state hierarchy
public enum OrderState {
    PENDING {
        @Override
        public void handle(Order order) {
            order.confirm();
        }
    },
    CONFIRMED {
        @Override
        public void handle(Order order) {
            order.ship();
        }
    },
    SHIPPED {
        @Override
        public void handle(Order order) {
            order.deliver();
        }
    };
    
    public abstract void handle(Order order);
}

// Or use state machine library
// StateMachine<OrderState, OrderEvent> stateMachine = 
//     StateMachineBuilder.builder()
//         .states(EnumSet.allOf(OrderState.class))
//         .build();
```

**Why It Matters**: Too many states make the pattern unmanageable.

---

### 12. Repository Pattern Query Complexity

**❌ PITFALL**:
```java
public interface UserRepository {
    List<User> findByNameAndEmailAndAgeAndCity(
        String name, String email, int age, String city
    );
    
    List<User> findByNameAndAge(String name, int age);
    
    List<User> findByEmailAndCity(String email, String city);
    
    // Explosion of query methods
}

// Problem: Too many specific query methods
```

**✅ PREVENTION**:
```java
// Use Specification pattern
public interface UserRepository {
    List<User> findAll(Specification<User> spec);
}

public class UserSpecifications {
    public static Specification<User> byName(String name) {
        return (root, query, cb) -> cb.equal(root.get("name"), name);
    }
    
    public static Specification<User> byEmail(String email) {
        return (root, query, cb) -> cb.equal(root.get("email"), email);
    }
    
    public static Specification<User> byAge(int age) {
        return (root, query, cb) -> cb.equal(root.get("age"), age);
    }
}

// Usage
List<User> users = repository.findAll(
    byName("John").and(byAge(30))
);
```

**Why It Matters**: Specific query methods lead to repository explosion.

---

### 13. MVC Pattern Tight Coupling

**❌ PITFALL**:
```java
// Controller directly manipulates View
public class UserController {
    private UserView view;
    
    public void updateUser(User user) {
        // Direct view manipulation
        view.nameField.setText(user.getName());
        view.emailField.setText(user.getEmail());
        view.ageField.setText(String.valueOf(user.getAge()));
        // ❌ Tight coupling to view implementation
    }
}
```

**✅ PREVENTION**:
```java
// Controller uses view interface
public interface UserView {
    void displayUser(User user);
    void showError(String message);
}

public class UserController {
    private User model;
    private UserView view;
    
    public void updateUser(User user) {
        this.model = user;
        view.displayUser(user);  // ✅ Loose coupling
    }
}

// View implementation
public class UserSwingView implements UserView {
    private JTextField nameField;
    private JTextField emailField;
    
    @Override
    public void displayUser(User user) {
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
    }
}
```

**Why It Matters**: Tight coupling makes code hard to test and maintain.

---

### 14. Abstract Factory Complexity

**❌ PITFALL**:
```java
// Over-complicated abstract factory
public interface UIFactory {
    Button createButton();
    TextField createTextField();
    CheckBox createCheckBox();
    RadioButton createRadioButton();
    ComboBox createComboBox();
    // ... 20 more methods
}

// Problem: Too many methods to implement
```

**✅ PREVENTION**:
```java
// Focused abstract factory
public interface UIFactory {
    Button createButton();
    TextField createTextField();
}

// Separate factories for different concerns
public interface LayoutFactory {
    Layout createVerticalLayout();
    Layout createHorizontalLayout();
}

// Or use builder with factory
public class UIBuilder {
    private UIFactory factory;
    
    public UIBuilder(UIFactory factory) {
        this.factory = factory;
    }
    
    public UIBuilder addButton(String label) {
        // Use factory to create button
        return this;
    }
}
```

**Why It Matters**: Abstract factories with too many methods are hard to implement.

---

### 15. Prototype Pattern Deep Copy Issues

**❌ PITFALL**:
```java
public class User implements Cloneable {
    private String name;
    private List<String> roles;
    
    @Override
    public User clone() {
        try {
            return (User) super.clone();  // Shallow copy!
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

// Problem: Shallow copy shares mutable objects
User original = new User("John", Arrays.asList("admin"));
User clone = original.clone();
clone.getRoles().add("user");  // ❌ Modifies original too!
```

**✅ PREVENTION**:
```java
public class User implements Cloneable {
    private String name;
    private List<String> roles;
    
    @Override
    public User clone() {
        try {
            User cloned = (User) super.clone();
            // Deep copy mutable objects
            cloned.roles = new ArrayList<>(this.roles);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

// Or use copy constructor
public class User {
    private String name;
    private List<String> roles;
    
    public User(User other) {
        this.name = other.name;
        this.roles = new ArrayList<>(other.roles);
    }
}
```

**Why It Matters**: Shallow copies can lead to unexpected mutations.

---

### 16. Adapter Pattern Incompatibility

**❌ PITFALL**:
```java
// Adapter that doesn't properly adapt
public class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedPlayer;
    
    public MediaAdapter(String audioType) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedPlayer = new VlcPlayer();
        }
    }
    
    @Override
    public void play(String audioType, String fileName) {
        // Incomplete adaptation
        if (advancedPlayer != null) {
            advancedPlayer.playVlc(fileName);
        }
        // ❌ Doesn't handle all cases
    }
}
```

**✅ PREVENTION**:
```java
// Complete adapter implementation
public class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedPlayer;
    
    public MediaAdapter(String audioType) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedPlayer = new VlcPlayer();
        } else if (audioType.equalsIgnoreCase("mp4")) {
            advancedPlayer = new Mp4Player();
        } else {
            throw new IllegalArgumentException("Unsupported type: " + audioType);
        }
    }
    
    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedPlayer.playVlc(fileName);
        } else if (audioType.equalsIgnoreCase("mp4")) {
            advancedPlayer.playMp4(fileName);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + audioType);
        }
    }
}
```

**Why It Matters**: Incomplete adapters lead to runtime errors.

---

### 17. Pattern Misuse - Using Wrong Pattern

**❌ PITFALL**:
```java
// Using Singleton when Factory is needed
public class DatabaseSingleton {
    private static final DatabaseSingleton INSTANCE = 
        new DatabaseSingleton();
    
    private DatabaseSingleton() {
    }
    
    public static DatabaseSingleton getInstance() {
        return INSTANCE;
    }
    
    public Database getDatabase(String type) {
        // ❌ Wrong pattern - Singleton for multiple instances
    }
}
```

**✅ PREVENTION**:
```java
// Use Factory for multiple instances
public class DatabaseFactory {
    public static Database createDatabase(String type) {
        switch (type.toLowerCase()) {
            case "mysql":
                return new MySQLDatabase();
            case "postgresql":
                return new PostgreSQLDatabase();
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}

// Use Singleton only for single instance
public class ConfigurationManager {
    private static final ConfigurationManager INSTANCE = 
        new ConfigurationManager();
    
    private ConfigurationManager() {
    }
    
    public static ConfigurationManager getInstance() {
        return INSTANCE;
    }
}
```

**Why It Matters**: Using the wrong pattern leads to design flaws.

---

### 18. Pattern Over-Engineering

**❌ PITFALL**:
```java
// Over-engineered simple application
public interface CalculatorFactory {
    Calculator createCalculator();
}

public class SimpleCalculatorFactory implements CalculatorFactory {
    @Override
    public Calculator createCalculator() {
        return new SimpleCalculator();
    }
}

public interface CalculatorDecorator extends Calculator {
    // Decorator for calculator
}

public class LoggingCalculatorDecorator implements CalculatorDecorator {
    // Logging decorator
}

// ❌ Excessive patterns for simple calculator
```

**✅ PREVENTION**:
```java
// Simple, direct approach
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    public int subtract(int a, int b) {
        return a - b;
    }
}

// Add patterns only when needed
public class LoggingCalculator {
    private Calculator calculator;
    
    public LoggingCalculator(Calculator calculator) {
        this.calculator = calculator;
    }
    
    public int add(int a, int b) {
        System.out.println("Adding " + a + " + " + b);
        return calculator.add(a, b);
    }
}
```

**Why It Matters**: Over-engineering adds unnecessary complexity.

---

## 📋 Prevention Checklist

- ✅ Use Enum Singleton for thread-safety and serialization
- ✅ Implement `readResolve()` for serializable Singletons
- ✅ Prevent reflection attacks in Singletons
- ✅ Create defensive copies in Builders
- ✅ Implement `detach()` in Observer pattern
- ✅ Validate null strategies in Strategy pattern
- ✅ Prevent circular decoration in Decorator pattern
- ✅ Provide both simple and detailed Facade interfaces
- ✅ Limit Proxy overhead to necessary operations
- ✅ Implement undo history size limits
- ✅ Simplify State pattern hierarchies
- ✅ Use Specification pattern for complex queries
- ✅ Use interfaces in MVC to reduce coupling
- ✅ Keep Abstract Factory focused
- ✅ Implement deep copy in Prototype pattern
- ✅ Complete Adapter implementations
- ✅ Use appropriate patterns for the problem
- ✅ Avoid over-engineering simple solutions

---

**Module 11 - Design Patterns Edge Cases**  
*Master the pitfalls and prevention strategies*