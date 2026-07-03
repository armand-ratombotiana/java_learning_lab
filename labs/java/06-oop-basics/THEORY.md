# OOP Basics — Theoretical Foundation

## Object-Oriented Programming Principles

### Encapsulation

Bundling data (fields) and behavior (methods) together, and controlling access through access modifiers. Encapsulation hides internal state, requiring all interaction through public methods.

```java
public class BankAccount {
    private double balance;  // Hidden state

    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }

    public double getBalance() {
        return balance;  // Read-only access
    }
}
```

### Classes and Objects

A **class** is a blueprint. An **object** is an instance created from that blueprint.

```java
public class Car {
    // Fields (state)
    String model;
    int year;

    // Constructor
    public Car(String model, int year) {
        this.model = model;
        this.year = year;
    }

    // Method (behavior)
    public void start() {
        System.out.println(model + " is starting...");
    }
}

// Creating objects
Car myCar = new Car("Tesla", 2024);
Car yourCar = new Car("BMW", 2023);
```

## Constructors

- Same name as the class, no return type
- If no constructor is defined, the compiler generates a default no-arg constructor
- Constructors can be overloaded
- `this()` calls another constructor in the same class

```java
public class Person {
    String name;
    int age;

    public Person() {
        this("Unknown", 0);  // Calling another constructor
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

## The `this` Keyword

`this` refers to the current object instance. Uses:
1. Disambiguate field from parameter: `this.name = name`
2. Call another constructor: `this(args)`
3. Pass current object: `method(this)`
4. Return current object: `return this`

## Static Members

### static Fields
Belong to the class, not instances. One copy shared across all instances.

```java
public class Counter {
    static int count = 0;  // Class variable
    int id;                // Instance variable

    public Counter() {
        id = ++count;      // Share count across all instances
    }
}
```

### static Methods
Belong to the class. Cannot access instance variables directly. Called on the class, not an instance.

```java
public class MathUtils {
    public static int max(int a, int b) {
        return a > b ? a : b;
    }
}
int m = MathUtils.max(5, 3);  // Called on class, not object
```

### static Initialization Block

Runs once when the class is first loaded:

```java
public class Database {
    static {
        // Load JDBC driver, read config files
        System.out.println("Class loaded");
    }
}
```

## Instance vs Class Members

| Aspect | Instance Member | Class Member (static) |
|--------|----------------|----------------------|
| Associated with | Each object | The class itself |
| Number of copies | One per object | One total |
| Access from instances | Direct | `obj.staticField` (works but bad style) |
| Access from class | Not possible | `ClassName.staticField` |
| Can access instance members | Yes | No |
| Memory allocation | Per object | Class area (Metaspace) |

## Object Lifecycle

1. **Declaration**: `Car myCar;` — reference variable, no object
2. **Instantiation**: `new Car()` — allocates memory on heap, initializes fields to defaults
3. **Initialization**: Constructor runs, sets initial state
4. **Usage**: Method calls, field access
5. **Garbage collection**: No more references reachable, GC reclaims memory
