# Quick Reference: Design Patterns

<div align="center">

![Module](https://img.shields.io/badge/Module-13-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Design%20Patterns-green?style=for-the-badge)

**Quick lookup guide for GOF design patterns**

</div>

---

## 📋 Creational Patterns

### Singleton
```java
public class Singleton {
    private static volatile Singleton instance;
    
    private Singleton() {}
    
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
}
```

### Factory Method
```java
public interface Product {
    void operation();
}

public interface Creator {
    Product factoryMethod();
}

public class ConcreteCreator implements Creator {
    public Product factoryMethod() {
        return new ConcreteProduct();
    }
}
```

### Abstract Factory
```java
public interface AbstractFactory {
    ProductA createProductA();
    ProductB createProductB();
}

public class ConcreteFactory implements AbstractFactory {
    public ProductA createProductA() { return new ConcreteProductA(); }
    public ProductB createProductB() { return new ConcreteProductB(); }
}
```

### Builder
```java
public class User {
    private String name;
    private int age;
    
    private User(Builder b) {
        this.name = b.name;
        this.age = b.age;
    }
    
    public static class Builder {
        private String name;
        private int age;
        
        public Builder name(String name) { this.name = name; return this; }
        public Builder age(int age) { this.age = age; return this; }
        public User build() { return new User(this); }
    }
}
// Usage: new User.Builder().name("John").age(30).build()
```

### Prototype
```java
public interface Prototype {
    Prototype clone();
}

public class ConcretePrototype implements Prototype {
    public Prototype clone() {
        return new ConcretePrototype(this);
    }
}
```

---

## 📋 Structural Patterns

### Adapter
```java
public class Adapter implements Target {
    private Adaptee adaptee;
    
    public Adapter(Adaptee adaptee) { this.adaptee = adaptee; }
    
    public void request() {
        adaptee.specificRequest();
    }
}
```

### Bridge
```java
// Abstraction
public abstract class Abstraction {
    protected Implementor impl;
    public void operation() { impl.implOperation(); }
}

// Implementor
public interface Implementor {
    void implOperation();
}
```

### Composite
```java
public interface Component {
    void operation();
}

public class Composite implements Component {
    private List<Component> children = new ArrayList<>();
    public void add(Component c) { children.add(c); }
    public void operation() {
        children.forEach(Component::operation);
    }
}
```

### Decorator
```java
public class Decorator implements Component {
    protected Component wrapped;
    public Decorator(Component c) { this.wrapped = c; }
    public void operation() {
        wrapped.operation();
    }
}
```

### Facade
```java
public class Facade {
    private SubSystemA a;
    private SubSystemB b;
    
    public void operation() {
        a.operation();
        b.operation();
    }
}
```

### Proxy
```java
public class Proxy implements Subject {
    private RealSubject real;
    public void request() {
        if (checkAccess()) {
            real.request();
            logAccess();
        }
    }
}
```

---

## 📋 Behavioral Patterns

### Observer
```java
public interface Observer {
    void update(Object event);
}

public class Subject {
    private List<Observer> observers = new ArrayList<>();
    public void addObserver(Observer o) { observers.add(o); }
    public void notifyObservers(Object event) {
        observers.forEach(o -> o.update(event));
    }
}
```

### Strategy
```java
public interface Strategy {
    Object execute(Object input);
}

public class Context {
    private Strategy strategy;
    public void setStrategy(Strategy s) { this.strategy = s; }
    public Object execute(Object input) {
        return strategy.execute(input);
    }
}
```

### Command
```java
public interface Command {
    void execute();
    void undo();
}

public class ConcreteCommand implements Command {
    private Receiver receiver;
    public void execute() { receiver.action(); }
    public void undo() { receiver.undoAction(); }
}
```

### State
```java
public class Context {
    private State state;
    public void setState(State s) { this.state = s; }
    public void request() { state.handle(this); }
}

public interface State {
    void handle(Context c);
}
```

### Template Method
```java
public abstract class AbstractClass {
    public void templateMethod() {
        step1();
        step2();
    }
    protected abstract void step1();
    protected abstract void step2();
}
```

### Chain of Responsibility
```java
public abstract class Handler {
    private Handler next;
    public Handler setNext(Handler h) { this.next = h; return h; }
    public void handle(Request req) {
        if (!process(req) && next != null) {
            next.handle(req);
        }
    }
}
```

### Iterator
```java
public interface Iterator<T> {
    boolean hasNext();
    T next();
}

public class ConcreteIterator implements Iterator<T> {
    public boolean hasNext() { /* ... */ }
    public T next() { /* ... */ }
}
```

---

## ✅ When to Use

| Pattern | Use Case |
|---------|----------|
| Singleton | One instance needed |
| Factory | Object creation varies |
| Builder | Complex object construction |
| Adapter | Incompatible interfaces |
| Decorator | Add behavior dynamically |
| Observer | One-to-many updates |
| Strategy | Interchangeable algorithms |
| Command | Encapsulate requests |

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>