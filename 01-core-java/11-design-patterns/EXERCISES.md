# Exercises: Design Patterns

<div align="center">

![Module](https://img.shields.io/badge/Module-11-blue?style=for-the-badge)
![Exercises](https://img.shields.io/badge/Exercises-30-green?style=for-the-badge)
![Difficulty](https://img.shields.io/badge/Difficulty-Easy%20to%20Hard-orange?style=for-the-badge)

**30 comprehensive exercises for Design Patterns module**

</div>

---

## 📚 Table of Contents

1. [Easy Exercises (1-10)](#easy-exercises-1-10)
2. [Medium Exercises (11-20)](#medium-exercises-11-20)
3. [Hard Exercises (21-26)](#hard-exercises-21-26)
4. [Interview Exercises (27-30)](#interview-exercises-27-30)

---

## 🟢 Easy Exercises (1-10)

### Exercise 1: Singleton Pattern
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Singleton, single instance, lazy initialization

**Pedagogic Objective:**
Understand the Singleton pattern for ensuring single instance.

**Problem:**
Create a Singleton class that can only have one instance.

**Complete Solution:**
```java
public class Singleton {
    private static Singleton instance;
    
    private Singleton() {
    }
    
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
    
    public void doSomething() {
        System.out.println("Singleton doing something");
    }
}

public class SingletonExample {
    public static void main(String[] args) {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        
        System.out.println("s1 == s2: " + (s1 == s2));
        s1.doSomething();
    }
}
```

**Key Concepts:**
- Private constructor prevents instantiation
- Static instance holds single object
- getInstance() returns same instance
- Thread safety considerations

---

### Exercise 2: Thread-Safe Singleton
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Singleton, thread safety, synchronization

**Pedagogic Objective:**
Understand thread-safe Singleton implementation.

**Complete Solution:**
```java
public class ThreadSafeSingleton {
    private static ThreadSafeSingleton instance;
    
    private ThreadSafeSingleton() {
    }
    
    public static synchronized ThreadSafeSingleton getInstance() {
        if (instance == null) {
            instance = new ThreadSafeSingleton();
        }
        return instance;
    }
}

public class EagerSingleton {
    private static final EagerSingleton instance = new EagerSingleton();
    
    private EagerSingleton() {
    }
    
    public static EagerSingleton getInstance() {
        return instance;
    }
}

public class ThreadSafeSingletonExample {
    public static void main(String[] args) {
        ThreadSafeSingleton s1 = ThreadSafeSingleton.getInstance();
        EagerSingleton s2 = EagerSingleton.getInstance();
        
        System.out.println("Thread-safe singleton: " + s1);
        System.out.println("Eager singleton: " + s2);
    }
}
```

**Key Concepts:**
- Synchronized method ensures thread safety
- Eager initialization loads at startup
- Double-checked locking pattern
- Performance vs safety tradeoff

---

### Exercise 3: Factory Pattern
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Factory, object creation, abstraction

**Pedagogic Objective:**
Understand Factory pattern for object creation.

**Complete Solution:**
```java
public interface Shape {
    void draw();
}

public class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing Circle");
    }
}

public class Rectangle implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing Rectangle");
    }
}

public class ShapeFactory {
    public static Shape createShape(String type) {
        if ("circle".equalsIgnoreCase(type)) {
            return new Circle();
        } else if ("rectangle".equalsIgnoreCase(type)) {
            return new Rectangle();
        }
        return null;
    }
}

public class FactoryPatternExample {
    public static void main(String[] args) {
        Shape circle = ShapeFactory.createShape("circle");
        Shape rectangle = ShapeFactory.createShape("rectangle");
        
        circle.draw();
        rectangle.draw();
    }
}
```

**Key Concepts:**
- Factory method creates objects
- Decouples client from concrete classes
- Centralizes object creation
- Easy to extend with new types

---

### Exercise 4: Builder Pattern
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Builder, object construction, fluent API

**Pedagogic Objective:**
Understand Builder pattern for complex object construction.

**Complete Solution:**
```java
public class Person {
    private String name;
    private int age;
    private String email;
    private String phone;
    
    private Person(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.email = builder.email;
        this.phone = builder.phone;
    }
    
    public static class Builder {
        private String name;
        private int age;
        private String email;
        private String phone;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder age(int age) {
            this.age = age;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }
        
        public Person build() {
            return new Person(this);
        }
    }
    
    @Override
    public String toString() {
        return "Person{" + "name='" + name + '\'' + ", age=" + age + ", email='" + email + '\'' + ", phone='" + phone + '\'' + '}';
    }
}

public class BuilderPatternExample {
    public static void main(String[] args) {
        Person person = new Person.Builder()
            .name("Alice")
            .age(25)
            .email("alice@example.com")
            .phone("123-456-7890")
            .build();
        
        System.out.println(person);
    }
}
```

**Key Concepts:**
- Builder class constructs complex objects
- Fluent API for readability
- Optional parameters easy to handle
- Immutable object creation

---

### Exercise 5: Observer Pattern
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Observer, event handling, loose coupling

**Pedagogic Objective:**
Understand Observer pattern for event notification.

**Complete Solution:**
```java
import java.util.*;

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

public class ObserverPatternExample {
    public static void main(String[] args) {
        Subject subject = new Subject();
        
        Observer obs1 = new ConcreteObserver("Observer1");
        Observer obs2 = new ConcreteObserver("Observer2");
        
        subject.attach(obs1);
        subject.attach(obs2);
        
        subject.notifyObservers("Event occurred!");
    }
}
```

**Key Concepts:**
- Observer interface for subscribers
- Subject maintains observer list
- Loose coupling between components
- Event-driven architecture

---

### Exercise 6: Strategy Pattern
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Strategy, algorithm encapsulation, runtime selection

**Pedagogic Objective:**
Understand Strategy pattern for algorithm selection.

**Complete Solution:**
```java
public interface PaymentStrategy {
    void pay(double amount);
}

public class CreditCardPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Paying $" + amount + " with Credit Card");
    }
}

public class PayPalPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Paying $" + amount + " with PayPal");
    }
}

public class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }
    
    public void checkout(double amount) {
        paymentStrategy.pay(amount);
    }
}

public class StrategyPatternExample {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        
        cart.setPaymentStrategy(new CreditCardPayment());
        cart.checkout(100.0);
        
        cart.setPaymentStrategy(new PayPalPayment());
        cart.checkout(50.0);
    }
}
```

**Key Concepts:**
- Strategy interface defines algorithm family
- Concrete strategies implement variations
- Runtime algorithm selection
- Easy to add new strategies

---

### Exercise 7: Adapter Pattern
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Adapter, interface compatibility, wrapper

**Pedagogic Objective:**
Understand Adapter pattern for interface compatibility.

**Complete Solution:**
```java
public interface Target {
    void request();
}

public class Adaptee {
    public void specificRequest() {
        System.out.println("Adaptee specific request");
    }
}

public class Adapter implements Target {
    private Adaptee adaptee;
    
    public Adapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }
    
    @Override
    public void request() {
        adaptee.specificRequest();
    }
}

public class AdapterPatternExample {
    public static void main(String[] args) {
        Adaptee adaptee = new Adaptee();
        Target target = new Adapter(adaptee);
        
        target.request();
    }
}
```

**Key Concepts:**
- Adapter wraps incompatible interface
- Converts interface to expected format
- Enables integration of incompatible classes
- Composition-based approach

---

### Exercise 8: Decorator Pattern
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Decorator, dynamic behavior, composition

**Pedagogic Objective:**
Understand Decorator pattern for dynamic behavior addition.

**Complete Solution:**
```java
public interface Component {
    void operation();
}

public class ConcreteComponent implements Component {
    @Override
    public void operation() {
        System.out.println("Basic operation");
    }
}

public abstract class Decorator implements Component {
    protected Component component;
    
    public Decorator(Component component) {
        this.component = component;
    }
    
    @Override
    public void operation() {
        component.operation();
    }
}

public class ConcreteDecorator extends Decorator {
    public ConcreteDecorator(Component component) {
        super(component);
    }
    
    @Override
    public void operation() {
        super.operation();
        System.out.println("Added behavior");
    }
}

public class DecoratorPatternExample {
    public static void main(String[] args) {
        Component component = new ConcreteComponent();
        component.operation();
        
        Component decorated = new ConcreteDecorator(component);
        decorated.operation();
    }
}
```

**Key Concepts:**
- Decorator wraps component
- Adds behavior dynamically
- More flexible than inheritance
- Composition over inheritance

---

### Exercise 9: Facade Pattern
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Facade, simplification, subsystem integration

**Pedagogic Objective:**
Understand Facade pattern for simplifying complex subsystems.

**Complete Solution:**
```java
public class SubsystemA {
    public void operationA() {
        System.out.println("Subsystem A operation");
    }
}

public class SubsystemB {
    public void operationB() {
        System.out.println("Subsystem B operation");
    }
}

public class Facade {
    private SubsystemA subsystemA;
    private SubsystemB subsystemB;
    
    public Facade() {
        this.subsystemA = new SubsystemA();
        this.subsystemB = new SubsystemB();
    }
    
    public void complexOperation() {
        subsystemA.operationA();
        subsystemB.operationB();
    }
}

public class FacadePatternExample {
    public static void main(String[] args) {
        Facade facade = new Facade();
        facade.complexOperation();
    }
}
```

**Key Concepts:**
- Facade provides unified interface
- Hides complex subsystem details
- Simplifies client code
- Reduces coupling

---

### Exercise 10: Template Method Pattern
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Template method, algorithm skeleton, inheritance

**Pedagogic Objective:**
Understand Template Method pattern for algorithm structure.

**Complete Solution:**
```java
public abstract class DataProcessor {
    public final void process() {
        readData();
        validateData();
        processData();
        saveData();
    }
    
    protected abstract void readData();
    protected abstract void validateData();
    protected abstract void processData();
    protected abstract void saveData();
}

public class CSVProcessor extends DataProcessor {
    @Override
    protected void readData() {
        System.out.println("Reading CSV data");
    }
    
    @Override
    protected void validateData() {
        System.out.println("Validating CSV data");
    }
    
    @Override
    protected void processData() {
        System.out.println("Processing CSV data");
    }
    
    @Override
    protected void saveData() {
        System.out.println("Saving CSV data");
    }
}

public class TemplateMethodPatternExample {
    public static void main(String[] args) {
        DataProcessor processor = new CSVProcessor();
        processor.process();
    }
}
```

**Key Concepts:**
- Template method defines algorithm skeleton
- Subclasses implement specific steps
- Enforces algorithm structure
- Promotes code reuse

---

## 🟡 Medium Exercises (11-20)

### Exercise 11: Abstract Factory Pattern
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Abstract factory, family of objects, factory hierarchy

**Complete Solution:**
```java
public interface Button {
    void render();
}

public interface Checkbox {
    void render();
}

public class WindowsButton implements Button {
    @Override
    public void render() {
        System.out.println("Rendering Windows Button");
    }
}

public class MacButton implements Button {
    @Override
    public void render() {
        System.out.println("Rendering Mac Button");
    }
}

public interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

public class WindowsFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new WindowsButton();
    }
    
    @Override
    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}

public class AbstractFactoryPatternExample {
    public static void main(String[] args) {
        GUIFactory factory = new WindowsFactory();
        Button button = factory.createButton();
        button.render();
    }
}
```

---

### Exercise 12: Prototype Pattern
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Prototype, cloning, object copying

**Complete Solution:**
```java
public class Prototype implements Cloneable {
    private String name;
    private int value;
    
    public Prototype(String name, int value) {
        this.name = name;
        this.value = value;
    }
    
    @Override
    public Prototype clone() {
        try {
            return (Prototype) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return "Prototype{" + "name='" + name + '\'' + ", value=" + value + '}';
    }
}

public class PrototypePatternExample {
    public static void main(String[] args) {
        Prototype original = new Prototype("Original", 10);
        Prototype clone = original.clone();
        
        System.out.println("Original: " + original);
        System.out.println("Clone: " + clone);
        System.out.println("Are they same? " + (original == clone));
    }
}
```

---

### Exercise 13: Chain of Responsibility
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Chain of responsibility, request handling, delegation

**Complete Solution:**
```java
public abstract class Handler {
    protected Handler nextHandler;
    
    public void setNextHandler(Handler nextHandler) {
        this.nextHandler = nextHandler;
    }
    
    public abstract void handle(Request request);
}

public class Request {
    private int level;
    
    public Request(int level) {
        this.level = level;
    }
    
    public int getLevel() {
        return level;
    }
}

public class ConcreteHandler extends Handler {
    private int level;
    
    public ConcreteHandler(int level) {
        this.level = level;
    }
    
    @Override
    public void handle(Request request) {
        if (request.getLevel() == level) {
            System.out.println("Handled at level " + level);
        } else if (nextHandler != null) {
            nextHandler.handle(request);
        }
    }
}

public class ChainOfResponsibilityExample {
    public static void main(String[] args) {
        Handler h1 = new ConcreteHandler(1);
        Handler h2 = new ConcreteHandler(2);
        Handler h3 = new ConcreteHandler(3);
        
        h1.setNextHandler(h2);
        h2.setNextHandler(h3);
        
        h1.handle(new Request(2));
    }
}
```

---

### Exercise 14: Command Pattern
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Command, encapsulation, undo/redo

**Complete Solution:**
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

public class CommandPatternExample {
    public static void main(String[] args) {
        Light light = new Light();
        Command command = new LightOnCommand(light);
        
        command.execute();
        command.undo();
    }
}
```

---

### Exercise 15: State Pattern
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** State, behavior change, state machine

**Complete Solution:**
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

public class StatePatternExample {
    public static void main(String[] args) {
        Context context = new Context(new ConcreteStateA());
        context.request();
        context.request();
    }
}
```

---

### Exercise 16: Visitor Pattern
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Visitor, double dispatch, element operations

**Complete Solution:**
```java
public interface Element {
    void accept(Visitor visitor);
}

public class ConcreteElementA implements Element {
    @Override
    public void accept(Visitor visitor) {
        visitor.visitElementA(this);
    }
}

public interface Visitor {
    void visitElementA(ConcreteElementA element);
    void visitElementB(ConcreteElementB element);
}

public class ConcreteVisitor implements Visitor {
    @Override
    public void visitElementA(ConcreteElementA element) {
        System.out.println("Visiting Element A");
    }
    
    @Override
    public void visitElementB(ConcreteElementB element) {
        System.out.println("Visiting Element B");
    }
}

public class VisitorPatternExample {
    public static void main(String[] args) {
        Element element = new ConcreteElementA();
        Visitor visitor = new ConcreteVisitor();
        element.accept(visitor);
    }
}
```

---

### Exercise 17: Proxy Pattern
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Proxy, lazy loading, access control

**Complete Solution:**
```java
public interface Subject {
    void request();
}

public class RealSubject implements Subject {
    @Override
    public void request() {
        System.out.println("RealSubject request");
    }
}

public class Proxy implements Subject {
    private RealSubject realSubject;
    
    @Override
    public void request() {
        if (realSubject == null) {
            realSubject = new RealSubject();
        }
        realSubject.request();
    }
}

public class ProxyPatternExample {
    public static void main(String[] args) {
        Subject subject = new Proxy();
        subject.request();
    }
}
```

---

### Exercise 18: Bridge Pattern
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Bridge, abstraction, implementation separation

**Complete Solution:**
```java
public interface Implementation {
    void operationImpl();
}

public class ConcreteImplementationA implements Implementation {
    @Override
    public void operationImpl() {
        System.out.println("Implementation A");
    }
}

public abstract class Abstraction {
    protected Implementation implementation;
    
    public Abstraction(Implementation implementation) {
        this.implementation = implementation;
    }
    
    public void operation() {
        implementation.operationImpl();
    }
}

public class RefinedAbstraction extends Abstraction {
    public RefinedAbstraction(Implementation implementation) {
        super(implementation);
    }
}

public class BridgePatternExample {
    public static void main(String[] args) {
        Implementation impl = new ConcreteImplementationA();
        Abstraction abstraction = new RefinedAbstraction(impl);
        abstraction.operation();
    }
}
```

---

### Exercise 19: Composite Pattern
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Composite, tree structure, recursive composition

**Complete Solution:**
```java
import java.util.*;

public interface Component {
    void operation();
}

public class Leaf implements Component {
    private String name;
    
    public Leaf(String name) {
        this.name = name;
    }
    
    @Override
    public void operation() {
        System.out.println("Leaf: " + name);
    }
}

public class Composite implements Component {
    private List<Component> children = new ArrayList<>();
    
    public void add(Component component) {
        children.add(component);
    }
    
    @Override
    public void operation() {
        System.out.println("Composite");
        for (Component child : children) {
            child.operation();
        }
    }
}

public class CompositePatternExample {
    public static void main(String[] args) {
        Composite root = new Composite();
        root.add(new Leaf("A"));
        root.add(new Leaf("B"));
        
        Composite branch = new Composite();
        branch.add(new Leaf("C"));
        root.add(branch);
        
        root.operation();
    }
}
```

---

### Exercise 20: Flyweight Pattern
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Flyweight, object sharing, memory optimization

**Complete Solution:**
```java
import java.util.*;

public class Flyweight {
    private String sharedState;
    
    public Flyweight(String sharedState) {
        this.sharedState = sharedState;
    }
    
    public void operation(String uniqueState) {
        System.out.println("Shared: " + sharedState + ", Unique: " + uniqueState);
    }
}

public class FlyweightFactory {
    private Map<String, Flyweight> flyweights = new HashMap<>();
    
    public Flyweight getFlyweight(String sharedState) {
        if (!flyweights.containsKey(sharedState)) {
            flyweights.put(sharedState, new Flyweight(sharedState));
        }
        return flyweights.get(sharedState);
    }
}

public class FlyweightPatternExample {
    public static void main(String[] args) {
        FlyweightFactory factory = new FlyweightFactory();
        
        Flyweight f1 = factory.getFlyweight("shared");
        Flyweight f2 = factory.getFlyweight("shared");
        
        System.out.println("Same object? " + (f1 == f2));
        f1.operation("unique1");
    }
}
```

---

## 🔴 Hard Exercises (21-26)

### Exercise 21: MVC Pattern
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** MVC, separation of concerns, architecture

**Complete Solution:**
```java
public class Model {
    private String data;
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
}

public class View {
    public void display(String data) {
        System.out.println("View: " + data);
    }
}

public class Controller {
    private Model model;
    private View view;
    
    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }
    
    public void updateModel(String data) {
        model.setData(data);
    }
    
    public void updateView() {
        view.display(model.getData());
    }
}

public class MVCPatternExample {
    public static void main(String[] args) {
        Model model = new Model();
        View view = new View();
        Controller controller = new Controller(model, view);
        
        controller.updateModel("Hello MVC");
        controller.updateView();
    }
}
```

---

### Exercise 22: Repository Pattern
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Repository, data access, abstraction

**Complete Solution:**
```java
import java.util.*;

public class User {
    private int id;
    private String name;
    
    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getId() { return id; }
    public String getName() { return name; }
}

public interface UserRepository {
    void save(User user);
    User findById(int id);
    List<User> findAll();
}

public class InMemoryUserRepository implements UserRepository {
    private Map<Integer, User> users = new HashMap<>();
    
    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }
    
    @Override
    public User findById(int id) {
        return users.get(id);
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}

public class RepositoryPatternExample {
    public static void main(String[] args) {
        UserRepository repo = new InMemoryUserRepository();
        repo.save(new User(1, "Alice"));
        repo.save(new User(2, "Bob"));
        
        System.out.println("User 1: " + repo.findById(1).getName());
        System.out.println("All users: " + repo.findAll().size());
    }
}
```

---

### Exercise 23: Dependency Injection Pattern
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Dependency injection, loose coupling, IoC

**Complete Solution:**
```java
public interface Logger {
    void log(String message);
}

public class ConsoleLogger implements Logger {
    @Override
    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
}

public class Service {
    private Logger logger;
    
    public Service(Logger logger) {
        this.logger = logger;
    }
    
    public void doWork() {
        logger.log("Doing work...");
    }
}

public class DependencyInjectionExample {
    public static void main(String[] args) {
        Logger logger = new ConsoleLogger();
        Service service = new Service(logger);
        service.doWork();
    }
}
```

---

### Exercise 24: Interceptor Pattern
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Interceptor, request/response handling, middleware

**Complete Solution:**
```java
import java.util.*;

public interface Interceptor {
    void intercept(Request request, Response response);
}

public class Request {
    public String data;
}

public class Response {
    public String data;
}

public class LoggingInterceptor implements Interceptor {
    @Override
    public void intercept(Request request, Response response) {
        System.out.println("Logging request: " + request.data);
    }
}

public class InterceptorChain {
    private List<Interceptor> interceptors = new ArrayList<>();
    
    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }
    
    public void execute(Request request, Response response) {
        for (Interceptor interceptor : interceptors) {
            interceptor.intercept(request, response);
        }
    }
}

public class InterceptorPatternExample {
    public static void main(String[] args) {
        InterceptorChain chain = new InterceptorChain();
        chain.addInterceptor(new LoggingInterceptor());
        
        Request request = new Request();
        request.data = "Test";
        Response response = new Response();
        
        chain.execute(request, response);
    }
}
```

---

### Exercise 25: Object Pool Pattern
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Object pool, resource management, reuse

**Complete Solution:**
```java
import java.util.*;

public class PooledObject {
    private boolean available = true;
    
    public void reset() {
        available = true;
    }
    
    public void use() {
        available = false;
    }
}

public class ObjectPool {
    private Queue<PooledObject> available = new LinkedList<>();
    private Set<PooledObject> inUse = new HashSet<>();
    
    public ObjectPool(int size) {
        for (int i = 0; i < size; i++) {
            available.add(new PooledObject());
        }
    }
    
    public PooledObject acquire() {
        PooledObject obj = available.poll();
        if (obj != null) {
            inUse.add(obj);
            obj.use();
        }
        return obj;
    }
    
    public void release(PooledObject obj) {
        if (inUse.remove(obj)) {
            obj.reset();
            available.add(obj);
        }
    }
}

public class ObjectPoolPatternExample {
    public static void main(String[] args) {
        ObjectPool pool = new ObjectPool(2);
        
        PooledObject obj1 = pool.acquire();
        PooledObject obj2 = pool.acquire();
        
        System.out.println("Objects acquired: " + (obj1 != null && obj2 != null));
        
        pool.release(obj1);
        PooledObject obj3 = pool.acquire();
        System.out.println("Reused object: " + (obj1 == obj3));
    }
}
```

---

### Exercise 26: Mediator Pattern
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Mediator, centralized control, communication

**Complete Solution:**
```java
import java.util.*;

public interface Mediator {
    void sendMessage(String message, Colleague colleague);
}

public abstract class Colleague {
    protected Mediator mediator;
    
    public Colleague(Mediator mediator) {
        this.mediator = mediator;
    }
    
    public abstract void receive(String message);
}

public class ConcreteColleague extends Colleague {
    private String name;
    
    public ConcreteColleague(Mediator mediator, String name) {
        super(mediator);
        this.name = name;
    }
    
    public void send(String message) {
        mediator.sendMessage(message, this);
    }
    
    @Override
    public void receive(String message) {
        System.out.println(name + " received: " + message);
    }
}

public class ConcreteMediator implements Mediator {
    private List<Colleague> colleagues = new ArrayList<>();
    
    public void addColleague(Colleague colleague) {
        colleagues.add(colleague);
    }
    
    @Override
    public void sendMessage(String message, Colleague sender) {
        for (Colleague colleague : colleagues) {
            if (colleague != sender) {
                colleague.receive(message);
            }
        }
    }
}

public class MediatorPatternExample {
    public static void main(String[] args) {
        ConcreteMediator mediator = new ConcreteMediator();
        
        Colleague c1 = new ConcreteColleague(mediator, "User1");
        Colleague c2 = new ConcreteColleague(mediator, "User2");
        
        mediator.addColleague(c1);
        mediator.addColleague(c2);
        
        ((ConcreteColleague) c1).send("Hello");
    }
}
```

---

## 🎯 Interview Exercises (27-30)

### Exercise 27: Design Pattern Selection
**Difficulty:** Interview  
**Time:** 35 minutes

**Complete Solution:**
```java
// Scenario: Building a configuration system
// Solution: Singleton + Builder

public class Configuration {
    private static Configuration instance;
    private String appName;
    private int port;
    private String database;
    
    private Configuration(Builder builder) {
        this.appName = builder.appName;
        this.port = builder.port;
        this.database = builder.database;
    }
    
    public static class Builder {
        private String appName;
        private int port;
        private String database;
        
        public Builder appName(String appName) {
            this.appName = appName;
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
        
        public Configuration build() {
            return new Configuration(this);
        }
    }
}
```

---

### Exercise 28: Pattern Combination
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
// Combining Factory + Observer + Strategy

public interface DataProcessor {
    void process(String data);
}

public class CSVProcessor implements DataProcessor {
    @Override
    public void process(String data) {
        System.out.println("Processing CSV: " + data);
    }
}

public class DataProcessorFactory {
    public static DataProcessor createProcessor(String type) {
        if ("csv".equalsIgnoreCase(type)) {
            return new CSVProcessor();
        }
        return null;
    }
}
```

---

### Exercise 29: Real-World Pattern Application
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
// E-commerce system using multiple patterns

public class Product {
    private String id;
    private String name;
    private double price;
    
    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}

public class ShoppingCart {
    private List<Product> items = new ArrayList<>();
    
    public void addItem(Product product) {
        items.add(product);
    }
    
    public double getTotal() {
        return items.stream().mapToDouble(p -> p.price).sum();
    }
}
```

---

### Exercise 30: Anti-Pattern Recognition
**Difficulty:** Interview  
**Time:** 35 minutes

**Complete Solution:**
```java
// Recognizing and fixing anti-patterns

// Anti-pattern: God Object
public class BadDesign {
    public void handleUser() {}
    public void handleOrder() {}
    public void handlePayment() {}
    public void handleShipping() {}
}

// Better design: Separation of Concerns
public class UserService {}
public class OrderService {}
public class PaymentService {}
public class ShippingService {}
```

---

## 📊 Solutions Summary

| Exercise | Title | Difficulty | Time | Topics |
|----------|-------|-----------|------|--------|
| 1 | Singleton | Easy | 15 min | Singleton |
| 2 | Thread-Safe | Easy | 20 min | Thread Safety |
| 3 | Factory | Easy | 20 min | Factory |
| 4 | Builder | Easy | 20 min | Builder |
| 5 | Observer | Easy | 20 min | Observer |
| 6 | Strategy | Easy | 20 min | Strategy |
| 7 | Adapter | Easy | 20 min | Adapter |
| 8 | Decorator | Easy | 20 min | Decorator |
| 9 | Facade | Easy | 20 min | Facade |
| 10 | Template Method | Easy | 20 min | Template |
| 11 | Abstract Factory | Medium | 25 min | Factory |
| 12 | Prototype | Medium | 25 min | Prototype |
| 13 | Chain | Medium | 25 min | Chain |
| 14 | Command | Medium | 25 min | Command |
| 15 | State | Medium | 25 min | State |
| 16 | Visitor | Medium | 25 min | Visitor |
| 17 | Proxy | Medium | 25 min | Proxy |
| 18 | Bridge | Medium | 25 min | Bridge |
| 19 | Composite | Medium | 25 min | Composite |
| 20 | Flyweight | Medium | 25 min | Flyweight |
| 21 | MVC | Hard | 40 min | Architecture |
| 22 | Repository | Hard | 40 min | Data Access |
| 23 | DI | Hard | 40 min | Injection |
| 24 | Interceptor | Hard | 40 min | Middleware |
| 25 | Object Pool | Hard | 40 min | Resource |
| 26 | Mediator | Hard | 40 min | Communication |
| 27 | Selection | Interview | 35 min | Analysis |
| 28 | Combination | Interview | 40 min | Integration |
| 29 | Real-World | Interview | 40 min | Application |
| 30 | Anti-Pattern | Interview | 35 min | Recognition |

---

<div align="center">

## Exercises: Design Patterns

**30 Comprehensive Exercises**

**Easy (10) | Medium (10) | Hard (6) | Interview (4)**

**Total Time: 10-12 hours**

---

[Back to Module →](./README.md)

[View Pedagogic Guide →](./PEDAGOGIC_GUIDE.md)

[Take Quizzes →](./QUIZZES.md)

</div>

(ending readme)