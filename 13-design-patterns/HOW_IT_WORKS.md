# How Design Patterns Work

## Creational Patterns

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
interface Product { void operation(); }
class ConcreteProduct implements Product { ... }

abstract class Creator {
    public abstract Product createProduct();
    
    public void doSomething() {
        Product p = createProduct();
        p.operation();
    }
}

class ConcreteCreator extends Creator {
    public Product createProduct() {
        return new ConcreteProduct();
    }
}
```

### Builder
```java
public class User {
    private final String name;
    private final int age;
    private final String address;
    
    private User(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.address = builder.address;
    }
    
    public static class Builder {
        private String name;
        private int age;
        private String address;
        
        public Builder name(String name) { this.name = name; return this; }
        public Builder age(int age) { this.age = age; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public User build() { return new User(this); }
    }
}

// Usage
User user = new User.Builder()
    .name("John")
    .age(30)
    .build();
```

## Structural Patterns

### Adapter
```java
interface Target {
    void request();
}

class Adaptee {
    void specificRequest() { ... }
}

class Adapter implements Target {
    private final Adaptee adaptee;
    
    public Adapter(Adaptee adaptee) { this.adaptee = adaptee; }
    
    public void request() {
        adaptee.specificRequest();
    }
}
```

### Decorator
```java
interface DataSource {
    void write(String data);
    String read();
}

class FileDataSource implements DataSource { ... }

class DataSourceDecorator implements DataSource {
    protected DataSource wrapped;
    
    public DataSourceDecorator(DataSource source) { this.wrapped = source; }
    
    public void write(String data) { wrapped.write(data); }
    public String read() { return wrapped.read(); }
}

class EncryptionDecorator extends DataSourceDecorator {
    public void write(String data) { 
        wrapped.write(encrypt(data)); 
    }
}
```

## Behavioral Patterns

### Observer
```java
interface Observer {
    void update(String message);
}

interface Subject {
    void attach(Observer o);
    void detach(Observer o);
    void notifyObservers();
}

class ConcreteSubject implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private String state;
    
    public void attach(Observer o) { observers.add(o); }
    public void notifyObservers() {
        observers.forEach(o -> o.update(state));
    }
}
```

### Strategy
```java
interface SortStrategy<T> {
    void sort(List<T> list);
}

class QuickSort<T> implements SortStrategy<T> { ... }
class MergeSort<T> implements SortStrategy<T> { ... }

class Sorter<T> {
    private SortStrategy<T> strategy;
    
    public void setStrategy(SortStrategy<T> strategy) {
        this.strategy = strategy;
    }
    
    public void sort(List<T> list) {
        strategy.sort(list);
    }
}
```