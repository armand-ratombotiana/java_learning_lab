# Design Patterns Module - PROJECTS.md

---

# Mini-Project: GOF Design Patterns Implementation

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: All 23 GOF Design Patterns, Creational, Structural, Behavioral

This project provides implementations of all 23 Gang of Four design patterns with practical examples.

---

## Project Structure

```
13-design-patterns/src/main/java/com/learning/
├── Main.java
├── creational/
│   ├── builder/
│   ├── factory/
│   ├── prototype/
│   └── singleton/
├── structural/
│   ├── adapter/
│   ├── bridge/
│   ├── composite/
│   ├── decorator/
│   ├── facade/
│   ├── flyweight/
│   └── proxy/
└── behavioral/
    ├── chain/
    ├── command/
    ├── interpreter/
    ├── iterator/
    ├── mediator/
    ├── memento/
    ├── observer/
    ├── state/
    ├── strategy/
    ├── template/
    └── visitor/
```

---

## Creational Patterns

### 1. Singleton Pattern

```java
// creational/singleton/Singleton.java
package com.learning.creational.singleton;

public final class Singleton {
    private static volatile Singleton instance;
    private final String data;
    
    private Singleton() {
        this.data = "Singleton Data";
    }
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
    
    public String getData() { return data; }
}
```

### 2. Factory Method Pattern

```java
// creational/factory/DocumentFactory.java
package com.learning.creational.factory;

public abstract class DocumentFactory {
    public abstract Document createDocument();
    
    public void openDocument() {
        Document doc = createDocument();
        doc.open();
    }
}

public class PDFDocumentFactory extends DocumentFactory {
    @Override
    public Document createDocument() {
        return new PDFDocument();
    }
}

public class WordDocumentFactory extends DocumentFactory {
    @Override
    public Document createDocument() {
        return new WordDocument();
    }
}

public interface Document {
    void open();
    void save();
}

public class PDFDocument implements Document {
    @Override
    public void open() { System.out.println("Opening PDF"); }
    @Override
    public void save() { System.out.println("Saving PDF"); }
}

public class WordDocument implements Document {
    @Override
    public void open() { System.out.println("Opening Word"); }
    @Override
    public void save() { System.out.println("Saving Word"); }
}
```

### 3. Abstract Factory Pattern

```java
// creational/factory/AbstractFactory.java
package com.learning.creational.factory;

public interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

public class WindowsFactory implements GUIFactory {
    @Override
    public Button createButton() { return new WindowsButton(); }
    @Override
    public Checkbox createCheckbox() { return new WindowsCheckbox(); }
}

public class MacFactory implements GUIFactory {
    @Override
    public Button createButton() { return new MacButton(); }
    @Override
    public Checkbox createCheckbox() { return new MacCheckbox(); }
}

public interface Button {
    void render();
}

public interface Checkbox {
    void render();
}

public class WindowsButton implements Button {
    @Override
    public void render() { System.out.println("Windows Button"); }
}

public class MacButton implements Button {
    @Override
    public void render() { System.out.println("Mac Button"); }
}

public class WindowsCheckbox implements Checkbox {
    @Override
    public void render() { System.out.println("Windows Checkbox"); }
}

public class MacCheckbox implements Checkbox {
    @Override
    public void render() { System.out.println("Mac Checkbox"); }
}
```

### 4. Builder Pattern

```java
// creational/builder/Builder.java
package com.learning.creational.builder;

public class User {
    private final String name;
    private final String email;
    private final int age;
    private final String address;
    
    private User(Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.age = builder.age;
        this.address = builder.address;
    }
    
    public static class Builder {
        private String name;
        private String email;
        private int age;
        private String address;
        
        public Builder name(String name) {
            this.name = name;
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
        
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        
        public User build() {
            return new User(this);
        }
    }
    
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getAge() { return age; }
    public String getAddress() { return address; }
}
```

### 5. Prototype Pattern

```java
// creational/prototype/Prototype.java
package com.learning.creational.prototype;

public abstract class DocumentPrototype implements Cloneable {
    private String id;
    private String content;
    
    public DocumentPrototype clone() {
        try {
            return (DocumentPrototype) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setId(String id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public String getId() { return id; }
    public String getContent() { return content; }
}

public class ReportPrototype extends DocumentPrototype {
    private String title;
    private String author;
    
    @Override
    public ReportPrototype clone() {
        ReportPrototype clone = (ReportPrototype) super.clone();
        clone.title = this.title;
        clone.author = this.author;
        return clone;
    }
    
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
}
```

---

## Structural Patterns

### 6. Adapter Pattern

```java
// structural/adapter/Adapter.java
package com.learning.structural.adapter;

public interface Target {
    String request();
}

public class Adapter implements Target {
    private final Adaptee adaptee;
    
    public Adapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }
    
    @Override
    public String request() {
        return "Adapter: " + adaptee.specificRequest();
    }
}

public class Adaptee {
    public String specificRequest() {
        return "Specific Request";
    }
}
```

### 7. Bridge Pattern

```java
// structural/bridge/Bridge.java
package com.learning.structural.bridge;

public abstract class Device {
    protected DeviceState state;
    
    public Device(DeviceState state) {
        this.state = state;
    }
    
    public abstract void powerOn();
    public abstract void powerOff();
}

public class TV extends Device {
    public TV(DeviceState state) {
        super(state);
    }
    
    @Override
    public void powerOn() { System.out.println("TV Power On"); }
    @Override
    public void powerOff() { System.out.println("TV Power Off"); }
}

public abstract class DeviceState {
    public abstract void handle(Device device);
}

public class OnState extends DeviceState {
    @Override
    public void handle(Device device) {
        System.out.println(device + " is on");
    }
}
```

### 8. Composite Pattern

```java
// structural/composite/Composite.java
package com.learning.structural.composite;

public interface FileSystemComponent {
    void display(String indent);
    int getSize();
}

public class File implements FileSystemComponent {
    private final String name;
    private final int size;
    
    public File(String name, int size) {
        this.name = name;
        this.size = size;
    }
    
    @Override
    public void display(String indent) {
        System.out.println(indent + "- " + name + " (" + size + "KB)");
    }
    
    @Override
    public int getSize() { return size; }
}

public class Folder implements FileSystemComponent {
    private final String name;
    private final java.util.List<FileSystemComponent> children = new java.util.ArrayList<>();
    
    public Folder(String name) {
        this.name = name;
    }
    
    public void add(FileSystemComponent component) {
        children.add(component);
    }
    
    @Override
    public void display(String indent) {
        System.out.println(indent + "+ " + name);
        for (FileSystemComponent child : children) {
            child.display(indent + "  ");
        }
    }
    
    @Override
    public int getSize() {
        return children.stream().mapToInt(FileSystemComponent::getSize).sum();
    }
}
```

### 9. Decorator Pattern

```java
// structural/decorator/Decorator.java
package com.learning.structural.decorator;

public interface Coffee {
    String getDescription();
    double getCost();
}

public class SimpleCoffee implements Coffee {
    @Override
    public String getDescription() { return "Coffee"; }
    @Override
    public double getCost() { return 2.0; }
}

public class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
    
    @Override
    public String getDescription() { return coffee.getDescription(); }
    @Override
    public double getCost() { return coffee.getCost(); }
}

public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) { super(coffee); }
    
    @Override
    public String getDescription() { return coffee.getDescription() + ", Milk"; }
    @Override
    public double getCost() { return coffee.getCost() + 0.5; }
}

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) { super(coffee); }
    
    @Override
    public String getDescription() { return coffee.getDescription() + ", Sugar"; }
    @Override
    public double getCost() { return coffee.getCost() + 0.2; }
}
```

### 10. Facade Pattern

```java
// structural/facade/Facade.java
package com.learning.structural.facade;

public class OrderFacade {
    private final Inventory inventory;
    private final Payment payment;
    private final Shipping shipping;
    
    public OrderFacade() {
        this.inventory = new Inventory();
        this.payment = new Payment();
        this.shipping = new Shipping();
    }
    
    public void placeOrder(String productId, String paymentInfo) {
        System.out.println("Placing order...");
        
        if (!inventory.checkAvailability(productId)) {
            System.out.println("Product not available");
            return;
        }
        
        if (!payment.processPayment(paymentInfo)) {
            System.out.println("Payment failed");
            return;
        }
        
        shipping.ship(productId);
        System.out.println("Order placed successfully");
    }
}

class Inventory {
    public boolean checkAvailability(String productId) {
        System.out.println("Checking inventory for " + productId);
        return true;
    }
}

class Payment {
    public boolean processPayment(String paymentInfo) {
        System.out.println("Processing payment: " + paymentInfo);
        return true;
    }
}

class Shipping {
    public void ship(String productId) {
        System.out.println("Shipping product: " + productId);
    }
}
```

### 11. Flyweight Pattern

```java
// structural/flyweight/Flyweight.java
package com.learning.structural.flyweight;

public class TreeType {
    private final String name;
    private final String color;
    private final String texture;
    
    public TreeType(String name, String color, String texture) {
        this.name = name;
        this.color = color;
        this.texture = texture;
    }
    
    public void draw(int x, int y) {
        System.out.println("Drawing " + name + " at (" + x + "," + y + ")");
    }
}

public class TreeFactory {
    private static final java.util.Map<String, TreeType> treeTypes = new java.util.HashMap<>();
    
    public static TreeType getTreeType(String name, String color, String texture) {
        String key = name + "_" + color + "_" + texture;
        
        return treeTypes.computeIfAbsent(key, k -> 
            new TreeType(name, color, texture));
    }
}

public class Tree {
    private int x, y;
    private TreeType type;
    
    public Tree(int x, int y, TreeType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    
    public void draw() {
        type.draw(x, y);
    }
}
```

### 12. Proxy Pattern

```java
// structural/proxy/Proxy.java
package com.learng.structural.proxy;

public interface Image {
    void display();
}

public class RealImage implements Image {
    private final String filename;
    
    public RealImage(String filename) {
        this.filename = filename;
        loadFromDisk();
    }
    
    private void loadFromDisk() {
        System.out.println("Loading: " + filename);
    }
    
    @Override
    public void display() {
        System.out.println("Displaying: " + filename);
    }
}

public class ImageProxy implements Image {
    private RealImage realImage;
    private final String filename;
    
    public ImageProxy(String filename) {
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

---

## Behavioral Patterns

### 13. Chain of Responsibility

```java
// behavioral/chain/Chain.java
package com.learning.behavioral.chain;

public abstract class Handler {
    private Handler next;
    
    public Handler setNext(Handler next) {
        this.next = next;
        return next;
    }
    
    public void handle(String request) {
        if (process(request)) {
            return;
        }
        if (next != null) {
            next.handle(request);
        }
    }
    
    protected abstract boolean process(String request);
}

public class AuthenticationHandler extends Handler {
    @Override
    protected boolean process(String request) {
        if (request.startsWith("AUTH:")) {
            System.out.println("Authenticated: " + request);
            return true;
        }
        return false;
    }
}

public class LoggingHandler extends Handler {
    @Override
    protected boolean process(String request) {
        if (request.startsWith("LOG:")) {
            System.out.println("Logged: " + request);
            return true;
        }
        return false;
    }
}
```

### 14. Command Pattern

```java
// behavioral/command/Command.java
package com.learning.behavioral.command;

public interface Command {
    void execute();
    void undo();
}

public class Light {
    public void on() { System.out.println("Light ON"); }
    public void off() { System.out.println("Light OFF"); }
}

public class LightOnCommand implements Command {
    private final Light light;
    
    public LightOnCommand(Light light) {
        this.light = light;
    }
    
    @Override
    public void execute() { light.on(); }
    @Override
    public void undo() { light.off(); }
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
```

### 15. Interpreter Pattern

```java
// behavioral/interpreter/Interpreter.java
package com.learning.behavioral.interpreter;

public interface Expression {
    boolean interpret(String context);
}

public class TerminalExpression implements Expression {
    private final String data;
    
    public TerminalExpression(String data) {
        this.data = data;
    }
    
    @Override
    public boolean interpret(String context) {
        return context.contains(data);
    }
}

public class AndExpression implements Expression {
    private final Expression expr1, expr2;
    
    public AndExpression(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }
    
    @Override
    public boolean interpret(String context) {
        return expr1.interpret(context) && expr2.interpret(context);
    }
}

public class OrExpression implements Expression {
    private final Expression expr1, expr2;
    
    public OrExpression(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }
    
    @Override
    public boolean interpret(String context) {
        return expr1.interpret(context) || expr2.interpret(context);
    }
}
```

### 16. Iterator Pattern

```java
// behavioral/iterator/Iterator.java
package com.learning.behavioral.iterator;

public interface Iterator<T> {
    boolean hasNext();
    T next();
}

public class Collection<T> {
    private final java.util.List<T> items = new java.util.ArrayList<>();
    
    public void add(T item) { items.add(item); }
    
    public Iterator<T> iterator() {
        return new CollectionIterator();
    }
    
    private class CollectionIterator implements Iterator<T> {
        private int index = 0;
        
        @Override
        public boolean hasNext() {
            return index < items.size();
        }
        
        @Override
        public T next() {
            return items.get(index++);
        }
    }
}
```

### 17. Mediator Pattern

```java
// behavioral/mediator/Mediator.java
package com.learning.behavioral.mediator;

public interface Mediator {
    void notify(Component sender, String event);
}

public class ChatMediator implements Mediator {
    private final java.util.List<Component> components = new java.util.ArrayList<>();
    
    public void addComponent(Component component) {
        components.add(component);
    }
    
    @Override
    public void notify(Component sender, String event) {
        System.out.println(sender + ": " + event);
        for (Component c : components) {
            if (c != sender) {
                c.receive(event);
            }
        }
    }
}

public abstract class Component {
    protected Mediator mediator;
    
    public Component(Mediator mediator) {
        this.mediator = mediator;
    }
    
    public abstract void send(String event);
    public abstract void receive(String event);
}

public class User extends Component {
    private final String name;
    
    public User(Mediator mediator, String name) {
        super(mediator);
        this.name = name;
    }
    
    @Override
    public void send(String event) {
        mediator.notify(this, event);
    }
    
    @Override
    public void receive(String event) {
        System.out.println(name + " received: " + event);
    }
}
```

### 18. Memento Pattern

```java
// behavioral/memento/Memento.java
package com.learning.behavioral.memento;

public class Editor {
    private String content;
    
    public Editor() { this.content = ""; }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
    
    public Memento save() {
        return new Memento(content);
    }
    
    public void restore(Memento memento) {
        content = memento.getState();
    }
    
    public static class Memento {
        private final String state;
        
        public Memento(String state) {
            this.state = state;
        }
        
        public String getState() {
            return state;
        }
    }
}

public class History {
    private final java.util.Stack<Editor.Memento> history = new java.util.Stack<>();
    
    public void push(Editor.Memento memento) {
        history.push(memento);
    }
    
    public Editor.Memento pop() {
        return history.pop();
    }
}
```

### 19. Observer Pattern

```java
// behavioral/observer/Observer.java
package com.learning.behavioral.observer;

public interface Observer {
    void update(String event);
}

public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers();
}

public class NewsAgency implements Subject {
    private final java.util.List<Observer> observers = new java.util.ArrayList<>();
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
```

### 20. State Pattern

```java
// behavioral/state/State.java
package com.learning.behavioral.state;

public interface State {
    void handle(Context context);
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
```

### 21. Strategy Pattern

```java
// behavioral/strategy/Strategy.java
package com.learning.behavioral.strategy;

public interface Strategy {
    int execute(int a, int b);
}

public class Context {
    private Strategy strategy;
    
    public Context(Strategy strategy) {
        this.strategy = strategy;
    }
    
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
    
    public int executeStrategy(int a, int b) {
        return strategy.execute(a, b);
    }
}

public class AddStrategy implements Strategy {
    @Override
    public int execute(int a, int b) { return a + b; }
}

public class MultiplyStrategy implements Strategy {
    @Override
    public int execute(int a, int b) { return a * b; }
}
```

### 22. Template Method Pattern

```java
// behavioral/template/Template.java
package com.learning.behavioral.template;

public abstract class DataMiner {
    public void mine() {
        openFile();
        extractData();
        parseData();
        analyzeData();
        sendReport();
    }
    
    protected abstract void openFile();
    protected abstract void extractData();
    protected abstract void parseData();
    
    private void analyzeData() {
        System.out.println("Analyzing data...");
    }
    
    private void sendReport() {
        System.out.println("Sending report...");
    }
}

public class PDFDataMiner extends DataMiner {
    @Override
    protected void openFile() { System.out.println("Opening PDF"); }
    @Override
    protected void extractData() { System.out.println("Extracting PDF"); }
    @Override
    protected void parseData() { System.out.println("Parsing PDF"); }
}
```

### 23. Visitor Pattern

```java
// behavioral/visitor/Visitor.java
package com.learning.behavioral.visitor;

public interface Visitor {
    void visitElementA(ElementA element);
    void visitElementB(ElementB element);
}

public interface Element {
    void accept(Visitor visitor);
}

public class ElementA implements Element {
    @Override
    public void accept(Visitor visitor) {
        visitor.visitElementA(this);
    }
    
    public void operationA() { System.out.println("Operation A"); }
}

public class ElementB implements Element {
    @Override
    public void accept(Visitor visitor) {
        visitor.visitElementB(this);
    }
    
    public void operationB() { System.out.println("Operation B"); }
}

public class ConcreteVisitor implements Visitor {
    @Override
    public void visitElementA(ElementA element) {
        element.operationA();
    }
    
    @Override
    public void visitElementB(ElementB element) {
        element.operationB();
    }
}
```

---

## Main Application

```java
// Main.java
package com.learning;

import com.learning.creational.*;
import com.learning.structural.*;
import com.learning.behavioral.*;

public class Main {
    public static void main(String[] args) {
        testCreational();
        testStructural();
        testBehavioral();
    }
    
    private static void testCreational() {
        System.out.println("=== Creational Patterns ===");
        
        Singleton instance = Singleton.getInstance();
        System.out.println("Singleton: " + instance.getData());
        
        User user = new User.Builder()
            .name("John")
            .email("john@email.com")
            .age(30)
            .build();
        System.out.println("User: " + user.getName());
    }
    
    private static void testStructural() {
        System.out.println("\n=== Structural Patterns ===");
        
        Coffee coffee = new SugarDecorator(
            new MilkDecorator(new SimpleCoffee()));
        System.out.println("Coffee: " + coffee.getDescription() + 
            " - $" + coffee.getCost());
    }
    
    private static void testBehavioral() {
        System.out.println("\n=== Behavioral Patterns ===");
        
        Context context = new Context(new AddStrategy());
        System.out.println("Strategy: " + 
            context.executeStrategy(5, 3));
    }
}
```

---

## Build Instructions

```bash
cd 13-design-patterns
javac -d target/classes -sourcepath src/main/java src/main/java/com/learning/**/*.java
java -cp target/classes com.learning.Main
```

---

# Real-World Project: Complete E-Commerce System with All GOF Patterns

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: All 23 GOF Patterns, Enterprise Application Patterns, Full System Architecture

This project demonstrates all 23 GOF design patterns in a complete e-commerce system architecture.

---

## Project Structure

```
13-design-patterns/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── ecommerce/
│   │   ├── ECommerceSystem.java
│   │   ├── OrderProcessor.java
│   │   └── PaymentProcessor.java
│   ├── model/
│   │   ├── Product.java
│   │   ├── Customer.java
│   │   └── Order.java
│   ├── creational/
│   │   ├── factory/
│   │   │   ├── ProductFactory.java
│   │   │   └── OrderFactory.java
│   │   ├── singleton/
│   │   │   ├── ECommerceRegistry.java
│   │   ├── builder/
│   │   │   ├── OrderBuilder.java
│   │   └── prototype/
│   │       └── ProductPrototype.java
│   ├── structural/
│   │   ├── adapter/
│   │   │   ├── PaymentGatewayAdapter.java
│   │   ├── facade/
│   │   │   ├── OrderFacade.java
│   │   ├── decorator/
│   │   │   ├── DiscountDecorator.java
│   │   ├── proxy/
│   │   │   └── ProductProxy.java
│   │   └── composite/
│   │       └── CategoryHierarchy.java
│   ├── behavioral/
│   │   ├── strategy/
│   │   │   ├── PaymentStrategy.java
│   │   ├── observer/
│   │   │   ├��─ OrderObserver.java
│   │   ├── command/
│   │   │   ├── OrderCommand.java
│   │   └── state/
│   │       ├── OrderState.java
│   └── service/
│       ├── InventoryService.java
│       └── NotificationService.java
└── src/main/resources/
```

---

## POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>design-patterns</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
</project>
```

---

## E-Commerce System Implementation

### Order Builder (Creational)

```java
// creational/builder/OrderBuilder.java
package com.learning.creational.builder;

import java.util.ArrayList;
import java.util.List;

public class OrderBuilder {
    private String orderId;
    private String customerId;
    private List<OrderItem> items = new ArrayList<>();
    private PaymentInfo paymentInfo;
    private ShippingAddress shippingAddress;
    private Discount discount;
    
    public OrderBuilder orderId(String orderId) {
        this.orderId = orderId;
        return this;
    }
    
    public OrderBuilder customerId(String customerId) {
        this.customerId = customerId;
        return this;
    }
    
    public OrderBuilder addItem(String productId, int quantity, double price) {
        items.add(new OrderItem(productId, quantity, price));
        return this;
    }
    
    public OrderBuilder paymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
        return this;
    }
    
    public OrderBuilder shippingAddress(ShippingAddress address) {
        this.shippingAddress = address;
        return this;
    }
    
    public OrderBuilder discount(Discount discount) {
        this.discount = discount;
        return this;
    }
    
    public Order build() {
        return new Order(orderId, customerId, items, paymentInfo, 
            shippingAddress, discount);
    }
    
    public static class OrderItem {
        private final String productId;
        private final int quantity;
        private final double price;
        
        public OrderItem(String productId, int quantity, double price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }
        
        public double getTotal() { return quantity * price; }
    }
}
```

### Product Factory (Factory Method)

```java
// creational/factory/ProductFactory.java
package com.learning.creational.factory;

import com.learning.model.Product;

public abstract class ProductFactory {
    public abstract Product createProduct();
    
    public static class PhysicalProductFactory extends ProductFactory {
        @Override
        public Product createProduct() {
            return new PhysicalProduct();
        }
    }
    
    public static class DigitalProductFactory extends ProductFactory {
        @Override
        public Product createProduct() {
            return new DigitalProduct();
        }
    }
    
    public static class ServiceProductFactory extends ProductFactory {
        @Override
        public Product createProduct() {
            return new ServiceProduct();
        }
    }
}
```

### Order State (State Pattern)

```java
// behavioral/state/OrderState.java
package com.learning.behavioral.state;

public interface OrderState {
    void process(OrderContext context);
    void cancel(OrderContext context);
}

public class OrderContext {
    private OrderState state;
    
    public OrderContext(OrderState state) {
        this.state = state;
    }
    
    public void setState(OrderState state) {
        this.state = state;
    }
    
    public void process() {
        state.process(this);
    }
    
    public void cancel() {
        state.cancel(this);
    }
}

public class NewOrderState implements OrderState {
    @Override
    public void process(OrderContext context) {
        System.out.println("Processing new order");
        context.setState(new ProcessingState());
    }
    
    @Override
    public void cancel(OrderContext context) {
        System.out.println("Cancelling new order");
        context.setState(new CancelledState());
    }
}

public class ProcessingState implements OrderState {
    @Override
    public void process(OrderContext context) {
        System.out.println("Order is being processed");
    }
    
    @Override
    public void cancel(OrderContext context) {
        System.out.println("Cannot cancel processing order");
    }
}

public class CancelledState implements OrderState {
    @Override
    public void process(OrderContext context) {
        System.out.println("Cannot process cancelled order");
    }
    
    @Override
    public void cancel(OrderContext context) {
        System.out.println("Order already cancelled");
    }
}
```

### Payment Strategy (Strategy Pattern)

```java
// behavioral/strategy/PaymentStrategy.java
package com.learning.behavioral.strategy;

public interface PaymentStrategy {
    boolean pay(double amount);
    String getType();
}

public class CreditCardPayment implements PaymentStrategy {
    @Override
    public boolean pay(double amount) {
        System.out.println("Paid $" + amount + " via Credit Card");
        return true;
    }
    
    @Override
    public String getType() { return "CREDIT_CARD"; }
}

public class PayPalPayment implements PaymentStrategy {
    @Override
    public boolean pay(double amount) {
        System.out.println("Paid $" + amount + " via PayPal");
        return true;
    }
    
    @Override
    public String getType() { return "PAYPAL"; }
}

public class CryptoPayment implements PaymentStrategy {
    @Override
    public boolean pay(double amount) {
        System.out.println("Paid $" + amount + " via Crypto");
        return true;
    }
    
    @Override
    public String getType() { return "CRYPTO"; }
}
```

### Order Observer (Observer Pattern)

```java
// behavioral/observer/OrderObserver.java
package com.learning.behavioral.observer;

import java.util.ArrayList;
import java.util.List;

public interface OrderObserver {
    void onOrderPlaced(Order order);
    void onOrderShipped(Order order);
    void onOrderDelivered(Order order);
}

public class OrderSubject {
    private final List<OrderObserver> observers = new ArrayList<>();
    
    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyOrderPlaced(Order order) {
        observers.forEach(o -> o.onOrderPlaced(order));
    }
    
    public void notifyOrderShipped(Order order) {
        observers.forEach(o -> o.onOrderShipped(order));
    }
    
    public void notifyOrderDelivered(Order order) {
        observers.forEach(o -> o.onOrderDelivered(order));
    }
}

public class EmailNotificationObserver implements OrderObserver {
    @Override
    public void onOrderPlaced(Order order) {
        System.out.println("Email: Order " + order.getId() + " placed");
    }
    
    @Override
    public void onOrderShipped(Order order) {
        System.out.println("Email: Order " + order.getId() + " shipped");
    }
    
    @Override
    public void onOrderDelivered(Order order) {
        System.out.println("Email: Order " + order.getId() + " delivered");
    }
}

public class SMSNotificationObserver implements OrderObserver {
    @Override
    public void onOrderPlaced(Order order) {
        System.out.println("SMS: Order " + order.getId() + " placed");
    }
    
    @Override
    public void onOrderShipped(Order order) {
        System.out.println("SMS: Order " + order.getId() + " shipped");
    }
    
    @Override
    public void onOrderDelivered(Order order) {
        System.out.println("SMS: Order " + order.getId() + " delivered");
    }
}
```

### Order Command (Command Pattern)

```java
// behavioral/command/OrderCommand.java
package com.learning.behavioral.command;

public interface OrderCommand {
    void execute();
    void undo();
}

public class PlaceOrderCommand implements OrderCommand {
    private final Order order;
    private final OrderProcessor processor;
    
    public PlaceOrderCommand(Order order, OrderProcessor processor) {
        this.order = order;
        this.processor = processor;
    }
    
    @Override
    public void execute() {
        processor.placeOrder(order);
    }
    
    @Override
    public void undo() {
        processor.cancelOrder(order.getId());
    }
}

public class CancelOrderCommand implements OrderCommand {
    private final String orderId;
    private final OrderProcessor processor;
    
    public CancelOrderCommand(String orderId, OrderProcessor processor) {
        this.orderId = orderId;
        this.processor = processor;
    }
    
    @Override
    public void execute() {
        processor.cancelOrder(orderId);
    }
    
    @Override
    public void undo() {
        processor.restoreOrder(orderId);
    }
}
```

### Order Facade (Facade Pattern)

```java
// structural/facade/OrderFacade.java
package com.learning.structural.facade;

public class OrderFacade {
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final ShippingService shippingService;
    private final NotificationService notificationService;
    
    public OrderFacade() {
        this.inventoryService = new InventoryService();
        this.paymentService = new PaymentService();
        this.shippingService = new ShippingService();
        this.notificationService = new NotificationService();
    }
    
    public boolean placeOrder(Order order) {
        if (!inventoryService.checkAvailability(order)) {
            return false;
        }
        
        if (!paymentService.processPayment(order)) {
            return false;
        }
        
        shippingService.scheduleShipping(order);
        notificationService.sendConfirmation(order);
        
        return true;
    }
}

class InventoryService {
    public boolean checkAvailability(Order order) {
        return true;
    }
}

class PaymentService {
    public boolean processPayment(Order order) {
        return true;
    }
}

class ShippingService {
    public void scheduleShipping(Order order) {
        System.out.println("Shipping scheduled for order: " + order.getId());
    }
}

class NotificationService {
    public void sendConfirmation(Order order) {
        System.out.println("Confirmation sent for order: " + order.getId());
    }
}
```

### Main Application

```java
// Main.java
package com.learning;

import com.learning.creational.builder.*;
import com.learning.behavioral.state.*;
import com.learning.behavioral.strategy.*;
import com.learning.behavioral.observer.*;
import com.learning.model.*;

public class Main {
    public static void main(String[] args) {
        testPatterns();
    }
    
    private static void testPatterns() {
        System.out.println("=== E-Commerce Design Patterns ===\n");
        
        testCreational();
        testBehavioral();
        testStructural();
    }
    
    private static void testCreational() {
        System.out.println("--- Creational Patterns ---");
        
        Order order = new OrderBuilder()
            .orderId("ORD-001")
            .customerId("CUST-001")
            .addItem("PROD-001", 2, 29.99)
            .addItem("PROD-002", 1, 49.99)
            .build();
        
        System.out.println("Order: " + order.getId());
    }
    
    private static void testBehavioral() {
        System.out.println("\n--- Behavioral Patterns ---");
        
        OrderContext context = new OrderContext(new NewOrderState());
        context.process();
        context.process();
        
        PaymentStrategy payment = new CreditCardPayment();
        payment.pay(100.00);
        
        OrderSubject subject = new OrderSubject();
        subject.addObserver(new EmailNotificationObserver());
        subject.notifyOrderPlaced(new Order("test", "customer", null, null, null, null));
    }
    
    private static void testStructural() {
        System.out.println("\n--- Structural Patterns ---");
        
        Order order = new OrderBuilder().orderId("ORD-002").customerId("CUST-001").build();
        
        OrderFacade facade = new OrderFacade();
        facade.placeOrder(order);
    }
}
```

---

## Build Instructions

```bash
cd 13-design-patterns
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

This project demonstrates all 23 GOF design patterns in a practical e-commerce system architecture.